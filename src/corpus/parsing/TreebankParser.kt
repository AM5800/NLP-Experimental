package corpus.parsing

import corpus.RelativeRange
import java.io.File


interface TreebankParser {
    val ParserId: String
    fun parse(path: File, range: RelativeRange, handler: TreebankParserHandler)
}