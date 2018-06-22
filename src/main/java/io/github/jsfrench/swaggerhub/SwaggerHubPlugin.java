package io.github.jsfrench.swaggerhub;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SwaggerHubPlugin implements Plugin<Project> {
    public void apply(Project project) {
//        project.getExtensions().create("swaggerhub");
        project.getTasks().create("swaggerhubDownload", DownloadTask.class);
    }
}