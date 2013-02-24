package es.osoco.transform

import static org.objectweb.asm.Opcodes.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.transform.*
import org.codehaus.groovy.control.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import java.lang.annotation.*
   
@GroovyASTTransformation(phase = CompilePhase.CONVERSION)
public class NotASTTransformation implements ASTTransformation {

   public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
       List classes = sourceUnit.ast?.classes
       MethodNode notMethod = notMethod()
       classes.each { ClassNode clazz -> clazz.addMethod(notMethod) }  
   }
   
   private MethodNode notMethod() {
       ASTNode[] ast = new AstBuilder().buildFromSpec {
           method('not', ACC_PUBLIC | ACC_STATIC, Boolean.class) {
               parameters {
                   parameter 'expression': Object.class
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