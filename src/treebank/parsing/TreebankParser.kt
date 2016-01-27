package treebank.parsing

import treebank.TreebankInfo


interface TreebankParser {
    val ParserId: String
    fun parse(info: TreebankInfo, handler: TreebankParserHandler)
}