import api from './api';

export const taskLogService = {
  // 작업의 모든 로그 조회
  getTaskLogs: (taskId) => api.get(`/tasks/${taskId}/logs`),

  // 최신 로그 조회 (Next Action)
  getLatestLog: (taskId) => api.get(`/tasks/${taskId}/logs/latest`),

  // 로그 생성
  createLog: (taskId, log) => api.post(`/tasks/${taskId}/logs`, log),

  // 로그 수정
  updateLog: (taskId, logId, log) => api.put(`/tasks/${taskId}/logs/${logId}`, log),

  // 로그 삭제
  deleteLog: (taskId, logId) => api.delete(`/tasks/${taskId}/logs/${logId}`),
};

