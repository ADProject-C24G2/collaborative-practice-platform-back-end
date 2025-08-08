package team8.ad.project.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentItemDTO {
    private Integer assignmentId;
    private String  assignmentName;
    private LocalDateTime expireTime;
    private Integer whetherFinish;   // 0/1
    private LocalDateTime finishTime;
}
