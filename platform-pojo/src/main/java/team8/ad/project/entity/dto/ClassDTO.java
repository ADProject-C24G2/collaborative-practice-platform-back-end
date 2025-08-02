package team8.ad.project.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClassDTO implements Serializable {

    private String name;

    private String description;

    private String accessType;

    private String Remember;

    private int expirationTime;

    private int maxMembers;

}
