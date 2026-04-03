<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-1 text-uppercase">Quản lý đơn hàng sản phẩm</h2>
    <p class="text-muted small mb-4">Chỉ hiển thị các đơn hàng mua sản phẩm catalog (mã ORD-...)</p>

    <!-- Filters -->
    <div class="card border-0 shadow-sm p-3 mb-4">
      <div class="row g-3 align-items-end justify-content-between">
        <div class="col-md-9">
          <div class="row g-2">
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Tìm kiếm</label>
              <input type="text" class="form-control form-control-sm" placeholder="Tìm mã đơn, khách hàng..."
                v-model="searchKeyword" @keyup.enter="loadOrders(0)">
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Trạng thái</label>
              <select class="form-select form-select-sm" v-model="filterStatus" @change="loadOrders(0)">
                <option value="">Tất cả</option>
                <option value="AWAITING_PAYMENT">Chờ thanh toán</option>
                <option value="DEPOSITED">Đã thanh toán</option>
                <option value="PROCESSING">Đang chuẩn bị</option>
                <option value="COMPLETED">Đã nhận hàng</option>
                <option value="CANCELLED">Đã hủy</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Từ ngày</label>
              <input type="date" class="form-control form-control-sm" v-model="dateFrom" @change="onDateFilterChanged">
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Đến ngày</label>
              <input type="date" class="form-control form-control-sm" v-model="dateTo" @change="onDateFilterChanged">
            </div>
          </div>
        </div>
        <div class="col-md-3 d-flex gap-2 justify-content-end">
          <button class="btn btn-primary btn-sm px-3" @click="loadOrders(0)">Tìm kiếm</button>
          <button class="btn btn-outline-secondary btn-sm px-3" @click="resetFilters">Đặt lại</button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="isLoading" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">Đang tải đơn hàng...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="card border-0 shadow-sm">
      <div class="card-body text-center py-5">
        <i class="bi bi-cart-x fs-1 text-muted"></i>
        <p class="text-muted mt-3">Chưa có đơn hàng sản phẩm nào</p>
      </div>
    </div>

    <!-- Orders Table -->
    <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <table class="table align-middle mb-0" style="table-layout: fixed;">
        <thead class="bg-light">
          <tr class="small text-muted text-uppercase">
            <th style="width: 200px;">Mã đơn</th>
            <th>Khách hàng</th>
            <th style="width: 120px;">Sản phẩm</th>
            <th style="width: 140px;">Trạng thái</th>
            <th style="width: 150px;">Tổng tiền</th>
            <th style="width: 130px;">Ngày tạo</th>
            <th style="width: 200px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td class="fw-bold text-primary">{{ order.orderNumber }}</td>
            <td>
              <div>{{ order.userName }}</div>
              <small class="text-muted">{{ order.companyName }}</small>
            </td>
            <td>
              <span class="badge bg-info-subtle text-info px-3">
                {{ order.items?.length || 0 }} sp
              </span>
            </td>
            <td>
              <span :class="getStatusBadgeClass(order.status)">{{ getStatusText(order.status) }}</span>
            </td>
            <td class="fw-semibold">{{ formatCurrency(order.totalPrice) }}</td>
            <td class="text-muted small">{{ formatDate(order.createdAt) }}</td>
            <td>
              <div class="btn-group btn-group-sm">
                <!-- View detail -->
                <button @click="openDetail(order)" class="btn btn-outline-primary btn-sm" title="Xem chi tiết">
                  <i class="bi bi-eye"></i>
                </button>
                <button
                  v-if="canAdminCancelOrder(order)"
                  @click="cancelOrderByAdmin(order)"
                  class="btn btn-outline-danger btn-sm"
                  title="Hủy đơn"
                >
                  <i class="bi bi-slash-circle"></i>
                </button>
                <!-- Confirm payment (AWAITING_PAYMENT -> DEPOSITED) -->
                <button
                  v-if="order.status === 'AWAITING_PAYMENT'"
                  @click="confirmPayment(order)"
                  class="btn btn-outline-success btn-sm"
                  title="Xác nhận đã thanh toán"
                >
                  <i class="bi bi-check-circle"></i> Xác nhận TT
                </button>
                <!-- Start preparation (DEPOSITED -> PROCESSING) -->
                <button
                  v-if="order.status === 'DEPOSITED'"
                  @click="startPreparation(order)"
                  class="btn btn-success btn-sm"
                  title="Bắt đầu chuẩn bị đơn"
                >
                  <i class="bi bi-box-seam me-1"></i>Chuẩn bị
                </button>
                <!-- Delay delivery (PROCESSING: báo trễ hẹn) -->
                <button
                  v-if="order.status === 'PROCESSING' && order.deliveryDate"
                  @click="openDelayModal(order)"
                  class="btn btn-outline-warning btn-sm"
                  title="Báo trễ hẹn"
                >
                  <i class="bi bi-calendar-x me-1"></i>Trễ hẹn
                </button>
                <!-- Ship order (PROCESSING -> SHIPPING) -->
                <button
                  v-if="order.status === 'PROCESSING' && isFullyPaid(order)"
                  @click="shipOrder(order)"
                  class="btn btn-outline-info btn-sm"
                  title="Giao cho ĐVVC"
                >
                  <i class="bi bi-truck me-1"></i>Giao hàng
                </button>
                <!-- Mark delivered (SHIPPING -> COMPLETED) -->
                <button
                  v-if="order.status === 'SHIPPING'"
                  @click="completeOrder(order)"
                  class="btn btn-primary btn-sm"
                  title="Xác nhận đã giao"
                >
                  <i class="bi bi-check-lg me-1"></i>Hoàn thành
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <nav v-if="totalPages > 1" class="p-3">
        <ul class="pagination justify-content-center mb-0">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <button class="page-link" @click="loadOrders(currentPage - 1)">Trước</button>
          </li>
          <li v-for="page in totalPages" :key="page" class="page-item"
            :class="{ active: page - 1 === currentPage }">
            <button class="page-link" @click="loadOrders(page - 1)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
            <button class="page-link" @click="loadOrders(currentPage + 1)">Sau</button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Detail Modal -->
    <div class="modal fade" id="productOrderDetailModal" tabindex="-1" ref="detailModalRef">
      <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content" v-if="selectedOrder">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-receipt me-2"></i>
              Chi tiết đơn — {{ selectedOrder.orderNumber }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <!-- Order summary -->
            <div class="row mb-4">
              <div class="col-md-4">
                <p class="mb-1"><strong>Khách hàng:</strong> {{ selectedOrder.userName }}</p>
                <p class="mb-1"><strong>Công ty:</strong> {{ selectedOrder.companyName }}</p>
              </div>
              <div class="col-md-4">
                <p class="mb-1">
                  <strong>Trạng thái:</strong>
                  <span :class="getStatusBadgeClass(selectedOrder.status)" class="ms-2">
                    {{ getStatusText(selectedOrder.status) }}
                  </span>
                </p>
                <p class="mb-1"><strong>Ngày tạo:</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
              </div>
              <div class="col-md-4">
                <p class="mb-1" v-if="selectedOrder.totalPrice">
                  <strong>Tổng thanh toán:</strong>
                  <span class="text-success fw-bold ms-1">{{ formatCurrency(selectedOrder.totalPrice) }}</span>
                </p>
                <p class="mb-1" v-if="selectedOrder.notes">
                  <strong>Ghi chú:</strong> {{ selectedOrder.notes }}
                </p>
              </div>
            </div>

            <!-- Items table -->
            <div class="table-responsive">
              <table class="table table-bordered table-sm align-middle">
                <thead class="table-light">
                  <tr>
                    <th>STT</th>
                    <th>Tên sản phẩm</th>
                    <th class="text-center">SL</th>
                    <th class="text-end">Đơn giá</th>
                    <th class="text-end">Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, idx) in selectedOrder.items" :key="item.id">
                    <td>{{ idx + 1 }}</td>
                    <td>{{ item.itemName }}</td>
                    <td class="text-center fw-bold">{{ item.quantity }}</td>
                    <td class="text-end">{{ item.unitPrice ? formatNumber(item.unitPrice) : '—' }}</td>
                    <td class="text-end fw-bold">{{ item.totalItemPrice ? formatNumber(item.totalItemPrice) : '—' }}</td>
                  </tr>
                </tbody>
                <tfoot v-if="selectedOrder.totalPrice">
                  <tr class="table-success fw-bold">
                    <td colspan="4" class="text-end">Tổng thanh toán:</td>
                    <td class="text-end">{{ formatNumber(selectedOrder.totalPrice) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button
              v-if="selectedOrder.status === 'AWAITING_PAYMENT'"
              @click="confirmPaymentFromModal"
              class="btn btn-success"
            >
              <i class="bi bi-check-circle me-1"></i>Xác nhận đã thanh toán
            </button>
            <button
              v-if="selectedOrder.status === 'DEPOSITED'"
              @click="startPreparationFromModal"
              class="btn btn-warning"
            >
              <i class="bi bi-box-seam me-1"></i>Bắt đầu chuẩn bị
            </button>
            <button
              v-if="selectedOrder.status === 'PROCESSING'"
              @click="markDeliveredFromModal"
              class="btn btn-primary"
            >
              <i class="bi bi-truck me-1"></i>Đánh dấu đã giao
            </button>
            <button
              v-if="canAdminCancelOrder(selectedOrder)"
              @click="cancelOrderFromModal"
              class="btn btn-outline-danger"
            >
              <i class="bi bi-slash-circle me-1"></i>Hủy đơn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Delay Delivery Modal -->
    <div class="modal fade" id="delayModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg">
          <div class="modal-header bg-warning text-dark">
            <h5 class="modal-title fw-bold">
              <i class="bi bi-calendar-x me-2"></i>Báo Trễ Hẹn
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-4">
            <div v-if="selectedOrder" class="mb-4">
              <p class="mb-2"><strong>Đơn hàng:</strong> {{ selectedOrder.orderNumber }}</p>
              <p class="mb-3"><strong>Ngày giao hiện tại:</strong> {{ formatDate(selectedOrder.deliveryDate) }}</p>
              
              <form @submit.prevent="submitDelayDelivery">
                <div class="mb-3">
                  <label class="form-label fw-bold">Ngày giao mới <span class="text-danger">*</span></label>
                  <input 
                    v-model="delayDeliveryForm.newDate" 
                    type="date" 
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-4">
                  <label class="form-label fw-bold">Lý do trễ hẹn <span class="text-danger">*</span></label>
                  <textarea 
                    v-model="delayDeliveryForm.reason" 
                    class="form-control"
                    rows="3"
                    placeholder="Nhập lý do..."
                    required
                  ></textarea>
                </div>
                <div class="d-flex gap-2">
                  <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                  <button type="submit" class="btn btn-warning text-dark fw-bold" :disabled="submittingDelay">
                    <span v-if="!submittingDelay"><i class="bi bi-check-lg me-1"></i>Xác nhận</span>
                    <span v-else><i class="bi bi-hourglass-split me-1"></i>Đang xử lý...</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'
import { Modal } from 'bootstrap'
import { getOrderStatusLabel } from '../../constants/orderStatus'

const orders = ref([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const selectedOrder = ref(null)
const detailModalRef = ref(null)
const searchKeyword = ref('')
const filterStatus = ref('')
const dateFrom = ref('')
const dateTo = ref('')
const delayDeliveryForm = ref({ newDate: '', reason: '' })
const submittingDelay = ref(false)
let bsModal = null
let bsDelayModal = null

onMounted(() => loadOrders())

const loadOrders = async (page = 0) => {
  isLoading.value = true
  try {
    const params = { page, size: 15, orderType: 'READY_MADE' }
    if (searchKeyword.value?.trim()) params.keyword = searchKeyword.value.trim()
    if (filterStatus.value) params.status = filterStatus.value
    if (dateFrom.value) params.dateFrom = dateFrom.value
    if (dateTo.value) params.dateTo = dateTo.value

    const response = await apiClient.get('/orders', { params })
    const data = response.data.content || response.data
    orders.value = Array.isArray(data) ? data : []
    currentPage.value = response.data.number || 0
    totalPages.value = response.data.totalPages || 1
  } catch (error) {
    console.error('Failed to load product orders:', error)
    Swal.fire('Lỗi', 'Không thể tải danh sách đơn hàng', 'error')
  } finally {
    isLoading.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  dateFrom.value = ''
  dateTo.value = ''
  loadOrders(0)
}

const onDateFilterChanged = async () => {
  if (dateFrom.value && dateTo.value && dateFrom.value > dateTo.value) {
    await Swal.fire('Khoảng ngày không hợp lệ', 'Từ ngày phải nhỏ hơn hoặc bằng Đến ngày', 'warning')
    return
  }
  await loadOrders(0)
}

const openDetail = async (order) => {
  try {
    const response = await apiClient.get(`/orders/${order.id}`)
    selectedOrder.value = response.data
    await nextTick()
    if (!bsModal && detailModalRef.value) {
      bsModal = new Modal(detailModalRef.value)
    }
    bsModal?.show()
  } catch (error) {
    Swal.fire('Lỗi', 'Không thể tải chi tiết đơn hàng', 'error')
  }
}

// Status transition helpers
const updateStatus = async (orderId, newStatus, successMsg) => {
  try {
    await apiClient.put(`/orders/${orderId}/status`, null, { params: { status: newStatus } })
    await loadOrders(currentPage.value)
    Swal.fire({ icon: 'success', title: successMsg, timer: 2000, showConfirmButton: false })
    return true
  } catch (error) {
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật trạng thái', 'error')
    return false
  }
}

const canAdminCancelOrder = (order) => {
  if (!order) return false
  return order.status === 'AWAITING_PAYMENT'
    || order.status === 'DEPOSITED'
    || order.status === 'PROCESSING'
}

const cancelOrderByAdmin = async (order) => {
  if (!canAdminCancelOrder(order)) return

  const result = await Swal.fire({
    title: 'Hủy đơn hàng?',
    text: `Đơn ${order.orderNumber} sẽ chuyển sang trạng thái "Đã hủy".`,
    input: 'textarea',
    inputLabel: 'Lý do hủy đơn',
    inputPlaceholder: 'Nhập lý do để gửi cho khách hàng...',
    inputAttributes: {
      maxlength: '500',
      'aria-label': 'Lý do hủy đơn bởi admin',
    },
    inputValidator: (value) => {
      if (!value || !value.trim()) {
        return 'Vui lòng nhập lý do hủy đơn'
      }
      return null
    },
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Hủy đơn',
    cancelButtonText: 'Đóng',
    confirmButtonColor: '#dc2626',
  })

  if (!result.isConfirmed) return

  try {
    const reason = (result.value || '').trim()
    await apiClient.put(`/orders/${order.id}/status`, { reason }, {
      params: { status: 'CANCELLED' },
    })
    await loadOrders(currentPage.value)
    Swal.fire({ icon: 'success', title: 'Đã hủy đơn', timer: 2000, showConfirmButton: false })
    bsModal?.hide()
  } catch (error) {
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể hủy đơn hàng', 'error')
  }
}

const confirmPayment = async (order) => {
  const result = await Swal.fire({
    title: 'Xác nhận đã thanh toán?',
    text: `Đơn ${order.orderNumber} sẽ chuyển sang trạng thái "Đã thanh toán".`,
    icon: 'question', showCancelButton: true,
    confirmButtonText: 'Xác nhận', cancelButtonText: 'Hủy',
    confirmButtonColor: '#16a34a',
  })
  if (result.isConfirmed) updateStatus(order.id, 'DEPOSITED', 'Đã xác nhận thanh toán!')
}

const startPreparation = async (order) => {
  const result = await Swal.fire({
    title: 'Bắt đầu chuẩn bị đơn?',
    text: `Đơn ${order.orderNumber} sẽ chuyển sang trạng thái "Đang chuẩn bị".`,
    icon: 'question', showCancelButton: true,
    confirmButtonText: 'Bắt đầu', cancelButtonText: 'Hủy',
    confirmButtonColor: '#2563eb',
  })
  if (result.isConfirmed) updateStatus(order.id, 'PROCESSING', 'Đã bắt đầu chuẩn bị đơn hàng!')
}

const markDelivered = async (order) => {
  const result = await Swal.fire({
    title: 'Đánh dấu đã giao hàng?',
    text: `Đơn ${order.orderNumber} sẽ chuyển sang "Đã nhận hàng" (hoàn thành).`,
    icon: 'question', showCancelButton: true,
    confirmButtonText: 'Đã giao', cancelButtonText: 'Hủy',
    confirmButtonColor: '#7c3aed',
  })
  if (result.isConfirmed) updateStatus(order.id, 'COMPLETED', 'Đã đánh dấu giao hàng thành công!')
}

// Modal action helpers
const confirmPaymentFromModal = async () => {
  const ok = await updateStatus(selectedOrder.value.id, 'DEPOSITED', 'Đã xác nhận thanh toán!')
  if (ok) bsModal?.hide()
}

const startPreparationFromModal = async () => {
  const ok = await updateStatus(selectedOrder.value.id, 'PROCESSING', 'Đã bắt đầu chuẩn bị!')
  if (ok) bsModal?.hide()
}

const markDeliveredFromModal = async () => {
  const ok = await updateStatus(selectedOrder.value.id, 'COMPLETED', 'Đã giao hàng thành công!')
  if (ok) bsModal?.hide()
}

const cancelOrderFromModal = async () => {
  if (!selectedOrder.value) return
  await cancelOrderByAdmin(selectedOrder.value)
}

// Helper: Check if order is fully paid
const isFullyPaid = (order) => {
  if (!order || !order.totalPrice || !order.depositAmount) return false
  return order.depositAmount >= order.totalPrice
}

// Delay delivery
const openDelayModal = async (order) => {
  selectedOrder.value = order
  delayDeliveryForm.value = { 
    newDate: '', 
    reason: '' 
  }
  await nextTick()
  if (!bsDelayModal) {
    bsDelayModal = new Modal(document.getElementById('delayModal'))
  }
  bsDelayModal?.show()
}

const submitDelayDelivery = async () => {
  if (!delayDeliveryForm.value.newDate || !delayDeliveryForm.value.reason) {
    Swal.fire('Lỗi', 'Vui lòng điền đầy đủ thông tin', 'warning')
    return
  }

  submittingDelay.value = true
  try {
    await apiClient.put(`/orders/${selectedOrder.value.id}/delay-delivery`, {
      newDeliveryDate: delayDeliveryForm.value.newDate,
      reason: delayDeliveryForm.value.reason
    })
    bsDelayModal?.hide()
    await loadOrders(currentPage.value)
    Swal.fire('Thành công', 'Đã báo trễ hẹn vàgửi email thông báo cho khách hàng', 'success')
  } catch (error) {
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật ngày giao', 'error')
  } finally {
    submittingDelay.value = false
  }
}

// Ship order
const shipOrder = async (order) => {
  const result = await Swal.fire({
    title: 'Giao hàng để ĐVVC?',
    text: `Đơn ${order.orderNumber} sẽ chuyển sang "Đang giao hàng".`,
    icon: 'question', showCancelButton: true,
    confirmButtonText: 'Xác nhận', cancelButtonText: 'Hủy',
    confirmButtonColor: '#2563eb',
  })
  if (result.isConfirmed) {
    try {
      await apiClient.put(`/orders/${order.id}/ship`)
      await loadOrders(currentPage.value)
      Swal.fire({ icon: 'success', title: 'Đã giao cho ĐVVC!', timer: 2000, showConfirmButton: false })
    } catch (error) {
      Swal.fire('Lỗi', error.response?.data?.error || 'Không thể giao hàng', 'error')
    }
  }
}

// Complete order
const completeOrder = async (order) => {
  const result = await Swal.fire({
    title: 'Xác nhận đã giao thành công?',
    text: `Đơn ${order.orderNumber} sẽ chuyển sang "Hoàn thành".`,
    icon: 'question', showCancelButton: true,
    confirmButtonText: 'Xác nhận', cancelButtonText: 'Hủy',
    confirmButtonColor: '#16a34a',
  })
  if (result.isConfirmed) {
    try {
      await apiClient.put(`/orders/${order.id}/complete`)
      await loadOrders(currentPage.value)
      Swal.fire({ icon: 'success', title: 'Hoàn thành đơn hàng!', timer: 2000, showConfirmButton: false })
    } catch (error) {
      Swal.fire('Lỗi', error.response?.data?.error || 'Không thể hoàn thành đơn', 'error')
    }
  }
}

// Formatters
const getStatusText = (status) => {
  return getOrderStatusLabel(status, {
    DEPOSITED: 'Đã thanh toán',
    PROCESSING: 'Đang chuẩn bị',
    COMPLETED: 'Đã giao hàng',
  })
}

const getStatusBadgeClass = (status) => {
  const map = {
    AWAITING_PAYMENT: 'badge bg-info-subtle text-info',
    DEPOSITED:        'badge bg-success-subtle text-success',
    PROCESSING:       'badge bg-primary-subtle text-primary',
    AWAITING_DELIVERY: 'badge bg-info-subtle text-info',
    SHIPPING:         'badge bg-primary-subtle text-primary',
    COMPLETED:        'badge bg-success-subtle text-success',
    CANCELLED:        'badge bg-danger-subtle text-danger',
  }
  return map[status] || 'badge bg-secondary'
}

const formatDate = (d) => {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const formatNumber = (n) => n ? new Intl.NumberFormat('vi-VN').format(n) : '—'

const formatCurrency = (n) => n
  ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n)
  : '—'
</script>
