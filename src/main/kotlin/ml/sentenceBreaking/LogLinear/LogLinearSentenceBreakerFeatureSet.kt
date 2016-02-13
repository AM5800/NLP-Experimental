package ml.sentenceBreaking.LogLinear

import ml.sentenceBreaking.SentenceBreakerTag
import ml.sentenceBreaking.SentenceBreakerUtils
import util.dot
import java.util.*

class LogLinearSentenceBreakerFeatureSet {
  val history = -3
  val future = 1
  val requiredStackSize = future - history
  val currentWordOffset = -history - 1

  private val _features = ArrayList<(List<String>, Int, SentenceBreakerTag) -> Boolean>()
  val features: List<(List<String>, Int, SentenceBreakerTag) -> Boolean>
    get() = _features

  init {
    _features.add { ws, i, tag -> nextIsLowercaseFeature(ws, i, tag) }
    _features.add { ws, i, tag -> sentenceBreakButNotPeriod(ws, i, tag) }
    _features.add { ws, i, tag -> period(ws, i, tag) }
    _features.add { ws, i, tag -> periodBetweenNumbers(ws, i, tag) }
    _features.add { ws, i, tag -> periodAfterNumber(ws, i, tag) }
    _features.add { ws, i, tag -> periodAfterSingleChar(ws, i, tag) }
  }

  private fun nextIsLowercaseFeature(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words.size < index + 1) return false

    val word = words[index]
    val isSentenceEndChar = SentenceBreakerUtils.isSentenceEndChar(word.last())
    val nextIsLowercase = words[index + 1].first().isLowerCase()
    if (isSentenceEndChar && nextIsLowercase && tag == SentenceBreakerTag.Regular) {
      return true
    }
    return false
  }

  private fun sentenceBreakButNotPeriod(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    val word = words[index]
    val isSentenceEndChar = SentenceBreakerUtils.isSentenceEndChar(word.last())
    val isPeriod = word.last() == '.'

    return tag == SentenceBreakerTag.SentenceBreak && isSentenceEndChar && !isPeriod
  }

  private fun period(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    val word = words[index]
    return tag == SentenceBreakerTag.SentenceBreak && word.endsWith('.')
  }

  private fun periodBetweenNumbers(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words[index] != "." || tag == SentenceBreakerTag.SentenceBreak) return false
    return SentenceBreakerUtils.isNumber(words[index - 1]) && SentenceBreakerUtils.isNumber(words[index + 1])
  }

  private fun periodAfterNumber(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words[index] != "." || tag == SentenceBreakerTag.SentenceBreak) return false
    return SentenceBreakerUtils.isNumber(words[index - 1])
  }

  private fun periodAfterSingleChar(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
    if (words[index] != "." || tag == SentenceBreakerTag.SentenceBreak) return false

    val prevWord = words[index - 1]
    val result = prevWord.length == 1 && prevWord.first().isLetter()

    return result
  }

  fun evaluate(words: List<String>, offset: Int, tag: SentenceBreakerTag, vs: DoubleArray): Double {
    val featureVector = features.map { it(words, offset, tag) }

    return dot(vs.take(featureVector.size), featureVector)
  }
}