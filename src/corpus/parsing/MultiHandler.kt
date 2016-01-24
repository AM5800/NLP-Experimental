package corpus.parsing

class MultiHandler(private val handlers: Array<out TreebankParserHandler>) : TreebankParserHandler() {
    override fun beginTreebank(path: String) {
        handlers.forEach { it.beginTreebank(path) }
    }

    override fun startSentence(id: String) {
        handlers.forEach { it.startSentence(id) }
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