package org.rsc.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.rsc.entity.Student;

@Mapper
public interface TestMapper {
    @Insert("insert into student(name, sex) values('测试', '男')")
    void insertStudent();
}
