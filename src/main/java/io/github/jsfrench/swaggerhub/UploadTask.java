package io.github.jsfrench.swaggerhub;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Uploads API definition to SwaggerHub
 */
public class UploadTask extends DefaultTask {
    private String owner;
    private String api;
    private String version;
    private String token;
    private String inputFile;
    private Boolean isPrivate = false;
    private String host = "api.swaggerhub.com";
    private int port = 443;
    private String protocol = "https";
    private String format = "json";

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

    @Input
    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    @Input @Optional
    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }
    @Optional
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Optional
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Optional
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Optional
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @TaskAction
    public void uploadDefinition() throws Exception {

        System.out.println("EXECUTING SWAGGERHUB UPLOAD PLUGIN");
        System.out.println(String.format("%s, %s, %s, %s, %s, %s, %s", api, owner, version, inputFile, format, host, token));

        swaggerHubClient = new SwaggerHubClient(host, port, protocol, token);

//        getLog().info("Uploading to " + host
//                + ": api: " + api
//                + ", owner: " + owner
//                + ", version: " + version
//                + ", inputFile: " + inputFile
//                + ", format: " + format
//                + ", isPrivate: " + isPrivate);

        try {
            String content = new String(Files.readAllBytes(Paths.get(inputFile)), Charset.forName("UTF-8"));

            SwaggerHubRequest swaggerHubRequest = new SwaggerHubRequest.Builder(api, owner, version)
                    .swagger(content)
                    .format(format)
                    .isPrivate(isPrivate)
                    .build();

            swaggerHubClient.saveDefinition(swaggerHubRequest);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
//            getLog().Exception("Failed to upload API definition", e);
        }
    }
}