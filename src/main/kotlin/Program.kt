import ml.sentenceBreaking.HeuristicSentenceBreaker
import ml.sentenceBreaking.LogLinear.LogLinearSentenceBreaker
import ml.sentenceBreaking.LogLinear.LogLinearSentenceBreakerFeatureSet
import ml.sentenceBreaking.LogLinear.LogLinearSentenceBreakerTrainer
import ml.sentenceBreaking.SentenceBreakerPerformanceTester
import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import ml.sentenceBreaking.SentenceBreakingHandler
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import treebank.TreebankRepository
import treebank.parsing.NegraParser
import treebank.parsing.OnlyPlainSentenceSelector
import treebank.parsing.TreebankParsersSet
import treebank.parsing.TreexParser
import treebank.parsing.shuffling.RelativeRange
import treebank.parsing.shuffling.TreebankShuffleRepository
import java.io.File
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

fun main(args: Array<String>) {
  val logger = initLogger()

  val treebanksRepo = TreebankRepository(File("data\\corpuses\\"))
  val parsers = TreebankParsersSet()
  parsers.registerParser(NegraParser())
  parsers.registerParser(TreexParser())

  val seedRepo = TreebankShuffleRepository(treebanksRepo, parsers, logger)

  seedRepo.shuffleIfNeeded(OnlyPlainSentenceSelector())

  val heuristicBreaker = HeuristicSentenceBreaker()

  val featureSet = LogLinearSentenceBreakerFeatureSet()
  val logLinearBreakerTrainer = LogLinearSentenceBreakerTrainer(featureSet, logger)

  val handler = SentenceBreakingHandler(heuristicBreaker)

  val trainingRange = RelativeRange(0, 80)
  val crossValidationRange = RelativeRange(trainingRange.end, 90)
  val testRange = RelativeRange(crossValidationRange.end, 100)

  val maker = SentenceBreakerTestDataMaker()

  logger.info("Parsing...")
  parsers.parse(treebanksRepo.getTreebanks().first { it.treebankPath.absolutePath.contains("TIGER", true) },
          seedRepo.newHandler(handler, trainingRange),
          seedRepo.newHandler(maker, testRange),
          seedRepo.newHandler(logLinearBreakerTrainer, trainingRange))
  logger.info("Parsing done")

  val tester = SentenceBreakerPerformanceTester()
  val logLinearBreaker = LogLinearSentenceBreaker(logLinearBreakerTrainer.getTrainedFeatureSet(20.0))
  val heuristicBreakerPerformance = tester.getPerformance(heuristicBreaker, maker.getTestData())
  val logLinearBreakerPerformance = tester.getPerformance(logLinearBreaker, maker.getTestData())
  println("Heuristic sentence breaking performance: $heuristicBreakerPerformance")
  println("LogLinear sentence breaking performance: $logLinearBreakerPerformance")

  logger.info("Done")
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

    val time = formatTime(record.millis)
    return "$time: ${record.message}\n"
  }

  private fun formatTime(millis: Long): String {
    val t = lastTime
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
      lastTime = millis
      return formatter.print(duration.toPeriod())
    } else {
      val duration = Duration(millis - t)
      return "+" + formatter.print(duration.toPeriod())
    }
  }
}

