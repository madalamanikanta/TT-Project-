import { Navbar } from '../components/layout/Navbar';
import { SearchBar } from '../components/shared/SearchBar';
import { CategoryCard } from '../components/shared/CategoryCard';
import { 
  Laptop, 
  Building2, 
  GraduationCap, 
  BarChart3, 
  Briefcase, 
  Database,
  CheckCircle,
  Shield,
  Zap,
  Github,
  Linkedin,
  Twitter
} from 'lucide-react';
import { companies } from '../data/mockData';

const categories = [
  { name: 'Remote', icon: Laptop },
  { name: 'MNC', icon: Building2 },
  { name: 'Fresher', icon: GraduationCap },
  { name: 'Analytics', icon: BarChart3 },
  { name: 'Internship', icon: Briefcase },
  { name: 'Data Science', icon: Database }
];

const features = [
  {
    icon: Zap,
    title: 'Smart Matching',
    description: 'AI-powered algorithm matches you with the best opportunities based on your skills'
  },
  {
    icon: Shield,
    title: 'Verified Listings',
    description: 'All internships are verified and from trusted companies'
  },
  {
    icon: CheckCircle,
    title: 'Easy Apply',
    description: 'Apply to multiple internships with just one click'
  }
];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      {/* Hero Section */}
      <section className="bg-gradient-to-br from-blue-50 to-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h1 className="text-5xl font-bold text-gray-900 mb-4">
              Find your dream internship now
            </h1>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Explore thousands of opportunities tailored to your skills
            </p>
          </div>
          <SearchBar />
        </div>
      </section>

      {/* Categories Section */}
      <section className="py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
            Browse by Category
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
            {categories.map((category) => (
              <CategoryCard key={category.name} name={category.name} icon={category.icon} />
            ))}
          </div>
        </div>
      </section>

      {/* Top Companies Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
            Top Companies Hiring
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-8">
            {companies.map((company) => (
              <div
                key={company}
                className="bg-gray-50 rounded-lg p-6 flex items-center justify-center hover:shadow-md transition-shadow"
              >
                <span className="text-gray-700 font-medium text-center">{company}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div>
              <h3 className="text-lg font-semibold mb-4">SkillMatch</h3>
              <p className="text-gray-400">
                Connecting talented students with amazing internship opportunities.
              </p>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Quick Links</h3>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white">About Us</a></li>
                <li><a href="#" className="hover:text-white">Contact</a></li>
                <li><a href="#" className="hover:text-white">Careers</a></li>
              </ul>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">For Students</h3>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white">Find Internships</a></li>
                <li><a href="#" className="hover:text-white">Career Advice</a></li>
                <li><a href="#" className="hover:text-white">Blog</a></li>
              </ul>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Follow Us</h3>
              <div className="flex space-x-4">
                <a href="#" className="text-gray-400 hover:text-white">
                  <Linkedin className="h-6 w-6" />
                </a>
                <a href="#" className="text-gray-400 hover:text-white">
                  <Twitter className="h-6 w-6" />
                </a>
                <a href="#" className="text-gray-400 hover:text-white">
                  <Github className="h-6 w-6" />
                </a>
              </div>
            </div>
          </div>
          <div className="border-t border-gray-800 pt-8 text-center text-gray-400">
            <p>&copy; 2026 SkillMatch. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
