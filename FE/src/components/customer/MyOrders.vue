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
        <div v-for="(order, index) in orders" :key="order.id" class="col-12 slide-up" :style="{ animationDelay: `${index * 0.1}s` }">
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
                    {{ getStatusText(order.status) }}
                  </span>
                </div>

                <!-- Pricing & Actions -->
                <div class="col-md-6 text-md-end mt-3 mt-md-0">
                  <!-- Price Info (if quoted) -->
                  <div v-if="order.totalPrice" class="mb-3">
                    <p class="mb-1">
                      <small class="text-muted">Tổng giá trị:</small><br>
                      <strong class="fs-5 text-primary">{{ formatCurrency(order.totalPrice) }}</strong>
                    </p>
                    <p class="mb-0">
                      <small class="text-muted">Cọc trước 70%:</small><br>
                      <strong class="text-success">{{ formatCurrency(order.depositAmount) }}</strong>
                    </p>
                  </div>

                  <!-- Actions -->
                  <div class="d-flex gap-2 justify-content-md-end flex-wrap">
                    <button @click="openDetailModal(order)" class="btn btn-outline-primary btn-sm">
                      <i class="bi bi-eye me-1"></i>Xem chi tiết
                    </button>
                    <button 
                      v-if="order.status === 'AWAITING_PAYMENT'" 
                      @click="showPaymentQR(order)" 
                      class="btn btn-success btn-sm">
                      <i class="bi bi-qr-code me-1"></i>Thanh toán
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
                  <strong>Cọc trước:</strong>
                  <span class="text-success fw-bold">{{ formatCurrency(selectedOrder.depositAmount) }}</span>
                </p>
              </div>
              <div class="col-md-4">
                <p class="mb-1"><strong>Số sản phẩm:</strong> {{ selectedOrder.items?.length || 0 }}</p>
              </div>
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
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button 
              v-if="selectedOrder.status === 'AWAITING_PAYMENT'" 
              @click="showPaymentQR(selectedOrder)" 
              class="btn btn-success">
              <i class="bi bi-qr-code me-1"></i>Thanh toán
            </button>
          </div>
        </div>
      </div>
    </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'
import { Modal } from 'bootstrap'

const orders = ref([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const selectedOrder = ref(null)
const detailModalRef = ref(null)
let bsModal = null

onMounted(() => {
  loadOrders()
})

const loadOrders = async (page = 0) => {
  isLoading.value = true
  try {
    const response = await apiClient.get('/orders/my', {
      params: { page, size: 10 }
    })
    orders.value = response.data.content || response.data
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
    const response = await apiClient.get(`/orders/${order.id}`)
    selectedOrder.value = response.data
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

const showPaymentQR = (order) => {
  Swal.fire({
    title: 'Thanh toán đơn hàng',
    html: `
      <div class="text-center">
        <p class="mb-3">Quét mã QR để thanh toán cọc <strong class="text-success">${formatCurrency(order.depositAmount)}</strong></p>
        <img src="${order.paymentQrUrl}" alt="QR Code" class="img-fluid" style="max-width: 300px;">
        <p class="mt-3 text-muted small">Sau khi thanh toán, vui lòng chờ Admin xác nhận.</p>
      </div>
    `,
    width: '500px',
    showCancelButton: true,
    confirmButtonText: 'Đã thanh toán',
    cancelButtonText: 'Đóng'
  }).then(async (result) => {
    if (result.isConfirmed) {
      Swal.fire('Cảm ơn!', 'Chúng tôi sẽ xác nhận thanh toán của bạn trong thời gian sớm nhất.', 'success')
      loadOrders()
    }
  })
}

const getStatusText = (status) => {
  const statusMap = {
    PENDING_QUOTE: 'Chờ báo giá',
    AWAITING_PAYMENT: 'Chờ thanh toán',
    PROCESSING: 'Đang gia công',
    COMPLETED: 'Hoàn thành',
    CANCELLED: 'Đã hủy'
  }
  return statusMap[status] || status
}

const getStatusBadgeClass = (status) => {
  const classMap = {
    PENDING_QUOTE: 'badge bg-warning',
    AWAITING_PAYMENT: 'badge bg-info',
    PROCESSING: 'badge bg-primary',
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
</style>
