package com.pvetec.common.service;
import com.pvetec.common.bean.UserBean;

public interface UserService {
	
    /** 
     * 检查是否存在用户
     */
    public boolean checkUser(UserBean user);
    
    
    /**
     * 
     * //检查是否存在用户
     * @param userName 用户名
     * @param userPw   密码(Md5加密 并大写)
     * @return
     */
    public boolean checkUser(String userName, String userPw);

}
