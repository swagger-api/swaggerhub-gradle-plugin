package io.github.jsfrench.swaggerhub;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;


public class SwaggerHubUploadTest {
    private WireMockServer wireMockServer;

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    private String outputFile;

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void testSwaggerHubUploadTask() throws IOException, URISyntaxException {
        copyInputFile("TestAPI.json", testProjectDir.getRoot());
        outputFile = testProjectDir.getRoot().toString() + "/testAPI.json";
        String uploadTask = "swaggerhubUpload";
        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .isPrivate(false)
                .

        String buildFileContent = "plugins { id 'io.github.jsfrench.swaggerhub.SwaggerHubPlugin' }\n" +
                uploadTask + " {\n" +
                "    host \'localhost\'\n" +
                "    port \'8089\'\n" +
                "    protocol \'http\'\n" +
                "    api \'TestAPI\'\n" +
                "    owner \'testUser\'\n" +
                "    version \'1.1.0\'\n" +
                "    inputFile \'" + testProjectDir.getRoot().toString() + "/TestAPI.json\'\n" +
                "    token \'dUmMyTokEn.1234abc\'\n" +
                "}";

        writeFile(buildFile, buildFileContent);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(uploadTask, "--stacktrace")
                .build();

        assertEquals(SUCCESS, result.task(":" + uploadTask).getOutcome());
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

    private void copyInputFile(String originalFile, File outputDir) throws IOException, URISyntaxException {
        String outputFilePath = outputDir.getPath() + "/" + originalFile;
        Path copied = Paths.get(outputFilePath);
        Path originalFilePath = Paths.get(ClassLoader.getSystemClassLoader().getResource(originalFile).toURI());
        Files.copy(originalFilePath, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    private UrlPathPattern setupServerMocking(SwaggerHubRequest request, String host, String port, String protocol, String token) {
        String api = request.getApi();
        String owner = request.getOwner();
        String version = request.getVersion();
        String format = request.getFormat();
        String isPrivate = Boolean.toString(request.isPrivate());


        startMockServer(Integer.parseInt(port));

        UrlPathPattern url = urlPathEqualTo("/apis/" + owner + "/" + api);

        stubFor(post(url)
                .withQueryParam("version", equalTo(version))
                .withQueryParam("isPrivate", equalTo(isPrivate != null ? isPrivate : "false"))
                .withHeader("Content-Type", equalToIgnoreCase(
                        String.format("application/%s; charset=UTF-8", format != null ? format : "json")))
                .withHeader("Authorization", equalTo(token))
                .withHeader("User-Agent", equalTo("swaggerhub-maven-plugin"))
                .willReturn(created()));

        return url;
    }

    private void startMockServer(int port) {
        wireMockServer = new WireMockServer(options().port(port));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }
}
