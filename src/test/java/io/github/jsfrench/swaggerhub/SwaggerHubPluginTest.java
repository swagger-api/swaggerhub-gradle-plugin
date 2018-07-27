package io.github.jsfrench.swaggerhub;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SwaggerHubPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    private String outputFile;

//    @Before
//    public void setup() {
//        buildFile = testProjectDir.newFile("testBuild.gradle");
//        outputFile = testProjectDir.getRoot().toString() + '/testAPI.json'
//
//
//        // Prepare build.gradle
//        build_gradle = testProjectDir.newFile('build.gradle')
//        build_gradle << 'plugins { id "io.github.jsfrench.swaggerhub.SwaggerHubPlugin" }\n' +
//                'swaggerhubDownload {\n' +
//                '    api \'PetStoreAPII\'\n' +
//                '    owner \'jsfrench\'\n' +
//                '    version \'1.0.0\'\n' +
//                '    outputFile \'' + outputFile + '\'\n' +
//                '}'
//    }

    @Before
    public void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void testSwaggerHubDownloadTask() throws IOException {
        outputFile = testProjectDir.getRoot().toString() + "/testAPI.json";
        String downloadTask = "swaggerhubDownload";

        String buildFileContent = "plugins { id 'io.github.jsfrench.swaggerhub.SwaggerHubPlugin' }\n" +
                "swaggerhubDownload {\n" +
                "    api \'PetStoreAPII\'\n" +
                "    owner \'jsfrench\'\n" +
                "    version \'1.0.0\'\n" +
                "    outputFile \'" + outputFile + "\'\n" +
                "}";

        writeFile(buildFile, buildFileContent);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments(downloadTask, "--stacktrace")
                .build();

        assertEquals(SUCCESS, result.task(":" + downloadTask).getOutcome());
//        assertTrue(new File(outputFile).exists());
    }

    private void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
//////////////////////////////////////

//    /**
//     * Helper method that runs a Gradle task in the testProjectDir
//     *
//     * @param arguments         the task arguments to execute
//     * @param isSuccessExpected boolean representing whether or not the build is supposed to fail
//     * @return the task's BuildResult
//     */
//    private BuildResult gradle(boolean isSuccessExpected, String[] arguments =['tasks']) {
//        arguments += '--stacktrace'
//        def runner = GradleRunner.create()
//                .withArguments(arguments)
//                .withProjectDir(testProjectDir.root)
//                .withPluginClasspath()
//                .withDebug(true)
//        return isSuccessExpected ? runner.build() : runner.buildAndFail()
//    }
//
//    private BuildResult gradle(String[] arguments =['tasks']) {
//        gradle(true, arguments)
//    }
//
//    @Test
//    public void download() {
//        def result = gradle(true, 'swaggerhubDownload')
//
//        assert result.task(":swaggerhubDownload").outcome == SUCCESS
////        assert new File(outputFile).exists()
////        assert result.output.contains("Hello, world!")
//    }
//}

}
