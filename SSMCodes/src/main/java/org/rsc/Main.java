package org.rsc;

import org.mybatis.spring.SqlSessionTemplate;
import org.rsc.aware.Book;
import org.rsc.config.MainConfiguration;
import org.rsc.entity.Student;
import org.rsc.mapper.TestMapper;
import org.rsc.service.TestService;
import org.rsc.service.impl.TestServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
    }
}