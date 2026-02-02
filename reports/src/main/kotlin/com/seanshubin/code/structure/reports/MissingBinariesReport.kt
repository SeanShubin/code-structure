package com.seanshubin.code.structure.reports

import com.seanshubin.code.structure.model.Observations
import com.seanshubin.code.structure.nameparser.NameDetail

class MissingBinariesReport : NameDetailReport() {
    override val reportName: String = "missing-binaries"
    override val category: ReportCategory = ReportCategory.BROWSE
    override val page: Page = Page.missingBinaries
    override fun lookupSourceFiles(observations: Observations): List<NameDetail> =
        observations.missingBinaries
}
