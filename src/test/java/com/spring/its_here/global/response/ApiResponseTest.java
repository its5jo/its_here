package com.spring.its_here.global.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Map;


@WebMvcTest(ApiResponseTestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiResponseTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void 성공_응답_JSON_형태를_확인한다() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 생성 성공"))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.reviewId").value(1));
    }
}

@RestController
class ApiResponseTestController {
    @GetMapping("/test/success")
    ResponseEntity<ApiResponse<Map<String, Long>>> success() {
        return ResponseEntity.ok(
                ApiResponse.success("리뷰 생성 성공", Map.of("reviewId", 1L))
        );
    }
}