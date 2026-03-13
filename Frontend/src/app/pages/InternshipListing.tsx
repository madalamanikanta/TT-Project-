import { useState, useEffect } from 'react';
import { Navbar } from '../components/layout/Navbar';
import { InternshipCard } from '../components/shared/InternshipCard';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Checkbox } from '../components/ui/checkbox';
import { Search } from 'lucide-react';
import { Internship } from '../types';
import { fetchAllInternships } from '../../services/internship';

export default function InternshipListing() {
  const [internships, setInternships] = useState<Internship[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedTypes, setSelectedTypes] = useState<string[]>([]);
  const [selectedLocations, setSelectedLocations] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const types = ['Remote', 'MNC', 'Startup', 'Fresher'];
  const locations = ['Bangalore', 'Mumbai', 'Delhi', 'Hyderabad', 'Pune'];

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await fetchAllInternships();
        setInternships(data);
      } catch (err: any) {
        console.error(err);
        setError(err.response?.data?.error || err.message || 'Failed to load internships');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const filteredInternships = internships.filter((internship) => {
    const text = searchQuery.toLowerCase();
    const title = internship.title?.toLowerCase() || '';
    const company = (internship.company || internship.organization || '').toLowerCase();
    const matchesSearch =
      searchQuery === '' ||
      title.includes(text) ||
      company.includes(text) ||
      internship.skills?.some(skill => skill.toLowerCase().includes(text));

    const matchesType =
      selectedTypes.length === 0 ||
      (internship.type && selectedTypes.includes(internship.type));
    const matchesLocation =
      selectedLocations.length === 0 ||
      (internship.location && selectedLocations.includes(internship.location));

    return matchesSearch && matchesType && matchesLocation;
  });

  const handleTypeChange = (type: string, checked: boolean) => {
    if (checked) {
      setSelectedTypes([...selectedTypes, type]);
    } else {
      setSelectedTypes(selectedTypes.filter(t => t !== type));
    }
  };

  const handleLocationChange = (location: string, checked: boolean) => {
    if (checked) {
      setSelectedLocations([...selectedLocations, location]);
    } else {
      setSelectedLocations(selectedLocations.filter(l => l !== location));
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Search Bar */}
        <div className="mb-8">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input
              type="text"
              placeholder="Search by title, company, or skills..."
              className="pl-10"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        {loading && <p>Loading...</p>}
        {error && <p className="text-red-600">{error}</p>}

        <div className="flex gap-8">
          {/* Filters Sidebar */}
          <div className="w-64 flex-shrink-0">
            <div className="bg-white rounded-lg border border-gray-200 p-6 sticky top-8">
              <h2 className="text-lg font-semibold text-gray-900 mb-6">Filters</h2>

              {/* Type Filter */}
              <div className="mb-6">
                <Label className="mb-3 block">Type</Label>
                <div className="space-y-3">
                  {types.map((type) => (
                    <div key={type} className="flex items-center">
                      <Checkbox
                        id={`type-${type}`}
                        checked={selectedTypes.includes(type)}
                        onCheckedChange={(checked) => handleTypeChange(type, checked as boolean)}
                      />
                      <label
                        htmlFor={`type-${type}`}
                        className="ml-2 text-sm text-gray-700 cursor-pointer"
                      >
                        {type}
                      </label>
                    </div>
                  ))}
                </div>
              </div>

              {/* Location Filter */}
              <div>
                <Label className="mb-3 block">Location</Label>
                <div className="space-y-3">
                  {locations.map((location) => (
                    <div key={location} className="flex items-center">
                      <Checkbox
                        id={`location-${location}`}
                        checked={selectedLocations.includes(location)}
                        onCheckedChange={(checked) => handleLocationChange(location, checked as boolean)}
                      />
                      <label
                        htmlFor={`location-${location}`}
                        className="ml-2 text-sm text-gray-700 cursor-pointer"
                      >
                        {location}
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Internship List */}
          <div className="flex-1">
            <div className="mb-4">
              <p className="text-gray-600">
                Showing {filteredInternships.length} internships
              </p>
            </div>
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {filteredInternships.map((internship) => (
                <InternshipCard key={internship.id} internship={internship} />
              ))}
            </div>
            {filteredInternships.length === 0 && (
              <div className="text-center py-12">
                <p className="text-gray-500">No internships found matching your criteria.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
