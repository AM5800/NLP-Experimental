import corpus.RelativeRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class RelativeRangeTest {
    @Test
    fun testSingleRange() {
        val range = 0.rangeTo(100000).toList()
        var tested = RelativeRange(0.0, 1.0)

        assertEquals(range.size, range.count { tested.accept(it, range.size) })
    }

    @Test
    fun test30_70() {
        val range = 0.rangeTo(100000).toList()
        val r30 = RelativeRange(0.0, 0.3)
        val r70 = RelativeRange(0.3, 1.0)


        val r30Count = range.count { r30.accept(it, range.size) }
        val r70Count = range.count { r70.accept(it, range.size) }

        assertNotEquals(r30, r70)
        assertEquals(range.size, r30Count + r70Count)

    }
}