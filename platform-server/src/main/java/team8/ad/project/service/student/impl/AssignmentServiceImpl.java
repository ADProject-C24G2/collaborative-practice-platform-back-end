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
import team8.ad.project.entity.dto.AssignmentProgressDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.entity.dto.SubmitQuestionDTO;
import team8.ad.project.entity.entity.AssignmentStudentsDetails;
import team8.ad.project.entity.entity.Question;
import team8.ad.project.mapper.student.AssignmentDetailMapper;
import team8.ad.project.mapper.student.AssignmentMapper;
import team8.ad.project.mapper.student.AssignmentStudentsDetailsMapper;
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

    @Autowired
    @Qualifier("studentAssignmentStudentsDetailsMapper")
    private AssignmentStudentsDetailsMapper assignmentStudentsDetailsMapper;

    @Override
    public AssignmentListRespDTO viewByClassId(Integer classId) {
        AssignmentListRespDTO resp = new AssignmentListRespDTO();
        try {
            var header = assignmentMapper.selectClassHeader(classId);
            if (header != null) {
                resp.setClassId(header.classId);
                resp.setClassName(header.className);
            } else {
                resp.setClassId(classId);
                resp.setClassName("");
            }

            // 关键：从 BaseContext 拿当前登录学生 id，传给 mapper
            Long studentId = null;
            Integer cur = team8.ad.project.context.BaseContext.getCurrentId();
            if (cur != null && cur > 0) {
                studentId = cur.longValue();
            }

            List<AssignmentItemDTO> list = assignmentMapper.listByClassId(classId, studentId);
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

    @Override
    public String saveOrUpdateAssignmentProgress(AssignmentProgressDTO dto) {
        try {
            if (dto == null || dto.getAssignmentId() == null) return "assignmentId不能为空";
            if (dto.getWhetherFinish() == null || (dto.getWhetherFinish() != 0 && dto.getWhetherFinish() != 1)) {
                return "whetherFinish只能是0或1";
            }
            if (dto.getAccuracy() == null) return "accuracy不能为空";

            // 从 BaseContext（由 Session 拦截器注入）拿当前用户
            Integer cur = team8.ad.project.context.BaseContext.getCurrentId();
            if (cur == null || cur <= 0) return "用户未登录";

            AssignmentStudentsDetails row = new AssignmentStudentsDetails();
            row.setAssignmentId(dto.getAssignmentId());
            row.setStudentId(cur.longValue());
            row.setWhetherFinish(dto.getWhetherFinish());
            row.setFinishTime(java.time.LocalDateTime.now());
            row.setAccurancy(dto.getAccuracy()); // 列名Accurancy

            int affected = assignmentStudentsDetailsMapper.upsert(row);
            if (affected <= 0) return "保存失败，请稍后重试";
            return null; // null 表示成功
        } catch (Exception e) {
            log.error("保存作业完成状态失败: dto={}, err={}", dto, e.getMessage(), e);
            return "系统异常";
        }
    }

    private long currentUserIdOrThrow() {
        Integer id = BaseContext.getCurrentId();
        if (id == null || id <= 0) throw new IllegalStateException("未登录");
        return id.longValue();
    }
}