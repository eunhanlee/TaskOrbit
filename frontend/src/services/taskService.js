import api from './api';

export const taskService = {
  // Today 탭 조회
  getTodayTasks: () => api.get('/tasks/today'),

  // Later 탭 조회
  getLaterTasks: () => api.get('/tasks/later'),

  // Done 탭 조회
  getDoneTasks: () => api.get('/tasks/done'),

  // Record 탭 조회
  getRecordTasks: () => api.get('/tasks/record'),

  // 작업 조회
  getTask: (id) => api.get(`/tasks/${id}`),

  // 작업 생성
  createTask: (task) => api.post('/tasks', task),

  // 작업 수정
  updateTask: (id, task) => api.put(`/tasks/${id}`, task),

  // 작업 삭제
  deleteTask: (id) => api.delete(`/tasks/${id}`),

  // 작업 완료
  completeTask: (id) => api.post(`/tasks/${id}/complete`),

  // 작업을 Waiting 상태로 변경
  setTaskWaiting: (id) => api.post(`/tasks/${id}/waiting`),

  // 작업을 활성화 (Waiting -> Ongoing)
  activateTask: (id) => api.post(`/tasks/${id}/activate`),

  // 카테고리별 조회
  getTasksByCategory: (category) => api.get(`/tasks/category/${category}`),
};

