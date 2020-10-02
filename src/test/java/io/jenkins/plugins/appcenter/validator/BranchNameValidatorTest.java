package io.jenkins.plugins.appcenter.validator;


import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class BranchNameValidatorTest {

    private BranchNameValidator validator;

    @Before
    public void setUp() {
        validator = new BranchNameValidator();
    }

    @Test
    public void should_ReturnFalse_When_BranchNameContainsSingleSpace() {
        // Given
        final String value = " ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_BranchNameContainsMultipleSpace() {
        // Given
        final String value = "                        ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_BranchNameContainsSpaces() {
        // Given
        final String value = "origin/ invalid branch";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnTrue_When_BranchNameDoesNotContainSpaces() {
        // Given
        final String value = "origin/master";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }
}