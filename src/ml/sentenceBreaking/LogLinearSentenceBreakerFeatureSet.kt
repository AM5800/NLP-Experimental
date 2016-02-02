package ml.sentenceBreaking

import java.util.*

class LogLinearSentenceBreakerFeatureSet {
    private val _features = ArrayList<(List<String>, Int, SentenceBreakerTag) -> Boolean>()
    val features: List<(List<String>, Int, SentenceBreakerTag) -> Boolean>
        get() = _features

    init {
        _features.add { ws, i, tag -> nextIsLowercaseFeature(ws, i, tag) }
        _features.add { ws, i, tag -> sentenceBreakButNotPeriod(ws, i, tag) }
    }

    private fun nextIsLowercaseFeature(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
        if (words.size < index + 1) return false

        val word = words[index]
        val isSentenceEndChar = SentenceBreakerUtils.isSentenceEndChar(word.last())
        val nextIsLowercase = words[index + 1].first().isLowerCase()
        if (isSentenceEndChar && nextIsLowercase && tag == SentenceBreakerTag.SentenceBreak) return true
        return false
    }

    private fun sentenceBreakButNotPeriod(words: List<String>, index: Int, tag: SentenceBreakerTag): Boolean {
        val word = words[index]
        val isSentenceEndChar = SentenceBreakerUtils.isSentenceEndChar(word.last())
        val isPeriod = word.last() == '.'

        return tag == SentenceBreakerTag.SentenceBreak && isSentenceEndChar && !isPeriod
    }
}