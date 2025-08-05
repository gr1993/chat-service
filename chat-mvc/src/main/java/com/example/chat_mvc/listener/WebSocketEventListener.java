package com.example.chat_mvc.listener;

import com.example.chat_mvc.common.RoomUserSessionManager;
import com.example.chat_mvc.dto.WebSocketRoomUser;
import com.example.chat_mvc.service.ChatRoomService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RoomUserSessionManager roomUserSessionManager;
    private final ChatRoomService chatRoomService;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    /**
     * 웹소켓 Connect 이벤트가 발생했을 때의 로직
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        activeConnections.incrementAndGet();
    }

    /**
     * 웹소켓 Disconnect 이벤트가 발생했을 때의 로직
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();

        WebSocketRoomUser roomUser = roomUserSessionManager.removeUserSession(sessionId);
        if (roomUser != null) {
            // 해당 사용자 퇴장 처리
            chatRoomService.exitRoom(roomUser.getRoomId(), roomUser.getUserId());
            activeConnections.decrementAndGet();
        }
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
        return meterRegistry -> meterRegistry.gauge("chat_app_active_connections", activeConnections);
    }
}
