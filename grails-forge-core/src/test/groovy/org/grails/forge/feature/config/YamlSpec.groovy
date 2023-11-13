package org.grails.forge.feature.config

import org.grails.forge.BeanContextSpec
import org.grails.forge.application.ApplicationType
import org.grails.forge.application.generator.GeneratorContext
import org.grails.forge.feature.FeaturePhase
import org.grails.forge.fixture.CommandOutputFixture
import org.grails.forge.options.Options
import spock.lang.Shared
import spock.lang.Subject
import spock.lang.Unroll

class YamlSpec extends BeanContextSpec implements CommandOutputFixture {

    @Shared
    @Subject
    Yaml yaml = beanContext.getBean(Yaml)

    void "order is highest"() {
        expect:
        yaml.order == FeaturePhase.HIGHEST.getOrder()
    }

    @Unroll
    void "yaml supports #description application type"(ApplicationType applicationType, String description) {
        expect:
        yaml.supports(applicationType)

        where:
        applicationType << ApplicationType.values()
        description = applicationType.name
    }

    void "test configuration files generated for yaml feature"() {
        when:
        GeneratorContext generatorContext = buildGeneratorContext([], { context ->
            context.getBootstrapConfiguration().put("abc", 123)
            context.getConfiguration("test", new ApplicationConfiguration("test", "test")).put("abc", 456)
            context.getConfiguration("prod", new ApplicationConfiguration("prod")).put("abc", 789)
        }, new Options())
        def output = generate(ApplicationType.WEB, generatorContext)

        then:
        output["grails-app/conf/application.yml"].contains '''\
info:
  app:
    name: '@info.app.name@'
'''
        output["grails-app/conf/bootstrap.yml"] == '''\
abc: 123
'''
        output["src/test/resources/application-test.yml"] == '''\
abc: 456
'''
        output["grails-app/conf/application-prod.yml"] == '''\
abc: 789
'''
    }
}
