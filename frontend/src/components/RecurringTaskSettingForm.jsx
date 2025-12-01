import { useState, useEffect } from 'react';

const RecurringTaskSettingForm = ({ setting, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    title: '',
    category: '',
    size: '',
    recurrenceType: 'DAILY',
    isActive: true,
  });

  useEffect(() => {
    if (setting) {
      setFormData({
        title: setting.title || '',
        category: setting.category || '',
        size: setting.size || '',
        recurrenceType: setting.recurrenceType || 'DAILY',
        isActive: setting.isActive !== undefined ? setting.isActive : true,
      });
    }
  }, [setting]);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-6 border-l-4 border-indigo-500">
      <h3 className="text-xl font-semibold mb-4">
        {setting ? '반복 작업 설정 수정' : '새 반복 작업 설정'}
      </h3>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            제목 *
          </label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            placeholder="예: 매일 아침 운동"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            카테고리
          </label>
          <input
            type="text"
            name="category"
            value={formData.category}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            placeholder="예: 건강"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            크기
          </label>
          <select
            name="size"
            value={formData.size}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">선택 안 함</option>
            <option value="UNDER_10_MIN">10분 미만</option>
            <option value="UNDER_30_MIN">30분 미만</option>
            <option value="OVER_1_HOUR">1시간 이상</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            반복 유형 *
          </label>
          <select
            name="recurrenceType"
            value={formData.recurrenceType}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="DAILY">매일</option>
            <option value="WEEKLY">매주</option>
            <option value="MONTHLY">매월</option>
            <option value="YEARLY">매년</option>
          </select>
        </div>

        <div>
          <label className="flex items-center">
            <input
              type="checkbox"
              name="isActive"
              checked={formData.isActive}
              onChange={handleChange}
              className="mr-2"
            />
            <span className="text-sm text-gray-700">활성화</span>
          </label>
        </div>
      </div>

      <div className="flex gap-2 mt-6">
        <button
          type="submit"
          className="flex-1 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600"
        >
          {setting ? '수정' : '생성'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
        >
          취소
        </button>
      </div>
    </form>
  );
};

export default RecurringTaskSettingForm;

