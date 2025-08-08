package team8.ad.project.service.student;

import team8.ad.project.entity.dto.AssignmentListRespDTO;

public interface AssignmentService {
    AssignmentListRespDTO viewByClassId(Integer classId);
}