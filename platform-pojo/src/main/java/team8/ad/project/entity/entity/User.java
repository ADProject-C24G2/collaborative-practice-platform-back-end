package team8.ad.project.entity.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {
    private int id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String signature;
    private String gender;
    private String avatar;

    private String title;
    private String group;
    private String userType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Tag> tags;

    private Integer createUser;
    private Integer updateUser;

    private Integer status;



}
