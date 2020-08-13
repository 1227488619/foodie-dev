package com.imooc.pojo.bo;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.pojo.bo
 * @date 2020/7/14 18:08
 */
public class UserBO {

    private String username;
    private String password;
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
