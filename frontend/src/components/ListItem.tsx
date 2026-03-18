'use client';

import { ReminderList } from '@/types';

interface Props {
  list: ReminderList;
  active: boolean;
  onClick: () => void;
  onContextMenu: (e: React.MouseEvent) => void;
  onDoubleClick?: () => void;
}

export default function ListItem({ list, active, onClick, onContextMenu, onDoubleClick }: Props) {
  return (
    <button
      onClick={onClick}
      onContextMenu={onContextMenu}
      onDoubleClick={onDoubleClick}
      className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-left transition-colors"
      style={{ backgroundColor: active ? '#E8E8ED' : 'transparent' }}
      onMouseEnter={e => { if (!active) e.currentTarget.style.backgroundColor = 'var(--hover-bg)'; }}
      onMouseLeave={e => { if (!active) e.currentTarget.style.backgroundColor = 'transparent'; }}
    >
      <div
        className="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0"
        style={{ backgroundColor: list.color }}
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round">
          <line x1="8" y1="6" x2="21" y2="6"/>
          <line x1="8" y1="12" x2="21" y2="12"/>
          <line x1="8" y1="18" x2="21" y2="18"/>
          <line x1="3" y1="6" x2="3.01" y2="6"/>
          <line x1="3" y1="12" x2="3.01" y2="12"/>
          <line x1="3" y1="18" x2="3.01" y2="18"/>
        </svg>
      </div>
      <span className="flex-1 text-sm font-medium truncate">{list.name}</span>
      <span className="text-xs text-[#8E8E93] flex-shrink-0">
        {list.incompleteCount > 0 ? list.incompleteCount : ''}
      </span>
    </button>
  );
}
