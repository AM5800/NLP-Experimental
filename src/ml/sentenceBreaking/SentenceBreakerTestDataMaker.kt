package ml.sentenceBreaking

import treebank.TreebankInfo
import treebank.parsing.TreebankParserHandler

public class SentenceBreakerTestData(public val text: String, public val sentenceBreaks: List<Int>)


public class SentenceBreakerTestDataMaker : TreebankParserHandler() {
    private val builder = StringBuilder()
    private val sentenceBreaks = arrayListOf<Int>()

    override fun beginTreebank(info: TreebankInfo) {
        builder.setLength(0)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        if (builder.length != 0 && pos?.startsWith('$') == false) builder.append(' ')
        builder.append(word)
    }

    override fun endSentence() {
        if (!SentenceBreakerUtils.isSentenceEndChar(builder.last())) builder.append('.')
        sentenceBreaks.add(builder.length - 1)
    }

    override fun endTreebank() {
        super.endTreebank()
    }

    public fun getTestData(): SentenceBreakerTestData {
        return SentenceBreakerTestData(builder.toString(), sentenceBreaks)
    }
}