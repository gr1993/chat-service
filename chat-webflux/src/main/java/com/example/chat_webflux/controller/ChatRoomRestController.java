package com.example.chat_webflux.controller;

import com.example.chat_webflux.dto.ApiResponse;
import com.example.chat_webflux.dto.ChatRoomInfo;
import com.example.chat_webflux.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class ChatRoomRestController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<ChatRoomInfo>>>> getRoomList() {
        return chatRoomService.getRoomList()
                .map(roomList -> ResponseEntity.ok(ApiResponse.ok(roomList)));
    }

    /**
     * WebFlux 환경에서는 x-www-form-urlencoded 일 때 @RequestParam 방식 말고 다른 방식의 body 파싱을 사용하여야 한다.
     */
    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Void>>> createRoom(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String name = formData.getFirst("name");
                    if (name == null || name.isBlank()) {
                        return Mono.just(ResponseEntity
                                .badRequest()
                                .body(ApiResponse.fail("name 파라미터가 필요합니다.")));
                    }

                    return chatRoomService.createRoom(name)
                            .thenReturn(ResponseEntity.ok(ApiResponse.ok()));
                });
    }
}
