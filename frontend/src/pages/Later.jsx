import { useState, useEffect } from 'react';
import { taskService } from '../services/taskService';
import TaskForm from '../components/TaskForm';
import TaskLogModal from '../components/TaskLogModal';

const Later = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingTask, setEditingTask] = useState(null);
  const [logModalTask, setLogModalTask] = useState(null);

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    try {
      setLoading(true);
      const data = await taskService.getLaterTasks();
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
        <h2 className="text-3xl font-bold text-gray-900">Later</h2>
        <button
          onClick={loadTasks}
          className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          새로고침
        </button>
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

      {tasks.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          나중에 할 일이 없습니다.
        </div>
      ) : (
        <div className="space-y-4">
          {tasks.map((task) => (
            <div
              key={task.id}
              className="bg-white rounded-lg shadow p-6 border-l-4 border-purple-500"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <input
                      type="checkbox"
                      checked={task.status === 'DONE'}
                      disabled
                      className="w-5 h-5 text-gray-400 rounded"
                    />
                    <h3 className="text-xl font-semibold text-gray-900">
                      {task.title}
                    </h3>
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
                    <span>예정: {formatDate(task.scheduleDate)}</span>
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

export default Later;
