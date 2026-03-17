<template>
  <div class="payment-modal-container">
    <!-- Loading State -->
    <div v-if="loading" class="state-container loading-state">
      <div class="spinner"></div>
      <p>Đang tải thông tin thanh toán...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="state-container error-state">
      <span class="error-icon">⚠️</span>
      <p>{{ error }}</p>
      <button @click="fetchPaymentInfo" class="btn btn-primary btn-sm">Thử lại</button>
    </div>

    <!-- Success: Paid -->
    <div v-else-if="paymentInfo && paymentInfo.orderStatus === 'DEPOSITED'" class="state-container paid-state">
      <div class="success-icon">✅</div>
      <h5>Đã nhận tiền cọc!</h5>
      <p class="mb-2">Đơn hàng <strong>{{ paymentInfo.orderNumber }}</strong> đã được xác nhận đặt cọc thành công.</p>
      <div class="alert alert-success mb-0">
        Số tiền nhận: <strong>{{ formatCurrency(paymentInfo.depositAmount) }}</strong>
      </div>
    </div>

    <!-- Payment QR Display -->
    <div v-else-if="paymentInfo" class="payment-content">
      <!-- Key Information Summary (3 cards) -->
      <div class="info-cards-row">
        <div class="info-card">
          <div class="card-label">Mã chuyển khoản</div>
          <div class="card-content">
            <span class="transfer-code-text">{{ paymentInfo.transferContent }}</span>
            <button @click="copyText(paymentInfo.transferContent, 'transferCode')" class="btn-copy-inline" :class="{ copied: copied.transferCode }">
              {{ copied.transferCode ? '✓ Đã sao chép' : '📋 Sao chép' }}
            </button>
          </div>
        </div>

        <div class="info-card highlight">
          <div class="card-label">Tiền cọc cần thanh toán</div>
          <div class="card-amount">{{ formatCurrency(paymentInfo.depositAmount) }}</div>
        </div>

        <div class="info-card">
          <div class="card-label">Trạng thái đơn hàng</div>
          <div class="card-status" :class="paymentInfo.orderStatus === 'DEPOSITED' ? 'success' : 'pending'">
            {{ paymentInfo.orderStatus === 'DEPOSITED' ? '✓ Đã cọc' : '◯ Chờ xác nhận' }}
          </div>
        </div>
      </div>

      <!-- QR Code Section -->
      <div class="qr-section">
        <div class="qr-title">📱 Quét mã QR để thanh toán</div>
        <div class="qr-box">
          <img 
            v-if="paymentInfo.qrUrl"
            :src="paymentInfo.qrUrl" 
            alt="Mã QR thanh toán" 
            class="qr-image"
            @error="onQrError"
          />
          <div v-if="qrError" class="qr-error">
            Không tải được mã QR. Vui lòng dùng thông tin bên dưới.
          </div>
        </div>
        <p class="qr-hint">Hỗ trợ tất cả ứng dụng ngân hàng Việt Nam</p>
      </div>

      <!-- Bank Transfer Info -->
      <div class="bank-info-section">
        <h6 class="section-title">📋 Thông tin chuyển khoản chi tiết</h6>
        <div class="info-rows">
          <div class="info-row">
            <span class="row-label">Ngân hàng</span>
            <span class="row-value">{{ paymentInfo.bankName }}</span>
          </div>
          <div class="info-row">
            <span class="row-label">Chủ tài khoản</span>
            <span class="row-value">{{ paymentInfo.accountName }}</span>
          </div>
          <div class="info-row">
            <span class="row-label">Số tài khoản</span>
            <div class="row-value-copy">
              <span class="mono">{{ paymentInfo.bankAccount }}</span>
              <button @click="copyText(paymentInfo.bankAccount, 'account')" class="btn-copy-small" :class="{ copied: copied.account }">
                {{ copied.account ? '✓' : '📋' }}
              </button>
            </div>
          </div>
          <div class="info-row">
            <span class="row-label">Số tiền</span>
            <span class="row-value amount">{{ formatCurrency(paymentInfo.depositAmount) }}</span>
          </div>
          <div class="info-row">
            <span class="row-label">Nội dung CK</span>
            <div class="row-value-copy">
              <span class="mono transfer-content">{{ paymentInfo.transferContent }}</span>
              <button @click="copyText(paymentInfo.transferContent, 'content')" class="btn-copy-small" :class="{ copied: copied.content }">
                {{ copied.content ? '✓' : '📋' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Important Note -->
      <div class="alert alert-warning mb-0">
        <strong>⚠️ Lưu ý quan trọng:</strong>
        <ul class="mb-0 mt-2">
          <li>Nhập <strong>đúng nội dung chuyển khoản</strong> để hệ thống tự xác nhận</li>
          <li>Chuyển <strong>đúng số tiền</strong> {{ formatCurrency(paymentInfo.depositAmount) }}</li>
          <li>Đơn hàng sẽ tự cập nhật sau khi ngân hàng xác nhận (30s - 2 phút)</li>
        </ul>
      </div>

      <!-- Polling Status -->
      <div v-if="!error" class="polling-status">
        <span class="pulse-dot"></span>
        <span>Đang chờ xác nhận thanh toán... (tự động cập nhật mỗi 10 giây)</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  orderId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['payment-confirmed'])

const API_BASE = 'http://localhost:8080/api'
const POLLING_INTERVAL = 10000

const paymentInfo = ref(null)
const loading = ref(true)
const error = ref(null)
const qrError = ref(false)
const copied = ref({ account: false, amount: false, content: false, transferCode: false })

let pollingTimer = null

async function fetchPaymentInfo() {
  loading.value = true
  error.value = null

  try {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    const res = await fetch(`${API_BASE}/payments/orders/${props.orderId}/qr`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (!res.ok) throw new Error(`HTTP ${res.status}`)

    paymentInfo.value = await res.json()

    if (paymentInfo.value.orderStatus === 'DEPOSITED') {
      stopPolling()
      emit('payment-confirmed', paymentInfo.value)
    }
  } catch (e) {
    error.value = 'Không thể tải thông tin thanh toán: ' + e.message
  } finally {
    loading.value = false
  }
}

function formatCurrency(amount) {
  if (!amount) return '—'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

async function copyText(text, fieldKey) {
  try {
    await navigator.clipboard.writeText(text)
    copied.value[fieldKey] = true
    setTimeout(() => { copied.value[fieldKey] = false }, 2000)
  } catch (e) {
    console.error('Failed to copy:', e)
  }
}

function onQrError() {
  qrError.value = true
}

function startPolling() {
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

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.payment-modal-container {
  font-family: 'Inter', 'Segoe UI', sans-serif;
  font-size: 0.95rem;
}

/* States */
.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.loading-state {
  gap: 12px;
  color: #6b7280;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state {
  gap: 16px;
  color: #dc2626;
}

.error-icon {
  font-size: 48px;
}

.paid-state {
  gap: 12px;
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  border-radius: 12px;
  color: #065f46;
}

.success-icon {
  font-size: 56px;
}

.paid-state h5 {
  margin: 0;
  font-weight: 700;
}

.paid-state p {
  margin: 0;
  font-size: 0.9rem;
}

/* Payment Content */
.payment-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

/* Info Cards Row */
.info-cards-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.info-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  transition: all 0.2s;
}

.info-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.info-card.highlight {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.card-label {
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #9ca3af;
  font-weight: 600;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.transfer-code-text {
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
  color: #d97706;
  background: #fef3c7;
  padding: 3px 6px;
  border-radius: 4px;
  flex: 1;
  word-break: break-all;
}

.btn-copy-inline {
  padding: 2px 6px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.7rem;
  color: #6b7280;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-copy-inline:hover {
  background: #e5e7eb;
}

.btn-copy-inline.copied {
  background: #dcfce7;
  border-color: #86efac;
  color: #16a34a;
}

.card-amount {
  font-size: 1.1rem;
  font-weight: 700;
  color: #059669;
}

.card-status {
  font-size: 0.95rem;
  font-weight: 600;
}

.card-status.success {
  color: #059669;
}

.card-status.pending {
  color: #d97706;
}

/* QR Section */
.qr-section {
  text-align: center;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
}

.qr-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 10px;
}

.qr-box {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px;
  background: white;
  border-radius: 8px;
  margin-bottom: 8px;
}

.qr-image {
  width: 200px;
  height: 200px;
  border-radius: 6px;
  display: block;
}

.qr-error {
  color: #9ca3af;
  font-size: 0.8rem;
  padding: 10px;
}

.qr-hint {
  font-size: 0.8rem;
  color: #9ca3af;
  margin: 0;
}

/* Bank Info Section */
.bank-info-section {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 12px;
}

.section-title {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #6b7280;
  margin: 0 0 10px;
  border-bottom: 1px solid #f3f4f6;
  padding-bottom: 8px;
}

.info-rows {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #f3f4f6;
  font-size: 0.85rem;
}

.info-row:last-child {
  border-bottom: none;
}

.row-label {
  color: #9ca3af;
  font-weight: 500;
  min-width: 100px;
}

.row-value {
  color: #111827;
  font-weight: 500;
  text-align: right;
}

.row-value.amount {
  color: #dc2626;
  font-weight: 600;
}

.row-value-copy {
  display: flex;
  align-items: center;
  gap: 6px;
}

.mono {
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
  background: #f3f4f6;
  padding: 3px 6px;
  border-radius: 4px;
  color: #111827;
}

.mono.transfer-content {
  background: #fef3c7;
  color: #92400e;
}

.btn-copy-small {
  padding: 1px 4px;
  background: none;
  border: 1px solid #d1d5db;
  border-radius: 3px;
  cursor: pointer;
  font-size: 0.7rem;
  color: #6b7280;
  transition: all 0.2s;
}

.btn-copy-small:hover {
  background: #f3f4f6;
}

.btn-copy-small.copied {
  background: #dcfce7;
  border-color: #86efac;
  color: #16a34a;
}

/* Alert and polling */
.alert {
  font-size: 0.85rem;
  padding: 10px 12px;
}

.alert ul {
  padding-left: 20px;
}

.alert li {
  margin-bottom: 4px;
  font-size: 0.8rem;
}

.polling-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px;
  background: #eff6ff;
  border-radius: 8px;
  color: #0284c7;
  font-size: 0.8rem;
}

.pulse-dot {
  width: 6px;
  height: 6px;
  background: #0284c7;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

/* Responsive */
@media (max-width: 600px) {
  .info-cards-row {
    grid-template-columns: 1fr;
  }

  .payment-content {
    gap: 12px;
    padding: 12px;
  }

  .qr-image {
    width: 160px;
    height: 160px;
  }
}
</style>
