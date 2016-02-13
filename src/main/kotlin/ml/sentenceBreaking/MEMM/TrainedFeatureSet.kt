package ml.sentenceBreaking.MEMM

import ml.sentenceBreaking.SentenceBreakerTag


class TrainedFeatureSet(private val features: LogLinearSentenceBreakerFeatureSet,
                        private val additionalFeatures: LogLinearTruncationsFeatureSet,
                        private val vs: DoubleArray) {
  val currentWordOffset = features.currentWordOffset
  val requiredStackSize = features.requiredStackSize

  fun reachVerdict(words: List<String>, offset: Int): SentenceBreakerTag {
    val pRegular1 = features.evaluate(words, offset, SentenceBreakerTag.Regular, vs)
    val pBreak1 = features.evaluate(words, offset, SentenceBreakerTag.SentenceBreak, vs)
    val pRegular2 = additionalFeatures.evaluate(words, offset, SentenceBreakerTag.Regular, vs.drop(features.features.size))
    val pBreak2 = additionalFeatures.evaluate(words, offset, SentenceBreakerTag.SentenceBreak, vs.drop(features.features.size))

    val pRegular = pRegular1 + pRegular2
    val pBreak = pBreak1 + pBreak2

    if (pBreak > pRegular) return SentenceBreakerTag.SentenceBreak
    return SentenceBreakerTag.Regular
  }
}