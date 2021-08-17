package Symbolic_differentiation_of_prefix_expressions_2kyu

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

object Conversions {
  implicit class CustomNode(node: Node) {
    def -(other: Node): Minus = new Minus(node, other)
    def +(other: Node): Plus = new Plus(node, other)
    def /(other: Node): Divide = new Divide(node, other)
    def *(other: Node): Multiply = new Multiply(node, other)
    def ^(other: Node): Power = new Power(node, other)
  }
  def variable: Variable = new Variable
  def value(d: Double): Value = Value(d)
  def cos(node: Node): Cos = new Cos(node)
  def sin(node: Node): Sin = new Sin(node)
  def tan(node: Node): Tan = new Tan(node)
  def exp(node: Node): Exp = new Exp(node)
  def ln(node: Node): Ln = new Ln(node)
}

import Conversions._

abstract class Node {
  def derivative: Node
  def d: Node = derivative
  def asString: String
  def reduce: Node = this
  def r: Node = reduce
}

case class Value(value: Double) extends Node {
  override def derivative: Node = Value(0)
  override def asString: String =
    if (math.ceil(value) - value == 0.0) value.toInt.toString
    else value.toString
}

class Variable extends Node {
  override def derivative: Node = Value(1)
  override def asString: String = "x"
}

trait Operation extends Node {
  override def reduce: Node = this
}

abstract class Operation2(
  var node1: Node,
  var node2: Node,
  val mathOperation: (Double, Double) => Double
) extends Operation {
 override def reduce: Node = {
   node1 = node1.reduce
   node2 = node2.reduce
   (node1, node2)
 } match {
   case (value1: Value, value2: Value) => Value(mathOperation(value1.value, value2.value))
   case _ => super.reduce
  }
}

abstract class Operation1(
  var node: Node,
) extends Operation {
  override def reduce: Node = {
    node = node.reduce
    this
  }
}

class Plus(_node1: Node, _node2: Node) extends Operation2(_node1, _node2, _ + _) {
  override def derivative: Node = node1.d + node2.d
  override def asString: String = s"(+ ${node1.asString} ${node2.asString})"

  override def reduce: Node = (node1.r, node2.r) match {
    case (Value(0), node) => node
    case (node, Value(0)) => node
    case _ => super.reduce
  }
}

class Minus(_node1: Node, _node2: Node) extends Operation2(_node1, _node2, _ - _) {

  override def derivative: Node = node1.d - node2.d
  override def asString: String = s"(- ${node1.asString} ${node2.asString})"

  override def reduce: Node = (node1.r, node2.r) match {
    case (_: Variable, _: Variable) => value(0)
    case _ => super.reduce
  }
}

class Multiply(_node1: Node, _node2: Node) extends Operation2(_node1, _node2, _ * _) {
  override def derivative: Node = (node1 * node2.d) + (node1.d * node2)
  override def asString: String = s"(* ${node1.asString} ${node2.asString})"

  override def reduce: Node = (node1.r, node2.r) match {
    case (Value(0), _) => value(0)
    case (_, Value(0)) => value(0)
    case (Value(1), node) => node
    case (node, Value(1)) => node
    case (_: Variable, _: Variable) => variable ^ value(2)
    case _ => super.reduce
  }
}

class Divide(_node1: Node, _node2: Node) extends Operation2(_node1, _node2, _ / _) {
  override def asString: String = s"(/ ${node1.asString} ${node2.asString})"
  override def derivative: Node =
    ((node2 * node1.d) - (node2.d * node1)) /
      (node2 ^ value(2))

  override def reduce: Node = (node1.r, node2.r) match {
    case (Value(0), _) => value(0)
    case (_, Value(0)) => throw new ArithmeticException(s"Division by zero: $asString")
    case (node, Value(1)) => node
    case (_: Variable, _: Variable) => value(1)
    case _ => super.reduce
  }
}

class Power(_node1: Node, _node2: Node) extends Operation2(_node1, _node2, math.pow) {
  override def asString: String = s"(^ ${node1.asString} ${node2.asString})"
  override def derivative: Node =
    node1.d * (node2 * (node1 ^ (node2 - value(1))))

  override def reduce: Node = (node1.r, node2.r) match {
    case (node, Value(1)) => node
    case (_, Value(0)) => value(1)
    case _ => super.reduce
  }
}

class Cos(_node: Node) extends Operation1(_node) {
  override def derivative: Node = node.d * (value(-1) * sin(node))
  override def asString: String = s"(cos ${node.asString})"
}

class Sin(_node: Node) extends Operation1(_node) {
  override def derivative: Node = node.d * cos(node)
  override def asString: String = s"(sin ${node.asString})"
}

class Tan(_node: Node) extends Operation1(_node) {
  override def derivative: Node = node.d * (value(1) + (tan(node) ^ value(2)))
  override def asString: String = s"(tan ${node.asString})"
}

class Exp(_node: Node) extends Operation1(_node) {
  override def derivative: Node = node.d * exp(node)
  override def asString: String = s"(exp ${node.asString})"
}

class Ln(_node: Node) extends Operation1(_node) {
  override def derivative: Node = node.d * (value(1) / node)
  override def asString: String = s"(ln ${node.asString})"
}

object PrefixDiff {
  def tokenize(expr: String): List[String] = {
    if (expr.matches("\\(.*\\)")) return tokenize(expr.slice(1, expr.length - 1))

    val buffer = new mutable.StringBuilder
    val content = ListBuffer.empty[String]  

    var braceCounter = 0

    def appendContent: Unit = {
      if (buffer.nonEmpty)
        content.append(buffer.toString)
      buffer.clear()
    }

    expr foreach {
        case ' '
          if braceCounter == 0 => appendContent
        case chr =>
          chr match {
            case '(' => braceCounter += 1
            case ')' => braceCounter -= 1
            case _ =>
          }
          buffer append chr
      }
    appendContent
    content.toList
  }

  def parse(expr: String): Node = {
    val tokens = tokenize(expr)
    if (tokens.length == 1) return tokens.head match {
      case double if double.toDoubleOption.nonEmpty => value(double.toDouble)
      case "x" => variable
    }
    tokens match {
      case "+" :: arg1 :: arg2 :: Nil => parse(arg1) + parse(arg2)
      case "-" :: arg1 :: arg2 :: Nil => parse(arg1) - parse(arg2)
      case "*" :: arg1 :: arg2 :: Nil => parse(arg1) * parse(arg2)
      case "/" :: arg1 :: arg2 :: Nil => parse(arg1) / parse(arg2)
      case "^" :: arg1 :: arg2 :: Nil => parse(arg1) ^ parse(arg2)
      case "cos" :: arg :: Nil => cos(parse(arg))
      case "sin" :: arg :: Nil => sin(parse(arg))
      case "tan" :: arg :: Nil => tan(parse(arg))
      case "ln" :: arg :: Nil => ln(parse(arg))
      case "exp" :: arg :: Nil => exp(parse(arg))
      case _ => throw new IllegalArgumentException(s"Cant parse \"$expr\"")
    }
  }

  def diff(expr: String): String = parse(expr).derivative.reduce.asString
}
