package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import team8.ad.project.entity.dto.ClassListItemDTO;
import java.util.List;

@Mapper
@Repository("studentClassMapper")
public interface ClassMapper {

    @Select("SELECT id AS classId, name AS className, description " +
            "FROM class")
    List<ClassListItemDTO> viewClass();
}
