package team8.ad.project.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginDTO implements Serializable {

    // 前端传来的字段是 username 和 password，但我们按邮箱登录，所以这里用 email

    private String email;

    private String password;

    // 前端传来的type，虽然本次登录逻辑里没用，但最好接收一下
    private String type;
}