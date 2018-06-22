package io.github.jsfrench.swaggerhub;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.testng.annotations.Test;
import org.gradle.testfixtures.ProjectBuilder;

import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertNotNull;
import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertTrue;

public class InitialTestClass {

    @Test
    public void uploadTest() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.swagger.swaggerhub.plugin");

        assertTrue(project.getPluginManager().hasPlugin("io.swagger.swaggerhub.plugin"));

        assertNotNull(project.getTasks().getByName("hello"));
    }
}
