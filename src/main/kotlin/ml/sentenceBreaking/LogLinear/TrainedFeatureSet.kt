package ml.sentenceBreaking.LogLinear

import ml.sentenceBreaking.SentenceBreakerTag


class TrainedFeatureSet(private val features: GroupingFeatureSet,
                        private val vs: DoubleArray,
                        val lambda: Double) {
  val currentWordOffset = features.currentWordOffset
  val requiredStackSize = features.requiredStackSize

  fun reachVerdict(words: List<String>, offset: Int): SentenceBreakerTag {
    val pRegular = features.evaluate(words, offset, SentenceBreakerTag.Regular, vs)
    val pBreak = features.evaluate(words, offset, SentenceBreakerTag.SentenceBreak, vs)

    if (pBreak > pRegular) return SentenceBreakerTag.SentenceBreak
    return SentenceBreakerTag.Regular
  }
}