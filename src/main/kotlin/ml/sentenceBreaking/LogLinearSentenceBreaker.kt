package ml.sentenceBreaking

import java.util.*

class LogLinearSentenceBreaker(val featureSet: LogLinearSentenceBreakerFeatureSet) : SentenceBreaker {
  override fun breakText(text: String): Iterable<SentenceBounds> {
    val words = splitWords(text)

    val stack = LinkedList<Located<String>>()

    val result = ArrayList<SentenceBounds>()
    var sentenceStart = -1
    for (wordRange in words) {
      val lastWord = text.substring(wordRange.start, wordRange.end)
      stack.addLast(Located(lastWord, wordRange.start, wordRange.end))

      if (sentenceStart == -1) sentenceStart = wordRange.start

      if (stack.size < featureSet.requiredStackSize) continue
      val currentWordIndex = featureSet.currentWordOffset
      val tag = getMostProbableTag(stack, currentWordIndex)

      val currentWord = stack[currentWordIndex]

      if (tag == SentenceBreakerTag.SentenceBreak) {
        result.add(SentenceBounds(sentenceStart, currentWord.end - 1))
        sentenceStart = -1
      }

      stack.removeFirst()
    }

    return result
  }

  private fun getMostProbableTag(list: List<Located<String>>, index: Int): SentenceBreakerTag {
    val words = list.map { it.value }

    // TODO V-vector
    val regulars = featureSet.features.map { it(words, index, SentenceBreakerTag.Regular) }.sumBy { if (it) 1 else 0 }
    val breaks = featureSet.features.map { it(words, index, SentenceBreakerTag.SentenceBreak) }.sumBy { if (it) 1 else 0 }

    if (regulars >= breaks) return SentenceBreakerTag.Regular
    return SentenceBreakerTag.SentenceBreak
  }
}