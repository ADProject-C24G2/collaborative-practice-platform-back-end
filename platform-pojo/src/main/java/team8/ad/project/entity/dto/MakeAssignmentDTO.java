package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class MakeAssignmentDTO {
    private String classId;
    private String assignmentName;
    private String expireTime;
    private List<String> questionIds;
}
