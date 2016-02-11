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

class TrainingTableEntry(val valueAt0: Boolean, val valueAt1: Boolean, val tag: SentenceBreakerTag) {

  fun by(y: Int): Boolean {
    return values[y]
  }

  val values = listOf(valueAt0, valueAt1)

  val valueAtSentenceBreak = values[SentenceBreakerTag.values().indexOf(SentenceBreakerTag.SentenceBreak)]
  val valueAtRegular = values[SentenceBreakerTag.values().indexOf(SentenceBreakerTag.Regular)]

  val answer = if (tag == SentenceBreakerTag.Regular) valueAtRegular else valueAtSentenceBreak
}

class LogLinearSentenceBreakerTrainer(private val featureSet: LogLinearSentenceBreakerFeatureSet) : TreebankParserHandler() {
  private val queue = LinkedList<Pair<String, SentenceBreakerTag>>()
  private val trainingData = ArrayList<List<TrainingTableEntry>>()

  override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
    queue.addLast(Pair(word, SentenceBreakerTag.Regular))
    process()
  }

  override fun endSentence() {
    val last = queue.removeLast()
    queue.addLast(Pair(last.first, SentenceBreakerTag.SentenceBreak))
  }

  private fun process() {
    if (queue.size < featureSet.requiredStackSize) return

    while (queue.size > featureSet.requiredStackSize) queue.removeFirst()

    val words = queue.map { it.first }
    val offset = featureSet.currentWordOffset
    val correctTag = queue[offset].second

    val result = featureSet.features.map { feature ->
      val featureResults = SentenceBreakerTag.values().map { tag -> feature(words, offset, tag) }
      TrainingTableEntry(featureResults[0], featureResults[1], correctTag)
    }

    trainingData.add(result)

    queue.removeFirst()
  }

  fun computeParameters(lambda: Double): DoubleArray {
    println("Computing parameters")
    val t0 = System.nanoTime()
    val minimizer = LbfgsMinimizer()
    val costFunction = InvertSignFunction(LogLinearCostFunction(lambda, trainingData.take(100)))
    val result = minimizer.minimize(costFunction)
    val dt = (System.nanoTime() - t0) / 1000000
    val minimum = costFunction.valueAt(result)
    println("found maximum: $minimum for $dt ms")
    println("values " + result.map { it.toString() }.joinToString(", "))
    return result
  }

}

class InvertSignFunction(private val function: Function) : Function {
  override fun getDimension(): Int {
    return function.dimension
  }

  override fun gradientAt(xs: DoubleArray): DoubleArray {
    val gradientAt = function.gradientAt(xs).map { -it }.toDoubleArray()
    return gradientAt
  }

  override fun valueAt(x: DoubleArray): Double {
    val valueAt = function.valueAt(x)
    return -valueAt
  }

}



