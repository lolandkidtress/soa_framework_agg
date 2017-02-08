package com.James.Filter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.MonitorHandle.trackingHandle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HTTP方式的调用链日志框架的过滤器
 * 
 * @author James
 * @since 1.0
 * @version 1.0
 */
@WebFilter(filterName = "httpTrackFilter_0", urlPatterns = { "/*" }, asyncSupported = true)
public class httpTrackFilter implements Filter {
	private static final Log logger = LogFactory.getLog(httpTrackFilter.class.getName());
	private trackingHandle track = new trackingHandle();

	public void doFilter(ServletRequest srequset, ServletResponse sresponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequset;
		HttpServletResponse response = (HttpServletResponse) sresponse;
		try {
			track.doFilter(request, response);
		} catch (Exception e) {
			logger.error("调用链初始化失败", e);
		}
		filterChain.doFilter(request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
}