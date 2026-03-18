'use client';

import { useState, useEffect } from 'react';
import { Reminder, Priority } from '@/types';

interface Props {
  reminder: Reminder;
  onUpdate: (id: number, data: Partial<Reminder>) => void;
  onDelete: (id: number) => void;
  onClose: () => void;
  isMobile?: boolean;
}

export default function ReminderDetail({ reminder, onUpdate, onDelete, onClose, isMobile }: Props) {
  const [title, setTitle] = useState(reminder.title);
  const [memo, setMemo] = useState(reminder.memo || '');
  const [dueDate, setDueDate] = useState(reminder.dueDate || '');
  const [dueTime, setDueTime] = useState(reminder.dueTime?.substring(0, 5) || '');
  const [priority, setPriority] = useState(reminder.priority);

  useEffect(() => {
    setTitle(reminder.title);
    setMemo(reminder.memo || '');
    setDueDate(reminder.dueDate || '');
    setDueTime(reminder.dueTime?.substring(0, 5) || '');
    setPriority(reminder.priority);
  }, [reminder]);

  const handleSave = () => {
    onUpdate(reminder.id, {
      title: title || reminder.title,
      memo: memo || null,
      dueDate: dueDate || null,
      dueTime: dueTime ? dueTime + ':00' : null,
      priority,
    });
  };

  if (isMobile) {
    return (
      <>
        <div className="fixed inset-0 bg-black/30 z-40" onClick={onClose} />
        <div className="fixed bottom-0 left-0 right-0 h-[70vh] bg-white rounded-t-2xl z-50 flex flex-col shadow-2xl modal-enter">
          <div className="flex items-center justify-center pt-2 pb-1">
            <div className="w-10 h-1 rounded-full bg-[#D1D1D6]" />
          </div>
          <div className="flex items-center justify-between px-4 py-2 border-b" style={{ borderColor: 'var(--separator)' }}>
            <span className="text-sm font-semibold">세부사항</span>
            <button onClick={onClose} className="text-sm font-medium" style={{ color: '#007AFF' }}>완료</button>
          </div>
          <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
            {renderForm()}
          </div>
          <div className="p-4 border-t" style={{ borderColor: 'var(--separator)' }}>
            <button
              onClick={() => { if (window.confirm('이 미리 알림을 삭제하시겠습니까?')) onDelete(reminder.id); }}
              className="w-full text-sm text-[#FF3B30] font-medium py-2 rounded-lg hover:bg-red-50 transition-colors"
            >
              미리 알림 삭제
            </button>
          </div>
        </div>
      </>
    );
  }

  function renderForm() {
    return (
      <>
        <div>
          <label className="text-xs font-medium text-[#8E8E93] mb-1.5 block">제목</label>
          <input
            value={title}
            onChange={e => setTitle(e.target.value)}
            onBlur={handleSave}
            className="w-full text-sm px-3.5 py-2.5 rounded-lg border bg-white outline-none focus:ring-2 focus:ring-blue-200"
            style={{ borderColor: 'var(--separator)' }}
          />
        </div>
        <div>
          <label className="text-xs font-medium text-[#8E8E93] mb-1.5 block">메모</label>
          <textarea
            value={memo}
            onChange={e => setMemo(e.target.value)}
            onBlur={handleSave}
            rows={3}
            className="w-full text-sm px-3.5 py-2.5 rounded-lg border bg-white outline-none resize-none focus:ring-2 focus:ring-blue-200"
            style={{ borderColor: 'var(--separator)' }}
          />
        </div>
        <div>
          <label className="text-xs font-medium text-[#8E8E93] mb-1.5 block">날짜</label>
          <input
            type="date"
            value={dueDate}
            onChange={e => setDueDate(e.target.value)}
            onBlur={handleSave}
            className="w-full text-sm px-3.5 py-2.5 rounded-lg border bg-white outline-none focus:ring-2 focus:ring-blue-200"
            style={{ borderColor: 'var(--separator)' }}
          />
        </div>
        <div>
          <label className="text-xs font-medium text-[#8E8E93] mb-1.5 block">시간</label>
          <input
            type="time"
            value={dueTime}
            onChange={e => setDueTime(e.target.value)}
            onBlur={handleSave}
            className="w-full text-sm px-3.5 py-2.5 rounded-lg border bg-white outline-none focus:ring-2 focus:ring-blue-200"
            style={{ borderColor: 'var(--separator)' }}
          />
        </div>
        <div>
          <label className="text-xs font-medium text-[#8E8E93] mb-1.5 block">우선순위</label>
          <select
            value={priority}
            onChange={e => {
              const newPriority = e.target.value as Priority;
              setPriority(newPriority);
              onUpdate(reminder.id, {
                title: title || reminder.title,
                memo: memo || null,
                dueDate: dueDate || null,
                dueTime: dueTime ? dueTime + ':00' : null,
                priority: newPriority,
              });
            }}
            className="w-full text-sm px-3.5 py-2.5 rounded-lg border bg-white outline-none focus:ring-2 focus:ring-blue-200"
            style={{ borderColor: 'var(--separator)' }}
          >
            <option value={Priority.NONE}>없음</option>
            <option value={Priority.LOW}>낮음 (!)</option>
            <option value={Priority.MEDIUM}>중간 (!!)</option>
            <option value={Priority.HIGH}>높음 (!!!)</option>
          </select>
        </div>
      </>
    );
  }

  return (
    <div className="w-80 border-l h-full flex flex-col" style={{ borderColor: 'var(--separator)', backgroundColor: '#FAFAFA' }}>
      {/* Header */}
      <div className="flex items-center justify-between px-5 py-4 border-b" style={{ borderColor: 'var(--separator)' }}>
        <span className="text-sm font-semibold">세부사항</span>
        <button onClick={onClose} className="text-[#8E8E93] hover:text-[#1C1C1E] text-lg leading-none">&times;</button>
      </div>

      {/* Form */}
      <div className="flex-1 overflow-y-auto p-5 flex flex-col gap-5">
        {renderForm()}
      </div>

      {/* Delete */}
      <div className="p-5 border-t" style={{ borderColor: 'var(--separator)' }}>
        <button
          onClick={() => { if (window.confirm('이 미리 알림을 삭제하시겠습니까?')) onDelete(reminder.id); }}
          className="w-full text-sm text-[#FF3B30] font-medium py-2 rounded-lg hover:bg-red-50 transition-colors"
        >
          미리 알림 삭제
        </button>
      </div>
    </div>
  );
}
