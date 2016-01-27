package corpus.parsing

import corpus.RelativeRange
import java.io.File


class RangeHandler(private val range: RelativeRange, private val original: TreebankParserHandler) : TreebankParserHandler() {
    var skip = false

    override fun beginTreebank(path: File) {
        original.beginTreebank(path)
    }

    override fun beginSentence(id: String, sentenceNumber: Int?, totalSentences: Int?) {
        if (sentenceNumber == null || totalSentences == null) throw Exception("Sentence range information is undefined")
        skip = range.accept(sentenceNumber, totalSentences)
        if (!skip) original.beginSentence(id, sentenceNumber, totalSentences)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        if (!skip) original.word(word, lemma, pos)
    }

    override fun endSentence() {
        if (!skip) original.endSentence()
    }

    override fun endTreebank() {
        original.endTreebank()
    }
}