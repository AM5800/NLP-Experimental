import corpus.CorpusRepository
import corpus.parsing.CorpusParsersSet
import corpus.parsing.NegraParser
import corpus.parsing.OnlyPlainSentenceSelector
import corpus.parsing.TreexParser
import corpus.parsing.shuffling.CorpusShuffleRepository
import corpus.parsing.shuffling.RelativeRange
import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.LogLinear.LogLinearSentenceBreaker
import ml.sentenceBreaking.LogLinear.LogLinearSentenceBreakerTrainer
import ml.sentenceBreaking.LogLinear.PriorityFeatureSet
import ml.sentenceBreaking.LogLinear.TrainedFeatureSet
import ml.sentenceBreaking.SentenceBreakerPerformanceTester
import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import ml.sentenceBreaking.SentenceBreakingHandler
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import java.io.File
import java.util.*
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

fun main(args: Array<String>) {
  val logger = initLogger()

  val corpuses = CorpusRepository(File("data\\corpuses\\"))
  val parsers = CorpusParsersSet()
  parsers.registerParser(NegraParser())
  parsers.registerParser(TreexParser())

  val seedRepo = CorpusShuffleRepository(corpuses, parsers, logger)

  seedRepo.shuffleIfNeeded(OnlyPlainSentenceSelector())

  val heuristicBreaker = HeuristicSentenceBreaker()

  val featureSet = PriorityFeatureSet()
  val logLinearBreakerTrainer = LogLinearSentenceBreakerTrainer(featureSet, logger)

  val heuristicTrainer = SentenceBreakingHandler(heuristicBreaker)

  val trainingRange = RelativeRange(0, 70)
  val crossValidationRange = RelativeRange(trainingRange.end, 90)
  val testRange = RelativeRange(crossValidationRange.end, 100)

  val crossValidationMaker = SentenceBreakerTestDataMaker()
  val testMaker = SentenceBreakerTestDataMaker()

  logger.info("Parsing treebanks")
  parsers.parse(corpuses.getCorpuses().first { it.infoFile.nameWithoutExtension.equals("TIGER", true) },
          seedRepo.newHandler(heuristicTrainer, trainingRange),
          seedRepo.newHandler(crossValidationMaker, crossValidationRange),
          seedRepo.newHandler(testMaker, testRange),
          seedRepo.newHandler(logLinearBreakerTrainer, trainingRange))
  logger.info("Parsing done")

  logLinearBreakerTrainer.parsingDone()

  val tester = SentenceBreakerPerformanceTester()

  val lambdas = doubleArrayOf(0.0, 1e-10, 1e-5, 1e-2, 0.25, 0.5, 1.0, 20.0, 500.0, 1e5, 1e10)

  val lambdasString = lambdas.map { it.toString() }.joinToString(", ")
  logger.info("Creating feature sets for λ=($lambdasString)")

  val featureSets = Arrays.stream(lambdas).parallel().mapToObj { lambda -> logLinearBreakerTrainer.createTrainedFeatureSet(lambda) }.toArray().filterIsInstance<TrainedFeatureSet>()
  logger.info("Feature sets created")

  val bestLogLinearBreaker = featureSets
          .map { fs -> LogLinearSentenceBreaker(fs) }
          .maxBy { breaker ->
            val performance = tester.getPerformance(breaker, crossValidationMaker.getTestData())
            logger.info("Performance(λ=${breaker.trainedFeatureSet.lambda}): $performance")
            performance
          }!!

  println("Selected best log linear λ = ${bestLogLinearBreaker.trainedFeatureSet.lambda}")
  val logLinearPerformance = tester.getPerformance(bestLogLinearBreaker, testMaker.getTestData(), true)
  println("LogLinear sentence breaking performance(λ=${bestLogLinearBreaker.trainedFeatureSet.lambda}): $logLinearPerformance")

  val heuristicBreakerPerformance = tester.getPerformance(heuristicBreaker, testMaker.getTestData())
  println("Heuristic sentence breaking performance: $heuristicBreakerPerformance")
}

fun initLogger(): Logger {
  val logger = Logger.getLogger("NLP")
  logger.useParentHandlers = false
  val console = ConsoleHandler()
  console.formatter = CustomLoggerFormatter()
  logger.addHandler(console)
  return logger
}

class CustomLoggerFormatter : Formatter() {
  private var lastTime: Long? = null

  override fun format(record: LogRecord?): String? {
    if (record == null) return null
    assert(record.threadID == 1) { "Multithreaded logging is not supported yet" }

    val time = formatTime(record.millis)
    return "$time: ${record.message}\n"
  }

  private fun formatTime(millis: Long): String {
    val t = lastTime
    lastTime = millis

    val formatter = PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .appendSeparator(".")
            .minimumPrintedDigits(3)
            .appendMillis()
            .toFormatter()

    if (t == null) {
      val duration = Duration(0)
      return formatter.print(duration.toPeriod())
    } else {
      val duration = Duration(millis - t)
      return "+" + formatter.print(duration.toPeriod())
    }
  }
}

