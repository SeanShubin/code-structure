package com.seanshubin.code.structure.fileselection

//
// This file was imported from: ../kotlin-reusable
// Module: file-selection
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

import java.nio.file.Path

interface FileChooser {
    fun choose(fileSelection: FileSelection): List<Path>
}
