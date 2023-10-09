package com.seanshubin.code.structure.beamformat

data class BeamFile(
    val fileSize: Int,
    val atoms: List<String>,
    val imports: List<Import>,
    val sections: List<Section>
)
