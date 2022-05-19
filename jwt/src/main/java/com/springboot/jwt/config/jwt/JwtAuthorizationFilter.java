package com.springboot.jwt.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.springboot.jwt.config.auth.PrincipalDetails;
import com.springboot.jwt.model.User;
import com.springboot.jwt.repository.UserRepository;

/*
 * 시큐리티가 filter가지고 있는데 그 중에 BasicAuthenticationFilter가 있음
 * 권한이나 인증이필요한  특정 주소를 요청했을 때 위 필터를 무조건 탄다.
 * 마약 권한이 인증이 필요한 주소가 아니라면 이 필터를 안탄다.
 */

public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	
	private UserRepository userRepository;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		//super.doFilterInternal(request, response, chain);
		System.out.println("인증이 필요한 요청이 들어옴");
		
		String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
		System.out.println(jwtHeader);
		
		//header가 있는지 확인
		if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken).getClaim("username").asString();
		
		if(username != null) {
			User userEntity = userRepository.findByUsername(username);
			PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
			//JWT토큰 서명을 통해서 서명이 정상이면 Authentication객체를 만들어준다.
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
			
			//강제로 시큐리티의 세션에 접근해서 세션을 저장해준다.
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			chain.doFilter(request, response);
		}
	}
}
