/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.forge.utils

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.functional.ThrowingSupplier
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.grails.forge.application.ApplicationType
import org.grails.forge.application.OperatingSystem
import org.grails.forge.application.Project
import org.grails.forge.application.generator.ProjectGenerator
import org.grails.forge.io.ConsoleOutput
import org.grails.forge.io.FileSystemOutputHandler
import org.grails.forge.io.OutputHandler
import org.grails.forge.options.BuildTool
import org.grails.forge.options.Language
import org.grails.forge.options.Options
import org.grails.forge.options.TestFramework
import org.grails.forge.util.NameUtils
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

abstract class CommandSpec extends Specification {

    @Shared
    @AutoCleanup
    ApplicationContext applicationContext

    @Shared
    GradleRunner gradleRunner = GradleRunner.create()

    abstract String getTempDirectoryPrefix()

    File dir

    void setupSpec() {
        applicationContext = ApplicationContext.run(getConfiguration())
    }

    void setup() {
        dir = Files.createTempDirectory(tempDirectoryPrefix).toFile()
    }

    void cleanup() {
        dir.deleteDir()
    }

    Map<String, Object> getConfiguration() {
        return Collections.EMPTY_MAP
    }

    String executeBuild(BuildTool buildTool, String command) {
        String output = null
        if (buildTool.isGradle()) {
            output = executeGradle(command).getOutput()
        }
        return output
    }

    BuildResult executeGradle(String command) {
        gradleRunner.withProjectDir(dir)
                .withArguments(command.split(' '))
                .build()
    }

    BuildResult executeGradle(String... arguments) {
        gradleRunner.withProjectDir(dir)
                .withArguments(arguments)
                .build()
    }

    void generateProject(Language lang,
                         BuildTool buildTool = BuildTool.DEFAULT_OPTION,
                         OperatingSystem operatingSystem = OperatingSystem.LINUX,
                         List<String> features = [],
                         ApplicationType applicationType = ApplicationType.WEB,
                         TestFramework testFramework = null) {
        applicationContext.getBean(ProjectGenerator).generate(applicationType,
                NameUtils.parse("example.grails.foo"),
                new Options(lang, testFramework, buildTool),
                operatingSystem,
                features,
                new FileSystemOutputHandler(dir, ConsoleOutput.NOOP),
                ConsoleOutput.NOOP
        )
    }

    void generateProject(Project project,
                         Language lang = Language.DEFAULT_OPTION,
                         BuildTool buildTool = BuildTool.DEFAULT_OPTION,
                         OperatingSystem operatingSystem = OperatingSystem.LINUX,
                         List<String> features = [],
                         ApplicationType applicationType = ApplicationType.WEB,
                         TestFramework testFramework = TestFramework.DEFAULT_OPTION) {
        applicationContext.getBean(ProjectGenerator).generate(applicationType,
                project,
                new Options(lang, testFramework, buildTool),
                operatingSystem,
                features,
                new FileSystemOutputHandler(dir, ConsoleOutput.NOOP),
                ConsoleOutput.NOOP)
    }

    ThrowingSupplier<OutputHandler, IOException> getOutputHandler(ConsoleOutput consoleOutput) {
        return { -> new FileSystemOutputHandler(dir, consoleOutput) }
    }
}
