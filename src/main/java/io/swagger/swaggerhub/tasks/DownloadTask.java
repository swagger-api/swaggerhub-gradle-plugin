package io.swagger.swaggerhub.tasks;


import io.swagger.swaggerhub.client.SwaggerHubClient;
import io.swagger.swaggerhub.client.SwaggerHubRequest;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Downloads API definition from SwaggerHub
 */
public class DownloadTask extends DefaultTask {
    private String owner;
    private String specName;
    private String version;
    private String token;
    private String outputFile;
    private String format = "json";
    private String host = "api.swaggerhub.com";
    private int port = 443;
    private String protocol = "https";
    private String specType = "api";
    private static Logger LOGGER = Logging.getLogger(DownloadTask.class);

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

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    @Input
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Input
    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
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
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
    public String getSpecType() { return specType; }

    public void setSpecType(String specType) { this.specType = specType; }

    @TaskAction
    public void downloadDefinition() throws GradleException {
        SwaggerHubClient swaggerHubClient = new SwaggerHubClient(host, port, protocol, token);

        LOGGER.info("Downloading from " + host
                + ": specName:" + specName
                + ", owner:" + owner
                + ", version:" + version
                + ", format:" + format
                + ", outputFile:" + outputFile);

        SwaggerHubRequest swaggerHubRequest = new SwaggerHubRequest.Builder(specName, owner, version)
                .format(format)
                .specType(specType)
                .build();

        try {
            String swaggerJson = swaggerHubClient.getDefinition(swaggerHubRequest);
            File file = new File(outputFile);

            setUpOutputDir(file);
            Files.write(Paths.get(outputFile), swaggerJson.getBytes(Charset.forName("UTF-8")));
        } catch (IOException | GradleException e) {
            throw new GradleException(e.getMessage(), e);
        }
    }

    private void setUpOutputDir(File file) {
        final File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
    }
}