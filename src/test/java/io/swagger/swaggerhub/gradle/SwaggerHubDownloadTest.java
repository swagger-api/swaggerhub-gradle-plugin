package io.swagger.swaggerhub.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;

public class SwaggerHubDownloadTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void testSwaggerHubDownloadTask() throws IOException {
        Path outputFile = Paths.get(testProjectDir.getRoot().toString(), "testAPI.json");
        String filePath = outputFile.toString().replace("\\", "/");

        String downloadTask = "swaggerhubDownload";
        String buildFileContent = "plugins { id 'io.swagger.swaggerhub' }\n" +
                downloadTask + " {\n" +
                "    api 'PetStoreAPI'\n" +
                "    owner 'jsfrench'\n" +
                "    version '1.0.0'\n" +
                "    outputFile '" + filePath + "'\n" +
                "}";

        Files.write(buildFile.toPath(), buildFileContent.getBytes());

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(downloadTask, "--stacktrace")
                .build();

        assertEquals(SUCCESS, result.task(":" + downloadTask).getOutcome());
        assertTrue(Files.exists(outputFile));
    }
}
