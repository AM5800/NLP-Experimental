package corpus.parsing

import corpus.RelativeRange


interface TreebankParser {
    val ParserId: String
    fun parse(path: String, range: RelativeRange, handler: TreebankParserHandler)
}