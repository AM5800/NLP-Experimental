import ml.sentenceBreaking.MEMM.LogLinearCostFunction
import ml.sentenceBreaking.SentenceBreakerTag
import ml.sentenceBreaking.TrainingTableEntry
import org.junit.Assert
import org.junit.Test

class LogLinearCostFunctionTest {


  @Test
  fun testZeros() {
    val costFun = LogLinearCostFunction(0.0, createTrainingData())
    val vs = doubleArrayOf(0.0)

    val valueAt = costFun.valueAt(vs)
    Assert.assertEquals(-2.0 * Math.log(2.0), valueAt, 0.001)

    val gradientAt = costFun.gradientAt(vs)
    Assert.assertArrayEquals(doubleArrayOf(0.0), gradientAt, 0.001)
  }

  private fun createTrainingData(): List<TrainingTableEntry> {
    val feature = { tag: SentenceBreakerTag -> tag == SentenceBreakerTag.SentenceBreak }
    val entry1 = TrainingTableEntry(SentenceBreakerTag.Regular, listOf(feature))
    val entry2 = TrainingTableEntry(SentenceBreakerTag.SentenceBreak, listOf(feature))

    return listOf(entry1, entry2)
  }
}