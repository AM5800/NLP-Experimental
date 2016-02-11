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

class TrainingTableEntry(val correctTag: SentenceBreakerTag, features: List<(SentenceBreakerTag) -> Boolean>) {
  private val values = SentenceBreakerTag.values().map { tag ->
    features.map { feature ->
      feature(tag)
    }.toBooleanArray()
  }

  val M: Int = values.first().size

  fun answerAt(k: Int): Boolean {
    return values[SentenceBreakerTag.values().indexOf(correctTag)][k]
  }

  fun answerAt(k: Int, tag: SentenceBreakerTag): Boolean {
    return values[SentenceBreakerTag.values().indexOf(tag)][k]
  }

  fun featureResultsAtTag(tag: SentenceBreakerTag): BooleanArray {
    return values[SentenceBreakerTag.values().indexOf(tag)]
  }
}

class LogLinearSentenceBreakerTrainer(private val featureSet: LogLinearSentenceBreakerFeatureSet) : TreebankParserHandler() {
  private val queue = LinkedList<Pair<String, SentenceBreakerTag>>()
  private val trainingData = ArrayList<TrainingTableEntry>()

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

    val features = featureSet.features.map { feature ->
      { tag: SentenceBreakerTag -> feature(words, offset, tag) }
    }

    trainingData.add(TrainingTableEntry(correctTag, features))

    queue.removeFirst()
  }

  fun computeParameters(lambda: Double): DoubleArray {
    println("Computing parameters")
    val t0 = System.nanoTime()
    val minimizer = LbfgsMinimizer()
    val costFunction = InvertSignFunction(LogLinearCostFunction(lambda, trainingData))
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



