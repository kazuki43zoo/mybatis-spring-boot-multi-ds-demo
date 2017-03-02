package com.example.mapper.ds1;

import com.example.Todo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface Todo1Mapper {

	@Insert("INSERT INTO todo (todo_title, details, finished) VALUES (#{todoTitle}, #{details}, #{finished})")
	@Options(useGeneratedKeys = true, keyProperty = "todoId")
	void insert(Todo todo);

	@Select("SELECT todo_id, todo_title, details, finished FROM todo WHERE todo_id = #{id}")
	Todo select(int id);

}