package ml.sentenceBreaking.MEMM

import ml.sentenceBreaking.SentenceBreakerTag


class TrainedFeatureSet(private val features: LogLinearSentenceBreakerFeatureSet,
                        private val additionalFeatures: LogLinearTruncationsFeatureSet,
                        private val vs: DoubleArray) {
  val currentWordOffset = features.currentWordOffset
  val requiredStackSize = features.requiredStackSize

  fun reachVerdict(words: List<String>, offset: Int): SentenceBreakerTag {
    val pRegular1 = features.evaluate(words, offset, SentenceBreakerTag.Regular, vs)
    val pRegular2 = additionalFeatures.evaluate(words, offset, SentenceBreakerTag.Regular, vs.drop(features.features.size))

    if (pRegular1 + pRegular2 >= 0.5) return SentenceBreakerTag.Regular
    return SentenceBreakerTag.SentenceBreak
  }
}