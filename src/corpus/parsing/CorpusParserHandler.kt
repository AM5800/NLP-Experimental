package corpus.parsing


public open class CorpusParserHandler {

    open fun beginCorpus(path : String) {}

    open fun startSentence(id: String) {}

    open fun word(word: String, lemma: String, pos: String?) {}

    open fun endSentence() {}

    open fun endCorpus() {}
}