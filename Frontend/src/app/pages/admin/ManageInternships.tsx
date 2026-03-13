import { useState, useEffect } from 'react';
import { Sidebar } from '../../components/layout/Sidebar';
import { Button } from '../../components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { Badge } from '../../components/ui/badge';
import { Input } from '../../components/ui/input';
import { Edit, Trash2, Plus, Search } from 'lucide-react';
import { Internship } from '../../types';
import { fetchAllInternships } from '../../../services/internship';

export default function ManageInternships() {
  const [searchQuery, setSearchQuery] = useState('');
  const [internships, setInternships] = useState<Internship[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const filteredInternships = internships.filter((internship) =>
    internship.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (internship.company || internship.organization || '')
      .toLowerCase()
      .includes(searchQuery.toLowerCase())
  );

  const handleDelete = (id: string | number) => {
    if (confirm('Are you sure you want to delete this internship?')) {
      setInternships(internships.filter(i => String(i.id) !== String(id)));
      // TODO: call backend delete API when available
    }
  };

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await fetchAllInternships();
        setInternships(data);
      } catch (err: any) {
        console.error(err);
        setError(err.response?.data?.error || err.message || 'Unable to load internships');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar userRole="admin" />
      
      <div className="flex-1">
        <div className="p-8">
          <div className="flex justify-between items-center mb-8">
            <div>
              <h1 className="text-3xl font-semibold text-gray-900 mb-2">
                Manage Internships
              </h1>
              <p className="text-gray-600">
                Add, edit, or remove internship listings
              </p>
            </div>
            <Button>
              <Plus className="h-5 w-5 mr-2" />
              Add Internship
            </Button>
          </div>

          {/* Search */}
          {loading && <p>Loading...</p>}
          {error && <p className="text-red-600">{error}</p>}
          <div className="mb-6">
            <div className="relative max-w-md">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
              <Input
                type="text"
                placeholder="Search internships..."
                className="pl-10"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </div>

          {/* Table */}
          <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Title</TableHead>
                  <TableHead>Company</TableHead>
                  <TableHead>Location</TableHead>
                  <TableHead>Skills</TableHead>
                  <TableHead>Stipend</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredInternships.map((internship) => (
                  <TableRow key={internship.id}>
                    <TableCell className="font-medium">{internship.title}</TableCell>
                    <TableCell>{internship.company}</TableCell>
                    <TableCell>{internship.location}</TableCell>
                    <TableCell>
                      <div className="flex flex-wrap gap-1">
                        {internship.skills.slice(0, 2).map((skill, index) => (
                          <Badge key={index} variant="secondary" className="text-xs">
                            {skill}
                          </Badge>
                        ))}
                        {internship.skills.length > 2 && (
                          <Badge variant="secondary" className="text-xs">
                            +{internship.skills.length - 2}
                          </Badge>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>{internship.stipend}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button variant="ghost" size="icon">
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleDelete(internship.id)}
                        >
                          <Trash2 className="h-4 w-4 text-destructive" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
          
          {filteredInternships.length === 0 && (
            <div className="text-center py-12 bg-white rounded-lg border border-gray-200 mt-6">
              <p className="text-gray-500">No internships found.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
