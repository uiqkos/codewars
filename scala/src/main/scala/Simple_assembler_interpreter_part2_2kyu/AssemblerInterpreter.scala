package Simple_assembler_interpreter_part2_2kyu

import Interpreter._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}


class Interpreter(
  var variables: mutable.Map[String, Int] = mutable.Map.empty[String, Int],
  var instructions: ListBuffer[Instruction] = ListBuffer.empty[Instruction]
) {
  var nextInstruction = 0
  var functionIndexes = Map.empty[String, Int] // label -> index
  var e = 0
  var output: Option[String] = Option.empty[String]
  val stack: mutable.Stack[Int] = mutable.Stack.empty[Int]

  abstract class Shift(instructionOffset: => Int) extends Instruction {
    def getNextInstruction: Int = nextInstruction + instructionOffset
  }

  abstract class Operation extends Shift(1)

  case class msg(messagesAndRegs: String*) extends Operation {
    override def apply: Unit =
      output = Option { output.getOrElse("") +
        messagesAndRegs
          .map(messageOrReg => variables
            .getOrElse(messageOrReg, messageOrReg)
          ).mkString
      }
  }

  case class mov(reg: String, value: String) extends Operation {
    override def apply: Unit =
      if (variables.contains(reg)) variables(reg) = parseValue(value)
      else variables += reg -> parseValue(value)
  }

  class inc(reg: String) extends add(reg, 1)

  class dec(reg: String) extends add(reg, -1)

  class add(reg: String, value: => Int) extends Operation {
    def this(reg: String, regOrValue: String) =
      this(reg, parseValue(regOrValue))
    override def apply: Unit =
      variables(reg) += value
  }

  class sub(reg: String, value: String) extends add(reg, -parseValue(value))

  case class mul(reg: String, value: String) extends Operation {
    override def apply: Unit = variables(reg) *= parseValue(value)
  }

  case class div(reg: String, value: String) extends Operation {
    override def apply: Unit = variables(reg) /= parseValue(value)
  }
  case class cmp(value1: String, value2: String) extends Operation {
    override def apply: Unit = e = parseValue(value1) compare parseValue(value2)
  }

  class jump(
    var p: () => Boolean,
    _nextInstruction: => Int
  ) extends Operation {
    override def apply: Unit = {}
    override def getNextInstruction: Int =
      if (p()) _nextInstruction
      else super.getNextInstruction
  }

  abstract class jumpToLabel(p: () => Boolean, label: String)
    extends jump(p, functionIndexes(label))

  case class jmp(label: String) extends jumpToLabel(() => true, label)
  case class jne(label: String) extends jumpToLabel(() => e != 0, label)
  case class je (label: String) extends jumpToLabel(() => e == 0, label)
  case class jge(label: String) extends jumpToLabel(() => e == 1 || e == 0, label)
  case class jg (label: String) extends jumpToLabel(() => e == 1, label)
  case class jle(label: String) extends jumpToLabel(() => e == -1 || e == 0, label)
  case class jl (label: String) extends jumpToLabel(() => e == -1, label)

  case class call(label: String) extends Instruction {
    override def apply: Unit = stack.append(nextInstruction + 1)
    override def getNextInstruction: Int = functionIndexes(label)
  }

  def define(label: String, functionInstructions: List[Instruction]): Unit = {
    val index = instructions.length
    instructions.appendAll(functionInstructions)
    functionIndexes += label -> index
  }

  def parseValue(value: String): Int =
    value.toIntOption.getOrElse(variables(value))

  case class jnz(reg: String, value: String)
    extends jump(() => parseValue(reg) != 0, parseValue(value))

  class ret extends Instruction {
    private def next = stack.pop
    override def apply: Unit = {}
    override def getNextInstruction: Int = next
  }

  class end extends Operation {
    override def apply: Unit = break
  }

  val instructionTypes = List(
    classOf[mov], classOf[msg],
    classOf[inc], classOf[dec],
    classOf[sub], classOf[add],
    classOf[div], classOf[mul],
    classOf[jnz], classOf[jmp], classOf[call],
    classOf[cmp],
    classOf[je], classOf[jne],
    classOf[jge], classOf[jg],
    classOf[jle], classOf[jl],
    classOf[ret], classOf[end]
  )

  def run: Interpreter = {
    nextInstruction = 0
    breakable {
      while (true) {
        val instruction = instructions(nextInstruction)
        instruction.apply
        nextInstruction = instruction.getNextInstruction
      }
    }
    this
  }
}

object Interpreter {

  trait Instruction {
    def apply: Unit
    def getNextInstruction: Int
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
        .foreach {
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

    def parseProgram(program: String): Interpreter = {
      val interpreter = new Interpreter()

      val instructionTypeByName = interpreter
        .instructionTypes
        .map(instructionType => (instructionType.getSimpleName, instructionType))
        .toMap

      val stringInstructions = program
        .split("\n")
        .toList
        .map(parseArguments)
        .filter(_.nonEmpty)

      val instructionBuffer = ListBuffer.empty[Instruction]
      var prevLabel = "@start"

      stringInstructions
        .foreach {
          case s"$label:" :: _ =>
            interpreter.define(prevLabel, instructionBuffer.toList)
            prevLabel = label
            instructionBuffer.clear

          case List("msg", args@_*) => instructionBuffer.append(interpreter.msg(args: _*))
          case List(instructionName, args@_*)
            if instructionTypeByName contains instructionName =>
              val argClasses: Seq[Class[_]] = classOf[Interpreter] +: args.map(_.getClass)
              val constructorArgs = interpreter +: args

              instructionBuffer.append {
                instructionTypeByName(instructionName)
                  .getConstructor(argClasses: _*)
                  .newInstance(constructorArgs: _*)
              }

          case line => throw new IllegalArgumentException(s"Cannot parse line: $line")
        }

      interpreter.define(prevLabel, instructionBuffer.toList)
      interpreter
    }
}

object AssemblerInterpreter {
  def interpret(input: String): Option[String] = {
    val interpreter = parseProgram(input)

    try interpreter.run
    catch { case _: Throwable =>
      return Option.empty[String]
    }

    interpreter.output
  }
}
