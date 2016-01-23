package ml.sentenceBreaking

import java.util.*

public data class Sentence(public val start : Int, public val end : Int)

public class HeuristicSentenceBreaker {

    private val nonBreakers = HashSet<String>()

    fun setNonBreakingWords(nonBreakers: Iterable<String>) {
        this.nonBreakers.addAll(nonBreakers.map { it.toLowerCase().trimEnd('.') })
    }

    public fun breakText(text : String) : Iterable<Sentence> {
        var searchStartIndex = 0
        var sentenceStart = 0

        val result = arrayListOf<Sentence>()

        while (true) {
            val index = text.indexOf('.', searchStartIndex)
            if (index == -1) break // TODO: check last sentence

            if (isSentence(text, sentenceStart, index)) {
                result.add(Sentence(sentenceStart, index))
                sentenceStart = index + 1
            }
            searchStartIndex = index + 1
        }

        return result
    }

    private fun isSentence(text: String, sentenceStart: Int, probableSentenceEnd: Int): Boolean {
        val prevWord = findPrevWord(text, sentenceStart, probableSentenceEnd)
        if (prevWord == null) return false

        val breaks = !nonBreakers.contains(prevWord.toLowerCase())
        return breaks
    }

    private fun findPrevWord(text: String, sentenceStart: Int, probableSentenceEnd: Int): String? {
        val index = text.lastIndexOf(' ', probableSentenceEnd)
        if (index == -1) return null
        if (index < sentenceStart) return null

        return text.substring(index, probableSentenceEnd).trim()
    }
}