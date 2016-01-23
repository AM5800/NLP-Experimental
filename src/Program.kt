import corpus.CorpusRange
import corpus.CorpusSplittingHandler
import corpus.ExternalShuffler
import corpus.parsing.CorpusParserHandler
import corpus.parsing.NegraParser
import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.SentenceBreakingHandler
import java.nio.file.Paths
import java.util.*

fun main(args : Array<String>) {
    val breaker = HeuristicSentenceBreaker()
    val handler = SentenceBreakingHandler(breaker)
    val negraParser = NegraParser()

    val learningSet = CorpusRange(0.0, 0.7)
    val validationSet = CorpusRange(0.7, 1.0)

    val corpusSplittingHandler = CorpusSplittingHandler(handler, learningSet)

    negraParser.parse("data\\corpuses\\tiger_release_aug07.corrected.16012013.xml", corpusSplittingHandler)

    for (nonBreaker in handler.nonBreakers) {
        println("Non breaker: " + nonBreaker)
    }
}

class OutputHandler : CorpusParserHandler() {
    override fun startSentence(id: String) {
        println("Started sentence: " + id)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        println("\tWord " + word)
    }

    override fun endSentence() {

    }

}
