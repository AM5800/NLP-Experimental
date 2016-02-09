package ml.sentenceBreaking

import com.github.lbfgs4j.LbfgsMinimizer
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
    return minimizer.minimize(LogLinearCostFunction(lambda, trainingData))
  }

}



