package io.github.jsfrench.swaggerhub;


import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Downloads API definition from SwaggerHub
 */
//@Mojo(name = "download")
public class SwaggerHubDownload implements Plugin<Project> {
    private String owner;
    private String api;
    private String version;
    private String format;
    private String host;
    private int port;
    private String protocol;
    private String token;
    private String outputFile;

    @Override
    public void apply(Project project) {
        SwaggerHubClient swaggerHubClient = new SwaggerHubClient(host, port, protocol, token);
//        getLog().info("Downloading from " + host
//                + ": api-" + api
//                + ", owner-" + owner
//                + ", version-" + version
//                + ", format-" + format
//                + ", outputFile-" + outputFile);

        SwaggerHubRequest swaggerHubRequest = new SwaggerHubRequest.Builder(api, owner, version)
                .format(format)
                .build();

        try {
            String swaggerJson = swaggerHubClient.getDefinition(swaggerHubRequest);
            File file = new File(outputFile);

            final File parentFile = file.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            Files.write(Paths.get(outputFile), swaggerJson.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
//            throw new Exception("Failed to download API definition", e);
            e.printStackTrace();
        }
    }
}