<template>
  <div class="payment-qr-container">
    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải thông tin thanh toán...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <span class="error-icon">⚠️</span>
      <p>{{ error }}</p>
      <button @click="fetchPaymentInfo" class="btn-retry">Thử lại</button>
    </div>

    <!-- Waiting for admin verification after webhook capture -->
    <div v-else-if="isAwaitingAdminVerification" class="paid-state pending-verify-state">
      <div class="success-icon">⏳</div>
      <h3>Đã ghi nhận chuyển khoản</h3>
      <p>
        Hệ thống đã nhận giao dịch cho đơn
        <strong>{{ paymentInfo.orderNumber }}</strong>
        và đang chờ admin xác nhận.
      </p>
      <div class="paid-amount">
        Số tiền đã ghi nhận: <strong>{{ formatCurrency(payableAmount) }}</strong>
      </div>
      <p class="pending-note">Bạn không cần chuyển lại tiền. Trạng thái sẽ tự động cập nhật sau khi được duyệt.</p>
    </div>

    <!-- Success: Paid deposit (1st payment) -->
    <div v-else-if="paymentInfo && paymentInfo.orderStatus === 'DEPOSITED'" class="paid-state">
      <div class="success-icon">✅</div>
      <h3>Đã nhận tiền cọc!</h3>
      <p>Đơn hàng <strong>{{ paymentInfo.orderNumber }}</strong> đã được xác nhận đặt cọc thành công.</p>
      <div class="paid-amount">
        Số tiền nhận: <strong>{{ formatCurrency(paymentInfo.depositAmount) }}</strong>
      </div>
    </div>

    <!-- Success: Full payment confirmed (2nd payment) -->
    <div v-else-if="paymentInfo && paymentInfo.orderStatus === 'AWAITING_DELIVERY'" class="paid-state">
      <div class="success-icon">✅</div>
      <h3>Đã nhận đủ tiền!</h3>
      <p>Đơn hàng <strong>{{ paymentInfo.orderNumber }}</strong> đã thanh toán đủ 100%.</p>
      <div class="paid-amount">
        Tổng đã thanh toán: <strong>{{ formatCurrency(paymentInfo.totalPrice) }}</strong>
      </div>
    </div>

    <!-- Payment QR Display -->
    <div v-else-if="paymentInfo" class="qr-payment">
      <!-- Header -->
      <div class="qr-header">
        <h3>
          <template v-if="isRemainingPhase">💳 Thanh toán mốc 2 (40%)</template>
          <template v-else>💳 Thanh toán mốc 1 (60%)</template>
        </h3>
        <p class="order-ref">Đơn hàng: <strong>{{ paymentInfo.orderNumber }}</strong></p>
        <p v-if="hasMilestoneSummary" class="order-ref milestone-ref">
          Mốc hiện tại: <strong>{{ currentMilestoneName }}</strong>
        </p>
      </div>

      <!-- Amount Summary -->
      <div class="amount-summary">
        <div class="amount-row">
          <span>Tổng giá trị đơn hàng</span>
          <span>{{ formatCurrency(paymentInfo.totalPrice) }}</span>
        </div>
        <div class="amount-row highlight">
          <span>{{ isRemainingPhase ? 'Cần thanh toán (40% còn lại)' : 'Cần thanh toán (60% tiền cọc)' }}</span>
          <span class="amount-primary">{{ formatCurrency(payableAmount) }}</span>
        </div>
        <div v-if="hasMilestoneSummary" class="amount-row">
          <span>Đã thanh toán xác nhận</span>
          <span>{{ formatCurrency(milestoneSummary.totalPaid) }}</span>
        </div>
        <div v-if="hasMilestoneSummary" class="amount-row">
          <span>Còn lại toàn đơn</span>
          <span>{{ formatCurrency(milestoneSummary.totalRemaining) }}</span>
        </div>
      </div>

      <!-- QR Code -->
      <div class="qr-section">
        <div class="qr-label">Quét mã QR để thanh toán</div>
        <div class="qr-wrapper">
          <img
            :src="paymentInfo.qrUrl"
            alt="Mã QR thanh toán SePay"
            class="qr-image"
            @error="onQrError"
          />
          <div v-if="qrError" class="qr-error">
            Không tải được mã QR.<br />Vui lòng dùng thông tin bên dưới.
          </div>
        </div>
        <p class="qr-hint">Hỗ trợ tất cả ứng dụng ngân hàng Việt Nam</p>
      </div>

      <!-- Bank Transfer Info -->
      <div class="bank-info">
        <h4>📋 Thông tin chuyển khoản</h4>
        <div class="info-grid">
          <div class="info-row">
            <span class="info-label">Ngân hàng</span>
            <span class="info-value bank-name">{{ paymentInfo.bankName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Số tài khoản</span>
            <div class="info-value-copy">
              <span>{{ paymentInfo.bankAccount }}</span>
              <button @click="copyText(paymentInfo.bankAccount, 'account')" class="btn-copy" title="Sao chép">
                {{ copied.account ? '✓' : '📋' }}
              </button>
            </div>
          </div>
          <div class="info-row">
            <span class="info-label">Chủ tài khoản</span>
            <span class="info-value">{{ paymentInfo.accountName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Số tiền</span>
            <div class="info-value-copy">
              <span class="amount-text">{{ formatCurrency(payableAmount) }}</span>
              <button @click="copyText(String(payableAmount), 'amount')" class="btn-copy" title="Sao chép">
                {{ copied.amount ? '✓' : '📋' }}
              </button>
            </div>
          </div>
          <div class="info-row">
            <span class="info-label">Nội dung CK</span>
            <div class="info-value-copy">
              <span class="content-highlight">{{ paymentInfo.transferContent }}</span>
              <button @click="copyText(paymentInfo.transferContent, 'content')" class="btn-copy" title="Sao chép">
                {{ copied.content ? '✓' : '📋' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Warning -->
      <div class="warning-box">
        ⚠️ <strong>Quan trọng:</strong> Vui lòng nhập <em>đúng nội dung chuyển khoản</em>
        để hệ thống tự động xác nhận thanh toán của bạn.
      </div>

      <!-- Status Polling Indicator -->
      <div class="polling-indicator">
        <div class="pulse-dot"></div>
        <span>
          {{ hasMilestoneSummary
            ? 'Đang chờ giao dịch và cập nhật theo mốc thanh toán...'
            : 'Đang chờ xác nhận thanh toán... (tự động cập nhật)' }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { paymentAPI } from '../../services/api'

const props = defineProps({
  orderId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['payment-confirmed'])

const POLLING_INTERVAL = 10000 // 10 giây

const paymentInfo = ref(null)
const milestoneSummary = ref(null)
const loading = ref(true)
const error = ref(null)
const qrError = ref(false)
const copied = ref({ account: false, amount: false, content: false })

const milestoneItems = computed(() => {
  return Array.isArray(milestoneSummary.value?.milestones)
    ? [...milestoneSummary.value.milestones].sort((a, b) => Number(a.milestoneOrder || 0) - Number(b.milestoneOrder || 0))
    : []
})

const hasMilestoneSummary = computed(() => milestoneItems.value.length > 0)

const activeMilestone = computed(() => {
  return milestoneItems.value.find((milestone) => {
    const status = String(milestone?.status || '')
    return status === 'ACTIVE' || status === 'OVERDUE' || status === 'PAID_UNVERIFIED'
  }) || null
})

const isRemainingPhase = computed(() => {
  if (activeMilestone.value?.milestoneOrder === 2) return true
  return paymentInfo.value?.orderStatus === 'AWAITING_REMAINING_PAYMENT'
})

const currentMilestoneName = computed(() => {
  if (activeMilestone.value?.milestoneName) {
    return activeMilestone.value.milestoneName
  }
  return isRemainingPhase.value ? 'Thanh toán cuối (Mốc 2)' : 'Đặt cọc (Mốc 1)'
})

const payableAmount = computed(() => {
  if (activeMilestone.value?.amount != null) {
    return Number(activeMilestone.value.amount || 0)
  }

  if (!paymentInfo.value) return 0
  const total = Number(paymentInfo.value.totalPrice || 0)
  const payable = Number(paymentInfo.value.depositAmount || 0)
  if (isRemainingPhase.value) {
    return Math.max(0, total - payable)
  }
  return payable
})

const isAwaitingAdminVerification = computed(() => {
  return String(activeMilestone.value?.status || '') === 'PAID_UNVERIFIED'
})

let pollingTimer = null

// Fetch payment info từ BE
async function fetchPaymentInfo() {
  loading.value = true
  error.value = null
  qrError.value = false

  try {
    const [info, milestoneData] = await Promise.all([
      paymentAPI.getPaymentInfo(props.orderId),
      paymentAPI.getMilestones(props.orderId).catch(() => null),
    ])

    paymentInfo.value = info
    milestoneSummary.value = milestoneData && Array.isArray(milestoneData?.milestones)
      ? milestoneData
      : null

    // Stop polling when payment was fully moved to the next order phase.
    if (paymentInfo.value.orderStatus === 'DEPOSITED' ||
        paymentInfo.value.orderStatus === 'AWAITING_DELIVERY') {
      stopPolling()
      emit('payment-confirmed', paymentInfo.value)
    }
  } catch (e) {
    error.value = 'Không thể tải thông tin thanh toán: ' + e.message
  } finally {
    loading.value = false
  }
}

// Format tiền VNĐ
function formatCurrency(amount) {
  if (!amount) return '—'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

// Copy to clipboard
async function copyText(text, field) {
  try {
    await navigator.clipboard.writeText(text)
    const key = field || 'content'
    copied.value[key] = true
    setTimeout(() => { copied.value[key] = false }, 2000)
  } catch {}
}

function onQrError() {
  qrError.value = true
}

function startPolling() {
  if (pollingTimer) return
  pollingTimer = setInterval(fetchPaymentInfo, POLLING_INTERVAL)
}

function stopPolling() {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

onMounted(() => {
  fetchPaymentInfo()
  startPolling()
})

watch(
  () => props.orderId,
  () => {
    stopPolling()
    fetchPaymentInfo()
    startPolling()
  }
)

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.payment-qr-container {
  max-width: 480px;
  margin: 0 auto;
  padding: 24px;
  font-family: 'Inter', 'Segoe UI', sans-serif;
}

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px;
  color: #6b7280;
}
.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Error */
.error-state {
  text-align: center;
  padding: 32px;
  color: #ef4444;
}
.btn-retry {
  margin-top: 12px;
  padding: 8px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* Paid success */
.paid-state {
  text-align: center;
  padding: 40px 24px;
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  border-radius: 16px;
  border: 1px solid #6ee7b7;
}
.pending-verify-state {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
  border-color: #fdba74;
}
.success-icon { font-size: 56px; margin-bottom: 12px; }
.paid-state h3 { color: #065f46; font-size: 1.3rem; margin: 0 0 8px; }
.paid-amount {
  margin-top: 16px;
  font-size: 1.1rem;
  color: #047857;
  background: white;
  padding: 12px 20px;
  border-radius: 10px;
}
.pending-note {
  margin: 12px 0 0;
  color: #9a3412;
  font-size: 0.88rem;
}

/* QR Payment */
.qr-payment {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  overflow: hidden;
}

.qr-header {
  background: linear-gradient(135deg, #1e40af, #3b82f6);
  color: white;
  padding: 20px 24px;
}
.qr-header h3 { margin: 0 0 4px; font-size: 1.1rem; }
.order-ref { margin: 0; opacity: 0.85; font-size: 0.9rem; }
.milestone-ref {
  margin-top: 4px;
  opacity: 0.9;
}

/* Amount summary */
.amount-summary {
  padding: 16px 24px;
  background: #f8faff;
  border-bottom: 1px solid #e5e7eb;
}
.amount-row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 0.9rem;
  color: #6b7280;
}
.amount-row.highlight { color: #111827; font-weight: 600; margin-top: 4px; }
.amount-primary { color: #dc2626; font-size: 1.05rem; }

/* QR Section */
.qr-section {
  padding: 20px 24px;
  text-align: center;
  border-bottom: 1px solid #f3f4f6;
}
.qr-label {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 12px;
  font-weight: 500;
}
.qr-wrapper {
  display: inline-block;
  padding: 8px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
}
.qr-image {
  width: 200px;
  height: 200px;
  display: block;
  border-radius: 8px;
}
.qr-error { font-size: 0.8rem; color: #9ca3af; margin-top: 8px; }
.qr-hint { font-size: 0.8rem; color: #9ca3af; margin-top: 8px; }

/* Bank Info */
.bank-info {
  padding: 16px 24px;
}
.bank-info h4 {
  margin: 0 0 12px;
  font-size: 0.95rem;
  color: #374151;
}
.info-grid { display: flex; flex-direction: column; gap: 10px; }
.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.info-label {
  font-size: 0.82rem;
  color: #9ca3af;
  width: 120px;
  flex-shrink: 0;
}
.info-value {
  font-size: 0.9rem;
  color: #111827;
  font-weight: 500;
  text-align: right;
}
.bank-name { color: #1e40af; }
.info-value-copy {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #111827;
  font-weight: 500;
}
.content-highlight {
  background: #fef3c7;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.88rem;
  color: #92400e;
}
.amount-text { color: #dc2626; font-weight: 600; }
.btn-copy {
  background: none;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 2px 8px;
  cursor: pointer;
  font-size: 0.8rem;
  color: #6b7280;
  transition: all 0.2s;
  flex-shrink: 0;
}
.btn-copy:hover { background: #f3f4f6; }

/* Warning */
.warning-box {
  margin: 0 24px 16px;
  padding: 12px 14px;
  background: #fefce8;
  border: 1px solid #fde68a;
  border-radius: 10px;
  font-size: 0.83rem;
  color: #78350f;
  line-height: 1.5;
}

/* Polling */
.polling-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #f0f9ff;
  font-size: 0.8rem;
  color: #0284c7;
}
.pulse-dot {
  width: 8px;
  height: 8px;
  background: #0284c7;
  border-radius: 50%;
  animation: pulse 1.5s ease-in-out infinite;
  flex-shrink: 0;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.4); opacity: 0.6; }
}
</style>
