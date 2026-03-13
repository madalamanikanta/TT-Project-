import { LucideIcon } from 'lucide-react';

interface CategoryCardProps {
  name: string;
  icon: LucideIcon;
}

export function CategoryCard({ name, icon: Icon }: CategoryCardProps) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md hover:border-primary transition-all cursor-pointer group">
      <div className="flex flex-col items-center text-center">
        <div className="bg-primary/10 rounded-full p-4 mb-3 group-hover:bg-primary/20 transition-colors">
          <Icon className="h-8 w-8 text-primary" />
        </div>
        <span className="text-gray-900 font-medium">{name}</span>
      </div>
    </div>
  );
}
