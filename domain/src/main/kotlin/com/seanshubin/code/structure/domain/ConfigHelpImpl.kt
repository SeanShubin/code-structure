package com.seanshubin.code.structure.domain
import com.seanshubin.code.structure.config.Configuration
import com.seanshubin.code.structure.relationparser.RelationParserRepository

class ConfigHelpImpl(
    private val config:Configuration,
    private val relationParserRepository: RelationParserRepository
    ):ConfigHelp {
    override fun generateConfigHelp(){
        val supportedBytecodeFormatNames = relationParserRepository.supportedBytecodeFormatNames()
        config.load(listOf("help", "bytecodeFormat" ), supportedBytecodeFormatNames )
    }
}