package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.StudentNameDTO;
import team8.ad.project.mapper.student.StudentUserMapper;
import team8.ad.project.service.student.StudentProfileService;

@Service("studentProfileService")
@Slf4j
public class StudentProfileServiceImpl implements StudentProfileService {

    @Autowired
    private StudentUserMapper studentUserMapper;

    @Override
    public StudentNameDTO getCurrentStudentName() {
        Integer cur = BaseContext.getCurrentId();
        if (cur == null || cur <= 0) {
            throw new IllegalStateException("未登录");
        }
        String name = studentUserMapper.selectNameById(cur.longValue());
        StudentNameDTO dto = new StudentNameDTO();
        dto.setName(name == null ? "" : name);
        return dto;
    }
}
