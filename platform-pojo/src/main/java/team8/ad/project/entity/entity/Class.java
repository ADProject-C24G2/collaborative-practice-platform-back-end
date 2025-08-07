package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Class {

    private int id;

    private int teacherId;

    private String name;

    private String description;

    private String accessType;

    private String token;

    private LocalDateTime accessExpiration;

    private LocalDateTime accessAvailable;

    private int maxMembers;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private int createUser;

    private int updateUser;

}
