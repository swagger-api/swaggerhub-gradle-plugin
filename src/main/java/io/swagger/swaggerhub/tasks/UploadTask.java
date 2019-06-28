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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Uploads API definition to SwaggerHub
 */
public class UploadTask extends DefaultTask {
    private String owner;
    private String specName;
    private String domain;
    private String version;
    private String token;
    private String inputFile;
    private Boolean isPrivate = false;
    private String host = "api.swaggerhub.com";
    private int port = 443;
    private String protocol = "https";
    private String format = "json";
    private String oas = "2.0";
    private String specType = "api";
    private static Logger LOGGER = Logging.getLogger(DownloadTask.class);

    private SwaggerHubClient swaggerHubClient;

    @Input
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Input
    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String api) {
        this.specName = api;
    }

    @Input
    @Optional
    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }

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
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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

    @Input
    @Optional
    public String getSpecType() { return specType; }

    public void setSpecType(String specType) { this.specType = specType; }

    @TaskAction
    public void uploadDefinition() throws GradleException {

        swaggerHubClient = new SwaggerHubClient(host, port, protocol, token);

        LOGGER.info("Uploading to " + host
                + ": specName: " + specName
                + ", owner: " + owner
                + ", version: " + version
                + ", inputFile: " + inputFile
                + ", format: " + format
                + ", isPrivate: " + isPrivate
                + ", oas: " + oas
                + ", specType: " + specType);

        try {
            String content = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

            SwaggerHubRequest swaggerHubRequest = new SwaggerHubRequest.Builder(specName, owner, version)
                    .swagger(content)
                    .format(format)
                    .isPrivate(isPrivate)
                    .oas(oas)
                    .specType(specType)
                    .build();

            swaggerHubClient.saveDefinition(swaggerHubRequest);
        } catch (IOException | GradleException e) {
            throw new GradleException(e.getMessage(), e);
        }
    }
    
}