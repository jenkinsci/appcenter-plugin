package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DistributionGroupValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = new DistributionGroupValidator();
    }

    @Test
    public void should_ReturnTrue_When_DistributionGroupContainsAlphanumericCharacters() {
        // Given
        final String value = "Collaborators";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_DistributionGroupContainsComplexCharacters() {
        // Given
        final String value = "Internal test group 5 漢字 !";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnFalse_When_DistributionGroupIsEmpty() {
        // Given
        final String value = "  ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }
}
