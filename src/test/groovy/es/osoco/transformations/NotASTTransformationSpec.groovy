package es.osoco.transformations

import spock.lang.Specification
import spock.lang.Unroll
import org.codehaus.groovy.control.*
import org.codehaus.groovy.transform.*
import org.codehaus.groovy.tools.ast.*

class NotASTTransformationSpec extends Specification {

    private helper

    def setup() {
        def transform = new NotASTTransformation()
        def phase = CompilePhase.CONVERSION
        helper = new TransformTestHelper(transform, phase)
    }

    @Unroll
    def "creates static method that negates boolean values"() {
        setup:
        def clazz = arbitraryScriptWithASTTransformationApplied()

        expect:
        clazz.not booleanValue == result
        clazz.newInstance().not booleanValue == result

        where:
        booleanValue  | result
        true          | false
        false         | true
        Boolean.TRUE  | false 
        Boolean.FALSE | true
    }

    def "creates static method that negates a groovy truth expression"() {
        setup:
        def clazz = arbitraryScriptWithASTTransformationApplied()

        expect:
        clazz.not new Object() == false
        clazz.not null == true
    }

    def "creates static method that negates a expressions based on static method calls"() {
        setup:
        def clazz = arbitraryScriptWithASTTransformationApplied()

        expect:
        clazz.not ArbitraryClass.staticTrueMethod() == false
        clazz.not ArbitraryClass.staticFalseMethod() == true
    }

    def "creates static method that negates a expressions based on static closure calls"() {
        setup:
        def clazz = arbitraryScriptWithASTTransformationApplied()

        expect:
        clazz.not ArbitraryClass.staticTrueClosure() == false
        clazz.not ArbitraryClass.staticFalseClosure() == true
    }

    def "creates static method that negates a expressions based on instance method calls"() {
        setup:
        def clazz = arbitraryScriptWithASTTransformationApplied()

        expect:
        clazz.not ArbitraryClass.newInstance().trueMethod() == false
        clazz.not ArbitraryClass.newInstance().falseMethod() == true
    }

    def "creates static method that negates a expressions based on instance closure calls"() {
        setup:
        def clazz = arbitraryScriptWithASTTransformationApplied()

        expect:
        clazz.not ArbitraryClass.newInstance().trueClosure() == false
        clazz.not ArbitraryClass.newInstance().falseClosure() == true
    }

    def "AST transformation is not applied when target class has a not method"() {
        setup:
        def clazz = scriptWithNotMethodAndASTTransformationApplied()

        expect:
        clazz.not(false) == false
    }

    private arbitraryScriptWithASTTransformationApplied() {
        helper.parse(' // arbitrary script ')
    }

    private scriptWithNotMethodAndASTTransformationApplied() {
        helper.parse('static Boolean not(Object object) { false }')
    }
}

class ArbitraryClass {
    static staticTrueMethod() { true }
    static staticFalseMethod() { false }
    static staticTrueClosure = { true }
    static staticFalseClosure = { false }
    def trueMethod() { true }
    def falseMethod() { false }
    def trueClosure = { true }
    def falseClosure = { false }
}