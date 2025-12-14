package com.rsc.annotation;

import java.lang.annotation.Annotation;

@Test1(test="1")
public class Anno {
    @Test("0")
    public static void main(String[] args) {
        Class<Stu> clazz = Stu.class;
        for (Annotation annotation : clazz.getAnnotations()) {
            System.out.println(annotation.annotationType());
            System.out.println(annotation instanceof Test);
            Test test = (Test)annotation;
            System.out.println(test.value());
        }
    }
}
