import { ReminderList, Reminder } from '@/types';

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
