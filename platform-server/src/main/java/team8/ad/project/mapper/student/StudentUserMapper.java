package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentUserMapper {

    @Select("SELECT `name` FROM `user` WHERE id = #{id}")
    String selectNameById(@Param("id") Long id);
}