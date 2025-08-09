package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserClassDetails {
    private int id;
    private int classId;
    private int studentId;
    private LocalDateTime createTime;



}
