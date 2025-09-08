package hansanhha;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class NestedDynamicTest {

    @TestFactory
    Stream<DynamicContainer> createGroupedTestInRuntime() {

        // DynamicContainer.dynamicContainer("그룹명", Stream<DynamicTest>)
        return Stream.of(
                DynamicContainer.dynamicContainer("group 1", Stream.of(
                        DynamicTest.dynamicTest("first test", () -> assertEquals(2, 1 + 1)),
                        DynamicTest.dynamicTest("last test", () -> assertTrue("hello".startsWith("h")))
                )),

                DynamicContainer.dynamicContainer("group 2", Stream.of(
                        DynamicTest.dynamicTest("first test", () -> assertFalse(10 < 5)),
                        DynamicTest.dynamicTest("last test", () -> assertEquals("junit", "junit"))
                ))
        );
    }

}
