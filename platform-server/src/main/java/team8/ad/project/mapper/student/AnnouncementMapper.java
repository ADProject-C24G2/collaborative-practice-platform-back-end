package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import team8.ad.project.entity.dto.AnnouncementItemDTO;

import java.util.List;

@Mapper
@Repository("studentAnnouncementMapper")
public interface AnnouncementMapper {

    @Select(
        "SELECT title, content, createTime " +
        "FROM announcement " +
        "WHERE classId = #{classId} " +
        // 发给全班（studentId IS NULL）或发给这个学生本人
        "AND (studentId IS NULL OR studentId = #{studentId}) " +
        "ORDER BY createTime DESC"
    )
    List<AnnouncementItemDTO> listByClassAndStudent(@Param("classId") Integer classId,
                                                    @Param("studentId") Long studentId);
}
