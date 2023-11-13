package com.seanshubin.code.structure.domain

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
        val entryPoints = createIdCaption("entry-points", "Entry Points")
        val graph = createIdCaption("graph", "Graph")
        val directCycles = createIdCaption("direct-cycles", "Direct Cycles")
        val groupCycles = createIdCaption("group-cycles", "Group Cycles")
        val lineageAncestorDescendant = createIdCaption("lineage-ad", "Ancestor depends on Descendant")
        val lineageDescendantAncestor = createIdCaption("lineage-da", "Descendant depends on Ancestor")
        val names = createIdCaption("names", "Names")
        val timing = createIdCaption("timing", "Timing")
        val groups = createIdCaption("group", "Groups")

    }
}
