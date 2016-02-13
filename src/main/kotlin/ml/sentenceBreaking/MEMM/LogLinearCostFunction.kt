package ml.sentenceBreaking.MEMM

import com.github.lbfgs4j.liblbfgs.Function
import ml.sentenceBreaking.SentenceBreakerTag
import util.dot

class LogLinearCostFunction(val lambda: Double, private val trainingData: List<TrainingTableEntry>) : Function {
  private val m = trainingData.first().M

  override fun getDimension(): Int {
    return m
  }

  override fun gradientAt(vs: DoubleArray): DoubleArray {
    val result = DoubleArray(m)

    for (k in 0..m - 1) {
      val empiricalCounts = trainingData.map { entry -> entry.answerAt(k).toInt() }.sum()

      val expectedCounts = trainingData.map { entry ->
        SentenceBreakerTag.values().map { tag ->
          val featureValue = entry.answerAt(k, tag)
          if (featureValue) computeP(entry, vs, tag) else 0.0
        }.sum()
      }.sum()

      val regularizationTerm = vs[k] * lambda
      result[k] = empiricalCounts - expectedCounts - regularizationTerm
    }

    return result
  }

  private fun computeP(entry: TrainingTableEntry, vs: DoubleArray, tag: SentenceBreakerTag = entry.correctTag): Double {
    val nominator = Math.exp(dot(vs, entry.featureResultsAtTag(tag)))
    val denominator = SentenceBreakerTag.values().map { tag ->
      Math.exp(dot(vs, entry.featureResultsAtTag(tag)))
    }.sum()

    return nominator / denominator
  }

  override fun valueAt(vs: DoubleArray): Double {
    val pSumTerm = trainingData.mapIndexed { i, entry -> Math.log(computeP(entry, vs)) }.sum()
    val regularizationTerm = lambda / 2 * vs.map { it * it }.sum()
    return pSumTerm - regularizationTerm
  }

}