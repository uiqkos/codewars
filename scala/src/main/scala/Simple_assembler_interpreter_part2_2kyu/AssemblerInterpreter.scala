package Simple_assembler_interpreter_part2_2kyu

import Simple_assembler_interpreter_part2_2kyu.Interpreter.Instruction

import java.lang.reflect.InvocationTargetException
import scala.Function.tupled
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.break


class Interpreter(
  var variables: mutable.Map[String, Int] = mutable.Map.empty[String, Int],
  var instructions: List[Instruction] = List.empty[Instruction]
) {

  var nextInstruction = 0
  var functionIndexes = Map.empty[String, Int] // label -> index
  var e = 0
  var output: Option[String] = Option.empty[String]
  val stack: mutable.Stack[Int] = mutable.Stack.empty[Int]

  def parseMessage(messageOrArg: String): String =
    if (variables contains messageOrArg) variables(messageOrArg).toString
    else messageOrArg

  def msg(messagesAndRegs: List[String]): Unit =
    output = Option(
      output.getOrElse("")
        + messagesAndRegs.map(parseMessage).mkString
    )

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
    e = parseValue(value1) compare parseValue(value2)

  def jne(label: String): Unit = if (e != 0) jmp(label)
  def je(label: String): Unit = if (e == 0) jmp(label)
  def jge(label: String): Unit = if (e != 0 || e == 1) jmp(label)
  def jg(label: String): Unit = if (e == 1) jmp(label)
  def jle(label: String): Unit = if (e == 0 || e == -1) jmp(label)
  def jl(label: String): Unit = if (e == -1) jmp(label)

  def call(label: String): Unit = {
    stack.append(functionIndexes(label))
    nextInstruction = functionIndexes(label) - 1
  }

  def jmp(label: String): Unit = {
    nextInstruction = functionIndexes(label) - 1
  }

  def define(label: String, instructions: List[Instruction]): Unit =
    functionIndexes += label -> instructions

  def parseValue(value: String): Int =
    value.toIntOption.getOrElse(variables(value))

  def jnz(reg: String, value: String): Unit =
    if (parseValue(reg) != 0) nextInstruction += parseValue(value) - 1

  def ret: Unit = nextInstruction = stack.pop
  def end: Unit = break

  def run: Interpreter = {
    nextInstruction = 0

      while (nextInstruction < instructions.length) {
        instructions(nextInstruction)(this)
        nextInstruction += 1
      }

    this
  }
}

object Interpreter {

  case class Instruction(instructionName: String, args: List[Any] = List.empty[Any]) {
    private val interpreterInstanceClass = new Interpreter().getClass
    val argsClasses: Seq[Class[_]] = args.map(_.getClass)

    def apply(interpreter: Interpreter): Unit = try
      interpreterInstanceClass
        .getMethod(instructionName, argsClasses: _*)
        .invoke(interpreter, args: _*)
      catch {
        case e: InvocationTargetException => throw e.getTargetException
        case e => throw e
      }

    override def toString: String = s"$instructionName ${args.mkString(", ")}"
  }

  object Instruction {
    def fromLineOption(line: String): Option[Instruction] = {
      val interpreterMethodNames = new Interpreter().getClass.getMethods.map(_.getName)
      // todo parseArgs
      parseArguments(line.strip) match {
        case "ret" :: _ => Option(new Instruction("ret"))

        case List(instructionName@_, args@_*)
          if interpreterMethodNames contains instructionName => Option {
            if (instructionName == "msg")
              new Instruction(instructionName, args.toList) {
                override def apply(interpreter: Interpreter): Unit =
                  interpreter.msg(args.map(_.asInstanceOf[String]))
              }
            else
              new Instruction(instructionName, args.toList)
        }

        case _ :: ";" :: _ => Option {
          new Instruction(";") {
            override def apply(interpreter: Interpreter): Unit = {}
          }
        }

        case _ => Option.empty[Instruction]
      }
    }

    def fromLine(line: String): Instruction = fromLineOption(line) match {
      case Some(value) => value
      case None => throw new IllegalArgumentException(s"Cannot parse line: $line")
    }
  }

  def parseArguments(args: String): List[String] = {
    val buffer = new mutable.StringBuilder()
    val parsedArgs = new ListBuffer[String]
    var waitString = false

    def appendArg: Unit = {
      if (buffer.nonEmpty) parsedArgs.append(buffer.toString)
      buffer.clear
    }

    args
      .map {
        case '\'' => waitString = !waitString
        case ';' =>
          appendArg
          return parsedArgs.toList
        case ' ' | ',' if !waitString => appendArg
        case c => buffer.append(c)
      }
    appendArg
    parsedArgs.toList
  }

  def parseProgram(program: String): List[Instruction] = {
    val stringInstructions = program
      .split("\n")
      .toList
      .filter(_.nonEmpty)

    val instructions = stringInstructions
      .takeWhile(_ != "end")
      .map(Instruction.fromLine)

    var prevLabel: String = "labels"

    val groups = stringInstructions
      .dropWhile(_ != "end")
      .drop(1)
      .groupBy {
        case s"$label:$_" =>
          prevLabel = label
          "labels"
        case _ => prevLabel
      }

    val functions = groups
      .filter(_._1 != "labels")
      .map(tupled(
        (label, instructions) =>
          // define instruction
          new Instruction("define", instructions) {
            override def apply(interpreter: Interpreter): Unit =
              interpreter.define(label, instructions.map(Instruction.fromLine))
          }
      ))
      .toList

    functions ++ instructions
  }
}

object AssemblerInterpreter {
  def interpret(input: String): Option[String] = {
    val interpreter = new Interpreter()
    interpreter.execute(Interpreter.parseProgram(input), verbose = true).output
  }
}
