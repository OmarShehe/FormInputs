package com.omarshehe.forminput.compose.ui.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FormInputImageStateTest {
    private fun state(values: List<FormInputImageState.ImageUploadValue?> = emptyList()) =
        FormInputImageState(
            id = "test",
            labelRes = null,
            placeholderRes = null,
            values = values,
        )

    // ── withPickedImage ─────────────────────────────────────────────────────

    @Test
    fun `withPickedImage appends to empty list when targetIndex is null`() {
        val result = state().withPickedImage(targetIndex = null, filePath = "content://a", fileName = "a.jpg", fileSize = 2048L)

        assertEquals(1, result.values.size)
        assertEquals("content://a", result.values[0]?.filePath)
        assertEquals("a.jpg", result.values[0]?.fileName)
    }

    @Test
    fun `withPickedImage appends to non-empty list without disturbing existing entries`() {
        val existing = FormInputImageState.ImageUploadValue(filePath = "content://existing")
        val result = state(listOf(existing)).withPickedImage(targetIndex = null, filePath = "content://b", fileName = "b.jpg", fileSize = null)

        assertEquals(2, result.values.size)
        assertEquals(existing, result.values[0])
        assertEquals("content://b", result.values[1]?.filePath)
    }

    @Test
    fun `withPickedImage replaces the value at a fixed index leaving other slots untouched`() {
        val slot0 = FormInputImageState.ImageUploadValue(filePath = "content://slot0")
        val original = state(listOf(slot0, null, null))
        val result = original.withPickedImage(targetIndex = 2, filePath = "content://slot2", fileName = "c.jpg", fileSize = 1024L)

        assertEquals(3, result.values.size)
        assertEquals(slot0, result.values[0])
        assertNull(result.values[1])
        assertEquals("content://slot2", result.values[2]?.filePath)
    }

    @Test
    fun `withPickedImage formats fileSize when provided`() {
        val result = state().withPickedImage(targetIndex = null, filePath = "content://x", fileName = "x.jpg", fileSize = 2048L)

        assertEquals("2.0 KB", result.values[0]?.fileSize)
    }

    @Test
    fun `withPickedImage leaves fileName and fileSize null when not provided`() {
        val result = state().withPickedImage(targetIndex = null, filePath = "content://x", fileName = null, fileSize = null)

        assertNull(result.values[0]?.fileName)
        assertNull(result.values[0]?.fileSize)
    }

    // ── withImageRemoved ─────────────────────────────────────────────────────

    @Test
    fun `withImageRemoved shrinks the list when unboundedAdd is true`() {
        val values = listOf(
            FormInputImageState.ImageUploadValue(filePath = "content://a"),
            FormInputImageState.ImageUploadValue(filePath = "content://b"),
            FormInputImageState.ImageUploadValue(filePath = "content://c"),
        )
        val result = state(values).withImageRemoved(index = 1, unboundedAdd = true)

        assertEquals(2, result.values.size)
        assertEquals("content://a", result.values[0]?.filePath)
        assertEquals("content://c", result.values[1]?.filePath)
    }

    @Test
    fun `withImageRemoved nulls the slot in place and preserves list size when unboundedAdd is false`() {
        val values = listOf(
            FormInputImageState.ImageUploadValue(filePath = "content://a"),
            FormInputImageState.ImageUploadValue(filePath = "content://b"),
            FormInputImageState.ImageUploadValue(filePath = "content://c"),
        )
        val result = state(values).withImageRemoved(index = 1, unboundedAdd = false)

        assertEquals(3, result.values.size)
        assertEquals("content://a", result.values[0]?.filePath)
        assertNull(result.values[1])
        assertEquals("content://c", result.values[2]?.filePath)
    }
}
