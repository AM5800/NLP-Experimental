package treebank.parsing

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import treebank.TreebankInfo
import java.io.File
import java.util.*
import javax.xml.parsers.SAXParserFactory


class TreexParser : TreebankParser {
    override val ParserId: String = "Treex"

    private class SaxHandler(private val handler: TreebankParserHandler,
                             private val treebankName: String) : DefaultHandler() {

        private class TreexWord(val word: String, val lemma: String, val pos: ParsePartOfSpeech?, val ord: Int)

        private var lmLevel = 0

        private var form = ""
        private var lemma = ""
        private var tag = ""

        private val words = ArrayList<TreexWord>()

        private var currentDataTag = ""

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            if (qName == "LM") {
                ++lmLevel
                if (lmLevel == 1) {
                    val id = makeId(attributes)
                    words.clear()
                    handler.beginSentence(id)
                }
            }

            if (lmLevel >= 1) {
                if (qName == "form" || qName == "lemma" || qName == "tag" || qName == "ord") currentDataTag = qName
            }
        }

        private fun makeId(attributes: Attributes?): String {
            return treebankName + attributes?.getValue("id")!!
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if (qName == "LM") {
                --lmLevel
                if (lmLevel == 0) {
                    words.sortBy { it.ord }
                    words.forEach { handler.word(it.word, it.lemma, it.pos) }
                    handler.endSentence()
                }
            }

            currentDataTag = ""
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            if (currentDataTag.isEmpty()) return
            val value = String(ch!!, start, length).trim()

            if (currentDataTag == "form") form = value
            else if (currentDataTag == "lemma") lemma = value
            else if (currentDataTag == "tag") tag = value
            else if (currentDataTag == "ord" && value != "0") {
                words.add(TreexWord(form, lemma, mapPos(tag), Integer.parseInt(value)))
            }
        }

        private fun mapPos(pos: String): ParsePartOfSpeech? {
            return when (pos) {
                "ADV" -> ParsePartOfSpeech.Adverb
                "ADJ" -> ParsePartOfSpeech.Adjective
                "PUNCT" -> ParsePartOfSpeech.Punctuation
                "DET" -> ParsePartOfSpeech.Determiner
                "NOUN" -> ParsePartOfSpeech.Noun
                "PRON" -> ParsePartOfSpeech.Pronoun
                "VERB" -> ParsePartOfSpeech.Verb
                "CONJ", "SCONJ" -> ParsePartOfSpeech.Conjunction
                "PART" -> ParsePartOfSpeech.Particle
                "NUM" -> ParsePartOfSpeech.Number
                "ADP" -> ParsePartOfSpeech.Preposition
                "PROPN" -> ParsePartOfSpeech.ProperName
                "AUX" -> ParsePartOfSpeech.Verb
                "X" -> null
                else -> throw Exception("Unknown pos tag: " + pos)
            }
        }
    }

    override fun parse(info: TreebankInfo, handler: TreebankParserHandler) {
        val treebankFiles = expandPath(info.treebankPath)

        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()

        handler.beginTreebank(info)
        for (treebank in treebankFiles) {
            parser.parse(treebank, SaxHandler(handler, treebank.nameWithoutExtension))
        }
        handler.endTreebank()
    }

    private fun expandPath(path: File): List<File> {
        return path.listFiles { f -> f.extension.equals("treex", true) }.toList()
    }
}