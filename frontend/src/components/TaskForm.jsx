import { useState } from 'react';

const TaskForm = ({ task, onSubmit, onCancel }) => {
  // 날짜를 YYYY-MM-DD 형식으로 변환
  const formatDateForInput = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const [formData, setFormData] = useState({
    title: task?.title || '',
    category: task?.category || '',
    size: task?.size || 'UNDER_30_MIN',
    status: task?.status || 'ONGOING',
    dueDate: task?.dueDate ? formatDateForInput(task.dueDate) : new Date().toISOString().split('T')[0],
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow">
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          제목
        </label>
        <input
          type="text"
          value={formData.title}
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          카테고리
        </label>
        <input
          type="text"
          value={formData.category}
          onChange={(e) => setFormData({ ...formData, category: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          크기
        </label>
        <select
          value={formData.size}
          onChange={(e) => setFormData({ ...formData, size: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="UNDER_10_MIN">10분 미만</option>
          <option value="UNDER_30_MIN">30분 미만</option>
          <option value="OVER_1_HOUR">1시간 이상</option>
        </select>
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          마감일 (Due Date) *
        </label>
        <input
          type="date"
          value={formData.dueDate}
          onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
        <p className="mt-1 text-xs text-gray-500">Today 탭에 표시될 날짜 (미완료 시 자동으로 다음날로 이동)</p>
      </div>

      <div className="flex gap-2">
        <button
          type="submit"
          className="flex-1 px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          {task ? '수정' : '생성'}
        </button>
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="flex-1 px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
          >
            취소
          </button>
        )}
      </div>
    </form>
  );
};

export default TaskForm;


