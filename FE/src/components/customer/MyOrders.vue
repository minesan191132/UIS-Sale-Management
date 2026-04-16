<template>
<div class="app-view">
  <div class="myorders-page">
    <Navbar />
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
          <div class="col-header" style="width:24%">MÃ ĐƠN HÀNG</div>
          <div class="col-header" style="width:16%">TRẠNG THÁI</div>
          <div class="col-header" style="width:14%">NGÀY ĐẶT</div>
          <div class="col-header" style="width:16%">NGÀY GIAO</div>
          <div class="col-header" style="width:18%">GIÁ TRỊ</div>
          <div class="col-header" style="width:12%">THAO TÁC</div>
        </div>
        <div v-for="i in 4" :key="i" class="order-table-row skeleton-row">
          <div style="width:24%"><div class="sk sk-title"></div><div class="sk sk-sub mt-1"></div></div>
          <div style="width:16%"><div class="sk sk-badge"></div></div>
          <div style="width:14%"><div class="sk sk-line"></div></div>
          <div style="width:16%"><div class="sk sk-line"></div></div>
          <div style="width:18%"><div class="sk sk-line"></div></div>
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
          <div class="col-header" style="width:24%">MÃ ĐƠN HÀNG</div>
          <div class="col-header" style="width:16%">TRẠNG THÁI</div>
          <div class="col-header" style="width:14%">NGÀY ĐẶT</div>
          <div class="col-header" style="width:16%">NGÀY GIAO</div>
          <div class="col-header" style="width:18%">GIÁ TRỊ</div>
          <div class="col-header" style="width:12%">THAO TÁC</div>
        </div>

        <div
          v-for="(order, index) in orders"
          :key="`${order.isTempImport ? 'imp' : 'ord'}-${order.id}`"
          class="order-table-row slide-up"
          :style="{ animationDelay: `${index * 0.06}s` }"
        >
          <!-- Mã đơn hàng -->
          <div class="row-cell order-number-cell" style="width:24%">
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
          <div class="row-cell status-cell" style="width:16%">
            <span :class="getStatusBadgeClass(order.status)" class="status-badge-lg">
              {{ getStatusText(order.status, order.orderType) }}
            </span>
          </div>

          <!-- Ngày đặt -->
          <div class="row-cell date-cell" style="width:14%">
            <div class="date-primary">{{ formatDateShort(order.createdAt) }}</div>
            <div class="date-secondary">{{ formatTime(order.createdAt) }}</div>
          </div>

          <!-- Ngày giao -->
          <div class="row-cell delivery-cell" style="width:16%">
            <template v-if="order.deliveryDate">
              <div class="delivery-primary" :class="isDeliveryDateOverdue(order.deliveryDate) ? 'text-danger' : 'text-info'">
                <i class="bi bi-truck me-1"></i>{{ formatDateShort(order.deliveryDate) }}
                <i v-if="isDeliveryDateOverdue(order.deliveryDate)" class="bi bi-exclamation-circle ms-1"></i>
              </div>
              <div class="date-secondary">Dự kiến giao</div>
            </template>
            <div v-else class="date-secondary">Chưa cập nhật</div>
          </div>

          <!-- Giá trị -->
          <div class="row-cell price-cell" style="width:18%">
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
              v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && order.status === 'AWAITING_CONTRACT'"
              @click="openContractModal(order)"
              class="action-link-btn warning-action mt-1">
              <i class="bi bi-file-earmark-text me-1"></i>Xem hợp đồng
            </button>
            <button
              v-else-if="order.orderType === 'CUSTOM_MANUFACTURING' && (order.status === 'AWAITING_PAYMENT' || order.status === 'AWAITING_REMAINING_PAYMENT')"
              @click="openPaymentModal(order)"
              class="action-link-btn success-action mt-1">
              <i class="bi bi-qr-code me-1"></i>{{ getCustomerPaymentActionLabel(order) }}
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
              v-if="order.status === 'SHIPPING' || order.status === 'COMPLETED'"
              @click="openComplaintModal(order)"
              :disabled="isActionLocked(order, 'complaint')"
              class="action-link-btn danger-action mt-1">
              <span v-if="isActionLocked(order, 'complaint')" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-exclamation-triangle me-1"></i>
              {{ isActionLocked(order, 'complaint') ? 'Đang mở' : (hasComplaintForOrder(order.id) ? 'Xem khiếu nại' : 'Khiếu nại') }}
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
                  <span :class="getStatusBadgeClass(selectedOrder.status)">{{ getStatusText(selectedOrder.status, selectedOrder.orderType) }}</span>
                </p>
                <p class="mb-1"><strong>Ngày tạo:</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
                <p class="mb-0"><strong>Ngày giao:</strong>
                  <span v-if="selectedOrder.deliveryDate" :class="isDeliveryDateOverdue(selectedOrder.deliveryDate) ? 'text-danger fw-semibold' : 'text-info fw-semibold'">
                    {{ formatDateShort(selectedOrder.deliveryDate) }}
                    <i v-if="isDeliveryDateOverdue(selectedOrder.deliveryDate)" class="bi bi-exclamation-circle ms-1"></i>
                  </span>
                  <span v-else class="text-muted">Chưa cập nhật</span>
                </p>
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

          <div v-if="selectedOrder.status === 'PENDING_APPROVAL'" class="alert alert-warning py-2 px-3 small mb-3 pending-approval-banner alert-fit-content">
            <i class="bi bi-hourglass-split me-1"></i>Đơn đang chờ admin duyệt.
          </div>
          <div v-if="selectedOrder.orderType === 'CUSTOM_MANUFACTURING' && selectedOrder.status === 'AWAITING_CONTRACT'" class="alert alert-info py-2 px-3 small mb-3 alert-fit-content">
            <i class="bi bi-file-earmark-text me-1"></i>Báo giá đã được gửi. Vui lòng xem và xác nhận hợp đồng để kích hoạt thanh toán mốc 1.
          </div>

          <div v-if="selectedOrder.orderType === 'CUSTOM_MANUFACTURING' && selectedOrderMilestones.length" class="contract-overview-wrap mb-3">
            <div class="contract-overview-card">
              <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-2">
                <h6 class="m-0 fw-bold text-dark">Tiến độ hợp đồng & mốc thanh toán</h6>
                <span v-if="selectedOrderContract" class="badge rounded-pill" :class="getContractStatusClass(selectedOrderContract.status)">
                  Hợp đồng: {{ getContractStatusText(selectedOrderContract.status) }}
                </span>
              </div>

              <div class="milestone-mini-list">
                <div v-for="milestone in selectedOrderMilestones" :key="`milestone-mini-${milestone.id}`" class="milestone-mini-item">
                  <div class="d-flex justify-content-between align-items-center gap-2">
                    <strong class="small">Mốc {{ milestone.milestoneOrder }} - {{ milestone.milestoneName }}</strong>
                    <span class="badge" :class="getMilestoneBadgeClass(milestone.status)">{{ getMilestoneStatusText(milestone.status) }}</span>
                  </div>
                  <div class="small text-muted mt-1">
                    {{ formatCurrency(milestone.amount) }}
                    <span v-if="milestone.dueDate" class="ms-2">Hạn: {{ formatDateShort(milestone.dueDate) }}</span>
                  </div>
                </div>
              </div>

              <button
                v-if="selectedOrder.status === 'AWAITING_CONTRACT'"
                class="btn btn-sm btn-outline-warning mt-3"
                @click="openContractModal(selectedOrder)">
                <i class="bi bi-file-earmark-text me-1"></i>Mở hợp đồng để xác nhận
              </button>
            </div>
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
              <div class="d-flex justify-content-between align-items-end mb-3">
                <div>
                  <p class="small text-muted fw-medium mb-1"><i class="bi bi-arrows-move me-1"></i>Cuộn ngang để xem đầy đủ các cột dữ liệu.</p>
                  <p class="mb-0 text-muted small fw-bold text-uppercase">Tiến trình review: <span class="badge bg-warning text-dark ms-1">{{ getReviewProgress(selectedOrder) }}</span></p>
                </div>
                <div v-if="selectedOrder?.status !== 'PENDING_APPROVAL'" class="btn-group bg-white border p-1 rounded-pill shadow-sm">
                  <button class="btn btn-sm rounded-pill fw-bold" :class="materialsDensity === 'comfortable' ? 'btn-navy' : 'btn-light text-muted'" @click="materialsDensity = 'comfortable'">Rộng rãi</button>
                  <button class="btn btn-sm rounded-pill fw-bold" :class="materialsDensity === 'compact' ? 'btn-navy' : 'btn-light text-muted'" @click="materialsDensity = 'compact'">Thu gọn</button>
                </div>
              </div>

              <div class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white">
                <div class="table-responsive" style="max-height: 500px; overflow-y: auto;">
                  <table class="table table-bordered align-middle mb-0 review-table" :class="materialsDensity === 'compact' ? 'table-sm review-table-compact' : 'review-table-comfortable'">
                    <thead class="table-light sticky-top" style="z-index: 1;">
                      <tr class="text-center small text-muted text-uppercase fw-bold">
                        <th style="width: 50px;">STT</th>
                        <th style="min-width: 120px;">VNN_NO</th>
                        <th style="min-width: 95px;">Item Code</th>
                        <th style="min-width: 105px;">Bản vẽ</th>
                        <th style="min-width: 180px;">Tên linh kiện</th>
                        <th style="min-width: 140px;">Spec</th>
                        <th style="min-width: 95px;">Vật liệu</th>
                        <th style="width: 65px;">SL</th>
                        <th style="min-width: 105px;">Ngày xuất</th>
                        <th style="min-width: 95px;">Review</th>
                        <th class="text-end" style="min-width: 130px;">Đơn giá</th>
                        <th class="text-end" style="min-width: 130px;">Thành tiền</th>
                      </tr>
                    </thead>
                    <tbody class="text-center bg-white">
                      <tr v-for="(item, index) in selectedOrder.items" :key="item.id" :class="getItemRowClass(item)">
                        <td class="fw-bold text-muted">{{ index + 1 }}</td>
                        <td class="small">{{ item.unit || '—' }}</td>
                        <td class="small text-secondary fw-bold">{{ item.itemCode || '—' }}</td>
                        <td class="font-monospace text-primary fw-bold small">{{ item.drawingNumber || '—' }}</td>
                        <td class="text-start fw-bold text-dark">
                          <div class="d-flex align-items-center justify-content-between gap-2">
                            <span class="cell-truncate flex-grow-1">{{ item.itemName || '—' }}</span>
                            <button type="button" class="btn btn-outline-secondary btn-sm item-note-btn" :disabled="selectedOrder.isTempImport" @click="openCustomerItemNoteModal(item)">
                              <i class="bi bi-chat-left-text"></i>
                            </button>
                          </div>
                        </td>
                        <td class="text-start small text-secondary">{{ item.specification || '—' }}</td>
                        <td class="small">{{ item.material || '—' }}</td>
                        <td><span class="badge bg-light text-dark border px-2 py-1">{{ item.quantity }}</span></td>
                        <td>
                          <span v-if="item.deliveryDate" class="badge bg-info-subtle text-info border border-info border-opacity-25">{{ formatDateShort(item.deliveryDate) }}</span>
                          <span v-else class="text-muted">—</span>
                        </td>
                        <td>
                          <span class="badge rounded-pill" :class="getReviewBadgeClass(item.reviewStatus)" style="font-size: 0.7rem">{{ getReviewStatusText(item.reviewStatus) }}</span>
                        </td>
                        <td class="text-end fw-semibold">
                          <span v-if="item.unitPrice">{{ formatNumber(item.unitPrice) }}</span>
                          <span v-else class="text-muted">—</span>
                        </td>
                        <td class="text-end fw-semibold">
                          <span v-if="item.totalItemPrice" class="fw-bold">{{ formatNumber(item.totalItemPrice) }}</span>
                          <span v-else class="text-muted">—</span>
                        </td>
                      </tr>
                    </tbody>
                    <tfoot v-if="selectedOrder.totalPrice">
                      <tr class="table-light">
                        <td colspan="11" class="text-end fw-bold py-2">Tổng giá trị đơn hàng:</td>
                        <td class="text-end fw-bold py-2 text-primary fs-6">{{ formatNumber(selectedOrder.totalPrice) }}</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
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
          <button
            v-if="selectedOrder.orderType === 'CUSTOM_MANUFACTURING' && selectedOrder.status === 'AWAITING_CONTRACT'"
            @click="openContractModal(selectedOrder); bsModal?.hide()"
            class="btn btn-warning text-dark">
            <i class="bi bi-file-earmark-text me-1"></i>Xem hợp đồng
          </button>
          <button
            v-else-if="selectedOrder.orderType === 'READY_MADE' && (selectedOrder.status === 'AWAITING_PAYMENT' || selectedOrder.status === 'DEPOSITED')"
            @click="openPaymentModal(selectedOrder); bsModal?.hide()"
            class="btn"
            :class="selectedOrder.status === 'DEPOSITED' ? 'btn-outline-success' : 'btn-success'">
            <i class="bi bi-qr-code me-1"></i>
            {{ selectedOrder.status === 'DEPOSITED' ? 'Xem trạng thái thanh toán' : 'Thanh toán' }}
          </button>
          <button
            v-else-if="selectedOrder.orderType === 'CUSTOM_MANUFACTURING' && (selectedOrder.status === 'AWAITING_PAYMENT' || selectedOrder.status === 'AWAITING_REMAINING_PAYMENT' || selectedOrder.status === 'DEPOSITED')"
            @click="openPaymentModal(selectedOrder); bsModal?.hide()"
            class="btn"
            :class="selectedOrder.status === 'DEPOSITED' ? 'btn-outline-success' : 'btn-success'">
            <i class="bi bi-qr-code me-1"></i>
            {{ getCustomerPaymentActionLabel(selectedOrder) }}
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
            <h5 class="modal-title"><i class="bi bi-wallet2 me-2"></i>Thanh toán đơn hàng</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-0" v-if="selectedPaymentOrderId">
            <PaymentQR
              :order-id="selectedPaymentOrderId"
              :order-type="activeOrderType"
              @payment-confirmed="onPaymentConfirmed"
            />
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <!-- Contract Modal -->
  <Teleport to="body">
    <div class="modal fade" id="contractModal" tabindex="-1" ref="contractModalRef">
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg">
          <div class="modal-header bg-warning-subtle">
            <h5 class="modal-title">
              <i class="bi bi-file-earmark-text me-2"></i>
              Hợp đồng đơn {{ selectedContractOrder?.orderNumber }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <div class="modal-body">
            <div v-if="contractLoading" class="text-center py-4">
              <span class="spinner-border spinner-border-sm me-2"></span>Đang tải hợp đồng...
            </div>

            <template v-else-if="selectedContract">
              <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
                <div>
                  <div class="small text-muted">Số hợp đồng</div>
                  <strong>{{ selectedContract.contractNumber || '—' }}</strong>
                </div>
                <span class="badge rounded-pill" :class="getContractStatusClass(selectedContract.status)">
                  {{ getContractStatusText(selectedContract.status) }}
                </span>
              </div>

              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <div class="border rounded p-3 h-100">
                    <div class="small text-muted fw-semibold mb-2">Bên cung cấp</div>
                    <div class="small"><strong>{{ selectedContract.supplierInfo?.companyName || '—' }}</strong></div>
                    <div class="small">MST: {{ selectedContract.supplierInfo?.taxCode || '—' }}</div>
                    <div class="small">Địa chỉ: {{ selectedContract.supplierInfo?.address || '—' }}</div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="border rounded p-3 h-100">
                    <div class="small text-muted fw-semibold mb-2">Bên mua</div>
                    <div class="small"><strong>{{ selectedContract.buyerInfo?.companyName || '—' }}</strong></div>
                    <div class="small">MST: {{ selectedContract.buyerInfo?.taxCode || '—' }}</div>
                    <div class="small">Địa chỉ: {{ selectedContract.buyerInfo?.address || '—' }}</div>
                  </div>
                </div>
              </div>

              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <div class="border rounded p-3 h-100 bg-light-subtle">
                    <div class="small text-muted fw-semibold mb-2">Ngày đặt hàng</div>
                    <div class="small fw-semibold text-dark">{{ selectedContract.orderInfo?.orderDate ? formatDateShort(selectedContract.orderInfo.orderDate) : '—' }}</div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="border rounded p-3 h-100 bg-light-subtle">
                    <div class="small text-muted fw-semibold mb-2">Ngày nhận hàng dự kiến</div>
                    <div class="small fw-semibold text-dark">{{ selectedContract.orderInfo?.deliveryDate ? formatDateShort(selectedContract.orderInfo.deliveryDate) : '—' }}</div>
                  </div>
                </div>
              </div>

              <div class="mb-3">
                <div class="small text-muted fw-semibold mb-2">Mốc thanh toán</div>
                <div class="border rounded overflow-hidden">
                  <table class="table table-sm mb-0">
                    <thead class="table-light">
                      <tr>
                        <th>Mốc</th>
                        <th class="text-end">Tỉ lệ</th>
                        <th class="text-end">Số tiền</th>
                        <th class="text-center">Trạng thái</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="milestone in selectedContract.milestones || []" :key="`contract-ms-${milestone.milestoneOrder}`">
                        <td>{{ milestone.milestoneName }}</td>
                        <td class="text-end">{{ milestone.percentage }}%</td>
                        <td class="text-end fw-semibold">{{ formatCurrency(milestone.amount) }}</td>
                        <td class="text-center">
                          <span class="badge" :class="getMilestoneBadgeClass(milestone.status)">{{ getMilestoneStatusText(milestone.status) }}</span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div class="mb-3">
                <div class="small text-muted fw-semibold mb-2">Điều khoản chất lượng</div>
                <ul class="small mb-0 ps-3">
                  <li v-for="(term, idx) in selectedContract.qualityTerms || []" :key="`quality-term-${idx}`">{{ term }}</li>
                </ul>
              </div>

              <div class="mb-1">
                <div class="small text-muted fw-semibold mb-2">Điều khoản hủy</div>
                <ul class="small mb-0 ps-3">
                  <li v-for="(term, idx) in selectedContract.cancelTerms || []" :key="`cancel-term-${idx}`">{{ term }}</li>
                </ul>
              </div>

              <div v-if="selectedContract.extraNotes" class="alert alert-light border mt-3 mb-0 small">
                <strong>Ghi chú bổ sung:</strong> {{ selectedContract.extraNotes }}
              </div>
            </template>

            <div v-else class="alert alert-danger mb-0">Không tìm thấy thông tin hợp đồng.</div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button
              v-if="selectedContract?.status === 'PENDING_CONFIRMATION'"
              type="button"
              class="btn btn-outline-danger"
              :disabled="contractSubmitting"
              @click="rejectSelectedContract">
              <span v-if="contractSubmitting" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-x-circle me-1"></i>Từ chối hợp đồng
            </button>
            <button
              v-if="selectedContract?.status === 'PENDING_CONFIRMATION'"
              type="button"
              class="btn btn-success"
              :disabled="contractSubmitting"
              @click="confirmSelectedContract">
              <span v-if="contractSubmitting" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-check2-circle me-1"></i>Xác nhận hợp đồng
            </button>
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
              <i class="bi bi-exclamation-triangle"></i>
              {{ hasComplaintForOrder(complaintOrder.id) ? 'Chi tiết Khiếu nại Đơn hàng' : 'Khiếu nại Đơn hàng' }}
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body p-4 bg-light">
            <div class="alert alert-warning py-2 small mb-3">
              <i class="bi bi-info-circle me-1"></i>Bạn chỉ có thể giải quyết khiếu nại ở trạng thái ĐANG GIAO hoặc HOÀN THÀNH.
            </div>
            
            <!-- Trạng thái & Phản hồi của Admin (Làm nổi bật) -->
            <div v-if="complaintAdminNote || (complaintStatus && complaintStatus !== 'OPEN')" class="alert mb-4 shadow-sm border" :class="{ 'alert-info border-info': complaintStatus === 'IN_REVIEW', 'alert-success border-success': complaintStatus === 'RESOLVED', 'alert-danger border-danger': complaintStatus === 'REJECTED', 'alert-secondary': complaintStatus === 'OPEN' }">
              <h5 class="alert-heading fw-bold mb-2">
                <i class="bi bi-info-circle-fill me-2"></i>Trạng thái: {{ complaintStatus }}
              </h5>
              <div v-if="complaintAdminNote" class="mt-3">
                <p class="mb-2 fw-bold text-dark"><i class="bi bi-chat-square-quote me-1"></i>Phản hồi từ Ban Quản Trị:</p>
                <div class="p-3 bg-white rounded text-dark border shadow-sm" style="white-space: pre-wrap; font-size: 0.95rem">{{ complaintAdminNote }}</div>
              </div>
            </div>

            <div class="card shadow-sm border-0 mb-4">
              <div class="card-body">
                <h6 class="card-title fw-bold text-dark mb-3">Thông tin khiếu nại</h6>
                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small text-muted">Loại khiếu nại</label>
                    <select v-model="complaintType" class="form-select form-select-sm" :disabled="complaintStatus === 'IN_REVIEW'">
                      <option value="MISSING_ITEM">Thiếu hàng</option>
                      <option value="DEFECTIVE_ITEM">Hàng lỗi / Hỏng hóc</option>
                      <option value="OTHER">Lý do khác</option>
                    </select>
                  </div>
                  <div class="col-12">
                    <label class="form-label fw-semibold small text-muted">Mô tả khiếu nại</label>
                    <textarea v-model="complaintDescription" class="form-control form-control-sm" rows="3" placeholder="Nhập mô tả cụ thể..." :disabled="complaintStatus === 'IN_REVIEW'"></textarea>
                  </div>
                </div>
              </div>
            </div>

            <div class="card shadow-sm border-0 mb-4">
              <div class="card-body p-0">
                <div class="table-responsive">
                  <table class="table table-bordered table-striped table-hover mb-0 align-middle" style="font-size: 0.85rem">
                    <thead class="table-light">
                      <tr>
                        <th>Sản phẩm</th>
                        <th class="text-center" style="width: 70px;">Đặt</th>
                        <th style="width: 100px;">SL Thiếu</th>
                        <th style="width: 100px;">SL Lỗi</th>
                        <th>Ghi chú lỗi</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="item in complaintOrder.items || []" :key="`complaint-item-${item.id}`">
                        <td>
                          <div class="fw-semibold text-dark">{{ item.itemName || 'Sản phẩm' }}</div>
                          <small class="text-muted font-monospace">{{ item.itemCode }}</small>
                        </td>
                        <td class="text-center fw-bold">{{ item.quantity || 0 }}</td>
                        <td>
                          <input v-model.number="complaintMissingByItem[item.id]" type="number" min="0" :max="item.quantity || 0" class="form-control form-control-sm" :disabled="complaintStatus === 'IN_REVIEW' || complaintType === 'DEFECTIVE_ITEM'" />
                        </td>
                        <td>
                          <input v-model.number="complaintDefectiveByItem[item.id]" type="number" min="0" :max="item.quantity || 0" class="form-control form-control-sm" :disabled="complaintStatus === 'IN_REVIEW' || complaintType === 'MISSING_ITEM'" />
                        </td>
                        <td>
                          <input v-model="complaintReasonByItem[item.id]" type="text" class="form-control form-control-sm" placeholder="Ghi chú thêm..." :disabled="complaintStatus === 'IN_REVIEW'" />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
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

            <!-- History section -->
            <div v-if="complaintHistory && complaintHistory.length > 0" class="card shadow-sm border-0 mt-4 mb-3">
              <div class="card-header bg-white fw-bold"><i class="bi bi-clock-history me-2"></i>Lịch sử khiếu nại</div>
              <div class="card-body p-0">
                <div class="list-group list-group-flush" style="max-height: 250px; overflow-y: auto;">
                  <div v-for="hi in complaintHistory" :key="hi.id" class="list-group-item">
                    <div class="d-flex w-100 justify-content-between">
                      <h6 class="mb-1 small fw-bold text-dark">{{ hi.actionType }} - <span class="badge" :class="hi.newStatus === 'OPEN' ? 'bg-secondary' : 'bg-success'">{{ hi.newStatus || 'UNKNOWN' }}</span></h6>
                      <small class="text-muted">{{ formatDate(hi.createdAt) }}</small>
                    </div>
                    <p class="mb-1 small text-muted">Bởi: <span class="fw-medium text-dark">{{ hi.actionByUserName || 'Khách hàng' }}</span></p>
                    <small>Ghi chú: <span class="text-secondary">{{ hi.note || 'Không có' }}</span></small>
                  </div>
                </div>
              </div>
            </div>

          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button type="button" class="btn btn-danger" :disabled="isSavingComplaint || complaintStatus === 'IN_REVIEW'" @click="submitComplaint" v-if="complaintStatus !== 'IN_REVIEW'">
              <span v-if="isSavingComplaint" class="spinner-border spinner-border-sm me-2"></span>
              {{ hasComplaintForOrder(complaintOrder.id) ? 'Cập nhật khiếu nại' : 'Gửi khiếu nại' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import apiClient, { ordersAPI, paymentAPI, contractAPI } from '../../services/api'
import { Modal } from 'bootstrap'
import PaymentQR from './PaymentQR.vue'
import { getOrderStatusLabel, getReviewStatusLabel } from '../../constants/orderStatus'
import Navbar from '../base/Navbar.vue'
import Footer from '../base/Footer.vue'

const route = useRoute()
const router = useRouter()

const VIEW_STATE_STORAGE_KEY = 'myOrders.viewState.v1'
const COMPLAINT_DRAFT_STORAGE_PREFIX = 'myOrders.complaintDraft.'
const COMPLAINT_DRAFT_MAX_AGE_MS = 3 * 24 * 60 * 60 * 1000

const orders = ref([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const selectedOrder = ref(null)
const detailModalRef = ref(null)
const paymentModalRef = ref(null)
const contractModalRef = ref(null)
const complaintModalRef = ref(null)
const selectedPaymentOrderId = ref(null)
const activeOrderType = ref('CUSTOM_MANUFACTURING')
const getDefaultStatusForType = (type) =>
  type === 'CUSTOM_MANUFACTURING' ? 'PENDING_APPROVAL' : 'AWAITING_PAYMENT'

const activeStatus = ref(getDefaultStatusForType(activeOrderType.value))
const statusCounts = ref({})
const complaintOrder = ref(null)
const complaintType = ref('MISSING_ITEM')
const complaintDescription = ref('')
const complaintStatus = ref('')
const complaintAdminNote = ref('')
const complaintHistory = ref([])
const complaintMissingByItem = ref({})
const complaintDefectiveByItem = ref({})
const complaintReasonByItem = ref({})
const complaintExistingImages = ref([])
const complaintRemovedImageIds = ref([])
const complaintNewImages = ref([])
const complaintExistsByOrderId = ref({})
const isSavingComplaint = ref(false)
const actionLoadingByOrderId = ref({})
const complaintInitialState = ref(null)
const complaintDraftRestoredAt = ref(null)
const bypassComplaintHideGuard = ref(false)
const orderHistoryEvents = ref([])
const orderRevisionSummaries = ref([])
const isLoadingOrderHistory = ref(false)
const selectedOrderMilestones = ref([])
const selectedOrderContract = ref(null)
const selectedContractOrder = ref(null)
const selectedContract = ref(null)
const contractLoading = ref(false)
const contractSubmitting = ref(false)
const activeCustomerDetailTab = ref('materials')
const materialsDensity = ref('comfortable')
let bsModal = null
let bsPaymentModal = null
let bsContractModal = null
let bsComplaintModal = null
let complaintModalHideHandler = null
let complaintModalHiddenHandler = null
let complaintDraftSaveTimer = null

// ── Type tabs (main level) ──
const typeTabs = [
  { key: 'CUSTOM_MANUFACTURING', label: 'Đơn hàng gia công', icon: '🔧' },
  { key: 'READY_MADE',           label: 'Sản phẩm phôi',   icon: '🛒' },
]

const manufacturingStatusTabs = [
  { key: 'PENDING_APPROVAL', label: 'Chờ duyệt đơn' },
  { key: 'PENDING_QUOTE', label: 'Chờ báo giá' },
  { key: 'AWAITING_CONTRACT', label: 'Chờ xác nhận hợp đồng' },
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
  { key: 'DEPOSITED', label: 'Đã thanh toán' },
  { key: 'PROCESSING', label: 'Đang chuẩn bị' },
  { key: 'AWAITING_DELIVERY', label: 'Chờ giao hàng' },
  { key: 'SHIPPING', label: 'Đang giao' },
  { key: 'COMPLETED', label: 'Đã nhận hàng' },
  { key: 'CANCELLED', label: 'Đã hủy' },
  { key: 'ALL', label: 'Tất cả' },
]

const manufacturingStatusKeySet = new Set(manufacturingStatusTabs.map(tab => tab.key))
const productStatusKeySet = new Set(productStatusTabs.map(tab => tab.key))



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

const parsePositiveInt = (value, fallback = 0) => {
  const num = Number(value)
  if (!Number.isFinite(num)) return fallback
  return Math.max(0, Math.trunc(num))
}

const getQueryValue = (value) => {
  if (Array.isArray(value)) {
    return value.length > 0 ? value[0] : undefined
  }
  return value
}

const makeOrderActionKey = (order, actionName) => {
  const scope = order?.isTempImport ? 'import' : 'order'
  return `${scope}:${order?.id || 'unknown'}:${actionName}`
}

const isActionLocked = (order, actionName) => {
  if (!order?.id) return false
  return !!actionLoadingByOrderId.value[makeOrderActionKey(order, actionName)]
}

const setActionLocked = (order, actionName, isLocked) => {
  if (!order?.id) return
  const key = makeOrderActionKey(order, actionName)
  if (isLocked) {
    actionLoadingByOrderId.value = {
      ...actionLoadingByOrderId.value,
      [key]: true,
    }
    return
  }

  const next = { ...actionLoadingByOrderId.value }
  delete next[key]
  actionLoadingByOrderId.value = next
}

const showStatusToast = (title, text = '') => {
  return Swal.fire({
    toast: true,
    position: 'top-end',
    icon: 'success',
    title,
    text,
    showConfirmButton: false,
    timer: 2400,
    timerProgressBar: true,
    scrollbarPadding: false,
    customClass: {
      popup: 'status-toast-popup',
      title: 'status-toast-title',
      htmlContainer: 'status-toast-text',
    },
  })
}

const persistViewState = (overrides = {}) => {
  const orderType = overrides.orderType ?? activeOrderType.value
  const status = overrides.status ?? activeStatus.value
  const page = parsePositiveInt(overrides.page ?? currentPage.value, 0)

  try {
    localStorage.setItem(VIEW_STATE_STORAGE_KEY, JSON.stringify({
      orderType,
      status,
      page,
      savedAt: Date.now(),
    }))
  } catch (error) {
    console.warn('Failed to persist MyOrders view state:', error)
  }
}

const restoreViewState = () => {
  try {
    const raw = localStorage.getItem(VIEW_STATE_STORAGE_KEY)
    if (!raw) return

    const parsed = JSON.parse(raw)
    const restoredOrderType = parsed?.orderType === 'READY_MADE' ? 'READY_MADE' : 'CUSTOM_MANUFACTURING'
    const validStatusSet = restoredOrderType === 'READY_MADE' ? productStatusKeySet : manufacturingStatusKeySet
    const restoredStatus = validStatusSet.has(parsed?.status)
      ? parsed.status
      : getDefaultStatusForType(restoredOrderType)

    activeOrderType.value = restoredOrderType
    activeStatus.value = restoredStatus
    currentPage.value = parsePositiveInt(parsed?.page, 0)
  } catch (error) {
    console.warn('Failed to restore MyOrders view state:', error)
  }
}

const getComplaintDraftStorageKey = (orderId) => `${COMPLAINT_DRAFT_STORAGE_PREFIX}${orderId}`

const normalizeComplaintMissingMap = (order, source) => {
  const normalized = {}
  const items = order?.items || []

  for (const item of items) {
    const itemId = item?.id
    if (itemId == null) continue

    const maxQty = Math.max(0, Number(item.quantity || 0))
    const rawQty = Number(source?.[itemId] || 0)
    const clamped = Number.isFinite(rawQty)
      ? Math.max(0, Math.min(maxQty, Math.trunc(rawQty)))
      : 0

    if (clamped > 0) {
      normalized[itemId] = clamped
    }
  }

  return normalized
}

const createComplaintStateSnapshot = () => {
  if (!complaintOrder.value?.id) return null

  const removedImageIds = (complaintRemovedImageIds.value || [])
    .map((id) => Number(id))
    .filter((id) => Number.isFinite(id))
    .sort((a, b) => a - b)

  return {
    description: (complaintDescription.value || '').trim(),
    missingByItem: normalizeComplaintMissingMap(complaintOrder.value, complaintMissingByItem.value),
    removedImageIds,
    hasNewImages: (complaintNewImages.value || []).length > 0,
  }
}

const toComparableComplaintState = (snapshot) => {
  if (!snapshot) return null

  return {
    description: snapshot.description || '',
    missingByItem: snapshot.missingByItem || {},
    removedImageIds: snapshot.removedImageIds || [],
  }
}

const setComplaintInitialState = () => {
  complaintInitialState.value = toComparableComplaintState(createComplaintStateSnapshot())
}

const hasMeaningfulComplaintDraft = (snapshot) => {
  if (!snapshot) return false
  if (snapshot.description) return true
  if (Object.keys(snapshot.missingByItem || {}).length > 0) return true
  if ((snapshot.removedImageIds || []).length > 0) return true
  return false
}

const hasComplaintUnsavedChanges = computed(() => {
  if (!complaintOrder.value?.id || !complaintInitialState.value) return false

  const current = createComplaintStateSnapshot()
  const initial = complaintInitialState.value
  if (!current) return false

  if (current.hasNewImages) return true
  if (current.description !== initial.description) return true
  if (JSON.stringify(current.missingByItem) !== JSON.stringify(initial.missingByItem)) return true
  if (JSON.stringify(current.removedImageIds) !== JSON.stringify(initial.removedImageIds)) return true

  return false
})

const clearComplaintDraft = (orderId = complaintOrder.value?.id) => {
  if (!orderId) return

  try {
    localStorage.removeItem(getComplaintDraftStorageKey(orderId))
  } catch (error) {
    console.warn('Failed to clear complaint draft:', error)
  }

  if (complaintOrder.value?.id === orderId) {
    complaintDraftRestoredAt.value = null
  }
}

const saveComplaintDraft = (orderId = complaintOrder.value?.id) => {
  if (!orderId || !complaintOrder.value?.id) return

  const snapshot = createComplaintStateSnapshot()
  if (!snapshot) return

  if (!hasMeaningfulComplaintDraft(snapshot)) {
    clearComplaintDraft(orderId)
    return
  }

  const payload = {
    description: snapshot.description,
    missingByItem: snapshot.missingByItem,
    removedImageIds: snapshot.removedImageIds,
    savedAt: Date.now(),
  }

  try {
    localStorage.setItem(getComplaintDraftStorageKey(orderId), JSON.stringify(payload))
  } catch (error) {
    console.warn('Failed to save complaint draft:', error)
  }
}

const loadComplaintDraft = (orderId) => {
  if (!orderId) return null

  try {
    const raw = localStorage.getItem(getComplaintDraftStorageKey(orderId))
    if (!raw) return null

    const parsed = JSON.parse(raw)
    const savedAt = Number(parsed?.savedAt || 0)

    if (savedAt > 0 && Date.now() - savedAt > COMPLAINT_DRAFT_MAX_AGE_MS) {
      clearComplaintDraft(orderId)
      return null
    }

    return {
      description: String(parsed?.description || ''),
      missingByItem: parsed?.missingByItem || {},
      removedImageIds: Array.isArray(parsed?.removedImageIds) ? parsed.removedImageIds : [],
      savedAt,
    }
  } catch (error) {
    console.warn('Failed to load complaint draft:', error)
    return null
  }
}

const scheduleComplaintDraftSave = () => {
  if (!complaintOrder.value?.id) return

  if (complaintDraftSaveTimer) {
    window.clearTimeout(complaintDraftSaveTimer)
  }

  complaintDraftSaveTimer = window.setTimeout(() => {
    saveComplaintDraft(complaintOrder.value?.id)
  }, 350)
}

const attachComplaintModalGuards = () => {
  if (!complaintModalRef.value || complaintModalHideHandler || complaintModalHiddenHandler) {
    return
  }

  complaintModalHideHandler = async (event) => {
    if (bypassComplaintHideGuard.value || isSavingComplaint.value || !hasComplaintUnsavedChanges.value) {
      return
    }

    event.preventDefault()
    saveComplaintDraft(complaintOrder.value?.id)

    const confirmation = await Swal.fire({
      title: 'Bạn có thay đổi chưa gửi',
      text: 'Dữ liệu đã được lưu nháp. Bạn muốn thoát khỏi màn hình khiếu nại?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Thoát khỏi màn hình',
      cancelButtonText: 'Tiếp tục chỉnh sửa',
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#64748b',
    })

    if (!confirmation.isConfirmed) return

    bypassComplaintHideGuard.value = true
    bsComplaintModal?.hide()
  }

  complaintModalHiddenHandler = () => {
    bypassComplaintHideGuard.value = false
    complaintOrder.value = null
    complaintDescription.value = ''
    complaintMissingByItem.value = {}
    complaintExistingImages.value = []
    complaintRemovedImageIds.value = []
    complaintNewImages.value = []
    complaintInitialState.value = null
    complaintDraftRestoredAt.value = null
  }

  complaintModalRef.value.addEventListener('hide.bs.modal', complaintModalHideHandler)
  complaintModalRef.value.addEventListener('hidden.bs.modal', complaintModalHiddenHandler)
}

const detachComplaintModalGuards = () => {
  if (complaintModalRef.value && complaintModalHideHandler) {
    complaintModalRef.value.removeEventListener('hide.bs.modal', complaintModalHideHandler)
  }
  if (complaintModalRef.value && complaintModalHiddenHandler) {
    complaintModalRef.value.removeEventListener('hidden.bs.modal', complaintModalHiddenHandler)
  }
  complaintModalHideHandler = null
  complaintModalHiddenHandler = null
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
  const orderId = Number(getQueryValue(route.query?.orderId) || 0)
  if (!Number.isFinite(orderId) || orderId <= 0) return

  try {
    const base = await apiClient.get(`/orders/${orderId}`)
    const order = base.data || {}
    const targetType = order.orderType || activeOrderType.value

    activeOrderType.value = targetType
    activeStatus.value = 'ALL'
    currentPage.value = 0
    persistViewState({ orderType: targetType, status: 'ALL', page: 0 })

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

const autoOpenPaymentForNewOrder = async (orderId) => {
  try {
    showStatusToast('Đặt hàng thành công!', 'Vui lòng thanh toán để hoàn tất đơn hàng.')
    selectedPaymentOrderId.value = orderId
    await nextTick()
    if (!bsPaymentModal && paymentModalRef.value) {
      bsPaymentModal = new Modal(paymentModalRef.value, { focus: false })
    }
    bsPaymentModal?.show()
  } catch (error) {
    console.warn('Failed to auto-open payment modal:', error)
  }
}

const handleBeforeUnload = (event) => {
  if (!hasComplaintUnsavedChanges.value) return
  event.preventDefault()
  event.returnValue = ''
}

onMounted(async () => {
  restoreViewState()
  window.addEventListener('beforeunload', handleBeforeUnload)

  const newOrderId = getQueryValue(route.query?.newOrderId)
  const queryOrderType = getQueryValue(route.query?.orderType)
  if (newOrderId && queryOrderType === 'READY_MADE') {
    activeOrderType.value = 'READY_MADE'
    activeStatus.value = 'AWAITING_PAYMENT'
    currentPage.value = 0
    persistViewState({ orderType: 'READY_MADE', status: 'AWAITING_PAYMENT', page: 0 })
  }

  await Promise.all([
    loadOrders(currentPage.value),
    loadStatusCounts(activeOrderType.value),
  ])

  if (newOrderId) {
    const id = Number(newOrderId)
    if (Number.isFinite(id) && id > 0) {
      await autoOpenPaymentForNewOrder(id)
    }

    const nextQuery = { ...route.query }
    delete nextQuery.newOrderId
    delete nextQuery.orderType
    delete nextQuery.orderId
    await router.replace({ path: route.path, query: nextQuery })
  } else {
    await openOrderFromQueryIfPresent()
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
  detachComplaintModalGuards()

  if (complaintDraftSaveTimer) {
    window.clearTimeout(complaintDraftSaveTimer)
    complaintDraftSaveTimer = null
  }
})

const changeOrderType = (key) => {
  activeOrderType.value = key
  activeStatus.value = getDefaultStatusForType(key)
  currentPage.value = 0
  persistViewState({ orderType: key, status: activeStatus.value, page: 0 })
  Promise.all([
    loadOrders(0),
    loadStatusCounts(key),
  ])
}

const changeStatus = (key) => {
  activeStatus.value = key
  currentPage.value = 0
  persistViewState({ status: key, page: 0 })
  loadOrders(0)
}

watch(complaintDescription, () => {
  scheduleComplaintDraftSave()
})

watch(complaintMissingByItem, () => {
  scheduleComplaintDraftSave()
}, { deep: true })

watch(complaintRemovedImageIds, () => {
  scheduleComplaintDraftSave()
}, { deep: true })

const loadStatusCounts = async (orderType) => {
  const safeType = orderType || activeOrderType.value
  const tabs = safeType === 'READY_MADE' ? productStatusTabs : manufacturingStatusTabs
  const counts = createZeroCounts(tabs)

  try {
    if (safeType === 'CUSTOM_MANUFACTURING') {
      const [pendingImportResponse, rejectedImportResponse] = await Promise.all([
        apiClient.get('/orders/imports/my'),
        apiClient.get('/orders/imports/my', {
          params: { status: 'REJECTED' },
        }),
      ])

      const pendingApprovalCount = Array.isArray(pendingImportResponse.data) ? pendingImportResponse.data.length : 0
      const rejectedImportCount = Array.isArray(rejectedImportResponse.data) ? rejectedImportResponse.data.length : 0
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

      counts.CANCELLED = Number(counts.CANCELLED || 0) + rejectedImportCount

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
  const safePage = parsePositiveInt(page, 0)
  isLoading.value = true

  try {
    if (activeOrderType.value === 'CUSTOM_MANUFACTURING' && activeStatus.value === 'PENDING_APPROVAL') {
      const importResponse = await apiClient.get('/orders/imports/my')
      orders.value = (importResponse.data || []).map(o => ({ ...o, isTempImport: true }))
      currentPage.value = 0
      totalPages.value = 1
      persistViewState({ page: 0 })
      return
    }

    if (activeOrderType.value === 'CUSTOM_MANUFACTURING' && activeStatus.value === 'CANCELLED' && safePage === 0) {
      const params = { page: safePage, size: 10, orderType: activeOrderType.value, status: activeStatus.value }
      const [response, rejectedImportResponse] = await Promise.all([
        apiClient.get('/orders/my', { params }),
        apiClient.get('/orders/imports/my', {
          params: { status: 'REJECTED' },
        }),
      ])

      const cancelledOrders = response.data.content || response.data
      const rejectedImports = (rejectedImportResponse.data || []).map(o => ({ ...o, isTempImport: true }))
      const merged = [...rejectedImports, ...(Array.isArray(cancelledOrders) ? cancelledOrders : [])]
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))

      orders.value = merged.map(o => ({ ...o, isTempImport: !!o.isTempImport }))
      currentPage.value = 0
      totalPages.value = 1
      persistViewState({ page: 0 })
      return
    }

    const params = { page: safePage, size: 10, orderType: activeOrderType.value }
    if (activeStatus.value !== 'ALL') params.status = activeStatus.value

    const response = await apiClient.get('/orders/my', { params })
    let merged = response.data.content || response.data

    if (activeOrderType.value === 'CUSTOM_MANUFACTURING' && activeStatus.value === 'ALL' && safePage === 0) {
      const importResponse = await apiClient.get('/orders/imports/my')
      const pendingImports = (importResponse.data || []).map(o => ({ ...o, isTempImport: true }))
      merged = [...pendingImports, ...(Array.isArray(merged) ? merged : [])]
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
    }

    orders.value = Array.isArray(merged) ? merged.map(o => ({ ...o, isTempImport: !!o.isTempImport })) : []
    currentPage.value = parsePositiveInt(response.data.number, safePage)
    totalPages.value = Math.max(1, parsePositiveInt(response.data.totalPages, 1))
    persistViewState({ page: currentPage.value })
  } catch (error) {
    console.error('Failed to load orders:', error)
    Swal.fire('Lỗi', 'Không thể tải danh sách đơn hàng', 'error')
  } finally {
    isLoading.value = false
  }
}

const loadOrderPaymentArtifacts = async (order) => {
  if (!order?.id || order?.isTempImport || order?.orderType !== 'CUSTOM_MANUFACTURING') {
    selectedOrderMilestones.value = []
    selectedOrderContract.value = null
    return
  }

  const [milestonesResult, contractResult] = await Promise.allSettled([
    paymentAPI.getMilestones(order.id),
    contractAPI.getOrderContract(order.id),
  ])

  if (milestonesResult.status === 'fulfilled' && Array.isArray(milestonesResult.value?.milestones)) {
    selectedOrderMilestones.value = [...milestonesResult.value.milestones]
      .sort((a, b) => Number(a.milestoneOrder || 0) - Number(b.milestoneOrder || 0))
  } else {
    selectedOrderMilestones.value = []
  }

  if (contractResult.status === 'fulfilled') {
    selectedOrderContract.value = contractResult.value
  } else {
    selectedOrderContract.value = null
  }
}

const openDetailModal = async (order) => {
  try {
    const detailUrl = order?.isTempImport ? `/orders/imports/${order.id}` : `/orders/${order.id}`
    const response = await apiClient.get(detailUrl)
    selectedOrder.value = { ...response.data, isTempImport: !!order?.isTempImport }
    activeCustomerDetailTab.value = 'materials'
    await loadOrderPaymentArtifacts(selectedOrder.value)
    await nextTick()

    if (!bsModal && detailModalRef.value) {
      bsModal = new Modal(detailModalRef.value, { focus: false })
    }
    bsModal?.show()
    loadOrderHistory(selectedOrder.value)
  } catch (error) {
    console.error('Failed to load order detail:', error)
    Swal.fire('Lỗi', 'Không thể tải chi tiết đơn hàng', 'error')
  }
}

const loadOrderHistory = async (order) => {
  if (!order?.id || order?.isTempImport) {
    orderHistoryEvents.value = []
    orderRevisionSummaries.value = []
    return
  }

  isLoadingOrderHistory.value = true
  try {
    const [history, revisions] = await Promise.all([
      ordersAPI.getOrderHistory(order.id),
      ordersAPI.getOrderRevisions(order.id),
    ])
    orderHistoryEvents.value = Array.isArray(history) ? history : []
    orderRevisionSummaries.value = Array.isArray(revisions) ? revisions : []
  } catch (error) {
    console.warn('Failed to load order history:', error)
    orderHistoryEvents.value = []
    orderRevisionSummaries.value = []
  } finally {
    isLoadingOrderHistory.value = false
  }
}

const getHistoryEventTitle = (event) => {
  if (!event) return 'Cập nhật đơn hàng'

  const fromStatus = event.fromStatus ? getOrderStatusLabel(event.fromStatus, customStatusLabelOverrides) : null
  const toStatus = event.toStatus ? getOrderStatusLabel(event.toStatus, customStatusLabelOverrides) : null

  switch (event.eventType) {
    case 'IMPORT_APPROVED':
      return 'Đã duyệt import dữ liệu đơn'
    case 'IMPORT_REJECTED':
      return 'Đơn import đã bị từ chối'
    case 'ORDER_CANCELLED':
      return 'Đơn hàng đã bị hủy'
    case 'CUSTOMER_CONFIRMED_RECEIVED':
      return 'Khách hàng xác nhận đã nhận hàng'
    case 'QUOTE_SET':
      return 'Đã gửi báo giá'
    case 'PAYMENT_CONFIRMED':
      return 'Đã xác nhận thanh toán'
    case 'AUTO_COMPLETED':
      return 'Hệ thống tự động hoàn thành đơn'
    default:
      if (fromStatus && toStatus) {
        return `Chuyển trạng thái: ${fromStatus} -> ${toStatus}`
      }
      if (toStatus) {
        return `Cập nhật trạng thái: ${toStatus}`
      }
      return 'Cập nhật đơn hàng'
  }
}

const getHistoryActorText = (event) => {
  if (event?.actorName) return event.actorName
  if (event?.actorRole === 'ADMIN') return 'Admin'
  if (event?.actorRole === 'CUSTOMER') return 'Khách hàng'
  return 'Hệ thống'
}

const getHistoryNoteText = (event) => {
  const note = event?.note ? String(event.note).trim() : ''
  if (!note) return ''
  if (event?.eventType === 'IMPORT_REJECTED') {
    return `Lý do từ chối: ${note}`
  }
  if (event?.eventType === 'ORDER_CANCELLED') {
    return `Lý do hủy: ${note}`
  }
  return note
}

const isAdminRejectedOrder = (order) => {
  if (!order || order.status !== 'CANCELLED') return false

  if (order.rejectedByAdmin === true) return true

  const role = String(order.cancelledByRole || '').trim().toUpperCase()
  return role === 'ADMIN'
}

const isCancelEventType = (eventType) => {
  return eventType === 'ORDER_CANCELLED' || eventType === 'IMPORT_REJECTED'
}

const historyEventMetaById = computed(() => {
  const source = Array.isArray(orderHistoryEvents.value) ? [...orderHistoryEvents.value] : []
  source.sort((a, b) => {
    const ta = a?.createdAt ? new Date(a.createdAt).getTime() : 0
    const tb = b?.createdAt ? new Date(b.createdAt).getTime() : 0
    if (ta !== tb) return ta - tb
    return Number(a?.id || 0) - Number(b?.id || 0)
  })

  let cancelCount = 0
  const result = {}

  for (const event of source) {
    const eventId = event?.id
    if (!eventId) continue

    const eventType = event?.eventType
    const isCancelEvent = isCancelEventType(eventType)

    if (isCancelEvent) cancelCount += 1

    result[eventId] = {
      cancelNo: isCancelEvent ? cancelCount : null,
    }
  }

  return result
})

const getHistoryMeta = (event) => {
  if (!event?.id) return {}
  return historyEventMetaById.value[event.id] || {}
}

const getCancelSequence = (event) => {
  const value = getHistoryMeta(event).cancelNo
  return Number.isFinite(value) ? value : null
}

const totalCancelAttempts = computed(() => {
  return (orderHistoryEvents.value || []).filter((event) => isCancelEventType(event?.eventType)).length
})

const sortedOrderHistoryEvents = computed(() => {
  const source = Array.isArray(orderHistoryEvents.value) ? [...orderHistoryEvents.value] : []
  source.sort((a, b) => {
    const ta = a?.createdAt ? new Date(a.createdAt).getTime() : 0
    const tb = b?.createdAt ? new Date(b.createdAt).getTime() : 0
    if (ta !== tb) return tb - ta
    return Number(b?.id || 0) - Number(a?.id || 0)
  })
  return source
})

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

const getReviewStatusText = (status) => {
  return getReviewStatusLabel(status)
}

const getReviewBadgeClass = (status) => {
  const map = {
    PENDING_REVIEW: 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25',
    APPROVED: 'bg-success bg-opacity-10 text-success border border-success border-opacity-25',
    REJECTED: 'bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25',
    NEED_DISCUSSION: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
  }
  return map[status] || 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25'
}

const getItemRowClass = (item) => {
  const map = {
    APPROVED: '',
    REJECTED: 'bg-danger bg-opacity-10',
    NEED_DISCUSSION: 'bg-warning bg-opacity-10',
  }
  const baseClass = map[item?.reviewStatus] || ''
  return hasAnyItemNote(item) ? `${baseClass} row-has-note` : baseClass
}

const hasCustomerItemNote = (item) => {
  const note = item?.notes ? String(item.notes).trim() : ''
  return !!note
}

const hasAdminItemNote = (item) => {
  const note = item?.adminNote ? String(item.adminNote).trim() : ''
  return !!note
}

const hasAnyItemNote = (item) => {
  return hasCustomerItemNote(item) || hasAdminItemNote(item)
}

const preserveItemOrder = (previousItems = [], incomingItems = []) => {
  if (!Array.isArray(incomingItems)) return []
  if (!Array.isArray(previousItems) || previousItems.length === 0) return incomingItems

  const indexById = new Map()
  previousItems.forEach((item, index) => {
    if (item?.id != null) indexById.set(item.id, index)
  })

  return [...incomingItems].sort((a, b) => {
    const indexA = indexById.has(a?.id) ? indexById.get(a.id) : Number.MAX_SAFE_INTEGER
    const indexB = indexById.has(b?.id) ? indexById.get(b.id) : Number.MAX_SAFE_INTEGER
    if (indexA !== indexB) return indexA - indexB
    return Number(a?.id || 0) - Number(b?.id || 0)
  })
}

const getReviewProgress = (order) => {
  if (!order?.items?.length) return '0/0'
  const reviewed = order.items.filter((item) => item.reviewStatus && item.reviewStatus !== 'PENDING_REVIEW').length
  return `${reviewed}/${order.items.length}`
}

const escapeHtml = (value) => {
  if (value == null) return ''
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

const syncOrderAfterItemNoteUpdate = (updatedOrder) => {
  if (!updatedOrder?.id || !selectedOrder.value?.id) return

  const currentOrderId = selectedOrder.value.id
  const wasTempImport = !!selectedOrder.value.isTempImport
  const previousItems = selectedOrder.value.items || []

  const normalizedUpdatedOrder = {
    ...updatedOrder,
    items: preserveItemOrder(previousItems, updatedOrder.items || []),
    isTempImport: wasTempImport,
  }

  selectedOrder.value = normalizedUpdatedOrder

  const orderIndex = orders.value.findIndex((order) => {
    return order.id === currentOrderId && !!order.isTempImport === wasTempImport
  })

  if (orderIndex !== -1) {
    orders.value[orderIndex] = {
      ...orders.value[orderIndex],
      ...normalizedUpdatedOrder,
    }
  }
}

const openCustomerItemNoteModal = async (item) => {
  if (!selectedOrder.value?.id || !item?.id) return

  if (selectedOrder.value.isTempImport) {
    await Swal.fire('Chưa hỗ trợ', 'Đơn import chờ duyệt chưa thể cập nhật ghi chú theo sản phẩm.', 'info')
    return
  }

  const orderId = selectedOrder.value.id
  const adminNote = item?.adminNote ? String(item.adminNote).trim() : ''
  const currentCustomerNote = item?.notes ? String(item.notes).trim() : ''
  const itemName = item?.itemName ? String(item.itemName).trim() : 'Sản phẩm'

  const adminNoteBlock = adminNote
    ? `<div class="alert alert-warning text-start py-2 px-3 mb-3"><div class="fw-semibold mb-1">Ghi chú từ admin</div><div style="white-space: pre-wrap; word-break: break-word;">${escapeHtml(adminNote)}</div></div>`
    : '<div class="text-muted small text-start mb-3">Chưa có ghi chú từ admin cho sản phẩm này.</div>'

  const result = await Swal.fire({
    title: `Ghi chú - ${itemName}`,
    html: `
      ${adminNoteBlock}
      <label for="customer-item-note-input" class="form-label fw-semibold text-start w-100 mb-1">Ghi chú của bạn</label>
      <textarea id="customer-item-note-input" class="swal2-textarea" style="display:block;width:100%;min-height:130px;margin:0;" maxlength="2000" placeholder="Nhập ghi chú cho sản phẩm này...">${escapeHtml(currentCustomerNote)}</textarea>
    `,
    focusConfirm: false,
    showCancelButton: true,
    confirmButtonText: 'Lưu ghi chú',
    cancelButtonText: 'Đóng',
    confirmButtonColor: '#0d6efd',
    preConfirm: () => {
      const input = document.getElementById('customer-item-note-input')
      if (!input) return ''
      const nextValue = String(input.value || '')
      if (nextValue.trim().length > 2000) {
        Swal.showValidationMessage('Ghi chú không được vượt quá 2000 ký tự')
        return false
      }
      return nextValue
    },
  })

  if (!result.isConfirmed) return

  const nextCustomerNote = String(result.value || '').trim()
  if (nextCustomerNote === currentCustomerNote) return

  try {
    const updatedOrder = await ordersAPI.updateItemNotes(orderId, item.id, nextCustomerNote || null)
    syncOrderAfterItemNoteUpdate(updatedOrder)
    await Swal.fire({
      icon: 'success',
      title: 'Đã lưu ghi chú',
      timer: 1600,
      showConfirmButton: false,
    })
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể lưu ghi chú sản phẩm'
    await Swal.fire('Lỗi', msg, 'error')
  }
}

const openPaymentModal = async (order) => {
  selectedPaymentOrderId.value = order.id
  await nextTick()
  if (!bsPaymentModal && paymentModalRef.value) {
    bsPaymentModal = new Modal(paymentModalRef.value, { focus: false })
  }
  bsPaymentModal?.show()
}

const openContractModal = async (order) => {
  if (!order?.id || order?.isTempImport) {
    return
  }

  selectedContractOrder.value = order
  contractLoading.value = true
  selectedContract.value = null

  try {
    const contract = await contractAPI.getOrderContract(order.id)
    selectedContract.value = contract
    selectedOrderContract.value = contract

    if (Array.isArray(contract?.milestones)) {
      selectedOrderMilestones.value = [...contract.milestones]
        .sort((a, b) => Number(a.milestoneOrder || 0) - Number(b.milestoneOrder || 0))
    }

    await nextTick()
    if (!bsContractModal && contractModalRef.value) {
      bsContractModal = new Modal(contractModalRef.value, { focus: false })
    }
    bsContractModal?.show()
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể tải hợp đồng của đơn hàng'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    contractLoading.value = false
  }
}

const confirmSelectedContract = async () => {
  if (!selectedContract.value?.id || contractSubmitting.value) return

  const result = await Swal.fire({
    title: 'Xác nhận hợp đồng?',
    text: 'Sau khi xác nhận, hệ thống sẽ mở mốc thanh toán đầu tiên (60%).',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#198754',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận hợp đồng',
    cancelButtonText: 'Đóng',
  })
  if (!result.isConfirmed) return

  contractSubmitting.value = true
  try {
    await contractAPI.confirm(selectedContract.value.id)
    bsContractModal?.hide()

    await Promise.all([
      loadOrders(currentPage.value),
      loadStatusCounts(activeOrderType.value),
    ])

    if (selectedOrder.value?.id === selectedContractOrder.value?.id) {
      const detailResponse = await apiClient.get(`/orders/${selectedOrder.value.id}`)
      selectedOrder.value = { ...detailResponse.data, isTempImport: !!selectedOrder.value?.isTempImport }
      await loadOrderPaymentArtifacts(selectedOrder.value)
    }

    await showStatusToast('Đã xác nhận hợp đồng', 'Bạn có thể tiến hành thanh toán mốc đầu tiên.')
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể xác nhận hợp đồng'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    contractSubmitting.value = false
  }
}

const rejectSelectedContract = async () => {
  if (!selectedContract.value?.id || contractSubmitting.value) return

  const result = await Swal.fire({
    title: 'Từ chối hợp đồng?',
    input: 'textarea',
    inputLabel: 'Lý do từ chối',
    inputPlaceholder: 'Nhập lý do để admin cập nhật lại điều khoản...',
    inputValidator: (value) => {
      if (!value || !value.trim()) return 'Vui lòng nhập lý do từ chối'
      return null
    },
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Từ chối hợp đồng',
    cancelButtonText: 'Hủy',
  })
  if (!result.isConfirmed) return

  contractSubmitting.value = true
  try {
    await contractAPI.reject(selectedContract.value.id, String(result.value || '').trim())
    bsContractModal?.hide()

    await Promise.all([
      loadOrders(currentPage.value),
      loadStatusCounts(activeOrderType.value),
    ])

    if (selectedOrder.value?.id === selectedContractOrder.value?.id) {
      const detailResponse = await apiClient.get(`/orders/${selectedOrder.value.id}`)
      selectedOrder.value = { ...detailResponse.data, isTempImport: !!selectedOrder.value?.isTempImport }
      await loadOrderPaymentArtifacts(selectedOrder.value)
    }

    await showStatusToast('Đã từ chối hợp đồng', 'Đơn hàng đã quay lại trạng thái chờ báo giá.')
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể từ chối hợp đồng'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    contractSubmitting.value = false
  }
}

const onPaymentConfirmed = (paymentInfo) => {
  // Reload danh sách để cập nhật trạng thái
  Promise.all([
    loadOrders(currentPage.value),
    loadStatusCounts(activeOrderType.value),
  ])
  const successMessage = paymentInfo.orderStatus === 'AWAITING_DELIVERY'
    ? `Đơn hàng ${paymentInfo.orderNumber} đã được xác nhận thanh toán đầy đủ.`
    : `Đơn hàng ${paymentInfo.orderNumber} đã được cập nhật trạng thái thanh toán.`
  showStatusToast('Đã cập nhật thanh toán', successMessage)
}

const canCancelOrder = (order) => {
  if (!order) return false
  return order.status === 'PENDING_APPROVAL'
    || order.status === 'PENDING_QUOTE'
    || order.status === 'AWAITING_CONTRACT'
    || order.status === 'AWAITING_PAYMENT'
}

const hasComplaintForOrder = (orderId) => {
  return !!complaintExistsByOrderId.value[orderId]
}

const confirmReceivedOrder = async (order) => {
  if (!order?.id || isActionLocked(order, 'confirm')) return

  const result = await Swal.fire({
    title: 'Xác nhận đã nhận hàng?',
    text: `Bạn xác nhận đã nhận đủ hàng cho đơn ${order.orderNumber}?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#16a34a',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Đã nhận đủ',
    cancelButtonText: 'Để sau',
  })

  if (!result.isConfirmed) return

  setActionLocked(order, 'confirm', true)
  try {
    await ordersAPI.confirmReceived(order.id)
    await Promise.all([
      loadOrders(currentPage.value),
      loadStatusCounts(activeOrderType.value),
    ])
    await showStatusToast('Đã xác nhận nhận hàng', `Đơn ${order.orderNumber} đã được chuyển sang hoàn thành.`)
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể xác nhận nhận hàng'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    setActionLocked(order, 'confirm', false)
  }
}

const openComplaintModal = async (order) => {
  if (!order?.id || isActionLocked(order, 'complaint')) return

  if (order.status !== 'SHIPPING' && order.status !== 'COMPLETED') {
    await Swal.fire('Không hợp lệ', 'Chỉ có thể khiếu nại khi đơn đang giao hoặc đã hoàn thành.', 'warning')
    return
  }

  setActionLocked(order, 'complaint', true)
  try {
    const [detailResponse, complaintData] = await Promise.all([
      apiClient.get(`/orders/${order.id}`),
      ordersAPI.getMyComplaint(order.id),
    ])

    complaintOrder.value = detailResponse.data || order
    const existingComplaint = complaintData || null

    complaintExistsByOrderId.value = {
      ...complaintExistsByOrderId.value,
      [order.id]: !!existingComplaint,
    }

    complaintDescription.value = existingComplaint?.description || ''
    complaintType.value = existingComplaint?.type || 'MISSING_ITEM'
    complaintStatus.value = existingComplaint?.status || 'OPEN'
    complaintAdminNote.value = existingComplaint?.adminNote || ''
    complaintHistory.value = existingComplaint?.history || []

    complaintExistingImages.value = Array.isArray(existingComplaint?.images) ? existingComplaint.images : []
    complaintRemovedImageIds.value = []
    complaintNewImages.value = []

    const missingMap = {}
    const defectiveMap = {}
    const reasonMap = {}
    ;(complaintOrder.value?.items || []).forEach((item) => {
      missingMap[item.id] = 0
      defectiveMap[item.id] = 0
      reasonMap[item.id] = ''
    })
    ;(existingComplaint?.missingItems || []).forEach((item) => {
      if (item?.orderItemId != null) {
        missingMap[item.orderItemId] = Number(item.missingQuantity || 0)
        defectiveMap[item.orderItemId] = Number(item.defectiveQuantity || 0)
        reasonMap[item.orderItemId] = item.reasonNote || ''
      }
    })
    complaintMissingByItem.value = missingMap
    complaintDefectiveByItem.value = defectiveMap
    complaintReasonByItem.value = reasonMap

    const isComplaintReadOnly = existingComplaint?.status === 'IN_REVIEW'

    setComplaintInitialState()

    await nextTick()
    if (!bsComplaintModal && complaintModalRef.value) {
      bsComplaintModal = new Modal(complaintModalRef.value, { focus: false })
    }

    attachComplaintModalGuards()
    bsComplaintModal?.show()
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể mở form khiếu nại'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    setActionLocked(order, 'complaint', false)
  }
}

const toggleKeepExistingComplaintImage = (imageId) => {
  const current = complaintRemovedImageIds.value || []
  if (current.includes(imageId)) {
    complaintRemovedImageIds.value = current.filter((id) => id !== imageId)
    return
  }
  complaintRemovedImageIds.value = [...current, imageId]
}

const removeNewComplaintImage = (index) => {
  complaintNewImages.value = complaintNewImages.value.filter((_, idx) => idx !== index)
}

const onComplaintImagesSelected = (event) => {
  const files = Array.from(event.target?.files || [])
  if (files.length === 0) return

  const keptExisting = complaintExistingImages.value
    .filter((img) => !complaintRemovedImageIds.value.includes(img.id))
    .length
  const currentNew = complaintNewImages.value.length
  const availableSlots = Math.max(0, 5 - keptExisting - currentNew)

  if (availableSlots <= 0) {
    Swal.fire('Giới hạn ảnh', 'Bạn chỉ có thể lưu tối đa 5 ảnh cho mỗi khiếu nại.', 'warning')
    event.target.value = ''
    return
  }

  const accepted = files.slice(0, availableSlots)
  complaintNewImages.value = [...complaintNewImages.value, ...accepted]

  if (accepted.length < files.length) {
    Swal.fire('Giới hạn ảnh', `Chỉ nhận thêm ${availableSlots} ảnh.`, 'info')
  }

  event.target.value = ''
}

const submitComplaint = async () => {
  if (!complaintOrder.value?.id || isSavingComplaint.value) return

  const normalizedDescription = (complaintDescription.value || '').trim()
  if (!normalizedDescription) {
    Swal.fire('Thiếu thông tin', 'Vui lòng nhập mô tả khiếu nại.', 'warning')
    return
  }

  const missingItems = (complaintOrder.value.items || [])
    .map((item) => {
      const missingRaw = Number(complaintMissingByItem.value[item.id] || 0)
      const defectiveRaw = Number(complaintDefectiveByItem.value[item.id] || 0)
      
      const missingQty = complaintType.value === 'DEFECTIVE_ITEM' ? 0 : missingRaw
      const defectiveQty = complaintType.value === 'MISSING_ITEM' ? 0 : defectiveRaw

      return {
        orderItemId: item.id,
        missingQuantity: missingQty,
        defectiveQuantity: defectiveQty,
        reasonNote: complaintReasonByItem.value[item.id] || ''
      }
    })
    .filter((item) => item.missingQuantity !== 0 || item.defectiveQuantity !== 0)

  const hasNegative = missingItems.some((item) => item.missingQuantity < 0 || item.defectiveQuantity < 0)
  if (hasNegative) {
    Swal.fire('Dữ liệu không hợp lệ', 'Số lượng lỗi hoặc thiếu không được là số âm.', 'warning')
    return
  }

  if (missingItems.length === 0) {
    Swal.fire('Thiếu dữ liệu', 'Vui lòng nhập số lượng lỗi/thiếu cho ít nhất 1 sản phẩm.', 'warning')
    return
  }

  const keepImageIds = complaintExistingImages.value
    .filter((img) => !complaintRemovedImageIds.value.includes(img.id))
    .map((img) => img.id)
  const totalImages = keepImageIds.length + complaintNewImages.value.length
  if (totalImages < 1) {
    Swal.fire('Thiếu ảnh minh chứng', 'Khiếu nại cần tối thiểu 1 ảnh minh chứng.', 'warning')
    return
  }

  const formData = new FormData()
  formData.append('type', complaintType.value)
  formData.append('description', normalizedDescription)
  formData.append('missingItems', JSON.stringify(missingItems))
  keepImageIds.forEach((id) => formData.append('keepImageIds', String(id)))
  complaintNewImages.value.forEach((file) => formData.append('images', file))

  const wasExistingComplaint = hasComplaintForOrder(complaintOrder.value.id)
  isSavingComplaint.value = true
  try {
    await ordersAPI.upsertMyComplaint(complaintOrder.value.id, formData)

    complaintExistsByOrderId.value = {
      ...complaintExistsByOrderId.value,
      [complaintOrder.value.id]: true,
    }

    bypassComplaintHideGuard.value = true
    bsComplaintModal?.hide()
    await Promise.all([
      loadOrders(currentPage.value),
      loadStatusCounts(activeOrderType.value),
    ])
    await Swal.fire({
      icon: 'success',
      title: wasExistingComplaint ? 'Đã cập nhật khiếu nại' : 'Đã gửi khiếu nại',
      text: 'Hệ thống đã ghi nhận khiếu nại của bạn.',
      timer: 2200,
      showConfirmButton: false,
    })
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể gửi khiếu nại'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    isSavingComplaint.value = false
  }
}

const cancelOrder = async (order) => {
  if (!order?.id || isActionLocked(order, 'cancel')) return

  const result = await Swal.fire({
    title: 'Hủy đơn hàng?',
    text: `Bạn có chắc muốn hủy đơn ${order.orderNumber}? Hành động này không thể hoàn tác.`,
    input: 'textarea',
    inputLabel: 'Lý do hủy',
    inputPlaceholder: 'Vui lòng nhập lý do hủy đơn hàng',
    inputAttributes: {
      'aria-label': 'Lý do hủy đơn hàng',
      maxlength: '500',
    },
    inputValidator: (value) => {
      if (!value || !value.trim()) {
        return 'Vui lòng nhập lý do hủy đơn hàng'
      }
      return null
    },
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Hủy đơn',
    cancelButtonText: 'Giữ lại',
  })

  if (!result.isConfirmed) return

  const cancelReason = (result.value || '').trim()
  const confirmCode = String(order.orderNumber || order.importCode || order.id).trim()

  const finalConfirmation = await Swal.fire({
    title: 'Xác nhận lần cuối',
    text: `Để tránh hủy nhầm, vui lòng nhập chính xác mã đơn: ${confirmCode}`,
    input: 'text',
    inputPlaceholder: `Nhập ${confirmCode}`,
    inputValidator: (value) => {
      if (!value || value.trim() !== confirmCode) {
        return 'Mã đơn không khớp'
      }
      return null
    },
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận hủy',
    cancelButtonText: 'Quay lại',
  })

  if (!finalConfirmation.isConfirmed) return

  setActionLocked(order, 'cancel', true)
  try {
    if (order?.isTempImport) {
      await apiClient.put(`/orders/imports/${order.id}/cancel`, { reason: cancelReason })
    } else {
      await apiClient.put(`/orders/${order.id}/cancel`, { reason: cancelReason })
    }

    await loadOrders(currentPage.value)
    await loadStatusCounts(activeOrderType.value)

    if (selectedOrder.value?.id === order.id) {
      bsModal?.hide()
    }

    showStatusToast('Đã hủy đơn hàng', `Đơn ${order.orderNumber} đã được hủy.`)
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể hủy đơn hàng'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    setActionLocked(order, 'cancel', false)
  }
}

// Giữ lại để tương thích nhưng redirect sang openPaymentModal
const showPaymentQR = (order) => {
  openPaymentModal(order)
}

const getCustomerPaymentActionLabel = (order) => {
  if (!order) return 'Thanh toán'
  if (order.orderType !== 'CUSTOM_MANUFACTURING') {
    return order.status === 'DEPOSITED' ? 'Đã TT ✔' : 'Thanh toán'
  }

  if (order.status === 'AWAITING_REMAINING_PAYMENT') return 'TT mốc 2 (40%)'
  if (order.status === 'AWAITING_PAYMENT') return 'TT mốc 1 (60%)'
  if (order.status === 'DEPOSITED') return 'Đã cọc ✔'
  return 'Xem thanh toán'
}

const readyMadeStatusLabelOverrides = {
  DEPOSITED: 'Đã thanh toán ✔',
  PROCESSING: 'Đang chuẩn bị',
  COMPLETED: 'Đã nhận được hàng',
}

const customStatusLabelOverrides = {
  AWAITING_CONTRACT: 'Chờ xác nhận hợp đồng',
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
    PENDING_APPROVAL: 'badge bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
    PENDING_QUOTE: 'badge bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
    AWAITING_CONTRACT: 'badge bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    AWAITING_PAYMENT: 'badge bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    DEPOSITED: 'badge bg-success bg-opacity-10 text-success border border-success border-opacity-25',
    PROCESSING: 'badge bg-navy text-white border border-navy',
    AWAITING_REMAINING_PAYMENT: 'badge bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
    AWAITING_DELIVERY: 'badge bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    SHIPPING: 'badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25',
    COMPLETED: 'badge bg-success bg-opacity-10 text-success border border-success border-opacity-25',
    CANCELLED: 'badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25'
  }
  return classMap[status] || 'badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25'
}

const getContractStatusText = (status) => {
  const map = {
    DRAFT: 'Bản nháp',
    PENDING_CONFIRMATION: 'Chờ xác nhận',
    CONFIRMED: 'Đã xác nhận',
    REJECTED: 'Đã từ chối',
    CANCELLED: 'Đã hủy',
  }
  return map[String(status || '').toUpperCase()] || 'Không xác định'
}

const getContractStatusClass = (status) => {
  const map = {
    DRAFT: 'text-bg-secondary',
    PENDING_CONFIRMATION: 'text-bg-warning',
    CONFIRMED: 'text-bg-success',
    REJECTED: 'text-bg-danger',
    CANCELLED: 'text-bg-dark',
  }
  return map[String(status || '').toUpperCase()] || 'text-bg-secondary'
}

const getMilestoneStatusText = (status) => {
  const map = {
    PENDING: 'Chưa kích hoạt',
    ACTIVE: 'Đang chờ thanh toán',
    PAID_UNVERIFIED: 'Đã chuyển khoản - chờ duyệt',
    PAID: 'Đã thanh toán',
    OVERDUE: 'Quá hạn',
    CANCELLED: 'Đã hủy',
  }
  return map[String(status || '').toUpperCase()] || String(status || 'Không rõ')
}

const getMilestoneBadgeClass = (status) => {
  const map = {
    PENDING: 'text-bg-secondary',
    ACTIVE: 'text-bg-info',
    PAID_UNVERIFIED: 'text-bg-warning',
    PAID: 'text-bg-success',
    OVERDUE: 'text-bg-danger',
    CANCELLED: 'text-bg-dark',
  }
  return map[String(status || '').toUpperCase()] || 'text-bg-secondary'
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
      bsPaymentModal = new Modal(paymentModalRef.value, { focus: false })
    }
    bsPaymentModal?.show()
  })
}

// ── Pagination helper ──
const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value + 1
  const pages = []
  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (current > 3) pages.push('...')
    for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) pages.push(i)
    if (current < total - 2) pages.push('...')
    pages.push(total)
  }
  return pages
})

// ── Date helpers ──
const formatDateShort = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
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

.safety-note {
  display: flex;
  align-items: center;
  padding: 9px 12px;
  border-radius: 10px;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: linear-gradient(90deg, rgba(239, 246, 255, 0.95), rgba(247, 250, 255, 0.95));
  color: #1e3a8a;
  font-size: 0.84rem;
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

.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.border-navy { border-color: #0b2e59 !important; }

.modal-xl {
  max-width: 96vw;
}

.modal {
  overflow-y: scroll;
}

.btn-navy {
  background-color: #0b2e59;
  color: #fff;
  border: none;
}

.btn-navy:hover {
  background-color: #173b6c;
  color: #fff;
}

.review-table th,
.review-table td {
  vertical-align: middle;
}

.review-table thead th {
  white-space: nowrap;
  font-size: 0.78rem;
  border-bottom: 2px solid #e2e8f0;
}

.review-table tbody td {
  font-size: 0.84rem;
  border-bottom: 1px solid #f1f5f9;
}

.review-table tbody tr.row-has-note > td {
  background-image: linear-gradient(0deg, rgba(245, 158, 11, 0.12), rgba(245, 158, 11, 0.12));
  border-top-color: rgba(245, 158, 11, 0.45);
  border-bottom-color: rgba(245, 158, 11, 0.45);
}

.review-table tbody tr.row-has-note > td:first-child {
  box-shadow: inset 4px 0 0 #f59e0b;
}

.review-table-comfortable td {
  padding: 0.75rem 0.5rem;
}

.review-table-compact td {
  padding: 0.4rem 0.35rem;
}

.review-table-compact tbody td {
  font-size: 0.78rem;
}

:deep(.status-toast-popup) {
  border-radius: 12px;
  border: 1px solid #cfe8d8;
  background: linear-gradient(135deg, #f7fffa 0%, #ecfff4 100%);
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.14);
  min-width: 320px;
}

:deep(.status-toast-title) {
  font-weight: 700;
  color: #14532d;
}

:deep(.status-toast-text) {
  color: #166534;
  font-size: 0.85rem;
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

.item-note-btn {
  white-space: nowrap;
}

.note-quick-btn {
  max-width: 100%;
}

.order-summary-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.9rem 1rem;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.contract-overview-wrap {
  display: flex;
  justify-content: center;
}

.contract-overview-card {
  width: min(100%, 980px);
  border: 1px solid #f8d7a4;
  background: linear-gradient(180deg, #fffdf7 0%, #fff7e6 100%);
  border-radius: 12px;
  padding: 0.85rem;
}

.milestone-mini-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 0.55rem;
}

.milestone-mini-item {
  border: 1px solid #f1dfb7;
  background: #ffffff;
  border-radius: 10px;
  padding: 0.55rem 0.65rem;
}

.modal-section-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.modal-tab-btn {
  border: 1px solid #cbd5e1;
  background: #ffffff;
  color: #334155;
  border-radius: 999px;
  padding: 0.45rem 0.9rem;
  font-size: 0.86rem;
  font-weight: 600;
  transition: all 0.2s ease;
}

.modal-tab-btn:hover {
  border-color: #93c5fd;
  color: #1d4ed8;
  background: #eff6ff;
}

.modal-tab-active {
  border-color: #2563eb;
  color: #1e3a8a;
  background: #dbeafe;
  box-shadow: inset 0 0 0 1px #93c5fd;
}

.tab-panel {
  border-radius: 12px;
  border: 1px solid #dbe3ef;
  padding: 0.85rem;
}

.tab-panel-history {
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
}

.tab-panel-materials {
  background: #ffffff;
}

.history-empty-state {
  border: 1px dashed #cbd5e1;
  border-radius: 10px;
  padding: 0.65rem 0.8rem;
  color: #64748b;
  background: #f8fafc;
  font-size: 0.86rem;
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

.complaint-image-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
  font-size: 0.8rem;
}

.complaint-image-tag.removed {
  opacity: 0.6;
  text-decoration: line-through;
}

.order-history-list {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  max-height: 340px;
  overflow-y: auto;
}

.order-history-item {
  padding: 10px 12px;
  border-bottom: 1px dashed #e2e8f0;
}

.order-history-item:last-child {
  border-bottom: none;
}

.history-timeline .order-history-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.history-bullet {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #2563eb;
  margin-top: 7px;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.18);
  flex-shrink: 0;
}

.history-content {
  flex: 1;
  min-width: 0;
}

.modal-tab-fade-enter-active,
.modal-tab-fade-leave-active {
  transition: opacity 0.2s ease;
}

.modal-tab-fade-enter-from,
.modal-tab-fade-leave-to {
  opacity: 0;
}

/* ===== NEW TABLE DESIGN STYLES ===== */

/* Page Layout */
.myorders-page {
  min-height: 100vh;
  background: #f0f4f8;
}

/* Hero Section */
.orders-hero {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 3rem 0 2.5rem;
  border-bottom: 4px solid #f59e0b;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.hero-title {
  font-size: 2rem;
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.5px;
}

.hero-desc {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.95rem;
  max-width: 600px;
  margin: 0;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn-hero-primary {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  color: #0f172a;
  font-weight: 700;
  padding: 0.6rem 1.4rem;
  border-radius: 8px;
  border: none;
  display: inline-flex;
  align-items: center;
  text-decoration: none;
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.35);
  transition: all 0.25s ease;
}

.btn-hero-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.5);
  color: #0f172a;
}

/* Content Area */
.orders-content {
  padding-top: 2rem;
  padding-bottom: 3rem;
}

/* Status Filter Pills */
.status-filter-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 15px;
  border: 1.5px solid #cbd5e1;
  background: #ffffff;
  color: #475569;
  font-size: 0.88rem;
  font-weight: 500;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.status-pill:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #eff6ff;
}

.status-pill.active {
  background: #1e3a8a;
  color: white;
  border-color: #1e3a8a;
  box-shadow: 0 2px 8px rgba(30, 58, 138, 0.3);
}

.pill-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 5px;
  border-radius: 999px;
  font-size: 0.72rem;
  font-weight: 700;
  background: rgba(0, 0, 0, 0.12);
  color: currentColor;
}

.status-pill.active .pill-count {
  background: rgba(255, 255, 255, 0.2);
}

/* Orders Table Card */
.orders-table-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.orders-table-header {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-bottom: 2px solid #e2e8f0;
  padding: 0;
}

.col-header {
  padding: 14px 20px;
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

/* Order Table Rows */
.order-table-row {
  display: flex;
  align-items: flex-start;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s ease;
  padding: 0;
}

.order-table-row:last-child {
  border-bottom: none;
}

.order-table-row:hover {
  background: #f8faff;
}

.row-cell {
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* Number Cell */
.order-number-text {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1e3a8a;
  letter-spacing: 0.3px;
}

.order-items-count {
  font-size: 0.8rem;
  color: #94a3b8;
  margin-top: 3px;
}

/* Mini alerts inside table rows */
.mini-alert {
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
  width: fit-content;
  max-width: 100%;
  font-size: 0.75rem;
  padding: 3px 8px;
  border-radius: 6px;
  line-height: 1.4;
  white-space: normal;
  word-break: break-word;
}

.mini-alert-warning {
  background: rgba(245, 158, 11, 0.12);
  color: #92400e;
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.mini-alert-danger {
  background: rgba(239, 68, 68, 0.1);
  color: #991b1b;
  border: 1px solid rgba(239, 68, 68, 0.25);
}

.mini-alert-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

/* Status badge in table */
.status-badge-lg {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  align-self: flex-start;
  width: fit-content;
  max-width: 100%;
  padding: 5px 12px;
  font-size: 0.78rem;
  font-weight: 600;
  border-radius: 6px;
  letter-spacing: 0.3px;
  line-height: 1.2;
  white-space: nowrap;
}

.status-cell {
  align-items: flex-start;
  justify-content: flex-start;
}

/* Date Cell */
.date-primary {
  font-size: 0.9rem;
  font-weight: 600;
  color: #334155;
}

.date-secondary {
  font-size: 0.78rem;
  color: #94a3b8;
  margin-top: 2px;
}

.delivery-cell {
  align-items: flex-start;
}

.delivery-primary {
  font-size: 0.78rem;
  line-height: 1.3;
  font-weight: 500;
}

/* Price Cell */
.price-total {
  font-size: 1rem;
  font-weight: 700;
  color: #1e3a8a;
}

.price-sub {
  font-size: 0.78rem;
  color: #64748b;
}

/* Action Cell */
.action-cell {
  align-items: flex-start;
}

.action-link-btn {
  display: inline-flex;
  align-items: center;
  font-size: 0.82rem;
  font-weight: 600;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s ease;
  line-height: 1.4;
}

.primary-action { color: #2563eb; }
.primary-action:hover { color: #1e40af; }
.danger-action { color: #dc2626; }
.danger-action:hover { color: #991b1b; }
.success-action { color: #16a34a; }
.success-action:hover { color: #14532d; }
.warning-action { color: #d97706; }
.warning-action:hover { color: #92400e; }

.action-link-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Skeleton loading for table */
.skeleton-row .sk {
  background: #e2e8f0;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
}

.skeleton-row .sk::after {
  content: "";
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.6) 50%, transparent 100%);
  animation: shimmer 1.5s infinite;
}

.sk-title { height: 18px; width: 80%; }
.sk-sub { height: 12px; width: 50%; }
.sk-badge { height: 24px; width: 80px; border-radius: 6px; }
.sk-line { height: 14px; width: 70%; }
.sk-btn { height: 20px; width: 90px; }

/* Empty State Card */
.empty-state-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
  padding: 4rem 2rem;
  text-align: center;
}

.empty-icon {
  font-size: 3.5rem;
  color: #cbd5e1;
  display: block;
  margin-bottom: 1rem;
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #334155;
  margin-bottom: 0.5rem;
}

.empty-desc {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

/* Pagination */
.table-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
  background: #f8fafc;
  flex-wrap: wrap;
  gap: 8px;
}

.pagination-info {
  font-size: 0.84rem;
  color: #64748b;
}

.pagination-controls {
  display: flex;
  gap: 4px;
  align-items: center;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 8px;
  border: 1.5px solid #e2e8f0;
  background: #ffffff;
  color: #475569;
  font-size: 0.88rem;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #eff6ff;
}

.page-btn.active {
  background: #1e3a8a;
  border-color: #1e3a8a;
  color: white;
  box-shadow: 0 2px 8px rgba(30, 58, 138, 0.3);
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.ellipsis-btn {
  border: none;
  background: transparent;
  cursor: default;
}

</style>