import org.junit.Assert
import org.junit.Test
import treebank.parsing.shuffling.RelativeRange

class RelativeRangeTest {
  @Test
  fun testSingleRange() {
    val range = 0.rangeTo(100000).toList()
    var tested = RelativeRange(0, 100)

    Assert.assertEquals(range.size, range.count { tested.accept(it, range.size) })
  }

  @Test
  fun test30_70() {
    val range = 0.rangeTo(99999).toList()
    val r30 = RelativeRange(0, 30)
    val r70 = RelativeRange(30, 100)


    val r30Count = range.count { r30.accept(it, range.size) }
    val r70Count = range.count { r70.accept(it, range.size) }

    Assert.assertNotEquals(r30, r70)
    Assert.assertEquals(range.size, r30Count + r70Count)
    Assert.assertEquals(30000, r30Count)
  }

  @Test
  fun test100() {
    val range = 0.rangeTo(99999).toList()
    val r100 = RelativeRange(99, 100)

    Assert.assertNotEquals(0, range.count { r100.accept(it, range.size) })
  }
}