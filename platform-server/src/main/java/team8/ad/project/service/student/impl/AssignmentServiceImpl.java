package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import team8.ad.project.entity.dto.AssignmentItemDTO;
import team8.ad.project.entity.dto.AssignmentListRespDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.mapper.student.AssignmentDetailMapper;
import team8.ad.project.mapper.student.AssignmentMapper;
import team8.ad.project.service.student.AssignmentService;
import team8.ad.project.service.student.QuestionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("studentAssignmentService")
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private QuestionService questionServiceImpl;
    
    @Autowired
    @Qualifier("studentAssignmentDetailMapper")
    private AssignmentDetailMapper assignmentDetailMapper;

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

    @Override
    public ListDTO<SelectQuestionDTO> selectQuestionsByAssignmentId(Integer assignmentId) {
        ListDTO<SelectQuestionDTO> dto = new ListDTO<>();
        try {
            List<Integer> ids = assignmentDetailMapper.listQuestionIdsByAssignment(assignmentId);
            if (ids == null || ids.isEmpty()) {
                dto.setList(Collections.emptyList());
                return dto;
            }
            List<SelectQuestionDTO> list = new ArrayList<>(ids.size());
            for (Integer qid : ids) {
                SelectQuestionDTO q = questionServiceImpl.getQuestionById(qid);
                if (q != null) list.add(q);
            }
            dto.setList(list);
        } catch (Exception e) {
            log.error("按作业查询题目失败: assignmentId={}, err={}", assignmentId, e.getMessage(), e);
            dto.setList(Collections.emptyList());
        }
        return dto;
    }
}