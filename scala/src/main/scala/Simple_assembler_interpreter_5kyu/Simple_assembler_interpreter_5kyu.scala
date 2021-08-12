package Simple_assembler_interpreter_5kyu

import java.util.NoSuchElementException
import scala.collection.mutable

class Interpreter(val instructions: List[Interpreter => Unit]) {
  var variables = mutable.Map.empty[String, Int]
  var nextInstruction = 0

  def mov(reg: String, value: String): Unit = {
    variables += reg -> parseValue(value)
  }

  def inc(reg: String): Unit = add(reg, 1)

  def dec(reg: String): Unit = add(reg, -1)

  def add(reg: String, value: Int): Unit = {
    if (variables.contains(reg)) variables(reg) += value
    else throw new NoSuchElementException(s"register $reg not defined")
  }

  def parseValue(value: String): Int = value.toIntOption.getOrElse(variables(value))

  def jnz(reg: String, value: String): Unit =
    if (parseValue(reg) != 0) nextInstruction += parseValue(value) - 1

  def next: Interpreter = {
    if (nextInstruction < instructions.length) {
      instructions(nextInstruction)(this)
      nextInstruction += 1
      return this
    }
    null
  }
}

object SimpleAssembler {
  def interpret(program: List[String]): Map[String, Int] = {
    val commands: List[Interpreter => Unit] = program
      .map(_.split(" ").toList)
      .map {
        case "mov" :: reg :: value :: _ => _.mov(reg, value)
        case "dec" :: reg :: _ => _.dec(reg)
        case "inc" :: reg :: _ => _.inc(reg)
        case "jnz" :: reg :: value :: _ => _.jnz(reg, value)
      }

    val interpreter = new Interpreter(commands)

    while (interpreter.next != null) {}

    interpreter.variables.toMap
  }
}
