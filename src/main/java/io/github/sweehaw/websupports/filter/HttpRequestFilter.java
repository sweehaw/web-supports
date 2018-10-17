package io.github.sweehaw.websupports.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sweehaw.websupports.util.CommUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sweehaw
 */
@Slf4j
@Component
public class HttpRequestFilter extends GenericFilterBean {

    private List<String> filterList = new ArrayList<>();
    private List<String> responseFilterList = new ArrayList<>();
    private boolean printRequest;
    private boolean printResponse;

    public HttpRequestFilter(boolean printRequest, boolean printResponse) {
        this.printRequest = printRequest;
        this.printResponse = printResponse;
    }

    public void setFilterList(String... filterList) {
        this.filterList.addAll(Arrays.asList(filterList));
    }

    public void setFilterList(List<String> filterList) {
        this.filterList.addAll(filterList);
    }

    public void setResponseFilterList(String... responseFilterList) {
        this.responseFilterList.addAll(Arrays.asList(responseFilterList));
    }

    public void setResponseFilterList(List<String> responseFilterList) {
        this.responseFilterList.addAll(responseFilterList);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (this.checkUrlPattern(request.getRequestURI(), this.filterList)) {

            LoggerRequestWrapper loggerRequestWrapper = new LoggerRequestWrapper(request);
            LoggerRequestMessage loggerRequestMessage = new LoggerRequestMessage();
            LoggerResponseWrapper loggerResponseWrapper = new LoggerResponseWrapper(response);

            String randomString = this.getRandomString(request);
            this.requestLogger(loggerRequestMessage, loggerRequestWrapper, randomString);

            loggerRequestWrapper.resetInputStream();
            if (!this.isRandomStringExist(request)) {
                loggerRequestWrapper.addRandomString(randomString);
            }

            chain.doFilter(loggerRequestWrapper, loggerResponseWrapper);
            this.responseLogger(request, response, loggerResponseWrapper, randomString);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void requestLogger(LoggerRequestMessage loggerRequestMessage, LoggerRequestWrapper loggerRequestWrapper, String randomString) throws JsonProcessingException {

        if (this.printRequest) {
            ObjectMapper m = new ObjectMapper();

            String url = loggerRequestMessage.getUrl(loggerRequestWrapper);
            String ip = loggerRequestMessage.getIp(loggerRequestWrapper);
            String method = loggerRequestMessage.getMethod(loggerRequestWrapper);
            Object header = loggerRequestMessage.getHeader(loggerRequestWrapper);
            Object param = loggerRequestMessage.getParam(loggerRequestWrapper);
            Object body = loggerRequestMessage.getBody(loggerRequestWrapper);

            log.info("");
            log.info("{} ====================================== Incoming ======================================", randomString);
            log.info("{} U: {}", randomString, url);
            log.info("{} M: {}", randomString, method);
            log.info("{} I: {}", randomString, ip);
            log.info("{} H: {}", randomString, m.writeValueAsString(header));
            log.info("{} P: {}", randomString, param);
            log.info("{} B: {}", randomString, body instanceof String ? body : m.writeValueAsString(body));
        }
    }

    private void responseLogger(HttpServletRequest request, HttpServletResponse response, LoggerResponseWrapper loggerResponseWrapper, String randomString) throws IOException {

        if (this.printResponse && this.checkUrlPattern(request.getRequestURI(), this.responseFilterList)) {
            loggerResponseWrapper.flushBuffer();
            byte[] copy = loggerResponseWrapper.getCopy();
            log.info("{} R: {}", randomString, new String(copy, response.getCharacterEncoding()));
        }
    }

    private String getRandomString(HttpServletRequest request) {
        return this.isRandomStringExist(request)
                ? request.getHeader("randomString")
                : CommUtils.randomAlphanumeric();
    }

    private boolean isRandomStringExist(HttpServletRequest request) {
        return request.getHeader("randomString") != null;
    }

    private boolean checkUrlPattern(String uri, List<String> filterList) {
        return filterList.size() == 0 || filterList
                .stream()
                .anyMatch(pattern -> this.filterUrl(uri, pattern));
    }

    private boolean filterUrl(String uri, String pattern) {

        String oneStar = "[a-zA-Z0-9_]*";
        String twoStar = "[a-zA-Z0-9_/]+";

        pattern = pattern.replace("**", twoStar);
        pattern = pattern.replace("*", oneStar);

        return uri.matches(pattern);
    }
}
