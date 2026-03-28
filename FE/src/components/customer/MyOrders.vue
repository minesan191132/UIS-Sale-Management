<template>
  <div class="container py-5 mt-5 page-container">
    <div class="d-flex justify-content-between align-items-center mb-4 fade-in">
      <h2 class="mb-0">
        <i class="bi bi-list-check me-2 text-primary"></i>Đơn Hàng Của Tôi
      </h2>
      <div class="d-flex gap-2">
        <router-link to="/create-order" class="btn btn-outline-primary shadow-sm hover-elevate">
          <i class="bi bi-plus-circle me-1"></i>Tạo đơn mới
        </router-link>
        <router-link to="/" class="btn btn-primary btn-glow shadow hover-elevate">
          <i class="bi bi-house-door me-1"></i>Trang chủ
        </router-link>
      </div>
    </div>

    <!-- Main Type Tabs -->
    <div class="type-tabs-wrap mb-4">
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

    <!-- Status Filter Tabs -->
    <div class="order-tabs-wrap mb-4">
      <div class="order-tabs">
        <button
          v-for="statusTab in currentStatusTabs"
          :key="statusTab.key"
          class="order-tab"
          :class="{ active: activeStatus === statusTab.key }"
          @click="changeStatus(statusTab.key)">
          {{ statusTab.label }}
          <span class="order-tab-count">
            {{ getStatusCount(statusTab.key) > 99 ? '99+' : getStatusCount(statusTab.key) }}
          </span>
        </button>
      </div>
    </div>

    <!-- Skeleton Loading -->
    <div v-if="isLoading" class="row g-3">
      <div v-for="i in 3" :key="i" class="col-12">
        <div class="card shadow-sm border-0 skeleton-card">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-md-6">
                <div class="skeleton-text skeleton-title mb-2"></div>
                <div class="skeleton-text skeleton-line w-50 mb-1"></div>
                <div class="skeleton-text skeleton-line w-25 mb-2"></div>
                <div class="skeleton-text skeleton-badge"></div>
              </div>
              <div class="col-md-6 text-md-end mt-3 mt-md-0">
                <div class="skeleton-text skeleton-line w-25 ms-auto mb-1"></div>
                <div class="skeleton-text skeleton-line w-50 ms-auto mb-3"></div>
                <div class="skeleton-button ms-auto"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="card shadow-sm border-0">
      <div class="card-body text-center py-5">
        <i class="bi bi-inbox fs-1 text-muted mb-3"></i>
        <h5 class="text-muted mb-3">Bạn chưa có đơn hàng nào</h5>
        <router-link to="/create-order" class="btn btn-primary">
          <i class="bi bi-plus-circle me-2"></i>Tạo đơn hàng mới
        </router-link>
      </div>
    </div>

    <!-- Orders List -->
    <div v-else class="stagger-list">
      <div class="row g-3">
        <div v-for="(order, index) in orders" :key="`${order.isTempImport ? 'imp' : 'ord'}-${order.id}`" class="col-12 slide-up" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="card shadow-sm border-0 hover-card">
            <div class="card-body">
              <div class="row align-items-center">
                <!-- Order Info -->
                <div class="col-md-6">
                  <h5 class="mb-2">
                    <i class="bi bi-file-earmark-text me-2"></i>
                    {{ order.orderNumber }}
                  </h5>
                  <p class="text-muted mb-1">
                    <i class="bi bi-calendar me-2"></i>
                    {{ formatDate(order.createdAt) }}
                  </p>
                  <p class="text-muted mb-2">
                    <i class="bi bi-box me-2"></i>
                    {{ order.items?.length || 0 }} sản phẩm
                  </p>
                  <span :class="getStatusBadgeClass(order.status)">
                    {{ getStatusText(order.status, order.orderType) }}
                  </span>
                  <div v-if="order.status === 'PENDING_APPROVAL'" class="alert alert-warning mt-2 mb-0 py-2 px-3 small pending-approval-banner">
                    <i class="bi bi-hourglass-split me-1"></i>
                    Đơn đang chờ được duyệt. Sau khi duyệt, hệ thống sẽ chuyển sang bước báo giá.
                  </div>
                </div>

                <!-- Pricing & Actions -->
                <div class="col-md-6 text-md-end mt-3 mt-md-0">
                  <!-- Price Info (if quoted) -->
                  <div v-if="order.totalPrice" class="mb-3">
                    <p class="mb-1">
                      <small class="text-muted">Tổng giá trị:</small><br>
                      <strong class="fs-5 text-primary">{{ formatCurrency(order.totalPrice) }}</strong>
                    </p>
                    <!-- READY_MADE: "Đã thanh toán" label -->
                    <p v-if="order.orderType === 'READY_MADE'" class="mb-0">
                      <small class="text-muted">Đã thanh toán:</small><br>
                      <strong class="text-success">{{ formatCurrency(order.depositAmount) }}</strong>
                    </p>
                    <!-- CUSTOM_MANUFACTURING: "Cọc trước 60%" or "Còn lại cần thanh toán" -->
                    <p v-else class="mb-0">
                      <small class="text-muted">
                        {{ order.status === 'AWAITING_REMAINING_PAYMENT' ? 'Còn lại cần thanh toán:' : 'Cọc trước 60%:' }}
                      </small><br>
                      <strong :class="order.status === 'AWAITING_REMAINING_PAYMENT' ? 'text-danger' : 'text-success'">
                        {{ order.status === 'AWAITING_REMAINING_PAYMENT' 
                           ? formatCurrency(Number(order.totalPrice) - Number(order.depositAmount)) 
                           : formatCurrency(order.depositAmount) }}
                      </strong>
                    </p>

                    <!-- Delivery Date Display -->
                    <p v-if="order.deliveryDate" class="mb-0 mt-2">
                      <small class="text-muted">Ngày giao dự kiến:</small><br>
                      <strong :class="isDeliveryDateOverdue(order.deliveryDate) ? 'text-danger' : 'text-info'">
                        {{ formatDate(order.deliveryDate) }}
                        <i v-if="isDeliveryDateOverdue(order.deliveryDate)" class="bi bi-exclamation-circle ms-1"></i>
                      </strong>
                    </p>

                    <!-- Full Payment Badge (for CUSTOM_MANUFACTURING when deposit >= total) -->
                    <div v-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.depositAmount && order.totalPrice && order.depositAmount >= order.totalPrice" class="mt-2">
                      <span class="badge bg-success">
                        <i class="bi bi-check-circle me-1"></i>Đã thanh toán toàn bộ
                      </span>
                    </div>
                  </div>

                  <!-- Actions -->
                  <div class="d-flex gap-2 justify-content-md-end flex-wrap mt-2">
                    <button @click="openDetailModal(order)" class="btn btn-outline-primary btn-sm">
                      <i class="bi bi-eye me-1"></i>Xem chi tiết
                    </button>

                    <button
                      v-if="canCancelOrder(order)"
                      @click="cancelOrder(order)"
                      class="btn btn-outline-danger btn-sm">
                      <i class="bi bi-x-circle me-1"></i>Hủy đơn
                    </button>

                    <!-- Payment Button - READY_MADE (100% payment) -->
                    <button 
                      v-if="order.orderType === 'READY_MADE' && (order.status === 'AWAITING_PAYMENT' || order.status === 'DEPOSITED')" 
                      @click="openPaymentModal(order)" 
                      class="btn btn-sm"
                      :class="order.status === 'DEPOSITED' ? 'btn-outline-success' : 'btn-success'"
                    >
                      <i class="bi bi-qr-code me-1"></i>
                      {{ order.status === 'DEPOSITED' ? 'Đã thanh toán ✔' : 'Thanh toán' }}
                    </button>

                    <!-- Deposit Payment Button - CUSTOM_MANUFACTURING (60% deposit) -->
                    <button 
                      v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.status === 'AWAITING_PAYMENT'" 
                      @click="openPaymentModal(order)" 
                      class="btn btn-sm btn-success"
                    >
                      <i class="bi bi-qr-code me-1"></i>Thanh toán cọc
                    </button>

                    <!-- Early Payment Button - CUSTOM_MANUFACTURING (remaining amount) -->
                    <button 
                      v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && (order.status === 'PROCESSING' || order.status === 'AWAITING_REMAINING_PAYMENT') && order.depositAmount && order.totalPrice && order.depositAmount < order.totalPrice" 
                      @click="openRemainingPaymentModal(order)" 
                      class="btn btn-sm btn-outline-warning"
                    >
                      <i class="bi bi-cash-coin me-1"></i>Thanh toán nốt số dư
                    </button>

                    <!-- Deposit Status - CUSTOM_MANUFACTURING -->
                    <button 
                      v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.status === 'DEPOSITED'" 
                      @click="openPaymentModal(order)" 
                      class="btn btn-sm btn-outline-success"
                    >
                      <i class="bi bi-check-circle me-1"></i>Đã cọc ✔
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <nav v-if="totalPages > 1" class="mt-4">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <button class="page-link" @click="loadOrders(currentPage - 1)">Trước</button>
          </li>
          <li 
            v-for="page in totalPages" 
            :key="page" 
            class="page-item" 
            :class="{ active: page - 1 === currentPage }">
            <button class="page-link" @click="loadOrders(page - 1)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
            <button class="page-link" @click="loadOrders(currentPage + 1)">Sau</button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Detail Modal - Teleported to body to fix backdrop z-index -->
    <Teleport to="body">
    <div class="modal fade" id="detailModal" tabindex="-1" ref="detailModalRef">
      <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content" v-if="selectedOrder">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-file-earmark-text me-2"></i>
              Chi tiết đơn hàng — {{ selectedOrder.orderNumber }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <!-- Order Info -->
            <div class="row mb-3 pb-3 border-bottom">
              <div class="col-md-4">
                <p class="mb-1"><strong>Trạng thái:</strong> 
                  <span :class="getStatusBadgeClass(selectedOrder.status)">{{ getStatusText(selectedOrder.status) }}</span>
                </p>
                <p class="mb-1"><strong>Ngày tạo:</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
              </div>
              <div class="col-md-4">
                <p class="mb-1" v-if="selectedOrder.totalPrice">
                  <strong>Tổng giá trị:</strong> 
                  <span class="text-primary fw-bold">{{ formatCurrency(selectedOrder.totalPrice) }}</span>
                </p>
                <p class="mb-1" v-if="selectedOrder.depositAmount">
                  <strong>{{ selectedOrder.status === 'AWAITING_REMAINING_PAYMENT' ? 'Còn lại cần TT:' : 'Cọc trước:' }}</strong>
                  <span :class="selectedOrder.status === 'AWAITING_REMAINING_PAYMENT' ? 'text-danger fw-bold' : 'text-success fw-bold'">
                    {{ selectedOrder.status === 'AWAITING_REMAINING_PAYMENT' 
                       ? formatCurrency(Number(selectedOrder.totalPrice) - Number(selectedOrder.depositAmount)) 
                       : formatCurrency(selectedOrder.depositAmount) }}
                  </span>
                </p>
              </div>
              <div class="col-md-4">
                <p class="mb-1"><strong>Số sản phẩm:</strong> {{ selectedOrder.items?.length || 0 }}</p>
              </div>
            </div>

            <div v-if="selectedOrder.status === 'PENDING_APPROVAL'" class="alert alert-warning py-2 px-3 small mb-3 pending-approval-banner">
              <i class="bi bi-hourglass-split me-1"></i>
              Đơn đang chờ admin duyệt. Bạn có thể theo dõi trạng thái tại đây.
            </div>

            <!-- Items Table - scrollable, same format as admin -->
            <h6 class="mb-3 mt-2">Danh sách vật tư ({{ selectedOrder.items?.length || 0 }} items)</h6>
            <div class="table-responsive" style="max-height: 450px; overflow-y: auto;">
              <table class="table table-bordered table-sm table-hover align-middle mb-0 animated-table uniform-table" style="font-size: 0.85rem; table-layout: fixed; width: 100%;">
                <thead class="table-light" style="position: sticky; top: 0; z-index: 1;">
                  <tr>
                    <th class="text-center cell-uniform" style="width: 4%">STT</th>
                    <th class="cell-uniform" style="width: 14%">VNN_NO</th>
                    <th class="cell-uniform" style="width: 10%">Item Code<br><small class="text-muted fw-normal">品目コード</small></th>
                    <th class="cell-uniform" style="width: 10%">Drawing No.<br><small class="text-muted fw-normal">図番</small></th>
                    <th class="cell-uniform" style="width: 18%">Parts Name<br><small class="text-muted fw-normal">品名</small></th>
                    <th class="cell-uniform" style="width: 14%">Spec<br><small class="text-muted fw-normal">型式</small></th>
                    <th class="cell-uniform" style="width: 8%">Material<br><small class="text-muted fw-normal">材質</small></th>
                    <th class="text-center cell-uniform" style="width: 5%">QTY</th>
                    <th class="text-end cell-uniform" style="width: 9%">Đơn giá<br><small class="text-muted fw-normal">VNĐ</small></th>
                    <th class="text-end cell-uniform" style="width: 9%">Thành tiền<br><small class="text-muted fw-normal">VNĐ</small></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in selectedOrder.items" :key="item.id">
                    <td class="text-center cell-uniform">{{ index + 1 }}</td>
                    <td class="cell-uniform cell-truncate">{{ item.unit || '—' }}</td>
                    <td class="cell-uniform cell-truncate">{{ item.itemCode || '—' }}</td>
                    <td class="cell-uniform cell-truncate">{{ item.drawingNumber || '—' }}</td>
                    <td class="cell-uniform cell-truncate">{{ item.itemName || '—' }}</td>
                    <td class="cell-uniform cell-truncate">{{ item.specification || '—' }}</td>
                    <td class="cell-uniform cell-truncate">{{ item.material || '—' }}</td>
                    <td class="text-center fw-bold cell-uniform">{{ item.quantity }}</td>
                    <td class="text-end cell-uniform">
                      <span v-if="item.unitPrice">{{ formatNumber(item.unitPrice) }}</span>
                      <span v-else class="text-muted">—</span>
                    </td>
                    <td class="text-end cell-uniform">
                      <span v-if="item.totalItemPrice" class="fw-bold">{{ formatNumber(item.totalItemPrice) }}</span>
                      <span v-else class="text-muted">—</span>
                    </td>
                  </tr>
                </tbody>
                <tfoot v-if="selectedOrder.totalPrice">
                  <tr class="table-light">
                    <td colspan="9" class="text-end fw-bold py-2">Tổng giá trị đơn hàng:</td>
                    <td class="text-end fw-bold py-2 text-primary fs-6">{{ formatNumber(selectedOrder.totalPrice) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <!-- Review Status Summary (if items have been reviewed) -->
            <div v-if="hasReviewedItems" class="mt-3">
              <h6 class="mb-2">Trạng thái review</h6>
              <div class="d-flex gap-2 flex-wrap">
                <span class="badge bg-success">Đã duyệt: {{ reviewCounts.approved }}</span>
                <span v-if="reviewCounts.rejected > 0" class="badge bg-danger">Từ chối: {{ reviewCounts.rejected }}</span>
                <span v-if="reviewCounts.discussion > 0" class="badge bg-warning text-dark">Cần trao đổi: {{ reviewCounts.discussion }}</span>
                <span v-if="reviewCounts.pending > 0" class="badge bg-secondary">Chờ review: {{ reviewCounts.pending }}</span>
              </div>
              <!-- Rejected/Discussion notes visible to customer -->
              <div v-for="item in selectedOrder.items" :key="'note-' + item.id" class="mt-1">
                <div v-if="item.adminNote && (item.reviewStatus === 'REJECTED' || item.reviewStatus === 'NEED_DISCUSSION')" 
                  class="alert py-1 px-2 mb-1"
                  :class="item.reviewStatus === 'REJECTED' ? 'alert-danger' : 'alert-warning'"
                  style="font-size: 0.8rem">
                  <strong>{{ item.itemName }}:</strong> {{ item.adminNote }}
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Dóng</button>
            <button
              v-if="canCancelOrder(selectedOrder)"
              @click="cancelOrder(selectedOrder); bsModal?.hide()"
              class="btn btn-outline-danger">
              <i class="bi bi-x-circle me-1"></i>Hủy đơn hàng
            </button>
            <button 
              v-if="selectedOrder.status === 'AWAITING_PAYMENT' || selectedOrder.status === 'DEPOSITED'" 
              @click="openPaymentModal(selectedOrder); bsModal?.hide()" 
              class="btn"
              :class="selectedOrder.status === 'DEPOSITED' ? 'btn-outline-success' : 'btn-success'"
            >
              <i class="bi bi-qr-code me-1"></i>
              {{ selectedOrder.status === 'DEPOSITED' ? 'Xem trạng thái thanh toán' : 'Thanh toán cọc 60%' }}
            </button>
          </div>
        </div>
      </div>
    </div>
    </Teleport>

    <!-- ===== Payment QR Modal ===== -->
    <Teleport to="body">
      <div class="modal fade" id="paymentQrModal" tabindex="-1" ref="paymentModalRef">
        <div class="modal-dialog modal-dialog-centered" style="max-width: 520px;">
          <div class="modal-content border-0 shadow-lg">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">
                <i class="bi bi-wallet2 me-2"></i>Thanh toán đặt cọc
              </h5>
              <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0" v-if="selectedPaymentOrderId">
              <PaymentQR
                :order-id="selectedPaymentOrderId"
                @payment-confirmed="onPaymentConfirmed"
              />
            </div>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'
import { Modal } from 'bootstrap'
import PaymentQR from './PaymentQR.vue'
import { getOrderStatusLabel } from '../../constants/orderStatus'

const route = useRoute()
const router = useRouter()

const orders = ref([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const selectedOrder = ref(null)
const detailModalRef = ref(null)
const paymentModalRef = ref(null)
const selectedPaymentOrderId = ref(null)
const activeOrderType = ref('CUSTOM_MANUFACTURING')
const getDefaultStatusForType = (type) =>
  type === 'CUSTOM_MANUFACTURING' ? 'PENDING_APPROVAL' : 'AWAITING_PAYMENT'

const activeStatus = ref(getDefaultStatusForType(activeOrderType.value))
const statusCounts = ref({})
let bsModal = null
let bsPaymentModal = null

// ── Type tabs (main level) ──
const typeTabs = [
  { key: 'CUSTOM_MANUFACTURING', label: 'Đơn hàng gia công', icon: '🔧' },
  { key: 'READY_MADE',           label: 'Sản phẩm phôi',   icon: '🛒' },
]

const manufacturingStatusTabs = [
  { key: 'PENDING_APPROVAL', label: 'Chờ duyệt đơn' },
  { key: 'PENDING_QUOTE', label: 'Chờ báo giá' },
  { key: 'AWAITING_PAYMENT', label: 'Chờ thanh toán' },
  { key: 'DEPOSITED', label: 'Đã cọc' },
  { key: 'PROCESSING', label: 'Đang gia công' },
  { key: 'AWAITING_REMAINING_PAYMENT', label: 'Chờ TT đợt 2' },
  { key: 'AWAITING_DELIVERY', label: 'Chờ giao hàng' },
  { key: 'SHIPPING', label: 'Đang giao' },
  { key: 'COMPLETED', label: 'Hoàn thành' },
  { key: 'CANCELLED', label: 'Đã hủy' },
  { key: 'ALL', label: 'Tất cả' },
]

const productStatusTabs = [
  { key: 'AWAITING_PAYMENT', label: 'Chờ thanh toán' },
  { key: 'AWAITING_DELIVERY', label: 'Chờ giao hàng' },
  { key: 'SHIPPING', label: 'Đang giao' },
  { key: 'COMPLETED', label: 'Đã nhận hàng' },
  { key: 'CANCELLED', label: 'Đã hủy' },
  { key: 'ALL', label: 'Tất cả' },
]



const currentStatusTabs = computed(() =>
  activeOrderType.value === 'READY_MADE' ? productStatusTabs : manufacturingStatusTabs
)

const getStatusCount = (statusKey) => {
  return Number(statusCounts.value?.[statusKey] || 0)
}

const createZeroCounts = (tabs) => {
  const base = {}
  for (const tab of tabs) {
    base[tab.key] = 0
  }
  return base
}

const extractTotalCount = (data) => {
  const directTotal = Number(data?.totalElements)
  if (Number.isFinite(directTotal) && directTotal >= 0) {
    return directTotal
  }

  const nestedTotal = Number(data?.page?.totalElements)
  if (Number.isFinite(nestedTotal) && nestedTotal >= 0) {
    return nestedTotal
  }

  const numberOfElements = Number(data?.numberOfElements)
  if (Number.isFinite(numberOfElements) && numberOfElements >= 0) {
    return numberOfElements
  }

  if (Array.isArray(data?.content)) {
    return data.content.length
  }

  if (Array.isArray(data)) {
    return data.length
  }

  return 0
}

const clearOrderIdQuery = async () => {
  if (!route.query?.orderId) return
  const nextQuery = { ...route.query }
  delete nextQuery.orderId
  await router.replace({ path: route.path, query: nextQuery })
}

const openOrderFromQueryIfPresent = async () => {
  const orderId = Number(route.query?.orderId || 0)
  if (!Number.isFinite(orderId) || orderId <= 0) return

  try {
    const base = await apiClient.get(`/orders/${orderId}`)
    const order = base.data || {}
    const targetType = order.orderType || activeOrderType.value

    activeOrderType.value = targetType
    activeStatus.value = 'ALL'

    await Promise.all([
      loadOrders(0),
      loadStatusCounts(targetType),
    ])

    await openDetailModal({ id: orderId, isTempImport: false })
  } catch (error) {
    console.warn('Failed to open order from notification deeplink:', error)
    await Swal.fire('Không thể mở đơn hàng', 'Đơn hàng có thể đã bị xóa hoặc bạn không có quyền truy cập.', 'warning')
  } finally {
    await clearOrderIdQuery()
  }
}

onMounted(async () => {
  await Promise.all([
    loadOrders(),
    loadStatusCounts(activeOrderType.value),
  ])
  await openOrderFromQueryIfPresent()
})

const changeOrderType = (key) => {
  activeOrderType.value = key
  activeStatus.value = getDefaultStatusForType(key)
  Promise.all([
    loadOrders(0),
    loadStatusCounts(key),
  ])
}

const changeStatus = (key) => {
  activeStatus.value = key
  loadOrders(0)
}

const loadStatusCounts = async (orderType) => {
  const safeType = orderType || activeOrderType.value
  const tabs = safeType === 'READY_MADE' ? productStatusTabs : manufacturingStatusTabs
  const counts = createZeroCounts(tabs)

  try {
    if (safeType === 'CUSTOM_MANUFACTURING') {
      const importResponse = await apiClient.get('/orders/imports/my')
      const pendingApprovalCount = Array.isArray(importResponse.data) ? importResponse.data.length : 0
      counts.PENDING_APPROVAL = pendingApprovalCount

      const statusKeys = manufacturingStatusTabs
        .map(s => s.key)
        .filter(key => key !== 'ALL' && key !== 'PENDING_APPROVAL')

      const responses = await Promise.allSettled(
        statusKeys.map(status =>
          apiClient.get('/orders/my', {
            params: { page: 0, size: 1, orderType: safeType, status },
          }),
        ),
      )

      statusKeys.forEach((status, idx) => {
        const result = responses[idx]
        if (result?.status === 'fulfilled') {
          counts[status] = extractTotalCount(result.value?.data)
        }
      })

      counts.ALL = Object.values(counts).reduce((sum, count) => sum + Number(count || 0), 0)
    } else {
      const statusKeys = productStatusTabs
        .map(s => s.key)
        .filter(key => key !== 'ALL')

      const responses = await Promise.allSettled(
        statusKeys.map(status =>
          apiClient.get('/orders/my', {
            params: { page: 0, size: 1, orderType: safeType, status },
          }),
        ),
      )

      statusKeys.forEach((status, idx) => {
        const result = responses[idx]
        if (result?.status === 'fulfilled') {
          counts[status] = extractTotalCount(result.value?.data)
        }
      })

      counts.ALL = Object.values(counts).reduce((sum, count) => sum + Number(count || 0), 0)
    }
  } catch (error) {
    console.warn('Failed to load status counts:', error)
  }

  statusCounts.value = counts
}

const loadOrders = async (page = 0) => {
  isLoading.value = true
  try {
    if (activeOrderType.value === 'CUSTOM_MANUFACTURING' && activeStatus.value === 'PENDING_APPROVAL') {
      const importResponse = await apiClient.get('/orders/imports/my')
      orders.value = (importResponse.data || []).map(o => ({ ...o, isTempImport: true }))
      currentPage.value = 0
      totalPages.value = 1
      return
    }

    const params = { page, size: 10, orderType: activeOrderType.value }
    if (activeStatus.value !== 'ALL') params.status = activeStatus.value

    const response = await apiClient.get('/orders/my', { params })
    let merged = response.data.content || response.data

    if (activeOrderType.value === 'CUSTOM_MANUFACTURING' && activeStatus.value === 'ALL' && page === 0) {
      const importResponse = await apiClient.get('/orders/imports/my')
      const pendingImports = (importResponse.data || []).map(o => ({ ...o, isTempImport: true }))
      merged = [...pendingImports, ...(Array.isArray(merged) ? merged : [])]
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
    }

    orders.value = Array.isArray(merged) ? merged.map(o => ({ ...o, isTempImport: !!o.isTempImport })) : []
    currentPage.value = response.data.number || 0
    totalPages.value = response.data.totalPages || 1
  } catch (error) {
    console.error('Failed to load orders:', error)
    Swal.fire('Lỗi', 'Không thể tải danh sách đơn hàng', 'error')
  } finally {
    isLoading.value = false
  }
}

const openDetailModal = async (order) => {
  try {
    const detailUrl = order?.isTempImport ? `/orders/imports/${order.id}` : `/orders/${order.id}`
    const response = await apiClient.get(detailUrl)
    selectedOrder.value = { ...response.data, isTempImport: !!order?.isTempImport }
    await nextTick()

    if (!bsModal && detailModalRef.value) {
      bsModal = new Modal(detailModalRef.value)
    }
    bsModal?.show()
  } catch (error) {
    console.error('Failed to load order detail:', error)
    Swal.fire('Lỗi', 'Không thể tải chi tiết đơn hàng', 'error')
  }
}

const hasReviewedItems = computed(() => {
  if (!selectedOrder.value?.items) return false
  return selectedOrder.value.items.some(i => i.reviewStatus && i.reviewStatus !== 'PENDING_REVIEW')
})

const reviewCounts = computed(() => {
  if (!selectedOrder.value?.items) return { approved: 0, rejected: 0, discussion: 0, pending: 0 }
  const items = selectedOrder.value.items
  return {
    approved: items.filter(i => i.reviewStatus === 'APPROVED').length,
    rejected: items.filter(i => i.reviewStatus === 'REJECTED').length,
    discussion: items.filter(i => i.reviewStatus === 'NEED_DISCUSSION').length,
    pending: items.filter(i => !i.reviewStatus || i.reviewStatus === 'PENDING_REVIEW').length
  }
})

const openPaymentModal = async (order) => {
  selectedPaymentOrderId.value = order.id
  await nextTick()
  if (!bsPaymentModal && paymentModalRef.value) {
    bsPaymentModal = new Modal(paymentModalRef.value)
  }
  bsPaymentModal?.show()
}

const onPaymentConfirmed = (paymentInfo) => {
  // Reload danh sách để cập nhật trạng thái
  Promise.all([
    loadOrders(),
    loadStatusCounts(activeOrderType.value),
  ])
  Swal.fire({
    icon: 'success',
    title: 'Đã nhận tiền cọc!',
    text: `Đơn hàng ${paymentInfo.orderNumber} đã được xác nhận đặt cọc thành công.`,
    timer: 3000,
    showConfirmButton: false
  })
}

const canCancelOrder = (order) => {
  if (!order) return false
  return order.status === 'PENDING_APPROVAL'
    || order.status === 'PENDING_QUOTE'
    || order.status === 'AWAITING_PAYMENT'
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

  try {
    if (order?.isTempImport) {
      await apiClient.put(`/orders/imports/${order.id}/cancel`)
    } else {
      await apiClient.put(`/orders/${order.id}/cancel`)
    }

    await loadOrders(currentPage.value)
    await loadStatusCounts(activeOrderType.value)
    Swal.fire({
      icon: 'success',
      title: 'Đã hủy đơn hàng',
      text: `Đơn ${order.orderNumber} đã được hủy.`,
      timer: 2200,
      showConfirmButton: false,
    })
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể hủy đơn hàng'
    Swal.fire('Lỗi', msg, 'error')
  }
}

// Giữ lại để tương thích nhưng redirect sang openPaymentModal
const showPaymentQR = (order) => {
  openPaymentModal(order)
}

const readyMadeStatusLabelOverrides = {
  DEPOSITED: 'Đã thanh toán ✔',
  PROCESSING: 'Đang chuẩn bị',
  COMPLETED: 'Đã nhận được hàng',
}

const customStatusLabelOverrides = {
  DEPOSITED: 'Đã cọc ✔',
}

const getStatusText = (status, orderType) => {
  if (orderType === 'READY_MADE') {
    return getOrderStatusLabel(status, readyMadeStatusLabelOverrides)
  }

  return getOrderStatusLabel(status, customStatusLabelOverrides)
}

const getStatusBadgeClass = (status) => {
  const classMap = {
    PENDING_APPROVAL: 'badge bg-warning text-dark',
    PENDING_QUOTE: 'badge bg-warning text-dark',
    AWAITING_PAYMENT: 'badge bg-info text-dark',
    DEPOSITED: 'badge bg-success',
    PROCESSING: 'badge bg-primary',
    AWAITING_REMAINING_PAYMENT: 'badge bg-warning',
    AWAITING_DELIVERY: 'badge bg-info',
    SHIPPING: 'badge bg-primary',
    COMPLETED: 'badge bg-success',
    CANCELLED: 'badge bg-danger'
  }
  return classMap[status] || 'badge bg-secondary'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatNumber = (amount) => {
  if (!amount) return '—'
  return new Intl.NumberFormat('vi-VN').format(amount)
}

const formatCurrency = (amount) => {
  if (!amount) return '-'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

const isDeliveryDateOverdue = (deliveryDate) => {
  if (!deliveryDate) return false
  const delivery = new Date(deliveryDate)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  delivery.setHours(0, 0, 0, 0)
  return delivery < today
}

const openRemainingPaymentModal = (order) => {
  selectedPaymentOrderId.value = order.id
  // Store remaining amount for display (optional, can be calculated in PaymentQR component)
  selectedOrder.value = order
  nextTick().then(() => {
    if (!bsPaymentModal && paymentModalRef.value) {
      bsPaymentModal = new Modal(paymentModalRef.value)
    }
    bsPaymentModal?.show()
  })
}
</script>

<style scoped>
/* Page & List Animations */
.page-container {
  animation: fadeIn 0.4s ease-out forwards;
}

.fade-in {
  animation: fadeIn 0.6s ease-out forwards;
}

.slide-up {
  opacity: 0;
  transform: translateY(20px);
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Card Hover Effects */
.hover-card {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid rgba(0,0,0,0.05) !important;
}

.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08) !important;
  border-color: rgba(13, 110, 253, 0.2) !important;
}

/* Button Hover Effects */
.hover-elevate {
  transition: all 0.2s ease;
}

.hover-elevate:hover {
  transform: translateY(-2px);
}

.btn-glow {
  position: relative;
  overflow: hidden;
}

.btn-glow::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, transparent 60%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  transition: transform 0.4s ease, opacity 0.4s ease;
}

.pending-approval-banner {
  border: 1px solid rgba(245, 158, 11, 0.35);
  background: linear-gradient(90deg, rgba(255, 243, 205, 0.95), rgba(255, 251, 235, 0.95));
}

.btn-glow:hover::after {
  transform: translate(-50%, -50%) scale(1);
  opacity: 1;
}

/* Table Hover Effect */
.animated-table tbody tr {
  transition: background-color 0.2s ease;
}

.animated-table tbody tr:hover {
  background-color: rgba(13, 110, 253, 0.03);
}

.modal-xl {
  max-width: 1100px;
}

/* Skeleton Loading Animation */
.skeleton-card {
  background: #fff;
  overflow: hidden;
  position: relative;
}

.skeleton-text, .skeleton-button {
  background: #e2e5e7;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.skeleton-text::after, .skeleton-button::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(255,255,255,0) 0%, rgba(255,255,255,0.6) 50%, rgba(255,255,255,0) 100%);
  animation: shimmer 1.5s infinite;
}

.skeleton-title { height: 24px; width: 40%; }
.skeleton-line { height: 16px; }
.skeleton-badge { height: 24px; width: 100px; border-radius: 50px; }
.skeleton-button { height: 32px; width: 120px; border-radius: 4px; }

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* Uniform Table Cells */
.uniform-table th,
.uniform-table td {
  vertical-align: middle;
}

.cell-uniform {
  padding: 10px 8px !important;
  min-height: 48px;
  line-height: 1.4;
}

.cell-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cell-truncate:hover {
  white-space: normal;
  word-break: break-word;
}

/* ── Type Tabs ── */
.type-tabs-wrap {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 12px;
}

.type-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
  margin-bottom: -14px;
  position: relative;
  top: 2px;
}

.type-tab:hover {
  color: #334155;
  background: rgba(59, 130, 246, 0.05);
}

.type-tab.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

.type-tab-icon {
  font-size: 1.1em;
}

/* ── Status Filter Tabs ── */
.order-tabs-wrap {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.order-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.order-tab {
  padding: 6px 14px;
  border: 1px solid #cbd5e1;
  background: #f8fafc;
  color: #475569;
  font-size: 0.9rem;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.order-tab:hover {
  background: #f1f5f9;
  border-color: #3b82f6;
  color: #3b82f6;
}

.order-tab.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.order-tab-count {
  margin-left: 6px;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 0.72rem;
  font-weight: 700;
  line-height: 1;
  background: rgba(15, 23, 42, 0.1);
  color: currentColor;
}

.order-tab.active .order-tab-count {
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
}
</style>
