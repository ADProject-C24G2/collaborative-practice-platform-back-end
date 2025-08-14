package team8.ad.project.entity.vo;

import lombok.Data;

@Data
public class StudentInformationVO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Integer status; // Corresponds to frontend's 0 | 1
}
