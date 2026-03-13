import { useState, useEffect } from 'react';
import { Sidebar } from '../../components/layout/Sidebar';
import { StatsCard } from '../../components/shared/StatsCard';
import { InternshipCard } from '../../components/shared/InternshipCard';
import { Briefcase, Heart, Bookmark, TrendingUp } from 'lucide-react';
import { fetchDashboardData } from '../../../services/internship';

interface Internship {
  id: string;
  title: string;
  company: string;
  location: string;
  description: string;
  requirements: string[];
  skills: string[];
  type: string;
  duration: string;
  salary?: string;
}

interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: string;
  isActive: boolean;
}

export default function StudentDashboard() {
  const [user, setUser] = useState<User | null>(null);
  const [internships, setInternships] = useState<Internship[]>([]);
  const [userSkills, setUserSkills] = useState<string[]>([]); // default empty array avoids undefined
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          const parsedUser = JSON.parse(storedUser);
          setUser(parsedUser);
        }

        const { skills, matches } = await fetchDashboardData();
        setUserSkills(skills);
        setInternships(matches.slice(0, 3).map(m => m.internship));
      } catch (err: any) {
        console.error('Dashboard error:', err);
        if (err.response?.data?.error) {
          setError(err.response.data.error);
        } else if (err.message === 'Network Error') {
          setError('Unable to contact server. Please ensure backend is running at http://localhost:8080');
        } else {
          setError('Failed to load dashboard data');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar userRole="student" />
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading your dashboard...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar userRole="student" />
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <p className="text-red-600">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar userRole="student" />
      
      <div className="flex-1">
        <div className="p-8">
          {/* Welcome Section */}
          <div className="mb-8">
            <h1 className="text-3xl font-semibold text-gray-900 mb-2">
              Welcome back, {user?.name || 'User'}! 👋
            </h1>
            <p className="text-gray-600">
              Here's what's happening with your internship search today.
            </p>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <StatsCard
              title="Total Internships"
              value={internships.length.toString()}
              icon={Briefcase}
              trend="+12%"
            />
            <StatsCard
              title="Matched Internships"
              value={internships.length.toString()}
              icon={Heart}
              description="Based on your skills"
            />
            <StatsCard
              title="Your Skills"
              value={userSkills.length.toString()}
              icon={Bookmark}
            />
            <StatsCard
              title="Applications"
              value="0"
              icon={TrendingUp}
              trend="Start applying!"
            />
          </div>

          {/* Skills Section */}
          {Array.isArray(userSkills) && userSkills.length > 0 && (
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Your Skills</h2>
              <div className="flex flex-wrap gap-2">
                {userSkills.map((skill, index) => (
                  <span
                    key={index}
                    className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm"
                  >
                    {skill}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Recommended Internships */}
          <div>
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-semibold text-gray-900">
                Recommended for You
              </h2>
              <a href="/internships" className="text-primary hover:underline">
                View all
              </a>
            </div>
            {Array.isArray(internships) && internships.length > 0 ? (
              <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                {internships.map((internship) => (
                  <InternshipCard key={internship.id} internship={internship} />
                ))}
              </div>
            ) : (
              <p className="text-gray-600">No internships matched yet. Add some skills to get recommendations!</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
