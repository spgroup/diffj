package org.incava.diffj.type;

import org.incava.analysis.FileDiff;
import org.incava.diffj.*;
import org.incava.ijdk.text.Location;

public class TestInnerClass extends ItemsTest {
    public TestInnerClass(String name) {
        super(name);
    }

    protected FileDiff makeClassRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, CLASS_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    public void testInnerClassAdded() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    class ITest {",
                           "        class I2Test {",
                           "        }",
                           "    }",
                           "}"),
                 
                 makeClassRef(null, "I2Test", loc(2, 5), loc(3, 5), loc(4, 9), loc(5, 9)));
    }

    public void testInnerClassRemoved() {
        evaluate(new Lines("class Test {",
                           "    class ITest {",
                           "        class I2Test {",
                           "        }",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    class ITest {",
                           "    }",
                           "}"),
                 
                 makeClassRef("I2Test", null, loc(3, 9), loc(4, 9), loc(2, 5), loc(3, 5)));
    }
}
