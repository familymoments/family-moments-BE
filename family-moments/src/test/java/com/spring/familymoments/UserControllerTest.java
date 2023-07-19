package com.spring.familymoments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.familymoments.domain.user.UserController;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.model.PostUserReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    MockMvc mockMvc;
    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("유저 생성")
    public void saveUserTest() throws Exception {
        /*LocalDateTime today = LocalDateTime.now();
        PostUserReq postUserReq = new PostUserReq(null, "spacewalk0", "정유영", "ju001217@naver.com", today, "융입니다", "");
        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUserReq)))
                        .andExpect(status().isOk())
                        .andDo(print());
        //verify(userService).createUser(postUserReq);*/
    }
}
