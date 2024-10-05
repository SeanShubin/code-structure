package com.seanshubin.code.structure.domain

enum class ErrorType(val caption: String) {
    DIRECT_CYCLE("Direct Cycle"),
    GROUP_CYCLE("Group Cycle"),
    ANCESTOR_DEPENDS_ON_DESCENDANT("Ancestor Depends on Descendant"),
    DESCENDANT_DEPENDS_ON_ANCESTOR("Descendant Depends On Ancestor");
}
