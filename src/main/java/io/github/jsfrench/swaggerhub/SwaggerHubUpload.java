package io.github.jsfrench.swaggerhub;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Uploads API definition to SwaggerHub
 */
public class SwaggerHubUpload implements Plugin<Project> {
//    @Parameter(property = "upload.owner", required = true)
    private String owner;
//    @Parameter(property = "upload.api", required = true)
    private String api;
//    @Parameter(property = "upload.version")
    private String version;
//    @Parameter(property = "upload.host", defaultValue = "api.swaggerhub.com")
    private String host;
//    @Parameter(property = "upload.port", defaultValue = "443")
    private int port;
//    @Parameter(property = "upload.protocol", defaultValue = "https")
    private String protocol;
//    @Parameter(property = "upload.format", defaultValue = "json")
    private String format;
//    @Parameter(property = "upload.token")
    private String token;
//    @Parameter(property = "upload.inputFile", required = true)
    private String inputFile;
//    @Parameter(property = "upload.isPrivate", defaultValue = "false")
    private Boolean isPrivate;

    private SwaggerHubClient swaggerHubClient;

    @Override
    public void apply(Project project) {

        project.task("hello")
                .doLast(task -> System.out.println("HELLO FROM GRADLE"));

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
//            getLog().Exception("Failed to upload API definition", e);
        }
    }
}