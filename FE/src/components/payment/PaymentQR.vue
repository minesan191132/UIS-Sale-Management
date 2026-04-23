<template>
  <div class="payment-page">
    <!-- ── Loading ───────────────────────────────────── -->
    <div v-if="loading" class="state-card">
      <div class="spinner"></div>
      <p>Đang tải thông tin đơn hàng...</p>
    </div>

    <!-- ── Error ─────────────────────────────────────── -->
    <div v-else-if="error" class="state-card state-error">
      <span class="state-icon">⚠️</span>
      <h2>Không thể tải đơn hàng</h2>
      <p class="sub">{{ error }}</p>
      <button class="btn-retry" @click="init">Thử lại</button>
    </div>

    <!-- ── Success / Paid ─────────────────────────────── -->
    <div v-else-if="paid" class="state-card state-success">
      <div class="success-circle">✓</div>
      <h2>Thanh toán thành công!</h2>
      <p class="sub">Đơn hàng <strong>#{{ info.orderNumber }}</strong> đã được xác nhận thanh toán.</p>
      <div class="success-detail">
        <div class="srow">
          <span>Số tiền đã thanh toán</span>
          <span class="green">{{ formatCurrency(info.totalPrice) }}</span>
        </div>
      </div>
      <p class="sub mt-8">Tự động chuyển trang sau {{ redirectCountdown }} giây...</p>
      <button class="btn-retry" style="margin-top:12px" @click="goBack">Xem đơn hàng</button>
    </div>

    <!-- ── Timeout ─────────────────────────────────────── -->
    <div v-else-if="timedOut" class="state-card state-timeout">
      <span class="state-icon">⏰</span>
      <h2>Hết thời gian chờ</h2>
      <p class="sub">Giao dịch chưa được xác nhận sau 10 phút. Vui lòng thử lại.</p>
      <button class="btn-retry" @click="init">Tạo mã mới</button>
    </div>

    <!-- ── Main QR Screen ─────────────────────────────── -->
    <div v-else-if="info" class="qr-layout">

      <!-- LEFT: Order Info + QR -->
      <div class="left-panel">

        <!-- Status Badge -->
        <div class="status-badge" :class="statusClass">
          <span class="badge-dot"></span>
          {{ statusLabel }}
        </div>

        <!-- Order Header -->
        <div class="order-header">
          <p class="order-label">Mã đơn hàng</p>
          <h1 class="order-number">#{{ info.orderNumber }}</h1>
        </div>

        <!-- Payment Summary Cards -->
        <div class="summary-cards">
          <div class="summary-card">
            <p class="summary-label">Mã chuyển khoản</p>
            <p class="summary-value transfer-code">{{ info.transferContent }}</p>
            <button class="copy-btn-small" @click="copy(info.transferContent, 'content')" :class="{ copied: copied.content }">
              {{ copied.content ? '✓ Đã sao chép' : 'Sao chép' }}
            </button>
          </div>
          <div class="summary-card highlight">
            <p class="summary-label">Số tiền cần thanh toán</p>
            <p class="summary-value">{{ formatCurrency(info.totalPrice) }}</p>
          </div>
          <div class="summary-card">
            <p class="summary-label">Trạng thái</p>
            <p class="summary-value status-text" :class="info.orderStatus === 'DEPOSITED' ? 'success' : 'pending'">
              {{ info.orderStatus === 'DEPOSITED' ? '✓ Đã thanh toán' : '◯ Chờ xác nhận' }}
            </p>
          </div>
        </div>

        <!-- Price Cards -->
        <div class="price-grid">
          <div class="price-card">
            <p class="price-label">Tổng giá trị đơn</p>
            <p class="price-value">{{ formatCurrency(info.totalPrice) }}</p>
          </div>
          <div class="price-card accent">
            <p class="price-label">Cần thanh toán (100%)</p>
            <p class="price-value highlight">{{ formatCurrency(info.totalPrice) }}</p>
          </div>
        </div>

        <!-- QR Code -->
        <div class="qr-box">
          <img v-if="info.qrUrl" :src="info.qrUrl" alt="QR SePay" class="qr-img" />
          <div v-else class="qr-placeholder">
            <span>Không có QR</span>
          </div>
          <p class="qr-caption">Dùng app ngân hàng quét mã để thanh toán</p>
        </div>

        <!-- Countdown -->
        <div class="countdown-bar">
          <div class="bar-fill" :style="{ width: barPercent + '%' }" :class="{ urgent: barPercent < 20 }"></div>
        </div>
        <div class="countdown-row">
          <span class="dot-pulse" :class="{ active: polling }"></span>
          <span class="countdown-text">Còn lại {{ mm }}:{{ ss }}</span>
        </div>
      </div>

      <!-- RIGHT: Bank Info + Transfer -->
      <div class="right-panel">

        <!-- Section: Thông tin chuyển khoản -->
        <div class="info-section">
          <h3 class="section-title">Thông tin chuyển khoản</h3>

          <div class="info-row">
            <span class="info-label">Ngân hàng</span>
            <span class="info-value bank-tag">{{ info.bankName }}</span>
          </div>

          <div class="info-row">
            <span class="info-label">Chủ tài khoản</span>
            <span class="info-value">{{ info.accountName }}</span>
          </div>

          <div class="info-row">
            <span class="info-label">Số tài khoản</span>
            <div class="copy-row">
              <span class="info-value mono">{{ info.bankAccount }}</span>
              <button class="copy-btn" @click="copy(info.bankAccount, 'acc')" :class="{ copied: copied.acc }">
                {{ copied.acc ? '✓ Đã sao chép' : 'Sao chép' }}
              </button>
            </div>
          </div>

          <div class="info-row">
            <span class="info-label">Số tiền</span>
            <span class="info-value amount-big">{{ formatCurrency(info.totalPrice) }}</span>
          </div>

          <div class="info-row transfer-content-row">
            <span class="info-label">Nội dung CK</span>
            <div class="copy-row">
              <span class="info-value mono transfer-code">{{ info.transferContent }}</span>
              <button class="copy-btn" @click="copy(info.transferContent, 'content')" :class="{ copied: copied.content }">
                {{ copied.content ? '✓ Đã sao chép' : 'Sao chép' }}
              </button>
            </div>
          </div>
        </div>

        <!-- Section: Lưu ý quan trọng -->
        <div class="note-section">
          <h4 class="note-title">⚠️ Lưu ý quan trọng</h4>
          <ul class="note-list">
            <li>Nhập <strong>đúng nội dung chuyển khoản</strong> để hệ thống tự xác nhận</li>
            <li>Chuyển <strong>đúng số tiền</strong> {{ formatCurrency(info.totalPrice) }}</li>
            <li>Đơn hàng sẽ tự cập nhật sau khi ngân hàng xác nhận (30s - 2 phút)</li>
          </ul>
        </div>

        <!-- Section: Trạng thái -->
        <div class="status-section">
          <h3 class="section-title">Trạng thái đơn hàng</h3>
          <div class="timeline">
            <div class="tl-item done">
              <div class="tl-dot">✓</div>
              <div class="tl-content">
                <p class="tl-title">Đơn hàng được tạo</p>
                <p class="tl-sub">Đã xác nhận</p>
              </div>
            </div>
            <div class="tl-item" :class="{ done: info.orderStatus === 'DEPOSITED', active: info.orderStatus === 'AWAITING_PAYMENT' }">
              <div class="tl-dot">{{ info.orderStatus === 'DEPOSITED' ? '✓' : '2' }}</div>
              <div class="tl-content">
                <p class="tl-title">Thanh toán 100%</p>
                <p class="tl-sub">{{ info.orderStatus === 'DEPOSITED' ? 'Đã thanh toán thành công' : 'Đang chờ thanh toán...' }}</p>
              </div>
            </div>
            <div class="tl-item">
              <div class="tl-dot">3</div>
              <div class="tl-content">
                <p class="tl-title">Xử lý đơn hàng</p>
                <p class="tl-sub">Chưa bắt đầu</p>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import apiClient, { paymentAPI } from '../../services/api'

const route = useRoute()
const router = useRouter()

// ── State ────────────────────────────────────────────
const info = ref(null)
const orderId = ref(null)
const loading = ref(true)
const error = ref(null)
const paid = ref(false)
const timedOut = ref(false)
const polling = ref(false)
const redirectCountdown = ref(5)
const copied = ref({ acc: false, content: false })

const TIMEOUT_SECONDS = 600
const elapsed = ref(0)

let pollInterval = null
let countdownInterval = null
let timeoutTimer = null
let redirectTimer = null

// ── Computed ─────────────────────────────────────────

const remaining_amount = computed(() => {
  if (!info.value) return 0
  const total = Number(info.value.totalPrice) || 0
  const deposit = Number(info.value.depositAmount) || 0
  return total - deposit
})

const depositPercent = computed(() => {
  if (!info.value || !info.value.totalPrice || !info.value.depositAmount) return 0
  return Math.round((Number(info.value.depositAmount) / Number(info.value.totalPrice)) * 100)
})

const barPercent = computed(() =>
  Math.max(0, 100 - (elapsed.value / TIMEOUT_SECONDS) * 100)
)

const remainingSecs = computed(() => Math.max(0, TIMEOUT_SECONDS - elapsed.value))
const mm = computed(() => String(Math.floor(remainingSecs.value / 60)).padStart(2, '0'))
const ss = computed(() => String(remainingSecs.value % 60).padStart(2, '0'))

const statusLabel = computed(() => {
  if (!info.value) return ''
  return info.value.orderStatus === 'DEPOSITED'
    ? '✅ Đã thanh toán thành công'
    : '🕐 Đang chờ thanh toán'
})

const statusClass = computed(() => {
  if (!info.value) return ''
  return info.value.orderStatus === 'DEPOSITED' ? 'badge-paid' : 'badge-pending'
})

// ── Lifecycle ─────────────────────────────────────────

onMounted(() => {
  orderId.value = Number(route.params.orderId)
  init()
})

onUnmounted(() => {
  clearAll()
})

// ── Methods ───────────────────────────────────────────

async function init() {
  clearAll()
  loading.value = true
  error.value = null
  paid.value = false
  timedOut.value = false
  elapsed.value = 0

  try {
    const data = await paymentAPI.getPaymentInfo(orderId.value)
    info.value = data

    // Route /payment/:orderId is kept for compatibility, but READY_MADE must pay in My Orders modal.
    let orderType = data?.orderType
    if (!orderType) {
      try {
        const orderResponse = await apiClient.get(`/orders/${orderId.value}`)
        orderType = orderResponse?.data?.orderType
      } catch (lookupError) {
        console.warn('Unable to resolve order type for payment route:', lookupError)
      }
    }

    if (orderType === 'READY_MADE') {
      loading.value = false
      await router.replace({
        path: '/my-orders',
        query: {
          newOrderId: String(orderId.value),
          orderType: 'READY_MADE',
        },
      })
      return
    }

    // Nếu đã DEPOSITED từ trước, hiện success ngay
    if (data.orderStatus === 'DEPOSITED') {
      loading.value = false
      paid.value = true
      return
    }

    loading.value = false
    startPolling()
  } catch (e) {
    loading.value = false
    error.value = e.response?.data?.message || e.response?.data?.error || 'Không thể tải thông tin đơn hàng. Vui lòng thử lại.'
  }
}

function startPolling() {
  polling.value = true

  countdownInterval = setInterval(() => { elapsed.value++ }, 1000)

  pollInterval = setInterval(async () => {
    try {
      const res = await paymentAPI.getPaymentInfo(orderId.value)
      info.value = res // update live data
      if (res.orderStatus === 'DEPOSITED') {
        clearAll()
        paid.value = true
        startRedirectCountdown()
      }
    } catch (_) { /* silent */ }
  }, 3000)

  timeoutTimer = setTimeout(() => {
    clearAll()
    timedOut.value = true
  }, TIMEOUT_SECONDS * 1000)
}

function startRedirectCountdown() {
  redirectCountdown.value = 5
  redirectTimer = setInterval(() => {
    redirectCountdown.value--
    if (redirectCountdown.value <= 0) {
      clearInterval(redirectTimer)
      goBack()
    }
  }, 1000)
}

function clearAll() {
  clearInterval(pollInterval)
  clearInterval(countdownInterval)
  clearInterval(redirectTimer)
  clearTimeout(timeoutTimer)
  polling.value = false
  pollInterval = null
  countdownInterval = null
  redirectTimer = null
  timeoutTimer = null
}

function goBack() {
  router.push('/my-orders')
}

async function copy(text, key) {
  try {
    await navigator.clipboard.writeText(text)
    copied.value[key] = true
    setTimeout(() => { copied.value[key] = false }, 2500)
  } catch (_) {
    // Fallback
    const el = document.createElement('textarea')
    el.value = text
    document.body.appendChild(el)
    el.select()
    document.execCommand('copy')
    document.body.removeChild(el)
    copied.value[key] = true
    setTimeout(() => { copied.value[key] = false }, 2500)
  }
}

function formatCurrency(value) {
  if (value == null) return '—'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');

* { box-sizing: border-box; }

.payment-page {
  font-family: 'Inter', sans-serif;
  min-height: 100vh;
  background: linear-gradient(135deg, #080e1a 0%, #0f1e35 45%, #0a1628 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
}

/* ── State cards ──────────────────────────────── */

.state-card {
  background: rgba(255,255,255,0.04);
  backdrop-filter: blur(24px);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 24px;
  padding: 52px 44px;
  text-align: center;
  color: #fff;
  min-width: 320px;
  max-width: 460px;
}

.state-icon { font-size: 56px; display: block; margin-bottom: 20px; }

.state-card h2 { font-size: 22px; font-weight: 700; margin: 0 0 10px; }
.state-card .sub { color: rgba(255,255,255,0.55); font-size: 14px; margin: 0; }
.mt-8 { margin-top: 8px; }

.state-success { border-color: rgba(52,211,153,0.35); background: rgba(16,185,129,0.07); }
.state-error   { border-color: rgba(248,113,113,0.35); background: rgba(239,68,68,0.07); }
.state-timeout { border-color: rgba(251,191,36,0.35);  background: rgba(245,158,11,0.07); }

.success-circle {
  width: 80px; height: 80px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 40px; color: #fff;
  margin: 0 auto 20px;
  animation: pop 0.45s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 0 40px rgba(16,185,129,0.4);
}

@keyframes pop {
  from { transform: scale(0); opacity: 0; }
  to   { transform: scale(1); opacity: 1; }
}

.success-detail {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 14px;
  padding: 16px 20px;
  margin: 20px 0 8px;
  text-align: left;
}

.srow {
  display: flex; justify-content: space-between;
  font-size: 14px; color: rgba(255,255,255,0.75);
  padding: 5px 0;
}
.srow:not(:last-child) { border-bottom: 1px solid rgba(255,255,255,0.07); }
.srow .green { color: #34d399; font-weight: 700; }

/* ── Layout ───────────────────────────────────── */

.qr-layout {
  display: flex;
  gap: 24px;
  width: 100%;
  max-width: 980px;
  align-items: flex-start;
}

/* ── Left panel ───────────────────────────────── */

.left-panel {
  flex: 0 0 360px;
  background: rgba(255,255,255,0.04);
  backdrop-filter: blur(28px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 24px;
  padding: 28px 28px 24px;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Status Badge */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
  width: fit-content;
}
.badge-pending {
  background: rgba(245,158,11,0.15);
  border: 1px solid rgba(245,158,11,0.35);
  color: #fbbf24;
}
.badge-paid {
  background: rgba(16,185,129,0.15);
  border: 1px solid rgba(16,185,129,0.35);
  color: #34d399;
}
.badge-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: currentColor;
  animation: badge-pulse 2s ease-in-out infinite;
}
@keyframes badge-pulse {
  0%, 100% { opacity: 1; } 50% { opacity: 0.4; }
}

/* Order header */
.order-header { }
.order-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: rgba(255,255,255,0.35);
  margin: 0 0 4px;
  font-weight: 600;
}
.order-number {
  font-size: 26px;
  font-weight: 800;
  margin: 0;
  background: linear-gradient(135deg, #93c5fd, #6366f1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

/* Summary cards */
.summary-cards {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  margin: 20px 0;
}

.summary-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.summary-card.highlight {
  background: rgba(52,211,153,0.08);
  border-color: rgba(52,211,153,0.2);
}

.summary-label {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  color: rgba(255,255,255,0.35);
  margin: 0 0 6px;
  font-weight: 600;
}

.summary-value {
  font-size: 15px;
  font-weight: 700;
  color: rgba(255,255,255,0.9);
  margin: 0 0 8px;
  word-break: break-all;
}

.summary-value.transfer-code {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  color: #fbbf24;
  background: rgba(245,158,11,0.1);
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 12px;
}

.summary-value.status-text {
  font-size: 14px;
}

.summary-value.status-text.success {
  color: #34d399;
}

.summary-value.status-text.pending {
  color: #fbbf24;
}

.copy-btn-small {
  padding: 3px 10px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.6);
  border-radius: 6px;
  cursor: pointer;
  font-size: 11px;
  font-weight: 500;
  transition: all 0.2s;
  font-family: inherit;
}

.copy-btn-small:hover {
  background: rgba(255,255,255,0.15);
  color: #fff;
}

.copy-btn-small.copied {
  background: rgba(16,185,129,0.15);
  border-color: rgba(16,185,129,0.4);
  color: #34d399;
}

/* Price grid */
.price-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.price-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 14px;
  padding: 14px;
}
.price-card.accent {
  background: rgba(99,102,241,0.1);
  border-color: rgba(99,102,241,0.3);
}
.price-label {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  color: rgba(255,255,255,0.35);
  margin: 0 0 6px;
  font-weight: 600;
}
.price-value {
  font-size: 15px;
  font-weight: 700;
  color: rgba(255,255,255,0.9);
  margin: 0;
}
.price-value.highlight {
  font-size: 17px;
  color: #a5b4fc;
}
.price-note {
  font-size: 11px;
  color: rgba(165,180,252,0.6);
  margin: 4px 0 0;
}

/* QR */
.qr-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.qr-img {
  width: 200px; height: 200px;
  border-radius: 16px;
  background: #fff;
  padding: 8px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.5);
  display: block;
}
.qr-placeholder {
  width: 200px; height: 200px;
  border-radius: 16px;
  background: rgba(255,255,255,0.05);
  border: 2px dashed rgba(255,255,255,0.15);
  display: flex; align-items: center; justify-content: center;
  color: rgba(255,255,255,0.3);
  font-size: 13px;
}
.qr-caption {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
  text-align: center;
  margin: 0;
}

/* Countdown */
.countdown-bar {
  height: 4px;
  background: rgba(255,255,255,0.08);
  border-radius: 2px;
  overflow: hidden;
}
.bar-fill {
  height: 100%;
  background: linear-gradient(to right, #6366f1, #93c5fd);
  border-radius: 2px;
  transition: width 1s linear, background 0.5s;
}
.bar-fill.urgent { background: linear-gradient(to right, #ef4444, #f97316); }

.countdown-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.dot-pulse {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  flex-shrink: 0;
}
.dot-pulse.active {
  background: #34d399;
  animation: dp 2s ease-in-out infinite;
}
@keyframes dp { 0%,100%{opacity:1;transform:scale(1)} 50%{opacity:0.4;transform:scale(0.8)} }
.countdown-text {
  font-size: 13px;
  color: rgba(255,255,255,0.45);
}

/* ── Right panel ──────────────────────────────── */

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-section, .note-section, .status-section {
  background: rgba(255,255,255,0.04);
  backdrop-filter: blur(24px);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 20px;
  padding: 22px 24px;
  color: #fff;
}

.section-title {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: rgba(255,255,255,0.4);
  margin: 0 0 18px;
}

.info-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.info-row:last-child { border-bottom: none; padding-bottom: 0; }
.info-row:first-of-type { padding-top: 0; }

.info-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  color: rgba(255,255,255,0.3);
  font-weight: 600;
}
.info-value {
  font-size: 15px;
  font-weight: 500;
  color: rgba(255,255,255,0.9);
}
.info-value.mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 14px;
  background: rgba(255,255,255,0.07);
  padding: 5px 10px;
  border-radius: 8px;
  display: inline-block;
}
.info-value.bank-tag {
  background: rgba(99,102,241,0.15);
  border: 1px solid rgba(99,102,241,0.3);
  color: #a5b4fc;
  padding: 3px 12px;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
  display: inline-block;
}
.info-value.amount-big {
  font-size: 22px;
  font-weight: 800;
  color: #34d399;
}
.info-value.transfer-code {
  color: #fbbf24;
  background: rgba(245,158,11,0.1);
  border: 1px solid rgba(245,158,11,0.2);
}

.copy-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.copy-btn {
  padding: 4px 12px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.7);
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s;
  white-space: nowrap;
  font-family: inherit;
}
.copy-btn:hover { background: rgba(255,255,255,0.15); color: #fff; }
.copy-btn.copied {
  background: rgba(16,185,129,0.15);
  border-color: rgba(16,185,129,0.4);
  color: #34d399;
}

/* Note section */
.note-section {
  background: rgba(245,158,11,0.05);
  border-color: rgba(245,158,11,0.2);
}
.note-title {
  font-size: 13px;
  font-weight: 700;
  color: #fbbf24;
  margin: 0 0 12px;
}
.note-list {
  margin: 0;
  padding-left: 20px;
  color: rgba(255,255,255,0.6);
  font-size: 13px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.note-list strong { color: rgba(255,255,255,0.85); }

/* Timeline */
.timeline {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.tl-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  position: relative;
  padding-bottom: 20px;
}
.tl-item:last-child { padding-bottom: 0; }

.tl-item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 32px;
  bottom: 0;
  width: 2px;
  background: rgba(255,255,255,0.08);
}
.tl-item.done:not(:last-child)::before { background: rgba(16,185,129,0.4); }

.tl-dot {
  width: 32px; height: 32px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700;
  flex-shrink: 0;
  background: rgba(255,255,255,0.07);
  border: 2px solid rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.4);
  position: relative; z-index: 1;
}
.tl-item.done .tl-dot {
  background: rgba(16,185,129,0.2);
  border-color: rgba(16,185,129,0.5);
  color: #34d399;
}
.tl-item.active .tl-dot {
  background: rgba(245,158,11,0.2);
  border-color: rgba(245,158,11,0.5);
  color: #fbbf24;
  animation: tl-pulse 2s ease-in-out infinite;
}
@keyframes tl-pulse {
  0%,100%{box-shadow: 0 0 0 0 rgba(245,158,11,0.3)}
  50%{box-shadow: 0 0 0 6px rgba(245,158,11,0)}
}

.tl-content { padding-top: 4px; }
.tl-title { font-size: 14px; font-weight: 600; color: rgba(255,255,255,0.85); margin: 0 0 2px; }
.tl-sub { font-size: 12px; color: rgba(255,255,255,0.4); margin: 0; }
.tl-item.done .tl-sub { color: #34d399; }
.tl-item.active .tl-sub { color: #fbbf24; }

/* Buttons */
.btn-retry {
  margin-top: 20px;
  padding: 11px 30px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.18);
  color: #fff;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
  font-family: inherit;
}
.btn-retry:hover { background: rgba(255,255,255,0.16); }

/* Spinner */
.spinner {
  width: 44px; height: 44px;
  border: 3px solid rgba(255,255,255,0.1);
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 18px;
}
@keyframes spin { to { transform: rotate(360deg); } }

.state-card p { color: rgba(255,255,255,0.55); font-size: 14px; }

/* ── Responsive ──────────────────────────────── */
@media (max-width: 768px) {
  .qr-layout {
    flex-direction: column;
    align-items: stretch;
  }
  .left-panel { flex: none; }
  .price-grid { grid-template-columns: 1fr; }
}
</style>
