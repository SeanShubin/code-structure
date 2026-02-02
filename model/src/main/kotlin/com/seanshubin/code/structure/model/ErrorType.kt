package com.seanshubin.code.structure.model

enum class ErrorType(val caption: String) {
    IN_DIRECT_CYCLE("In Direct Cycle"),
    IN_GROUP_CYCLE("In Group Cycle"),
    ANCESTOR_DEPENDS_ON_DESCENDANT("Ancestor Depends on Descendant"),
    DESCENDANT_DEPENDS_ON_ANCESTOR("Descendant Depends On Ancestor");
}
