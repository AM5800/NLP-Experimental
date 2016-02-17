package ml.sentenceBreaking.LogLinear

import ml.sentenceBreaking.SentenceBreakerTag
import ml.sentenceBreaking.SentenceBreakerUtils
import util.dot
import java.util.*

class PriorityFeatureSet {
  val history = 2
  val future = 1
  val requiredStackSize = future + history + 1
  val currentWordOffset = history

  private val truncationsFeatureSet = LogLinearTruncationsFeatureSet()

  private val features = ArrayList<(List<String>, Int, SentenceBreakerTag) -> Boolean>()
  private val trainingData = ArrayList<TrainingTableEntry>()

  init {
    features.add { ws, i, tag -> nonPeriod(ws, i, tag) }
    features.add { ws, i, tag -> periodLowercase(ws, i, tag) }
    features.add { ws, i, tag -> numberPeriodNumber(ws, i, tag) }
    features.add { ws, i, tag -> charPeriod(ws, i, tag) }
    features.add { ws, i, tag -> upperNumberPeriod(ws, i, tag) }
    features.add { ws, i, tag -> numberPeriod(ws, i, tag) }
    features.add { ws, i, tag -> period(ws, i, tag) }
  }

  private fun periodLowercase(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words.size < index + 1) return false

    val word = words[index]
    val isSentenceEndChar = SentenceBreakerUtils.isSentenceEndChar(word.last())
    val nextIsLowercase = words[index + 1].first().isLowerCase()
    if (isSentenceEndChar && nextIsLowercase && tag == SentenceBreakerTag.Regular) {
      return true
    }
    return false
  }

  private fun nonPeriod(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    val word = words[index]
    val isSentenceEndChar = word.indexOfAny(charArrayOf('!', '?')) != -1// word.contains("!\\?")// SentenceBreakerUtils.isSentenceEndChar(word.last())
    val isPeriod = word.last() == '.'

    return tag == SentenceBreakerTag.SentenceBreak && isSentenceEndChar && !isPeriod
  }

  private fun period(words: List<String>, offset: Int, tag: SentenceBreakerTag): Boolean {
    if (numberPeriod(words, offset, tag)) return false
    if (numberPeriodNumber(words, offset, tag)) return false
    if (charPeriod(words, offset, tag)) return false
    if (upperNumberPeriod(words, offset, tag)) return false

    val word = words[offset]
    return tag == SentenceBreakerTag.SentenceBreak && word.endsWith('.')
  }

  private fun numberPeriodNumber(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words[index] != "." || tag == SentenceBreakerTag.SentenceBreak) return false
    return SentenceBreakerUtils.isNumber(words[index - 1]) && SentenceBreakerUtils.isNumber(words[index + 1])
  }

  private fun numberPeriod(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words[index] != "." || tag == SentenceBreakerTag.SentenceBreak) return false
    return SentenceBreakerUtils.isNumber(words[index - 1])
  }

  private fun charPeriod(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words[index] != "." || tag == SentenceBreakerTag.SentenceBreak) return false

    val prevWord = words[index - 1]
    val result = prevWord.length == 1 && prevWord.first().isLetter()

    return result
  }

  private fun upperNumberPeriod(words: List<String>, offset: Int, tag: SentenceBreakerTag): Boolean {
    if (tag == SentenceBreakerTag.Regular) return false

    val word = words[offset]
    if (word != ".") return false

    val prevWord = words[offset - 1]
    if (!SentenceBreakerUtils.isNumber(prevWord)) return false

    val prevPrevWord = words[offset - 2]
    return prevPrevWord.first().isUpperCase()
  }

  fun evaluate(words: List<String>, offset: Int, tag: SentenceBreakerTag, vs: DoubleArray): Double {
    val featureVector = getFeatureVector(words, offset, tag)
    val truncationsP = truncationsFeatureSet.evaluate(words, offset, tag, vs.drop(features.size))

    return dot(vs.take(featureVector.size).toDoubleArray(), featureVector) + truncationsP
  }

  private fun getFeatureVector(words: List<String>, offset: Int, tag: SentenceBreakerTag): BooleanArray {
    val result = BooleanArray(features.size)
    features.forEachIndexed { i, feature ->
      val featureValue = feature(words, offset, tag)
      if (featureValue) {
        result[i] = true
        return@forEachIndexed
      }
    }

    return result
  }

  fun train(words: List<String>, offset: Int, tag: SentenceBreakerTag) {

    truncationsFeatureSet.train(words, offset, tag, trainingData.size)

    val values = SentenceBreakerTag.values().map { tag ->
      Pair(tag, getFeatureVector(words, offset, tag))
    }.toMap()

    trainingData.add(TrainingTableEntry.create(tag, values))
  }

  fun filterSingleOccurrences() {
    truncationsFeatureSet.filterSingleOccurrences()
  }

  fun getTrainingData(): List<TrainingTableEntry> {
    return truncationsFeatureSet.merge(trainingData)
  }
}