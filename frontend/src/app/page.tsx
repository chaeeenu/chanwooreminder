'use client';

import { useState, useEffect, useCallback } from 'react';
import { ReminderList, Reminder, ViewState } from '@/types';
import * as api from '@/lib/api';
import Sidebar from '@/components/Sidebar';
import ReminderPanel from '@/components/ReminderPanel';
import ReminderDetail from '@/components/ReminderDetail';
import ListFormModal from '@/components/ListFormModal';

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [view, setView] = useState<ViewState>({ type: 'smart', filter: 'today' });
  const [selectedReminder, setSelectedReminder] = useState<Reminder | null>(null);
  const [showCompleted, setShowCompleted] = useState(false);
  const [showListModal, setShowListModal] = useState(false);
  const [editingList, setEditingList] = useState<ReminderList | null>(null);
  const [smartCounts, setSmartCounts] = useState({ today: 0, scheduled: 0, all: 0, completed: 0 });

  // Load lists
  const loadLists = useCallback(async () => {
    const data = await api.getLists();
    setLists(data);
  }, []);

  // Load smart counts
  const loadSmartCounts = useCallback(async () => {
    const [today, scheduled, all, completed] = await Promise.all([
      api.getTodayReminders(),
      api.getScheduledReminders(),
      api.getAllReminders(),
      api.getCompletedReminders(),
    ]);
    setSmartCounts({
      today: today.length,
      scheduled: scheduled.length,
      all: all.length,
      completed: completed.length,
    });
  }, []);

  // Load reminders based on current view
  const loadReminders = useCallback(async () => {
    let data: Reminder[];
    if (view.type === 'smart') {
      switch (view.filter) {
        case 'today': data = await api.getTodayReminders(); break;
        case 'scheduled': data = await api.getScheduledReminders(); break;
        case 'all': data = await api.getAllReminders(); break;
        case 'completed': data = await api.getCompletedReminders(); break;
      }
    } else {
      data = await api.getReminders(view.listId);
    }
    setReminders(data);
  }, [view]);

  useEffect(() => { loadLists(); loadSmartCounts(); }, [loadLists, loadSmartCounts]);
  useEffect(() => { loadReminders(); }, [loadReminders]);

  const refresh = async () => {
    await Promise.all([loadLists(), loadSmartCounts(), loadReminders()]);
  };

  // View info
  const getViewTitle = () => {
    if (view.type === 'list') {
      return lists.find(l => l.id === view.listId)?.name || '';
    }
    return '';
  };

  const getViewColor = () => {
    if (view.type === 'list') {
      return lists.find(l => l.id === view.listId)?.color || '#007AFF';
    }
    const colors = { today: '#007AFF', scheduled: '#FF3B30', all: '#1C1C1E', completed: '#8E8E93' };
    return colors[view.filter];
  };

  // Handlers
  const handleToggleReminder = async (id: number) => {
    await api.toggleReminder(id);
    await refresh();
    if (selectedReminder?.id === id) {
      try {
        const updated = await api.updateReminder(id, {});
        setSelectedReminder(null);
      } catch {
        setSelectedReminder(null);
      }
    }
  };

  const handleAddReminder = async (title: string) => {
    if (view.type !== 'list') return;
    await api.createReminder(view.listId, { title });
    await refresh();
  };

  const handleUpdateReminder = async (id: number, data: Partial<Reminder>) => {
    const updated = await api.updateReminder(id, data);
    setSelectedReminder(updated);
    await refresh();
  };

  const handleDeleteReminder = async (id: number) => {
    await api.deleteReminder(id);
    setSelectedReminder(null);
    await refresh();
  };

  const handleSaveList = async (data: { name: string; color: string; icon: string }) => {
    if (editingList) {
      await api.updateList(editingList.id, data);
    } else {
      const newList = await api.createList(data);
      setView({ type: 'list', listId: newList.id });
    }
    setShowListModal(false);
    setEditingList(null);
    await refresh();
  };

  const handleDeleteList = async (id: number) => {
    await api.deleteList(id);
    setView({ type: 'smart', filter: 'today' });
    await refresh();
  };

  return (
    <div className="flex h-screen overflow-hidden">
      <Sidebar
        lists={lists}
        view={view}
        smartCounts={smartCounts}
        onViewChange={(v) => { setView(v); setSelectedReminder(null); }}
        onAddList={() => { setEditingList(null); setShowListModal(true); }}
        onEditList={(list) => { setEditingList(list); setShowListModal(true); }}
        onDeleteList={handleDeleteList}
      />

      <ReminderPanel
        view={view}
        title={getViewTitle()}
        color={getViewColor()}
        reminders={reminders}
        selectedReminder={selectedReminder}
        showCompleted={showCompleted}
        onToggleShowCompleted={() => setShowCompleted(!showCompleted)}
        onToggleReminder={handleToggleReminder}
        onSelectReminder={setSelectedReminder}
        onAddReminder={handleAddReminder}
      />

      {selectedReminder && (
        <ReminderDetail
          reminder={selectedReminder}
          onUpdate={handleUpdateReminder}
          onDelete={handleDeleteReminder}
          onClose={() => setSelectedReminder(null)}
        />
      )}

      {showListModal && (
        <ListFormModal
          editingList={editingList}
          onSave={handleSaveList}
          onClose={() => { setShowListModal(false); setEditingList(null); }}
        />
      )}
    </div>
  );
}
