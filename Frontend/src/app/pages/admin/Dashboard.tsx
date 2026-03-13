import { Sidebar } from '../../components/layout/Sidebar';
import { StatsCard } from '../../components/shared/StatsCard';
import { InternshipList } from '../../components/shared/InternshipList';
import { Users, Briefcase, TrendingUp, CheckCircle } from 'lucide-react';

export default function AdminDashboard() {
  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar userRole="admin" />
      
      <div className="flex-1">
        <div className="p-8">
          <div className="mb-8">
            <h1 className="text-3xl font-semibold text-gray-900 mb-2">
              Admin Dashboard
            </h1>
            <p className="text-gray-600">
              Overview of platform statistics and activities
            </p>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <StatsCard
              title="Total Users"
              value="2,547"
              icon={Users}
              trend="+18%"
              description="vs last month"
            />
            <StatsCard
              title="Total Internships"
              value="1,234"
              icon={Briefcase}
              trend="+12%"
              description="Active listings"
            />
            <StatsCard
              title="Applications"
              value="8,432"
              icon={TrendingUp}
              trend="+25%"
              description="This month"
            />
            <StatsCard
              title="Success Rate"
              value="68%"
              icon={CheckCircle}
              trend="+5%"
              description="Placement rate"
            />
          </div>

          {/* Internship List Section */}
          <div className="bg-white rounded-lg border border-gray-200 p-6 mb-8">
            <InternshipList onFetchComplete={(count) => {
              console.log(`Fetched ${count} new internships`);
            }} />
          </div>

          {/* Recent Activity */}
          <div className="bg-white rounded-lg border border-gray-200 p-6 mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Recent Activity
            </h2>
            <div className="space-y-4">
              {[
                { action: 'New user registered', user: 'rahul@example.com', time: '5 minutes ago' },
                { action: 'Internship posted', user: 'Google - Frontend Developer', time: '1 hour ago' },
                { action: 'Application submitted', user: 'priya@example.com', time: '2 hours ago' },
                { action: 'New user registered', user: 'amit@example.com', time: '3 hours ago' },
                { action: 'Internship updated', user: 'Microsoft - Data Science', time: '5 hours ago' }
              ].map((activity, index) => (
                <div key={index} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="text-gray-900 font-medium">{activity.action}</p>
                    <p className="text-gray-600 text-sm">{activity.user}</p>
                  </div>
                  <span className="text-gray-500 text-sm">{activity.time}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Quick Actions */}
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Quick Actions
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <button className="p-4 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
                Add New Internship
              </button>
              <button className="p-4 bg-gray-100 text-gray-900 rounded-lg hover:bg-gray-200 transition-colors">
                Manage Users
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
