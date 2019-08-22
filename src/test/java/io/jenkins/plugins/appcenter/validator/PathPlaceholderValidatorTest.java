package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class PathPlaceholderValidatorTest {
    private PathPlaceholderValidator validator;

    @Before
    public void setUp() {
        validator = new PathPlaceholderValidator();
    }

    @Test
    public void should_ReturnTrue_When_PathDoesNotStartsWithEnvVariable() {
        // Given
        final String value = "relative/path/to/${SOME_ENV_VAR}.ipa";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnFalse_When_PathStartsWithEnvVariable() {
        // Given
        final String value = "${SOME_ENV_VAR}.ipa";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }
}
