package ml.sentenceBreaking.LogLinear

import com.github.lbfgs4j.LbfgsMinimizer
import com.github.lbfgs4j.liblbfgs.Function
import corpus.parsing.CorpusParserHandler
import corpus.parsing.ParsePartOfSpeech
import ml.sentenceBreaking.SentenceBreakerTag
import java.util.*
import java.util.logging.Logger

fun Boolean.toInt(): Int {
  if (this == true) return 1
  return 0
}

class LogLinearSentenceBreakerTrainer(private val featureSet: PriorityFeatureSet, private val logger: Logger) : CorpusParserHandler() {
  private val queue = LinkedList<Pair<String, SentenceBreakerTag>>()

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

    featureSet.train(words, offset, correctTag)

    queue.removeFirst()
  }

  fun createTrainedFeatureSet(lambda: Double): TrainedFeatureSet {
    val minimizer = LbfgsMinimizer(false)
    val merged = mergedTrainingData ?: throw Exception("Training data is not merged. Forgot to call parsingDone?")
    val costFunction = InvertSignFunction(LogLinearCostFunction(lambda, merged))
    val result = minimizer.minimize(costFunction)
    return TrainedFeatureSet(featureSet, result, lambda)
  }

  private var mergedTrainingData: List<TrainingTableEntry>? = null

  fun parsingDone() {
    logger.info("Merging training data")
    featureSet.filterSingleOccurrences()
    val merged = featureSet.getTrainingData()
    mergedTrainingData = merged
    logger.info("Training data merged. Using ${merged.first().M} features and ${merged.size} training samples")
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



