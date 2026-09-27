// Single Group Chat: "💬 دفعة الرحلة إلى 3000$"
// Messages older than 48 hours are automatically purged.

export interface ChatMessage {
  id: string;
  senderId: number;
  senderName: string;
  text?: string;
  imageUrl?: string;
  timestamp: number;
}

const STORAGE_KEY = 'sami_chat_messages_v2';
const CHANNEL_NAME = 'sami_cohort_chat_channel';
const FORTY_EIGHT_HOURS_MS = 48 * 60 * 60 * 1000;

// Broadcast channel for instantaneous cross-tab/cross-window realtime sync
let broadcastChannel: BroadcastChannel | null = null;
try {
  if (typeof window !== 'undefined' && 'BroadcastChannel' in window) {
    broadcastChannel = new BroadcastChannel(CHANNEL_NAME);
  }
} catch (e) {
  console.warn('BroadcastChannel not supported', e);
}

/**
 * Filter out messages older than 48 hours as required.
 */
export function filterExpiredMessages(messages: ChatMessage[]): ChatMessage[] {
  const now = Date.now();
  return messages.filter((m) => now - m.timestamp < FORTY_EIGHT_HOURS_MS);
}

/**
 * Load local messages, purge expired ones, and return valid messages sorted oldest to newest.
 * Starts completely empty on first launch as requested by the user.
 */
export function loadMessages(): ChatMessage[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return [];
    }
    const parsed: ChatMessage[] = JSON.parse(raw);
    const valid = filterExpiredMessages(parsed);
    // If some were purged, save updated list
    if (valid.length !== parsed.length) {
      saveMessages(valid);
    }
    return valid.sort((a, b) => a.timestamp - b.timestamp);
  } catch (err) {
    console.error('Failed to load chat messages', err);
    return [];
  }
}

/**
 * Save messages to localStorage.
 */
export function saveMessages(messages: ChatMessage[]): void {
  try {
    const valid = filterExpiredMessages(messages);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(valid));
  } catch (err) {
    console.error('Failed to save chat messages', err);
  }
}

/**
 * Send a new message, persist it, notify via BroadcastChannel and server endpoint if available.
 */
export async function sendChatMessage(
  senderId: number,
  senderName: string,
  content: { text?: string; imageUrl?: string }
): Promise<ChatMessage> {
  const newMessage: ChatMessage = {
    id: `msg_${Date.now()}_${Math.random().toString(36).substring(2, 8)}`,
    senderId,
    senderName,
    text: content.text?.trim() || undefined,
    imageUrl: content.imageUrl,
    timestamp: Date.now(),
  };

  const current = loadMessages();
  const updated = [...current, newMessage];
  saveMessages(updated);

  // Broadcast to other tabs
  if (broadcastChannel) {
    try {
      broadcastChannel.postMessage({ type: 'NEW_MESSAGE', message: newMessage });
    } catch (e) {
      console.warn('Failed to broadcast message', e);
    }
  }

  // Also try syncing to backend server if available
  try {
    fetch('/api/chat/messages', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newMessage),
    }).catch(() => {
      // offline or local mode
    });
  } catch {
    // Ignore fetch error
  }

  return newMessage;
}

/**
 * Subscribe to realtime incoming messages.
 */
export function subscribeToChat(onNewMessage: (msg: ChatMessage) => void): () => void {
  if (!broadcastChannel) return () => {};

  const handler = (event: MessageEvent) => {
    if (event.data && event.data.type === 'NEW_MESSAGE') {
      onNewMessage(event.data.message);
    }
  };

  broadcastChannel.addEventListener('message', handler);
  return () => {
    broadcastChannel?.removeEventListener('message', handler);
  };
}
