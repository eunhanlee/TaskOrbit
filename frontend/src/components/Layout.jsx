import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { undoRedoService } from '../services/undoRedoService';

const Layout = ({ children }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const [isUndoing, setIsUndoing] = useState(false);
  const [isRedoing, setIsRedoing] = useState(false);

  const tabs = [
    { path: '/today', label: 'Today' },
    { path: '/done', label: 'Done' },
    { path: '/record', label: 'Record' },
    { path: '/later', label: 'Later' },
    { path: '/repeat', label: 'Repeat' },
  ];

  const handleUndo = async () => {
    try {
      setIsUndoing(true);
      const response = await undoRedoService.undo();
      if (response.success) {
        // 페이지 새로고침하여 변경사항 반영
        navigate(0);
      } else {
        alert('되돌릴 작업이 없습니다.');
      }
    } catch (err) {
      alert('Undo 실패: ' + (err.message || '알 수 없는 오류'));
      console.error(err);
    } finally {
      setIsUndoing(false);
    }
  };

  const handleRedo = async () => {
    try {
      setIsRedoing(true);
      const response = await undoRedoService.redo();
      if (response.success) {
        // 페이지 새로고침하여 변경사항 반영
        navigate(0);
      } else {
        alert('다시 적용할 작업이 없습니다.');
      }
    } catch (err) {
      alert('Redo 실패: ' + (err.message || '알 수 없는 오류'));
      console.error(err);
    } finally {
      setIsRedoing(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = '/login';
  };

  const username = localStorage.getItem('username');

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold text-gray-900">TaskOrbit</h1>
            <div className="flex items-center gap-4">
              {username && (
                <div className="flex items-center gap-2">
                  <span className="text-sm text-gray-600">안녕하세요, {username}님</span>
                  <button
                    onClick={handleLogout}
                    className="px-3 py-1 bg-gray-200 text-gray-700 rounded-md text-sm hover:bg-gray-300 transition-colors"
                  >
                    로그아웃
                  </button>
                </div>
              )}
              <div className="flex gap-2">
                <button
                  onClick={handleUndo}
                  disabled={isUndoing || isRedoing}
                  className="px-3 py-1.5 bg-gray-200 text-gray-700 rounded-md text-sm font-medium hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  title="Undo (Ctrl+Z)"
                >
                  {isUndoing ? 'Undoing...' : '↶ Undo'}
                </button>
                <button
                  onClick={handleRedo}
                  disabled={isUndoing || isRedoing}
                  className="px-3 py-1.5 bg-gray-200 text-gray-700 rounded-md text-sm font-medium hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  title="Redo (Ctrl+Y)"
                >
                  {isRedoing ? 'Redoing...' : '↷ Redo'}
                </button>
              </div>
              <nav className="flex space-x-1">
                {tabs.map((tab) => (
                  <Link
                    key={tab.path}
                    to={tab.path}
                    className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                      location.pathname === tab.path || 
                      (tab.path === '/today' && location.pathname === '/')
                        ? 'bg-blue-500 text-white'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    {tab.label}
                  </Link>
                ))}
              </nav>
            </div>
          </div>
        </div>
      </header>
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
};

export default Layout;


