$file = 'MyOrders.vue'
$content = [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8)

# Find where <template> ends and <script setup> begins  
$scriptStart = $content.IndexOf('<script setup>')
Write-Host "Script starts at: $scriptStart"

$newTemplate = @'
<template>
  <Navbar />
  <div class="myorders-page">
    <!-- Hero Header -->
    <div class="orders-hero">
      <div class="container">
        <div class="hero-content">
          <div>
            <h1 class="hero-title">Đơn Hàng Của Tôi</h1>
            <p class="hero-desc">Quản lý và theo dõi tiến độ các đơn hàng gia công của bạn. Hệ thống cập nhật thời gian thực trạng thái sản xuất và báo giá kỹ thuật.</p>
          </div>
          <div class="hero-actions">
            <router-link to="/create-order" class="btn btn-hero-primary">
              <i class="bi bi-plus-circle me-2"></i>Tạo đơn mới
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div class="container orders-content">
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

      <!-- Status Filter Pills -->
      <div class="status-filter-wrap mb-4">
        <button
          v-for="statusTab in currentStatusTabs"
          :key="statusTab.key"
          class="status-pill"
          :class="{ active: activeStatus === statusTab.key }"
          @click="changeStatus(statusTab.key)"
        >
          {{ statusTab.label }}
          <span class="pill-count">{{ getStatusCount(statusTab.key) > 99 ? '99+' : getStatusCount(statusTab.key) }}</span>
        </button>
      </div>

      <!-- Skeleton Loading -->
      <div v-if="isLoading" class="orders-table-card">
        <div class="orders-table-header">
          <div class="col-header" style="width:28%">MÃ ĐƠN HÀNG</div>
          <div class="col-header" style="width:20%">TRẠNG THÁI</div>
          <div class="col-header" style="width:20%">NGÀY ĐẶT</div>
          <div class="col-header" style="width:20%">GIÁ TRỊ</div>
          <div class="col-header" style="width:12%">THAO TÁC</div>
        </div>
        <div v-for="i in 4" :key="i" class="order-table-row skeleton-row">
          <div style="width:28%"><div class="sk sk-title"></div><div class="sk sk-sub mt-1"></div></div>
          <div style="width:20%"><div class="sk sk-badge"></div></div>
          <div style="width:20%"><div class="sk sk-line"></div></div>
          <div style="width:20%"><div class="sk sk-line"></div></div>
          <div style="width:12%"><div class="sk sk-btn"></div></div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="orders.length === 0" class="empty-state-card">
        <i class="bi bi-inbox empty-icon"></i>
        <h5 class="empty-title">Bạn chưa có đơn hàng nào</h5>
        <p class="empty-desc">Tạo đơn hàng gia công đầu tiên của bạn ngay hôm nay.</p>
        <router-link to="/create-order" class="btn btn-hero-primary">
          <i class="bi bi-plus-circle me-2"></i>Tạo đơn hàng mới
        </router-link>
      </div>

      <!-- Orders Table Card -->
      <div v-else class="orders-table-card fade-in">
        <div class="orders-table-header">
          <div class="col-header" style="width:28%">MÃ ĐƠN HÀNG</div>
          <div class="col-header" style="width:20%">TRẠNG THÁI</div>
          <div class="col-header" style="width:20%">NGÀY ĐẶT</div>
          <div class="col-header" style="width:20%">GIÁ TRỊ</div>
          <div class="col-header" style="width:12%">THAO TÁC</div>
        </div>

        <div
          v-for="(order, index) in orders"
          :key="`${order.isTempImport ? 'imp' : 'ord'}-${order.id}`"
          class="order-table-row slide-up"
          :style="{ animationDelay: `${index * 0.06}s` }"
        >
          <!-- Mã đơn hàng -->
          <div class="row-cell order-number-cell" style="width:28%">
            <div class="order-number-text">{{ order.orderNumber }}</div>
            <div class="order-items-count"><i class="bi bi-box me-1"></i>{{ order.items?.length || 0 }} sản phẩm</div>
            <div v-if="order.status === 'PENDING_APPROVAL'" class="mini-alert mini-alert-warning mt-1">
              <i class="bi bi-hourglass-split me-1"></i>Chờ duyệt — sẽ báo giá sau
            </div>
            <div v-else-if="order.status === 'CANCELLED' && isAdminRejectedOrder(order)" class="mini-alert mini-alert-danger mt-1">
              <i class="bi bi-shield-x me-1"></i>Admin từ chối<span v-if="order.cancelReason"> — {{ order.cancelReason }}</span>
            </div>
            <div v-else-if="order.status === 'CANCELLED' && order.cancelReason" class="mini-alert mini-alert-secondary mt-1">
              <i class="bi bi-info-circle me-1"></i>{{ order.cancelReason }}
            </div>
          </div>

          <!-- Trạng thái -->
          <div class="row-cell" style="width:20%">
            <span :class="getStatusBadgeClass(order.status)" class="status-badge-lg">
              {{ getStatusText(order.status, order.orderType) }}
            </span>
          </div>

          <!-- Ngày đặt -->
          <div class="row-cell date-cell" style="width:20%">
            <div class="date-primary">{{ formatDateShort(order.createdAt) }}</div>
            <div class="date-secondary">{{ formatTime(order.createdAt) }}</div>
            <div v-if="order.deliveryDate" class="delivery-date mt-1" :class="isDeliveryDateOverdue(order.deliveryDate) ? 'text-danger' : 'text-info'">
              <i class="bi bi-truck me-1"></i>{{ formatDateShort(order.deliveryDate) }}
              <i v-if="isDeliveryDateOverdue(order.deliveryDate)" class="bi bi-exclamation-circle ms-1"></i>
            </div>
          </div>

          <!-- Giá trị -->
          <div class="row-cell price-cell" style="width:20%">
            <div v-if="order.totalPrice">
              <div class="price-total">{{ formatCurrency(order.totalPrice) }}</div>
              <div class="price-sub mt-1">
                <span v-if="order.orderType === 'READY_MADE'">
                  Đã TT: <strong class="text-success">{{ formatCurrency(order.depositAmount) }}</strong>
                </span>
                <span v-else-if="order.status === 'AWAITING_REMAINING_PAYMENT'">
                  Còn lại: <strong class="text-danger">{{ formatCurrency(Number(order.totalPrice) - Number(order.depositAmount)) }}</strong>
                </span>
                <span v-else>
                  Cọc 60%: <strong class="text-success">{{ formatCurrency(order.depositAmount) }}</strong>
                </span>
              </div>
              <span v-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.depositAmount && order.totalPrice && order.depositAmount >= order.totalPrice" class="badge bg-success mt-1" style="font-size:0.7rem">
                <i class="bi bi-check-circle me-1"></i>Đã TT toàn bộ
              </span>
            </div>
            <span v-else class="text-muted small">Chưa báo giá</span>
          </div>

          <!-- Thao tác -->
          <div class="row-cell action-cell" style="width:12%">
            <button @click="openDetailModal(order)" class="action-link-btn primary-action">
              <i class="bi bi-eye me-1"></i>Xem chi tiết
            </button>
            <button
              v-if="canCancelOrder(order)"
              @click="cancelOrder(order)"
              :disabled="isActionLocked(order, 'cancel')"
              class="action-link-btn danger-action mt-1">
              <span v-if="isActionLocked(order, 'cancel')" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-x-circle me-1"></i>
              {{ isActionLocked(order, 'cancel') ? 'Đang xử lý' : 'Hủy đơn' }}
            </button>
            <button
              v-if="order.orderType === 'READY_MADE' && (order.status === 'AWAITING_PAYMENT' || order.status === 'DEPOSITED')"
              @click="openPaymentModal(order)"
              class="action-link-btn success-action mt-1">
              <i class="bi bi-qr-code me-1"></i>
              {{ order.status === 'DEPOSITED' ? 'Đã TT ✔' : 'Thanh toán' }}
            </button>
            <button
              v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.status === 'AWAITING_PAYMENT'"
              @click="openPaymentModal(order)"
              class="action-link-btn success-action mt-1">
              <i class="bi bi-qr-code me-1"></i>TT cọc 60%
            </button>
            <button
              v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && (order.status === 'PROCESSING' || order.status === 'AWAITING_REMAINING_PAYMENT') && order.depositAmount && order.totalPrice && order.depositAmount < order.totalPrice"
              @click="openRemainingPaymentModal(order)"
              class="action-link-btn warning-action mt-1">
              <i class="bi bi-cash-coin me-1"></i>TT nốt số dư
            </button>
            <button
              v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.status === 'DEPOSITED'"
              @click="openPaymentModal(order)"
              class="action-link-btn success-action mt-1">
              <i class="bi bi-check-circle me-1"></i>Đã cọc ✔
            </button>
            <button
              v-if="order.status === 'SHIPPING'"
              @click="confirmReceivedOrder(order)"
              :disabled="isActionLocked(order, 'confirm')"
              class="action-link-btn success-action mt-1">
              <span v-if="isActionLocked(order, 'confirm')" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-check2-circle me-1"></i>
              {{ isActionLocked(order, 'confirm') ? 'Đang cập nhật' : 'Đã nhận hàng' }}
            </button>
            <button
              v-if="order.status === 'SHIPPING'"
              @click="openComplaintModal(order)"
              :disabled="isActionLocked(order, 'complaint')"
              class="action-link-btn danger-action mt-1">
              <span v-if="isActionLocked(order, 'complaint')" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-exclamation-triangle me-1"></i>
              {{ isActionLocked(order, 'complaint') ? 'Đang mở' : (hasComplaintForOrder(order.id) ? 'Sửa khiếu nại' : 'Khiếu nại') }}
            </button>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="table-pagination">
          <span class="pagination-info">
            Hiển thị trang {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          <div class="pagination-controls">
            <button class="page-btn" :disabled="currentPage === 0 || isLoading" @click="loadOrders(currentPage - 1)">
              <i class="bi bi-chevron-left"></i>
            </button>
            <button
              v-for="page in visiblePages"
              :key="page"
              class="page-btn"
              :class="{ active: page - 1 === currentPage, 'ellipsis-btn': page === '...' }"
              :disabled="page === '...' || isLoading"
              @click="page !== '...' && loadOrders(page - 1)"
            >{{ page }}</button>
            <button class="page-btn" :disabled="currentPage >= totalPages - 1 || isLoading" @click="loadOrders(currentPage + 1)">
              <i class="bi bi-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Detail Modal -->
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
          <div class="order-summary-card mb-3">
            <div class="row g-3">
              <div class="col-md-4">
                <p class="mb-1"><strong>Trạng thái:</strong>
                  <span :class="getStatusBadgeClass(selectedOrder.status)">{{ getStatusText(selectedOrder.status) }}</span>
                </p>
                <p class="mb-0"><strong>Ngày tạo:</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
              </div>
              <div class="col-md-4">
                <p class="mb-1" v-if="selectedOrder.totalPrice">
                  <strong>Tổng giá trị:</strong>
                  <span class="text-primary fw-bold">{{ formatCurrency(selectedOrder.totalPrice) }}</span>
                </p>
                <p class="mb-0" v-if="selectedOrder.depositAmount">
                  <strong>{{ selectedOrder.status === 'AWAITING_REMAINING_PAYMENT' ? 'Còn lại cần TT:' : 'Cọc trước:' }}</strong>
                  <span :class="selectedOrder.status === 'AWAITING_REMAINING_PAYMENT' ? 'text-danger fw-bold' : 'text-success fw-bold'">
                    {{ selectedOrder.status === 'AWAITING_REMAINING_PAYMENT'
                       ? formatCurrency(Number(selectedOrder.totalPrice) - Number(selectedOrder.depositAmount))
                       : formatCurrency(selectedOrder.depositAmount) }}
                  </span>
                </p>
              </div>
              <div class="col-md-4">
                <p class="mb-0"><strong>Số sản phẩm:</strong> {{ selectedOrder.items?.length || 0 }}</p>
              </div>
            </div>
          </div>

          <div v-if="selectedOrder.status === 'PENDING_APPROVAL'" class="alert alert-warning py-2 px-3 small mb-3 pending-approval-banner">
            <i class="bi bi-hourglass-split me-1"></i>Đơn đang chờ admin duyệt.
          </div>
          <div v-if="selectedOrder.status === 'CANCELLED' && isAdminRejectedOrder(selectedOrder)" class="alert alert-danger py-2 px-3 small mb-3">
            <i class="bi bi-shield-x me-1"></i>Đơn bị admin từ chối.
            <span v-if="selectedOrder.cancelReason">Lý do: {{ selectedOrder.cancelReason }}</span>
          </div>
          <div v-else-if="selectedOrder.status === 'CANCELLED' && selectedOrder.cancelReason" class="alert alert-secondary py-2 px-3 small mb-3">
            <i class="bi bi-info-circle me-1"></i>Lý do hủy: {{ selectedOrder.cancelReason }}
          </div>

          <div class="modal-section-tabs mb-3" role="tablist">
            <button type="button" role="tab" class="modal-tab-btn" :class="{ 'modal-tab-active': activeCustomerDetailTab === 'history' }" @click="activeCustomerDetailTab = 'history'">
              <i class="bi bi-clock-history me-1"></i>Lịch sử đơn hàng
              <span class="badge rounded-pill text-bg-light ms-2">{{ sortedOrderHistoryEvents.length }}</span>
            </button>
            <button type="button" role="tab" class="modal-tab-btn" :class="{ 'modal-tab-active': activeCustomerDetailTab === 'materials' }" @click="activeCustomerDetailTab = 'materials'">
              <i class="bi bi-grid-1x2 me-1"></i>Chi tiết vật tư & Báo giá
              <span class="badge rounded-pill text-bg-light ms-2">{{ selectedOrder.items?.length || 0 }}</span>
            </button>
          </div>

          <transition name="modal-tab-fade" mode="out-in">
            <div v-if="activeCustomerDetailTab === 'history'" id="customer-history-panel" key="customer-history" class="tab-panel tab-panel-history" role="tabpanel">
              <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                <h6 class="fw-bold m-0">Nhật ký xử lý đơn hàng</h6>
                <div class="small text-muted d-flex flex-wrap gap-3">
                  <span>Tổng revision: {{ orderRevisionSummaries.length }}</span>
                  <span>Tổng số lần hủy: {{ totalCancelAttempts }}</span>
                </div>
              </div>
              <div v-if="isLoadingOrderHistory" class="history-empty-state">
                <span class="spinner-border spinner-border-sm me-2"></span>Đang tải lịch sử...
              </div>
              <div v-else-if="sortedOrderHistoryEvents.length === 0" class="history-empty-state">
                Chưa có bản ghi lịch sử cho đơn hàng này.
              </div>
              <div v-else class="order-history-list history-timeline">
                <div v-for="event in sortedOrderHistoryEvents" :key="`history-${event.id}`" class="order-history-item">
                  <div class="history-bullet"></div>
                  <div class="history-content">
                    <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
                      <div class="fw-semibold small">{{ getHistoryEventTitle(event) }}</div>
                      <small class="text-muted">{{ formatDate(event.createdAt) }}</small>
                    </div>
                    <div class="small text-muted">
                      Người thao tác: {{ getHistoryActorText(event) }}
                      <span v-if="event.revisionNo"> • Revision #{{ event.revisionNo }}</span>
                      <span v-if="getCancelSequence(event) !== null"> • Lần hủy #{{ getCancelSequence(event) }}</span>
                    </div>
                    <div v-if="event.note" class="small mt-1 fw-medium">{{ getHistoryNoteText(event) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else id="customer-materials-panel" key="customer-materials" class="tab-panel tab-panel-materials" role="tabpanel">
              <h6 class="mb-3 mt-1">Danh sách vật tư ({{ selectedOrder.items?.length || 0 }} items)</h6>
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
                      <td class="cell-uniform">
                        <div class="d-flex align-items-center justify-content-between gap-2">
                          <span class="cell-truncate flex-grow-1">{{ item.itemName || '—' }}</span>
                          <button type="button" class="btn btn-outline-secondary btn-sm item-note-btn" :disabled="selectedOrder.isTempImport" @click="openCustomerItemNoteModal(item)">
                            <i class="bi bi-chat-left-text"></i>
                          </button>
                        </div>
                      </td>
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
              <div v-if="hasReviewedItems" class="mt-3">
                <h6 class="mb-2">Trạng thái review</h6>
                <div class="d-flex gap-2 flex-wrap">
                  <span class="badge bg-success">Đã duyệt: {{ reviewCounts.approved }}</span>
                  <span v-if="reviewCounts.rejected > 0" class="badge bg-danger">Từ chối: {{ reviewCounts.rejected }}</span>
                  <span v-if="reviewCounts.discussion > 0" class="badge bg-warning text-dark">Cần trao đổi: {{ reviewCounts.discussion }}</span>
                  <span v-if="reviewCounts.pending > 0" class="badge bg-secondary">Chờ review: {{ reviewCounts.pending }}</span>
                </div>
                <div class="d-flex flex-wrap gap-2 mt-2">
                  <button v-for="item in selectedOrder.items" :key="'note-' + item.id" v-show="item.adminNote && (item.reviewStatus === 'REJECTED' || item.reviewStatus === 'NEED_DISCUSSION')" type="button" class="btn btn-outline-warning btn-sm note-quick-btn" @click="openCustomerItemNoteModal(item)">
                    <i class="bi bi-chat-left-text me-1"></i>{{ item.itemName || 'Sản phẩm' }}
                  </button>
                </div>
              </div>
            </div>
          </transition>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          <button v-if="canCancelOrder(selectedOrder)" @click="cancelOrder(selectedOrder)" :disabled="isActionLocked(selectedOrder, 'cancel')" class="btn btn-outline-danger">
            <span v-if="isActionLocked(selectedOrder, 'cancel')" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="bi bi-x-circle me-1"></i>
            {{ isActionLocked(selectedOrder, 'cancel') ? 'Đang xử lý' : 'Hủy đơn hàng' }}
          </button>
          <button v-if="selectedOrder.status === 'AWAITING_PAYMENT' || selectedOrder.status === 'DEPOSITED'" @click="openPaymentModal(selectedOrder); bsModal?.hide()" class="btn" :class="selectedOrder.status === 'DEPOSITED' ? 'btn-outline-success' : 'btn-success'">
            <i class="bi bi-qr-code me-1"></i>
            {{ selectedOrder.status === 'DEPOSITED' ? 'Xem trạng thái thanh toán' : 'Thanh toán cọc 60%' }}
          </button>
        </div>
      </div>
    </div>
  </div>
  </Teleport>

  <!-- Payment QR Modal -->
  <Teleport to="body">
    <div class="modal fade" id="paymentQrModal" tabindex="-1" ref="paymentModalRef">
      <div class="modal-dialog modal-dialog-centered" style="max-width: 520px;">
        <div class="modal-content border-0 shadow-lg">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title"><i class="bi bi-wallet2 me-2"></i>Thanh toán đặt cọc</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-0" v-if="selectedPaymentOrderId">
            <PaymentQR :order-id="selectedPaymentOrderId" @payment-confirmed="onPaymentConfirmed" />
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <!-- Complaint Modal -->
  <Teleport to="body">
    <div class="modal fade" id="complaintModal" tabindex="-1" ref="complaintModalRef">
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg" v-if="complaintOrder">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">
              <i class="bi bi-exclamation-octagon me-2"></i>
              {{ hasComplaintForOrder(complaintOrder.id) ? 'Sửa khiếu nại thiếu hàng' : 'Khiếu nại thiếu hàng' }}
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="alert alert-warning py-2 small mb-3">
              <i class="bi bi-info-circle me-1"></i>Bạn chỉ có thể gửi khiếu nại khi đơn đang ở trạng thái ĐANG GIAO.
            </div>
            <div v-if="complaintDraftRestoredAt" class="alert alert-info py-2 small mb-3">
              <i class="bi bi-clock-history me-1"></i>Đã khôi phục bản nháp lưu lúc {{ formatDate(complaintDraftRestoredAt) }}.
            </div>
            <div class="mb-3">
              <label class="form-label fw-semibold">Mô tả khiếu nại</label>
              <textarea v-model="complaintDescription" class="form-control" rows="3" placeholder="Ví dụ: Thiếu 2 sản phẩm mã XYZ trong kiện hàng..."></textarea>
            </div>
            <div class="mb-3">
              <label class="form-label fw-semibold">Chi tiết số lượng thiếu theo từng sản phẩm</label>
              <div class="table-responsive border rounded">
                <table class="table table-sm mb-0 align-middle">
                  <thead class="table-light">
                    <tr>
                      <th>Sản phẩm</th>
                      <th class="text-center" style="width: 120px;">Đặt</th>
                      <th style="width: 180px;">Thiếu</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in complaintOrder.items || []" :key="`complaint-item-${item.id}`">
                      <td>
                        <div class="fw-semibold">{{ item.itemName || item.itemCode || 'Sản phẩm' }}</div>
                        <small class="text-muted">{{ item.itemCode || '---' }}</small>
                      </td>
                      <td class="text-center">{{ item.quantity || 0 }}</td>
                      <td><input v-model.number="complaintMissingByItem[item.id]" type="number" min="0" :max="item.quantity || 0" class="form-control form-control-sm" /></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label fw-semibold">Ảnh minh chứng</label>
              <div v-if="complaintExistingImages.length > 0" class="mb-2">
                <div class="small fw-semibold mb-1">Ảnh đã lưu</div>
                <div class="d-flex flex-wrap gap-2">
                  <div v-for="img in complaintExistingImages" :key="`existing-img-${img.id}`" class="complaint-image-tag" :class="{ removed: complaintRemovedImageIds.includes(img.id) }">
                    <span>{{ img.originalFilename || `Ảnh #${img.id}` }}</span>
                    <button type="button" class="btn btn-sm btn-link text-danger p-0" @click="toggleKeepExistingComplaintImage(img.id)">
                      {{ complaintRemovedImageIds.includes(img.id) ? 'Giữ lại' : 'Bỏ ảnh' }}
                    </button>
                  </div>
                </div>
              </div>
              <input type="file" accept="image/*" multiple class="form-control" @change="onComplaintImagesSelected" />
              <small class="text-muted">Tối đa 5 ảnh, mỗi ảnh tối đa 5MB.</small>
              <div v-if="complaintNewImages.length > 0" class="mt-2">
                <div class="small fw-semibold mb-1">Ảnh mới sẽ tải lên</div>
                <div class="d-flex flex-wrap gap-2">
                  <div v-for="(file, idx) in complaintNewImages" :key="`new-img-${idx}`" class="complaint-image-tag">
                    <span>{{ file.name }}</span>
                    <button type="button" class="btn btn-sm btn-link text-danger p-0" @click="removeNewComplaintImage(idx)">Xóa</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button type="button" class="btn btn-danger" :disabled="isSavingComplaint" @click="submitComplaint">
              <span v-if="isSavingComplaint" class="spinner-border spinner-border-sm me-2"></span>
              {{ hasComplaintForOrder(complaintOrder.id) ? 'Cập nhật khiếu nại' : 'Gửi khiếu nại' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <Footer />
</template>
'@

# Replace line endings
$newTemplate = $newTemplate -replace "`r`n", "`n" -replace "`r", "`n"

$afterTemplate = $content.Substring($scriptStart)
$newContent = $newTemplate + "`n" + $afterTemplate

[System.IO.File]::WriteAllText($file, $newContent, [System.Text.Encoding]::UTF8)
Write-Host "Done! New file length: $($newContent.Length)"
