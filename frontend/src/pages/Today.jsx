import { useState, useEffect } from 'react';
import { taskService } from '../services/taskService';
import TaskForm from '../components/TaskForm';
import TaskLogModal from '../components/TaskLogModal';

const Today = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [logModalTask, setLogModalTask] = useState(null);

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await taskService.getTodayTasks();
      setTasks(data || []);
    } catch (err) {
      const errorMessage = err.message || '작업을 불러오는데 실패했습니다.';
      setError(errorMessage);
      console.error('작업 로드 실패:', err);
      // 에러 발생 시 빈 배열로 설정하여 앱이 계속 작동하도록 함
      setTasks([]);
    } finally {
      setLoading(false);
    }
  };

  // 일반 작업과 Waiting 작업 분리
  const activeTasks = tasks.filter(task => task.status !== 'WAITING');
  const waitingTasks = tasks.filter(task => task.status === 'WAITING');

  const handleComplete = async (id) => {
    try {
      await taskService.completeTask(id);
      loadTasks(); // 목록 새로고침
    } catch (err) {
      alert('작업 완료 처리에 실패했습니다.');
      console.error(err);
    }
  };

  const handleWaiting = async (id) => {
    try {
      await taskService.setTaskWaiting(id);
      loadTasks(); // 목록 새로고침
    } catch (err) {
      alert('상태 변경에 실패했습니다.');
      console.error(err);
    }
  };

  const handleActivate = async (id) => {
    try {
      await taskService.activateTask(id);
      loadTasks(); // 목록 새로고침
    } catch (err) {
      alert('활성화에 실패했습니다.');
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    
    try {
      await taskService.deleteTask(id);
      loadTasks(); // 목록 새로고침
    } catch (err) {
      alert('삭제에 실패했습니다.');
      console.error(err);
    }
  };

  const getDelayDays = (scheduleDate) => {
    const today = new Date();
    const schedule = new Date(scheduleDate);
    const diffTime = today - schedule;
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 0;
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

  const handleCreateTask = async (taskData) => {
    try {
      await taskService.createTask(taskData);
      setShowForm(false);
      loadTasks();
    } catch (err) {
      alert('작업 생성에 실패했습니다.');
      console.error(err);
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

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-3xl font-bold text-gray-900">Today</h2>
        <div className="flex gap-2">
          <button
            onClick={() => setShowForm(!showForm)}
            className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600"
          >
            {showForm ? '취소' : '작업 추가'}
          </button>
          <button
            onClick={loadTasks}
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            새로고침
          </button>
        </div>
      </div>

      {showForm && (
        <div className="mb-6">
          <TaskForm
            onSubmit={handleCreateTask}
            onCancel={() => setShowForm(false)}
          />
        </div>
      )}

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

      {/* 일반 작업 섹션 */}
      <div className="mb-8">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">오늘 할 일</h3>
        {activeTasks.length === 0 ? (
          <div className="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
            오늘 할 일이 없습니다.
          </div>
        ) : (
          <div className="space-y-4">
            {activeTasks.map((task) => {
              const delayDays = getDelayDays(task.scheduleDate);
              return (
                <div
                  key={task.id}
                  className="bg-white rounded-lg shadow p-6 border-l-4 border-blue-500"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <input
                          type="checkbox"
                          checked={task.status === 'DONE'}
                          onChange={() => handleComplete(task.id)}
                          className="w-5 h-5 text-blue-600 rounded"
                        />
                        <h3 className="text-xl font-semibold text-gray-900">
                          {task.title}
                        </h3>
                        {delayDays > 0 && (
                          <span className="px-2 py-1 bg-red-100 text-red-700 text-sm rounded">
                            -{delayDays}
                          </span>
                        )}
                      </div>
                      
                      {task.nextAction && (
                        <p className="text-gray-600 mb-2">
                          <span className="font-medium">Next Action:</span> {task.nextAction}
                        </p>
                      )}
                      
                      <div className="flex items-center gap-4 text-sm text-gray-500">
                        {task.category && (
                          <span className="px-2 py-1 bg-gray-100 rounded">
                            {task.category}
                          </span>
                        )}
                        {task.size && (
                          <span className="px-2 py-1 bg-gray-100 rounded">
                            {task.size}
                          </span>
                        )}
                        <span>{formatDate(task.scheduleDate)}</span>
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
                      <button
                        onClick={() => handleWaiting(task.id)}
                        className="px-3 py-1 bg-yellow-100 text-yellow-700 rounded text-sm hover:bg-yellow-200"
                      >
                        Waiting
                      </button>
                      <button
                        onClick={() => handleDelete(task.id)}
                        className="px-3 py-1 bg-red-100 text-red-700 rounded text-sm hover:bg-red-200"
                      >
                        삭제
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Waiting 작업 섹션 */}
      {waitingTasks.length > 0 && (
        <div>
          <h3 className="text-xl font-semibold text-gray-800 mb-4">대기 중인 작업</h3>
          <div className="space-y-4">
            {waitingTasks.map((task) => {
              const delayDays = getDelayDays(task.scheduleDate);
              return (
                <div
                  key={task.id}
                  className="bg-white rounded-lg shadow p-6 border-l-4 border-yellow-500 opacity-75"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <input
                          type="checkbox"
                          checked={task.status === 'DONE'}
                          onChange={() => handleComplete(task.id)}
                          className="w-5 h-5 text-blue-600 rounded"
                        />
                        <h3 className="text-xl font-semibold text-gray-700">
                          {task.title}
                        </h3>
                        <span className="px-2 py-1 bg-yellow-100 text-yellow-700 text-sm rounded">
                          Waiting
                        </span>
                        {delayDays > 0 && (
                          <span className="px-2 py-1 bg-red-100 text-red-700 text-sm rounded">
                            -{delayDays}
                          </span>
                        )}
                      </div>
                      
                      {task.nextAction && (
                        <p className="text-gray-600 mb-2">
                          <span className="font-medium">Next Action:</span> {task.nextAction}
                        </p>
                      )}
                      
                      <div className="flex items-center gap-4 text-sm text-gray-500">
                        {task.category && (
                          <span className="px-2 py-1 bg-gray-100 rounded">
                            {task.category}
                          </span>
                        )}
                        {task.size && (
                          <span className="px-2 py-1 bg-gray-100 rounded">
                            {task.size}
                          </span>
                        )}
                        <span>{formatDate(task.scheduleDate)}</span>
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
                      <button
                        onClick={() => handleActivate(task.id)}
                        className="px-3 py-1 bg-green-100 text-green-700 rounded text-sm hover:bg-green-200"
                      >
                        활성화
                      </button>
                      <button
                        onClick={() => handleDelete(task.id)}
                        className="px-3 py-1 bg-red-100 text-red-700 rounded text-sm hover:bg-red-200"
                      >
                        삭제
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

export default Today;
