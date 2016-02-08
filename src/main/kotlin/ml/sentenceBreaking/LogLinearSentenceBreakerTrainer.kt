package ml.sentenceBreaking

import com.github.lbfgs4j.LbfgsMinimizer
import com.github.lbfgs4j.liblbfgs.Function
import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler
import java.util.*

fun Boolean.toInt(): Int {
  if (this == true) return 1
  return 0
}

class TrainingTableEntry(val valueAtRegular: Boolean, val tag: SentenceBreakerTag) {
  val answer = if (tag == SentenceBreakerTag.Regular) valueAtRegular else !valueAtRegular

  fun by(y: Int): Boolean {
    return values[y]
  }

  val valueAtSentenceBreak = !valueAtRegular

  val values = listOf(valueAtRegular, valueAtSentenceBreak)
}

class LogLinearSentenceBreakerTrainer(private val featureSet: LogLinearSentenceBreakerFeatureSet) : TreebankParserHandler() {
  private var currentWord: String? = null

  private val queue = LinkedList<String>()
  private val trainingData = ArrayList<List<TrainingTableEntry>>()

  override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
    if (currentWord != null) {
      queue.addLast(currentWord)
    }
    currentWord = word
    process(SentenceBreakerTag.Regular)
  }

  override fun endSentence() {
    if (currentWord != null) queue.push(currentWord)
    currentWord = null
    process(SentenceBreakerTag.SentenceBreak)
  }

  private fun process(tag: SentenceBreakerTag) {
    if (queue.size < featureSet.requiredStackSize) return

    while (queue.size > featureSet.requiredStackSize) queue.removeFirst()

    val entries = featureSet.features
            .map { it(queue, featureSet.currentWordOffset, tag) }
            .map { TrainingTableEntry(getValueAtRegular(it, tag), tag) }

    trainingData.add(entries)
  }

  private fun getValueAtRegular(it: Boolean, tag: SentenceBreakerTag) = if (tag == SentenceBreakerTag.Regular) it else !it

  fun computeParameters(lambda: Double): DoubleArray {
    val minimizer = LbfgsMinimizer()
    return minimizer.minimize(CostFunction(lambda, trainingData))
  }

  class CostFunction(val lambda: Double, private val trainingData: List<List<TrainingTableEntry>>) : Function {
    private val n = trainingData.size
    private val m = trainingData.first().size
    private val Y = trainingData.first().first().values.size

    override fun getDimension(): Int {
      return m
    }

    override fun gradientAt(x: DoubleArray): DoubleArray {
      val result = DoubleArray(m)

      for (k in 0..m-1) {
        for (i in 0..n-1) {
          val valueAtK = trainingData[i][k].answer.toInt()
          result[k] += valueAtK

          for (y in 0..Y-1) {
            val valueAtKGivenY = trainingData[i][k].by(y).toInt()
            val p = computeP(y, i, x)

            result[k] -= valueAtKGivenY * p
          }
        }

        val regularizationTerm = x[k] * -lambda
        result[k] -= regularizationTerm
      }

      return result
    }

    private fun computeP(y: Int, i: Int, vs: DoubleArray): Double {
      val entries = trainingData[i]
      val nom = Math.exp(dot(vs, entries.map { it.by(y) }.toBooleanArray()))

      var denom = 0.0
      for (yy in 0..Y-1) {
        denom += Math.exp(dot(vs, entries.map { it.by(yy) }.toBooleanArray()))
      }

      return nom / denom
    }

    override fun valueAt(x: DoubleArray): Double {
      val linearTerm = trainingData.map {dot(x, it.map {it.answer}.toBooleanArray() )}.sum()

      var logTerm = 0.0
      for (i in 0..n-1) {

        var expTerm = 0.0
        for (y in 0..Y-1) {
          val fValues = trainingData[i].map {it.by(y)}.toBooleanArray()
          val dotProduct = dot(x, fValues)
          expTerm += Math.exp(dotProduct)
        }

        logTerm += Math.log(expTerm)
      }

      val regularizationTerm = x.map { it * it }.sum() * lambda / 2

      return linearTerm + logTerm - regularizationTerm
    }

  }
}


