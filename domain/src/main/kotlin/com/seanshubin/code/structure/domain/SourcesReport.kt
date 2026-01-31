package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.nameparser.NameDetail

class SourcesReport : NameDetailReport() {
    override val reportName: String = "sources"
    override val category: ReportCategory = ReportCategory.BROWSE
    override val page: Page = Page.sources
    override fun lookupSourceFiles(observations: Observations): List<NameDetail> =
        observations.sources
}
