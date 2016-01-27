package treebank.parsing

import treebank.TreebankInfo
import java.util.*


class TreebankParsersSet {

    private val registeredParsers = ArrayList<TreebankParser>()

    fun registerParser(parser: TreebankParser) {
        registeredParsers.add(parser)
    }

    fun parse(info: TreebankInfo, vararg handlers: TreebankParserHandler) {
        val parser = registeredParsers.first { it.ParserId.equals(info.formatId, true) }
        parser.parse(info, MultiHandler(handlers))
    }
}

