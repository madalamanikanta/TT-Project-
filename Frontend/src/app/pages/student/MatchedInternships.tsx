import { useEffect, useState } from 'react';
import { Sidebar } from '../../components/layout/Sidebar';
import { InternshipCard } from '../../components/shared/InternshipCard';
import { Internship, MatchResult } from '../../types';
import {
  fetchMatchedInternships,
  fetchUserSkills,
} from '../../../services/internship';

export default function MatchedInternships() {
  const [matches, setMatches] = useState<MatchResult[]>([]);
  const [userSkills, setUserSkills] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const rawMatches = await fetchMatchedInternships();
        // backend should already return them sorted, but ensure order on frontend as well
        rawMatches.sort((a, b) => b.matchScore - a.matchScore);
        setMatches(rawMatches);

        const stored = localStorage.getItem('user');
        if (stored) {
          const user = JSON.parse(stored) as { id: number };
          if (user?.id) {
            const skills = await fetchUserSkills(user.id);
            setUserSkills(skills);
          }
        }
      } catch (err: any) {
        console.error(err);
        if (err.message === 'Network Error') {
          setError('Unable to reach the server. Please ensure the backend is running on http://localhost:8080');
        } else {
          setError(err.response?.data?.error || err.message || 'Failed to load matches');
        }
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const computePercentage = (match: MatchResult): number => {
    const skillCount = match.internship.skills?.length || 0;
    if (skillCount === 0) return 0;
    // maximum score per skill is 2 based on backend algorithm
    const max = skillCount * 2;
    return Math.min(100, Math.round((match.matchScore / max) * 100));
  };

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar userRole="student" />
      
      <div className="flex-1">
        <div className="p-8">
          <div className="mb-8">
            <h1 className="text-3xl font-semibold text-gray-900 mb-2">
              Matched Internships
            </h1>
            <p className="text-gray-600">
              Internships that match your skills and profile
            </p>
          </div>

          {loading && <p>Loading...</p>}
          {error && <p className="text-red-600">{error}</p>}

          {userSkills.length > 0 && (
            <div className="bg-white rounded-lg border border-gray-200 p-6 mb-8">
              <h2 className="text-lg font-semibold text-gray-900 mb-3">
                Your Skills
              </h2>
              <div className="flex flex-wrap gap-2">
                {userSkills.map((skill, index) => (
                  <span
                    key={index}
                    className="px-3 py-1.5 bg-primary/10 text-primary rounded-md text-sm font-medium"
                  >
                    {skill}
                  </span>
                ))}
              </div>
            </div>
          )}

          {!loading && !error && (
            <>
              <div className="mb-4">
                <p className="text-gray-600">
                  Found {matches.length} matching internships
                </p>
              </div>
              <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                {matches.map((item) => (
                  <div key={item.internship.id}>
                    <InternshipCard
                      internship={item.internship}
                      showMatchPercentage={computePercentage(item)}
                    />
                    <div className="mt-2 text-sm text-gray-600">
                      Matching skills: {item.internship.skills?.join(', ')}
                    </div>
                  </div>
                ))}
              </div>

              {matches.length === 0 && (
                <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
                  <p className="text-gray-500">
                    No matching internships found. Update your skills to see more matches.
                  </p>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
