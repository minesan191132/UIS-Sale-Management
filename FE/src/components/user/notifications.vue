<template>
  <section class="content-section notif-section">

    <!-- Header -->
    <div class="section-header notif-header-row">
      <div>
        <h1 class="section-title">Thông Báo</h1>
        <p class="section-desc">Cập nhật mới nhất về đơn hàng và tài khoản của bạn</p>
      </div>
      <button v-if="unreadCount > 0" class="btn-mark-all" @click="markAllRead">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12"/>
        </svg>
        Đánh dấu tất cả đã đọc
      </button>
    </div>
    <div class="section-divider"></div>

    <!-- Filter Tabs -->
    <div class="notif-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="notif-tab"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <span v-if="tab.key === 'unread' && unreadCount > 0" class="tab-badge">{{ unreadCount }}</span>
      </button>
    </div>

    <!-- Skeleton Loading -->
    <div v-if="isLoading" class="notif-list">
      <div v-for="i in 4" :key="i" class="notif-item notif-skeleton">
        <div class="sk-icon"></div>
        <div class="sk-body">
          <div class="sk-line sk-title"></div>
          <div class="sk-line sk-text"></div>
          <div class="sk-line sk-time"></div>
        </div>
      </div>
    </div>

    <!-- Notification List -->
    <div v-else-if="filteredNotifications.length > 0" class="notif-list">
      <div
        v-for="notif in filteredNotifications"
        :key="notif.id"
        class="notif-item"
        :class="{ unread: !notif.read }"
        @click="markRead(notif)"
      >
        <!-- Category Icon -->
        <div class="notif-icon-wrap" :class="`icon-${notif.type}`">
          <svg v-if="notif.type === 'order'" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
            <rect x="9" y="3" width="6" height="4" rx="1"/>
            <path d="M9 12h6M9 16h4"/>
          </svg>
          <svg v-else-if="notif.type === 'payment'" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
            <line x1="1" y1="10" x2="23" y2="10"/>
          </svg>
          <svg v-else-if="notif.type === 'system'" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
        </div>

        <!-- Content -->
        <div class="notif-content">
          <div class="notif-title-row">
            <span class="notif-title">{{ notif.title }}</span>
            <span v-if="!notif.read" class="unread-dot"></span>
          </div>
          <p class="notif-body">{{ notif.body }}</p>
          <span class="notif-time">{{ notif.time }}</span>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="notif-empty">
      <div class="empty-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="52" height="52" viewBox="0 0 24 24" fill="none"
          stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
        </svg>
      </div>
      <p class="empty-title">Không có thông báo nào</p>
      <p class="empty-sub">Bạn đã xem hết tất cả thông báo</p>
    </div>

  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { notificationsAPI } from '../../services/api'

const emit = defineEmits(['unread-count-changed'])
const router = useRouter()

const isLoading = ref(true)
const activeTab = ref('all')

const tabs = [
  { key: 'all', label: 'Tất cả' },
  { key: 'unread', label: 'Chưa đọc' },
  { key: 'read', label: 'Đã đọc' },
]

const notifications = ref([])

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

const filteredNotifications = computed(() => {
  if (activeTab.value === 'unread') return notifications.value.filter(n => !n.read)
  if (activeTab.value === 'read') return notifications.value.filter(n => n.read)
  return notifications.value
})

const normalizeType = (type) => {
  if (!type) return 'system'
  const value = String(type).toLowerCase()
  if (value === 'order') return 'order'
  if (value === 'payment') return 'payment'
  return 'system'
}

const formatRelativeTime = (dateTime) => {
  if (!dateTime) return 'Vừa xong'
  const date = new Date(dateTime)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMinutes = Math.max(1, Math.floor(diffMs / (1000 * 60)))

  if (diffMinutes < 60) return `${diffMinutes} phút trước`
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) return `${diffHours} giờ trước`
  const diffDays = Math.floor(diffHours / 24)
  if (diffDays < 7) return `${diffDays} ngày trước`
  const diffWeeks = Math.floor(diffDays / 7)
  if (diffWeeks < 5) return `${diffWeeks} tuần trước`
  return date.toLocaleDateString('vi-VN')
}

const mapNotification = (item) => {
  return {
    id: item.id,
    orderId: item.orderId,
    type: normalizeType(item.type),
    title: item.title,
    body: item.body,
    time: formatRelativeTime(item.createdAt),
    read: !!item.read,
    createdAt: item.createdAt,
  }
}

const loadNotifications = async () => {
  isLoading.value = true
  try {
    const data = await notificationsAPI.getMy()
    const rows = Array.isArray(data?.notifications) ? data.notifications : []
    notifications.value = rows.map(mapNotification)
      .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
    emit('unread-count-changed', Number(data?.unreadCount || 0))
  } catch (e) {
    console.error('Failed to load notifications:', e)
    notifications.value = []
    emit('unread-count-changed', 0)
  } finally {
    isLoading.value = false
  }
}

const markRead = async (notif) => {
  if (!notif) return
  try {
    if (!notif.read) {
      await notificationsAPI.markRead(notif.id)
      notif.read = true
      emit('unread-count-changed', unreadCount.value)
    }

    if (notif.orderId) {
      await router.push({
        path: '/my-orders',
        query: { orderId: String(notif.orderId) },
      })
    }
  } catch (e) {
    console.error('Failed to mark notification as read:', e)
  }
}

const markAllRead = async () => {
  try {
    await notificationsAPI.markAllRead()
    notifications.value.forEach(n => (n.read = true))
    emit('unread-count-changed', 0)
  } catch (e) {
    console.error('Failed to mark all notifications as read:', e)
  }
}

onMounted(loadNotifications)
</script>

<style scoped>
/* ── Section Shell ── */
.notif-section {
  padding: 28px 32px;
}

.section-header {
  margin-bottom: 10px;
}

.section-title {
  font-size: 19px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px;
}

.section-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.section-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0 0 20px;
}

/* Header row */
.notif-header-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-mark-all {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 13px;
  color: #2563eb;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.2s, border-color 0.2s;
  flex-shrink: 0;
  margin-top: 4px;
}

.btn-mark-all:hover {
  background: #eff6ff;
  border-color: #2563eb;
}

/* ── Filter Tabs ── */
.notif-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
}

.notif-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.notif-tab:hover {
  border-color: #2563eb;
  color: #2563eb;
}

.notif-tab.active {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
  font-weight: 500;
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: #fff;
  color: #2563eb;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
}

/* ── Notification List ── */
.notif-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.notif-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 14px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f8fafc;
}

.notif-item:last-child {
  border-bottom: none;
}

.notif-item:hover {
  background: #f8fafc;
}

.notif-item.unread {
  background: #eff6ff;
}

.notif-item.unread:hover {
  background: #dbeafe;
}

/* Icon */
.notif-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-order {
  background: #eff6ff;
  color: #2563eb;
}

.icon-payment {
  background: #f0fdf4;
  color: #16a34a;
}

.icon-system {
  background: #fefce8;
  color: #ca8a04;
}

.icon-general {
  background: #f1f5f9;
  color: #64748b;
}

/* Content */
.notif-content {
  flex: 1;
  min-width: 0;
}

.notif-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.notif-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #2563eb;
  flex-shrink: 0;
}

.notif-body {
  font-size: 13px;
  color: #475569;
  margin: 0 0 6px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notif-time {
  font-size: 12px;
  color: #94a3b8;
}

/* ── Skeleton ── */
.notif-skeleton {
  pointer-events: none;
}

.sk-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #e2e8f0;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.sk-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 4px;
}

.sk-line {
  height: 12px;
  background: #e2e8f0;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.sk-title { width: 55%; height: 14px; }
.sk-text  { width: 90%; }
.sk-time  { width: 25%; height: 10px; }

.sk-icon::after,
.sk-line::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.6) 50%, transparent 100%);
  animation: shimmer 1.4s infinite;
}

@keyframes shimmer {
  0%   { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* ── Empty State ── */
.notif-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 10px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #475569;
  margin: 0;
}

.empty-sub {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

/* ── Responsive ── */
@media (max-width: 576px) {
  .notif-section {
    padding: 20px 16px;
  }
  .notif-body {
    -webkit-line-clamp: 3;
  line-clamp: 3;
  }
}
</style>
