import { Search, MapPin } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';

export function SearchBar() {
  return (
    <div className="bg-white rounded-xl shadow-lg p-6 max-w-4xl mx-auto">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {/* Skills Input */}
        <div className="md:col-span-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input
              type="text"
              placeholder="Skills (e.g., React)"
              className="pl-10"
            />
          </div>
        </div>

        {/* Experience Dropdown */}
        <div className="md:col-span-1">
          <Select>
            <SelectTrigger>
              <SelectValue placeholder="Experience" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="fresher">Fresher</SelectItem>
              <SelectItem value="0-1">0-1 years</SelectItem>
              <SelectItem value="1-2">1-2 years</SelectItem>
              <SelectItem value="2+">2+ years</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Location Input */}
        <div className="md:col-span-1">
          <div className="relative">
            <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input
              type="text"
              placeholder="Location"
              className="pl-10"
            />
          </div>
        </div>

        {/* Search Button */}
        <div className="md:col-span-1">
          <Button className="w-full">
            <Search className="h-5 w-5 mr-2" />
            Search
          </Button>
        </div>
      </div>
    </div>
  );
}
