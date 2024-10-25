package com.xs.config;

import com.xs.service.impl.AdminServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private AdminServiceImpl adminService;

	@Resource
	private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new MyPasswordEncoder();
	}

	public static void main(String[] args) {

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(adminService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				//禁用 csrf 防御
				.csrf().disable()
				//开启跨域支持
				.cors().and()
				//基于Token，不创建会话
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				//任何 /admin 开头的路径下的请求都需要经过JWT验证
				.antMatchers("/admin/**").authenticated()
				//其它路径全部放行
				.anyRequest().permitAll()
				.and()
				.headers().frameOptions().disable()
				.and()
				//自定义JWT过滤器
				.addFilterBefore(new JwtLoginFilter("/admin/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
				//未登录时，返回json，在前端执行重定向
				.exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint);

	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
