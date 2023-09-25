package org.grails.forge.cli.command

import io.micronaut.context.ApplicationContext
import org.grails.forge.application.ApplicationType
import org.grails.forge.cli.CodeGenConfig
import org.grails.forge.cli.CommandFixture
import org.grails.forge.cli.CommandSpec
import org.grails.forge.io.ConsoleOutput
import spock.lang.AutoCleanup
import spock.lang.Shared

class CreateInterceptorCommandSpec extends CommandSpec implements CommandFixture {

    @Shared
    @AutoCleanup
    ApplicationContext beanContext = ApplicationContext.run()


    void "test creating a service"() {

        setup:
        generateProject(ApplicationType.WEB)
        CodeGenConfig codeGenConfig = CodeGenConfig.load(beanContext, dir, ConsoleOutput.NOOP)
        ConsoleOutput consoleOutput = Mock(ConsoleOutput)
        CreateInterceptorCommand command = new CreateInterceptorCommand(codeGenConfig, getOutputHandler(consoleOutput), consoleOutput)
        command.interceptorName = "test"

        when:
        Integer exitCode = command.call()
        File output = new File(dir, "grails-app/controllers/example/grails/TestInterceptor.groovy")
        File specOutput = new File(dir, "src/test/groovy/example/grails/TestInterceptorSpec.groovy")

        then:
        exitCode == 0
        output.exists()
        specOutput.exists()
        2 * consoleOutput.out({ it.contains("Rendered interceptor") })
    }

    void "test app with interceptor"() {
        setup:
        generateProject(ApplicationType.WEB)
        CodeGenConfig codeGenConfig = CodeGenConfig.load(beanContext, dir, ConsoleOutput.NOOP)
        ConsoleOutput consoleOutput = Mock(ConsoleOutput)
        CreateInterceptorCommand command = new CreateInterceptorCommand(codeGenConfig, getOutputHandler(consoleOutput), consoleOutput)

        new File(dir, "grails-app/controllers/example/grails/TestController.groovy").text = '''package example.grails

class TestController {

    def index() {
        render request.getAttribute('foo')
    }

}'''


        when:
        command.interceptorName  = 'test'
        Integer exitCode = command.call()
        executeGradleCommand("test")

        then:
        exitCode == 0
        testOutputContains("BUILD SUCCESSFUL")

        when:
        new File(dir, "src/test/groovy/example/grails/TestInterceptorSpec.groovy").text = '''package example.grails

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class TestInterceptorSpec extends Specification implements InterceptorUnitTest<TestInterceptor> {

    void "test interceptor matching"() {
        when:
        withRequest(controller: "test")
    
        then:
        interceptor.doesMatch()
    
        when:
        withRequest(controller: "person")
    
        then:
        !interceptor.doesMatch()
    }
    
    void "test controller execution with interceptors"() {
        given:
        def controller = (TestController)mockController(TestController)
    
        when:
        withInterceptors([controller: "test"]) {
            controller.renderAttribute()
        }
    
        then:
        response.text == "Foo is Bar"
    }
}'''
        executeGradleCommand("test")

        then:
        testOutputContains("TestInterceptorSpec > test controller execution with interceptors FAILED")

        when:
        new File(dir, "grails-app/controllers/example/grails/TestInterceptor.groovy").text = '''package example.grails

class TestInterceptor {

    TestInterceptor() {
        match(controller: "test")
    }

    boolean before() {
        request.setAttribute('foo', 'Foo is Bar')
        true
    }
}'''

        executeGradleCommand("test")

        then:
        testOutputContains("BUILD SUCCESSFUL")
    }
}
