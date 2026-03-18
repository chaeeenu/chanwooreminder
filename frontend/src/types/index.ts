export enum Priority {
  NONE = 'NONE',
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
}

export interface ReminderList {
  id: number;
  name: string;
  color: string;
  icon: string;
  incompleteCount: number;
}

export interface Tag {
  id: number;
  name: string;
  color: string;
  reminderCount: number;
}

export interface Reminder {
  id: number;
  title: string;
  memo: string | null;
  dueDate: string | null;
  dueTime: string | null;
  priority: Priority;
  isCompleted: boolean;
  completedAt: string | null;
  listId: number;
  listName: string;
  listColor: string;
  parentId: number | null;
  tags: Tag[];
  sortOrder: number | null;
  createdAt: string;
  updatedAt: string;
}

export type SmartFilter = 'today' | 'scheduled' | 'all' | 'completed';

export type ViewState =
  | { type: 'smart'; filter: SmartFilter }
  | { type: 'list'; listId: number }
  | { type: 'tag'; tagId: number }
  | { type: 'search'; query: string };
