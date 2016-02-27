package corpus.parsing

import corpus.CorpusInfo


interface CorpusParser {
  val ParserId: String
  fun parse(info: CorpusInfo, handler: CorpusParserHandler)
}