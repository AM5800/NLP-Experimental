package treebank.parsing

import treebank.TreebankInfo

class MultiHandler(private val handlers: Array<out TreebankParserHandler>) : TreebankParserHandler() {
  override fun beginTreebank(info: TreebankInfo) {
    handlers.forEach { it.beginTreebank(info) }
  }

  override fun beginSentence(id: String) {
    handlers.forEach { it.beginSentence(id) }
  }

  override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
    handlers.forEach { it.word(word, lemma, pos) }
  }

  override fun endSentence() {
    handlers.forEach { it.endSentence() }
  }

  override fun endTreebank() {
    handlers.forEach { it.endTreebank() }
  }
}