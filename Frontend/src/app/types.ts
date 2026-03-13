// central TypeScript interfaces used across the application

export interface Internship {
  id: number | string;
  title: string;
  organization?: string;      // backend field name
  company?: string;           // some UI code expects this
  location?: string;
  type?: string;
  skills: string[];
  description?: string;
  duration?: string;
  stipend?: string;
  posted?: string;
  // fields returned by matching endpoint
  score?: number;
}

export interface SavedInternship {
  id?: number;
  userId: number;
  internshipId: number;
  savedAt: string;
}

export interface MatchResult {
  internship: Internship;
  matchScore: number;
}

export interface User {
  id: number | string;
  name: string;
  email: string;
  role: 'student' | 'admin';
  phone?: string;
}
