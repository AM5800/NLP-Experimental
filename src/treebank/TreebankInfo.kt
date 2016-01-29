package treebank

import java.io.File

class TreebankInfo(public val infoFile: File,
                   public val formatId: String,
                   public val treebankPath: File,
                   public val metadata: Map<String, String>) {

}