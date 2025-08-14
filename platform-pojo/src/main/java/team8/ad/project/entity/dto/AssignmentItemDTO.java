package team8.ad.project.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentItemDTO {
    private Integer assignmentId;
    private String  assignmentName;
    private LocalDateTime expireTime;
    private Integer whetherFinish;   // assignment_students_details中的
    private LocalDateTime finishTime; // assignment_students_details中的
}
