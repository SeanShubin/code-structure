package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.FilesContract

class EnvironmentImpl(override val files: FilesContract) : Environment
