package io.swagger.swaggerhub.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;

public class SwaggerHubDownloadTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    private String outputFile;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void testSwaggerHubDownloadTask() throws IOException {
        outputFile = testProjectDir.getRoot().toString() + "/testAPI.json";
        String downloadTask = "swaggerhubDownload";

        String buildFileContent = "plugins { id 'io.swagger.swaggerhub' }\n" +
                downloadTask + " {\n" +
                "    specName \'PetStoreAPI\'\n" +
                "    owner \'jsfrench\'\n" +
                "    version \'1.0.0\'\n" +
                "    outputFile \'" + outputFile + "\'\n" +
                "}";

        writeFile(buildFile, buildFileContent);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(downloadTask, "--stacktrace")
                .build();

        assertEquals(SUCCESS, result.task(":" + downloadTask).getOutcome());
        assertTrue(new File(outputFile).exists());
    }

    @Test
    public void testSwaggerHubDomainDownloadTask() throws IOException {
        outputFile = testProjectDir.getRoot().toString() + "/testDomain.json";
        String downloadTask = "swaggerhubDownload";

        String buildFileContent = "plugins { id 'io.swagger.swaggerhub' }\n" +
                downloadTask + " {\n" +
                "    specName \'testDomain\'\n" +
                "    owner \'jsfrench\'\n" +
                "    version \'1.0.0\'\n" +
                "    specType \'domain\'\n" +
                "    outputFile \'" + outputFile + "\'\n" +
                "}";

        writeFile(buildFile, buildFileContent);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(downloadTask, "--stacktrace")
                .build();

        assertEquals(SUCCESS, result.task(":" + downloadTask).getOutcome());
        assertTrue(new File(outputFile).exists());
    }

    private void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}
