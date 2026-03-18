'use client';

import { useRef } from 'react';
import { Reminder, ViewState, SmartFilter } from '@/types';
import ReminderItem from './ReminderItem';
import AddReminderInput from './AddReminderInput';

interface Props {
  view: ViewState;
  title: string;
  color: string;
  reminders: Reminder[];
  selectedReminder: Reminder | null;
  showCompleted: boolean;
  onToggleShowCompleted: () => void;
  onToggleReminder: (id: number) => void;
  onSelectReminder: (reminder: Reminder) => void;
  onAddReminder: (title: string, extras?: { dueDate?: string; priority?: string }) => void;
  addInputRef?: React.RefObject<{ activate: () => void } | null>;
  selectedIndex?: number;
}

const filterLabels: Record<SmartFilter, string> = {
  today: '오늘',
  scheduled: '예정',
  all: '전체',
  completed: '완료됨',
};

export default function ReminderPanel({
  view, title, color, reminders, selectedReminder,
  showCompleted, onToggleShowCompleted,
  onToggleReminder, onSelectReminder, onAddReminder,
  addInputRef, selectedIndex,
}: Props) {
  const incompleteReminders = reminders.filter(r => !r.isCompleted);
  const completedReminders = reminders.filter(r => r.isCompleted);
  const isSmartCompleted = view.type === 'smart' && view.filter === 'completed';
  const displayReminders = isSmartCompleted ? completedReminders : incompleteReminders;

  const viewIdentity = view.type === 'smart' ? `smart-${view.filter}`
    : view.type === 'list' ? `list-${view.listId}`
    : view.type === 'tag' ? `tag-${view.tagId}`
    : `search-${view.query}`;

  return (
    <div className="flex-1 h-screen flex flex-col overflow-hidden">
      {/* Header */}
      <div className="px-8 pt-8 pb-4">
        <h1 className="text-3xl font-bold tracking-tight" style={{ color }}>
          {view.type === 'smart' ? filterLabels[view.filter] : title}
        </h1>
        {view.type === 'search' && (
          <p className="text-sm text-[#8E8E93] mt-1">{reminders.length}개의 결과</p>
        )}
      </div>

      {/* Reminders */}
      <div key={viewIdentity} className="flex-1 overflow-y-auto px-4 view-fade-in">
        {displayReminders.length === 0 && (
          <div className="flex flex-col items-center justify-center mt-24 text-[#8E8E93]">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" className="mb-3 opacity-40">
              <rect x="3" y="3" width="18" height="18" rx="3" />
              <line x1="8" y1="9" x2="16" y2="9" />
              <line x1="8" y1="13" x2="13" y2="13" />
            </svg>
            <p className="text-sm font-medium">미리 알림 없음</p>
            {view.type === 'list' && (
              <p className="text-xs mt-1 opacity-70">아래 버튼으로 새 미리 알림을 추가하세요</p>
            )}
          </div>
        )}
        {displayReminders.map((r, idx) => (
          <ReminderItem
            key={r.id}
            reminder={r}
            onToggle={onToggleReminder}
            onSelect={onSelectReminder}
            selected={selectedReminder?.id === r.id}
            highlighted={selectedIndex === idx}
            showListName={view.type === 'search' || view.type === 'tag' || view.type === 'smart'}
            searchQuery={view.type === 'search' ? view.query : undefined}
          />
        ))}

        {/* Completed section (for non-completed views) */}
        {!isSmartCompleted && completedReminders.length > 0 && (
          <div className="mt-6">
            <button
              onClick={onToggleShowCompleted}
              className="flex items-center gap-2 px-4 py-2.5 text-xs font-semibold text-[#8E8E93]"
            >
              <svg
                width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round"
                style={{ transform: showCompleted ? 'rotate(90deg)' : 'rotate(0deg)', transition: 'transform 0.2s' }}
              >
                <polyline points="9 18 15 12 9 6"/>
              </svg>
              완료됨 ({completedReminders.length})
            </button>
            {showCompleted && completedReminders.map(r => (
              <ReminderItem
                key={r.id}
                reminder={r}
                onToggle={onToggleReminder}
                onSelect={onSelectReminder}
                selected={selectedReminder?.id === r.id}
              />
            ))}
          </div>
        )}
      </div>

      {/* Add Reminder */}
      {view.type === 'list' && (
        <div className="border-t" style={{ borderColor: 'var(--separator)' }}>
          <AddReminderInput listColor={color} onAdd={onAddReminder} ref={addInputRef} />
        </div>
      )}
    </div>
  );
}
