package com.awesomeJdk.practise.cbaeldungex;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * https://www.baeldung.com/java-class-view-bytecode
 * 查看class的字节码
 */
public class class3_bytecode {
    public static void main(String[] args) {
        try {
            ClassReader reader = new ClassReader("java.lang.Object");
            StringWriter sw = new StringWriter();
            TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
            reader.accept(tcv, 8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
