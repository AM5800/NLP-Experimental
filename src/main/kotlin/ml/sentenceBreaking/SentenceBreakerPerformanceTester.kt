package ml.sentenceBreaking


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
    val firstPart = data.text.substring(if (startIndex < 0) 0 else startIndex, end)
    val secondPart = data.text.subSequence(end, end + 10)

    return firstPart + "/" + secondPart
  }
}