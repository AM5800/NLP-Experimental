package ml.sentenceBreaking

import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler
import java.util.*
import java.util.regex.Pattern

class SentenceBreakingHandler(private val breaker: HeuristicSentenceBreaker) : TreebankParserHandler() {
    val nonBreakers = HashSet<String>()
    private val romanNumberRegex = Pattern.compile("[IVXLCDM]+\\.")

    override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
        if (pos == ParsePartOfSpeech.Number || pos == ParsePartOfSpeech.Punctuation) return

        if (check(word)) {
            nonBreakers.add(word.toLowerCase())
        }
        if (check(lemma)) {
            nonBreakers.add(lemma.toLowerCase())
        }
    }

    private fun check(word: String): Boolean {
        if (word.first().isDigit()) return false
        if (!word.endsWith('.')) return false
        val isRomanNumber = romanNumberRegex.matcher(word).matches()
        return !isRomanNumber
    }

    override fun endTreebank() {
        breaker.setNonBreakingWords(nonBreakers)
    }
}