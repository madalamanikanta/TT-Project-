import { Link } from 'react-router-dom';
import { MapPin, Briefcase, DollarSign, Clock } from 'lucide-react';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Internship } from '../../types';

interface InternshipCardProps {
  internship: Internship;
  showMatchPercentage?: number;
}

export function InternshipCard({ internship, showMatchPercentage }: InternshipCardProps) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 mb-1">
            {internship.title}
          </h3>
          <p className="text-gray-600">
            {internship.company || internship.organization}
          </p>
        </div>
        {showMatchPercentage && (
          <Badge className="bg-green-100 text-green-700 hover:bg-green-100">
            {showMatchPercentage}% Match
          </Badge>
        )}
      </div>

      <div className="space-y-2 mb-4">
        <div className="flex items-center text-gray-600 text-sm">
          <MapPin className="h-4 w-4 mr-2" />
          {internship.location}
        </div>
        <div className="flex items-center text-gray-600 text-sm">
          <Briefcase className="h-4 w-4 mr-2" />
          {internship.duration}
        </div>
        {internship.stipend && (
          <div className="flex items-center text-gray-600 text-sm">
            <DollarSign className="h-4 w-4 mr-2" />
            {internship.stipend}
          </div>
        )}
        <div className="flex items-center text-gray-600 text-sm">
          <Clock className="h-4 w-4 mr-2" />
          {internship.posted}
        </div>
      </div>

      <div className="flex flex-wrap gap-2 mb-4">
        {internship.skills?.slice(0, 4).map((skill, index) => (
          <Badge key={index} variant="secondary">
            {skill}
          </Badge>
        ))}
        {internship.skills && internship.skills.length > 4 && (
          <Badge variant="secondary">+{internship.skills.length - 4} more</Badge>
        )}
      </div>

      <div className="flex gap-2">
        <Link to={`/internships/${internship.id}`} className="flex-1">
          <Button variant="outline" className="w-full">
            View Details
          </Button>
        </Link>
        <Button className="flex-1">Apply Now</Button>
      </div>
    </div>
  );
}
