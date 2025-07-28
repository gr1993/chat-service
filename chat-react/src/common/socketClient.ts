import SockJS from "sockjs-client";
import { CompatClient, Stomp } from "@stomp/stompjs";

const SOCKET_URL = import.meta.env.VITE_API_URL + "/ws";
let sharedClient: CompatClient | null = null;

export function connectWebSocket(callback?: (client: CompatClient, sessionId: string) => void) {
  if (sharedClient) return sharedClient;

  const socket = new SockJS(SOCKET_URL);
  const client = Stomp.over(socket);

  client.connect({}, () => {
    console.log("STOMP 연결됨");
    sharedClient = client;

    // SockJS 객체에서 직접 세션 ID를 추출
    const url = (socket as any)._transport.url;
    const parts = url.split('/');
    const extractedSessionId = parts[parts.length - 2];

    callback?.(client, extractedSessionId);
  });

  return client;
}

export function getWebSocketClient(): CompatClient | null {
  return sharedClient;
}

export function disconnectWebSocket() {
  if (sharedClient?.connected) {
    sharedClient.disconnect(() => {
      console.log("STOMP 연결 종료");
      sharedClient = null;
    });
  }
}