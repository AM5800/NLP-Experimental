import ml.sentenceBreaking.SentenceBreakerTestDataMaker
import org.junit.Assert
import org.junit.Test
import treebank.parsing.ParsePartOfSpeech

class SentenceBreakerTestDataMakerTest {
    @Test
    fun testWordConcat() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        Assert.assertEquals("Der Tisch", maker.getTestData().text)
    }

    @Test
    fun testEndSentence() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.endSentence()
        Assert.assertEquals("Der Tisch.", maker.getTestData().text)
    }

    @Test
    fun testEndSentencePeriod() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.word(".", "", ParsePartOfSpeech.Punctuation)
        maker.endSentence()
        Assert.assertEquals("Der Tisch.", maker.getTestData().text)
    }

    @Test
    fun testEndSentenceExclamation() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.word("!", "", ParsePartOfSpeech.Punctuation)
        maker.endSentence()
        Assert.assertEquals("Der Tisch!", maker.getTestData().text)
    }

    @Test
    fun testEndSentenceQuestion() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.word("?", "", ParsePartOfSpeech.Punctuation)
        maker.endSentence()
        Assert.assertEquals("Der Tisch?", maker.getTestData().text)
    }

    @Test
    fun testEndSentenceEllipsis() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.word("...", "", ParsePartOfSpeech.Punctuation)
        maker.endSentence()
        Assert.assertEquals("Der Tisch...", maker.getTestData().text)
    }

    @Test
    fun testEndSentenceComma() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word(",", "", ParsePartOfSpeech.Punctuation)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.endSentence()
        Assert.assertEquals("Der, Tisch.", maker.getTestData().text)
    }

    @Test
    fun testTigerQuotes() {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word("''", "", ParsePartOfSpeech.QuotationEnd)
        maker.endSentence()
        maker.word("``", "", ParsePartOfSpeech.QuotationStart)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.endSentence()
        Assert.assertEquals("Der''. ``Tisch.", maker.getTestData().text)
    }

    private fun testQuotes(startSymbols: String,
                           endSymbols: String = startSymbols,
                           posStart: ParsePartOfSpeech = ParsePartOfSpeech.QuotationSymbol,
                           posEnd: ParsePartOfSpeech = ParsePartOfSpeech.QuotationSymbol) {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word(startSymbols, "", posStart)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.word(endSymbols, "", posEnd)
        maker.endSentence()
        val expected = "Der " + startSymbols + "Tisch" + endSymbols + "."
        Assert.assertEquals(expected, maker.getTestData().text)
    }

    private fun testBrackets(startSymbols: String, endSymbols: String) {
        val maker = SentenceBreakerTestDataMaker()
        maker.word("Der", "", ParsePartOfSpeech.Determiner)
        maker.word(startSymbols, "", ParsePartOfSpeech.Punctuation)
        maker.word("Tisch", "", ParsePartOfSpeech.Noun)
        maker.word(endSymbols, "", ParsePartOfSpeech.Punctuation)
        maker.endSentence()
        val expected = "Der" + startSymbols + "Tisch" + endSymbols + "."
        Assert.assertEquals(expected, maker.getTestData().text)
    }

    @Test
    fun testNormalQuote() = testQuotes("\"", "\"")

    @Test
    fun testSingleQuote() = testQuotes("'", "'")

    @Test
    fun testTigerStyleQuote() = testQuotes("``", "''", ParsePartOfSpeech.QuotationStart, ParsePartOfSpeech.QuotationEnd)

    @Test
    fun testBrackets() = testBrackets("(", ")")
}