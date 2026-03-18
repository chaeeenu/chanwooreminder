'use client';

import { useState } from 'react';
import { Reminder, Priority } from '@/types';

function highlightText(text: string, query: string) {
  if (!query) return text;
  const regex = new RegExp(`(${query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
  const parts = text.split(regex);
  return parts.map((part, i) =>
    regex.test(part) ? <mark key={i} className="bg-yellow-200 rounded-sm px-0.5">{part}</mark> : part
  );
}

interface Props {
  reminder: Reminder;
  onToggle: (id: number) => void;
  onSelect: (reminder: Reminder) => void;
  selected: boolean;
  highlighted?: boolean;
  showListName?: boolean;
  searchQuery?: string;
}

const priorityMarks: Record<Priority, string> = {
  [Priority.NONE]: '',
  [Priority.LOW]: '!',
  [Priority.MEDIUM]: '!!',
  [Priority.HIGH]: '!!!',
};

export default function ReminderItem({ reminder, onToggle, onSelect, selected, highlighted, showListName, searchQuery }: Props) {
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
      className={`flex items-start gap-3.5 px-4 py-3 cursor-pointer transition-colors rounded-lg ${fading ? 'fade-out' : ''}`}
      style={{
        backgroundColor: selected ? '#E8E8ED' : highlighted ? '#F0F0F5' : 'transparent',
        outline: highlighted && !selected ? '2px solid #007AFF' : 'none',
        outlineOffset: '-2px',
        borderRadius: '8px',
      }}
      onClick={() => onSelect(reminder)}
      onMouseEnter={e => { if (!selected) e.currentTarget.style.backgroundColor = 'var(--hover-bg)'; }}
      onMouseLeave={e => { if (!selected) e.currentTarget.style.backgroundColor = 'transparent'; }}
    >
      {/* Checkbox */}
      <button
        onClick={handleToggle}
        className="w-[22px] h-[22px] rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-0.5 checkbox-spring"
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
            className={`text-[15px] leading-snug ${reminder.isCompleted ? 'line-through text-[#8E8E93]' : ''}`}
          >
            {searchQuery ? highlightText(reminder.title, searchQuery) : reminder.title}
          </span>
        </div>
        {reminder.memo && (
          <p className="text-xs text-[#8E8E93] mt-1 truncate">{reminder.memo}</p>
        )}
        <div className="flex items-center gap-2 flex-wrap">
          {reminder.dueDate && (
            <span
              className="text-xs mt-1 inline-block"
              style={{ color: isOverdue ? '#FF3B30' : '#8E8E93' }}
            >
              {formatDate(reminder.dueDate)}
              {reminder.dueTime && ` ${reminder.dueTime.substring(0, 5)}`}
            </span>
          )}
          {showListName && reminder.listName && (
            <span className="text-xs mt-1 inline-flex items-center gap-1 text-[#8E8E93]">
              <span className="w-2 h-2 rounded-full inline-block" style={{ backgroundColor: reminder.listColor }} />
              {reminder.listName}
            </span>
          )}
        </div>
        {reminder.tags && reminder.tags.length > 0 && (
          <div className="flex gap-1 mt-1 flex-wrap">
            {reminder.tags.map(tag => (
              <span key={tag.id} className="text-[10px] px-1.5 py-0.5 rounded-full" style={{ backgroundColor: tag.color + '20', color: tag.color }}>
                {tag.name}
              </span>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
