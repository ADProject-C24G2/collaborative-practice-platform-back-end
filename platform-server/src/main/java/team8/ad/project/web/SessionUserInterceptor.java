package team8.ad.project.web;

import lombok.extern.slf4j.Slf4j;
import team8.ad.project.constant.UserConstant;
import team8.ad.project.context.BaseContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class SessionUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Object uid = (session == null) ? null : session.getAttribute(UserConstant.USER_ID_IN_SESSION);

        if (uid instanceof Number) {
            BaseContext.setCurrentId(((Number) uid).intValue());
            return true;
        }

        // 未登录：返回 401（也可以按你们统一返回体写 JSON，见下方注释）
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 如果想返回 JSON：
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0,\"msg\":\"未登录或会话已失效\"}");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentId(); // 防止线程复用串号
    }
}
