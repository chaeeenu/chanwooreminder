'use client';

import { useState, useCallback } from 'react';
import { ReminderList, SmartFilter, ViewState } from '@/types';
import SmartFilterCard from './SmartFilterCard';
import ListItem from './ListItem';
import ContextMenu, { ContextMenuItem } from './ContextMenu';

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
  collapsed?: boolean;
  onToggleCollapse?: () => void;
}

const smartFilters: SmartFilter[] = ['today', 'scheduled', 'all', 'completed'];

export default function Sidebar({ lists, view, smartCounts, onViewChange, onAddList, onEditList, onDeleteList, collapsed, onToggleCollapse }: Props) {
  const [contextMenu, setContextMenu] = useState<{ x: number; y: number; list: ReminderList } | null>(null);

  const handleContextMenu = useCallback((e: React.MouseEvent, list: ReminderList) => {
    e.preventDefault();
    setContextMenu({ x: e.clientX, y: e.clientY, list });
  }, []);

  const contextMenuItems: ContextMenuItem[] = contextMenu ? [
    { label: '편집', onClick: () => onEditList(contextMenu.list) },
    { label: '삭제', onClick: () => onDeleteList(contextMenu.list.id), danger: true },
  ] : [];

  if (collapsed) {
    return (
      <div
        className="w-16 h-screen flex flex-col items-center overflow-hidden border-r py-3 gap-2"
        style={{ backgroundColor: 'var(--sidebar-bg)', borderColor: 'var(--separator)' }}
      >
        <button onClick={onToggleCollapse} className="p-2 rounded-lg hover:bg-[#E8E8ED] mb-2">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#8E8E93" strokeWidth="2" strokeLinecap="round">
            <line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>
          </svg>
        </button>
        {lists.map(list => (
          <button
            key={list.id}
            onClick={() => onViewChange({ type: 'list', listId: list.id })}
            className="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 transition-transform hover:scale-110"
            style={{
              backgroundColor: list.color,
              boxShadow: view.type === 'list' && view.listId === list.id ? `0 0 0 2px var(--sidebar-bg), 0 0 0 4px ${list.color}` : 'none',
            }}
            title={list.name}
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round">
              <line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/>
              <line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/>
            </svg>
          </button>
        ))}
        <button onClick={onAddList} className="w-10 h-10 rounded-full flex items-center justify-center mt-auto" style={{ color: '#007AFF' }}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>
          </svg>
        </button>
      </div>
    );
  }

  return (
    <div
      className="w-72 h-screen flex flex-col overflow-hidden border-r"
      style={{ backgroundColor: 'var(--sidebar-bg)', borderColor: 'var(--separator)' }}
    >
      {/* Smart Filters */}
      <div className="p-4">
        {onToggleCollapse && (
          <button onClick={onToggleCollapse} className="p-1.5 rounded-lg hover:bg-[#E8E8ED] mb-3">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#8E8E93" strokeWidth="2" strokeLinecap="round">
              <line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>
            </svg>
          </button>
        )}
        <div className="grid grid-cols-2 gap-3">
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
      <div className="flex-1 overflow-y-auto px-4 pb-4">
        <div className="flex items-center justify-between mb-2 mt-1">
          <span className="text-xs font-bold text-[#8E8E93] uppercase tracking-wide">나의 목록</span>
        </div>
        {lists.length === 0 ? (
          <div className="flex flex-col items-center mt-8 text-[#8E8E93]">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" className="mb-2 opacity-40">
              <rect x="3" y="3" width="18" height="18" rx="3" />
              <line x1="12" y1="8" x2="12" y2="16" />
              <line x1="8" y1="12" x2="16" y2="12" />
            </svg>
            <p className="text-xs">목록 없음</p>
            <p className="text-xs opacity-60 mt-0.5">아래에서 목록을 추가하세요</p>
          </div>
        ) : (
          <div className="flex flex-col gap-1">
            {lists.map(list => (
              <ListItem
                key={list.id}
                list={list}
                active={view.type === 'list' && view.listId === list.id}
                onClick={() => onViewChange({ type: 'list', listId: list.id })}
                onContextMenu={e => handleContextMenu(e, list)}
                onDoubleClick={() => onEditList(list)}
              />
            ))}
          </div>
        )}
      </div>

      {/* Add List Button */}
      <div className="p-4 border-t" style={{ borderColor: 'var(--separator)' }}>
        <button
          onClick={onAddList}
          className="flex items-center gap-2 text-sm font-medium w-full px-3 py-2 rounded-lg transition-colors"
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

      {/* Context Menu */}
      {contextMenu && (
        <ContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          items={contextMenuItems}
          onClose={() => setContextMenu(null)}
        />
      )}
    </div>
  );
}
