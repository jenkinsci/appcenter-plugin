package io.jenkins.plugins.appcenter.validator;


import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class AppNameValidatorTest {

    private AppNameValidator validator;

    @Before
    public void setUp() {
        validator = new AppNameValidator();
    }

    @Test
    public void should_ReturnFalse_When_AppNameContainsSingleSpace() {
        // Given
        final String value = " ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_AppNameContainsMultipleSpace() {
        // Given
        final String value = "                        ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_AppNameContainsSpaces() {
        // Given
        final String value = " my super duper app ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnTrue_When_AppNameDoesNotContainSpaces() {
        // Given
        final String value = "my-super-duper-app";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }
}