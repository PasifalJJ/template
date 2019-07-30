package com.jsc.mapper;

import com.jsc.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserMapper extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {

}
