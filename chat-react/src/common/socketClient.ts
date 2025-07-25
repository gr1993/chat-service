import SockJS from "sockjs-client";
import { CompatClient, Stomp } from "@stomp/stompjs";

const SOCKET_URL = import.meta.env.VITE_API_URL + "/ws";
let sharedClient: CompatClient | null = null;

export function connectWebSocket(callback?: (client: CompatClient) => void) {
  if (sharedClient) return sharedClient;

  const socket = new SockJS(SOCKET_URL);
  const client = Stomp.over(socket);

  client.connect({}, () => {
    console.log("STOMP 연결됨");
    sharedClient = client;
    callback?.(client);
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
    });
  }
}