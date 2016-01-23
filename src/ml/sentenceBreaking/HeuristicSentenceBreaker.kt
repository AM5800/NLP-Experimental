package ml.sentenceBreaking

import java.util.*

public data class SentenceBounds(public val start: Int, public val end: Int)

public class HeuristicSentenceBreaker {

    private val nonBreakers = HashSet<String>()

    fun setNonBreakingWords(nonBreakers: Iterable<String>) {
        this.nonBreakers.addAll(nonBreakers.map { it.toLowerCase().trimEnd('.') })
    }

    public fun breakText(text: String): Iterable<SentenceBounds> {
        var sentenceStart = 0

        val result = arrayListOf<SentenceBounds>()

        text.forEachIndexed { i, c ->
            if (SentenceBreakerUtils.isSentenceEndChar(c)) {
                if (isSentence(text, sentenceStart, i)) {
                    result.add(SentenceBounds(sentenceStart, i))
                    sentenceStart = i + 1 // TODO shouldn't we omit spaces in front?
                }
            }
        }

        return result
    }

    private fun isSentence(text: String, sentenceStart: Int, probableSentenceEnd: Int): Boolean {
        if (probableSentenceEnd == text.length - 1 && probableSentenceEnd - sentenceStart > 0) return true
        val prevWord = findPrevWord(text, sentenceStart, probableSentenceEnd)
        if (prevWord == null) return false

        if (nonBreakers.contains(prevWord.toLowerCase())) return false

        if (SentenceBreakerUtils.isNumber(prevWord)) return false

        return true
    }

    private fun findPrevWord(text: String, sentenceStart: Int, probableSentenceEnd: Int): String? {
        val index = text.lastIndexOf(' ', probableSentenceEnd)
        if (index == -1) return null
        if (index < sentenceStart) return null

        return text.substring(index, probableSentenceEnd).trim()
    }
}