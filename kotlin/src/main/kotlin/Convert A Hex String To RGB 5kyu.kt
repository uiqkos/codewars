data class RGB(val r: Int, val g: Int, val b: Int)

fun hexStringToRGB(hexString: String): RGB {
    return RGB(
        r = hexString.slice(1..2).toInt(16),
        g = hexString.slice(3..4).toInt(16),
        b = hexString.slice(5..6).toInt(16)
    )
}

fun main() {
    println("FF".toInt(16))
}