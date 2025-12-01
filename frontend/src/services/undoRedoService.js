import api from './api';

export const undoRedoService = {
  // Undo: 가장 최근 변경을 되돌림
  undo: () => api.post('/undo-redo/undo'),

  // Redo: 되돌린 변경을 다시 적용
  redo: () => api.post('/undo-redo/redo'),
};


