package Simple_assembler_interpreter_5kyu

import org.scalatest._
import flatspec._
import SimpleAssembler._
import org.scalatest.matchers.should.Matchers

// TODO: replace this example test with your own, this is just here to demonstrate usage.
// See http://www.scalatest.org/at_a_glance for example usages

class SimpleAssemblerSpec extends AnyFlatSpec with Matchers {
  "interpret(List(\"mov a 5\",\"inc a\",\"dec a\",\"dec a\",\"jnz a -1\",\"inc a\"))" should "return Map(\"a\"->1)" in {
    interpret(List("mov a 5","inc a","dec a","dec a","jnz a -1","inc a")) should be (Map("a"->1))
  }

  "interpret(List(\"mov a -10\",\"mov b a\",\"inc a\",\"dec b\",\"jnz a -2\"))" should "return Map(\"a\"->0,\"b\"->-20)" in {
    interpret(List("mov a -10","mov b a","inc a","dec b","jnz a -2")) should be (Map("a"->0,"b"->(-20)))
  }
}

