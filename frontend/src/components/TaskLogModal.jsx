import { useState, useEffect } from 'react';
import { taskLogService } from '../services/taskLogService';

const TaskLogModal = ({ task, isOpen, onClose }) => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingLog, setEditingLog] = useState(null);
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0],
    content: '',
    nextAction: '',
  });

  useEffect(() => {
    if (isOpen && task) {
      loadLogs();
    }
  }, [isOpen, task]);

  const loadLogs = async () => {
    try {
      setLoading(true);
      const data = await taskLogService.getTaskLogs(task.id);
      setLogs(data);
    } catch (err) {
      console.error('로그를 불러오는데 실패했습니다.', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingLog) {
        await taskLogService.updateLog(task.id, editingLog.id, formData);
      } else {
        await taskLogService.createLog(task.id, formData);
      }
      setShowForm(false);
      setEditingLog(null);
      setFormData({
        date: new Date().toISOString().split('T')[0],
        content: '',
        nextAction: '',
      });
      loadLogs();
    } catch (err) {
      alert('로그 저장에 실패했습니다.');
      console.error(err);
    }
  };

  const handleEdit = (log) => {
    setEditingLog(log);
    setFormData({
      date: log.date,
      content: log.content || '',
      nextAction: log.nextAction || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (logId) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    
    try {
      await taskLogService.deleteLog(task.id, logId);
      loadLogs();
    } catch (err) {
      alert('삭제에 실패했습니다.');
      console.error(err);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR');
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="px-6 py-4 border-b flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-900">
            작업 로그: {task?.title}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-2xl"
          >
            ×
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {loading ? (
            <div className="text-center py-8 text-gray-500">로딩 중...</div>
          ) : (
            <>
              {/* Log Form */}
              {showForm && (
                <div className="mb-6 bg-gray-50 p-4 rounded-lg">
                  <h3 className="text-lg font-semibold mb-4">
                    {editingLog ? '로그 수정' : '새 로그 추가'}
                  </h3>
                  <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        날짜
                      </label>
                      <input
                        type="date"
                        value={formData.date}
                        onChange={(e) =>
                          setFormData({ ...formData, date: e.target.value })
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                        required
                      />
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        내용
                      </label>
                      <textarea
                        value={formData.content}
                        onChange={(e) =>
                          setFormData({ ...formData, content: e.target.value })
                        }
                        rows="4"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                        placeholder="진행 상황이나 메모를 입력하세요"
                      />
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Next Action
                      </label>
                      <input
                        type="text"
                        value={formData.nextAction}
                        onChange={(e) =>
                          setFormData({ ...formData, nextAction: e.target.value })
                        }
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                        placeholder="다음에 할 일을 한 줄로 입력하세요"
                      />
                    </div>
                    <div className="flex gap-2">
                      <button
                        type="submit"
                        className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                      >
                        저장
                      </button>
                      <button
                        type="button"
                        onClick={() => {
                          setShowForm(false);
                          setEditingLog(null);
                          setFormData({
                            date: new Date().toISOString().split('T')[0],
                            content: '',
                            nextAction: '',
                          });
                        }}
                        className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
                      >
                        취소
                      </button>
                    </div>
                  </form>
                </div>
              )}

              {/* Add Log Button */}
              {!showForm && (
                <button
                  onClick={() => setShowForm(true)}
                  className="mb-4 px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600"
                >
                  + 로그 추가
                </button>
              )}

              {/* Log List */}
              {logs.length === 0 ? (
                <div className="text-center py-8 text-gray-500">
                  로그가 없습니다.
                </div>
              ) : (
                <div className="space-y-4">
                  {logs.map((log) => (
                    <div
                      key={log.id}
                      className="bg-white border border-gray-200 rounded-lg p-4"
                    >
                      <div className="flex justify-between items-start mb-2">
                        <div className="flex-1">
                          <div className="text-sm text-gray-500 mb-2">
                            {formatDate(log.date)}
                          </div>
                          {log.content && (
                            <p className="text-gray-700 mb-2 whitespace-pre-wrap">
                              {log.content}
                            </p>
                          )}
                          {log.nextAction && (
                            <div className="bg-blue-50 border-l-4 border-blue-500 p-2 rounded">
                              <span className="text-sm font-medium text-blue-700">
                                Next Action:
                              </span>
                              <p className="text-blue-900">{log.nextAction}</p>
                            </div>
                          )}
                        </div>
                        <div className="flex gap-2 ml-4">
                          <button
                            onClick={() => handleEdit(log)}
                            className="px-2 py-1 text-sm bg-yellow-100 text-yellow-700 rounded hover:bg-yellow-200"
                          >
                            수정
                          </button>
                          <button
                            onClick={() => handleDelete(log.id)}
                            className="px-2 py-1 text-sm bg-red-100 text-red-700 rounded hover:bg-red-200"
                          >
                            삭제
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default TaskLogModal;

