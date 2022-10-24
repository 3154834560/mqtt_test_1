package com.example.demo.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author 王景阳
 * @date 2022/10/19 21:07
 */
@Slf4j
@Component
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("{} ---> {}", request.getMethod(), request.getRequestURL().toString());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
