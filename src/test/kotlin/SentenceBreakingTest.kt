import ml.sentenceBreaking.HeuristicSentenceBreaker
import org.junit.Assert
import org.junit.Test

class SentenceBreakingTest {
  private fun doTest(sample: String) {
    val front = "Some garbage. "
    val back = " Garbage continues"
    val withGarbage = front + sample + back
    val broken = HeuristicSentenceBreaker().breakText(withGarbage).toList()

    Assert.assertEquals(3, broken.size)
    Assert.assertEquals(withGarbage.length - back.length - 1, broken[1].end)
  }

  @Test
  fun testDot() = doTest("Das ist ein Tisch.")

  @Test
  fun testEllipsis() = doTest("Das ist ein Tisch...")

  @Test
  fun testExclamation() = doTest("Das ist ein Tisch!")

  @Test
  fun testQuestion() = doTest("Ist das ein Tisch?")

  @Test
  fun testDecimal() = doTest("Dieser Stein wiegt 1,5 kg.")

  @Test
  fun testDecimal2() = doTest("Dieser Stein wiegt 1.5 kg.")

  @Test
  fun testDecimal3() = doTest("Dieser Stein wiegt .5 kg.")

  @Test
  fun testOneCharWord() = doTest("blah a. blah.")
}