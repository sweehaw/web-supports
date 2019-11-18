package io.github.sweehaw.websupports.resolver;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sweehaw.websupports.annotation.JsonArg;
import io.github.sweehaw.websupports.annotation.JsonHeader;
import io.github.sweehaw.websupports.util.CommUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;


/**
 * @author sweehaw
 */
public class ServletModelAttributeResolver implements HandlerMethodArgumentResolver {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String JSONBODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonArg.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer viewContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        try {

            String className = parameter.getParameterType().getName();

            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getConstructor();
            Object object = constructor.newInstance();
            HashMap<String, String> pathVariable = this.getPathVariable(webRequest);
            object = this.getRequestBody(object, parameter, webRequest);

            List<Field> fields = new ArrayList<>(Arrays.asList(c.getDeclaredFields()));
            List<Field> superFields = new ArrayList<>(Arrays.asList(c.getSuperclass().getDeclaredFields()));
            fields.addAll(superFields);

            for (Field f : fields) {

                this.setPathVariable(f, object, pathVariable);
                this.setParameterValue(f, object, webRequest);
                this.setServletRequest(f, object, webRequest);
                this.setHeaderValue(f, object, webRequest);
                ResolverTools.setToUpperCase(f, object);
            }

            return object;

        } catch (Exception ex) {
            this.logger.error(ex.toString(), ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, String> getPathVariable(NativeWebRequest webRequest) {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return (HashMap<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    private Object getRequestBody(Object o, MethodParameter parameter, NativeWebRequest webRequest) {

        try {

            HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
            String jsonBody = (String) webRequest.getAttribute(JSONBODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);

            if (CommUtils.parseString(jsonBody) == null) {
                jsonBody = CommUtils.getStringFromInputStream(servletRequest.getInputStream());
                webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            return objectMapper.readValue(jsonBody, o.getClass());
        } catch (Exception ex) {
            return o;
        }
    }

    private void setPathVariable(Field f, Object o, HashMap<String, String> map) throws Exception {

        JsonProperty jsonProperty = f.getAnnotation(JsonProperty.class);

        if (jsonProperty != null) {

            String jsonKey = jsonProperty.value();
            Object value = map.get(jsonKey);

            if (CommUtils.parseString(value) != null) {

                f.setAccessible(true);

                if (f.getType().isAssignableFrom(String.class)) {
                    f.set(o, CommUtils.parseString(value));

                } else if (f.getType().isAssignableFrom(Integer.class)) {
                    f.set(o, CommUtils.parseInt(value));

                } else if (f.getType().isAssignableFrom(Long.class)) {
                    f.set(o, CommUtils.parseLong(value));

                }
            }
        }
    }

    private void setServletRequest(Field f, Object o, NativeWebRequest nativeWebRequest) throws Exception {

        if (f.getType().isAssignableFrom(HttpServletRequest.class)) {
            HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
            f.setAccessible(true);
            f.set(o, request);
        } else if (f.getType().isAssignableFrom(HttpServletResponse.class)) {
            HttpServletResponse response = (HttpServletResponse) nativeWebRequest.getNativeResponse();
            f.setAccessible(true);
            f.set(o, response);
        }
    }

    private void setHeaderValue(Field f, Object o, NativeWebRequest nativeWebRequest) throws Exception {

        JsonHeader jsonHeader = f.getAnnotation(JsonHeader.class);

        if (jsonHeader != null) {
            String jsonKey = jsonHeader.value();
            String headerValue = nativeWebRequest.getHeader(jsonKey);
            f.setAccessible(true);
            f.set(o, headerValue);
        }
    }

    private void setParameterValue(Field f, Object o, NativeWebRequest nativeWebRequest) throws Exception {

        JsonProperty jsonProperty = f.getAnnotation(JsonProperty.class);
        JsonFormat jsonFormat = f.getAnnotation(JsonFormat.class);

        if (jsonProperty != null) {

            String jsonKey = jsonProperty.value();
            Object value = nativeWebRequest.getParameter(jsonKey);

            if (CommUtils.parseString(value) != null) {

                f.setAccessible(true);

                if (f.getType().isAssignableFrom(String.class)) {
                    f.set(o, CommUtils.parseString(value));

                } else if (f.getType().isAssignableFrom(Integer.class)) {
                    f.set(o, CommUtils.parseInt(value));

                } else if (f.getType().isAssignableFrom(BigDecimal.class)) {
                    f.set(o, CommUtils.parseBigDecimal(value));

                } else if (f.getType().isAssignableFrom(Date.class) && jsonFormat != null) {
                    f.set(o, CommUtils.parseDate(jsonFormat.pattern(), value.toString()));

                } else if (f.getType().isAssignableFrom(Long.class)) {
                    f.set(o, CommUtils.parseLong(value));

                }
            }
        }
    }
}