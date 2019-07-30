package com.jsc.config;

import com.jsc.security.*;
import com.jsc.security.provider.SelfAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启 SpringSecurity注解\
@EnableWebSecurity
public class MySpringSecurityConf extends WebSecurityConfigurerAdapter {

    @Autowired
    private AjaxAuthenticationEntryPoint authenticationEntryPoint; //未登录访问权限资源时返回json数据给前端

    @Autowired
    private AjaxAuthenticationSuccessHandler authenticationSuccessHandler; //登录成功处理器

    @Autowired
    private AjaxAuthenticationFailureHandler authenticationFailureHandler; //登录失败处理器

    @Autowired
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler; //退出登录处理器

    @Autowired
    private AjaxAccessDeniedHandler accessDeniedHandler; //// 无权访问返回的 JSON 格式数据给前端（否则为 403 html 页面）

    @Autowired
    private SelfAuthenticationProvider authenticationProvider; // 自定义安全认证提供者


    /**
     * 认证管理器
     * 1、直接传入用户名密码存入内存当中
     * 2、传入认证管理器
     * 3、传入认证提供者
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置默认嫩能够访问的对象，无需从数据库中进行查询
        auth.inMemoryAuthentication()
                .withUser("qqq")
                .password("{noop}123")
                .roles("USER");
        auth.inMemoryAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())
                .withUser("小明")
                .password(new BCryptPasswordEncoder().encode("123456"))
                .roles("USER");
        // 传入自定义安全认证提供者
        auth.authenticationProvider(authenticationProvider);
    }

    /**
     * 安全核心配置类，可以添加其他的过滤器并设置其它的过滤器
     * 每个and之间都是一个过滤器，用来完成指定的过滤任务
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                .and()

                .authorizeRequests() //配置的拦截器：ChannelProcessingFilter
                .antMatchers("/static/**").permitAll() //设置静态资源可以匿名访问
                .anyRequest() //设置网页认证请求，anyRequest()表示所有请求都需要验证
                .authenticated() //其他url需要身份认证

                .and()
                .formLogin() //配置的拦截器：AuthenticationProcessingFilter 主要用于登录设置
                .passwordParameter("password")
                .usernameParameter("username")
                .loginPage("/login") //指定登录url,没登陆用于默认跳转这个页面,这是通过Controller进行跳转
                .loginProcessingUrl("/loginAuth") //指定表单提交路径
                .permitAll()
                .failureHandler(authenticationFailureHandler) //登录失败处理器
                .successHandler(authenticationSuccessHandler) //登录成功处理器
                .and()

                .logout() //开启注销 配置的拦截器：LogoutFilter 主要用于退出
                .logoutUrl("/logout").permitAll() //指定注销请求路径
                .logoutSuccessHandler(ajaxLogoutSuccessHandler) //登出成功处理器
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler); //无权访问处理器
    }
}
