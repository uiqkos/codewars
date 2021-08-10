import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

class RegExpTest {
    /* **************** *
     *  RANDOM TESTS  *
     * ************** */

    private val rand = Random()
    private val randStr: String
        get() {
            val sb = StringBuilder(10 + rand.nextInt(100))
            for (i in 0 until sb.capacity())
                sb.append((32 + rand.nextInt(94)).toChar())
            return sb.toString()
        }

    @Test
    fun coreDisplay___NOT_A_TEST___Look_Inside___() {
        println(Normal('a'))
        println(ZeroOrMore(Normal('a')))
        println(Or(Any(), ZeroOrMore(Normal('a'))))
        println(Str(listOf(Normal('a'), Normal('b'))))
        println(Str(listOf(Normal('b'),
            Or(Normal('c'),
                Normal('d')),
            ZeroOrMore(Normal('e')))))
    }

    private fun shouldBe(input: String, expected: String) {
        val actual = RegExpParser(input).parse()
        assertEquals(String.format("Parsing \"%s\": ", input), expected, "$actual")
    }


    @Test
    fun basicTests() {
        for (s in listOf(arrayOf(".", "."),
            arrayOf("a", "a"),
            arrayOf("a|b", "(a|b)"),
            arrayOf("a*", "a*"),
            arrayOf("(a)", "a"),
            arrayOf("(a)*", "a*"),
            arrayOf("(a|b)*", "(a|b)*"),
            arrayOf("a|b*", "(a|b*)"),
            arrayOf("abcd", "(abcd)"),
            arrayOf("ab|cd", "((ab)|(cd))"))) shouldBe(s[0], s[1])
    }


    @Test
    fun precedenceTests() {
        for (s in listOf(arrayOf("ab*", "(ab*)"),
            arrayOf("(ab)*", "(ab)*"),
            arrayOf("ab|a", "((ab)|a)"),
            arrayOf("a(b|a)", "(a(b|a))"),
            arrayOf("a|b*", "(a|b*)"),
            arrayOf("(a|b)*", "(a|b)*"))) shouldBe(s[0], s[1])
    }


    @Test
    fun otherExamples() {
        for (s in listOf(arrayOf("a", "a"),
            arrayOf("ab", "(ab)"),
            arrayOf("a.*", "(a.*)"),
            arrayOf("(a.*)|(bb)", "((a.*)|(bb))"))) shouldBe(s[0], s[1])
    }


    @Test
    fun invalidTests() {
        for (s in listOf(arrayOf("*", ""),
            arrayOf("(", ""),
            arrayOf("(hi!", ""),
            arrayOf(")(", ""),
            arrayOf("a|t|y", ""),
            arrayOf("a**", ""))) shouldBe(s[0], s[1])
    }


    @Test
    fun complexExamples() {
        for (s in listOf(arrayOf("((aa)|ab)*|a", "(((aa)|(ab))*|a)"),
            arrayOf("((a.)|.b)*|a", "(((a.)|(.b))*|a)"))) shouldBe(s[0], s[1])
    }
}