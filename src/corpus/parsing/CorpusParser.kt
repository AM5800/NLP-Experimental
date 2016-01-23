package corpus.parsing


public interface CorpusParser {
    public fun parse(path : String, handler: CorpusParserHandler)
}