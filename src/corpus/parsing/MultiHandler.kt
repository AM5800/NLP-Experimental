package corpus.parsing

import java.io.File

class MultiHandler(private val handlers: Array<out TreebankParserHandler>) : TreebankParserHandler() {
    override fun beginTreebank(path: File) {
        handlers.forEach { it.beginTreebank(path) }
    }

    override fun beginSentence(id: String) {
        handlers.forEach { it.beginSentence(id) }
    }

    override fun word(word: String, lemma: String, pos: String?) {
        handlers.forEach { it.word(word, lemma, pos) }
    }

    override fun endSentence() {
        handlers.forEach { it.endSentence() }
    }

    override fun endTreebank() {
        handlers.forEach { it.endTreebank() }
    }
}