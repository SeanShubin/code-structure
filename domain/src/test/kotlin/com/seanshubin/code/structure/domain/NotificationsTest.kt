package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.domain.TestUtil.exactlyOne
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationsTest {
    @Test
    fun timeTaken() {
        // given
        val emitLine = EmitLineStub()
        val notifications = NotificationsImpl(emitLine)
        val timeTaken = Duration.ofMillis(12345)
        val expected = "time taken: 12 seconds 345 milliseconds"

        // when
        notifications.timeTakenEvent("time taken", timeTaken)

        // then
        val actual = emitLine.lines.exactlyOne()
        assertEquals(expected, actual)
    }

    class EmitLineStub : (String) -> Unit {
        val lines = mutableListOf<String>()
        override fun invoke(line: String) {
            lines.add(line)
        }
    }
}