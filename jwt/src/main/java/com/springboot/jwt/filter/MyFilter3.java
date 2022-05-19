package com.springboot.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter3 implements Filter{
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		/*
		 * 토큰: 정상적인 로그인이 완료되면 jwt를 만들어서 응답해줘야함
		 * 요청 할 때마다 header에 Authorization에 value값으로 토큰을 넘겨줘야함
		 * 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지를 검증해야함(RSA, HS256)
		 * 
		 */
		
		if(req.getMethod().equals("POST")) {
			System.out.println("POST요청됨");
			String headerAuth = req.getHeader("Authorization");
			System.out.println(headerAuth);
			
			if(headerAuth.equals("junil")) {
				chain.doFilter(request, response);
			}else {
				System.out.println("필터3");
				PrintWriter out = res.getWriter();
				out.println("인증안됨");
			}
		}
		
		
	}
}
