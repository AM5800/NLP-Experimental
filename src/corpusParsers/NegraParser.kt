package corpusParsers

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory


public class NegraParser {
    private class SaxHandler(private val handler: CorpusParserHandler) : DefaultHandler() {
        private var inSentence = false
        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "s") {
                val id = attributes?.getValue("id")!!
                handler.startSentence(id)
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

    public fun parse(path : String, handler: CorpusParserHandler) {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        parser.parse(path, SaxHandler(handler))
    }
}