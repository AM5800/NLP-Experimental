package ml.sentenceBreaking.MEMM

import ml.sentenceBreaking.SentenceBreakerTag
import ml.sentenceBreaking.dot


class TrainedFeatureSet(private val features: LogLinearSentenceBreakerFeatureSet,
                        private val additionalFeatures: LogLinearSpecialFeatureSet,
                        private val vs: DoubleArray) {
  val currentWordOffset = features.currentWordOffset
  val requiredStackSize = features.requiredStackSize

  fun reachVerdict(words: Iterable<String>, offset: Int): SentenceBreakerTag {
    val featureVector1 = features.evaluate(words, offset, SentenceBreakerTag.Regular)
    val featureVector2 = additionalFeatures.evaluate(words, offset, SentenceBreakerTag.Regular)
    val pRegular = dot(vs, featureVector1.plus(featureVector2))

    if (pRegular >= 0.5) return SentenceBreakerTag.Regular
    return SentenceBreakerTag.SentenceBreak
  }
}