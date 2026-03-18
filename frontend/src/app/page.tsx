'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import { ReminderList, Reminder, Tag, ViewState, Priority } from '@/types';
import * as api from '@/lib/api';
import { useMediaQuery } from '@/hooks/useMediaQuery';
import { useNotification } from '@/hooks/useNotification';
import Sidebar from '@/components/Sidebar';
import ReminderPanel from '@/components/ReminderPanel';
import ReminderDetail from '@/components/ReminderDetail';
import ListFormModal from '@/components/ListFormModal';
import { AddReminderInputHandle } from '@/components/AddReminderInput';

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [view, setView] = useState<ViewState>({ type: 'smart', filter: 'today' });
  const [selectedReminder, setSelectedReminder] = useState<Reminder | null>(null);
  const [showCompleted, setShowCompleted] = useState(false);
  const [showListModal, setShowListModal] = useState(false);
  const [editingList, setEditingList] = useState<ReminderList | null>(null);
  const [smartCounts, setSmartCounts] = useState({ today: 0, scheduled: 0, all: 0, completed: 0 });
  const [tags, setTags] = useState<Tag[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [navigateIndex, setNavigateIndex] = useState(-1);

  const addInputRef = useRef<AddReminderInputHandle | null>(null);

  const isMobile = useMediaQuery('(max-width: 767px)');
  const isTablet = useMediaQuery('(min-width: 768px) and (max-width: 1024px)');
  useNotification(reminders);

  // Load lists
  const loadLists = useCallback(async () => {
    const data = await api.getLists();
    setLists(data);
  }, []);

  // Load tags
  const loadTags = useCallback(async () => {
    const data = await api.getTags();
    setTags(data);
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
    } else if (view.type === 'list') {
      data = await api.getReminders(view.listId);
    } else if (view.type === 'tag') {
      data = await api.getRemindersByTag(view.tagId);
    } else if (view.type === 'search') {
      data = view.query.trim() ? await api.searchReminders(view.query) : [];
    } else {
      data = [];
    }
    setReminders(data);
  }, [view]);

  useEffect(() => { loadLists(); loadSmartCounts(); loadTags(); }, [loadLists, loadSmartCounts, loadTags]);
  useEffect(() => { loadReminders(); }, [loadReminders]);

  const refresh = async () => {
    await Promise.all([loadLists(), loadSmartCounts(), loadTags(), loadReminders()]);
  };

  // Auto-collapse sidebar on tablet
  useEffect(() => {
    setSidebarCollapsed(isTablet);
  }, [isTablet]);

  // Keyboard shortcuts
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const tag = (e.target as HTMLElement).tagName;
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return;

      const ctrlOrMeta = e.ctrlKey || e.metaKey;

      // Ctrl/Cmd+N: activate add reminder input
      if (ctrlOrMeta && e.key === 'n') {
        e.preventDefault();
        if (view.type === 'list') {
          addInputRef.current?.activate();
        }
        return;
      }

      // Escape: close detail panel or modal
      if (e.key === 'Escape') {
        if (showListModal) {
          setShowListModal(false);
          setEditingList(null);
        } else if (selectedReminder) {
          setSelectedReminder(null);
        }
        setNavigateIndex(-1);
        return;
      }

      // Delete/Backspace: delete selected reminder
      if ((e.key === 'Delete' || e.key === 'Backspace') && selectedReminder) {
        e.preventDefault();
        if (window.confirm('이 미리 알림을 삭제하시겠습니까?')) {
          handleDeleteReminder(selectedReminder.id);
        }
        return;
      }

      // Arrow navigation
      const incompleteReminders = reminders.filter(r => !r.isCompleted);
      const isSmartCompleted = view.type === 'smart' && view.filter === 'completed';
      const navReminders = isSmartCompleted ? reminders.filter(r => r.isCompleted) : incompleteReminders;

      if (e.key === 'ArrowDown' && navReminders.length > 0) {
        e.preventDefault();
        const newIdx = navigateIndex < navReminders.length - 1 ? navigateIndex + 1 : 0;
        setNavigateIndex(newIdx);
        setSelectedReminder(navReminders[newIdx]);
        return;
      }

      if (e.key === 'ArrowUp' && navReminders.length > 0) {
        e.preventDefault();
        const newIdx = navigateIndex > 0 ? navigateIndex - 1 : navReminders.length - 1;
        setNavigateIndex(newIdx);
        setSelectedReminder(navReminders[newIdx]);
        return;
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [view, selectedReminder, showListModal, reminders, navigateIndex]);

  // Reset navigate index when view changes
  useEffect(() => {
    setNavigateIndex(-1);
  }, [view]);

  // View info
  const getViewTitle = () => {
    if (view.type === 'list') {
      return lists.find(l => l.id === view.listId)?.name || '';
    }
    if (view.type === 'tag') {
      return tags.find(t => t.id === view.tagId)?.name || '';
    }
    if (view.type === 'search') {
      return `"${view.query}" 검색 결과`;
    }
    return '';
  };

  const getViewColor = () => {
    if (view.type === 'list') {
      return lists.find(l => l.id === view.listId)?.color || '#007AFF';
    }
    if (view.type === 'tag') {
      return tags.find(t => t.id === view.tagId)?.color || '#007AFF';
    }
    if (view.type === 'search') {
      return '#8E8E93';
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
        await api.updateReminder(id, {});
        setSelectedReminder(null);
      } catch {
        setSelectedReminder(null);
      }
    }
  };

  const handleAddReminder = async (title: string, extras?: { dueDate?: string; priority?: string }) => {
    if (view.type !== 'list') return;
    const created = await api.createReminder(view.listId, {
      title,
      dueDate: extras?.dueDate,
      priority: extras?.priority,
    });
    await refresh();
    setSelectedReminder(created);
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

  const handleViewChange = (v: ViewState) => {
    setView(v);
    setSelectedReminder(null);
    setNavigateIndex(-1);
    if (isMobile) setSidebarOpen(false);
  };

  return (
    <div className="flex h-screen overflow-hidden">
      {/* Mobile hamburger */}
      {isMobile && !sidebarOpen && (
        <button
          onClick={() => setSidebarOpen(true)}
          className="fixed top-3 left-3 z-30 p-2 rounded-lg bg-white shadow-md"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#1C1C1E" strokeWidth="2" strokeLinecap="round">
            <line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>
          </svg>
        </button>
      )}

      {/* Sidebar */}
      {isMobile ? (
        sidebarOpen && (
          <>
            <div className="fixed inset-0 bg-black/30 z-40" onClick={() => setSidebarOpen(false)} />
            <div className="fixed left-0 top-0 bottom-0 z-50 modal-enter">
              <Sidebar
                lists={lists}
                tags={tags}
                view={view}
                smartCounts={smartCounts}
                onViewChange={handleViewChange}
                onAddList={() => { setEditingList(null); setShowListModal(true); }}
                onEditList={(list) => { setEditingList(list); setShowListModal(true); }}
                onDeleteList={handleDeleteList}
                onSearch={(q) => handleViewChange({ type: 'search', query: q })}
              />
            </div>
          </>
        )
      ) : (
        <Sidebar
          lists={lists}
          tags={tags}
          view={view}
          smartCounts={smartCounts}
          onViewChange={handleViewChange}
          onAddList={() => { setEditingList(null); setShowListModal(true); }}
          onEditList={(list) => { setEditingList(list); setShowListModal(true); }}
          onDeleteList={handleDeleteList}
          collapsed={sidebarCollapsed}
          onToggleCollapse={() => setSidebarCollapsed(!sidebarCollapsed)}
          onSearch={(q) => handleViewChange({ type: 'search', query: q })}
        />
      )}

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
        addInputRef={addInputRef}
        selectedIndex={navigateIndex}
      />

      {selectedReminder && (
        isMobile ? (
          <ReminderDetail
            reminder={selectedReminder}
            onUpdate={handleUpdateReminder}
            onDelete={handleDeleteReminder}
            onClose={() => setSelectedReminder(null)}
            isMobile
          />
        ) : (
          <ReminderDetail
            reminder={selectedReminder}
            onUpdate={handleUpdateReminder}
            onDelete={handleDeleteReminder}
            onClose={() => setSelectedReminder(null)}
          />
        )
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
