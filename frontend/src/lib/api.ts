import { ReminderList, Reminder, Tag } from '@/types';

const BASE_URL = 'http://localhost:8080/api';

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) throw new Error(`API error: ${res.status}`);
  if (res.status === 204) return undefined as T;
  return res.json();
}

// Lists
export const getLists = () => fetchJson<ReminderList[]>('/lists');
export const createList = (data: { name: string; color: string; icon: string }) =>
  fetchJson<ReminderList>('/lists', { method: 'POST', body: JSON.stringify(data) });
export const updateList = (id: number, data: { name?: string; color?: string; icon?: string }) =>
  fetchJson<ReminderList>(`/lists/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const deleteList = (id: number) =>
  fetchJson<void>(`/lists/${id}`, { method: 'DELETE' });

// Reminders
export const getReminders = (listId: number) =>
  fetchJson<Reminder[]>(`/lists/${listId}/reminders`);
export const createReminder = (listId: number, data: { title: string; memo?: string; dueDate?: string; dueTime?: string; priority?: string }) =>
  fetchJson<Reminder>(`/lists/${listId}/reminders`, { method: 'POST', body: JSON.stringify(data) });
export const updateReminder = (id: number, data: Partial<Reminder>) =>
  fetchJson<Reminder>(`/reminders/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const toggleReminder = (id: number) =>
  fetchJson<Reminder>(`/reminders/${id}/toggle`, { method: 'PATCH' });
export const deleteReminder = (id: number) =>
  fetchJson<void>(`/reminders/${id}`, { method: 'DELETE' });

// Smart filters
export const getTodayReminders = () => fetchJson<Reminder[]>('/reminders/today');
export const getScheduledReminders = () => fetchJson<Reminder[]>('/reminders/scheduled');
export const getAllReminders = () => fetchJson<Reminder[]>('/reminders/all');
export const getCompletedReminders = () => fetchJson<Reminder[]>('/reminders/completed');

// Search
export const searchReminders = (q: string) => fetchJson<Reminder[]>(`/reminders/search?q=${encodeURIComponent(q)}`);

// Reorder
export const reorderReminders = (items: { id: number; sortOrder: number }[]) =>
  fetchJson<void>('/reminders/reorder', { method: 'PUT', body: JSON.stringify({ items }) });

// Subtasks
export const getSubtasks = (parentId: number) => fetchJson<Reminder[]>(`/reminders/${parentId}/subtasks`);
export const createSubtask = (parentId: number, data: { title: string }) =>
  fetchJson<Reminder>(`/reminders/${parentId}/subtasks`, { method: 'POST', body: JSON.stringify(data) });

// Tags
export const getTags = () => fetchJson<Tag[]>('/tags');
export const createTag = (data: { name: string; color: string }) =>
  fetchJson<Tag>('/tags', { method: 'POST', body: JSON.stringify(data) });
export const updateTag = (id: number, data: { name?: string; color?: string }) =>
  fetchJson<Tag>(`/tags/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const deleteTag = (id: number) => fetchJson<void>(`/tags/${id}`, { method: 'DELETE' });
export const addTagToReminder = (reminderId: number, tagId: number) =>
  fetchJson<Reminder>(`/reminders/${reminderId}/tags/${tagId}`, { method: 'POST' });
export const removeTagFromReminder = (reminderId: number, tagId: number) =>
  fetchJson<Reminder>(`/reminders/${reminderId}/tags/${tagId}`, { method: 'DELETE' });
export const getRemindersByTag = (tagId: number) => fetchJson<Reminder[]>(`/tags/${tagId}/reminders`);
