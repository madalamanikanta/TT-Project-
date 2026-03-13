import api from './api';
import { Internship, MatchResult } from '../app/types';

/**
 * Wrapper around axios calls for internship-related backend operations
 */

export const fetchInternshipById = async (id: string | number): Promise<Internship> => {
  const response = await api.get(`/internships/${id}`);
  return response.data.data as Internship;
};

export const fetchAllInternships = async (): Promise<Internship[]> => {
  const response = await api.get('/internships');
  return response.data.data as Internship[];
};

export const fetchSavedInternships = async (): Promise<Internship[]> => {
  const response = await api.get('/saved-internships');
  return response.data.data as Internship[];
};

export const checkInternshipSaved = async (id: string | number): Promise<boolean> => {
  const response = await api.get(`/saved-internships/${id}`);
  return response.data.saved as boolean;
};

export const saveInternship = async (id: string | number) => {
  return api.post(`/saved-internships/${id}`);
};

export const deleteSavedInternship = async (id: string | number) => {
  return api.delete(`/saved-internships/${id}`);
};

export const fetchMatchedInternships = async (): Promise<MatchResult[]> => {
  const response = await api.get('/internships/matches');
  const data = response.data.data as any[];
  // convert to MatchResult shape
  return data.map(item => ({
    internship: {
      ...item,
      // backend uses "organization" instead of "company"
      company: item.organization || item.company,
    } as Internship,
    matchScore: item.score ?? 0,
  }));
};

export interface DashboardPayload {
  skills: Array<{ id?: number; name: string } | string>;
  matches: any[]; // will be converted to MatchResult later
}

/**
 * Fetch aggregated dashboard data (skills + matches) for the authenticated user.
 */
export const fetchDashboardData = async (): Promise<{
  skills: string[];
  matches: MatchResult[];
}> => {
  const response = await api.get<DashboardPayload>('/dashboard');
  const { skills = [], matches = [] } = response.data;

  // normalize skills to string array
  const skillNames: string[] = skills.map(s => {
    if (typeof s === 'string') return s;
    return (s as any).name || '';
  }).filter(Boolean);

  const matchResults: MatchResult[] = matches.map(item => ({
    internship: {
      ...item,
      company: item.organization || item.company,
    } as Internship,
    matchScore: item.score ?? 0,
  }));

  return { skills: skillNames, matches: matchResults };
};

export const fetchUserSkills = async (userId: number | string): Promise<string[]> => {
  const response = await api.get(`/users/${userId}/skills`);
  const skills = response.data.data as any[];
  // each skill object likely has a name
  return skills.map(s => s.name as string);
};
