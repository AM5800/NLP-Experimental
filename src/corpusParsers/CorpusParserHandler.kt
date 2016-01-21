package corpusParsers


public interface CorpusParserHandler {
    fun startSentence(id: String)

    fun word(word: String, lemma: String, pos: String?)

    fun endSentence()
}