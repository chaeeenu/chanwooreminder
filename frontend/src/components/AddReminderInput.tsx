'use client';

import { useState, useEffect, useRef, useImperativeHandle, forwardRef } from 'react';
import { Priority } from '@/types';

interface Props {
  listColor: string;
  onAdd: (title: string, extras?: { dueDate?: string; priority?: string }) => void;
}

export interface AddReminderInputHandle {
  activate: () => void;
}

const PRIORITY_CYCLE: Priority[] = [Priority.NONE, Priority.LOW, Priority.MEDIUM, Priority.HIGH];
const PRIORITY_LABELS: Record<Priority, string> = {
  [Priority.NONE]: '없음',
  [Priority.LOW]: '!',
  [Priority.MEDIUM]: '!!',
  [Priority.HIGH]: '!!!',
};

const AddReminderInput = forwardRef<AddReminderInputHandle, Props>(({ listColor, onAdd }, ref) => {
  const [active, setActive] = useState(false);
  const [title, setTitle] = useState('');
  const [quickDueDate, setQuickDueDate] = useState('');
  const [quickPriority, setQuickPriority] = useState<Priority>(Priority.NONE);
  const inputRef = useRef<HTMLInputElement>(null);

  useImperativeHandle(ref, () => ({
    activate: () => {
      setActive(true);
      setTimeout(() => inputRef.current?.focus(), 0);
    },
  }));

  const handleSubmit = () => {
    if (title.trim()) {
      const extras: { dueDate?: string; priority?: string } = {};
      if (quickDueDate) extras.dueDate = quickDueDate;
      if (quickPriority !== Priority.NONE) extras.priority = quickPriority;
      onAdd(title.trim(), Object.keys(extras).length > 0 ? extras : undefined);
      setTitle('');
      setQuickDueDate('');
      setQuickPriority(Priority.NONE);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSubmit();
    } else if (e.key === 'Escape') {
      setTitle('');
      setQuickDueDate('');
      setQuickPriority(Priority.NONE);
      setActive(false);
    }
  };

  const getTodayString = () => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  };

  const cyclePriority = () => {
    const idx = PRIORITY_CYCLE.indexOf(quickPriority);
    setQuickPriority(PRIORITY_CYCLE[(idx + 1) % PRIORITY_CYCLE.length]);
  };

  if (!active) {
    return (
      <button
        onClick={() => setActive(true)}
        className="flex items-center gap-2.5 px-5 py-3.5 text-sm font-medium w-full"
        style={{ color: listColor }}
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="8" x2="12" y2="16"/>
          <line x1="8" y1="12" x2="16" y2="12"/>
        </svg>
        새로운 미리 알림
      </button>
    );
  }

  return (
    <div className="px-5 py-3">
      <div className="flex items-center gap-3.5">
        <div
          className="w-[22px] h-[22px] rounded-full border-2 flex-shrink-0"
          style={{ borderColor: listColor }}
        />
        <input
          ref={inputRef}
          autoFocus
          value={title}
          onChange={e => setTitle(e.target.value)}
          onKeyDown={handleKeyDown}
          onBlur={() => { handleSubmit(); setActive(false); }}
          placeholder="새로운 미리 알림"
          className="flex-1 text-[15px] outline-none bg-transparent"
        />
      </div>
      {/* Quick setting buttons */}
      <div className="flex items-center gap-2.5 mt-2.5 ml-9">
        <button
          onMouseDown={e => e.preventDefault()}
          onClick={() => setQuickDueDate(quickDueDate ? '' : getTodayString())}
          className="flex items-center gap-1 px-2 py-1 rounded-md text-xs transition-colors"
          style={{
            backgroundColor: quickDueDate ? listColor : '#F2F2F7',
            color: quickDueDate ? 'white' : '#8E8E93',
          }}
        >
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
            <line x1="16" y1="2" x2="16" y2="6" />
            <line x1="8" y1="2" x2="8" y2="6" />
            <line x1="3" y1="10" x2="21" y2="10" />
          </svg>
          오늘
        </button>
        <button
          onMouseDown={e => e.preventDefault()}
          onClick={cyclePriority}
          className="flex items-center gap-1 px-2 py-1 rounded-md text-xs transition-colors"
          style={{
            backgroundColor: quickPriority !== Priority.NONE ? '#FF3B30' : '#F2F2F7',
            color: quickPriority !== Priority.NONE ? 'white' : '#8E8E93',
          }}
        >
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z" />
            <line x1="4" y1="22" x2="4" y2="15" />
          </svg>
          {quickPriority !== Priority.NONE ? PRIORITY_LABELS[quickPriority] : '우선순위'}
        </button>
      </div>
    </div>
  );
});

AddReminderInput.displayName = 'AddReminderInput';

export default AddReminderInput;
