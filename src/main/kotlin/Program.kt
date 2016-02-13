import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.MEMM.LogLinearSentenceBreaker
import ml.sentenceBreaking.MEMM.LogLinearSentenceBreakerFeatureSet
import ml.sentenceBreaking.MEMM.LogLinearSentenceBreakerTrainer
import ml.sentenceBreaking.SentenceBreakerPerformanceTester
import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import ml.sentenceBreaking.SentenceBreakingHandler
import treebank.TreebankRepository
import treebank.parsing.NegraParser
import treebank.parsing.OnlyPlainSentenceSelector
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

  seedRepo.shuffleIfNeeded(OnlyPlainSentenceSelector())

  val heuristicBreaker = HeuristicSentenceBreaker()

  val featureSet = LogLinearSentenceBreakerFeatureSet()
  val logLinearBreakerTrainer = LogLinearSentenceBreakerTrainer(featureSet)

  val handler = SentenceBreakingHandler(heuristicBreaker)

  val trainingRange = RelativeRange(0, 80)
  val crossValidationRange = RelativeRange(trainingRange.end, 90)
  val testRange = RelativeRange(crossValidationRange.end, 100)

  val maker = SentenceBreakerTestDataMaker()

  parsers.parse(treebanksRepo.getTreebanks().first { it.treebankPath.absolutePath.contains("TIGER", true) },
          seedRepo.newHandler(handler, trainingRange),
          seedRepo.newHandler(maker, testRange),
          seedRepo.newHandler(logLinearBreakerTrainer, RelativeRange(0, 1)))

  val tester = SentenceBreakerPerformanceTester()
  val logLinearBreaker = LogLinearSentenceBreaker(logLinearBreakerTrainer.getTrainedFeatureSet(20.0))
  val heuristicBreakerPerformance = tester.getPerformance(heuristicBreaker, maker.getTestData())
  val logLinearBreakerPerformance = tester.getPerformance(logLinearBreaker, maker.getTestData())
  println("Heuristic sentence breaking performance: $heuristicBreakerPerformance")
  println("LogLinear sentence breaking performance: $logLinearBreakerPerformance")
}

