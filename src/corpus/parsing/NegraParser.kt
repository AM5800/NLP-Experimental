package corpus.parsing

import corpus.RelativeRange
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import util.changeExtension
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import javax.xml.parsers.SAXParserFactory


public class NegraParser : TreebankParser {
    override val ParserId: String = "NEGRA4"

    private class SaxHandler(private val handler: TreebankParserHandler,
                             expectedItems: List<String>) : DefaultHandler() {

        private val expectedSentenceIds = HashSet<String>(expectedItems)

        private var skipMode = false
        private var inSentence = false

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "s") {
                val id = attributes?.getValue("id")!!.trim()
                if (expectedSentenceIds.contains(id)) {
                    handler.startSentence(id)
                    skipMode = false
                } else skipMode = true
                inSentence = true
            }
            if (!skipMode && inSentence && qName == "t") {
                val word = attributes?.getValue("word")!!
                val lemma = attributes?.getValue("lemma")!!
                val pos = attributes?.getValue("pos")
                handler.word(word, lemma, pos)
            }

            super.startElement(uri, localName, qName, attributes)
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if (!skipMode && qName == "s") {
                handler.endSentence()
                inSentence = false
            }

            super.endElement(uri, localName, qName)
        }
    }

    override fun parse(path: String, range: RelativeRange, handler: TreebankParserHandler) {

        val shuffleFile = File(path).changeExtension("shuffled")
        if (!shuffleFile.exists()) createShuffleFile(shuffleFile)

        val expectedItems = loadShuffleInfo(shuffleFile, range)

        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        handler.beginTreebank(path)
        parser.parse(path, SaxHandler(handler, expectedItems))
        handler.endTreebank()
    }

    private fun createShuffleFile(shuffleFile: File) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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


