package treebank.parsing

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import treebank.TreebankInfo
import javax.xml.parsers.SAXParserFactory


public class NegraParser : TreebankParser {
    override val ParserId: String = "NEGRA4"

    private class SaxHandler(private val handler: TreebankParserHandler) : DefaultHandler() {

        private var inSentence = false

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "s") {
                val id = attributes?.getValue("id")!!.trim()
                handler.beginSentence(id)
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

    override fun parse(info: TreebankInfo, handler: TreebankParserHandler) {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        handler.beginTreebank(info)
        parser.parse(info.treebankPath, SaxHandler(handler))
        handler.endTreebank()
    }
}


