package ml.sentenceBreaking.MEMM

import ml.sentenceBreaking.*
import java.util.*

class LogLinearSentenceBreaker(val trainedFeatureSet: TrainedFeatureSet) : SentenceBreaker {
  override fun breakText(text: String): Iterable<SentenceBounds> {
    val words = splitWords(text)

    val stack = LinkedList<Located<String>>()

    val result = ArrayList<SentenceBounds>()
    var sentenceStart = -1
    for (wordRange in words) {
      val lastWord = text.substring(wordRange.start, wordRange.end)
      stack.addLast(Located(lastWord, wordRange.start, wordRange.end))

      if (sentenceStart == -1) sentenceStart = wordRange.start

      if (stack.size < trainedFeatureSet.requiredStackSize) continue
      val currentWordIndex = trainedFeatureSet.currentWordOffset
      val tag = trainedFeatureSet.reachVerdict(stack.map { it.value }, currentWordIndex)

      val currentWord = stack[currentWordIndex]

      if (tag == SentenceBreakerTag.SentenceBreak) {
        result.add(SentenceBounds(sentenceStart, currentWord.end - 1))
        sentenceStart = -1
      }

      stack.removeFirst()
    }

    return result
  }
}