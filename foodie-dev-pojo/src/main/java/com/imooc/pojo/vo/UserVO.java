package com.imooc.pojo.vo;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.pojo.vo
 * @date 2020/8/7 10:32
 */
public class UserVO {

    private String id;
    private String username;
    private String nickname;
    private String face;
    private String email;
    private String uniqueToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniqueToken() {
        return uniqueToken;
    }

    public void setUniqueToken(String uniqueToken) {
        this.uniqueToken = uniqueToken;
    }
}
