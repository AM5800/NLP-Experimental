package ml.sentenceBreaking.LogLinear

import ml.sentenceBreaking.SentenceBreakerTag
import java.util.*

class LogLinearTruncationsFeatureSet {
  private val truncationsOccurrences = mutableMapOf<String, ArrayList<Int>>()
  private val truncationToK = mutableMapOf<String, Int>()

  fun addTrainingSample(words: List<String>, offset: Int, tag: SentenceBreakerTag, index: Int) {
    if (tag != SentenceBreakerTag.Regular) return

    val word = getWord(words, offset)

    val wordWithoutDot = word.trimEnd('.')
    if (word.endsWith('.') && wordWithoutDot.length >= 2) {
      addOccurrence(wordWithoutDot, index)
    } else if (word.endsWith('.')) {
      val prevWord = getWord(words, offset - 1)
      if (prevWord.endsWith('.') || prevWord.length < 2) return

      addOccurrence(prevWord, index)
    }
  }

  private fun addOccurrence(word: String, index: Int) {
    val occurrences = truncationsOccurrences[word]
    if (occurrences == null) truncationsOccurrences[word] = arrayListOf(index)
    else occurrences.add(index)

    if (!truncationToK.contains(word)) truncationToK[word] = truncationToK.size
  }

  private fun getWord(words: List<String>, offset: Int): String {
    return words[offset].trim('\'', '"', '`', ',', ';', '?', ')', '(', '!', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9').toLowerCase()
  }

  fun merge(trainingData: List<TrainingTableEntry>): List<TrainingTableEntry> {
    val currentM = trainingData.first().M
    val targetM = truncationsOccurrences.size + currentM

    val indexToK = truncationsOccurrences.toList()
            .flatMap { pair ->
              val k = truncationToK[pair.first]
              if (k == null) emptyList()
              else pair.second.map { Pair(it, k) }
            }.toMap()

    val newTrainingData = trainingData.mapIndexed { i, entry ->
      val k = indexToK[i]
      TrainingTableEntry.create(entry, targetM, { tag, index ->
        k == index - currentM && tag == SentenceBreakerTag.Regular
      })
    }

    return newTrainingData
  }

  private fun evaluate(word: String, vs: List<Double>): Double {
    val k = truncationToK[word] ?: return 0.0

    return vs[k]
  }

  fun evaluate(words: List<String>, offset: Int, tag: SentenceBreakerTag, vs: List<Double>): Double {
    if (tag == SentenceBreakerTag.SentenceBreak) return 0.0

    val word = getWord(words, offset)
    val wordWithoutDot = word.trimEnd('.')
    if (word.endsWith('.') && wordWithoutDot.length >= 2) {
      return evaluate(wordWithoutDot, vs)
    } else if (word.endsWith('.')) {
      val prevWord = getWord(words, offset - 1)
      if (prevWord.endsWith('.')) return 0.0

      return evaluate(prevWord, vs)
    }

    return 0.0
  }

}