package com.example.chat_mvc.controller;

import com.example.chat_mvc.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomRestController.class)
public class ChatRoomRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatRoomService chatRoomService;


    @Test
    void getRoomList_성공() throws Exception {
        //when
        ResultActions mvcAction = mockMvc.perform(get("/api/room"));

        //then
        mvcAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andReturn();

        verify(chatRoomService).getRoomList();
    }

    @Test
    void createRoom_성공() throws Exception {
        //given
        String name = "park";

        //when
        ResultActions mvcAction = mockMvc.perform(
                post("/api/room")
                        .param("name", name)
        );

        //then
        mvcAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청 성공"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andReturn();

        verify(chatRoomService).createRoom(name);
    }

    @Test
    void enterRoom_성공() throws Exception {
        //given
        Long roomId = 1L;
        String userId = "park";

        //when
        ResultActions mvcAction = mockMvc.perform(
                post("/api/room/" + roomId + "/enter")
                        .param("userId", userId)
        );

        //then
        mvcAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청 성공"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andReturn();

        verify(chatRoomService).enterRoom(roomId, userId);
    }

    @Test
    void exitRoom_성공() throws Exception {
        //given
        Long roomId = 1L;
        String userId = "park";

        //when
        ResultActions mvcAction = mockMvc.perform(
                post("/api/room/" + roomId + "/exit")
                        .param("userId", userId)
        );

        //then
        mvcAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청 성공"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andReturn();

        verify(chatRoomService).exitRoom(roomId, userId);
    }
}
