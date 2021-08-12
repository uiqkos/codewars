import java.lang.IllegalArgumentException

interface RegExp {
    override fun toString(): String
}

class Void : RegExp {
    override fun toString(): String {
        return "Void()"
    }
}

class Any : RegExp {
    override fun toString(): String {
        return "Any()"
    }
}

data class Or(
    val first: RegExp,
    var second: RegExp = Void()
) : RegExp {
    override fun toString(): String {
        return "Or($first, $second)"
    }

    fun apply(regExp: RegExp): RegExp {
        second = regExp
        return this
    }
}

data class Normal(val char: Char) : RegExp {
    override fun toString(): String {
        return "Normal($char)"
    }
}

data class ZeroOrMore(val content: RegExp = Void()) : RegExp {
    override fun toString(): String {
        return "ZeroOrMore($content)"
    }
}

data class Str(var content: List<RegExp>) : RegExp {
    override fun toString(): String {
        return "Str([${content.joinToString()}])"
    }
    fun append(regExp: RegExp) {
        content = content.plus(regExp)
    }
}

data class RegExpParser(val input: String) {

    fun parse(): RegExp {
        val content: MutableList<RegExp> = mutableListOf()
        var prev: RegExp = Void()
        var operation: (RegExp) -> RegExp = {r -> r}

        fun resetOperation() = run {
            operation = { r ->
                content.add(r)
                r
            }
        }

        resetOperation()

        for (c in input.reversed()) {
            print("$prev -> ")

            prev = when (c) {
                in 'a'..'z',
                in 'A'..'Z',
                in '0'..'9' -> Normal(c).let(operation).also { resetOperation() }
                '.' -> Any().let(operation).also { resetOperation() }

                '|' -> Or(prev).also {
                    operation = it::apply
                }
                '*' -> Void().also {
                    operation = ::ZeroOrMore
                }

                else -> throw IllegalArgumentException("undefined token: $c")
            }
//            when {
//                prev is Normal
//            }
        }

        return content.reversed().let {
            if (it.size == 1) it.first()
            else Str(it)
        }
    }
}

fun main() {
    println(RegExpParser("ab*").parse())
    println(RegExpParser("a.*").parse())
    println(RegExpParser("a|b").parse())
    println(RegExpParser("a|b*").parse())
}