package com.spring.its_here.domain.area.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.its_here.domain.area.service.AreaService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.config.SecurityConfig;
import com.spring.its_here.global.security.CustomUserDetailsService;
import com.spring.its_here.global.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AreaController.class)
@Import(SecurityConfig.class)
class AreaControllerTest {
    @MockitoBean
    AreaService areaService;

    @MockitoBean
    JwtProvider jwtProvider;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class area_create {
        @ParameterizedTest
        @ValueSource(strings = {"city", "district", "town"})
        @DisplayName("필수 필드가 빈 문자열이면 예외")
        void field_duplicate(String missingField) throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("city", "서울특별시");
            request.put("district", "강남구");
            request.put("town", "역삼동");
            request.put(missingField, "");

            mockMvc.perform(post("/api/areas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
                    .andExpect(jsonPath("$.details." + missingField).exists());

            verifyNoMoreInteractions(areaService);
        }
    }
}