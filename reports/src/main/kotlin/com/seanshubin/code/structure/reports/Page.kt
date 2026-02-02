package com.seanshubin.code.structure.reports

interface Page {
    val caption: String
    val link: String
    val file: String
    val id: String

    companion object {
        fun createIdCaption(id: String, caption: String): Page = object : Page {
            override val caption: String get() = caption
            override val link: String get() = "$id.html"
            override val file: String get() = "$id.html"
            override val id: String get() = id
        }

        fun createCaptionLink(caption: String, link: String) = object : Page {
            override val caption: String get() = caption
            override val link: String get() = link
            override val file: String get() = throw UnsupportedOperationException()
            override val id: String get() = throw UnsupportedOperationException()
        }

        val tableOfContents = createIdCaption("index", "Table Of Contents")
        val sources = createIdCaption("sources", "Sources")
        val binaries = createIdCaption("binaries", "Binaries")
        val missingBinaries = createIdCaption("missing-binaries", "Sources without corresponding Binaries")
        val dependencies = createIdCaption("dependencies", "Dependencies")
        val entryPoints = createIdCaption("entry-points", "Entry Points")
        val graph = createIdCaption("graph", "Graph")
        val inDirectCycle = createIdCaption("in-direct-cycle", "In Direct Cycle")
        val inGroupCycle = createIdCaption("in-group-cycle", "In Group Cycle")
        val lineageAncestorDescendant = createIdCaption("lineage-ad", "Ancestor depends on Descendant")
        val lineageDescendantAncestor = createIdCaption("lineage-da", "Descendant depends on Ancestor")
        val codeUnits = createIdCaption("code-units", "Code Units")
        val timing = createIdCaption("timing", "Timing")
        val groups = createIdCaption("group", "Groups")
    }
}
