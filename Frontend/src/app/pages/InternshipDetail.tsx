import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Navbar } from '../components/layout/Navbar';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { MapPin, Briefcase, DollarSign, Clock, Building2, Share2, Bookmark } from 'lucide-react';
import { toast } from 'sonner';
import { Internship } from '../types';
import {
  fetchInternshipById,
  checkInternshipSaved,
  saveInternship,
  deleteSavedInternship,
} from '../../services/internship';

export default function InternshipDetail() {
  const { id } = useParams<{ id: string }>();
  const [internship, setInternship] = useState<Internship | null>(null);
  const [saved, setSaved] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      if (!id) return;
      setLoading(true);
      try {
        const data = await fetchInternshipById(id);
        setInternship(data);
        const isSaved = await checkInternshipSaved(id);
        setSaved(isSaved);
      } catch (err: any) {
        console.error(err);
        setError(err.response?.data?.error || err.message || 'Unable to load internship');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  const toggleSave = async () => {
    if (!internship) return;
    try {
      if (saved) {
        await deleteSavedInternship(internship.id);
        setSaved(false);
        toast.success('Internship removed from saved');
      } else {
        await saveInternship(internship.id);
        setSaved(true);
        toast.success('Internship saved successfully');
      }
    } catch (err: any) {
      console.error(err);
      toast.error('Failed to update saved status');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p>Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-red-600">{error}</p>
      </div>
    );
  }

  if (!internship) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg border border-gray-200 p-8">
          {/* Header */}
          <div className="flex justify-between items-start mb-6">
            <div>
              <h1 className="text-3xl font-semibold text-gray-900 mb-2">
                {internship.title}
              </h1>
              <div className="flex items-center text-gray-600">
                <Building2 className="h-5 w-5 mr-2" />
                <span className="text-lg">
                  {internship.company || internship.organization}
                </span>
              </div>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" size="icon" onClick={toggleSave}>
                <Bookmark
                  className={`h-5 w-5 ${saved ? 'text-primary' : ''}`}
                />
              </Button>
              <Button variant="outline" size="icon">
                <Share2 className="h-5 w-5" />
              </Button>
            </div>
          </div>

          {/* Details */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8 p-4 bg-gray-50 rounded-lg">
            <div className="flex items-center text-gray-600">
              <MapPin className="h-5 w-5 mr-2" />
              <div>
                <p className="text-xs text-gray-500">Location</p>
                <p className="font-medium">{internship.location}</p>
              </div>
            </div>
            <div className="flex items-center text-gray-600">
              <Briefcase className="h-5 w-5 mr-2" />
              <div>
                <p className="text-xs text-gray-500">Duration</p>
                <p className="font-medium">{internship.duration}</p>
              </div>
            </div>
            <div className="flex items-center text-gray-600">
              <DollarSign className="h-5 w-5 mr-2" />
              <div>
                <p className="text-xs text-gray-500">Stipend</p>
                <p className="font-medium">{internship.stipend}</p>
              </div>
            </div>
            <div className="flex items-center text-gray-600">
              <Clock className="h-5 w-5 mr-2" />
              <div>
                <p className="text-xs text-gray-500">Posted</p>
                <p className="font-medium">{internship.posted}</p>
              </div>
            </div>
          </div>

          {/* Description */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              About the Internship
            </h2>
            <p className="text-gray-700 leading-relaxed">
              {internship.description}
            </p>
          </div>

          {/* Required Skills */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Required Skills
            </h2>
            <div className="flex flex-wrap gap-2">
              {internship.skills?.map((skill, index) => (
                <Badge key={index} variant="secondary" className="px-4 py-2">
                  {skill}
                </Badge>
              ))}
            </div>
          </div>

          {/* Responsibilities */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Key Responsibilities
            </h2>
            <ul className="list-disc list-inside space-y-2 text-gray-700">
              <li>Collaborate with the development team on real-world projects</li>
              <li>Write clean, maintainable, and efficient code</li>
              <li>Participate in code reviews and team meetings</li>
              <li>Learn and implement best practices and modern technologies</li>
              <li>Contribute to project documentation and testing</li>
            </ul>
          </div>

          {/* Qualifications */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Qualifications
            </h2>
            <ul className="list-disc list-inside space-y-2 text-gray-700">
              <li>Currently pursuing or recently completed a degree in Computer Science or related field</li>
              <li>Strong understanding of programming fundamentals</li>
              <li>Excellent problem-solving skills</li>
              <li>Good communication and teamwork abilities</li>
              <li>Passion for learning new technologies</li>
            </ul>
          </div>

          {/* Apply Button */}
          <div className="flex gap-4">
            <Button size="lg" className="flex-1">
              Apply Now
            </Button>
            <Button variant="outline" size="lg" className="flex-1">
              Save for Later
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
