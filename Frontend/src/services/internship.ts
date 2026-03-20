import api from './api';
import { Internship, MatchResult } from '../app/types';
import { mockInternships, mockUsers } from '../app/data/mockData';

/**
 * Wrapper around axios calls for internship-related backend operations
 */

/** Safely normalize skills to a string array regardless of backend shape */
function normalizeSkills(skills: unknown): string[] {
  if (!skills) return [];
  if (Array.isArray(skills)) return (skills as any[])
    .map(s => (typeof s === 'string' ? s : s?.name || String(s)).trim())
    .filter(Boolean);
  if (typeof skills === 'string') {
    const trimmed = skills.trim();
    return trimmed ? [trimmed] : [];
  }
  return [];
}

/** Apply skills normalization to a raw internship object */
function normalizeInternship(item: any): Internship {
  return { ...item, skills: normalizeSkills(item.skills) } as Internship;
}

export const fetchInternshipById = async (id: string | number): Promise<Internship> => {
  try {
    const response = await api.get(`/internships/${id}`);
    return normalizeInternship(response.data.data);
  } catch (error) {
    console.warn(`Failed to fetch internship ${id} from backend, using mock fallback.`, error);
    const mock = mockInternships.find(i => i.id === String(id)) || mockInternships[0];
    return mock as unknown as Internship;
  }
};

export const fetchAllInternships = async (): Promise<Internship[]> => {
  try {
    const response = await api.get('/internships');
    return (response.data.data as any[]).map(normalizeInternship);
  } catch (error) {
    console.warn('Failed to fetch internships from backend, using mock fallback.', error);
    return mockInternships as unknown as Internship[];
  }
};

export const fetchSavedInternships = async (): Promise<Internship[]> => {
  try {
    const response = await api.get('/saved-internships');
    return (response.data.data as any[]).map(normalizeInternship);
  } catch (error) {
    console.warn('Failed to fetch saved internships from backend, using empty fallback.', error);
    return [];
  }
};

export const checkInternshipSaved = async (id: string | number): Promise<boolean> => {
  try {
    const response = await api.get(`/saved-internships/${id}`);
    return response.data.saved as boolean;
  } catch (error) {
    return false;
  }
};

export const saveInternship = async (id: string | number) => {
  return api.post(`/saved-internships/${id}`);
};

export const deleteSavedInternship = async (id: string | number) => {
  return api.delete(`/saved-internships/${id}`);
};

export const fetchMatchedInternships = async (): Promise<MatchResult[]> => {
  try {
    const response = await api.get('/internships/matches');
    const data = response.data.data as any[];
    // convert to MatchResult shape
    return data.map(item => ({
      internship: normalizeInternship({
        ...item,
        // backend uses "organization" instead of "company"
        company: item.organization || item.company,
      }),
      matchScore: item.score ?? 0,
    }));
  } catch (error) {
    console.warn('Failed to fetch matched internships from backend, using mock fallback.', error);
    return mockInternships.slice(0, 3).map(i => ({
      internship: i as unknown as Internship,
      matchScore: 1,
    }));
  }
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
  try {
    const response = await api.get<DashboardPayload>('/dashboard');
    const { skills = [], matches = [] } = response.data;

    // normalize skills to string array
    const skillNames: string[] = skills.map(s => {
      if (typeof s === 'string') return s;
      return (s as any).name || '';
    }).filter(Boolean);

    const matchResults: MatchResult[] = matches.map(item => ({
      internship: normalizeInternship({
        ...item,
        company: item.organization || item.company,
      }),
      matchScore: item.score ?? 0,
    }));

    return { skills: skillNames, matches: matchResults };
  } catch (error) {
    console.error('Critical failure: dashboard requires backend data.', error);
    throw error; // Dashboard is explicitly allowed to depend on DB
  }
};

export const fetchUserSkills = async (userId: number | string): Promise<string[]> => {
  try {
    const response = await api.get(`/users/${userId}/skills`);
    const skills = response.data.data as any[];
    // each skill object likely has a name
    return skills.map(s => s.name as string);
  } catch (error) {
    console.warn('Failed to fetch user skills from backend, using mock fallback.', error);
    return mockUsers[0].skills || [];
  }
};
