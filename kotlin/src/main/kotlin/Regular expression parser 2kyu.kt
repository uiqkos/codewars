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

data class Or(val first: RegExp = Void(), var second: RegExp = Void()) : RegExp {
    override fun toString(): String {
        return "Or($first, $second)"
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
        var prev: RegExp = Void()

        for (c in input) {
            var next = when (c) {
                in 'a'..'z',
                in 'A'..'Z',
                in '0'..'9' -> Normal(c)

                '|' -> Or(prev)
                '.' -> Any()
                '*' -> ZeroOrMore(prev)

                else -> Void()
            }
            when {
                prev is Or &&
                    prev.second is Void ->
                    next = prev.let {
                        it.second = next
                        it
                    }

                prev is Normal &&
                    next is Normal -> next = Str(mutableListOf(prev, next))

                prev is Str &&
                    next is Normal -> next = Str(prev.content.plus(next))
            }
            prev = next
            print("$prev -> ")
        }
        return prev
    }
}

fun main() {
    println(RegExpParser("ab*").parse())
    println(RegExpParser("a.*").parse())
}