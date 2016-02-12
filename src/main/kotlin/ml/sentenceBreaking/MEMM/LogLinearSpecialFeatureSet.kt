package ml.sentenceBreaking.MEMM

import ml.sentenceBreaking.SentenceBreakerTag
import ml.sentenceBreaking.TrainingTableEntry
import java.util.*

class LogLinearSpecialFeatureSet {
  private val truncationsOccurrences = mutableMapOf<String, ArrayList<Int>>()

  fun addTrainingSample(words: List<String>, offset: Int, tag: SentenceBreakerTag, index: Int) {
    if (tag != SentenceBreakerTag.Regular) return

    val word = getWord(words, offset)

    val wordWithoutDot = word.trimEnd('.')
    if (word.endsWith('.') && wordWithoutDot.length >= 2) {
      addOccurrence(wordWithoutDot, index)
    } else if (word.endsWith('.')) {
      val prevWord = getWord(words, offset - 1)
      if (prevWord.endsWith('.')) return

      addOccurrence(prevWord, index)
    }
  }

  private fun addOccurrence(word: String, index: Int) {
    val occurrences = truncationsOccurrences[word]
    if (occurrences == null) truncationsOccurrences[word] = arrayListOf(index)
    else occurrences.add(index)
  }

  private fun getWord(words: List<String>, offset: Int): String {
    return words[offset].trim('\'', '"', '`', ',', ';', '?', ')', '(', '!').toLowerCase()
  }

  fun merge(trainingData: List<TrainingTableEntry>): List<TrainingTableEntry> {
    val currentM = trainingData.first().M
    val targetM = truncationsOccurrences.size + currentM

    val indexToK = truncationsOccurrences.toList().mapIndexed { i, pair -> Pair(i, pair.second) }
            .flatMap { pair -> pair.second.map { Pair(pair.first, it) } }
            .toMap()

    val newTrainingData = trainingData.mapIndexed { i, entry ->
      val k = indexToK[i]
      TrainingTableEntry.create(entry, targetM, { tag, index ->
        k == index && tag == SentenceBreakerTag.Regular
      })
    }

    return newTrainingData
  }

  fun evaluate(words: Iterable<String>, offset: Int, tag: SentenceBreakerTag): BooleanArray {
    throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}