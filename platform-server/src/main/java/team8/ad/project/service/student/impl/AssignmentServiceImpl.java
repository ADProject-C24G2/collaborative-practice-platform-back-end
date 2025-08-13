package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.AssignmentItemDTO;
import team8.ad.project.entity.dto.AssignmentListRespDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.entity.dto.SubmitQuestionDTO;
import team8.ad.project.entity.entity.Question;
import team8.ad.project.mapper.student.AssignmentDetailMapper;
import team8.ad.project.mapper.student.AssignmentMapper;
import team8.ad.project.mapper.student.AssignmentSubmitMapper;
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
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("studentAssignmentSubmitMapper")
    private AssignmentSubmitMapper assignmentSubmitMapper;
    
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

     @Override
    public ListDTO<SubmitQuestionDTO> submitAssignment(Integer assignmentId) {
        ListDTO<SubmitQuestionDTO> dto = new ListDTO<>();
        try {
            List<Question> raws = assignmentSubmitMapper.listRawQuestionsByAssignment(assignmentId);
            if (raws == null || raws.isEmpty()) {
                dto.setList(Collections.emptyList());
                return dto;
            }
            List<SubmitQuestionDTO> list = new ArrayList<>(raws.size());
            for (Question q : raws) {
                SubmitQuestionDTO item = new SubmitQuestionDTO();
                item.setId(q.getId());
                item.setImage(q.getImage());
                item.setQuestion(q.getQuestion());
                try {
                    List<String> choices = objectMapper.readValue(
                            q.getChoices(), new TypeReference<List<String>>() {}
                    );
                    item.setChoices(choices);
                } catch (Exception e) {
                    log.warn("解析choices失败: qid={}, err={}", q.getId(), e.getMessage());
                    item.setChoices(Collections.emptyList());
                }
                list.add(item);
            }
            dto.setList(list);
        } catch (Exception e) {
            log.error("SubmitAssignment 查询失败: assignmentId={}, err={}", assignmentId, e.getMessage(), e);
            dto.setList(Collections.emptyList());
        }
        return dto;
    }
    private long currentUserIdOrThrow() {
        Integer id = BaseContext.getCurrentId();
        if (id == null || id <= 0) throw new IllegalStateException("未登录");
        return id.longValue();
    }
}