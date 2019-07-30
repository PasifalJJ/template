package com.jsc.exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExceptionHandler implements HandlerExceptionResolver {
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv=new ModelAndView();
        if (ex instanceof MyException){
            mv.addObject("error",((MyException) ex).getMsg());
            mv.setViewName("error");
        }else {
            mv.addObject("error","服务器正忙~ ~ ~");
            mv.setViewName("error");
        }
        return mv;
    }
}
