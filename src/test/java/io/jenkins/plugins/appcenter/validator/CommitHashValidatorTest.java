package io.jenkins.plugins.appcenter.validator;


import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class CommitHashValidatorTest {

    private CommitHashValidator validator;

    @Before
    public void setUp() {
        validator = new CommitHashValidator();
    }

    @Test
    public void should_ReturnFalse_When_CommitHashContainsSingleSpace() {
        // Given
        final String value = " ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_CommitHashContainsMultipleSpace() {
        // Given
        final String value = "                        ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_CommitHashContainsSpaces() {
        // Given
        final String value = "ffa8d2d2ad619d13 20f94d1865d39647e9e8e278";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnTrue_When_CommitHashDoesNotContainSpaces() {
        // Given
        final String value = "ffa8d2d2ad619d1320f94d1865d39647e9e8e278";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }
}