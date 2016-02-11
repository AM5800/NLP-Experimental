package ml.sentenceBreaking

import com.github.lbfgs4j.liblbfgs.Function

class LogLinearCostFunction(val lambda: Double, private val trainingData: List<List<TrainingTableEntry>>) : Function {
  private val n = trainingData.size
  private val m = trainingData.first().size
  private val Y = trainingData.first().first().values.size

  override fun getDimension(): Int {
    return m
  }

  override fun gradientAt(x: DoubleArray): DoubleArray {
    val result = DoubleArray(m)

    for (k in 0..m - 1) {
      for (i in 0..n - 1) {
        val valueAtK = trainingData[i][k].answer.toInt()
        result[k] += valueAtK

        for (y in 0..Y - 1) {
          val valueAtKGivenY = trainingData[i][k].by(y).toInt()
          val p = computeP(y, i, x)

          result[k] += valueAtKGivenY * p
        }
      }

      val regularizationTerm = x[k] * lambda
      result[k] -= regularizationTerm
    }

    return result
  }

  private fun computeP(y: Int, i: Int, vs: DoubleArray): Double {
    val entries = trainingData[i]
    val nom = Math.exp(dot(vs, entries.map { it.by(y) }.toBooleanArray()))

    var denom = 0.0
    for (yy in 0..Y - 1) {
      denom += Math.exp(dot(vs, entries.map { it.by(yy) }.toBooleanArray()))
    }

    return nom / denom
  }

  override fun valueAt(x: DoubleArray): Double {
    val linearTerm = trainingData.map { dot(x, it.map { it.answer }.toBooleanArray()) }.sum()

    var logSumTerm = 0.0
    for (i in 0..n - 1) {

      var expTerm = 0.0
      for (y in 0..Y - 1) {
        val fValues = trainingData[i].map { it.by(y) }.toBooleanArray()
        val dotProduct = dot(x, fValues)
        expTerm += Math.exp(dotProduct)
      }

      logSumTerm += Math.log(expTerm)
    }

    val regularizationTerm = x.map { it * it }.sum() * lambda / 2

    return linearTerm - logSumTerm - regularizationTerm
  }

}