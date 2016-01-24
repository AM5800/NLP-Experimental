package corpus.parsing


open class TreebankParserHandler {

    open fun beginTreebank(path: String) {
    }

    open fun startSentence(id: String) {
    }

    open fun word(word: String, lemma: String, pos: String?) {
    }

    open fun endSentence() {
    }

    open fun endTreebank() {
    }
}