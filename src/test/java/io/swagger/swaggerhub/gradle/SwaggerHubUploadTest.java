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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    private String testInputAPI = "TestAPI.json";
    private String testInputDomain = "TestDomain.json";
    private static String UPLOAD_TASK = "swaggerhubUpload";
    private String api = "TestAPI";
    private String owner = "testUser";
    private String version = "1.1.0";
    private String host = "localhost";
    private String port = "8089";
    private String protocol = "http";
    private String token = "dUmMyTokEn.1234abc";
    private String inputFile;
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
        inputFile = String.format("%s/%s", testProjectDir.getRoot().toString(), testInputAPI);
        swagger = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .swagger(swagger)
                .specType("api")
                .build();

        setupServerMocking(request, host, port, protocol, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    @Test
    public void testUploadDomain() throws IOException, URISyntaxException {
        copyInputFile(testInputDomain, testProjectDir.getRoot());
        inputFile = String.format("%s/%s", testProjectDir.getRoot().toString(), testInputDomain);
        swagger = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .swagger(swagger)
                .specType("domain")
                .build();

        setupServerMocking(request, host, port, protocol, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    @Test
    public void testUploadPrivate() throws IOException, URISyntaxException {
        copyInputFile(testInputAPI, testProjectDir.getRoot());
        inputFile = String.format("%s/%s", testProjectDir.getRoot().toString(), testInputAPI);
        swagger = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .isPrivate(true)
                .swagger(swagger)
                .specType("api")
                .build();

        setupServerMocking(request, host, port, protocol, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    @Test
    public void testUploadYaml() throws Exception {
        testInputAPI = "TestAPI.yaml";
        copyInputFile(testInputAPI, testProjectDir.getRoot());
        inputFile = String.format("%s/%s", testProjectDir.getRoot().toString(), testInputAPI);
        swagger = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .format("yaml")
                .swagger(swagger)
                .specType("api")
                .build();

        setupServerMocking(request, host, port, protocol, token);
        assertEquals(SUCCESS, runBuild(request));
    }

    @Test
    public void testUploadYamlDomain() throws Exception {
        testInputDomain = "TestDomain.yaml";
        copyInputFile(testInputDomain, testProjectDir.getRoot());
        inputFile = String.format("%s/%s", testProjectDir.getRoot().toString(), testInputDomain);
        swagger = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

        SwaggerHubRequest request = new SwaggerHubRequest.Builder(api, owner, version)
                .format("yaml")
                .swagger(swagger)
                .specType("domain")
                .build();

        setupServerMocking(request, host, port, protocol, token);
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
        String buildFileContent =  "plugins { id 'io.swagger.swaggerhub' }\n" +
                UPLOAD_TASK + " {\n" +
                "    host \'" + host + "\'\n" +
                "    port " + port + "\n" +
                "    protocol \'" + protocol + "\'\n" +
                "    specName \'" + request.getSpecification() + "\'\n" +
                "    owner \'" + request.getOwner() + "\'\n" +
                "    version \'" + request.getVersion() + "\'\n" +
                "    specType \'" + request.getSpecType() + "\'\n" +
                getFormatSetting(request.getFormat()) +
                getIsPrivateSetting(request.isPrivate()) +
                "    inputFile \'" + inputFile + "\'\n" +
                "    token \'" + token + "\'\n" +
                "}";

        writeFile(buildFile, buildFileContent);

        return buildFileContent;
    }

    private String getIsPrivateSetting(Boolean isPrivate) {
        // false is default, so only include if set to true
        return isPrivate ? String.format("   isPrivate %s\n", Boolean.toString(isPrivate)) : "";
    }

    private String getFormatSetting(String format) {
        // json is default, so only include if set to yaml
        return StringUtils.isNotBlank(format) && format.equals("yaml") ? String.format("   format \'%s\'\n", format) : "";
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
        String api = request.getSpecification();
        String owner = request.getOwner();
        String version = request.getVersion();
        String format = request.getFormat();
        String isPrivate = Boolean.toString(request.isPrivate());
        String oasVersion = request.getOas();


        startMockServer(Integer.parseInt(port));

        UrlPathPattern url = urlPathEqualTo("/"+ request.getSpecType() +"s/" + owner + "/" + api);

        stubFor(post(url)
                .withQueryParam("version", equalTo(version))
                .withQueryParam("isPrivate", equalTo(isPrivate != null ? isPrivate : "false"))
                .withQueryParam("oas", equalTo(oasVersion != null ? oasVersion : "2.0"))
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
