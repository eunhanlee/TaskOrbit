import api from './api';

export const authService = {
  // 회원가입
  register: (username, email, password) => 
    api.post('/auth/register', { username, email, password }),

  // 로그인
  login: (username, password) => 
    api.post('/auth/login', { username, password }),
};


