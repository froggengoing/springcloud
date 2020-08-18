package com.awesomeJdk.practise.cbaeldungex;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.TimeUnit;

/**
 * https://www.baeldung.com/jvm-boolean-memory-layout
 * in-depth see: https://shipilev.net/jvm/objects-inside-out/
 */
public class class2_jol {
    public static void main(String[] args) {
        //By default, object references should be aligned by 8 bytes.
        //if change the alignment value to 32 via -XX:ObjectAlignmentInBytes=32,then the layout will be 32
        System.out.println(ClassLayout.parseClass(BooleanWrapper2.class).toPrintable());

        //#########################
        //In addition to two mark words and one klass word,
        // array pointers contain an extra 4 bytes to store their lengths.
        boolean[] value = new boolean[3];
        System.out.println(ClassLayout.parseInstance(value).toPrintable());

    }
    class BooleanWrapper {
        private boolean value;
    }
}
class BooleanWrapper2 {
    private boolean value;
}
