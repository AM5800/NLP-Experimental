import ml.sentenceBreaking.LogLinearCostFunction
import ml.sentenceBreaking.SentenceBreakerTag
import ml.sentenceBreaking.TrainingTableEntry
import org.junit.Assert
import org.junit.Test

class LogLinearCostFunctionTest {


  @Test
  fun testZeros() {
    val costFun = LogLinearCostFunction(0.0, createTrainingData())
    val vs = doubleArrayOf(0.0, 0.0)

    val valueAt = costFun.valueAt(vs)
    Assert.assertEquals(-2.0 * Math.log(2.0), valueAt, 0.001)

    val gradientAt = costFun.gradientAt(vs)
    Assert.assertArrayEquals(doubleArrayOf(1.0, 1.0), gradientAt, 0.001)
  }

  private fun createTrainingData(): List<List<TrainingTableEntry>> {
    val f1_1 = TrainingTableEntry(true, false, SentenceBreakerTag.Regular)
    val f1_2 = TrainingTableEntry(true, false, SentenceBreakerTag.Regular)
    val f2_1 = TrainingTableEntry(true, false, SentenceBreakerTag.Regular)
    val f2_2 = TrainingTableEntry(false, true, SentenceBreakerTag.SentenceBreak)

    return listOf(listOf(f1_1, f1_2), listOf(f2_1, f2_2))
  }
}