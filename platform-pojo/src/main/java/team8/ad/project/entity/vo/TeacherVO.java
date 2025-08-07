package team8.ad.project.entity.vo;

import lombok.Data;
import team8.ad.project.entity.entity.Tag;

import java.util.List;

@Data
public class TeacherVO {
    private String name;
    private String avatar;
    private String userid;
    private String email;
    private String signature;
    private String title;
    private String group;


    private List<Tag> tags;
    private String address;

}
