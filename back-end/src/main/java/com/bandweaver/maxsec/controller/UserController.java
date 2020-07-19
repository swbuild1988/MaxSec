package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/users")
    public R getUsers() {
        log.info("get users request");
        return new R(userService.selectAll());
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("get user request: " + id);
        String key = "user_" + id;

        User result = null;

        if (redisUtil.hasKey(key)) {
            result = (User) redisUtil.get(key);
            log.info("redis get " + result.getName());
        } else {
            result = userService.selectByPrimaryKey(id);
            redisUtil.set(key, result);
            log.info("redis set " + result.getName());
        }
        return result;
    }


    @PostMapping("/users")
    public R addUsers(@RequestBody User user) {
        userService.insert(user);
        return new R();
    }

    @PostMapping("/users_edit")
    public R editUsers(@RequestBody User user) {
        userService.update(user);
        return new R();
    }

    @GetMapping("/users_delete/{id}")
    public R deleteUser(@PathVariable("id") Integer id) {
        userService.delete(id);
        return new R();
    }

}
