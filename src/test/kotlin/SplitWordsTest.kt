import ml.sentenceBreaking.splitWords
import org.junit.Assert
import org.junit.Test

class SplitWordsTest {
  @Test
  fun test() {
    val text = "Was ist  das? Das ist ein ``Tisch''..."
    val words = splitWords(text).map { text.substring(it.start, it.end) }
    Assert.assertEquals(11, words.size)

    Assert.assertEquals(words[2], "das")
    Assert.assertEquals(words[3], "?")
    Assert.assertEquals(words[7], "``")
    Assert.assertEquals(words[9], "''")
    Assert.assertEquals(words[10], "...")
  }

  @Test
  fun testLeadingPeriod() {
    val text = "Das ist eine .NET Anwendung"
    val words = splitWords(text).map { text.substring(it.start, it.end) }
    Assert.assertEquals(6, words.size)
    Assert.assertEquals(words[3], ".")
    Assert.assertEquals(words[4], "NET")
  }
}