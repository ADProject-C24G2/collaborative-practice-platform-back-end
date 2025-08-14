// package team8.ad.project.entity.vo; // 建议放在 vo 包下
package team8.ad.project.entity.vo;

import lombok.Data;

/**
 * 班级列表视图对象 (Value Object)
 * 用于向前端返回班级列表信息
 */
@Data
public class ClassVO {
    private String id;
    private String token;// 前端期望 String 类型
    private String className;
    private Integer studentAmount; // 前端期望 Integer 类型
    private Integer ongoingAssignment; // 前端期望 Integer 类型
    private String avatar; // 前端期望 String 类型
    // private String title; // 前端 mock 数据里有 title: ""，但实际似乎没用，可以省略或设默认值
    // 如果保留，记得初始化或在 Service 设置
}