package com.smartmall.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.security.utils.JwtUtils;
import com.smartmall.user.dto.LoginDTO;
import com.smartmall.user.dto.PasswordUpdateDTO;
import com.smartmall.user.dto.UserRegisterDTO;
import com.smartmall.user.dto.UserUpdateDTO;
import com.smartmall.user.entity.User;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.user.mapper.UserMapper;
import com.smartmall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;//auth

    @Override
    public String login(LoginDTO dto) {
        User user = lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        boolean match = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!match) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        //生成JWT
        return jwtUtils.createToken(
                user.getId()
        );
    }
    /**
     * 用户注册
     */
    @Override
    @Transactional
    public User register(UserRegisterDTO dto) {
        // 1.检查用户名

        //MyBatis-Plus 提供的查询条件构造器，"Lambda" 是指它支持用方法引用（User::getUsername）
        //而不是字符串（"username"）来指定字段。用方法引用的好处是编译期类型安全
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        //构造一个 WHERE username = ? 的条件，? 的值是 dto.getUsername()
        //给usernameWrapper添加了一个筛选条件
        usernameWrapper.eq(User::getUsername, dto.getUsername());
        //count 方法来自 ServiceImpl 基类（也就是 IService 接口），
        //执行 SELECT COUNT(*) FROM user WHERE username = ?，返回符合条件的记录数
        if (this.count(usernameWrapper) > 0) {
            throw new BusinessException(ResultCode.USER_EXIST);
        }

        // 2.检查手机号
        //UserRegisterDTO 里 phone 字段没有 @NotBlank，是可选的吗？
        // 如果用户注册时没填手机号，dto.getPhone() 就是 null 或空字符串。
        // 这时候如果直接拿 null 去数据库查 WHERE phone = null（或空字符串），
        // 没有意义——不填手机号是允许的，不应该因为"没填"就被当成"重复"去拦截
        if (StrUtil.isNotBlank(dto.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, dto.getPhone());
            if (this.count(phoneWrapper) > 0) {
                throw new BusinessException(ResultCode.PHONE_EXIST);
            }
        }

        // 3.检查邮箱
        if (StrUtil.isNotBlank(dto.getEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, dto.getEmail());
            if (this.count(emailWrapper) > 0) {
                throw new BusinessException(ResultCode.EMAIL_EXIST);
            }
        }

        // 4.创建用户
        User user = new User();
        user.setUsername(dto.getUsername());

        // BCrypt加密
        //dto.getPassword() 是用户注册时填的明文密码，
        // passwordEncoder.encode(...) 把它转换成一段不可逆的加密字符串（哈希值），
        // 只有这个加密后的结果会被存进数据库的 password 字段。
        // 原始明文密码在这一行执行完之后，程序里就不再需要它了，也从没被存过
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        //三元运算符 条件 ? 值A : 值B：如果用户填了昵称，就用填的昵称
        user.setNickname(
                StrUtil.isNotBlank(dto.getNickname())
                        ? dto.getNickname()
                        : dto.getUsername()
        );

        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());

        //0未知 1男 2女
        user.setGender(dto.getGender() == null ? 0 : dto.getGender());

        // 默认正常
        user.setStatus(1);

        this.save(user);
        return user;
    }

    /**
     * 修改用户资料
     */
    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateDTO dto) {

        //来自 ServiceImpl/IService 的基础方法，等价于 SELECT * FROM user WHERE id = ?，
        //返回一个 User 对象，如果数据库里没有这条记录，返回 null
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }

        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }

        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }

        this.updateById(user);
        return user;
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional
    public void updatePassword(Long id, PasswordUpdateDTO dto) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 验证旧密码
        boolean match = passwordEncoder.matches(dto.getOldPassword(), user.getPassword());
        if (!match) {
            throw new BusinessException(ResultCode.USER_OLD_PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
    }

    /**
     * 禁用用户
     */
    @Override
    public void disableUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        user.setStatus(0);
        this.updateById(user);
    }

    /**
     * 启用用户
     */
    @Override
    public void enableUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        user.setStatus(1);
        this.updateById(user);
    }

}