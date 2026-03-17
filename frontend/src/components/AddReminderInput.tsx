'use client';

import { useState } from 'react';

interface Props {
  listColor: string;
  onAdd: (title: string) => void;
}

export default function AddReminderInput({ listColor, onAdd }: Props) {
  const [active, setActive] = useState(false);
  const [title, setTitle] = useState('');

  const handleSubmit = () => {
    if (title.trim()) {
      onAdd(title.trim());
      setTitle('');
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSubmit();
    } else if (e.key === 'Escape') {
      setTitle('');
      setActive(false);
    }
  };

  if (!active) {
    return (
      <button
        onClick={() => setActive(true)}
        className="flex items-center gap-2 px-4 py-3 text-sm font-medium w-full"
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
    <div className="flex items-center gap-3 px-4 py-2.5">
      <div
        className="w-5 h-5 rounded-full border-2 flex-shrink-0"
        style={{ borderColor: listColor }}
      />
      <input
        autoFocus
        value={title}
        onChange={e => setTitle(e.target.value)}
        onKeyDown={handleKeyDown}
        onBlur={() => { handleSubmit(); setActive(false); }}
        placeholder="새로운 미리 알림"
        className="flex-1 text-sm outline-none bg-transparent"
      />
    </div>
  );
}
