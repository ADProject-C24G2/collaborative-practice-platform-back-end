package team8.ad.project.entity.entity;

import lombok.Data;

@Data
public class Tag {

    private int id;
    private int teacherId;
    private String key;
    private String label;

    public Tag(int teacherId, String tagValue) {
        this.teacherId = teacherId;
        this.key = tagValue; // As requested, we'll use the label as the key
        this.label = tagValue;
    }
}
