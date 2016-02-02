package ml.sentenceBreaking

import java.util.*

class LogLinearSentenceBreaker(private val featureSet: LogLinearSentenceBreakerFeatureSet) : SentenceBreaker {

    override fun breakText(text: String): Iterable<SentenceBounds> {
        val words = splitWords(text)

        val stack = LinkedList<Located<String>>()
        val history = -3
        val future = 1

        val result = ArrayList<SentenceBounds>()
        var sentenceStart = -1
        for (wordRange in words) {
            val word = text.substring(wordRange.start, wordRange.end)
            stack.addLast(Located(word, wordRange.start))

            if (sentenceStart == -1) sentenceStart = wordRange.start

            if (stack.size < future - history) continue
            val tag = getMostProbableTag(stack, -history - 1)

            if (tag == SentenceBreakerTag.SentenceBreak) {
                result.add(SentenceBounds(sentenceStart, wordRange.end))
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
        val breaks = featureSet.features.map { it(words, index, SentenceBreakerTag.Regular) }.sumBy { if (it) 1 else 0 }

        if (regulars > breaks) return SentenceBreakerTag.Regular
        return SentenceBreakerTag.SentenceBreak
    }
}