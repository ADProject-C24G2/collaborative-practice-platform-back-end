package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterDTO {

    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String signature;
    private String gender;
    private String title;
    private String group;
    private List<String> tags;
}
