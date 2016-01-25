package corpus.parsing

import java.io.File


open class TreebankParserHandler {

    open fun beginTreebank(path: File) {
    }

    open fun beginSentence(id: String) {
    }

    open fun word(word: String, lemma: String, pos: String?) {
    }

    open fun endSentence() {
    }

    open fun endTreebank() {
    }
}