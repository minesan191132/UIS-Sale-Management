<template>
  <section class="content-section orders-section">

    <!-- Header -->
    <div class="section-header">
      <h1 class="section-title">Lịch Sử Đơn Mua</h1>
      <p class="section-desc">Theo dõi trạng thái và lịch sử tất cả đơn hàng của bạn</p>
    </div>
    <div class="section-divider"></div>

    <!-- Main Type Tabs -->
    <div class="type-tabs-wrap">
      <button
        v-for="typeTab in typeTabs"
        :key="typeTab.key"
        class="type-tab"
        :class="{ active: activeOrderType === typeTab.key }"
        @click="changeOrderType(typeTab.key)"
      >
        <span class="type-tab-icon">{{ typeTab.icon }}</span>
        {{ typeTab.label }}
      </button>
    </div>

    <!-- Status Filter Sub-Tabs -->
    <div class="order-tabs-wrap">
      <div class="order-tabs">
        <button
          v-for="tab in currentStatusTabs"
          :key="tab.key"
          class="order-tab"
          :class="{ active: activeStatus === tab.key }"
          @click="changeStatus(tab.key)"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- Skeleton Loading -->
    <div v-if="isLoading" class="orders-list">
      <div v-for="i in 3" :key="i" class="order-card order-skeleton">
        <div class="sk-card-left">
          <div class="sk-line sk-order-no"></div>
          <div class="sk-line sk-date"></div>
          <div class="sk-line sk-count"></div>
          <div class="sk-badge"></div>
        </div>
        <div class="sk-card-right">
          <div class="sk-line sk-price"></div>
          <div class="sk-line sk-deposit"></div>
          <div class="sk-btn-group">
            <div class="sk-btn"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="orders-empty">
      <div class="empty-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="52" height="52" viewBox="0 0 24 24" fill="none"
          stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
          <rect x="9" y="3" width="6" height="4" rx="1"/>
          <path d="M9 12h6M9 16h4"/>
        </svg>
      </div>
      <p class="empty-title">Không có đơn hàng nào</p>
      <p class="empty-sub">{{ activeStatus === 'ALL' ? 'Bạn chưa có đơn hàng nào trong mục này.' : 'Không có đơn hàng với trạng thái này.' }}</p>
    </div>

    <!-- Orders List -->
    <div v-else class="orders-list">
      <div
        v-for="(order, index) in orders"
        :key="order.id"
        class="order-card slide-in"
        :style="{ animationDelay: `${index * 0.07}s` }"
      >
        <!-- Left: Info -->
        <div class="order-info">
          <div class="order-number-row">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
              stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
            </svg>
            <span class="order-number">{{ order.orderNumber }}</span>
          </div>
          <p class="order-meta">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            {{ formatDate(order.createdAt) }}
          </p>
          <p class="order-meta">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            </svg>
            {{ order.items?.length || 0 }} sản phẩm
          </p>
          <!-- Delivery date & overdue badge -->
          <p v-if="order.deliveryDate" class="order-meta">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
            </svg>
            Ngày giao: {{ formatDateShort(order.deliveryDate) }}
            <span v-if="isOverdue(order.deliveryDate)" class="badge-overdue">🔴 Đang bị trễ hẹn</span>
          </p>
          <span :class="statusBadgeClass(order.status)" class="status-badge">
            {{ statusText(order.status, order.orderType) }}
          </span>
        </div>

        <!-- Right: Price & Actions -->
        <div class="order-actions-col">
          <div v-if="order.totalPrice" class="order-price-block">
            <div class="price-row">
              <span class="price-label">Tổng giá trị</span>
              <span class="price-value">{{ formatCurrency(order.totalPrice) }}</span>
            </div>
            <div class="price-row">
              <span class="price-label">
                <template v-if="order.status === 'AWAITING_REMAINING_PAYMENT'">Còn lại cần thanh toán</template>
                <template v-else>{{ activeOrderType === 'READY_MADE' ? 'Thanh toán' : 'Cọc trước 60%' }}</template>
              </span>
              <span :class="order.status === 'AWAITING_REMAINING_PAYMENT' ? 'price-remaining' : 'price-deposit'">
                <template v-if="order.status === 'AWAITING_REMAINING_PAYMENT'">
                  {{ formatCurrency(Number(order.totalPrice) - Number(order.depositAmount)) }}
                </template>
                <template v-else>{{ formatCurrency(order.depositAmount) }}</template>
              </span>
            </div>

          </div>

          <div class="order-btns">
            <button class="btn-detail" @click="openDetail(order)">
              <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              Xem chi tiết
            </button>
            <!-- Remaining payment button -->
            <button
              v-if="order.status === 'AWAITING_REMAINING_PAYMENT'"
              class="btn-pay btn-pay--remaining"
              @click="openPayment(order)"
            >
              💳 Thanh toán nốt số dư
            </button>
            <button
              v-else-if="order.status === 'AWAITING_PAYMENT' || order.status === 'DEPOSITED'"
              class="btn-pay"
              :class="{ 'btn-pay--done': order.status === 'DEPOSITED' }"
              @click="openPayment(order)"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
                <line x1="12" y1="18" x2="12.01" y2="18"/>
              </svg>
              {{ payBtnLabel(order) }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="!isLoading && totalPages > 1" class="pagination-wrap">
      <button class="pg-btn" :disabled="currentPage === 0" @click="loadOrders(currentPage - 1)">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
      </button>
      <button
        v-for="p in totalPages"
        :key="p"
        class="pg-btn pg-num"
        :class="{ active: p - 1 === currentPage }"
        @click="loadOrders(p - 1)"
      >{{ p }}</button>
      <button class="pg-btn" :disabled="currentPage >= totalPages - 1" @click="loadOrders(currentPage + 1)">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </button>
    </div>

    <!-- ── Detail Modal ── -->
    <Teleport to="body">
      <div v-if="showDetailModal" class="modal-overlay" @click.self="showDetailModal = false">
        <div class="modal-box modal-xl-box" role="dialog" aria-modal="true">
          <div class="modal-head">
            <span class="modal-head-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
              </svg>
              Chi tiết đơn — {{ selectedOrder?.orderNumber }}
            </span>
            <button class="modal-close" @click="showDetailModal = false">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>

          <div class="modal-body-content" v-if="selectedOrder">

            <!-- ⚠️ Remaining Payment Warning Banner -->
            <div
              v-if="selectedOrder.status === 'AWAITING_REMAINING_PAYMENT'"
              class="remaining-payment-banner"
            >
              <div class="rpb-icon">⚠️</div>
              <div class="rpb-content">
                <strong>Đơn hàng sắp hoàn thành!</strong>
                <p>Vui lòng thanh toán 30% phần còn lại trước khi giao hàng.</p>
                <p v-if="selectedOrder.totalPrice && selectedOrder.depositAmount" class="rpb-amount">
                  Số tiền cần thanh toán: <strong>{{ formatCurrency(selectedOrder.totalPrice - selectedOrder.depositAmount) }}</strong>
                </p>
              </div>
              <button class="btn-pay btn-pay--remaining" @click="openPayment(selectedOrder); showDetailModal = false">
                💳 Thanh toán nốt số dư
              </button>
            </div>

            <!-- Overdue delivery warning -->
            <div v-if="selectedOrder.deliveryDate && isOverdue(selectedOrder.deliveryDate)" class="overdue-banner">
              🔴 Đơn hàng đang bị <strong>trễ hẹn giao</strong>. Hệ thống đã ghi nhận và admin đang xử lý.
            </div>

            <!-- Status Timeline -->
            <div class="status-timeline" v-if="selectedOrder.status !== 'CANCELLED'">
              <div
                v-for="step in activeTimeline"
                :key="step.key"
                class="timeline-step"
                :class="{
                  'step-done':    step.index < currentStatusIndex,
                  'step-current': step.index === currentStatusIndex,
                  'step-future':  step.index > currentStatusIndex
                }"
              >
                <div class="step-dot">
                  <svg v-if="step.index < currentStatusIndex" xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                  <span v-else-if="step.index === currentStatusIndex" class="dot-pulse"></span>
                </div>
                <div class="step-connector" v-if="step.index < activeTimeline.length - 1"></div>
                <span class="step-label">{{ step.label }}</span>
              </div>
            </div>
            <div class="status-cancelled-banner" v-else>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
              Đơn hàng đã bị hủy
            </div>

            <!-- Summary Row -->
            <div class="detail-summary">
              <div class="detail-summary-item">
                <span class="ds-label">Ngày tạo</span>
                <span class="ds-value">{{ formatDate(selectedOrder.createdAt) }}</span>
              </div>
              <div v-if="selectedOrder.totalPrice" class="detail-summary-item">
                <span class="ds-label">Tổng giá trị</span>
                <span class="ds-value ds-price">{{ formatCurrency(selectedOrder.totalPrice) }}</span>
              </div>
              <div v-if="selectedOrder.depositAmount" class="detail-summary-item">
                <span class="ds-label">{{ selectedOrder.orderType === 'READY_MADE' ? 'Thanh toán' : 'Cọc 60%' }}</span>
                <span class="ds-value ds-deposit">{{ formatCurrency(selectedOrder.depositAmount) }}</span>
              </div>
              <div class="detail-summary-item">
                <span class="ds-label">Số sản phẩm</span>
                <span class="ds-value">{{ selectedOrder.items?.length || 0 }}</span>
              </div>
            </div>

            <!-- Items Table -->
            <h3 class="items-title">Danh sách vật tư</h3>
            <div class="table-wrap">
              <table class="items-table">
                <thead>
                  <tr>
                    <th class="th-center" style="width:4%">STT</th>
                    <th style="width:13%">VNN_NO</th>
                    <th style="width:10%">Item Code<br><small>品目コード</small></th>
                    <th style="width:10%">Drawing No.<br><small>図番</small></th>
                    <th style="width:17%">Parts Name<br><small>品名</small></th>
                    <th style="width:13%">Spec<br><small>型式</small></th>
                    <th style="width:8%">Material<br><small>材質</small></th>
                    <th class="th-center" style="width:5%">QTY</th>
                    <th class="th-right" style="width:10%">Đơn giá<br><small>VNĐ</small></th>
                    <th class="th-right" style="width:10%">Thành tiền<br><small>VNĐ</small></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, idx) in selectedOrder.items" :key="item.id">
                    <td class="td-center">{{ idx + 1 }}</td>
                    <td class="td-truncate">{{ item.unit || '—' }}</td>
                    <td class="td-truncate">{{ item.itemCode || '—' }}</td>
                    <td class="td-truncate">{{ item.drawingNumber || '—' }}</td>
                    <td class="td-truncate">{{ item.itemName || '—' }}</td>
                    <td class="td-truncate">{{ item.specification || '—' }}</td>
                    <td class="td-truncate">{{ item.material || '—' }}</td>
                    <td class="td-center td-bold">{{ item.quantity }}</td>
                    <td class="td-right">
                      <span v-if="item.unitPrice">{{ formatNumber(item.unitPrice) }}</span>
                      <span v-else class="td-muted">—</span>
                    </td>
                    <td class="td-right td-bold">
                      <span v-if="item.totalItemPrice">{{ formatNumber(item.totalItemPrice) }}</span>
                      <span v-else class="td-muted">—</span>
                    </td>
                  </tr>
                </tbody>
                <tfoot v-if="selectedOrder.totalPrice">
                  <tr>
                    <td colspan="9" class="tfoot-label">Tổng giá trị đơn hàng:</td>
                    <td class="tfoot-value">{{ formatNumber(selectedOrder.totalPrice) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <!-- Admin notes -->
            <div v-if="hasReviewedItems" class="review-block">
              <h4 class="review-title">Trạng thái duyệt</h4>
              <div class="review-badges">
                <span class="rv-badge rv-approved">Đã duyệt: {{ reviewCounts.approved }}</span>
                <span v-if="reviewCounts.rejected > 0" class="rv-badge rv-rejected">Từ chối: {{ reviewCounts.rejected }}</span>
                <span v-if="reviewCounts.discussion > 0" class="rv-badge rv-discuss">Trao đổi: {{ reviewCounts.discussion }}</span>
                <span v-if="reviewCounts.pending > 0" class="rv-badge rv-pending">Chờ: {{ reviewCounts.pending }}</span>
              </div>
              <div v-for="item in selectedOrder.items" :key="'n-' + item.id">
                <div
                  v-if="item.adminNote && (item.reviewStatus === 'REJECTED' || item.reviewStatus === 'NEED_DISCUSSION')"
                  class="admin-note"
                  :class="item.reviewStatus === 'REJECTED' ? 'note-reject' : 'note-discuss'"
                >
                  <strong>{{ item.itemName }}:</strong> {{ item.adminNote }}
                </div>
              </div>
            </div>
          </div>

          <div class="modal-foot">
            <button class="btn-modal-close" @click="showDetailModal = false">Đóng</button>

            <!-- Cancel button for manufacturing orders -->
            <button
              v-if="canCancelOrder(selectedOrder)"
              class="btn-cancel-order"
              :disabled="isCancelling"
              @click="cancelOrder(selectedOrder)"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
              {{ isCancelling ? 'Đang hủy...' : 'Hủy đơn hàng' }}
            </button>

            <button
              v-if="selectedOrder?.status === 'AWAITING_PAYMENT' || selectedOrder?.status === 'DEPOSITED'"
              class="btn-pay"
              :class="{ 'btn-pay--done': selectedOrder?.status === 'DEPOSITED' }"
              @click="openPayment(selectedOrder); showDetailModal = false"
            >
              {{ payBtnLabel(selectedOrder) }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ── Payment Modal ── -->
    <Teleport to="body">
      <div class="modal fade" id="orderHistoryPaymentModal" tabindex="-1" ref="paymentModalRef">
        <div class="modal-dialog modal-dialog-centered" style="max-width: 520px;">
          <div class="modal-content border-0 shadow-lg">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">
                <i class="bi bi-wallet2 me-2"></i>Thanh toán
              </h5>
              <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0" v-if="paymentOrderId">
              <PaymentQR :order-id="paymentOrderId" @payment-confirmed="onPaymentConfirmed" />
            </div>
          </div>
        </div>
      </div>
    </Teleport>

  </section>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'
import PaymentQR from '../customer/PaymentQR.vue'
import { Modal } from 'bootstrap'

const isLoading = ref(true)
const isCancelling = ref(false)
const orders = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const activeOrderType = ref('CUSTOM_MANUFACTURING')
const activeStatus = ref('ALL')

const selectedOrder = ref(null)
const showDetailModal = ref(false)

const paymentOrderId = ref(null)
const paymentModalRef = ref(null)
let bsPaymentModal = null

// ── Type tabs (main level) ──
const typeTabs = [
  { key: 'CUSTOM_MANUFACTURING', label: 'Đơn hàng gia công', icon: '🔧' },
  { key: 'READY_MADE',           label: 'Sản phẩm đã mua',   icon: '🛒' },
]

// ── Status sub-tabs per type ──
const manufacturingStatusTabs = [
  { key: 'ALL',                        label: 'Tất cả' },
  { key: 'PENDING_QUOTE',              label: 'Chờ báo giá' },
  { key: 'AWAITING_PAYMENT',           label: 'Chờ thanh toán' },
  { key: 'DEPOSITED',                  label: 'Đã cọc' },
  { key: 'PROCESSING',                 label: 'Đang gia công' },
  { key: 'AWAITING_REMAINING_PAYMENT', label: 'Chờ TT đợt 2' },
  { key: 'AWAITING_DELIVERY',          label: 'Chờ giao hàng' },
  { key: 'SHIPPING',                   label: 'Đang giao' },
  { key: 'COMPLETED',                  label: 'Hoàn thành' },
  { key: 'CANCELLED',                  label: 'Đã hủy' },
]

const productStatusTabs = [
  { key: 'ALL',               label: 'Tất cả' },
  { key: 'AWAITING_PAYMENT',  label: 'Chờ thanh toán' },
  { key: 'AWAITING_DELIVERY', label: 'Chờ giao hàng' },
  { key: 'SHIPPING',          label: 'Đang giao' },
  { key: 'COMPLETED',         label: 'Đã nhận hàng' },
  { key: 'CANCELLED',         label: 'Đã hủy' },
]

const currentStatusTabs = computed(() =>
  activeOrderType.value === 'READY_MADE' ? productStatusTabs : manufacturingStatusTabs
)

// ── Timeline definitions ──
const manufacturingTimeline = [
  { key: 'PENDING_QUOTE',              label: 'Chờ báo giá',        index: 0 },
  { key: 'AWAITING_PAYMENT',           label: 'Chờ thanh toán',     index: 1 },
  { key: 'DEPOSITED',                  label: 'Đã cọc',             index: 2 },
  { key: 'PROCESSING',                 label: 'Đang gia công',      index: 3 },
  { key: 'AWAITING_REMAINING_PAYMENT', label: 'Chờ TT đợt 2',      index: 4 },
  { key: 'AWAITING_DELIVERY',          label: 'Chờ giao hàng',      index: 5 },
  { key: 'SHIPPING',                   label: 'Đang giao',          index: 6 },
  { key: 'COMPLETED',                  label: 'Hoàn thành',         index: 7 },
]

const productTimeline = [
  { key: 'AWAITING_PAYMENT',  label: 'Chờ thanh toán',  index: 0 },
  { key: 'AWAITING_DELIVERY', label: 'Chờ giao hàng',   index: 1 },
  { key: 'SHIPPING',          label: 'Đang giao',        index: 2 },
  { key: 'COMPLETED',         label: 'Đã nhận hàng',    index: 3 },
]

const activeTimeline = computed(() => {
  if (!selectedOrder.value) return manufacturingTimeline
  return selectedOrder.value.orderType === 'READY_MADE' ? productTimeline : manufacturingTimeline
})

const currentStatusIndex = computed(() => {
  const step = activeTimeline.value.find(s => s.key === selectedOrder.value?.status)
  return step ? step.index : -1
})

// ── Status label + badge helpers ──
const statusText = (status, orderType) => {
  if (orderType === 'READY_MADE') {
    const map = {
      AWAITING_PAYMENT:  'Chờ thanh toán',
      AWAITING_DELIVERY: 'Chờ giao hàng ✔',
      SHIPPING:          'Đang giao hàng',
      COMPLETED:         'Đã nhận hàng ✔',
      CANCELLED:         'Đã hủy',
    }
    return map[status] || status
  }
  const map = {
    PENDING_QUOTE:              'Chờ báo giá',
    AWAITING_PAYMENT:           'Chờ thanh toán',
    DEPOSITED:                  'Đã cọc ✔',
    PROCESSING:                 'Đang gia công',
    AWAITING_REMAINING_PAYMENT: '⚠️ Chờ thanh toán đợt 2',
    AWAITING_DELIVERY:          'Chờ giao hàng',
    SHIPPING:                   'Đang giao hàng',
    COMPLETED:                  'Hoàn thành',
    CANCELLED:                  'Đã hủy',
  }
  return map[status] || status
}

const statusBadgeClass = (status) => {
  const map = {
    PENDING_QUOTE:              'badge-warning',
    AWAITING_PAYMENT:           'badge-info',
    DEPOSITED:                  'badge-success',
    PROCESSING:                 'badge-primary',
    AWAITING_REMAINING_PAYMENT: 'badge-danger',
    AWAITING_DELIVERY:          'badge-info',
    SHIPPING:                   'badge-primary',
    COMPLETED:                  'badge-success',
    CANCELLED:                  'badge-danger',
  }
  return map[status] || 'badge-secondary'
}

const payBtnLabel = (order) => {
  if (!order) return ''
  if (order.orderType === 'READY_MADE') {
    return order.status === 'DEPOSITED' ? 'Xem thanh toán' : 'Thanh toán'
  }
  return order.status === 'DEPOSITED' ? 'Đã cọc ✔' : 'Thanh toán cọc'
}

// ── Cancel order ──
const canCancelOrder = (order) => {
  if (!order) return false
  return order.orderType === 'CUSTOM_MANUFACTURING' &&
    (order.status === 'PENDING_QUOTE' || order.status === 'AWAITING_PAYMENT')
}

const cancelOrder = async (order) => {
  const result = await Swal.fire({
    title: 'Hủy đơn hàng?',
    text: `Bạn có chắc muốn hủy đơn ${order.orderNumber}? Hành động này không thể hoàn tác.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Hủy đơn',
    cancelButtonText: 'Giữ lại',
  })
  if (!result.isConfirmed) return

  isCancelling.value = true
  try {
    await apiClient.put(`/orders/${order.id}/cancel`)
    showDetailModal.value = false
    await loadOrders(currentPage.value)
    Swal.fire({ icon: 'success', title: 'Đã hủy đơn hàng', text: `Đơn ${order.orderNumber} đã được hủy.`, timer: 2500, showConfirmButton: false })
  } catch (err) {
    const msg = err.response?.data?.error || 'Không thể hủy đơn hàng'
    Swal.fire({ icon: 'error', title: 'Lỗi', text: msg, timer: 3000, showConfirmButton: false })
  } finally {
    isCancelling.value = false
  }
}

// ── Data loading ──
const changeOrderType = (key) => {
  activeOrderType.value = key
  activeStatus.value = 'ALL'
  loadOrders(0)
}

const changeStatus = (key) => {
  activeStatus.value = key
  loadOrders(0)
}

const loadOrders = async (page = 0) => {
  isLoading.value = true
  try {
    const params = { page, size: 8, orderType: activeOrderType.value }
    if (activeStatus.value !== 'ALL') params.status = activeStatus.value
    const response = await apiClient.get('/orders/my', { params })
    orders.value = response.data.content || response.data
    currentPage.value = response.data.number || 0
    totalPages.value = response.data.totalPages || 1
  } catch {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: 'Không thể tải danh sách đơn hàng', timer: 2000, showConfirmButton: false })
  } finally {
    isLoading.value = false
  }
}

const openDetail = async (order) => {
  try {
    const response = await apiClient.get(`/orders/${order.id}`)
    selectedOrder.value = response.data
    showDetailModal.value = true
  } catch {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: 'Không thể tải chi tiết đơn hàng', timer: 2000, showConfirmButton: false })
  }
}

const openPayment = async (order) => {
  paymentOrderId.value = order.id
  await nextTick()
  if (!bsPaymentModal && paymentModalRef.value) {
    bsPaymentModal = new Modal(paymentModalRef.value)
  }
  bsPaymentModal?.show()
}

const onPaymentConfirmed = (info) => {
  loadOrders(currentPage.value)
  Swal.fire({ icon: 'success', title: 'Đã xác nhận thanh toán!', text: `Đơn hàng ${info.orderNumber} đã được cập nhật.`, timer: 3000, showConfirmButton: false })
}

// ── Review counts ──
const hasReviewedItems = computed(() => {
  if (!selectedOrder.value?.items) return false
  return selectedOrder.value.items.some(i => i.reviewStatus && i.reviewStatus !== 'PENDING_REVIEW')
})

const reviewCounts = computed(() => {
  const items = selectedOrder.value?.items || []
  return {
    approved:   items.filter(i => i.reviewStatus === 'APPROVED').length,
    rejected:   items.filter(i => i.reviewStatus === 'REJECTED').length,
    discussion: items.filter(i => i.reviewStatus === 'NEED_DISCUSSION').length,
    pending:    items.filter(i => !i.reviewStatus || i.reviewStatus === 'PENDING_REVIEW').length,
  }
})

const formatDate = (d) => {
  if (!d) return '—'
  return new Date(d).toLocaleString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const formatDateShort = (d) => {
  if (!d) return '—'
  // Handle both date string and datetime
  const date = new Date(d)
  return date.toLocaleDateString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const isOverdue = (deliveryDate) => {
  if (!deliveryDate) return false
  return new Date(deliveryDate) < new Date(new Date().setHours(0,0,0,0))
}

const formatNumber = (n) => n ? new Intl.NumberFormat('vi-VN').format(n) : '—'

const formatCurrency = (n) => n
  ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n)
  : '—'

onMounted(() => loadOrders())
</script>

<style scoped>
/* ── Shell ── */
.orders-section {
  padding: 28px 32px;
}

.section-header { margin-bottom: 10px; }

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
  margin: 0 0 18px;
}

/* ── Type Tabs (Main level) ── */
.type-tabs-wrap {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.type-tab {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 10px 22px;
  border-radius: 10px;
  border: 2px solid #e2e8f0;
  background: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.type-tab:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

.type-tab.active {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
  box-shadow: 0 4px 12px rgba(37,99,235,0.25);
}

.type-tab-icon {
  font-size: 16px;
}

/* ── Status Sub-Tabs ── */
.order-tabs-wrap {
  overflow-x: auto;
  margin-bottom: 18px;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.order-tabs-wrap::-webkit-scrollbar { display: none; }

.order-tabs {
  display: flex;
  gap: 4px;
  min-width: max-content;
}

.order-tab {
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.order-tab:hover {
  border-color: #2563eb;
  color: #2563eb;
}

.order-tab.active {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
  font-weight: 500;
}

/* ── Order Cards ── */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 16px 18px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  transition: box-shadow 0.2s, border-color 0.2s, transform 0.2s;
}

.order-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.08);
  transform: translateY(-2px);
}

.slide-in {
  opacity: 0;
  animation: slideIn 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

.order-info {
  flex: 1;
  min-width: 0;
}

.order-number-row {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 6px;
}

.order-number {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.order-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
  color: #64748b;
  margin: 0 0 4px;
}

.status-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-top: 6px;
}

.badge-warning  { background: #fef9c3; color: #854d0e; }
.badge-info     { background: #e0f2fe; color: #0369a1; }
.badge-success  { background: #dcfce7; color: #15803d; }
.badge-primary  { background: #dbeafe; color: #1d4ed8; }
.badge-danger   { background: #fee2e2; color: #b91c1c; }
.badge-secondary{ background: #f1f5f9; color: #64748b; }

.order-actions-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  flex-shrink: 0;
}

.order-price-block {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

.price-label { font-size: 12px; color: #94a3b8; }

.price-value {
  font-size: 15px;
  font-weight: 700;
  color: #2563eb;
}

.price-deposit {
  font-size: 13px;
  font-weight: 600;
  color: #16a34a;
}

.price-remaining {
  font-size: 13px;
  font-weight: 600;
  color: #dc2626; /* Red/Orange for remaining amount */
}

.order-btns {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.btn-detail, .btn-pay, .btn-cancel-order {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-detail {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.btn-detail:hover {
  background: #dbeafe;
  border-color: #2563eb;
}

.btn-pay {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #fff;
}

.btn-pay:hover { opacity: 0.9; transform: translateY(-1px); }

.btn-pay--done {
  background: #f0fdf4;
  color: #15803d;
  border: 1px solid #86efac;
}

.btn-pay--done:hover { background: #dcfce7; }

.btn-cancel-order {
  background: #fff1f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.btn-cancel-order:hover:not(:disabled) {
  background: #fee2e2;
  border-color: #dc2626;
}

.btn-cancel-order:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ── Empty State ── */
.orders-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 10px;
}

.empty-icon {
  width: 80px; height: 80px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
}

.empty-title { font-size: 16px; font-weight: 600; color: #475569; margin: 0; }
.empty-sub   { font-size: 13px; color: #94a3b8; margin: 0; }

/* ── Pagination ── */
.pagination-wrap {
  display: flex;
  gap: 6px;
  justify-content: center;
  margin-top: 20px;
}

.pg-btn {
  min-width: 34px;
  height: 34px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #475569;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  padding: 0 8px;
}

.pg-btn:hover:not(:disabled) {
  border-color: #2563eb;
  color: #2563eb;
}

.pg-btn.active {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
  font-weight: 600;
}

.pg-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ── Modal ── */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  z-index: 1050;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 16px;
  overflow-y: auto;
  backdrop-filter: blur(2px);
}

.modal-box {
  background: #fff;
  border-radius: 14px;
  width: 100%;
  max-width: 860px;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18);
  animation: modalIn 0.25s cubic-bezier(0.16,1,0.3,1);
}

.modal-xl-box { max-width: 920px; }

@keyframes modalIn {
  from { opacity: 0; transform: translateY(-12px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.modal-head-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px; height: 30px;
  border-radius: 6px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s;
}

.modal-close:hover { background: #fee2e2; color: #dc2626; }

.modal-body-content {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

/* ── Timeline ── */
.status-timeline {
  display: flex;
  align-items: flex-start;
  gap: 0;
  margin-bottom: 22px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.timeline-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  gap: 6px;
}

.step-dot {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 11px;
  z-index: 1;
  transition: all 0.3s;
}

.step-done .step-dot    { background: #16a34a; }
.step-current .step-dot { background: #2563eb; box-shadow: 0 0 0 4px rgba(37,99,235,0.2); }
.step-future .step-dot  { background: #e2e8f0; }

.dot-pulse {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #fff;
  animation: pulseDot 1.2s ease-in-out infinite;
}

@keyframes pulseDot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50%       { opacity: 0.5; transform: scale(0.7); }
}

.step-connector {
  position: absolute;
  top: 14px;
  left: 50%;
  width: 100%;
  height: 2px;
  background: #e2e8f0;
  z-index: 0;
}

.step-done .step-connector { background: #16a34a; }

.step-label {
  font-size: 11px;
  color: #64748b;
  text-align: center;
  line-height: 1.3;
}

.step-current .step-label { color: #2563eb; font-weight: 600; }
.step-done .step-label    { color: #15803d; }

.status-cancelled-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff1f2;
  border-radius: 8px;
  color: #b91c1c;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
}

/* ── Detail Summary ── */
.detail-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 18px;
}

.detail-summary-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.ds-label { font-size: 11px; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.5px; }
.ds-value { font-size: 13px; font-weight: 600; color: #1e293b; }
.ds-price { color: #2563eb; }
.ds-deposit { color: #16a34a; }

/* ── Items Table ── */
.items-title {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin: 0 0 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.table-wrap {
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin-bottom: 16px;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12.5px;
}

.items-table th {
  background: #f8fafc;
  padding: 8px 10px;
  font-weight: 600;
  color: #475569;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.items-table td {
  padding: 8px 10px;
  border-bottom: 1px solid #f1f5f9;
  color: #334155;
  vertical-align: middle;
}

.items-table tbody tr:last-child td { border-bottom: none; }
.items-table tbody tr:hover td { background: #f8fafc; }

.th-center, .td-center { text-align: center; }
.th-right, .td-right   { text-align: right; }
.td-truncate           { max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.td-bold               { font-weight: 600; }
.td-muted              { color: #94a3b8; }

.tfoot-label {
  text-align: right;
  font-weight: 600;
  color: #475569;
  padding: 10px;
  background: #f8fafc;
  border-top: 2px solid #e2e8f0;
}

.tfoot-value {
  text-align: right;
  font-weight: 700;
  color: #2563eb;
  background: #f8fafc;
  border-top: 2px solid #e2e8f0;
}

/* ── Review block ── */
.review-block { margin-top: 16px; }

.review-title {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin: 0 0 8px;
}

.review-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.rv-badge {
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.rv-approved  { background: #dcfce7; color: #15803d; }
.rv-rejected  { background: #fee2e2; color: #b91c1c; }
.rv-discuss   { background: #fef9c3; color: #854d0e; }
.rv-pending   { background: #f1f5f9; color: #64748b; }

.admin-note {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12.5px;
  margin-bottom: 6px;
  line-height: 1.5;
}

.note-reject  { background: #fff1f2; border-left: 3px solid #dc2626; }
.note-discuss { background: #fffbeb; border-left: 3px solid #d97706; }

/* ── Modal Footer ── */
.modal-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: flex-end;
  padding: 14px 20px;
  border-top: 1px solid #f1f5f9;
}

.btn-modal-close {
  padding: 8px 20px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-modal-close:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
}

/* ── Skeleton ── */
.order-skeleton {
  pointer-events: none;
}

.sk-card-left { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.sk-card-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }

.sk-line {
  height: 12px;
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 4px;
}

@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.sk-order-no { width: 140px; height: 14px; }
.sk-date     { width: 110px; }
.sk-count    { width: 70px; }
.sk-badge    { width: 70px; height: 22px; border-radius: 20px; background: #f1f5f9; }
.sk-price    { width: 100px; height: 16px; }
.sk-deposit  { width: 80px; }
.sk-btn-group { display: flex; gap: 8px; }
.sk-btn      { width: 90px; height: 30px; border-radius: 6px; background: #f1f5f9; }
</style>
