package org.grails.forge.cli.command

import io.micronaut.context.ApplicationContext
import org.grails.forge.application.ApplicationType
import org.grails.forge.cli.CodeGenConfig
import org.grails.forge.cli.CommandFixture
import org.grails.forge.cli.CommandSpec
import org.grails.forge.io.ConsoleOutput
import org.grails.forge.options.Language
import spock.lang.AutoCleanup
import spock.lang.Shared

class CreateControllerCommandSpec extends CommandSpec implements CommandFixture {

    @Shared
    @AutoCleanup
    ApplicationContext beanContext = ApplicationContext.run()


    void "test creating a controller"() {

        setup:
        generateProject(ApplicationType.WEB)
        CodeGenConfig codeGenConfig = CodeGenConfig.load(beanContext, dir, ConsoleOutput.NOOP)
        ConsoleOutput consoleOutput = Mock(ConsoleOutput)
        CreateControllerCommand command = new CreateControllerCommand(codeGenConfig, getOutputHandler(consoleOutput), consoleOutput)
        command.controllerName = "Greeting"

        when:
        Integer exitCode = command.call()
        File output = new File(dir, "grails-app/controllers/example/grails/GreetingController.groovy")
        File specOutput = new File(dir, "src/test/groovy/example/grails/GreetingControllerSpec.groovy")

        then:
        exitCode == 0
        output.exists()
        specOutput.exists()
        2 * consoleOutput.out({ it.contains("Rendered controller") })
    }

    void "test app with controller"() {
        setup:
        generateProject(ApplicationType.WEB)
        CodeGenConfig codeGenConfig = CodeGenConfig.load(beanContext, dir, ConsoleOutput.NOOP)
        ConsoleOutput consoleOutput = Mock(ConsoleOutput)
        CreateControllerCommand command = new CreateControllerCommand(codeGenConfig, getOutputHandler(consoleOutput), consoleOutput)

        when:
        command.controllerName  = 'Greeting'
        Integer exitCode = command.call()
        executeGradleCommand('build')

        then:
        exitCode == 0
        testOutputContains("BUILD SUCCESSFUL")

    }
}
