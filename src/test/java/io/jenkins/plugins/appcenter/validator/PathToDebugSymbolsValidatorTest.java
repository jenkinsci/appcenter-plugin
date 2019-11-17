package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class PathToDebugSymbolsValidatorTest {

    private PathToDebugSymbolsValidator validator;

    @Before
    public void setUp() {
        validator = new PathToDebugSymbolsValidator();
    }

    @Test
    public void should_ReturnFalse_When_PathIsAbsolute_Windows() {
        // Given
        final String value = "C:\\\\windows\\path\\to\\symbols.zip";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_PathIsAbsolute_UnixLike() {
        // Given
        final String value = "/unix-like/path/to/mappings.txt";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnTrue_When_PathIsRelative_Windows() {
        // Given
        final String value = "path\\to\\symbols.zip";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_PathIsRelative_UnixLike() {
        // Given
        final String value = "path/to/mappings.txt";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_PathContainsEnvVars() {
        // Given
        final String value = "path/to/mapping-${BUILD_NUMBER}.txt";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnWarning_When_PathStartsWithEnvVars() {
        // Given
        final String value = "${SOME_ENV_VAR}.apk";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }
}