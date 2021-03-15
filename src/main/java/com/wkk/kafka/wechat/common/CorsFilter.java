package com.wkk.kafka.wechat.common;

import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.LogRecord;

/**
 * @author weikunkun
 * @since 2021/3/15
 */
@WebFilter(filterName = "CorsFilter")
@Configuration
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Controller-Allow-Origin", "*");
        response.setHeader("Access-Controller-Allow-Credentials", "true");
        response.setHeader("Access-Controller-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Controller-Max-Age", "3600");
        response.setHeader("Access-Controller-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        filterChain.doFilter(servletRequest, servletResponse);

    }
}
