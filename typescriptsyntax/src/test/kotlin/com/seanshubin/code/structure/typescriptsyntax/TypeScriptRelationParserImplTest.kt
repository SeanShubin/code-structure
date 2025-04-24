package com.seanshubin.code.structure.typescriptsyntax

import com.seanshubin.code.structure.contract.test.FilesContractUnsupportedOperation
import com.seanshubin.code.structure.relationparser.RelationDetail
import org.junit.Test
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals

class TypeScriptRelationParserImplTest {
    @Test
    fun rootLevel() {
        val tester = Tester(
            pathName = "./src/App.tsx",
            content = """import MyComponent from "./components/a/MyComponent";"""
        )
        val path = Paths.get("./src/App.tsx")
        val names = listOf("components.a.MyComponent")
        val actual = tester.typeScriptRelationParser.parseDependencies(path, names)
        val expected = listOf(
            RelationDetail(
                path,
                "",
                "App",
                listOf("components.a.MyComponent")
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun secondLevel() {
        val tester = Tester(
            pathName = "./src/components/a/FirstComponent.tsx",
            content = """import { Routes } from "../b/SecondComponent";"""
        )
        val path = Paths.get("./src/components/a/FirstComponent.tsx")
        val names = listOf("components.b.SecondComponent")
        val actual = tester.typeScriptRelationParser.parseDependencies(path, names)
        val expected = listOf(
            RelationDetail(
                path,
                "",
                "components.a.FirstComponent",
                listOf("components.b.SecondComponent")
            )
        )
        assertEquals(expected, actual)
    }

    class Tester(
        val pathName: String,
        val content: String
    ) {
        val charset = StandardCharsets.UTF_8
        val files = FilesStub(mapOf(pathName to content))
        val typeScriptRelationParser = TypeScriptRelationParserImpl(files, charset)
    }

    class FilesStub(val contentAtPath: Map<String, String>) : FilesContractUnsupportedOperation {
        override fun readString(path: Path, cs: Charset): String {
            return contentAtPath.getValue(path.toString())
        }
    }
}
