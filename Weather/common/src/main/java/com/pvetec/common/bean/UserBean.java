package com.pvetec.common.bean;
import java.util.Date;

/**
 * 
 * @author lcp 2016/1/15
 *
 */
/*@Entity
@Table(name="t_user")*/
public class UserBean {
	
/*    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)*/
    long id; 
    
    String name;
    
    String password;
    
    String sign;
    /**
     * 管理员0
     */
    String role;
    /**
     * 插入数据库时间
     */
    Date create_time;
    
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public Date getCreate_time() {
        return create_time;
    }
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}






