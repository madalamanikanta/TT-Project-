import { X } from 'lucide-react';
import { Badge } from '../ui/badge';

interface SkillTagProps {
  skill: string;
  onRemove?: () => void;
}

export function SkillTag({ skill, onRemove }: SkillTagProps) {
  return (
    <Badge variant="secondary" className="px-3 py-1.5">
      {skill}
      {onRemove && (
        <button
          onClick={onRemove}
          className="ml-2 hover:text-destructive transition-colors"
        >
          <X className="h-3 w-3" />
        </button>
      )}
    </Badge>
  );
}
