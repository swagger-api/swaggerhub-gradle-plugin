package io.swagger.swaggerhub.gradle;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import io.swagger.swaggerhub.client.SwaggerHubRequest;
import org.apache.commons.lang3.StringUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
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
    private Path inputFile;
    private String testInputAPI = "TestAPI.json";
    private static String UPLOAD_TASK = "swaggerhubUpload";
    private final String api = "TestAPI";
    private final String owner = "testUser";
    private final String version = "1.1.0";
    private final String host = "localhost";
    private final String port = "8089";
    private final String protocol = "http";
    private final String token = "dUmMyTokEn.1234abc";
    private String swagger;

    @Before
    public void setup() throws IOException, URISyntaxException {
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @After
    public void tearDown() throws Exception {
        wireMockServer.stop();
    }

    @Test
    public void testUpload() throws IOException, URISyntaxException {
        copyInputFile(testInputAPI, testProjectDir.getRoot());
        inputFile = getInputFilePath(testInputAPI);
        swagger = new String(Files.readAllBytes(inputFile), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .swagger(swagger)
                .build();

        setupServerMocking(request, host, port, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    @Test
    public void testUploadPrivate() throws IOException, URISyntaxException {
        copyInputFile(testInputAPI, testProjectDir.getRoot());
        inputFile = getInputFilePath(testInputAPI);
        swagger = new String(Files.readAllBytes(inputFile), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .isPrivate(true)
                .swagger(swagger)
                .build();

        setupServerMocking(request, host, port, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    @Test
    public void testUploadYaml() throws Exception {
        testInputAPI = "TestAPI.yaml";
        copyInputFile(testInputAPI, testProjectDir.getRoot());
        inputFile = getInputFilePath(testInputAPI);
        swagger = new String(Files.readAllBytes(inputFile), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .format("yaml")
                .swagger(swagger)
                .build();

        setupServerMocking(request, host, port, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    private TaskOutcome runBuild(SwaggerHubRequest request) throws IOException {
        createBuildFile(request);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(UPLOAD_TASK, "--stacktrace")
                .build();

        return result.task(":" + UPLOAD_TASK).getOutcome();
    }

    private String createBuildFile(SwaggerHubRequest request) throws IOException {
        // Convert Windows path to use slashes for Gradle config
        String filePath = inputFile.toString().replace("\\", "/");

        String buildFileContent =  "plugins { id 'io.swagger.swaggerhub' }\n" +
                UPLOAD_TASK + " {\n" +
                "    host '" + host + "'\n" +
                "    port " + port + "\n" +
                "    protocol '" + protocol + "'\n" +
                "    api '" + request.getApi() + "'\n" +
                "    owner '" + request.getOwner() + "'\n" +
                "    version '" + request.getVersion() + "'\n" +
                getFormatSetting(request.getFormat()) +
                getIsPrivateSetting(request.isPrivate()) +
                "    inputFile '" + filePath + "'\n" +
                "    token '" + token + "'\n" +
                "}";

        Files.write(buildFile.toPath(), buildFileContent.getBytes());
        return buildFileContent;
    }

    private String getIsPrivateSetting(Boolean isPrivate) {
        // false is default, so only include if set to true
        return isPrivate ? String.format("   isPrivate %s\n", isPrivate) : "";
    }

    private String getFormatSetting(String format) {
        // json is default, so only include if set to yaml
        return StringUtils.isNotBlank(format) && format.equals("yaml") ? String.format("   format '%s'\n", format) : "";
    }

    private void copyInputFile(String originalFile, File outputDir) throws IOException, URISyntaxException {
        Path copied = Paths.get(outputDir.getPath(), originalFile);
        Path originalFilePath = Paths.get(ClassLoader.getSystemClassLoader().getResource(originalFile).toURI());
        Files.copy(originalFilePath, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    private Path getInputFilePath(String filename) {
        return Paths.get(testProjectDir.getRoot().toString(), filename);
    }

    private UrlPathPattern setupServerMocking(SwaggerHubRequest request, String host, String port, String token) {
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
                .withHeader("User-Agent", equalTo("swaggerhub-gradle-plugin"))
                .withRequestBody(equalTo(request.getSwagger()))
                .willReturn(created()));

        return url;
    }

    private void startMockServer(int port) {
        wireMockServer = new WireMockServer(options().port(port));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }
}
