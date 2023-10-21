package com.seanshubin.code.structure.json

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

object JsonMappers {
    private val kotlinModule = KotlinModule.Builder().build()

    //    val prettyPrinter: PrettyPrinter = DefaultPrettyPrinter()
    val pretty: ObjectMapper
    val compact: ObjectMapper
    val parser: ObjectMapper

    init {
        val prettyPrinter = DefaultPrettyPrinter()
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)

        pretty = ObjectMapper().registerModule(kotlinModule).registerModule(JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDefaultPrettyPrinter(prettyPrinter)
        compact = ObjectMapper().registerModule(kotlinModule).registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        parser = compact

    }

    inline fun <reified T> parse(json: String): T = parser.readValue(json)
    fun String.normalizeJson(): String {
        val asObject = parser.readValue<Any>(this)
        val asNormalized = pretty.writeValueAsString(asObject)
        return asNormalized
    }
}
