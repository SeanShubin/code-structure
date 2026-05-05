package com.seanshubin.code.structure.fileselection

//
// This file was imported from: ../kotlin-reusable
// Module: file-selection
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

interface FileSelectionNotify {
    fun onInclude(relativePath: String, pattern: String)
    fun onExclude(relativePath: String, pattern: String)
    fun onUnmatched(relativePath: String)
    fun onSkipDirectory(relativePath: String, pattern: String)
}
