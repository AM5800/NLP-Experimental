import corpus.CorpusInfo
import corpus.parsing.CorpusParserHandler
import corpus.parsing.ParsePartOfSpeech
import corpus.parsing.TreexParser
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.*

class TreexParserTest {
  class TestWord(val word: String,
                 val lemma: String,
                 val pos: ParsePartOfSpeech?)

  class TestHandler(private val index: Int) : CorpusParserHandler() {
    val words = ArrayList<TestWord>()

    private var currentSentence = 0;

    override fun endSentence() {
      ++currentSentence;
    }

    override fun word(word: String, lemma: String, pos: ParsePartOfSpeech?) {
      if (currentSentence != index) return
      words.add(TestWord(word, lemma, pos))
    }
  }

  private val info = CorpusInfo(File("testData\\treex"), "treex", File("testData\\treex"), emptyMap<String, String>())

  @Test
  fun test0() {
    val handler = TestHandler(0)
    TreexParser().parse(info, handler)
    val sentence = handler.words.map { it.word }.joinToString(" ")

    Assert.assertEquals("Etwas duster , aber so sieht man auch nicht alles .", sentence)
  }

  @Test
  fun test55() {
    val handler = TestHandler(55)
    TreexParser().parse(info, handler)

    val pos = handler.words.map { it.pos }
    Assert.assertEquals(ParsePartOfSpeech.Adjective, pos[0])
    Assert.assertEquals(ParsePartOfSpeech.Noun, pos[1])
    Assert.assertEquals(ParsePartOfSpeech.Conjunction, pos[2])
    Assert.assertEquals(ParsePartOfSpeech.Adjective, pos[3])
    Assert.assertEquals(ParsePartOfSpeech.Noun, pos[4])
    Assert.assertEquals(ParsePartOfSpeech.Preposition, pos[5])
    Assert.assertEquals(ParsePartOfSpeech.Determiner, pos[6])
    Assert.assertEquals(ParsePartOfSpeech.Noun, pos[7])
    Assert.assertEquals(ParsePartOfSpeech.Punctuation, pos[8])
    Assert.assertEquals(ParsePartOfSpeech.Punctuation, pos[9])
  }
}


