'use client';

import { useState, useEffect } from 'react';
import { ReminderList } from '@/types';

interface Props {
  editingList: ReminderList | null;
  onSave: (data: { name: string; color: string; icon: string }) => void;
  onClose: () => void;
}

const COLORS = [
  '#FF3B30', '#FF9500', '#FFCC00', '#34C759',
  '#00C7BE', '#007AFF', '#AF52DE', '#FF2D55',
  '#A2845E', '#8E8E93', '#5856D6', '#30B0C4',
];

const ICONS = [
  'list.bullet', 'bookmark', 'mappin', 'gift',
  'birthday.cake', 'backpack', 'book', 'graduationcap',
  'pencil', 'star', 'heart', 'flag',
];

const iconSymbols: Record<string, string> = {
  'list.bullet': '☰',
  'bookmark': '🔖',
  'mappin': '📍',
  'gift': '🎁',
  'birthday.cake': '🎂',
  'backpack': '🎒',
  'book': '📖',
  'graduationcap': '🎓',
  'pencil': '✏️',
  'star': '⭐',
  'heart': '❤️',
  'flag': '🚩',
};

export default function ListFormModal({ editingList, onSave, onClose }: Props) {
  const [name, setName] = useState(editingList?.name || '');
  const [color, setColor] = useState(editingList?.color || '#007AFF');
  const [icon, setIcon] = useState(editingList?.icon || 'list.bullet');

  useEffect(() => {
    if (editingList) {
      setName(editingList.name);
      setColor(editingList.color);
      setIcon(editingList.icon);
    }
  }, [editingList]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    onSave({ name: name.trim(), color, icon });
  };

  return (
    <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50" onClick={onClose}>
      <div
        className="bg-white rounded-2xl w-80 shadow-2xl overflow-hidden"
        onClick={e => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-3 border-b" style={{ borderColor: 'var(--separator)' }}>
          <button onClick={onClose} className="text-sm" style={{ color: '#007AFF' }}>취소</button>
          <span className="text-sm font-semibold">{editingList ? '목록 편집' : '새로운 목록'}</span>
          <button
            onClick={handleSubmit}
            className="text-sm font-semibold"
            style={{ color: name.trim() ? '#007AFF' : '#8E8E93' }}
            disabled={!name.trim()}
          >
            완료
          </button>
        </div>

        <div className="p-5 flex flex-col items-center gap-4">
          {/* Icon Preview */}
          <div
            className="w-20 h-20 rounded-full flex items-center justify-center text-3xl"
            style={{ backgroundColor: color }}
          >
            <span className="filter brightness-0 invert">{iconSymbols[icon] || '☰'}</span>
          </div>

          {/* Name */}
          <input
            autoFocus
            value={name}
            onChange={e => setName(e.target.value)}
            placeholder="목록 이름"
            className="w-full text-center text-sm px-4 py-2.5 rounded-xl outline-none"
            style={{ backgroundColor: '#F2F2F7' }}
          />

          {/* Colors */}
          <div className="grid grid-cols-6 gap-2.5 w-full">
            {COLORS.map(c => (
              <button
                key={c}
                onClick={() => setColor(c)}
                className="w-8 h-8 rounded-full mx-auto flex items-center justify-center transition-transform"
                style={{
                  backgroundColor: c,
                  transform: color === c ? 'scale(1.2)' : 'scale(1)',
                  boxShadow: color === c ? `0 0 0 2px white, 0 0 0 4px ${c}` : 'none',
                }}
              />
            ))}
          </div>

          {/* Icons */}
          <div className="grid grid-cols-6 gap-2.5 w-full">
            {ICONS.map(i => (
              <button
                key={i}
                onClick={() => setIcon(i)}
                className="w-8 h-8 rounded-full mx-auto flex items-center justify-center text-sm transition-all"
                style={{
                  backgroundColor: icon === i ? color : '#F2F2F7',
                  color: icon === i ? 'white' : '#1C1C1E',
                  transform: icon === i ? 'scale(1.1)' : 'scale(1)',
                }}
              >
                {iconSymbols[i] || '☰'}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
