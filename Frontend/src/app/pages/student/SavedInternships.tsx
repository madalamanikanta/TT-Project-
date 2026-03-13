import { useEffect, useState } from 'react';
import { Sidebar } from '../../components/layout/Sidebar';
import { InternshipCard } from '../../components/shared/InternshipCard';
import { Bookmark } from 'lucide-react';
import { Internship } from '../../types';
import { fetchSavedInternships } from '../../../services/internship';

export default function SavedInternships() {
  const [savedInternships, setSavedInternships] = useState<Internship[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await fetchSavedInternships();
        setSavedInternships(data);
      } catch (err: any) {
        console.error(err);
        if (err.message === 'Network Error') {
          setError('Unable to reach the server. Please ensure the backend is running on http://localhost:8080');
        } else {
          setError(err.response?.data?.error || err.message || 'Failed to load saved internships');
        }
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar userRole="student" />
      
      <div className="flex-1">
        <div className="p-8">
          <div className="mb-8">
            <h1 className="text-3xl font-semibold text-gray-900 mb-2">
              Saved Internships
            </h1>
            <p className="text-gray-600">
              Internships you've bookmarked for later
            </p>
          </div>

          {loading && <p>Loading...</p>}
          {error && <p className="text-red-600">{error}</p>}

          {!loading && !error && (
            savedInternships.length > 0 ? (
              <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                {savedInternships.map((internship) => (
                  <InternshipCard key={internship.id} internship={internship} />
                ))}
              </div>
            ) : (
              <div className="text-center py-16 bg-white rounded-lg border border-gray-200">
                <Bookmark className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  No saved internships yet
                </h3>
                <p className="text-gray-600 mb-6">
                  Start saving internships you're interested in
                </p>
                <a
                  href="/internships"
                  className="inline-block px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors"
                >
                  Browse Internships
                </a>
              </div>
            )
          )}
        </div>
      </div>
    </div>
  );
}
