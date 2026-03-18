'use client';

import { SmartFilter } from '@/types';

interface Props {
  filter: SmartFilter;
  count: number;
  active: boolean;
  onClick: () => void;
}

const filterConfig: Record<SmartFilter, { label: string; color: string; icon: string }> = {
  today: { label: '오늘', color: '#007AFF', icon: '📅' },
  scheduled: { label: '예정', color: '#FF3B30', icon: '📆' },
  all: { label: '전체', color: '#1C1C1E', icon: '📋' },
  completed: { label: '완료됨', color: '#8E8E93', icon: '✓' },
};

export default function SmartFilterCard({ filter, count, active, onClick }: Props) {
  const config = filterConfig[filter];
  return (
    <button
      onClick={onClick}
      className="flex flex-col justify-between p-4 rounded-xl text-left transition-all"
      style={{
        backgroundColor: active ? '#E8E8ED' : '#FFFFFF',
        minHeight: '88px',
      }}
    >
      <div className="flex justify-between items-start w-full">
        <div
          className="w-7 h-7 rounded-full flex items-center justify-center text-white text-sm font-bold"
          style={{ backgroundColor: config.color }}
        >
          {filter === 'completed' ? '✓' : config.icon.charAt(0) === '�' ? '' : config.icon}
          {filter === 'today' && (
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          )}
          {filter === 'scheduled' && (
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          )}
          {filter === 'all' && (
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/></svg>
          )}
        </div>
        <span className="text-2xl font-bold" style={{ color: config.color }}>
          {count}
        </span>
      </div>
      <span className="text-xs font-semibold mt-2" style={{ color: '#8E8E93' }}>
        {config.label}
      </span>
    </button>
  );
}
