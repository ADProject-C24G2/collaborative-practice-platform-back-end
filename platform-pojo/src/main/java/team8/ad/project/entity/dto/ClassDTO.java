package team8.ad.project.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClassDTO implements Serializable {

    private String name;

    private String description;

    private String accessType;

    private List<String> date;

    private int maxMembers;

}
