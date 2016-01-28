package treebank.parsing.shuffling

import treebank.TreebankInfo
import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler
import java.util.*


class ReadTreebankShuffleHandler(private val repository: TreebankShuffleRepository,
                                 private val original: TreebankParserHandler,
                                 private val range: RelativeRange) : TreebankParserHandler() {
    private var shuffle: HashMap<String, Int>? = null
    private var skip = false

    override fun beginTreebank(info: TreebankInfo) {
        shuffle = repository.getShuffle(info)
    }

    override fun beginSentence(id: String) {
        val shuffle = this.shuffle!!
        val index = shuffle[id]!!
        skip = range.accept(index, shuffle.size)

        if (!skip) {
            original.beginSentence(id)
        }
    }

    override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
        if (!skip) {
            original.word(word, lemma, pos)
        }
    }

    override fun endSentence() {
        if (!skip) {
            original.endSentence()
        }
    }

    override fun endTreebank() {
        if (!skip) {
            original.endTreebank()
        }
    }
}