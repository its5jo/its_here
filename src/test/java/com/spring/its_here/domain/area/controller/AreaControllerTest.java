package com.spring.its_here.domain.area.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.service.AreaService;
import com.spring.its_here.global.config.SecurityConfig;
import com.spring.its_here.global.security.CustomUserDetailsService;
import com.spring.its_here.global.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.junit.jupiter.api.Assertions.assertInstanceOf;


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

    UUID areaId = UUID.randomUUID();

    @Nested
    class area_create {
        @Test
        @WithMockUser(authorities = "MANAGER")
        @DisplayName("지역생성 manager 권한이면 성공")
        void area_manager_success() throws Exception {
            AreaCreateRequestDto areaCreateRequestDto = new AreaCreateRequestDto(
                    "city",
                    "district",
                    "town"
            );
            given(areaService.createArea(any(AreaCreateRequestDto.class))).willReturn(new AreaCreateResponseDto(areaId, false));

            mockMvc.perform(post("/api/areas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(areaCreateRequestDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(authorities = "CUSTOMER")
        @DisplayName("CUSTOMER 권한이면 지역 생성에 실패한다")
        void create_fail_customer() throws Exception {
            AreaCreateRequestDto areaCreateRequestDto = new AreaCreateRequestDto(
                    "city",
                    "district",
                    "town"
            );
            mockMvc.perform(post("/api/areas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(areaCreateRequestDto)))
                    .andExpect(result ->
                            assertInstanceOf(
                                    AuthorizationDeniedException.class,
                                    result.getResolvedException()
                            ));
            then(areaService).shouldHaveNoInteractions();
        }

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
                    .andExpect(result ->
                            assertInstanceOf(
                                    MethodArgumentNotValidException.class,
                                    result.getResolvedException()
                            ));
            verifyNoMoreInteractions(areaService);
        }
    }
}