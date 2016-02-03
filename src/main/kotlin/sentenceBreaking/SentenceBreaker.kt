package ml.sentenceBreaking


interface SentenceBreaker {
    fun breakText(text: String): Iterable<SentenceBounds>
}