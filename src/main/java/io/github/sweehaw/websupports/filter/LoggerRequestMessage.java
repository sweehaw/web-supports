package io.github.sweehaw.websupports.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sweehaw
 */
class LoggerRequestMessage {

    String getUrl(LoggerRequestWrapper request) {

        String url = request.getRequestURL().toString();
        url = url.replace("%7B", "{");
        url = url.replace("%7D", "}");

        return url;
    }

    String getIp(LoggerRequestWrapper request) {
        return request.getRemoteAddr();
    }

    String getMethod(LoggerRequestWrapper request) {
        return request.getMethod();
    }

    Object getHeader(LoggerRequestWrapper request) {

        String s = "authorization;content-length;content-type;accept;";
        HashMap<String, String> m = new HashMap<>(0);

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String k = headerNames.nextElement();
                String v = request.getHeader(k);
                if (s.contains(k)) {
                    m.put(k, v);
                }
            }
        }

        return m;
    }

    Object getBody(LoggerRequestWrapper request) {

        String line = "";
        String[] excludeParam = new String[]{"password", "secureCode"};

        try {
            line = IOUtils.toString(request.getReader());
            HashMap map = new ObjectMapper().readValue(line, HashMap.class);
            map.forEach((k, v) -> {
                if (Arrays.stream(excludeParam).anyMatch(s -> s.equalsIgnoreCase(k.toString()))) {
                    map.remove(k);
                }
            });
            return map;
        } catch (IOException e) {
            return line.isEmpty() ? line : getSerializeBody(line, excludeParam);
        }
    }

    String getSerializeBody(String line, String[] excludeParam) {
        try {
            Map<String, String> map = new HashMap<>();
            String ss = URLDecoder.decode(line, "UTF-8");
            String[] params = ss.split("&");

            Arrays.asList(params).forEach(p -> {

                String[] param = p.split("=");
                String k = param.length > 0 ? param[0] : "";
                String v = param.length > 1 ? param[1] : "";

                if (Arrays.stream(excludeParam).noneMatch(s -> s.equalsIgnoreCase(k))) {
                    map.put(k, v);
                }
            });
            return new ObjectMapper().writeValueAsString(map);
        } catch (Exception ex) {
            return "";
        }
    }

    String getParam(LoggerRequestWrapper request) {
        return request.getQueryString();
    }
}
