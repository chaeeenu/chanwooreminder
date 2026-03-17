'use client';

import { useState } from 'react';
import { Reminder, Priority } from '@/types';

interface Props {
  reminder: Reminder;
  onToggle: (id: number) => void;
  onSelect: (reminder: Reminder) => void;
  selected: boolean;
}

const priorityMarks: Record<Priority, string> = {
  [Priority.NONE]: '',
  [Priority.LOW]: '!',
  [Priority.MEDIUM]: '!!',
  [Priority.HIGH]: '!!!',
};

export default function ReminderItem({ reminder, onToggle, onSelect, selected }: Props) {
  const [fading, setFading] = useState(false);

  const handleToggle = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!reminder.isCompleted) {
      setFading(true);
      setTimeout(() => {
        onToggle(reminder.id);
        setFading(false);
      }, 500);
    } else {
      onToggle(reminder.id);
    }
  };

  const isOverdue = reminder.dueDate && !reminder.isCompleted &&
    new Date(reminder.dueDate) < new Date(new Date().toISOString().split('T')[0]);

  const formatDate = (date: string) => {
    const d = new Date(date);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateObj = new Date(date + 'T00:00:00');

    if (dateObj.getTime() === today.getTime()) return '오늘';
    if (dateObj.getTime() === tomorrow.getTime()) return '내일';
    return `${d.getMonth() + 1}/${d.getDate()}`;
  };

  return (
    <div
      className={`flex items-start gap-3 px-4 py-2.5 cursor-pointer transition-colors rounded-lg ${fading ? 'fade-out' : ''}`}
      style={{
        backgroundColor: selected ? '#E8E8ED' : 'transparent',
      }}
      onClick={() => onSelect(reminder)}
      onMouseEnter={e => { if (!selected) e.currentTarget.style.backgroundColor = 'var(--hover-bg)'; }}
      onMouseLeave={e => { if (!selected) e.currentTarget.style.backgroundColor = 'transparent'; }}
    >
      {/* Checkbox */}
      <button
        onClick={handleToggle}
        className="w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-0.5 transition-all"
        style={{
          borderColor: reminder.isCompleted ? reminder.listColor : reminder.listColor,
          backgroundColor: reminder.isCompleted || fading ? reminder.listColor : 'transparent',
        }}
      >
        {(reminder.isCompleted || fading) && (
          <svg
            width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"
            className={fading ? 'check-animate' : ''}
          >
            <polyline points="20 6 9 17 4 12"/>
          </svg>
        )}
      </button>

      {/* Content */}
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-1.5">
          {reminder.priority !== Priority.NONE && (
            <span className="text-xs font-bold" style={{ color: '#FF3B30' }}>
              {priorityMarks[reminder.priority]}
            </span>
          )}
          <span
            className={`text-sm leading-tight ${reminder.isCompleted ? 'line-through text-[#8E8E93]' : ''}`}
          >
            {reminder.title}
          </span>
        </div>
        {reminder.memo && (
          <p className="text-xs text-[#8E8E93] mt-0.5 truncate">{reminder.memo}</p>
        )}
        {reminder.dueDate && (
          <span
            className="text-xs mt-0.5 inline-block"
            style={{ color: isOverdue ? '#FF3B30' : '#8E8E93' }}
          >
            {formatDate(reminder.dueDate)}
            {reminder.dueTime && ` ${reminder.dueTime.substring(0, 5)}`}
          </span>
        )}
      </div>
    </div>
  );
}
