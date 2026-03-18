'use client';

import { useEffect, useRef, useCallback } from 'react';
import { Reminder } from '@/types';

export function useNotification(reminders: Reminder[]) {
  const notifiedRef = useRef<Set<number>>(new Set());

  const requestPermission = useCallback(async () => {
    if (typeof window === 'undefined' || !('Notification' in window)) return;
    if (Notification.permission === 'default') {
      await Notification.requestPermission();
    }
  }, []);

  useEffect(() => {
    requestPermission();
  }, [requestPermission]);

  useEffect(() => {
    if (typeof window === 'undefined' || !('Notification' in window)) return;
    if (Notification.permission !== 'granted') return;

    const checkInterval = setInterval(() => {
      const now = new Date();
      reminders.forEach(reminder => {
        if (reminder.isCompleted || !reminder.dueDate || !reminder.dueTime) return;
        if (notifiedRef.current.has(reminder.id)) return;

        const dueDateTime = new Date(`${reminder.dueDate}T${reminder.dueTime}`);
        const diff = dueDateTime.getTime() - now.getTime();

        if (diff <= 0 && diff > -60000) {
          new Notification('미리 알림', {
            body: reminder.title,
            icon: '/favicon.ico',
            tag: `reminder-${reminder.id}`,
          });
          notifiedRef.current.add(reminder.id);
        }
      });
    }, 30000);

    return () => clearInterval(checkInterval);
  }, [reminders]);
}
