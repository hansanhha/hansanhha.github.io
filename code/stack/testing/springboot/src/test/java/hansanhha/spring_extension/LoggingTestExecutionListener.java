package hansanhha.spring_extension;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class LoggingTestExecutionListener implements TestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        System.out.println(getCurrentTestCaseName(testContext) + ": test case executing");
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        System.out.println(getCurrentTestCaseName(testContext) + ": test case executed");
    }

    private String getCurrentTestCaseName(TestContext testContext) {
        String className = testContext.getTestClass().getSimpleName();
        String methodName = testContext.getTestMethod().getName();

        return className + "." + methodName;
    }

}
