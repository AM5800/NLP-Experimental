package ml.sentenceBreaking


public class SentenceBreakerPerformanceTester {
    fun getPerformance(breaker : HeuristicSentenceBreaker, data: SentenceBreakerTestData) : Double {
        val sentences = breaker.breakText(data.text)

        val expected = data.sentenceBreaks.toSet()

        val correct = sentences.count {expected.contains(it.end)}
        val total = expected.count() + sentences.count() - correct

        val wrong = sentences.filter { !expected.contains(it.end) }
        wrong.take(10).forEach { println(data.text.substring(it.start - 5, it.end + 5)) }


        return correct.toDouble() / total
    }
}