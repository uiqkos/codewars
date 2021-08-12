package Simple_assembler_interpreter_part2_2kyu

import scala.Function.tupled
import scala.collection.mutable

class Interpreter(
  var variables: mutable.Map[String, Int] = mutable.Map.empty[String, Int]
) {

  var nextInstruction = 0
  var functions = Map.empty[String, List[Interpreter => Unit]]
  var e = 0
  var output: Option[String] = Option.empty[String]

  def msg(message: String, regOrValue: String): Unit =
    output = Option(output.getOrElse("") + s"$message${parseValue(regOrValue)}")

  def mov(reg: String, value: String): Unit = {
    if (variables.contains(reg)) variables(reg) = parseValue(value)
    else variables += reg -> parseValue(value)
  }

  def inc(reg: String): Unit =
    add(reg, 1)

  def dec(reg: String): Unit =
    add(reg, -1)

  def add(reg: String, value: Int): Unit =
    variables(reg) += value

  def add(reg: String, value: String): Unit =
    add(reg, parseValue(value))

  def sub(reg: String, value: String): Unit =
    add(reg, -parseValue(value))

  def mul(reg: String, value: String): Unit =
    variables(reg) *= parseValue(value)

  def div(reg: String, value: String): Unit =
    variables(reg) /= parseValue(value)

  def cmp(value1: String, value2: String): Unit =
    e = parseValue(value1).compare(parseValue(value2))

  def jne(label: String): Unit = if (e != 0) call(label)
  def je(label: String): Unit = if (e == 0) call(label)
  def jge(label: String): Unit = if (e != 0 || e == 1) call(label)
  def jg(label: String): Unit = if (e == 1) call(label)
  def jle(label: String): Unit = if (e == 0 || e == -1) call(label)
  def jl(label: String): Unit = if (e == -1) call(label)

  def call(label: String): Unit = run(functions(label))

  def define(label: String, instructions: List[Interpreter => Unit]): Unit =
    functions += label -> instructions

  def parseValue(value: String): Int =
    value.toIntOption.getOrElse(variables(value))

  def jnz(reg: String, value: String): Unit =
    if (parseValue(reg) != 0) nextInstruction += parseValue(value) - 1

  def run(instructions: List[Interpreter => Unit]): Interpreter = {
    while (nextInstruction < instructions.length) {
      instructions(nextInstruction)(this)
      nextInstruction += 1
    }
    nextInstruction = 0
    this
  }

  def run(
    instructions: List[Interpreter => Unit], 
    initialVariables: mutable.Map[String, Int]
  ): Interpreter = {
    variables = initialVariables
    run(instructions)
  }

}

object Interpreter {
  def parseProgram(program: String): List[Interpreter => Unit] = {
    val stringInstructions = program
      .split("\n")
      .toList
      .map(_.split(" |, ").filter(_ != "").toList)

    val mapper: List[String] => Interpreter => Unit = {
      case "mov" :: reg :: regOrValue :: _ => _.mov(reg, regOrValue)
      case "dec" :: reg :: _ => _.dec(reg)
      case "inc" :: reg :: _ => _.inc(reg)
      case "add" :: reg :: regOrValue :: _ => _.add(reg, regOrValue)
      case "sub" :: reg :: regOrValue :: _ => _.sub(reg, regOrValue)
      case "cmp" :: regOrValue1 :: regOrValue2 :: _ => _.cmp(regOrValue1, regOrValue2)
      case "je"  :: label :: _ => _.je(label)
      case "jne" :: label :: _ => _.jne(label)
      case "jge" :: label :: _ => _.jge(label)
      case "jg"  :: label :: _ => _.jg(label)
      case "jl"  :: label :: _ => _.jl(label)
      case "jle" :: label :: _ => _.jle(label)
      case "jnz" :: reg :: regOrValue :: _ => _.jnz(reg, regOrValue)
      case "msg" :: message :: regOrValue :: _ => _.msg(message, regOrValue)
      case _ => _ => {}
    }

    val instructions: List[Interpreter => Unit] = stringInstructions
      .takeWhile(_.headOption.getOrElse("") != "end")
      .map(mapper)

    var prevLabel: String = "labels"

    val groups = stringInstructions
      .dropWhile(_.headOption.getOrElse("") != "end")
      .drop(1)
      .groupBy {
        case s"$label:" :: _ =>
          prevLabel = label
          "labels"
        case "ret" :: _ => "labels"
        case _ => prevLabel
      }

    val functions = groups
      .filter(_._1 != "labels")
      .map(tupled(
        (label, instructions) => {
          interpreter: Interpreter =>
            interpreter.define(label, instructions.map(mapper))
        }
      ))
      .toList
    functions ++ instructions
  }
}

object AssemblerInterpreter {
  def interpret(input: String): Option[String] = {
    new Interpreter().run(Interpreter.parseProgram(input)).output
  }
}
