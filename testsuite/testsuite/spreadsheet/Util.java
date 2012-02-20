package testsuite.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.ValueVisitor;

public class Util {

    public static void assertIsString(Value value, final String string) {
        value.visit(new ValueVisitor() {
            
            @Override
            public void visitString(String expression) {
                assertEquals("assertIsString: got String!", string, expression);
            }
            
            @Override
            public void visitLoop() {
                fail("assertIsString: got Loop! (" + string + ")");
            }
            
            @Override
            public void visitInvalid(String expression) {
                assertEquals("assertIsString: got Invalid!", string, expression);
            }
            
            @Override
            public void visitDouble(double value) {
                assertEquals("assertIsString: got Double!", string, value);
            }
        });  
    }

    public static void assertIsDouble(Value value, final double d) {
        value.visit(new ValueVisitor() {
            
            @Override
            public void visitString(String expression) {
                assertEquals("assertIsDouble: got String!", d, expression);
            }
            
            @Override
            public void visitLoop() {
                fail("assertIsDouble: got Loop! (" + d + ")");
            }
            
            @Override
            public void visitInvalid(String expression) {
                assertEquals("assertIsDouble: got Invalid!", d, expression);
            }
            
            @Override
            public void visitDouble(double value) {
                assertEquals("assertIsDouble: got Double!", d, value, 0);
            }
        });
    }
    
    public static void assertIsLoopValue(Value value) {
        value.visit(new ValueVisitor() {

            @Override
            public void visitDouble(double value) {
                fail("assertIsLoopValue: got double" + value);
            }

            @Override
            public void visitInvalid(String expression) {
                fail("assertIsLoopValue: got InValid" + expression);
            }

            @Override
            public void visitLoop() {
                // yay!
            }

            @Override
            public void visitString(String expression) {
                fail("assertIsLoopValue: got String" + expression);

            }
        });
    }

    public static void assertIsInvalidValue(Value value,
            final String expectedInvalidValue) {
        value.visit(new ValueVisitor() {

            @Override
            public void visitDouble(double value) {
                assertEquals("assertIsInvalidValue got double",
                        expectedInvalidValue, value);
            }

            @Override
            public void visitInvalid(String expression) {
                assertEquals("assertIsInvalidValue", expectedInvalidValue,
                        expression);
            }

            @Override
            public void visitLoop() {
                assertEquals("assertIsInvalidValue got Loop",
                        expectedInvalidValue, "#LOOP");
            }

            @Override
            public void visitString(String expression) {
                assertEquals("assertIsInvalidValue got String",
                        expectedInvalidValue, expression);
            }
        });
    }
}
