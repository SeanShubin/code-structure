package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.nameparser.NameDetail

class SourcesReport : NameDetailReport() {
    override val name: String = "sources"
    override val page: Page = Page.sources
    override fun lookupSourceFiles(observations: Observations): List<NameDetail> =
        observations.sources
}
