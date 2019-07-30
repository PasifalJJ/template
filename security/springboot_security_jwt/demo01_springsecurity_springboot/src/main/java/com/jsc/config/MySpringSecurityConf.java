package com.jsc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启 SpringSecurity注解
public class MySpringSecurityConf extends WebSecurityConfigurerAdapter {

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
                .authorizeRequests() //配置的拦截器：ChannelProcessingFilter
                .antMatchers("/static/**").permitAll() //设置静态资源可以匿名访问
                //.antMatchers("/templates/**").permitAll()
                .anyRequest()
                .hasAnyRole("USER")
                   //设置网页认证请求，anyRequest()表示所有请求都需要验证
                .and()
                .formLogin() //配置的拦截器：AuthenticationProcessingFilter 主要用于登录设置
                .passwordParameter("password")
                .usernameParameter("username")
                .loginPage("/login") //指定登录url,没登陆用于默认跳转这个页面,这是通过Controller进行跳转
                .loginProcessingUrl("/loginAuth") //指定表单提交路径
                .failureUrl("/fail") //登录失败跳转页面
                .permitAll() //上述两个路径允许匿名访问
                .defaultSuccessUrl("/index") //登录成功条状页面，可设置跳转登录是否来这个页面

                .and()
                .logout() //开启注销 配置的拦截器：LogoutFilter 主要用于退出
                .logoutUrl("/logout") //指定注销请求路径
                .logoutSuccessUrl("/login") //指定注销成功后跳转路径
                .permitAll(); //设置注销路径可以匿名访问
    }
}
