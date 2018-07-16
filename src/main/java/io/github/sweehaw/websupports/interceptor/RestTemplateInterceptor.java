package io.github.sweehaw.websupports.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author sweehaw
 */
@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        List<String> list = request.getHeaders().getValuesAsList("randomString");

        String randomString = !list.isEmpty() ? list.get(0) : RandomStringUtils.randomNumeric(8);
        this.logRequest(request, body, randomString);

        ClientHttpResponse response = execution.execute(request, body);
        this.logResponse(response, randomString);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body, String randomString) throws UnsupportedEncodingException {

        log.info("");
        log.info("{} ====================================== Outgoing ======================================", randomString);
        log.info("{} U: {}", randomString, request.getURI().toString());
        log.info("{} M: {}", randomString, request.getMethod().toString());
        log.info("{} B: {}", randomString, new String(body, "UTF-8"));
    }

    private void logResponse(ClientHttpResponse response, String randomString) throws IOException {

        String statusCode = response.getRawStatusCode() + "";
        String statusText = response.getStatusText();

        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }

        log.info("{} S: {} {}", randomString, statusCode, statusText);
        log.info("{} R: {}", randomString, inputStringBuilder.toString());
    }
}
