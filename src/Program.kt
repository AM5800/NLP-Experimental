import corpus.RelativeRange
import corpus.parsing.NegraParser
import corpus.parsing.TreebankParsersSet
import corpus.parsing.TreexParser
import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.SentenceBreakerPerformanceTester
import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import ml.sentenceBreaking.SentenceBreakingHandler
import java.io.File

fun main(args: Array<String>) {
    val parsers = TreebankParsersSet()

    parsers.registerParser(NegraParser())
    parsers.registerParser(TreexParser())

    val breaker = HeuristicSentenceBreaker()
    val handler = SentenceBreakingHandler(breaker)

    val learningRange = RelativeRange(0.0, 0.7)
    val validationRange = RelativeRange(0.7, 1.0)

    val maker = SentenceBreakerTestDataMaker()

    //    parsers.parseDirectory(File("data\\corpuses\\"), learningRange, handler)
    //    parsers.parseDirectory(File("data\\corpuses\\"), validationRange, maker)
    parsers.parse(File("data\\corpuses\\hamledt3.treebank"), learningRange, handler)
    parsers.parse(File("data\\corpuses\\hamledt3.treebank"), validationRange, maker)

    val performance = SentenceBreakerPerformanceTester().getPerformance(breaker, maker.getTestData())
    println("Sentence breaking performance: $performance")
}

