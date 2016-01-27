package corpus.parsing

import java.io.File


interface TreebankParser {
    val ParserId: String
    fun parse(path: File, handler: TreebankParserHandler)
}