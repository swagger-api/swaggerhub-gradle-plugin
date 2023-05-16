package io.swagger.swaggerhub.tasks;

import io.swagger.swaggerhub.client.SwaggerHubClient;
import io.swagger.swaggerhub.client.SwaggerHubRequest;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Uploads API definition to SwaggerHub
 */
public class UploadTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(UploadTask.class);
    private String owner;
    private String api;
    private String version;
    private String token;
    private String inputFile;
    private Boolean isPrivate = false;
    private String host = "api.swaggerhub.com";
    private Integer port = 443;
    private String protocol = "https";
    private String format = "json";
    private String oas = "2.0";

    private SwaggerHubClient swaggerHubClient;

    @Input
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Input
    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @Input
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Input
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @InputFile
    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    @Input
    @Optional
    public Boolean isPrivate() {
        return isPrivate;
    }

    public void isPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Input
    @Optional
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Input
    @Optional
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Input
    @Optional
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Input
    @Optional
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Input
    @Optional
    public String getOas() { return oas; }

    public void setOas(String oas) { this.oas = oas; }

    @TaskAction
    public void uploadDefinition() throws GradleException {

        swaggerHubClient = new SwaggerHubClient(host, port, protocol, token);

        LOGGER.info("Uploading to {}: api: {}, owner: {}, version: {}, inputFile: {}, format: {}, isPrivate: {}, oas: {} ",
                host, api, owner, version, inputFile, format, isPrivate, oas);

        try {
            String content = new String(Files.readAllBytes(Paths.get(inputFile)), StandardCharsets.UTF_8);

            SwaggerHubRequest swaggerHubRequest = new SwaggerHubRequest.Builder(api, owner, version)
                    .swagger(content)
                    .format(format)
                    .isPrivate(isPrivate)
                    .oas(oas)
                    .build();

            swaggerHubClient.saveDefinition(swaggerHubRequest);
        } catch (IOException | GradleException e) {
            throw new GradleException(e.getMessage(), e);
        }
    }
}
