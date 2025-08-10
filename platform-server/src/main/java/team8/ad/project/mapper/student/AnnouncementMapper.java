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
    "SELECT " +
    "  id           AS id, " +
    "  title        AS title, " +
    "  content      AS content, " +
    "  createTime   AS createTime, " +
    "  CAST(status AS SIGNED) AS status " +   // ← 关键：把 tinyint(1) 转成整型
    "FROM announcement " +
    "WHERE classId = #{classId} " +
    "  AND (studentId IS NULL OR studentId = #{studentId}) " +
    "ORDER BY createTime DESC"
    )
    List<AnnouncementItemDTO> listByClassAndStudent(@Param("classId") Integer classId,
                                                    @Param("studentId") Long studentId);

    @org.apache.ibatis.annotations.Update(
        "UPDATE announcement " +
        "SET status = 1 " +
        "WHERE id = #{id}"
    )
    int markRead(@org.apache.ibatis.annotations.Param("id") Integer id);
}
