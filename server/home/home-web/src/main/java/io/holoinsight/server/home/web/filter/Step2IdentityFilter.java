/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.filter;

import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.config.RestAuthUtil;
import io.holoinsight.server.home.biz.access.MonitorAccessService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.web.common.TokenUrlFactoryHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author jsy1001de
 * @version 1.0: IdentityFilter.java, v 0.1 2022年03月14日 5:22 下午 jsy1001de Exp $
 */
@Configuration
@Order(4)
@Slf4j
public class Step2IdentityFilter implements Filter {

  private static final Long token_expire_sec = 28 * 60L;

  @Autowired
  private ULAFacade ulaFacade;

  @Autowired
  private MonitorAccessService monitorAccessService;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletRequest;
    HttpServletResponse resp = (HttpServletResponse) servletResponse;
    try {
      boolean next = identity(req, resp);
      if (next) {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    } catch (Throwable e) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          RequestContext.getTrace() + " identity error, " + e.getMessage());
      log.error("{} identity error", RequestContext.getTrace(), e);
    }
  }

  public boolean identity(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // token 降级
    String token = req.getHeader("apiToken");
    if (StringUtil.isNotBlank(token)) {
      req.setAttribute(MonitorUser.MONITOR_USER, MonitorUser.newTokenUser(token));
      return tokenCheck(token, req, resp);
    }

    String accessKey = req.getHeader("accessKey");
    if (!StringUtils.isBlank(accessKey)) {
      if (monitorAccessService.accessCheck(accessKey)) {
        req.setAttribute(MonitorUser.MONITOR_USER, MonitorUser.newTokenUser(accessKey));
        return true;
      }
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          req.getServletPath() + ", accessKey is not existed, " + accessKey);
      log.error(req.getServletPath() + ", accessKey is not existed, " + accessKey);
      return false;
    }

    if (RestAuthUtil.singleton.isNoAuthRequest(req) || !RestAuthUtil.singleton.isAuthRequest(req)) {
      MonitorUser adminUser = MonitorUser.adminUser;
      adminUser.setAuthToken("singleton");
      req.setAttribute(MonitorUser.MONITOR_USER, adminUser);
      return true;
    }

    return userCheck(req, resp);
  }

  public boolean userCheck(HttpServletRequest req, HttpServletResponse resp) {
    try {
      // 判断用户登陆了
      MonitorUser userCookie = MonitorCookieUtil.getUserCookie(req);
      // 这里需要增加一次判断，防止远端session已经释放
      MonitorUser user = ulaFacade.checkLogin(req, resp);
      if (userCookie != null && user != null) {
        // double check 校验用户cookies里面的tenant和 用户信息里面的cookies 是否一致
        String tenantCookie = MonitorCookieUtil.getTenantCookie(req);
        if (StringUtil.isNotBlank(user.getLoginTenant())
            && user.getLoginTenant().equalsIgnoreCase(tenantCookie)
            && (user.getIdentityType() != IdentityType.INNER)) {
          // cookie存在，用户存在
          req.setAttribute(MonitorUser.MONITOR_USER, user);
          return true;
        }
      }
      if (user != null) {
        // 用户是登陆好，设置好，然后返回
        MonitorCookieUtil.addUserCookie(user, resp);
        req.setAttribute(MonitorUser.MONITOR_USER, user);
        return true;
      }
      if (!resp.isCommitted()) {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      }
    } catch (Throwable e) {
      try {
        log.error("login failed", e);
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "login failed");
      } catch (Throwable e1) {
        log.error("login failed and senderror fail", e);
      }
    }
    return false;

  }

  public boolean tokenCheck(String token, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (!(TokenUrlFactoryHolder.checkIsExist(req.getServletPath()))) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          req.getServletPath() + " is not open, please connect monitor admin, " + token);
      log.error(req.getServletPath() + " is not open, please connect monitor admin, " + token);
      return false;
    }
    Boolean aBoolean = monitorAccessService.tokenExpire(token, token_expire_sec);
    if (aBoolean) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          req.getServletPath() + ", token expired, " + token);
      log.error(req.getServletPath() + ", token expired, " + token);
      return false;
    }

    return true;
  }
}
