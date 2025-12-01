import api from './api';

export const recurringTaskSettingService = {
  // 활성화된 반복 작업 설정 조회
  getActiveSettings: () => api.get('/recurring-settings/active'),

  // 모든 반복 작업 설정 조회
  getAllSettings: () => api.get('/recurring-settings'),

  // 반복 작업 설정 조회
  getSetting: (id) => api.get(`/recurring-settings/${id}`),

  // 반복 작업 설정 생성
  createSetting: (setting) => api.post('/recurring-settings', setting),

  // 반복 작업 설정 수정
  updateSetting: (id, setting) => api.put(`/recurring-settings/${id}`, setting),

  // 반복 작업 설정 삭제
  deleteSetting: (id) => api.delete(`/recurring-settings/${id}`),

  // 반복 작업 설정 활성화/비활성화
  toggleActive: (id) => api.post(`/recurring-settings/${id}/toggle`),
};


