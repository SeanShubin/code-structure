package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract

interface Environment {
    val files: FilesContract
}