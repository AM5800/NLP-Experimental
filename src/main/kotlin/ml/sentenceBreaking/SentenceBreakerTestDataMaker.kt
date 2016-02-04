package ml.sentenceBreaking

import treebank.parsing.ParsePartOfSpeech
import treebank.parsing.TreebankParserHandler
import java.util.*

class SentenceBreakerTestData(val text: String, val sentenceBreaks: List<Int>)


class SentenceBreakerTestDataMaker : TreebankParserHandler() {
  private val builder = StringBuilder()
  private val sentenceBreaks = arrayListOf<Int>()
  private val bracketsSymbols = setOf("(", ")", "<", ">", "[", "]", "{", "}")
  private val quotesStack = Stack<String>()

  private var previousWord = ""
  private var previousPos: ParsePartOfSpeech? = null
  override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
    var needSpace = true
    builder.length != 0

    if (builder.length == 0) needSpace = false
    else if (pos == ParsePartOfSpeech.Punctuation) needSpace = false
    else if (isQuot(previousPos)) needSpace = false
    else if (pos == ParsePartOfSpeech.QuotationStart) needSpace = true
    else if (pos == ParsePartOfSpeech.QuotationEnd) needSpace = false
    else if (pos == ParsePartOfSpeech.QuotationSymbol) {
      // Closing quote
      if (!quotesStack.empty() && quotesStack.last() == word) {
        needSpace = false
        quotesStack.pop()
      }
      // Opening quote
      else if (quotesStack.empty() || quotesStack.last() != word) {
        needSpace = true
        quotesStack.push(word)
      }
    } else if (bracketsSymbols.contains(word)) needSpace = true
    else if (bracketsSymbols.contains(previousWord)) needSpace = false

    if (needSpace) builder.append(' ')
    builder.append(word)

    previousWord = word
    previousPos = pos
  }

  private fun isQuot(pos: ParsePartOfSpeech?): Boolean {
    return pos == ParsePartOfSpeech.QuotationSymbol
            || pos == ParsePartOfSpeech.QuotationStart
            || pos == ParsePartOfSpeech.QuotationEnd
  }

  override fun endSentence() {
    if (!SentenceBreakerUtils.isSentenceEndChar(builder.last())) {
      builder.append('.')
      previousWord = "."
      previousPos = ParsePartOfSpeech.Punctuation
    }
    sentenceBreaks.add(builder.length - 1)
  }

  override fun endTreebank() {
    super.endTreebank()
  }

  fun getTestData(): SentenceBreakerTestData {
    return SentenceBreakerTestData(builder.toString(), sentenceBreaks)
  }
}