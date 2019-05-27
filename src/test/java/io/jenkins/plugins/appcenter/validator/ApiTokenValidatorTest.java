package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ApiTokenValidatorTest {

    private ApiTokenValidator validator;

    @Before
    public void setUp() {
        validator = new ApiTokenValidator();
    }

    @Test
    public void should_ReturnFalse_When_ApiTokenContainsSingleSpace() {
        // Given
        final String value = " ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_ApiTokenContainsMultipleSpace() {
        // Given
        final String value = "                        ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_ApiTokenContainsSpaces() {
        // Given
        final String value = " a12b3 4cd 5f89 98av3 ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnTrue_When_ApiTokenDoesNotContainSpaces() {
        // Given
        final String value = "a12b3-4cd-5f89-98av3";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }
}