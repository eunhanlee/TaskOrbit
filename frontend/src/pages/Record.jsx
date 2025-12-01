import { useState, useEffect } from 'react';
import { taskService } from '../services/taskService';
import TaskForm from '../components/TaskForm';
import TaskLogModal from '../components/TaskLogModal';

const Record = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingTask, setEditingTask] = useState(null);
  const [logModalTask, setLogModalTask] = useState(null);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [showCategoryFilter, setShowCategoryFilter] = useState(false);
  const [sortOrder, setSortOrder] = useState('default');
  const [dateFilter, setDateFilter] = useState('all');
  const [showDateFilter, setShowDateFilter] = useState(false);
  const [customStartDate, setCustomStartDate] = useState('');
  const [customEndDate, setCustomEndDate] = useState('');

  useEffect(() => {
    loadTasks();
  }, []);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showCategoryFilter && !event.target.closest('.category-filter')) {
        setShowCategoryFilter(false);
      }
      if (showDateFilter && !event.target.closest('.date-filter')) {
        setShowDateFilter(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showCategoryFilter, showDateFilter]);

  const loadTasks = async () => {
    try {
      setLoading(true);
      const data = await taskService.getRecordTasks();
      setTasks(data);
      setError(null);
    } catch (err) {
      setError('작업을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateTask = async (taskData) => {
    try {
      await taskService.updateTask(editingTask.id, taskData);
      setEditingTask(null);
      loadTasks();
    } catch (err) {
      alert('작업 수정에 실패했습니다.');
      console.error(err);
    }
  };

  // 카테고리 목록 추출 (카테고리 없음 포함)
  const hasNoCategoryTasks = tasks.some(task => !task.category);
  const categories = [
    ...new Set(tasks.map(task => task.category).filter(Boolean))
  ];
  if (hasNoCategoryTasks) {
    categories.push('카테고리 없음');
  }

  // 날짜 필터링 함수 (updatedAt 기준)
  const filterByDate = (taskList) => {
    if (dateFilter === 'all') {
      return taskList;
    }
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return taskList.filter(task => {
      const taskDate = new Date(task.updatedAt);
      taskDate.setHours(0, 0, 0, 0);
      
      switch (dateFilter) {
        case 'today':
          return taskDate.getTime() === today.getTime();
        case 'week':
          const weekAgo = new Date(today);
          weekAgo.setDate(today.getDate() - 7);
          return taskDate >= weekAgo;
        case 'month':
          const monthAgo = new Date(today);
          monthAgo.setMonth(today.getMonth() - 1);
          return taskDate >= monthAgo;
        case 'custom':
          if (!customStartDate || !customEndDate) return true;
          const start = new Date(customStartDate);
          const end = new Date(customEndDate);
          start.setHours(0, 0, 0, 0);
          end.setHours(23, 59, 59, 999);
          return taskDate >= start && taskDate <= end;
        default:
          return true;
      }
    });
  };

  // 날짜 정렬 함수 (updatedAt 기준)
  const sortByDate = (taskList) => {
    if (sortOrder === 'default') {
      return taskList;
    }
    
    const sorted = [...taskList].sort((a, b) => {
      const dateA = new Date(a.updatedAt);
      const dateB = new Date(b.updatedAt);
      return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
    });
    
    return sorted;
  };

  // 카테고리 필터링 함수
  const filterByCategory = (taskList) => {
    if (selectedCategories.length === 0) {
      return taskList;
    }
    return taskList.filter(task => {
      const taskCategory = task.category || '카테고리 없음';
      return selectedCategories.includes(taskCategory);
    });
  };

  // 모든 필터와 정렬 적용
  const filteredTasks = sortByDate(filterByDate(filterByCategory(tasks)));

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

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR');
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
        <h2 className="text-3xl font-bold text-gray-900">Record</h2>
        <div className="flex gap-2">
          <div className="relative category-filter">
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
          <div className="relative date-filter">
            <button
              onClick={() => setShowDateFilter(!showDateFilter)}
              className="px-4 py-2 border border-gray-300 rounded-md bg-white text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              날짜 필터
              {dateFilter !== 'all' && (
                <span className="ml-2 px-2 py-0.5 bg-purple-500 text-white text-xs rounded-full">
                  {dateFilter === 'today' ? '오늘' : dateFilter === 'week' ? '주' : dateFilter === 'month' ? '월' : '범위'}
                </span>
              )}
            </button>
            {showDateFilter && (
              <div className="absolute right-0 mt-2 w-56 bg-white border border-gray-300 rounded-md shadow-lg z-10 p-3">
                <div className="mb-2">
                  <label className="block text-sm font-semibold mb-2">기간 선택</label>
                  <div className="space-y-2">
                    <label className="flex items-center cursor-pointer">
                      <input
                        type="radio"
                        name="dateFilter"
                        value="all"
                        checked={dateFilter === 'all'}
                        onChange={(e) => setDateFilter(e.target.value)}
                        className="mr-2"
                      />
                      <span className="text-sm">전체</span>
                    </label>
                    <label className="flex items-center cursor-pointer">
                      <input
                        type="radio"
                        name="dateFilter"
                        value="today"
                        checked={dateFilter === 'today'}
                        onChange={(e) => setDateFilter(e.target.value)}
                        className="mr-2"
                      />
                      <span className="text-sm">오늘</span>
                    </label>
                    <label className="flex items-center cursor-pointer">
                      <input
                        type="radio"
                        name="dateFilter"
                        value="week"
                        checked={dateFilter === 'week'}
                        onChange={(e) => setDateFilter(e.target.value)}
                        className="mr-2"
                      />
                      <span className="text-sm">이번 주</span>
                    </label>
                    <label className="flex items-center cursor-pointer">
                      <input
                        type="radio"
                        name="dateFilter"
                        value="month"
                        checked={dateFilter === 'month'}
                        onChange={(e) => setDateFilter(e.target.value)}
                        className="mr-2"
                      />
                      <span className="text-sm">이번 달</span>
                    </label>
                    <label className="flex items-center cursor-pointer">
                      <input
                        type="radio"
                        name="dateFilter"
                        value="custom"
                        checked={dateFilter === 'custom'}
                        onChange={(e) => setDateFilter(e.target.value)}
                        className="mr-2"
                      />
                      <span className="text-sm">날짜 범위</span>
                    </label>
                  </div>
                </div>
                {dateFilter === 'custom' && (
                  <div className="mt-3 pt-3 border-t">
                    <div className="mb-2">
                      <label className="block text-xs text-gray-600 mb-1">시작일</label>
                      <input
                        type="date"
                        value={customStartDate}
                        onChange={(e) => setCustomStartDate(e.target.value)}
                        className="w-full px-2 py-1 border border-gray-300 rounded text-sm"
                      />
                    </div>
                    <div>
                      <label className="block text-xs text-gray-600 mb-1">종료일</label>
                      <input
                        type="date"
                        value={customEndDate}
                        onChange={(e) => setCustomEndDate(e.target.value)}
                        className="w-full px-2 py-1 border border-gray-300 rounded text-sm"
                      />
                    </div>
                  </div>
                )}
                {dateFilter !== 'all' && (
                  <button
                    onClick={() => {
                      setDateFilter('all');
                      setCustomStartDate('');
                      setCustomEndDate('');
                    }}
                    className="mt-2 w-full px-2 py-1 text-xs text-red-600 hover:bg-red-50 rounded"
                  >
                    필터 초기화
                  </button>
                )}
              </div>
            )}
          </div>
          <div className="relative">
            <button
              onClick={() => {
                if (sortOrder === 'default') setSortOrder('asc');
                else if (sortOrder === 'asc') setSortOrder('desc');
                else setSortOrder('default');
              }}
              className="px-4 py-2 border border-gray-300 rounded-md bg-white text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              날짜 정렬
              {sortOrder === 'asc' && <span className="ml-2 text-xs">↑ 오름차순</span>}
              {sortOrder === 'desc' && <span className="ml-2 text-xs">↓ 내림차순</span>}
              {sortOrder === 'default' && <span className="ml-2 text-xs text-gray-400">기본</span>}
            </button>
          </div>
          <button
            onClick={loadTasks}
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            새로고침
          </button>
        </div>
      </div>

      {editingTask && (
        <div className="mb-6">
          <TaskForm
            task={editingTask}
            onSubmit={handleUpdateTask}
            onCancel={() => setEditingTask(null)}
          />
        </div>
      )}

      {logModalTask && (
        <TaskLogModal
          task={logModalTask}
          isOpen={!!logModalTask}
          onClose={() => setLogModalTask(null)}
        />
      )}

      {filteredTasks.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          {tasks.length === 0 ? '완료 기록이 없습니다.' : '선택한 카테고리의 작업이 없습니다.'}
        </div>
      ) : (
        <div className="space-y-4">
          {filteredTasks.map((task) => (
            <div
              key={task.id}
              className="bg-white rounded-lg shadow p-6 border-l-4 border-gray-400"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <input
                      type="checkbox"
                      checked={true}
                      disabled
                      className="w-5 h-5 text-gray-400 rounded"
                    />
                    <h3 className="text-xl font-semibold text-gray-700 line-through">
                      {task.title}
                    </h3>
                  </div>
                  
                  <div className="flex items-center gap-4 text-sm text-gray-500">
                    {task.category && (
                      <span className="px-2 py-1 bg-gray-100 rounded">
                        {task.category}
                      </span>
                    )}
                    <span>완료: {formatDate(task.updatedAt)}</span>
                  </div>
                </div>

                <div className="flex gap-2 ml-4">
                  <button
                    onClick={() => setLogModalTask(task)}
                    className="px-3 py-1 bg-purple-100 text-purple-700 rounded text-sm hover:bg-purple-200"
                  >
                    로그
                  </button>
                  <button
                    onClick={() => setEditingTask(task)}
                    className="px-3 py-1 bg-blue-100 text-blue-700 rounded text-sm hover:bg-blue-200"
                  >
                    수정
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

export default Record;
