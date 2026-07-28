package com.smartmall.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartmall.common.Result;
import com.smartmall.user.converter.UserConverter;
import com.smartmall.user.dto.LoginDTO;
import com.smartmall.user.dto.PasswordUpdateDTO;
import com.smartmall.user.dto.UserRegisterDTO;
import com.smartmall.user.dto.UserUpdateDTO;
import com.smartmall.user.entity.User;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.user.service.UserService;
import com.smartmall.user.vo.UserVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制层
 *
 * 提供用户相关 REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;


    /**
     * 用户登录
     *
     * POST /api/user/login
     */
    @PostMapping("/login")
    public Result<String> login(
            @Valid @RequestBody LoginDTO dto
    ){

        String result = userService.login(dto);

        return Result.success(result);

    }
    /**
     * 用户注册
     *
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("用户注册 username={}", dto.getUsername());

        //user接收 service 传回的信息
        User user = userService.register(dto);
        //user -> uservo
        UserVO vo = userConverter.toVO(user);
        //返回统一响应格式：
        return Result.success(vo);

        //==return Result.success(userConverter.toVO(userService.register(dto)));
    }

    /**
     * 根据ID查询用户
     *
     * GET /api/user/{id}
     */
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        UserVO vo = userConverter.toVO(user);
        return Result.success(vo);
    }

    /**
     * 查询全部用户
     *
     * GET /api/user/list
     */
    @GetMapping("/list")
    public Result<List<UserVO>> list() {
        List<User> users = userService.list();
        List<UserVO> result = users.stream()
                .map(userConverter::toVO)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 用户分页查询
     *
     * GET /api/user/page?page=1&size=10
     */
    @GetMapping("/page")
    public Result<Page<UserVO>> page(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        Page<User> userPage = new Page<>(page, size);
        Page<User> resultPage = userService.page(userPage);
        Page<UserVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(userConverter::toVO)
                .collect(Collectors.toList()));
        return Result.success(voPage);
    }

    /**
     * 修改用户信息
     *
     * PUT /api/user/{id}
     */
    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        log.info("修改用户 id={}", id);
        User user = userService.updateUser(id, dto);
        UserVO vo = userConverter.toVO(user);
        return Result.success(vo);
    }

    /**
     * 修改密码
     *
     * PUT /api/user/{id}/password
     */
    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateDTO dto) {
        userService.updatePassword(id, dto);
        return Result.success();
    }

    /**
     * 禁用用户
     *
     * PUT /api/user/{id}/disable
     */
    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        userService.disableUser(id);
        return Result.success();
    }

    /**
     * 启用用户
     *
     * PUT /api/user/{id}/enable
     */
    @PutMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        userService.enableUser(id);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * DELETE /api/user/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (!success) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return Result.success();
    }
}