package ml.sentenceBreaking

import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler

public class SentenceBreakerTestData(public val text: String, public val sentenceBreaks: List<Int>)


public class SentenceBreakerTestDataMaker : TreebankParserHandler() {
    private val builder = StringBuilder()
    private val sentenceBreaks = arrayListOf<Int>()

    override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
        if (builder.length != 0 && pos != ParsePartOfSpeech.Punctuation) builder.append(' ')
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