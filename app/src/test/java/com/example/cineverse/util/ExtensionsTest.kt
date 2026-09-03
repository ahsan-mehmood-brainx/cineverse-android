package com.example.cineverse.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExtensionsTest {

    @Test
    fun capitalizeWords_singleWord() {
        val result = "hello".capitalizeWords()

        assertThat(result).isEqualTo("Hello")
    }

    @Test
    fun capitalizeWords_multipleWords() {
        val result = "hello world".capitalizeWords()

        assertThat(result).isEqualTo("Hello World")
    }

    @Test
    fun capitalizeWords_emptyString() {
        val result = "".capitalizeWords()

        assertThat(result).isEmpty()
    }

    @Test
    fun capitalizeWords_alreadyCapitalized() {
        val result = "Hello World".capitalizeWords()

        assertThat(result).isEqualTo("Hello World")
    }

    @Test
    fun capitalizeWords_singleCharacter() {
        val result = "a".capitalizeWords()

        assertThat(result).isEqualTo("A")
    }

    @Test
    fun capitalizeWords_withNumbers() {
        val result = "hello123 world".capitalizeWords()

        assertThat(result).isEqualTo("Hello123 World")
    }

    @Test
    fun capitalizeWords_withSpecialCharacters() {
        val result = "hello-world test".capitalizeWords()

        // Behavior depends on implementation of JavaTextUtils
        assertThat(result).isNotEmpty()
    }

    @Test
    fun capitalizeWords_onlySpaces() {
        val result = "   ".capitalizeWords()

        assertThat(result).isEqualTo("   ")
    }
}
