'use client';

import { ReminderList, SmartFilter, ViewState } from '@/types';
import SmartFilterCard from './SmartFilterCard';
import ListItem from './ListItem';

interface SmartCounts {
  today: number;
  scheduled: number;
  all: number;
  completed: number;
}

interface Props {
  lists: ReminderList[];
  view: ViewState;
  smartCounts: SmartCounts;
  onViewChange: (view: ViewState) => void;
  onAddList: () => void;
  onEditList: (list: ReminderList) => void;
  onDeleteList: (id: number) => void;
}

const smartFilters: SmartFilter[] = ['today', 'scheduled', 'all', 'completed'];

export default function Sidebar({ lists, view, smartCounts, onViewChange, onAddList, onEditList, onDeleteList }: Props) {
  return (
    <div
      className="w-72 h-screen flex flex-col overflow-hidden border-r"
      style={{ backgroundColor: 'var(--sidebar-bg)', borderColor: 'var(--separator)' }}
    >
      {/* Smart Filters */}
      <div className="p-3">
        <div className="grid grid-cols-2 gap-2">
          {smartFilters.map(f => (
            <SmartFilterCard
              key={f}
              filter={f}
              count={smartCounts[f]}
              active={view.type === 'smart' && view.filter === f}
              onClick={() => onViewChange({ type: 'smart', filter: f })}
            />
          ))}
        </div>
      </div>

      {/* My Lists */}
      <div className="flex-1 overflow-y-auto px-3 pb-3">
        <div className="flex items-center justify-between mb-1 mt-2">
          <span className="text-xs font-bold text-[#8E8E93] uppercase tracking-wide">나의 목록</span>
        </div>
        <div className="flex flex-col gap-0.5">
          {lists.map(list => (
            <ListItem
              key={list.id}
              list={list}
              active={view.type === 'list' && view.listId === list.id}
              onClick={() => onViewChange({ type: 'list', listId: list.id })}
              onContextMenu={e => {
                e.preventDefault();
                const action = window.confirm(`"${list.name}" 목록을 삭제하시겠습니까?`);
                if (action) onDeleteList(list.id);
              }}
            />
          ))}
        </div>
      </div>

      {/* Add List Button */}
      <div className="p-3 border-t" style={{ borderColor: 'var(--separator)' }}>
        <button
          onClick={onAddList}
          className="flex items-center gap-2 text-sm font-medium w-full px-2 py-1.5 rounded-lg transition-colors"
          style={{ color: '#007AFF' }}
          onMouseEnter={e => e.currentTarget.style.backgroundColor = 'var(--hover-bg)'}
          onMouseLeave={e => e.currentTarget.style.backgroundColor = 'transparent'}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="16"/>
            <line x1="8" y1="12" x2="16" y2="12"/>
          </svg>
          목록 추가
        </button>
      </div>
    </div>
  );
}
