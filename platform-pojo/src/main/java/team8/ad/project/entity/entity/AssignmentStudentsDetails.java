package team8.ad.project.entity.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssignmentStudentsDetails {

    private Integer assignmentId;
    private Long    studentId;
    private Integer whetherFinish;     // 0/1
    private LocalDateTime finishTime;  // 服务器当前时间
    private BigDecimal accurancy;      // 注意拼写，对应表字段 Accurancy
}
