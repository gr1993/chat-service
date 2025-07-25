import { useEffect, useRef } from "react";
import type { CompatClient } from "@stomp/stompjs";
import { connectWebSocket, disconnectWebSocket } from "@/common/socketClient";

export function useWebSocket(onConnect?: (client: CompatClient) => void) {
  const clientRef = useRef<CompatClient | null>(null);
  const didConnect = useRef(false); 

  useEffect(() => {

    if (didConnect.current) {
      return;
    }

    didConnect.current = true;

    const client = connectWebSocket((connectedClient) => {
      clientRef.current = connectedClient;
      onConnect?.(connectedClient);
    });

    if (client) {
      clientRef.current = client;
    }

    return () => {
      if (clientRef.current) {
        disconnectWebSocket();
        clientRef.current = null;
      }
    };
    
  }, []);

  return clientRef;
}