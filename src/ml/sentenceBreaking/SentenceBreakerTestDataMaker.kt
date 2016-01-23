package ml.sentenceBreaking

import corpus.parsing.CorpusParserHandler

public class SentenceBreakerTestData(public val text : String, public val sentenceBreaks : List<Int>)


public class SentenceBreakerTestDataMaker : CorpusParserHandler() {
    private val builder = StringBuilder()
    private val sentenceBreaks = arrayListOf<Int>()

    override fun beginCorpus(path: String) {
        builder.setLength(0)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        if (builder.length != 0 && pos?.startsWith('$') == false) builder.append(' ')
        builder.append(word)
    }

    override fun endSentence() {
        if (!isSentenceEndChar(builder.last())) builder.append('.')
        sentenceBreaks.add(builder.length - 1)
    }

    private fun isSentenceEndChar(ch : Char) : Boolean {
        return ch == '.' || ch == '?' || ch == '!'
    }

    override fun endCorpus() {
        super.endCorpus()
    }

    public fun getTestData() : SentenceBreakerTestData {
        return SentenceBreakerTestData(builder.toString(), sentenceBreaks)
    }
}