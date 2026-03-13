import { useState, useEffect } from 'react';
import { Sidebar } from '../../components/layout/Sidebar';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { SkillTag } from '../../components/shared/SkillTag';
import { Upload, Plus, Loader2 } from 'lucide-react';
import api from '../../../services/api';

interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: string;
  isActive: boolean;
}

interface Skill {
  id: number;
  name: string;
}

interface SkillsApiResponse {
  success: boolean;
  skills?: Skill[];
  data?: Skill[];
  skillCount?: number;
  error?: string;
}

export default function ProfilePage() {
  const [user, setUser] = useState<User | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: ''
  });
  const [skills, setSkills] = useState<Skill[]>([]);
  const [newSkill, setNewSkill] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const userData = localStorage.getItem('user');
        if (userData) {
          const parsedUser = JSON.parse(userData);
          setUser(parsedUser);
          setFormData({
            name: parsedUser.name || '',
            email: parsedUser.email || '',
            phone: parsedUser.phone || ''
          });

          // Fetch user skills
          const skillsResponse = await api.get<SkillsApiResponse>(`/users/${parsedUser.id}/skills`);
          // backend may return .skills or .data
          const rawSkills = Array.isArray(skillsResponse.data.skills)
            ? skillsResponse.data.skills
            : Array.isArray(skillsResponse.data.data)
            ? skillsResponse.data.data
            : [];
          if (skillsResponse.data.success && Array.isArray(rawSkills)) {
            setSkills(rawSkills);
          }
        }
      } catch (err: any) {
        setError('Failed to load profile');
        console.error('Profile error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleAddSkill = async () => {
    if (!newSkill.trim() || !user) return;

    try {
      const response = await api.post(`/users/${user.id}/skills`, {
        skillNames: [newSkill.trim()]
      });

      if (response.data.success) {
        // Refresh skills
        const skillsResponse = await api.get<SkillsApiResponse>(`/users/${user.id}/skills`);
        const rawSkills = Array.isArray(skillsResponse.data.skills)
          ? skillsResponse.data.skills
          : Array.isArray(skillsResponse.data.data)
          ? skillsResponse.data.data
          : [];
        if (skillsResponse.data.success && Array.isArray(rawSkills)) {
          setSkills(rawSkills);
        }
        setNewSkill('');
      }
    } catch (err: any) {
      setError('Failed to add skill');
    }
  };

  const handleRemoveSkill = async (skillId: number) => {
    if (!user) return;

    try {
      await api.delete(`/users/${user.id}/skills/${skillId}`);
      setSkills(skills.filter(skill => skill.id !== skillId));
    } catch (err: any) {
      setError('Failed to remove skill');
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    setSaving(true);
    try {
      const response = await api.put(`/auth/profile/${user.id}`, formData);
      if (response.data.success) {
        // Update local storage
        const updatedUser = { ...user, ...formData };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        setUser(updatedUser);
        alert('Profile updated successfully!');
      }
    } catch (err: any) {
      setError('Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen bg-gray-50">
        <Sidebar userRole="student" />
        <div className="flex-1 flex items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin" />
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar userRole="student" />
      
      <div className="flex-1">
        <div className="p-8">
          <div className="max-w-3xl">
            <h1 className="text-3xl font-semibold text-gray-900 mb-2">
              My Profile
            </h1>
            <p className="text-gray-600 mb-8">
              Manage your personal information and skills
            </p>

            <form onSubmit={handleSave} className="space-y-8">
              {/* Personal Information */}
              <div className="bg-white rounded-lg border border-gray-200 p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">
                  Personal Information
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-2">
                    <Label htmlFor="name">Full Name</Label>
                    <Input
                      id="name"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="phone">Phone</Label>
                    <Input
                      id="phone"
                      value={formData.phone}
                      onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              {/* Skills */}
              <div className="bg-white rounded-lg border border-gray-200 p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">
                  Skills
                </h2>
                <div className="space-y-4">
                  <div className="flex gap-2">
                    <Input
                      placeholder="Add a skill (e.g., Python)"
                      value={newSkill}
                      onChange={(e) => setNewSkill(e.target.value)}
                      onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleAddSkill())}
                    />
                    <Button type="button" onClick={handleAddSkill}>
                      <Plus className="h-5 w-5 mr-2" />
                      Add
                    </Button>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {skills.map((skill) => (
                      <SkillTag
                        key={skill.id}
                        skill={skill.name}
                        onRemove={() => handleRemoveSkill(skill.id)}
                      />
                    ))}
                  </div>
                </div>
              </div>

              {/* Resume Upload */}
              <div className="bg-white rounded-lg border border-gray-200 p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">
                  Resume
                </h2>
                <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-primary transition-colors cursor-pointer">
                  <Upload className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600 mb-2">
                    Click to upload or drag and drop
                  </p>
                  <p className="text-sm text-gray-500">
                    PDF, DOC up to 10MB
                  </p>
                </div>
                <p className="text-sm text-gray-600 mt-2">
                  Current resume: <span className="text-primary">resume_2026.pdf</span>
                </p>
              </div>

              {/* Save Button */}
              <div className="flex justify-end">
                <Button type="submit" size="lg" disabled={saving}>
                  {saving ? (
                    <>
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                      Saving...
                    </>
                  ) : (
                    'Save Changes'
                  )}
                </Button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
