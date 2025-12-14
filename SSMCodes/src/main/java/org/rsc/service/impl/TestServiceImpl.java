package org.rsc.service.impl;

import jakarta.annotation.Resource;
import org.rsc.mapper.TestMapper;
import org.rsc.service.TestService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestServiceImpl implements TestService {
    @Resource
    TestMapper mapper;

    @Transactional
    @Override
    public void test() {
        System.out.println("before exc");
        test2();
        System.out.println("after exc");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void test2() {

        mapper.insertStudent();
        if (true) throw new RuntimeException("exception");
    }
}
