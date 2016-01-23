import corpus.CorpusRange
import corpus.CorpusSplittingHandler
import corpus.parsing.CorpusParserHandler
import corpus.parsing.NegraParser
import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.SentenceBreakerPerformanceTester
import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import ml.sentenceBreaking.SentenceBreakingHandler

fun main(args: Array<String>) {
    val breaker = HeuristicSentenceBreaker()
    val handler = SentenceBreakingHandler(breaker)
    val negraParser = NegraParser()

    val learningHandler = CorpusSplittingHandler(handler, CorpusRange(0.0, 0.7))

    val maker = SentenceBreakerTestDataMaker()
    val validationHandler = CorpusSplittingHandler(maker, CorpusRange(0.7, 1.0))

    negraParser.parse("data\\corpuses\\tiger_release_aug07.corrected.16012013.xml", learningHandler)
    negraParser.parse("data\\corpuses\\tiger_release_aug07.corrected.16012013.xml", validationHandler)

    val performance = SentenceBreakerPerformanceTester().getPerformance(breaker, maker.getTestData())
    println("Sentence breaking performance: $performance")
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
