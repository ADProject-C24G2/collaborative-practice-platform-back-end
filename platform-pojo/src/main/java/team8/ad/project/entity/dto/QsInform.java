package team8.ad.project.entity.dto;
import lombok.Data;

@Data
public class QsInform {
    private Integer id;
    private byte[] image; // 或 String，如果存储路径
    private String question;
    private String grade;
    private String subject;
    private String topic;
    private String category;
}
