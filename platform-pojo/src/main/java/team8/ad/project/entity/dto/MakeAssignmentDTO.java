package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class MakeAssignmentDTO {
    private String classId;
    private String expire_time;
    private String title;
    private List<String> questionIds;
}
