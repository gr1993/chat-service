import { useEffect, useRef } from "react";
import type { CompatClient } from "@stomp/stompjs";
import { getWebSocketClient } from "@/common/socketClient";

const ESCAPE_STRICT_MODE = import.meta.env.VITE_ESCAPE_STRICT_MODE;

export function useWebSocket(onConnect?: (client: CompatClient) => void) {
  const clientRef = useRef<CompatClient | null>(null);
  const retryTimerRef = useRef<NodeJS.Timeout | null>(null);
  const isMounted = useRef(false);

  useEffect(() => {
    if (ESCAPE_STRICT_MODE !== 'Y' || isMounted.current) {
      let retryCount = 0;
      const interval = 100;

      const tryConnect = () => {
        const client = getWebSocketClient();
        if (client && client.connected) {
          clientRef.current = client;
          onConnect?.(client);
        } else {
          console.log('웹소켓 연결 대기')
          retryCount++;
          retryTimerRef.current = setTimeout(tryConnect, interval);
        }
      }

      tryConnect();

      return () => {
        if (retryTimerRef.current) {
          clearTimeout(retryTimerRef.current);
        }
      };
    } else {
      isMounted.current = true;
    }
  }, []);

  return clientRef;
}