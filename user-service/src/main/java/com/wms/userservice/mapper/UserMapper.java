package com.wms.userservice.mapper;

import com.wms.api.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username} limit 1")
    User findUserByUsername(String username);

    @Insert("insert into user(username, password, is_active, permission) values(#{username}, #{password}, #{is_active}, #{permission})")
    void addUser(@Param("username") String username, @Param("password") String password, @Param("is_active") int is_active, @Param("permission") int permission);

    @Update("update user set password = #{newPassword} where id = #{id}")
    void changePassword(@Param("id") int id, @Param("newPassword") String newPassword);

    @Update("update user set is_active = #{is_active}, permission = #{permission} where id = #{id}")
    void changeUserActiveOrPermission(@Param("is_active") int is_active, @Param("permission") int permission, @Param("id") int id);

    @Delete("delete from user where id = #{id}")
    void deleteUser(@Param("id") int id);

}
