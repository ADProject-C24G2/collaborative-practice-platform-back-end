package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.ad.project.entity.dto.AssignmentItemDTO;
import team8.ad.project.entity.dto.AssignmentListRespDTO;
import team8.ad.project.mapper.student.AssignmentMapper;
import team8.ad.project.service.student.AssignmentService;

import java.util.Collections;
import java.util.List;

@Service("studentAssignmentService")
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Override
    public AssignmentListRespDTO viewByClassId(Integer classId) {
        AssignmentListRespDTO resp = new AssignmentListRespDTO();
        try {
            var header = assignmentMapper.selectClassHeader(classId);
            if (header != null) {
                resp.setClassId(header.classId);
                resp.setClassName(header.className);
            } else {
                // 班级不存在也给个兜底
                resp.setClassId(classId);
                resp.setClassName("");
            }
            List<AssignmentItemDTO> list = assignmentMapper.listByClassId(classId);
            resp.setList(list != null ? list : Collections.emptyList());
        } catch (Exception e) {
            log.error("查询作业失败: classId={}, err={}", classId, e.getMessage(), e);
            resp.setClassId(classId);
            resp.setClassName("");
            resp.setList(Collections.emptyList());
        }
        return resp;
    }
}