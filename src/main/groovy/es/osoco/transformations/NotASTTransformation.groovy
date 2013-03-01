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

    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        List classes = sourceUnit.ast.classes
        MethodNode notMethod = notMethod()
        classes.each { ClassNode clazz -> clazz.addMethod notMethod }
    }

    private MethodNode notMethod() {
        ASTNode[] ast = new AstBuilder().buildFromSpec {
            method('not', MethodNode.ACC_PUBLIC | MethodNode.ACC_STATIC, Boolean) {
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
