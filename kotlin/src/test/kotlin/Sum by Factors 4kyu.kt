import org.junit.Assert.assertEquals
import org.junit.Test

class SumOfDividedTest {

    @Test
    fun testOne() {
        val lst = intArrayOf(12, 15)
        assertEquals("(2 12)(3 27)(5 15)",
            sumOfDivided(lst))
    }

    @Test
    fun testTwo() {
        val lst = intArrayOf(15, 21, 24, 30, 45)
        assertEquals("(2 54)(3 135)(5 90)(7 21)",
            sumOfDivided(lst))
    }

    @Test
    fun testThree() {
        val lst = intArrayOf(107, 158, 204, 100, 118, 123, 126, 110, 116, 100)
        assertEquals("(2 1032)(3 453)(5 310)(7 126)(11 110)(17 204)(29 116)(41 123)(59 118)(79 158)(107 107)",
            sumOfDivided(lst))
    }

    @Test
    fun testFour() {
        val lst = intArrayOf()
        assertEquals("",
            sumOfDivided(lst))
    }

    @Test
    fun testFive() {
        val lst = intArrayOf(1070, 1580, 2040, 1000, 1180, 1230, 1260, 1100, 1160, 1000)
        assertEquals("(2 12620)(3 4530)(5 12620)(7 1260)(11 1100)(17 2040)(29 1160)(41 1230)(59 1180)(79 1580)(107 1070)",
            sumOfDivided(lst))
    }
}