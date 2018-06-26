package io.github.jsfrench.swaggerhub

import org.junit.rules.TemporaryFolder;
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.BuildResult
import org.junit.*


class SwaggerHubPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    private File build_gradle

    @Before
    public void setup() {
        // Prepare build.gradle
        build_gradle = testProjectDir.newFile('build.gradle')
        build_gradle << 'plugins { id "io.github.jsfrench.swaggerhub.SwaggerHubPlugin" }\n' +
                'swaggerhubDownload {\n' +
                '    api \'PetStoreAPI\'\n' +
                '    owner \'jsfrench\'\n' +
                '    version \'1.0.0\'\n' +
                '    outputFile \'target/test/petStoreAPI.json\'\n' +
                '}'
    }


    /**
     * Helper method that runs a Gradle task in the testProjectDir
     * @param arguments the task arguments to execute
     * @param isSuccessExpected boolean representing whether or not the build is supposed to fail
     * @return the task's BuildResult
     */
    private BuildResult gradle(boolean isSuccessExpected, String[] arguments = ['tasks']) {
        arguments += '--stacktrace'
        def runner = GradleRunner.create()
                .withArguments(arguments)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withDebug(true)
        return isSuccessExpected ? runner.build() : runner.buildAndFail()
    }

    private BuildResult gradle(String[] arguments = ['tasks']) {
        gradle(true, arguments)
    }

    @Test
    public void download() {
        def result = gradle('swaggerhubDownload')
        assert result.task(":swaggerhubDownload").outcome == SUCCESS
        assert result.output.contains("Hello, world!")
    }
}
