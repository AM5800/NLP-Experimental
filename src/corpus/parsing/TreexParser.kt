package corpus.parsing

import corpus.ShufflingHandler
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import util.changeExtension
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import javax.xml.parsers.SAXParserFactory


class TreexParser : TreebankParser {
    override val ParserId: String = "Treex"

    private class SaxHandler(private val handler: TreebankParserHandler,
                             private val shuffleInfo: HashMap<String, Int>?,
                             private val treebankName: String) : DefaultHandler() {

        private var lmLevel = 0

        private var form = ""
        private var lemma = ""

        private var currentDataTag = ""

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "LM") ++lmLevel

            if (qName == "LM" && lmLevel == 1) {
                val id = makeId(attributes)
                handler.beginSentence(id, shuffleInfo?.get(id), shuffleInfo?.size)
            }

            if (lmLevel >= 1) {
                if (qName == "form" || qName == "lemma" || qName == "tag") currentDataTag = qName
            }
        }

        private fun makeId(attributes: Attributes?): String {
            return treebankName + attributes?.getValue("id")!!
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if (qName == "LM") --lmLevel
            currentDataTag = ""
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            if (currentDataTag.isEmpty()) return
            val value = String(ch!!, start, length).trim()

            if (currentDataTag == "form") form = value
            else if (currentDataTag == "lemma") lemma = value
            else if (currentDataTag == "tag") {
                handler.word(form, lemma, value)
            }
        }
    }

    override fun parse(path: File, handler: TreebankParserHandler) {
        val treebankFiles = expandPath(path)

        val shuffleFile = path.changeExtension("shuffled")
        if (!shuffleFile.exists()) createShuffleFile(shuffleFile, treebankFiles)

        val shuffleInfo = loadShuffleInfo(shuffleFile)

        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()

        handler.beginTreebank(path)
        for (treebank in treebankFiles) {
            handler.beginTreebank(treebank)
            parser.parse(treebank, SaxHandler(handler, shuffleInfo, treebank.nameWithoutExtension))
            handler.endTreebank()
        }
        handler.endTreebank()
    }

    private fun expandPath(path: File): List<File> {
        return path.listFiles { f -> f.extension.equals("treex", true) }.toList()
    }

    private fun createShuffleFile(shuffleFile: File, treebanks: List<File>) {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        val handler = ShufflingHandler()

        for (treebank in treebanks) {
            parser.parse(treebank, SaxHandler(handler, null, treebank.nameWithoutExtension))
        }

        handler.save(shuffleFile)
    }

    private fun loadShuffleInfo(shuffleFile: File): HashMap<String, Int> {
        val result = LinkedHashMap<String, Int>()
        BufferedReader(FileReader(shuffleFile)).use { reader ->
            val n = Integer.parseInt(reader.readLine())
            repeat(n, { i ->
                val line = reader.readLine()?.trim()
                if (line != null) result.put(line, i)
            })
        }
        return result
    }
}