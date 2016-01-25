package corpus.parsing

import corpus.RelativeRange
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
                             expectedItems: List<String>? = null) : DefaultHandler() {

        private val expectedSentenceIds = if (expectedItems == null) null else HashSet<String>(expectedItems)

        private var skipMode = false
        private var lmLevel = 0

        private var form = ""
        private var lemma = ""

        private var currentDataTag = ""

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "LM") ++lmLevel

            if (qName == "LM" && lmLevel == 1) {
                val id = attributes?.getValue("id")!!
                if (expectedSentenceIds == null || expectedSentenceIds.contains(id)) {
                    skipMode = false
                    handler.beginSentence(id)
                } else skipMode = true
            }

            if (!skipMode && lmLevel >= 1) {
                if (qName == "form" || qName == "lemma" || qName == "tag") currentDataTag = qName
            }
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

    override fun parse(path: File, range: RelativeRange, handler: TreebankParserHandler) {

        val treebankFiles = expandPath(path)

        val shuffleFile = path.changeExtension("shuffled")
        if (!shuffleFile.exists()) createShuffleFile(shuffleFile, treebankFiles)

        val expectedItems = loadShuffleInfo(shuffleFile, range)

        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()

        for (treebank in treebankFiles) {
            handler.beginTreebank(treebank)
            parser.parse(treebank, SaxHandler(handler, expectedItems))
            handler.endTreebank()
        }
    }

    private fun expandPath(path: File): List<File> {
        return path.listFiles { f -> f.extension.equals("treex", true) }.toList()
    }

    private fun createShuffleFile(shuffleFile: File, treebanks: List<File>) {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        val handler = ShufflingHandler()

        for (treebank in treebanks) {
            parser.parse(treebank, SaxHandler(handler))
        }

        handler.save(shuffleFile)
    }

    private fun loadShuffleInfo(shuffleFile: File, range: RelativeRange): List<String> {
        val result = ArrayList<String>()
        BufferedReader(FileReader(shuffleFile)).use { reader ->
            val n = Integer.parseInt(reader.readLine())
            repeat(n, { i ->
                val line = reader.readLine()?.trim()
                if (line != null && range.accept(i, n)) result.add(line)
            })
        }
        return result
    }
}