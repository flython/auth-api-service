package top.okfly.auth.portal.controller.advice;

import lombok.Data;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "top.okfly.auth.portal.controller")
public class CommonRespWrapper implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof CommonResp) {
            return body;
        } else {
            var resp = new CommonResp();
            resp.setData(body);
            return resp;
        }
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public CommonResp parseException(Exception exception) {
        var resp = new CommonResp();
        resp.setMsg(exception.getMessage());
        return resp;
    }

    @Data
    public static class CommonResp {
        String msg = "ok";
        Object data;
    }


}