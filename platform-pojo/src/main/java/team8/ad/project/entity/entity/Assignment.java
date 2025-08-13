package team8.ad.project.entity.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Assignment {
    private Integer id;
    private Long classId;
    private String assignmentName;
    private Date expireTime;
    private Date createTime;
    private int whetherFinish;
    private Date finishTime;
}
