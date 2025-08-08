package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.ad.project.entity.dto.ClassListItemDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.mapper.student.ClassMapper;
import team8.ad.project.service.student.ClassService;

import java.util.Collections;

@Slf4j
@Service("studentClassServiceImpl")
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public ListDTO<ClassListItemDTO> viewClass() {
        ListDTO<ClassListItemDTO> dto = new ListDTO<>();
        try {
            dto.setList(classMapper.viewClass());
        } catch (Exception e) {
            log.error("查询课程失败: {}", e.getMessage(), e);
            dto.setList(Collections.emptyList());
        }
        return dto;
    }
}
