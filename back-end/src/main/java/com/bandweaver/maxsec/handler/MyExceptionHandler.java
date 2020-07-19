package com.bandweaver.maxsec.handler;


import com.bandweaver.maxsec.dto.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 捕获所有的异常并处理
 */
@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        log.info("binder.getFieldDefaultPrefix {}" + binder.getFieldDefaultPrefix());
        log.info("binder.getFieldMarkerPrefix {}" + binder.getFieldMarkerPrefix());
    }

    /**
     * Description : 全局异常捕捉处理
     *
     * @return
     */
    @ExceptionHandler(Exception.class)
    public R apiExceptionHandler(HttpServletRequest request, Throwable ex) {
        log.info("ApiException 异常抛出：{}", ex);
        return new R(getStatus(request).value(), new Throwable(ex.getMessage()));
    }

    // 捕捉shiro的异常
    @ExceptionHandler({ShiroException.class})
    @ResponseBody
    public R handle401(ShiroException ex) {
        log.info("Shiro Exception 异常抛出：{}", ex);

        return new R(401, new Throwable(ex.getMessage()));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
