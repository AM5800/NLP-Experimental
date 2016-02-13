package ml.sentenceBreaking.LogLinear

import ml.sentenceBreaking.SentenceBreakerTag

class TrainingTableEntry private constructor(val correctTag: SentenceBreakerTag, private val values: List<BooleanArray>) {
  val M: Int = values.first().size

  fun answerAt(k: Int): Boolean {
    return values[SentenceBreakerTag.values().indexOf(correctTag)][k]
  }

  fun answerAt(k: Int, tag: SentenceBreakerTag): Boolean {
    return values[SentenceBreakerTag.values().indexOf(tag)][k]
  }

  fun featureResultsAtTag(tag: SentenceBreakerTag): BooleanArray {
    return values[SentenceBreakerTag.values().indexOf(tag)]
  }

  companion object {
    fun create(correctTag: SentenceBreakerTag, features: List<(SentenceBreakerTag) -> Boolean>): TrainingTableEntry {
      val values = SentenceBreakerTag.values().map { tag ->
        features.map { feature ->
          feature(tag)
        }.toBooleanArray()
      }
      return TrainingTableEntry(correctTag, values)
    }

    fun create(entry: TrainingTableEntry, newM: Int, fillFunction: (SentenceBreakerTag, Int) -> Boolean): TrainingTableEntry {

      val values = SentenceBreakerTag.values().mapIndexed { tagIndex, tag ->
        (0..newM - 1).map { k ->
          if (k < entry.M) entry.values[tagIndex][k]
          else fillFunction(tag, k)
        }.toBooleanArray()
      }

      return TrainingTableEntry(entry.correctTag, values)
    }
  }
}