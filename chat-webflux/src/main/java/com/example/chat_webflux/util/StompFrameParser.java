package com.example.chat_webflux.util;

public class StompFrameParser {

    /**
     * STOMP 메시지의 커맨드를 추출
     */
    public static String getCommand(String stompMessage) {
        if (stompMessage == null || stompMessage.isBlank()) {
            return "HEARTBEAT";
        }
        int commandEndIndex = stompMessage.indexOf('\n');
        if (commandEndIndex > 0) {
            return stompMessage.substring(0, commandEndIndex);
        }
        return "UNKNOWN";
    }

    /**
     * STOMP 메시지에서 특정 헤더 값을 추출
     */
    public static String getHeader(String stompMessage, String headerName) {
        // 메시지 헤더 부분만 추출
        int headersEndIndex = stompMessage.indexOf("\n\n");
        if (headersEndIndex == -1) {
            return null; // 헤더가 없는 경우
        }
        String headersPart = stompMessage.substring(0, headersEndIndex);
        String[] lines = headersPart.split("\n");

        for (String line : lines) {
            if (line.startsWith(headerName + ":")) {
                return line.substring(headerName.length() + 1);
            }
        }
        return null;
    }

    /**
     * STOMP 메시지에서 바디를 추출
     */
    public static String getBody(String stompMessage) {
        int bodyStartIndex = stompMessage.indexOf("\n\n") + 2;
        if (bodyStartIndex == 1) { // "\n\n"이 없는 경우
            return null;
        }
        int bodyEndIndex = stompMessage.indexOf('\0', bodyStartIndex);
        if (bodyEndIndex > bodyStartIndex) {
            return stompMessage.substring(bodyStartIndex, bodyEndIndex);
        }
        return null;
    }

    /**
     * 메시지 전송용 STOMP 프레임 생성
     */
    public static String createStompMessageFrame(String destination, String jsonBody) {
        return "MESSAGE\n" +
                "destination:" + destination + "\n" +
                "content-type:application/json;charset=UTF-8\n" +
                "\n" +
                jsonBody + "\n\0";
    }
}
