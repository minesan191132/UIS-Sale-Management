<template>
  <div class="catalog-order-page p-4 min-vh-100 d-flex flex-column" style="background-color: #f8f9fa;">
    
    <div class="d-flex flex-column flex-lg-row justify-content-between align-items-lg-end mb-4 pb-2 border-bottom">
      <div class="mb-3 mb-lg-0">
        <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">
          <i class="bi bi-cart-check-fill me-2 text-navy"></i> Quản lý Đơn Hàng
        </h2>
        <p class="text-muted small mb-0">Theo dõi catalog, xác nhận thanh toán và điều phối giao hàng.</p>
      </div>
      
      <div class="d-flex gap-2 flex-wrap stat-pill-group">
        <div class="stat-pill text-primary border-primary">
          <i class="bi bi-layers-fill"></i> Tổng: <b class="fs-6 ms-1">{{ orders.length }}</b>
        </div>
        <div class="stat-pill text-warning border-warning" style="color: #d97706 !important;">
          <i class="bi bi-hourglass-split"></i> Chờ thanh toán: <b class="fs-6 ms-1">{{ catalogSummary.awaitingPayment }}</b>
        </div>
        <div class="stat-pill text-navy border-navy">
          <i class="bi bi-box-seam-fill"></i> Đang chuẩn bị: <b class="fs-6 ms-1">{{ catalogSummary.processing }}</b>
        </div>
        <div class="stat-pill text-info border-info" style="color: #0284c7 !important;">
          <i class="bi bi-truck"></i> Đang giao: <b class="fs-6 ms-1">{{ catalogSummary.shipping }}</b>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm p-4 mb-4 rounded-4 bg-white">
      <div class="row g-3 align-items-end">
        <div class="col-xl-3 col-md-6">
          <label class="small fw-bolder text-muted mb-2 text-uppercase" style="letter-spacing: 0.5px;">Tìm kiếm</label>
          <div class="position-relative">
            <i class="bi bi-search position-absolute text-muted" style="top: 50%; left: 15px; transform: translateY(-50%);"></i>
            <input type="text" class="form-control custom-input ps-5" placeholder="Mã đơn, khách hàng..." v-model="searchKeyword" @keyup.enter="loadOrders(0)">
          </div>
        </div>
        <div class="col-xl-2 col-md-6">
          <label class="small fw-bolder text-muted mb-2 text-uppercase" style="letter-spacing: 0.5px;">Trạng thái</label>
          <select class="form-select custom-input fw-medium text-dark" v-model="filterStatus" @change="loadOrders(0)">
            <option value="">Tất cả</option>
            <option value="AWAITING_PAYMENT">Chờ thanh toán</option>
            <option value="DEPOSITED">Đã thanh toán</option>
            <option value="PROCESSING">Đang chuẩn bị</option>
            <option value="AWAITING_DELIVERY">Chờ giao hàng</option>
            <option value="SHIPPING">Đang giao</option>
            <option value="COMPLETED">Đã nhận hàng</option>
            <option value="CANCELLED">Đã hủy</option>
          </select>
        </div>
        <div class="col-xl-2 col-md-4">
          <label class="small fw-bolder text-muted mb-2 text-uppercase" style="letter-spacing: 0.5px;">Từ ngày</label>
          <input type="date" class="form-control custom-input fw-medium text-secondary" v-model="dateFrom" @change="onDateFilterChanged">
        </div>
        <div class="col-xl-2 col-md-4">
          <label class="small fw-bolder text-muted mb-2 text-uppercase" style="letter-spacing: 0.5px;">Đến ngày</label>
          <input type="date" class="form-control custom-input fw-medium text-secondary" v-model="dateTo" @change="onDateFilterChanged">
        </div>
        <div class="col-xl-3 col-md-4 d-flex gap-2 justify-content-md-end mt-4 mt-xl-0">
          <button class="btn btn-light border rounded-pill px-4 fw-bold hover-lift" @click="resetFilters">Đặt lại</button>
          <button class="btn btn-navy rounded-pill px-4 fw-bold shadow-sm hover-lift flex-grow-1" @click="loadOrders(0)">
            <i class="bi bi-funnel-fill me-1"></i> Lọc
          </button>
        </div>
      </div>
    </div>

    <div v-if="isLoading" class="text-center py-5 flex-grow-1 d-flex flex-column justify-content-center">
      <div class="spinner-grow text-primary mx-auto" role="status" style="width: 3rem; height: 3rem;"></div>
      <p class="text-muted mt-3 fw-bold">Đang tải danh sách đơn hàng...</p>
    </div>

    <div v-else-if="orders.length === 0" class="card border-0 shadow-sm rounded-4 flex-grow-1">
      <div class="card-body text-center py-5 d-flex flex-column align-items-center justify-content-center">
        <div class="rounded-circle bg-light d-flex align-items-center justify-content-center mb-3" style="width: 80px; height: 80px;">
          <i class="bi bi-cart-x fs-1 text-muted opacity-50"></i>
        </div>
        <h5 class="fw-bold text-dark">Chưa có đơn hàng nào</h5>
        <p class="text-muted">Không tìm thấy đơn hàng sản phẩm khớp với bộ lọc của bạn.</p>
      </div>
    </div>

    <div v-else class="table-responsive flex-grow-1 pb-4 px-1">
      <table class="table modern-table align-middle mb-0 w-100">
        <thead class="bg-light">
          <tr>
            <th class="ps-4 py-3 text-muted fw-bold text-uppercase" style="font-size: 0.8rem; width: 180px;">Mã đơn</th>
            <th class="py-3 text-muted fw-bold text-uppercase" style="font-size: 0.8rem;">Khách hàng</th>
            <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem; width: 120px;">Sản phẩm</th>
            <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem; width: 180px;">Trạng thái</th>
            <th class="py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 150px;">Tổng tiền</th>
            <th class="py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 130px;">Ngày tạo</th>
            <th class="pe-4 py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 220px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id" class="shadow-sm bg-white hover-lift border">
            <td class="ps-4 py-3">
              <span class="badge bg-light text-navy border px-2 py-1 fw-bold font-monospace fs-6 shadow-sm">
                {{ order.orderNumber }}
              </span>
            </td>
            
            <td class="py-3">
              <h6 class="mb-0 fw-bold text-dark fs-6">{{ order.userName }}</h6>
              <span class="text-muted small fw-medium"><i class="bi bi-buildings me-1"></i>{{ order.companyName }}</span>
            </td>
            
            <td class="py-3 text-center">
              <span class="badge bg-light border border-secondary border-opacity-25 text-secondary px-3 py-2 fw-bold rounded-pill">
                {{ order.items?.length || 0 }} SP
              </span>
            </td>
            
            <td class="py-3 text-center">
              <div class="d-flex align-items-center justify-content-center gap-2">
                <span class="glowing-dot" :class="getStatusDotClass(order.status)"></span>
                <span class="badge rounded-pill px-3 py-2 fw-bold" :class="getStatusBadgeClass(order.status)" style="font-size: 0.75rem;">
                  {{ getStatusText(order.status) }}
                </span>
              </div>
            </td>
            
            <td class="py-3 text-end fw-bolder text-dark fs-5">
              {{ formatCurrency(order.totalPrice) }}
            </td>
            
            <td class="py-3 text-end text-secondary fw-medium small">
              {{ formatDate(order.createdAt) }}
            </td>
            
            <td class="pe-4 py-3 text-end">
              <div class="d-flex flex-wrap gap-2 justify-content-end action-group-wrap">
                <button @click="openDetail(order)" class="btn btn-action-circle bg-light text-primary border" title="Xem chi tiết">
                  <i class="bi bi-eye-fill"></i>
                </button>
                <button v-if="canAdminCancelOrder(order)" @click="cancelOrderByAdmin(order)" class="btn btn-action-circle bg-light text-danger border" title="Hủy đơn">
                  <i class="bi bi-slash-circle"></i>
                </button>
                <button v-if="order.status === 'AWAITING_PAYMENT'" @click="confirmPayment(order)" class="btn btn-action-circle bg-light text-success border" title="Xác nhận thanh toán">
                  <i class="bi bi-currency-dollar"></i>
                </button>
                <button v-if="order.status === 'DEPOSITED'" @click="startPreparation(order)" class="btn btn-action-circle bg-light text-navy border" title="Bắt đầu chuẩn bị">
                  <i class="bi bi-box-seam-fill"></i>
                </button>
                <button v-if="order.status === 'PROCESSING'" @click="openReadyToDeliverModal(order)" class="btn btn-action-circle bg-light text-info border" title="Sẵn sàng giao (nhập ngày)">
                  <i class="bi bi-calendar-check-fill"></i>
                </button>
                <button v-if="order.status === 'AWAITING_DELIVERY' && order.deliveryDate" @click="openDelayModal(order)" class="btn btn-action-circle bg-light text-warning border" title="Báo trễ hẹn">
                  <i class="bi bi-calendar-x-fill"></i>
                </button>
                <button v-if="order.status === 'AWAITING_DELIVERY'" @click="shipOrder(order)" class="btn btn-action-circle bg-light text-primary border" title="Giao cho ĐVVC">
                  <i class="bi bi-truck"></i>
                </button>
                <button v-if="order.status === 'SHIPPING'" @click="completeOrder(order)" class="btn btn-action-circle bg-success text-white border-0 shadow-sm" title="Xác nhận đã giao">
                  <i class="bi bi-check-lg"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPages > 1" class="d-flex justify-content-between align-items-center mt-2 pb-4 px-2">
      <span class="text-muted fw-medium small">Trang <b class="text-dark fs-6">{{ currentPage + 1 }}</b> / {{ totalPages }}</span>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-white shadow-sm border rounded-pill px-4 py-2 d-flex align-items-center hover-lift fw-bold" :disabled="currentPage === 0" @click="loadOrders(currentPage - 1)">
          <i class="bi bi-chevron-left me-1"></i> Trước
        </button>
        <button class="btn btn-white shadow-sm border rounded-pill px-4 py-2 d-flex align-items-center hover-lift fw-bold" :disabled="currentPage >= totalPages - 1" @click="loadOrders(currentPage + 1)">
          Sau <i class="bi bi-chevron-right ms-1"></i>
        </button>
      </div>
    </div>

    <div class="modal fade" id="productOrderDetailModal" tabindex="-1" ref="detailModalRef">
      <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden bg-white">
          
          <div class="modal-header bg-white px-4 py-3 border-bottom border-light">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center" style="font-size: 1.1rem;">
              <i class="bi bi-receipt-cutoff me-2 text-navy fs-5"></i>
              Chi tiết đơn hàng — <span class="text-navy ms-1 font-monospace">{{ selectedOrder?.orderNumber }}</span>
            </h5>
            <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal"></button>
          </div>
          
          <div class="modal-body px-4 pb-4 pt-4" v-if="selectedOrder">
            <div class="row g-4 mb-4">
              <div class="col-md-4">
                <div class="p-3 border rounded-4 h-100 bg-white shadow-sm hover-lift">
                  <p class="mb-2 text-muted small fw-bold text-uppercase" style="letter-spacing: 0.5px;">Khách hàng</p>
                  <h6 class="fw-bold text-dark mb-1 fs-6">{{ selectedOrder.userName }}</h6>
                  <div class="small text-secondary mt-2" style="line-height: 1.4;">
                    <i class="bi bi-buildings text-muted me-1"></i>{{ selectedOrder.companyName }}
                  </div>
                </div>
              </div>

              <div class="col-md-4">
                <div class="p-3 border rounded-4 h-100 bg-white shadow-sm hover-lift">
                  <p class="mb-2 text-muted small fw-bold text-uppercase" style="letter-spacing: 0.5px;">Trạng thái & Thời gian</p>
                  <div class="mb-2 d-flex align-items-center gap-2 mt-1">
                    <span class="badge rounded-pill px-3 py-2 text-start" :class="getStatusBadgeClass(selectedOrder.status)">
                      {{ getStatusText(selectedOrder.status) }}
                    </span>
                  </div>
                  <div class="d-flex align-items-center gap-2 mt-2 text-dark small fw-medium">
                    <i class="bi bi-clock text-muted"></i> Ngày tạo: {{ formatDate(selectedOrder.createdAt) }}
                  </div>
                </div>
              </div>

              <div class="col-md-4">
                <div class="p-3 border rounded-4 h-100 shadow-sm hover-lift" style="background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);">
                  <p class="mb-1 text-muted small fw-bold text-uppercase" style="letter-spacing: 0.5px;">Tổng thanh toán</p>
                  <h3 class="fw-bolder text-danger mt-2 mb-0">{{ formatCurrency(selectedOrder.totalPrice) }}</h3>
                </div>
              </div>
            </div>

            <div class="alert bg-light border border-secondary border-opacity-25 rounded-4 p-3 mb-4 d-flex gap-3 shadow-sm" v-if="selectedOrder.notes">
              <i class="bi bi-truck text-navy fs-3 mt-1"></i>
              <div class="flex-grow-1">
                <h6 class="fw-bold text-navy mb-2 small text-uppercase">Thông tin Giao hàng & Ghi chú</h6>
                <p class="mb-0 text-dark fw-medium" style="font-size: 0.95rem; line-height: 1.6; white-space: pre-wrap;">{{ selectedOrder.notes }}</p>
              </div>
            </div>

            <div class="border rounded-4 overflow-hidden shadow-sm">
              <table class="table table-hover align-middle mb-0 custom-detail-table">
                <thead class="bg-light">
                  <tr>
                    <th class="ps-4 text-muted small fw-bold text-uppercase py-3 border-bottom">STT</th>
                    <th class="text-muted small fw-bold text-uppercase py-3 border-bottom">Tên sản phẩm</th>
                    <th class="text-center text-muted small fw-bold text-uppercase py-3 border-bottom">Số lượng</th>
                    <th class="text-end text-muted small fw-bold text-uppercase py-3 border-bottom">Đơn giá</th>
                    <th class="text-end pe-4 text-muted small fw-bold text-uppercase py-3 border-bottom">Thành tiền</th>
                  </tr>
                </thead>
                <tbody class="bg-white">
                  <tr v-for="(item, idx) in selectedOrder.items" :key="item.id">
                    <td class="ps-4 fw-medium text-muted py-3">{{ idx + 1 }}</td>
                    <td class="fw-bold text-dark py-3">{{ item.itemName }}</td>
                    <td class="text-center py-3">
                      <span class="badge bg-light text-dark border px-3 py-2 fs-6 shadow-sm rounded-pill">{{ item.quantity }}</span>
                    </td>
                    <td class="text-end text-secondary fw-medium py-3">{{ item.unitPrice ? formatNumber(item.unitPrice) : '—' }}</td>
                    <td class="text-end pe-4 fw-bold text-dark py-3">{{ item.totalItemPrice ? formatNumber(item.totalItemPrice) : '—' }}</td>
                  </tr>
                </tbody>
                <tfoot v-if="selectedOrder.totalPrice" class="bg-light">
                  <tr>
                    <td colspan="4" class="text-end py-3 fw-bold text-muted text-uppercase fs-6 border-top">Tổng cộng:</td>
                    <td class="text-end pe-4 py-3 fw-bolder text-danger fs-5 border-top">{{ formatCurrency(selectedOrder.totalPrice) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
          
          <div class="modal-footer border-top px-4 py-3 bg-white justify-content-end"
               v-if="canAdminCancelOrder(selectedOrder) || selectedOrder?.status === 'AWAITING_PAYMENT' || selectedOrder?.status === 'DEPOSITED' || selectedOrder?.status === 'PROCESSING' || selectedOrder?.status === 'AWAITING_DELIVERY' || selectedOrder?.status === 'SHIPPING'">
            <div class="d-flex gap-2">
              <button v-if="canAdminCancelOrder(selectedOrder)" @click="cancelOrderFromModal" class="btn btn-outline-danger rounded-pill px-4 fw-bold hover-lift"><i class="bi bi-slash-circle me-1"></i>Hủy đơn</button>
              <button v-if="selectedOrder?.status === 'AWAITING_PAYMENT'" @click="confirmPaymentFromModal" class="btn btn-success rounded-pill px-4 fw-bold shadow-sm hover-lift"><i class="bi bi-currency-dollar me-1"></i>Xác nhận thanh toán</button>
              <button v-if="selectedOrder?.status === 'DEPOSITED'" @click="startPreparationFromModal" class="btn btn-navy rounded-pill px-4 fw-bold shadow-sm hover-lift"><i class="bi bi-box-seam me-1"></i>Bắt đầu chuẩn bị</button>
              <button v-if="selectedOrder?.status === 'PROCESSING'" @click="openReadyToDeliverFromModal" class="btn btn-info text-white rounded-pill px-4 fw-bold shadow-sm hover-lift"><i class="bi bi-calendar-check me-1"></i>Sẵn sàng giao</button>
              <button v-if="selectedOrder?.status === 'AWAITING_DELIVERY'" @click="shipOrderFromModal" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm hover-lift"><i class="bi bi-truck me-1"></i>Giao cho ĐVVC</button>
              <button v-if="selectedOrder?.status === 'SHIPPING'" @click="completeOrderFromModal" class="btn btn-success rounded-pill px-4 fw-bold shadow-sm hover-lift"><i class="bi bi-check-lg me-1"></i>Đã giao thành công</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="delayModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
          <div class="modal-header bg-warning px-4 py-3 border-0">
            <h5 class="modal-title fw-bolder text-dark"><i class="bi bi-calendar-x me-2"></i>Báo Trễ Hẹn Giao Hàng</h5>
            <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-4 bg-white" v-if="selectedOrder">
            <div class="bg-light p-3 rounded-3 mb-4 border">
              <p class="mb-1 text-secondary small">Mã đơn hàng: <b class="text-dark">{{ selectedOrder.orderNumber }}</b></p>
              <p class="mb-0 text-secondary small">Ngày giao dự kiến cũ: <b class="text-danger">{{ formatDate(selectedOrder.deliveryDate) }}</b></p>
            </div>
            
            <form @submit.prevent="submitDelayDelivery">
              <div class="mb-3">
                <label class="form-label fw-bold text-dark small text-uppercase">Ngày giao mới <span class="text-danger">*</span></label>
                <input v-model="delayDeliveryForm.newDate" type="date" class="form-control custom-input" required />
              </div>
              <div class="mb-4">
                <label class="form-label fw-bold text-dark small text-uppercase">Lý do trễ hẹn <span class="text-danger">*</span></label>
                <textarea v-model="delayDeliveryForm.reason" class="form-control custom-input" rows="3" placeholder="Nhập lý do chi tiết..." required></textarea>
              </div>
              <div class="d-flex gap-2 justify-content-end pt-3 border-top">
                <button type="button" class="btn btn-light border rounded-pill px-4 fw-bold" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-warning text-dark fw-bold rounded-pill px-4 shadow-sm" :disabled="submittingDelay">
                  <span v-if="!submittingDelay"><i class="bi bi-envelope-paper me-1"></i>Gửi thông báo</span>
                  <span v-else class="spinner-border spinner-border-sm"></span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="readyToDeliverModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
          <div class="modal-header bg-info text-white px-4 py-3 border-0">
            <h5 class="modal-title fw-bolder"><i class="bi bi-calendar-check me-2"></i>Sẵn Sàng Giao Hàng</h5>
            <button type="button" class="btn-close btn-close-white shadow-none" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-4 bg-white" v-if="selectedOrder">
            <div class="bg-light p-3 rounded-3 mb-4 border">
              <p class="mb-1 text-secondary small">Mã đơn hàng: <b class="text-dark">{{ selectedOrder.orderNumber }}</b></p>
              <p class="mb-0 text-secondary small">Tổng giá trị: <b class="text-danger">{{ formatCurrency(selectedOrder.totalPrice) }}</b></p>
            </div>

            <form @submit.prevent="submitReadyToDeliver">
              <div class="mb-4">
                <label class="form-label fw-bold text-dark small text-uppercase">
                  Ngày giao hàng dự kiến <span class="text-danger">*</span>
                </label>
                <input v-model="readyToDeliverDate" type="date" class="form-control custom-input" required />
                <div class="form-text">Ngày giao sẽ hiển thị cho khách hàng</div>
              </div>
              <div class="d-flex gap-2 justify-content-end pt-3 border-top">
                <button type="button" class="btn btn-light border rounded-pill px-4 fw-bold" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-info text-white fw-bold rounded-pill px-4 shadow-sm" :disabled="submittingReadyToDeliver">
                  <span v-if="!submittingReadyToDeliver"><i class="bi bi-check-lg me-1"></i>Xác nhận sẵn sàng giao</span>
                  <span v-else class="spinner-border spinner-border-sm"></span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'
import { Modal } from 'bootstrap'
import { getOrderStatusLabel } from '../../constants/orderStatus'
import { useSseOrderUpdates } from '../../services/useSseOrderUpdates'

// KEEPS ALL YOUR ORIGINAL SCRIPT LOGIC EXACTLY INTACT
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
const readyToDeliverDate = ref('')
const submittingReadyToDeliver = ref(false)
let bsModal = null
let bsDelayModal = null
let bsReadyToDeliverModal = null

const catalogSummary = computed(() => {
  const base = { awaitingPayment: 0, processing: 0, shipping: 0 }
  for (const order of orders.value) {
    if (order.status === 'AWAITING_PAYMENT') base.awaitingPayment += 1
    if (order.status === 'PROCESSING') base.processing += 1
    if (order.status === 'SHIPPING') base.shipping += 1
  }
  return base
})

// ── Real-time SSE: auto-refresh when backend pushes ORDER_UPDATED ──
const { connected: sseConnected } = useSseOrderUpdates(() => {
  loadOrders(currentPage.value)
})

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
    if (!bsModal && detailModalRef.value) { bsModal = new Modal(detailModalRef.value) }
    bsModal?.show()
  } catch (error) { Swal.fire('Lỗi', 'Không thể tải chi tiết đơn hàng', 'error') }
}

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
  return order.status === 'AWAITING_PAYMENT' || order.status === 'DEPOSITED' || order.status === 'PROCESSING'
}

const cancelOrderByAdmin = async (order) => {
  if (!canAdminCancelOrder(order)) return
  const result = await Swal.fire({
    title: 'Hủy đơn hàng?', text: `Đơn ${order.orderNumber} sẽ chuyển sang trạng thái "Đã hủy".`,
    input: 'textarea', inputLabel: 'Lý do hủy đơn', inputPlaceholder: 'Nhập lý do để gửi cho khách hàng...',
    inputAttributes: { maxlength: '500', 'aria-label': 'Lý do hủy đơn bởi admin' },
    inputValidator: (value) => { return (!value || !value.trim()) ? 'Vui lòng nhập lý do hủy đơn' : null },
    icon: 'warning', showCancelButton: true, confirmButtonText: 'Hủy đơn', cancelButtonText: 'Đóng', confirmButtonColor: '#dc2626',
  })
  if (!result.isConfirmed) return
  try {
    const reason = (result.value || '').trim()
    await apiClient.put(`/orders/${order.id}/status`, { reason }, { params: { status: 'CANCELLED' } })
    await loadOrders(currentPage.value)
    Swal.fire({ icon: 'success', title: 'Đã hủy đơn', timer: 2000, showConfirmButton: false })
    bsModal?.hide()
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể hủy đơn hàng', 'error') }
}

const confirmPayment = async (order) => {
  const result = await Swal.fire({ title: 'Xác nhận thanh toán?', text: `Đơn ${order.orderNumber} -> Đã thanh toán.`, icon: 'question', showCancelButton: true, confirmButtonText: 'Xác nhận', confirmButtonColor: '#10b981' })
  if (result.isConfirmed) updateStatus(order.id, 'DEPOSITED', 'Đã xác nhận thanh toán!')
}

const startPreparation = async (order) => {
  const result = await Swal.fire({ title: 'Bắt đầu chuẩn bị?', text: `Đơn ${order.orderNumber} -> Đang chuẩn bị.`, icon: 'question', showCancelButton: true, confirmButtonText: 'Bắt đầu', confirmButtonColor: '#0b2e59' })
  if (result.isConfirmed) updateStatus(order.id, 'PROCESSING', 'Đã bắt đầu chuẩn bị đơn!')
}

const confirmPaymentFromModal = async () => { const ok = await updateStatus(selectedOrder.value.id, 'DEPOSITED', 'Đã xác nhận thanh toán!'); if (ok) bsModal?.hide() }
const startPreparationFromModal = async () => { const ok = await updateStatus(selectedOrder.value.id, 'PROCESSING', 'Đã bắt đầu chuẩn bị!'); if (ok) bsModal?.hide() }
const cancelOrderFromModal = async () => { if (!selectedOrder.value) return; await cancelOrderByAdmin(selectedOrder.value) }

const openReadyToDeliverModal = async (order) => {
  selectedOrder.value = order
  readyToDeliverDate.value = ''
  await nextTick()
  if (!bsReadyToDeliverModal) {
    bsReadyToDeliverModal = new Modal(document.getElementById('readyToDeliverModal'))
  }
  bsReadyToDeliverModal?.show()
}

const submitReadyToDeliver = async () => {
  if (!readyToDeliverDate.value) {
    Swal.fire('Lỗi', 'Vui lòng chọn ngày giao hàng', 'warning')
    return
  }

  submittingReadyToDeliver.value = true
  try {
    await apiClient.put(`/orders/${selectedOrder.value.id}/ready-to-deliver`, {
      deliveryDate: readyToDeliverDate.value,
    })
    bsReadyToDeliverModal?.hide()
    await loadOrders(currentPage.value)
    Swal.fire({
      icon: 'success',
      title: 'Đã cập nhật!',
      text: 'Đơn hàng chuyển sang trạng thái chờ giao hàng.',
      timer: 2500,
      showConfirmButton: false,
    })
  } catch (error) {
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật trạng thái', 'error')
  } finally {
    submittingReadyToDeliver.value = false
  }
}

const openReadyToDeliverFromModal = async () => {
  if (!selectedOrder.value) return
  bsModal?.hide()
  await nextTick()
  await openReadyToDeliverModal(selectedOrder.value)
}

const openDelayModal = async (order) => {
  selectedOrder.value = order
  delayDeliveryForm.value = { newDate: '', reason: '' }
  await nextTick()
  if (!bsDelayModal) { bsDelayModal = new Modal(document.getElementById('delayModal')) }
  bsDelayModal?.show()
}

const submitDelayDelivery = async () => {
  if (!delayDeliveryForm.value.newDate || !delayDeliveryForm.value.reason) { Swal.fire('Lỗi', 'Vui lòng điền đầy đủ', 'warning'); return }
  submittingDelay.value = true
  try {
    await apiClient.put(`/orders/${selectedOrder.value.id}/delay-delivery`, { newDeliveryDate: delayDeliveryForm.value.newDate, reason: delayDeliveryForm.value.reason })
    bsDelayModal?.hide()
    await loadOrders(currentPage.value)
    Swal.fire('Thành công', 'Đã báo trễ hẹn và gửi email!', 'success')
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật ngày giao', 'error') } 
  finally { submittingDelay.value = false }
}

const shipOrder = async (order) => {
  const result = await Swal.fire({ title: 'Giao cho ĐVVC?', text: `Đơn ${order.orderNumber} -> Đang giao hàng.`, icon: 'question', showCancelButton: true, confirmButtonText: 'Xác nhận', confirmButtonColor: '#0ea5e9' })
  if (result.isConfirmed) {
    try {
      await apiClient.put(`/orders/${order.id}/ship`)
      await loadOrders(currentPage.value)
      Swal.fire({ icon: 'success', title: 'Đã giao cho ĐVVC!', timer: 2000, showConfirmButton: false })
      return true
    } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể giao', 'error') }
  }
  return false
}

const completeOrder = async (order) => {
  const result = await Swal.fire({ title: 'Xác nhận giao thành công?', text: `Đơn ${order.orderNumber} -> Hoàn thành.`, icon: 'question', showCancelButton: true, confirmButtonText: 'Xác nhận', confirmButtonColor: '#10b981' })
  if (result.isConfirmed) {
    try {
      await apiClient.put(`/orders/${order.id}/complete`)
      await loadOrders(currentPage.value)
      Swal.fire({ icon: 'success', title: 'Hoàn thành đơn hàng!', timer: 2000, showConfirmButton: false })
      return true
    } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể hoàn thành', 'error') }
  }
  return false
}

const shipOrderFromModal = async () => {
  if (!selectedOrder.value) return
  const ok = await shipOrder(selectedOrder.value)
  if (ok) bsModal?.hide()
}

const completeOrderFromModal = async () => {
  if (!selectedOrder.value) return
  const ok = await completeOrder(selectedOrder.value)
  if (ok) bsModal?.hide()
}

const getStatusText = (status) => {
  return getOrderStatusLabel(status, {
    DEPOSITED: 'Đã thanh toán',
    PROCESSING: 'Đang chuẩn bị',
    AWAITING_DELIVERY: 'Chờ giao hàng',
    SHIPPING: 'Đang giao',
    COMPLETED: 'Đã giao hàng',
  })
}

// HỆ THỐNG MÀU SẮC CHUẨN MỚI (Pastel Badges)
const getStatusBadgeClass = (status) => {
  const map = {
    AWAITING_PAYMENT: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-25',
    DEPOSITED:        'bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    PROCESSING:       'bg-navy bg-opacity-10 text-navy border border-navy border-opacity-25',
    AWAITING_DELIVERY:'bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    SHIPPING:         'bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25',
    COMPLETED:        'bg-success bg-opacity-10 text-success border border-success border-opacity-25',
    CANCELLED:        'bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25',
  }
  return map[status] || 'bg-secondary bg-opacity-10 text-secondary border border-secondary'
}

// GLOWING DOTS (Dấu chấm sáng)
const getStatusDotClass = (status) => {
  const map = {
    AWAITING_PAYMENT: 'bg-warning shadow-warning',
    DEPOSITED:        'bg-info shadow-info',
    PROCESSING:       'bg-navy shadow-navy',
    AWAITING_DELIVERY:'bg-info shadow-info',
    SHIPPING:         'bg-primary shadow-primary',
    COMPLETED:        'bg-success shadow-success',
    CANCELLED:        'bg-danger shadow-danger',
  }
  return map[status] || 'bg-secondary'
}

const formatDate = (d) => { if (!d) return '—'; return new Date(d).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) }
const formatNumber = (n) => n ? new Intl.NumberFormat('vi-VN').format(n) : '—'
const formatCurrency = (n) => n ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n) : '—'
</script>

<style scoped>
/* Màu sắc & Animation */
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.border-navy { border-color: #0b2e59 !important; }
.bg-slate-50 { background-color: #f8fafc !important; }

.btn-navy { background-color: #0b2e59; color: #fff; border: none; transition: 0.3s; }
.btn-navy:hover { background-color: #173b6c; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(11, 46, 89, 0.2); }

.hover-lift { transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.06) !important; z-index: 2; position: relative; }

/* ─── HIỆU ỨNG PILL STATS ĐỘNG ─── */
.stat-pill-group {
  perspective: 1000px;
}
.stat-pill {
  background: #ffffff; border-radius: 50rem; padding: 10px 22px; 
  border: 1px solid #e2e8f0; display: flex; align-items: center; 
  font-weight: 600; cursor: default; position: relative; overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 2px 5px rgba(0,0,0,0.02);
  z-index: 1;
}
/* Lớp màu nền mờ khi Hover */
.stat-pill::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: currentColor; opacity: 0; z-index: -1;
  transition: opacity 0.3s ease;
}
.stat-pill:hover {
  transform: translateY(-4px) scale(1.02); /* Nảy lên và to ra 2% */
  box-shadow: 0 10px 20px rgba(0,0,0,0.08);
  border-color: currentColor;
}
.stat-pill:hover::before {
  opacity: 0.08; /* Ánh lên màu nền mờ mờ */
}

/* Bảng Separated Row */
.modern-table { border-collapse: separate; border-spacing: 0 12px; }
.modern-table thead th { border: none; padding-bottom: 0; }
.modern-table tbody tr { border-radius: 12px; transition: all 0.2s; border: 1px solid #f1f5f9; }
.modern-table tbody td { border: none; background: #fff; }
.modern-table tbody td:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.modern-table tbody td:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }

/* Form Input */
.custom-input { border: 1px solid #e2e8f0; border-radius: 10px; padding: 0.6rem 1rem; transition: 0.2s; box-shadow: none !important; }
.custom-input:focus { border-color: #0b2e59 !important; box-shadow: 0 0 0 3px rgba(11, 46, 89, 0.1) !important; background-color: #fff; }

/* Nút Action Hình Tròn */
.btn-action-circle {
  width: 40px; height: 40px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center;
  transition: all 0.2s; font-size: 1.1rem;
}
.btn-action-circle:hover { transform: scale(1.15); }
.btn-action-circle.text-primary:hover { background-color: #dbeafe !important; color: #1d4ed8 !important; border-color: #bfdbfe !important; }
.btn-action-circle.text-danger:hover { background-color: #fee2e2 !important; color: #dc2626 !important; border-color: #fecaca !important; }
.btn-action-circle.text-success:hover { background-color: #d1fae5 !important; color: #059669 !important; border-color: #a7f3d0 !important; }
.btn-action-circle.text-warning:hover { background-color: #fef3c7 !important; color: #d97706 !important; border-color: #fde68a !important; }
.btn-action-circle.text-navy:hover { background-color: #e0f2fe !important; color: #0b2e59 !important; border-color: #bae6fd !important; }
.btn-action-circle.text-info:hover { background-color: #cffafe !important; color: #0284c7 !important; border-color: #7dd3fc !important; }

/* Glowing Dots */
.glowing-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; flex-shrink: 0; }
.shadow-success { box-shadow: 0 0 10px #10b981; }
.shadow-danger { box-shadow: 0 0 10px #ef4444; }
.shadow-warning { box-shadow: 0 0 10px #f59e0b; }
.shadow-info { box-shadow: 0 0 10px #0ea5e9; }
.shadow-primary { box-shadow: 0 0 10px #3b82f6; }
.shadow-navy { box-shadow: 0 0 10px #0b2e59; }
</style>