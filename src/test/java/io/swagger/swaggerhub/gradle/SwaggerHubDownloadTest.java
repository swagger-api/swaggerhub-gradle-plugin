package io.swagger.swaggerhub.gradle;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
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

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.TestCase.assertTrue;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;

public class SwaggerHubDownloadTest {
    private static final String DOWNLOAD_TASK = "swaggerhubDownload";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    private String filePath;
    private Path outputFile;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
        outputFile = Paths.get(testProjectDir.getRoot().toString(), "testAPI.json");
        filePath = outputFile.toString().replace("\\", "/");
    }

    @Test
    public void testSwaggerHubDownloadTask() throws IOException {
        String buildFileContent = "plugins { id 'io.swagger.swaggerhub' }\n" +
                                  DOWNLOAD_TASK + " {\n" +
                                  "    api 'PetStoreAPI'\n" +
                                  "    owner 'jsfrench'\n" +
                                  "    version '1.0.0'\n" +
                                  "    outputFile '" + filePath + "'\n" +
                                  "}";

        Files.write(buildFile.toPath(), buildFileContent.getBytes());

        BuildResult result = executeTask();

        assertEquals(SUCCESS, result.task(":" + DOWNLOAD_TASK).getOutcome());
        assertTrue(Files.exists(outputFile));
    }

    @Test
    public void downloadsUnresolvedByDefault() throws IOException {
        stubFor(WireMock.get(anyUrl()).willReturn(WireMock.ok()));

        String buildFileContent = "plugins { id 'io.swagger.swaggerhub' }\n" +
                                  DOWNLOAD_TASK + " {\n" +
                                  "    protocol 'http'\n" +
                                  "    host 'localhost'\n" +
                                  "    port " + wireMockRule.port() + "\n" +
                                  "    api 'PetStoreAPI'\n" +
                                  "    owner 'jsfrench'\n" +
                                  "    version '1.0.0'\n" +
                                  "    outputFile '" + filePath + "'\n" +
                                  "}\n";

        Files.write(buildFile.toPath(), buildFileContent.getBytes());

        executeTask();

        WireMock.verify(getRequestedFor(urlEqualTo("/apis/jsfrench/PetStoreAPI/1.0.0?resolved=false")));
    }

    @Test
    public void supportsResolvedFlag() throws IOException {
        stubFor(WireMock.get(anyUrl()).willReturn(WireMock.ok()));

        String buildFileContent = "plugins { id 'io.swagger.swaggerhub' }\n" +
                                  DOWNLOAD_TASK + " {\n" +
                                  "    protocol 'http'\n" +
                                  "    host 'localhost'\n" +
                                  "    port " + wireMockRule.port() + "\n" +
                                  "    api 'PetStoreAPI'\n" +
                                  "    owner 'jsfrench'\n" +
                                  "    version '1.0.0'\n" +
                                  "    outputFile '" + filePath + "'\n" +
                                  "    resolved true\n" +
                                  "}\n";

        Files.write(buildFile.toPath(), buildFileContent.getBytes());

        executeTask();

        WireMock.verify(getRequestedFor(urlEqualTo("/apis/jsfrench/PetStoreAPI/1.0.0?resolved=true")));
    }

    private BuildResult executeTask() {
        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(DOWNLOAD_TASK, "--stacktrace")
                .build();
    }
}
