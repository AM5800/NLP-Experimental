package ml.sentenceBreaking

import java.util.*

data class SentenceBounds(val start: Int, val end: Int)

class HeuristicSentenceBreaker : SentenceBreaker {

  private val nonBreakers = HashSet<String>()

  fun setNonBreakingWords(nonBreakers: Iterable<String>) {
    this.nonBreakers.addAll(nonBreakers.map { it.toLowerCase().trimEnd('.') })
  }

  override fun breakText(text: String): Iterable<SentenceBounds> {
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

    if (text.length - sentenceStart > 3) {
      result.add(SentenceBounds(sentenceStart, text.length - 1))
    }

    return result
  }


  private fun isSentence(text: String, sentenceStart: Int, probableSentenceEnd: Int): Boolean {
    if (probableSentenceEnd == text.length - 1 && probableSentenceEnd - sentenceStart > 0) return true

    // example: abc.de.fg - sentence can't break here
    if (!text[probableSentenceEnd + 1].isWhitespace()) {
      return false
    }

    val currentChar = text[probableSentenceEnd]

    // Most problematic char
    if (currentChar != '.') {
      return true
    }

    val prevWord = findPrevWord(text, sentenceStart, probableSentenceEnd)
    if (prevWord == null) return false

    // example: m.
    //if (prevWord.length == 1 && prevWord.first().isLetter()) return false

    // example: mr. Dursley
    if (nonBreakers.contains(prevWord.toLowerCase())) return false

    // TODO how to break in "... am 6. Juli" vs "... in 2004."?
    // example: 1.5
    //if (SentenceBreakerUtils.isNumber(prevWord)) return false

    // example: www.leningrad.spb.ru
    if (SentenceBreakerUtils.isWebLink(prevWord)) return false

    // example A.V.G.
    if (SentenceBreakerUtils.isAbbreviation(prevWord + ".")) return false

    return true
  }

  private fun findPrevWord(text: String, sentenceStart: Int, probableSentenceEnd: Int): String? {
    val index = text.lastIndexOf(' ', probableSentenceEnd)
    if (index == -1) return null
    if (index < sentenceStart) return null

    return text.substring(index, probableSentenceEnd).trim()
  }
}