import ml.sentenceBreaking.*
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

  val heuristicBreaker = HeuristicSentenceBreaker()
  val logLinearBreaker = LogLinearSentenceBreaker(LogLinearSentenceBreakerFeatureSet())

  // TODO educate logLinearBreaker
  val handler = SentenceBreakingHandler(heuristicBreaker)

  val learningRange = RelativeRange(0, 80)
  val validationRange = RelativeRange(learningRange.end, 100)

  val maker = SentenceBreakerTestDataMaker()

  parsers.parse(treebanksRepo.getTreebanks(),
          seedRepo.newHandler(handler, learningRange),
          seedRepo.newHandler(maker, validationRange))

  val tester = SentenceBreakerPerformanceTester()
  val heuristicBreakerPerformance = tester.getPerformance(heuristicBreaker, maker.getTestData())
  val logLinearBreakerPerformance = tester.getPerformance(logLinearBreaker, maker.getTestData(), true)
  println("Heuristic sentence breaking performance: $heuristicBreakerPerformance")
  println("LogLinear sentence breaking performance: $logLinearBreakerPerformance")
}

