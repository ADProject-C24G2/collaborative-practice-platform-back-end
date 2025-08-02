package team8.ad.project.mapper.teacher;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import team8.ad.project.annotation.AutoFill;
import team8.ad.project.entity.entity.Class;
import team8.ad.project.enumeration.OperationType;

import java.util.List;

@Mapper
public interface ClassMapper {


    /**
     * Create class
     * @param newClass
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Class newClass);


    /**
     * Query all class
     * @return
     */
    @Select("select * from class")
    List<Class> selectAll();

}
