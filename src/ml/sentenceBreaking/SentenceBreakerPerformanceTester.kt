package ml.sentenceBreaking


public class SentenceBreakerPerformanceTester {
    fun getPerformance(breaker : HeuristicSentenceBreaker, data: SentenceBreakerTestData) : Double {
        val sentences = breaker.breakText(data.text)

        val expected = data.sentenceBreaks.toSet()

        val correct = sentences.count {expected.contains(it.end)}
        val total = expected.count() + sentences.count() - correct

        return correct.toDouble() / total
    }
}