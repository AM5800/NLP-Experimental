package ml.sentenceBreaking

import util.clamp


class SentenceBreakerPerformanceTester {
  fun getPerformance(breaker: SentenceBreaker,
                     data: SentenceBreakerTestData,
                     outputErrors: Boolean = false): Double {
    val sentences = breaker.breakText(data.text)

    val expected = data.sentenceBreaks.toSet()

    val correct = sentences.count { expected.contains(it.end) }
    val total = expected.count() + sentences.count() - correct

    if (outputErrors) {
      println("Found wrong breaks")
      val wrongFound = sentences.filter { !expected.contains(it.end) }
      wrongFound.take(20).forEach { println(formatError(data, it.end, expected)) }

      println("\nNot found breaks")
      val ends = sentences.map { it.end }.toSet()
      val notFound = expected.filter { !ends.contains(it) }
      notFound.take(20).forEach { println(formatError(data, it, expected)) }
    }

    return correct.toDouble() / total
  }

  private fun formatError(data: SentenceBreakerTestData, end: Int, expected: Set<Int>): String {
    val startIndex = end - 50
    val firstPart = data.text.substring(startIndex.clamp(0), end)
    val newEnd = end + 10
    val secondPart = data.text.substring(end, newEnd.clamp(0, data.text.length - 1))

    return firstPart + "/" + secondPart
  }
}