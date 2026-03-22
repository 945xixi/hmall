package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    // TODO：②保存用户信息

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户信息
        String userInfo = request.getHeader("user-info");
        // 是否获取成功
        if (StrUtil.isNotBlank(userInfo)) {
            // 存入ThreadLocal中
            UserContext.setUser(Long.valueOf(userInfo));
        }

        // 都放行，因为网关已经筛选过了
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 清理用户
        UserContext.removeUser();
    }
}
