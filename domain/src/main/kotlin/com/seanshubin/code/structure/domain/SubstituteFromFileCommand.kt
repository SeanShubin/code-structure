package com.seanshubin.code.structure.domain

import java.nio.charset.StandardCharsets
import java.nio.file.Path

data class SubstituteFromFileCommand(
    override val source: String,
    val templateFile: Path,
    val searchText: String,
    val replaceFile: Path,
    val destinationFile: Path
) : Command {
    override val category: String get() = "SubstituteFromFileCommand"
    override val id: String
        get() = "SubstituteFromFileCommand(" +
                "templateFile = $templateFile," +
                " searchText = '$searchText'," +
                " replaceFile = $replaceFile," +
                " destinationFile = $destinationFile)"

    override fun execute(environment: Environment) {
        val templateText = environment.files.readString(templateFile, StandardCharsets.UTF_8)
        val replaceText = environment.files.readString(replaceFile, StandardCharsets.UTF_8)
        val destinationText = templateText.replace(searchText, replaceText)
        environment.files.writeString(destinationFile, destinationText)
    }
}
