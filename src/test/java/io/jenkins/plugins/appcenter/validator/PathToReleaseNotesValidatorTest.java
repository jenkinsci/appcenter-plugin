package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class PathToReleaseNotesValidatorTest {

    private PathToReleaseNotesValidator validator;

    @Before
    public void setUp() {
        validator = new PathToReleaseNotesValidator();
    }

    @Test
    public void should_ReturnFalse_When_PathIsAbsolute_Windows() {
        // Given
        final String value = "C:\\\\windows\\path\\to\\release-notes.md";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_PathIsAbsolute_UnixLike() {
        // Given
        final String value = "/unix-like/path/to/release-notes.md";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnTrue_When_PathIsRelative_Windows() {
        // Given
        final String value = "path\\to\\release-notes.md";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_PathIsRelative_UnixLike() {
        // Given
        final String value = "path/to/release-notes.md";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_PathContainsEnvVars() {
        // Given
        final String value = "path/to/release-notes-${BUILD_NUMBER}.md";

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