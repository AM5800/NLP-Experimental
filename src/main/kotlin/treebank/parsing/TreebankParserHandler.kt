package treebank.parsing

import treebank.TreebankInfo


open class TreebankParserHandler {

    open fun beginTreebank(info: TreebankInfo) {
    }

    open fun beginSentence(id: String) {
    }

    open fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
    }

    open fun endSentence() {
    }

    open fun endTreebank() {
    }
}