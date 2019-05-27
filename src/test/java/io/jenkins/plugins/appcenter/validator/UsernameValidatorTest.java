package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UsernameValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = new UsernameValidator();
    }

    @Test
    public void should_ReturnTrue_When_ApiTokenIsLowerCaseLettersOnly() {
        // Given
        final String value = "johndoe";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_ApiTokenIsUpperCaseLettersOnly() {
        // Given
        final String value = "JOHNDOE";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_ApiTokenIsNumbersOnly() {
        // Given
        final String value = "1234567890";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_ApiTokenIsMixOfNumbersAndLetters() {
        // Given
        final String value = "j0hNDo3";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_ApiTokenIsMixOfNumbersAndLettersAndDashesAndDotsAndUnderscores() {
        // Given
        final String value = "j0hNDo3-._";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnFalse_When_ApiTokenIsBlank() {
        // Given
        final String value = "          ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_ApiTokenContainsExclamation() {
        // Given
        final String value = "johndoe!";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }
}