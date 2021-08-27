///**
// * 
// */
//package com.international.codyweb.filter;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import com.netflix.zuul.exception.ZuulException;
//
///**
// * @author Cody Hoang
// *
// */
//@Configuration
//public class SimpleFilter extends ZuulFilter {
//	private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);
//	
//	@Override
//	public boolean shouldFilter() {
//		return true;
//	}
//
//	@Override
//	public Object run() throws ZuulException {
//		RequestContext ctx = RequestContext.getCurrentContext();
//	    HttpServletRequest request = ctx.getRequest();
//
//	    log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
//		return null;
//	}
//
//	@Override
//	public String filterType() {
//		return "pre";
//	}
//
//	@Override
//	public int filterOrder() {
//		return 1;
//	}
//	
//	
//}
