package com.rsc.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class Run {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // 获取反射
        /*Class<String> clazz = String.class;
        Class<?> clazz1 = Class.forName("java.lang.String");
        Class<?> clazz2 = new String("cpdd").getClass();
        System.out.println(clazz == clazz1);
        System.out.println(clazz == clazz2);
        System.out.println(Integer.TYPE == int.class);*/

        // 数组反射
        /*Class<String[]> clazz = String[].class;
        System.out.println(clazz.getName());
        System.out.println(clazz.getSimpleName());
        System.out.println(clazz.getTypeName());
        System.out.println(clazz.getClassLoader());
        System.out.println(clazz.cast(new char[]{1,2,3}));*/

        // 反射多态
        /*System.out.println("" instanceof String);
        System.out.println("".getClass() == String.class);
        Integer i = 1;
        System.out.println(i.getClass().getSuperclass());*/
        /*Integer i = 1;
        System.out.println(i.getClass().asSubclass(Number.class));
        for (Class<?> anIntegerface : i.getClass().getInterfaces()) {
            System.out.println(anIntegerface);
        }
        for (Type anIntegerface : i.getClass().getGenericInterfaces()) {
            System.out.println(anIntegerface.getTypeName());
        }
        System.out.println(i.getClass().getGenericSuperclass() instanceof Class);*/

        // 创建类对象
        Class<Student> clazz = Student.class;
        Student stu = clazz.newInstance();
        stu.print();

        StuWithCons stu1 = StuWithCons.class.getConstructor(String.class).newInstance("xxx");

    }

    static class Student {
        public void print() {
            System.out.println("stu");
        }
    }

    static class StuWithCons {
        public StuWithCons(String name) {}
    }
}
