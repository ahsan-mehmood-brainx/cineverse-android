package com.example.cineverse.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JavaTextUtilsTest {

    @Test
    fun capitalizeWords_null_returnsNull() {
        assertThat(JavaTextUtils.capitalizeWords(null)).isNull()
    }

    @Test
    fun capitalizeWords_empty_returnsEmpty() {
        assertThat(JavaTextUtils.capitalizeWords("")).isEmpty()
    }

    @Test
    fun capitalizeWords_singleWord_capitalizesFirstLetterOnly() {
        assertThat(JavaTextUtils.capitalizeWords("dune")).isEqualTo("Dune")
    }

    @Test
    fun capitalizeWords_multipleWords_capitalizesEachWord() {
        assertThat(JavaTextUtils.capitalizeWords("dune part two")).isEqualTo("Dune Part Two")
    }

    @Test
    fun capitalizeWords_alreadyMixedCase_normalizesCasing() {
        assertThat(JavaTextUtils.capitalizeWords("dUNE paRT twO")).isEqualTo("Dune Part Two")
    }

    @Test
    fun capitalizeWords_leadingSpaces_preservedAndNextLetterCapitalized() {
        assertThat(JavaTextUtils.capitalizeWords("  dune")).isEqualTo("  Dune")
    }

    @Test
    fun capitalizeWords_trailingSpaces_preserved() {
        assertThat(JavaTextUtils.capitalizeWords("dune  ")).isEqualTo("Dune  ")
    }

    @Test
    fun capitalizeWords_multipleSpacesBetweenWords_preserved() {
        assertThat(JavaTextUtils.capitalizeWords("dune   part")).isEqualTo("Dune   Part")
    }

    @Test
    fun capitalizeWords_tabsAndNewlines_treatedAsWhitespace() {
        assertThat(JavaTextUtils.capitalizeWords("dune\tpart\ntwo")).isEqualTo("Dune\tPart\nTwo")
    }

    @Test
    fun capitalizeWords_onlyWhitespace_isPreservedUnchanged() {
        assertThat(JavaTextUtils.capitalizeWords("   ")).isEqualTo("   ")
    }
}
