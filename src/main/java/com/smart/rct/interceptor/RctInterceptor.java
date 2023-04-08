package com.smart.rct.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.GlobalStatusMap;

@Component
public class RctInterceptor extends HandlerInterceptorAdapter {

	final static Logger logger = LoggerFactory.getLogger(RctInterceptor.class);

	User user;
	HttpSession session;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("pre Handle Interceptor.");
		try {
			session = request.getSession(false);
			// First time login
			if (session == null) {
				return true;
			}
			// AFTER login, Requests for a user will get redirected here
			if (session.getAttribute("userName") != null) {
				user = (User) session.getAttribute("userName");
				user.setLastAccessedTime(session.getLastAccessedTime());
				//GlobalStatusMap.loginUsersDetails.put(user.getTokenKey(), user);
				// Check timeout
				if (System.currentTimeMillis()
						- session.getLastAccessedTime() > GlobalInitializerListener.MAX_INACTIVE_SESSION_TIMEOUT) {

					logger.error("SESSION HAS EXPIRED!!! Please login again");

					session.removeAttribute("userName");
					session.invalidate();
					request.logout();

					response.setHeader("Cache-Control", "no-cache"); // Forces
																		// caches
																		// to
																		// obtain
																		// a new
																		// copy
																		// of
																		// the
																		// page
																		// from
																		// the
																		// origin
																		// server
					response.setHeader("Cache-Control", "no-store"); // Directs
																		// caches
																		// not
																		// to
																		// store
																		// the
																		// page
																		// under
																		// any
																		// circumstance
					response.setDateHeader("Expires", 0); // Causes the proxy
															// cache to see the
															// page as "stale"
					response.setHeader("Pragma", "no-cache"); // HTTP 1.0
																// backward
																// compatibility
					UserSessionPool.getInstance().removeUser(user);
					return true;
				} else {
					session.setMaxInactiveInterval((int) GlobalInitializerListener.MAX_INACTIVE_SESSION_TIMEOUT);
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Exception preHandle : " + ExceptionUtils.getFullStackTrace(e));
			return true;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info("post Handle Interceptor.");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		logger.info("afterCompletion Interceptor.");
	}

}
