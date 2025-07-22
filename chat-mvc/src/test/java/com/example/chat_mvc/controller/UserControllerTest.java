package com.example.chat_mvc.controller;

import com.example.chat_mvc.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @Test
    void entryUser_성공() throws Exception {
        //when
        ResultActions mvcAction = mockMvc.perform(post("/api/user/entry/abc"));

        //then
        mvcAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청 성공"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andReturn();

        verify(userService).entryUser("abc");
    }

    @Test
    void entryUser_실패_아이디중복() throws Exception {
        //given
        String errorMsg = "이미 존재하는 아이디입니다.";
        doThrow(new IllegalArgumentException(errorMsg))
                .when(userService).entryUser(any());

        //when
        ResultActions mvcAction = mockMvc.perform(post("/api/user/entry/abc"));

        //then
        mvcAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMsg))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andReturn();
    }

}
