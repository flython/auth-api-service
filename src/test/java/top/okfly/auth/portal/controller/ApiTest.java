package top.okfly.auth.portal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

public class ApiTest {

    protected ObjectMapper mapper = new ObjectMapper();
    protected MockMvc mockMvc;
    protected Map<String, Object> headers = new HashMap<>();

    @SneakyThrows
    protected <T> T readData(MvcResult mvcResult, Class<T> clazz) {
        JsonNode respJson = mapper.readTree(mvcResult.getResponse().getContentAsString());
        return mapper.readValue(respJson.get("data").toString(), clazz);
    }

    @SneakyThrows
    protected String readMsg(MvcResult mvcResult) {
        JsonNode respJson = mapper.readTree(mvcResult.getResponse().getContentAsString());
        return respJson.get("msg").asText();
    }

    @SneakyThrows
    protected MvcResult post(Object body, String api) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(api);
        headers.forEach(builder::header);
        return mockMvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    protected MvcResult delete(String api) {
        return delete(api, null);
    }

    @SneakyThrows
    protected MvcResult delete(String api, Map<String, String> values) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(api);
        if (values != null) {
            values.forEach(builder::param);
        }
        headers.forEach(builder::header);
        return mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    protected MvcResult get(String api) {
        return get(api, null);
    }

    @SneakyThrows
    protected MvcResult get(String api, Map<String, String> values) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(api);
        if (values != null) {
            values.forEach(builder::param);
        }
        headers.forEach(builder::header);
        return mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    protected void cacheHeader(String k, Object v) {
        headers.put(k, v);
    }


}
