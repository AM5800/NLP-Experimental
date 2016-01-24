package ml.sentenceBreaking

import corpus.parsing.TreebankParserHandler
import java.util.*
import java.util.regex.Pattern

class SentenceBreakingHandler(private val breaker: HeuristicSentenceBreaker) : TreebankParserHandler() {
    public val nonBreakers = HashSet<String>()
    private val romanNumberRegex = Pattern.compile("[IVXLCDM]+\\.")

    override fun word(word: String, lemma: String, pos: String?) {
        if (pos == "CARD" || pos?.startsWith('$') == true) return

        if (check(word)) {
            nonBreakers.add(word.toLowerCase())
        }
        if (check(lemma)) {
            nonBreakers.add(lemma.toLowerCase())
        }
    }

    private fun check(word : String) : Boolean {
        if (word.first().isDigit()) return false
        if (!word.endsWith('.')) return false
        val isRomanNumber = romanNumberRegex.matcher(word).matches()
        return !isRomanNumber
    }

    override fun endTreebank() {
        breaker.setNonBreakingWords(nonBreakers)
    }
}