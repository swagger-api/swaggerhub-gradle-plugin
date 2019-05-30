package io.swagger.swaggerhub;

import io.swagger.swaggerhub.tasks.DownloadTask;
import io.swagger.swaggerhub.tasks.DomainDownloadTask;
import io.swagger.swaggerhub.tasks.UploadTask;
import io.swagger.swaggerhub.tasks.DomainUploadTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SwaggerHubPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getTasks().create("swaggerhubDownload", DownloadTask.class);
        project.getTasks().create("swaggerhubDomainDownload", DomainDownloadTask.class);
        project.getTasks().create("swaggerhubUpload", UploadTask.class);
        project.getTasks().create("swaggerhubDomainUpload", DomainUploadTask.class);
    }
}