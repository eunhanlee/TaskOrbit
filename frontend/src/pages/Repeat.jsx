import { useState, useEffect } from 'react';
import { recurringTaskSettingService } from '../services/recurringTaskSettingService';
import RecurringTaskSettingForm from '../components/RecurringTaskSettingForm';

const Repeat = () => {
  const [settings, setSettings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [showCategoryFilter, setShowCategoryFilter] = useState(false);
  const [editingSetting, setEditingSetting] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);

  useEffect(() => {
    loadSettings();
  }, []);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showCategoryFilter && !event.target.closest('.relative')) {
        setShowCategoryFilter(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showCategoryFilter]);

  const loadSettings = async () => {
    try {
      setLoading(true);
      const data = await recurringTaskSettingService.getAllSettings();
      setSettings(data);
      setError(null);
    } catch (err) {
      setError('반복 작업 설정을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSetting = async (formData) => {
    try {
      await recurringTaskSettingService.createSetting(formData);
      setShowCreateForm(false);
      loadSettings();
    } catch (err) {
      alert('반복 작업 설정 생성에 실패했습니다.');
      console.error(err);
    }
  };

  const handleUpdateSetting = async (formData) => {
    try {
      await recurringTaskSettingService.updateSetting(editingSetting.id, formData);
      setEditingSetting(null);
      loadSettings();
    } catch (err) {
      alert('반복 작업 설정 수정에 실패했습니다.');
      console.error(err);
    }
  };

  const handleDeleteSetting = async (id) => {
    if (!window.confirm('이 반복 작업 설정을 삭제하시겠습니까?')) {
      return;
    }
    try {
      await recurringTaskSettingService.deleteSetting(id);
      loadSettings();
    } catch (err) {
      alert('반복 작업 설정 삭제에 실패했습니다.');
      console.error(err);
    }
  };

  const handleToggleActive = async (id) => {
    try {
      await recurringTaskSettingService.toggleActive(id);
      loadSettings();
    } catch (err) {
      alert('활성화 상태 변경에 실패했습니다.');
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-500">로딩 중...</div>
      </div>
    );
  }

  // 카테고리 목록 추출 (카테고리 없음 포함)
  const hasNoCategorySettings = settings.some(setting => !setting.category);
  const categories = [
    ...new Set(settings.map(setting => setting.category).filter(Boolean))
  ];
  if (hasNoCategorySettings) {
    categories.push('카테고리 없음');
  }

  // 카테고리 필터링 함수
  const filteredSettings = selectedCategories.length === 0
    ? settings
    : settings.filter(setting => {
        const settingCategory = setting.category || '카테고리 없음';
        return selectedCategories.includes(settingCategory);
      });

  // 카테고리 토글
  const toggleCategory = (category) => {
    setSelectedCategories(prev => 
      prev.includes(category)
        ? prev.filter(c => c !== category)
        : [...prev, category]
    );
  };

  // 전체 선택/해제
  const toggleAllCategories = () => {
    if (selectedCategories.length === categories.length) {
      setSelectedCategories([]);
    } else {
      setSelectedCategories([...categories]);
    }
  };

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
        <div className="flex gap-2">
          <button
            onClick={() => {
              setShowCreateForm(true);
              setEditingSetting(null);
            }}
            className="px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600"
          >
            새 반복 작업
          </button>
          <div className="relative">
            <button
              onClick={() => setShowCategoryFilter(!showCategoryFilter)}
              className="px-4 py-2 border border-gray-300 rounded-md bg-white text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              카테고리 필터
              {selectedCategories.length > 0 && (
                <span className="ml-2 px-2 py-0.5 bg-blue-500 text-white text-xs rounded-full">
                  {selectedCategories.length}
                </span>
              )}
            </button>
            {showCategoryFilter && (
              <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-300 rounded-md shadow-lg z-10 p-3">
                <div className="mb-2 pb-2 border-b">
                  <label className="flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedCategories.length === categories.length && categories.length > 0}
                      onChange={toggleAllCategories}
                      className="mr-2"
                    />
                    <span className="font-semibold text-sm">전체 선택</span>
                  </label>
                </div>
                <div className="max-h-48 overflow-y-auto">
                  {categories.length === 0 ? (
                    <div className="text-sm text-gray-500 py-2">카테고리가 없습니다</div>
                  ) : (
                    categories.map((category) => (
                      <label key={category} className="flex items-center cursor-pointer py-1 hover:bg-gray-50">
                        <input
                          type="checkbox"
                          checked={selectedCategories.includes(category)}
                          onChange={() => toggleCategory(category)}
                          className="mr-2"
                        />
                        <span className="text-sm">{category === '카테고리 없음' ? <em className="text-gray-500">{category}</em> : category}</span>
                      </label>
                    ))
                  )}
                </div>
                {selectedCategories.length > 0 && (
                  <button
                    onClick={() => setSelectedCategories([])}
                    className="mt-2 w-full px-2 py-1 text-xs text-red-600 hover:bg-red-50 rounded"
                  >
                    필터 초기화
                  </button>
                )}
              </div>
            )}
          </div>
          <button
            onClick={loadSettings}
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            새로고침
          </button>
        </div>
      </div>

      {showCreateForm && (
        <div className="mb-6">
          <RecurringTaskSettingForm
            onSubmit={handleCreateSetting}
            onCancel={() => setShowCreateForm(false)}
          />
        </div>
      )}

      {editingSetting && (
        <div className="mb-6">
          <RecurringTaskSettingForm
            setting={editingSetting}
            onSubmit={handleUpdateSetting}
            onCancel={() => setEditingSetting(null)}
          />
        </div>
      )}

      {filteredSettings.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          {settings.length === 0 ? '반복 작업 설정이 없습니다.' : '선택한 카테고리의 설정이 없습니다.'}
        </div>
      ) : (
        <div className="space-y-4">
          {filteredSettings.map((setting) => (
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

                <div className="flex gap-2 ml-4">
                  <button
                    onClick={() => handleToggleActive(setting.id)}
                    className={`px-3 py-1 rounded text-sm ${
                      setting.isActive
                        ? 'bg-yellow-100 text-yellow-700 hover:bg-yellow-200'
                        : 'bg-green-100 text-green-700 hover:bg-green-200'
                    }`}
                  >
                    {setting.isActive ? '비활성화' : '활성화'}
                  </button>
                  <button
                    onClick={() => {
                      setEditingSetting(setting);
                      setShowCreateForm(false);
                    }}
                    className="px-3 py-1 bg-blue-100 text-blue-700 rounded text-sm hover:bg-blue-200"
                  >
                    수정
                  </button>
                  <button
                    onClick={() => handleDeleteSetting(setting.id)}
                    className="px-3 py-1 bg-red-100 text-red-700 rounded text-sm hover:bg-red-200"
                  >
                    삭제
                  </button>
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
