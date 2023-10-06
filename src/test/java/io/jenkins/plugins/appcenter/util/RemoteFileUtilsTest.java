package io.jenkins.plugins.appcenter.util;

import hudson.FilePath;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;
import static io.jenkins.plugins.appcenter.util.TestFileUtil.TEST_FILE_PATH;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RemoteFileUtilsTest {

    @Mock
    FilePath filePath;

    private RemoteFileUtils remoteFileUtils;

    @Before
    public void setUp() {
        given(filePath.child(anyString())).willReturn(filePath);
        remoteFileUtils = new RemoteFileUtils(filePath);
    }

    @Test
    public void should_ReturnFile_When_FileExists() {
        // Given
        final File expected = new File(TEST_FILE_PATH);
        given(filePath.getRemote()).willReturn(TEST_FILE_PATH);

        // When
        final File remoteFile = remoteFileUtils.getRemoteFile(TEST_FILE_PATH);

        // Then
        assertThat(remoteFile).isEqualTo(expected);
    }

    @Test
    public void should_ReturnFileName_When_FileExists() {
        // Given
        given(filePath.getRemote()).willReturn(TEST_FILE_PATH);

        // When
        final String fileName = remoteFileUtils.getFileName(TEST_FILE_PATH);

        // Then
        assertThat(fileName).isEqualTo("xiola.apk");
    }

    @Test
    public void should_ReturnFileSize_When_FileExists() {
        // Given
        given(filePath.getRemote()).willReturn(TEST_FILE_PATH);

        // When
        final long fileSize = remoteFileUtils.getFileSize(TEST_FILE_PATH);

        // Then
        // Windows reports the file size differently so for the sake of simplicity we adjust our assertion here.
        assertThat(fileSize).isEqualTo(SystemUtils.IS_OS_UNIX ? 41 : 42);
    }
}