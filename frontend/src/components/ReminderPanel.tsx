'use client';

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
  onAddReminder: (title: string) => void;
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
}: Props) {
  const incompleteReminders = reminders.filter(r => !r.isCompleted);
  const completedReminders = reminders.filter(r => r.isCompleted);
  const isSmartCompleted = view.type === 'smart' && view.filter === 'completed';
  const displayReminders = isSmartCompleted ? completedReminders : incompleteReminders;

  return (
    <div className="flex-1 h-screen flex flex-col overflow-hidden">
      {/* Header */}
      <div className="px-6 pt-6 pb-2">
        <h1 className="text-2xl font-bold" style={{ color }}>
          {view.type === 'smart' ? filterLabels[view.filter] : title}
        </h1>
      </div>

      {/* Reminders */}
      <div className="flex-1 overflow-y-auto px-2">
        {displayReminders.length === 0 && (
          <div className="text-center text-[#8E8E93] text-sm mt-16">
            미리 알림 없음
          </div>
        )}
        {displayReminders.map(r => (
          <ReminderItem
            key={r.id}
            reminder={r}
            onToggle={onToggleReminder}
            onSelect={onSelectReminder}
            selected={selectedReminder?.id === r.id}
          />
        ))}

        {/* Completed section (for non-completed views) */}
        {!isSmartCompleted && completedReminders.length > 0 && (
          <div className="mt-4">
            <button
              onClick={onToggleShowCompleted}
              className="flex items-center gap-2 px-4 py-2 text-xs font-semibold text-[#8E8E93]"
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
          <AddReminderInput listColor={color} onAdd={onAddReminder} />
        </div>
      )}
    </div>
  );
}
