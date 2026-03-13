import { Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import StudentDashboard from './pages/student/Dashboard';
import ProfilePage from './pages/student/Profile';
import MatchedInternships from './pages/student/MatchedInternships';
import SavedInternships from './pages/student/SavedInternships';
import InternshipListing from './pages/InternshipListing';
import InternshipDetail from './pages/InternshipDetail';
import AdminDashboard from './pages/admin/Dashboard';
import ManageInternships from './pages/admin/ManageInternships';
import ManageUsers from './pages/admin/ManageUsers';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/internships" element={<InternshipListing />} />
      <Route path="/internships/:id" element={<InternshipDetail />} />
      
      {/* Student Routes */}
      <Route path="/student/dashboard" element={<StudentDashboard />} />
      <Route path="/student/profile" element={<ProfilePage />} />
      <Route path="/student/matches" element={<MatchedInternships />} />
      <Route path="/student/saved" element={<SavedInternships />}/>
      
      {/* Admin Routes */}
      <Route path="/admin/dashboard" element={<AdminDashboard />} />
      <Route path="/admin/internships" element={<ManageInternships />} />
      <Route path="/admin/manage-internships" element={<ManageInternships />} />
      <Route path="/admin/users" element={<ManageUsers />} />
      <Route path="/admin/manage-users" element={<ManageUsers />} />
      
      {/* Catch-all for undefined routes */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}