export interface Internship {
  id: string;
  title: string;
  company: string;
  location: string;
  type: string;
  skills: string[];
  description: string;
  duration: string;
  stipend?: string;
  posted: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'student' | 'admin';
  skills?: string[];
  resume?: string;
}

export const mockInternships: Internship[] = [
  {
    id: '1',
    title: 'Frontend Developer Intern',
    company: 'Google',
    location: 'Remote',
    type: 'Remote',
    skills: ['React', 'JavaScript', 'TypeScript', 'Tailwind CSS'],
    description: 'Join our team to build amazing web applications using modern technologies. You will work on real-world projects and collaborate with experienced developers.',
    duration: '3 months',
    stipend: '$1500/month',
    posted: '2 days ago'
  },
  {
    id: '2',
    title: 'Data Science Intern',
    company: 'Microsoft',
    location: 'Bangalore',
    type: 'MNC',
    skills: ['Python', 'Machine Learning', 'TensorFlow', 'Data Analysis'],
    description: 'Work on cutting-edge ML models and data analysis projects. Great opportunity to learn from industry experts.',
    duration: '6 months',
    stipend: '$2000/month',
    posted: '1 week ago'
  },
  {
    id: '3',
    title: 'Backend Developer Intern',
    company: 'Amazon',
    location: 'Hyderabad',
    type: 'MNC',
    skills: ['Node.js', 'Express', 'MongoDB', 'AWS'],
    description: 'Build scalable backend services and APIs. Learn cloud technologies and best practices.',
    duration: '4 months',
    stipend: '$1800/month',
    posted: '3 days ago'
  },
  {
    id: '4',
    title: 'UI/UX Design Intern',
    company: 'Figma',
    location: 'Remote',
    type: 'Remote',
    skills: ['Figma', 'Adobe XD', 'Prototyping', 'User Research'],
    description: 'Create beautiful and intuitive user interfaces. Work on design systems and user experience.',
    duration: '3 months',
    stipend: '$1200/month',
    posted: '5 days ago'
  },
  {
    id: '5',
    title: 'Mobile App Developer Intern',
    company: 'Meta',
    location: 'Mumbai',
    type: 'MNC',
    skills: ['React Native', 'Flutter', 'iOS', 'Android'],
    description: 'Develop mobile applications for millions of users. Learn mobile development best practices.',
    duration: '6 months',
    stipend: '$2200/month',
    posted: '1 day ago'
  },
  {
    id: '6',
    title: 'Full Stack Developer Intern',
    company: 'Netflix',
    location: 'Remote',
    type: 'Remote',
    skills: ['React', 'Node.js', 'PostgreSQL', 'Docker'],
    description: 'Work on both frontend and backend of our streaming platform. Great learning opportunity.',
    duration: '5 months',
    stipend: '$2500/month',
    posted: '4 days ago'
  },
  {
    id: '7',
    title: 'DevOps Intern',
    company: 'Oracle',
    location: 'Pune',
    type: 'MNC',
    skills: ['Docker', 'Kubernetes', 'CI/CD', 'AWS'],
    description: 'Learn cloud infrastructure and deployment automation. Work with modern DevOps tools.',
    duration: '4 months',
    stipend: '$1600/month',
    posted: '6 days ago'
  },
  {
    id: '8',
    title: 'Cybersecurity Intern',
    company: 'Cisco',
    location: 'Delhi',
    type: 'MNC',
    skills: ['Network Security', 'Ethical Hacking', 'Python', 'Linux'],
    description: 'Protect digital assets and learn about cybersecurity. Hands-on security projects.',
    duration: '3 months',
    stipend: '$1400/month',
    posted: '2 days ago'
  }
];

export const mockUsers: User[] = [
  {
    id: '1',
    name: 'Rahul Sharma',
    email: 'rahul@example.com',
    role: 'student',
    skills: ['React', 'JavaScript', 'Node.js']
  },
  {
    id: '2',
    name: 'Priya Patel',
    email: 'priya@example.com',
    role: 'student',
    skills: ['Python', 'Machine Learning', 'TensorFlow']
  },
  {
    id: '3',
    name: 'Admin User',
    email: 'admin@example.com',
    role: 'admin'
  }
];

export const categories = [
  { name: 'Remote', icon: 'Laptop' },
  { name: 'MNC', icon: 'Building2' },
  { name: 'Fresher', icon: 'GraduationCap' },
  { name: 'Analytics', icon: 'BarChart3' },
  { name: 'Internship', icon: 'Briefcase' },
  { name: 'Data Science', icon: 'Database' }
];

export const companies = [
  'Google', 'Microsoft', 'Amazon', 'Meta', 'Apple', 
  'Netflix', 'Tesla', 'Oracle', 'Adobe', 'Salesforce',
  'IBM', 'Intel', 'Cisco'
];
