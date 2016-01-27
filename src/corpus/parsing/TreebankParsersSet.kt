package corpus.parsing

import java.io.*
import java.util.*


class TreebankParsersSet {

    private val registeredParsers = ArrayList<TreebankParser>()

    private data class TreeBankInfo(val formatId: String,
                                    val relativePath: String)

    fun registerParser(parser: TreebankParser) {
        registeredParsers.add(parser)
    }

    fun parseDirectory(path: File, vararg handlers: TreebankParserHandler) {
        val fileList = path.listFiles { f -> f.extension.equals("treebank", true) }

        val datas = fileList.mapNotNull { tryParseInfo(it) }.filterNotNull()

        for (data in datas) {
            val parser = registeredParsers.first { it.ParserId.equals(data.formatId, true) }
            val treebankPath = File(path, data.relativePath)
            parser.parse(treebankPath, MultiHandler(handlers))
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

    fun parse(path: File, vararg handlers: TreebankParserHandler) {
        var info = tryParseInfo(path)
        if (info == null) return

        val parser = registeredParsers.first { it.ParserId.equals(info.formatId, true) }
        val treebankPath = File(path.parentFile, info.relativePath)
        parser.parse(treebankPath, MultiHandler(handlers))
    }
}

