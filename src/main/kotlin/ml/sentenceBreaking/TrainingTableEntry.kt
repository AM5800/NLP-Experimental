package ml.sentenceBreaking

class TrainingTableEntry private constructor(val correctTag: SentenceBreakerTag, features: List<(SentenceBreakerTag) -> Boolean>) {
  private val values = SentenceBreakerTag.values().map { tag ->
    features.map { feature ->
      feature(tag)
    }.toBooleanArray()
  }

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

    }

    fun create(entry: TrainingTableEntry, newM: Int, fillFunction: (SentenceBreakerTag, Int) -> Boolean): TrainingTableEntry {

    }
  }
}