package com.seanshubin.code.structure.config

import com.seanshubin.code.structure.untyped.Untyped

interface Configuration {
    fun load(default:Any, path:List<String>): Untyped
}
