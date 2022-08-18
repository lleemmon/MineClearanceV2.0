package com.mc.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> getUserList(User user);

}
