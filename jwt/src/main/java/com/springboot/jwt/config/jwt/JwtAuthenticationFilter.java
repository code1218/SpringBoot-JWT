package com.springboot.jwt.config.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.jwt.config.auth.PrincipalDetails;
import com.springboot.jwt.model.User;

import lombok.RequiredArgsConstructor;

/*
 * 스프링 시큐리티에 UsernamePasswordAuthenticationFilter가 있음
 * login post 요청으로 username, password 전송
 * 그 때 UsernamePasswordAuthenticationFilter가 실행됨
 */

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	
	//login요청을 하면 로그인 시도를 위해서 실행되는 메소드
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("로그인 시도");
		
		//1. username, password 받아서
		try {
			/*
			 * BufferedReader br = request.getReader();
			 * 
			 * String input = null;
			 * 
			 * while((input = br.readLine()) != null) { System.out.println(input); }
			 * System.out.println(request.getInputStream().toString());
			 */
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			System.out.println(user);
			
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			//PrincipalDetailsService loadUserByUsername()이 실행됨
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
			System.out.println("로그인 성공: " + principalDetails.getUser());
			
			return authentication;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		//2. 정상인지 로그인 시도를 해봄.
		//authenticationManager로 로그인 시도하면 principalDetailsService의 loadUserByUsername이 실행됨
		
		//3. PrincipalDetails를 세션에 담아준다(권한 관리를 위해)
		
		//4. Jwt를 생성해서 응답
	}
	
	//위의 attemptAuthentication실행 후 인증이 정상적으로 완료되면 실행됨
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("인증이 완료됨");
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		
		String jwt = JWT.create()
				.withSubject("test")
				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
		
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.SECRET + jwt);
	}
}
