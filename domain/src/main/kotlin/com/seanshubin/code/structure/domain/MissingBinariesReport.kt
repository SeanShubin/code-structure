package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.nameparser.NameDetail

class MissingBinariesReport : NameDetailReport() {
    override val reportName: String = "missing-binaries"
    override val page: Page = Page.missingBinaries
    override fun lookupSourceFiles(observations: Observations): List<NameDetail> =
        observations.missingBinaries
}
