package es.osoco.transformations

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.transform.*
import java.lang.annotation.*

@GroovyASTTransformation(phase = CompilePhase.CONVERSION)
class NotASTTransformation implements ASTTransformation {

    private static final METHOD_NAME = 'not'

    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        List classes = filterClassesWithoutNotMethod(sourceUnit.ast.classes)
        MethodNode notMethod = notMethod()
        classes.each { ClassNode clazz -> clazz.addMethod notMethod }
    }

    private filterClassesWithoutNotMethod(List classes) {
        classes.findAll { clazz -> clazz.methods.every { method -> method.name != METHOD_NAME } }
    }

    private MethodNode notMethod() {
        ASTNode[] ast = new AstBuilder().buildFromSpec {
            method(METHOD_NAME, MethodNode.ACC_PUBLIC | MethodNode.ACC_STATIC, Boolean) {
                parameters {
                    parameter 'expression': Object
                }
                exceptions {}
                block {
                    owner.expression.addAll new AstBuilder().buildFromCode { !expression }
                }
                annotations {}
            }
        }
        ast.first()
    }
}
