import axios from './axiosInstance';
import type { AxiosResponse } from 'axios';
import type { ApiResponse, ChatRoomInfo } from './types';

export const getRoomList = async (): Promise<ApiResponse<ChatRoomInfo[]>> => {
  const res: AxiosResponse<ApiResponse<ChatRoomInfo[]>> = await axios.get<ApiResponse<ChatRoomInfo[]>>(`/api/room`);
  return res.data;
};

export const createRoom = async (name: string): Promise<ApiResponse<null>> => {
  const form = new URLSearchParams();
  form.append('name', name);

  const res: AxiosResponse<ApiResponse<null>> = await axios.post<ApiResponse<null>>(
    `/api/room`, 
    form,
    {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    }
  );
  return res.data;
};