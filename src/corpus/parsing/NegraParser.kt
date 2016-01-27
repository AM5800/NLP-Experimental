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


public class NegraParser : TreebankParser {
    override val ParserId: String = "NEGRA4"

    private class SaxHandler(private val handler: TreebankParserHandler,
                             private val shuffleInfo: HashMap<String, Int>?) : DefaultHandler() {

        private var inSentence = false

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "s") {
                val id = attributes?.getValue("id")!!.trim()
                handler.beginSentence(id, shuffleInfo?.get(id), shuffleInfo?.size)
                inSentence = true
            }
            if (inSentence && qName == "t") {
                val word = attributes?.getValue("word")!!
                val lemma = attributes?.getValue("lemma")!!
                val pos = attributes?.getValue("pos")
                handler.word(word, lemma, pos)
            }

            super.startElement(uri, localName, qName, attributes)
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if (qName == "s") {
                handler.endSentence()
                inSentence = false
            }

            super.endElement(uri, localName, qName)
        }
    }

    override fun parse(path: File, handler: TreebankParserHandler) {

        val shuffleFile = path.changeExtension("shuffled")
        if (!shuffleFile.exists()) createShuffleFile(shuffleFile, path)

        val shuffleInfo = loadShuffleInfo(shuffleFile)

        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        handler.beginTreebank(path)
        parser.parse(path, SaxHandler(handler, shuffleInfo))
        handler.endTreebank()
    }

    private fun createShuffleFile(shuffleFile: File, treebankPath: File) {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        val handler = ShufflingHandler()
        parser.parse(treebankPath, SaxHandler(handler, null))
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


