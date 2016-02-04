package ml.sentenceBreaking

import com.github.lbfgs4j.LbfgsMinimizer
import com.github.lbfgs4j.liblbfgs.Function
import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler
import java.util.*

class TrainingTableEntry(val valueAtRegular: Boolean, val tag: SentenceBreakerTag)

class LogLinearSentenceBreakerTrainer(private val featureSet: LogLinearSentenceBreakerFeatureSet) : TreebankParserHandler() {
  private var currentWord: String? = null

  private val queue = LinkedList<String>()
  private val trainingData = ArrayList<List<TrainingTableEntry>>()

  override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
    if (currentWord != null) {
      queue.addLast(currentWord)
    }
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
    val minimzer = LbfgsMinimizer()
    return minimzer.minimize(CostFunction(lambda, trainingData))
  }

  class CostFunction(val lambda: Double, private val trainingData: ArrayList<List<TrainingTableEntry>>) : Function {

    private val foundValues = trainingData.map {
      it.map {
        if (it.tag == SentenceBreakerTag.Regular) it.valueAtRegular else !it.valueAtRegular
      }.toBooleanArray()
    }.toTypedArray()

    private val allValues: Array<Array<BooleanArray>> = trainingData.map {
      it.map {
        booleanArrayOf(it.valueAtRegular, !it.valueAtRegular)
      }.toTypedArray()
    }.toTypedArray()

    private val n = trainingData.size
    private val m = trainingData.first().size

    override fun getDimension(): Int {
      return m
    }

    override fun gradientAt(x: DoubleArray): DoubleArray {
      throw UnsupportedOperationException()
    }

    override fun valueAt(x: DoubleArray): Double {
      val linearTerm = foundValues.map { dot(x, it) }.sum()

      var logTerm = 0.0
      for (i in 0..n) {

        var expTerm = 0.0
        for (y in 0..allValues.first().first().size) {
          val fValues = allValues[i].map { it[y] }.toBooleanArray()
          val dotProduct = dot(x, fValues)
          expTerm += Math.exp(dotProduct)
        }

        logTerm += Math.log(expTerm)
      }

      val regularizationTerm = x.map { it * it }.sum() * lambda / 2

      return linearTerm + logTerm - regularizationTerm
    }

    private fun dot(x: DoubleArray, y: BooleanArray): Double {
      if (x.size != y.size) throw Exception("x and y size mismatch")

      return x.mapIndexed { i, d -> if (y[i]) d else 0.0 }.sum()
    }
  }
}


