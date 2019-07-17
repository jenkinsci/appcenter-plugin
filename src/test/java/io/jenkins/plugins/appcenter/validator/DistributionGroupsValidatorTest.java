package io.jenkins.plugins.appcenter.validator;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DistributionGroupsValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = new DistributionGroupsValidator();
    }

    @Test
    public void should_ReturnTrue_When_DistributionGroupsContainsAlphanumericCharacters() {
        // Given
        final String value = "Collaborators";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_DistributionGroupsContainsMultipleGroups() {
        // Given
        final String value = "Collaborators, internal,beta-testers ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnTrue_When_DistributionGroupsContainsComplexCharacters() {
        // Given
        final String value = "Internal test group 5 漢字 !";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_ReturnFalse_When_DistributionsGroupIsEmpty() {
        // Given
        final String value = "  ";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_ReturnFalse_When_DistributionsGroupContainsOnlySeparators() {
        // Given
        final String value = " ,,, ,,  ,,,    ,,,,   , , ,";

        // When
        final boolean result = validator.isValid(value);

        // Then
        assertThat(result).isFalse();
    }
}