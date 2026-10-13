package com.smartmall.auth.service;


import com.smartmall.auth.dto.LoginDTO;
import com.smartmall.auth.vo.LoginVO;


public interface AuthService {


    LoginVO login(LoginDTO dto);


}