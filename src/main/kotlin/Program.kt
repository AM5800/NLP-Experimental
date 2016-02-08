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

  val featureSet = LogLinearSentenceBreakerFeatureSet()
  val logLinearBreakerTrainer = LogLinearSentenceBreakerTrainer(featureSet)

  val handler = SentenceBreakingHandler(heuristicBreaker)

  val trainingRange = RelativeRange(0, 80)
  val crossValidationRange = RelativeRange(trainingRange.end, 90)
  val testRange = RelativeRange(crossValidationRange.end, 100)

  val maker = SentenceBreakerTestDataMaker()

  parsers.parse(treebanksRepo.getTreebanks(),
          seedRepo.newHandler(handler, trainingRange),
          seedRepo.newHandler(maker, testRange),
          seedRepo.newHandler(logLinearBreakerTrainer, trainingRange))

  val tester = SentenceBreakerPerformanceTester()
  val logLinearBreaker = LogLinearSentenceBreaker(featureSet, logLinearBreakerTrainer.computeParameters(20.0))
  val heuristicBreakerPerformance = tester.getPerformance(heuristicBreaker, maker.getTestData())
  val logLinearBreakerPerformance = tester.getPerformance(logLinearBreaker, maker.getTestData())
  println("Heuristic sentence breaking performance: $heuristicBreakerPerformance")
  println("LogLinear sentence breaking performance: $logLinearBreakerPerformance")
}

