package team8.ad.project.service.student;

import team8.ad.project.entity.dto.AssignmentListRespDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.entity.dto.SubmitQuestionDTO;

public interface AssignmentService {
    AssignmentListRespDTO viewByClassId(Integer classId);
     ListDTO<SelectQuestionDTO> selectQuestionsByAssignmentId(Integer assignmentId);
     ListDTO<SubmitQuestionDTO> submitAssignment(Integer assignmentId);
}