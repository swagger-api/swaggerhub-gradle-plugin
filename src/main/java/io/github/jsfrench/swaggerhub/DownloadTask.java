package io.github.jsfrench.swaggerhub;


import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Downloads API definition from SwaggerHub
 */
public class DownloadTask extends DefaultTask {
    private String owner;
    private String api;
    private String version;
    private String format;
    private String host;
    private int port;
    private String protocol;
    private String token;
    private String outputFile;

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
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Input
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Input
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Input
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Input
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Input
    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    @TaskAction
    public void downloadDefinition() {

        System.out.println("EXECUTING SWAGGERHUB DOWNLOAD PLUGIN");
//        SwaggerHubClient swaggerHubClient = new SwaggerHubClient(host, port, protocol, token);
//
//  getLog().info("Downloading from " + host
//                + ": api-" + api
//                + ", owner-" + owner
//                + ", version-" + version
//                + ", format-" + format
//                + ", outputFile-" + outputFile);

//        SwaggerHubRequest swaggerHubRequest = new SwaggerHubRequest.Builder(api, owner, version)
//                .format(format)
//                .build();
//
//        try {
//            String swaggerJson = swaggerHubClient.getDefinition(swaggerHubRequest);
//            File file = new File(outputFile);
//
//            final File parentFile = file.getParentFile();
//            if (parentFile != null) {
//                parentFile.mkdirs();
//            }
//            Files.write(Paths.get(outputFile), swaggerJson.getBytes(Charset.forName("UTF-8")));
//        } catch (Exception e) {
////            throw new Exception("Failed to download API definition", e);
//            e.printStackTrace();
//        }
    }
}