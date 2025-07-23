export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
}

export type ChatRoomInfo = {
  roomId: number;
  roomName: string;
  lastId: number | null;
  lastMessage: string | null;
  lastDt: string | null;
};