package ml.sentenceBreaking.MEMM

import com.github.lbfgs4j.LbfgsMinimizer
import com.github.lbfgs4j.liblbfgs.Function
import ml.sentenceBreaking.SentenceBreakerTag
import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler
import java.util.*

fun Boolean.toInt(): Int {
  if (this == true) return 1
  return 0
}

class LogLinearSentenceBreakerTrainer(private val featureSet: LogLinearSentenceBreakerFeatureSet) : TreebankParserHandler() {
  private val queue = LinkedList<Pair<String, SentenceBreakerTag>>()
  private val trainingData = ArrayList<TrainingTableEntry>()
  private val truncationsFeatureSet = LogLinearTruncationsFeatureSet()


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

    truncationsFeatureSet.addTrainingSample(words, offset, correctTag, trainingData.size)

    trainingData.add(TrainingTableEntry.create(correctTag, features))

    queue.removeFirst()
  }

  fun getTrainedFeatureSet(lambda: Double): TrainedFeatureSet {
    println("Computing parameters")
    val t0 = System.nanoTime()
    val minimizer = LbfgsMinimizer()
    val updatedTrainingData = truncationsFeatureSet.merge(trainingData)
    val costFunction = InvertSignFunction(LogLinearCostFunction(lambda, updatedTrainingData))
    val result = minimizer.minimize(costFunction)
    val dt = (System.nanoTime() - t0) / 1000000
    val minimum = costFunction.valueAt(result)
    println("found maximum: $minimum for $dt ms")
    println("values " + result.map { it.toString() }.joinToString(", "))
    return TrainedFeatureSet(featureSet, truncationsFeatureSet, result)
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



