import { useState, useEffect } from 'react';
import api from '../services/api';

const Repeat = () => {
  const [settings, setSettings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    try {
      setLoading(true);
      const data = await api.get('/recurring-settings/active');
      setSettings(data);
      setError(null);
    } catch (err) {
      setError('반복 작업 설정을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-500">로딩 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
        {error}
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-3xl font-bold text-gray-900">Repeat</h2>
        <button
          onClick={loadSettings}
          className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          새로고침
        </button>
      </div>

      {settings.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          반복 작업 설정이 없습니다.
        </div>
      ) : (
        <div className="space-y-4">
          {settings.map((setting) => (
            <div
              key={setting.id}
              className="bg-white rounded-lg shadow p-6 border-l-4 border-indigo-500"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">
                    {setting.title}
                  </h3>
                  
                  <div className="flex items-center gap-4 text-sm text-gray-500">
                    {setting.category && (
                      <span className="px-2 py-1 bg-gray-100 rounded">
                        {setting.category}
                      </span>
                    )}
                    <span className="px-2 py-1 bg-indigo-100 text-indigo-700 rounded">
                      {setting.recurrenceType}
                    </span>
                    <span className={setting.isActive ? 'text-green-600' : 'text-gray-400'}>
                      {setting.isActive ? '활성' : '비활성'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Repeat;
