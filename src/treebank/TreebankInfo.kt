package treebank

import java.io.File
import java.util.*

class TreebankInfo(public val infoFile: File,
                   public val formatId: String,
                   public val treebankPath: File,
                   public val metadata: HashMap<String, String>) {

}