package corpus.parsing

import corpus.RelativeRange
import java.io.*
import java.util.*


class TreebankParsersSet {

    private val registeredParsers = ArrayList<TreebankParser>()

    private data class TreeBankInfo(val formatId: String,
                                    val relativePath: String)

    fun registerParser(parser: TreebankParser) {
        registeredParsers.add(parser)
    }

    fun parse(path: String, range: RelativeRange, vararg handlers: TreebankParserHandler) {
        val file = File(path)
        val fileList = file.listFiles { f -> f.extension.equals("treebank", true) }

        val datas = fileList.mapNotNull { tryParseInfo(it) }.filterNotNull()

        for (data in datas) {
            val parser = registeredParsers.first { it.ParserId.equals(data.formatId, true) }
            val treebankPath = File(path, data.relativePath)
            parser.parse(treebankPath.absolutePath, range, MultiHandler(handlers))
        }
    }

    private fun tryParseInfo(path: File): TreeBankInfo? {

        try {
            return BufferedReader(FileReader(path)).use { reader ->
                val id = reader.readLine()!!.trim()
                val relativePath = reader.readLine()!!.trim()

                return TreeBankInfo(id, relativePath)
            }

        } catch (e: Exception) {
            // TODO need logging!
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            println(sw.toString())
            return null
        }
    }
}

