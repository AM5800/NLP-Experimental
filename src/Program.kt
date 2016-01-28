import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.SentenceBreakerPerformanceTester
import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import ml.sentenceBreaking.SentenceBreakingHandler
import treebank.TreebankRepository
import treebank.parsing.NegraParser
import treebank.parsing.TreebankParsersSet
import treebank.parsing.TreexParser
import treebank.parsing.shuffling.RelativeRange
import treebank.parsing.shuffling.TreebankShuffleRepository
import java.io.File

fun main(args: Array<String>) {
    val treebanksRepo = TreebankRepository(File("data\\corpuses\\"))
    val parsers = TreebankParsersSet()
    parsers.registerParser(NegraParser())
    parsers.registerParser(TreexParser())

    val seedRepo = TreebankShuffleRepository(treebanksRepo, parsers)

    seedRepo.shuffleIfNeeded()

    val breaker = HeuristicSentenceBreaker()
    val handler = SentenceBreakingHandler(breaker)

    val learningRange = RelativeRange(0, 80)
    val validationRange = RelativeRange(learningRange.end, 100)

    val maker = SentenceBreakerTestDataMaker()

    parsers.parse(treebanksRepo.getTreebanks(),
            seedRepo.newHandler(handler, learningRange),
            seedRepo.newHandler(maker, validationRange))

    val performance = SentenceBreakerPerformanceTester().getPerformance(breaker, maker.getTestData())
    println("Sentence breaking performance: $performance")
}

