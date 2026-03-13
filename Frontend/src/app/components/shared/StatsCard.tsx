import { LucideIcon } from 'lucide-react';

interface StatsCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  description?: string;
  trend?: string;
}

export function StatsCard({ title, value, icon: Icon, description, trend }: StatsCardProps) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between mb-4">
        <div className="bg-primary/10 rounded-lg p-3">
          <Icon className="h-6 w-6 text-primary" />
        </div>
        {trend && (
          <span className="text-green-600 text-sm font-medium">{trend}</span>
        )}
      </div>
      <h3 className="text-2xl font-semibold text-gray-900 mb-1">{value}</h3>
      <p className="text-gray-600 text-sm">{title}</p>
      {description && (
        <p className="text-gray-500 text-xs mt-2">{description}</p>
      )}
    </div>
  );
}
