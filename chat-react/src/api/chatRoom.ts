import axios from './axiosInstance';
import type { AxiosResponse } from 'axios';
import type { ApiResponse, ChatRoomInfo } from './types';

export const getRoomList = async (): Promise<ApiResponse<ChatRoomInfo[]>> => {
  const res: AxiosResponse<ApiResponse<ChatRoomInfo[]>> = await axios.get<ApiResponse<ChatRoomInfo[]>>(`/api/room`);
  return res.data;
};