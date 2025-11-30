import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Today from './pages/Today';
import Done from './pages/Done';
import Record from './pages/Record';
import Later from './pages/Later';
import Repeat from './pages/Repeat';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Today />} />
          <Route path="/today" element={<Today />} />
          <Route path="/done" element={<Done />} />
          <Route path="/record" element={<Record />} />
          <Route path="/later" element={<Later />} />
          <Route path="/repeat" element={<Repeat />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
