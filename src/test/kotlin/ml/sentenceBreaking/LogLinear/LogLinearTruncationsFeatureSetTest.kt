package ml.sentenceBreaking.LogLinear

import ml.sentenceBreaking.SentenceBreakerTag
import org.junit.Assert.*
import org.junit.Test

class LogLinearTruncationsFeatureSetTest {
  @Test
  fun testSeparatePeriod() {
    val featureSet = LogLinearTruncationsFeatureSet()
    val words = listOf("Dr", ".", "Schmidt")
    featureSet.train(words, 0, SentenceBreakerTag.Regular, 0)
    featureSet.train(words, 1, SentenceBreakerTag.Regular, 1)
    featureSet.train(words, 2, SentenceBreakerTag.Regular, 2)

    val p = featureSet.evaluate(words, 1, SentenceBreakerTag.Regular, listOf(1.0))
    assertEquals(1.0, p, 0.001)
  }

  @Test
  fun testMerge() {
    val featureSet = LogLinearTruncationsFeatureSet()
    val entry = TrainingTableEntry.create(SentenceBreakerTag.Regular, listOf({ tag -> tag == SentenceBreakerTag.Regular }))

    val words = listOf("Dr", ".", "Schmidt")
    featureSet.train(words, 0, SentenceBreakerTag.Regular, 0)
    featureSet.train(words, 1, SentenceBreakerTag.Regular, 1)
    featureSet.train(words, 2, SentenceBreakerTag.Regular, 2)

    val merged = featureSet.merge(listOf(entry, entry, entry))

    assertEquals(3, merged.size)
    assertEquals(2, merged.first().M)

    assertTrue(merged[1].answerAt(1, SentenceBreakerTag.Regular))
    assertTrue(merged[1].answerAt(0, SentenceBreakerTag.Regular))
    assertFalse(merged[1].answerAt(1, SentenceBreakerTag.SentenceBreak))
    assertFalse(merged[1].answerAt(0, SentenceBreakerTag.SentenceBreak))
  }

  @Test
  fun testMergeEmpty() {
    val featureSet = LogLinearTruncationsFeatureSet()
    val entry = TrainingTableEntry.create(SentenceBreakerTag.Regular, emptyList())

    val words = listOf("Dr", ".", "Schmidt")
    featureSet.train(words, 0, SentenceBreakerTag.Regular, 0)
    featureSet.train(words, 1, SentenceBreakerTag.Regular, 1)
    featureSet.train(words, 2, SentenceBreakerTag.Regular, 2)

    val merged = featureSet.merge(listOf(entry, entry, entry))

    assertEquals(3, merged.size)
    assertEquals(1, merged.first().M)

    assertTrue(merged[1].answerAt(0, SentenceBreakerTag.Regular))
    assertFalse(merged[1].answerAt(0, SentenceBreakerTag.SentenceBreak))
  }
}