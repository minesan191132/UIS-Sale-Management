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

    <div class="safety-note mb-3">
      <i class="bi bi-shield-check me-2"></i>
      Bộ lọc đang xem được tự lưu. Khiếu nại chưa gửi sẽ được lưu nháp để tránh mất dữ liệu.
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
                  <div
                    v-else-if="order.status === 'CANCELLED' && isAdminRejectedOrder(order)"
                    class="alert alert-danger mt-2 mb-0 py-2 px-3 small"
                  >
                    <i class="bi bi-shield-x me-1"></i>
                    Đơn bị admin từ chối.
                    <span v-if="order.cancelReason">Lý do: {{ order.cancelReason }}</span>
                  </div>
                  <div
                    v-else-if="order.status === 'CANCELLED' && order.cancelReason"
                    class="alert alert-secondary mt-2 mb-0 py-2 px-3 small"
                  >
                    <i class="bi bi-info-circle me-1"></i>
                    Lý do hủy: {{ order.cancelReason }}
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
                      :disabled="isActionLocked(order, 'cancel')"
                      class="btn btn-outline-danger btn-sm">
                      <span v-if="isActionLocked(order, 'cancel')" class="spinner-border spinner-border-sm me-1"></span>
                      <i v-else class="bi bi-x-circle me-1"></i>
                      {{ isActionLocked(order, 'cancel') ? 'Đang xử lý' : 'Hủy đơn' }}
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

                    <button
                      v-if="order.status === 'SHIPPING'"
                      @click="confirmReceivedOrder(order)"
                      :disabled="isActionLocked(order, 'confirm')"
                      class="btn btn-sm btn-success"
                    >
                      <span v-if="isActionLocked(order, 'confirm')" class="spinner-border spinner-border-sm me-1"></span>
                      <i v-else class="bi bi-check2-circle me-1"></i>
                      {{ isActionLocked(order, 'confirm') ? 'Đang cập nhật' : 'Đã nhận được hàng' }}
                    </button>

                    <button
                      v-if="order.status === 'SHIPPING'"
                      @click="openComplaintModal(order)"
                      :disabled="isActionLocked(order, 'complaint')"
                      class="btn btn-sm btn-outline-danger"
                    >
                      <span v-if="isActionLocked(order, 'complaint')" class="spinner-border spinner-border-sm me-1"></span>
                      <i v-else class="bi bi-exclamation-triangle me-1"></i>
                      {{ isActionLocked(order, 'complaint') ? 'Đang mở form' : (hasComplaintForOrder(order.id) ? 'Sửa khiếu nại' : 'Khiếu nại thiếu hàng') }}
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
            <button class="page-link" :disabled="currentPage === 0 || isLoading" @click="loadOrders(currentPage - 1)">Trước</button>
          </li>
          <li 
            v-for="page in totalPages" 
            :key="page" 
            class="page-item" 
            :class="{ active: page - 1 === currentPage }">
            <button class="page-link" :disabled="isLoading" @click="loadOrders(page - 1)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
            <button class="page-link" :disabled="currentPage >= totalPages - 1 || isLoading" @click="loadOrders(currentPage + 1)">Sau</button>
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
            <div
              v-if="selectedOrder.status === 'CANCELLED' && isAdminRejectedOrder(selectedOrder)"
              class="alert alert-danger py-2 px-3 small mb-3"
            >
              <i class="bi bi-shield-x me-1"></i>
              Đơn bị admin từ chối.
              <span v-if="selectedOrder.cancelReason">Lý do: {{ selectedOrder.cancelReason }}</span>
            </div>
            <div
              v-else-if="selectedOrder.status === 'CANCELLED' && selectedOrder.cancelReason"
              class="alert alert-secondary py-2 px-3 small mb-3"
            >
              <i class="bi bi-info-circle me-1"></i>
              Lý do hủy: {{ selectedOrder.cancelReason }}
            </div>

            <div class="mb-3">
              <h6 class="mb-2">Lịch sử đơn hàng</h6>
              <div v-if="isLoadingOrderHistory" class="small text-muted">
                <span class="spinner-border spinner-border-sm me-2"></span>
                Đang tải lịch sử đơn hàng...
              </div>
              <div v-else-if="orderHistoryEvents.length === 0" class="small text-muted border rounded py-2 px-3 bg-light">
                Chưa có bản ghi lịch sử cho đơn hàng này.
              </div>
              <div v-else class="order-history-list">
                <div
                  v-for="event in orderHistoryEvents"
                  :key="`history-${event.id}`"
                  class="order-history-item"
                >
                  <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
                    <div class="fw-semibold small">{{ getHistoryEventTitle(event) }}</div>
                    <small class="text-muted">{{ formatDate(event.createdAt) }}</small>
                  </div>
                  <div class="small text-muted">
                    Người thao tác: {{ getHistoryActorText(event) }}
                    <span v-if="event.revisionNo"> • Revision dữ liệu #{{ event.revisionNo }}</span>
                    <span v-if="getCancelSequence(event) !== null"> • Lần hủy #{{ getCancelSequence(event) }}</span>
                  </div>
                  <div v-if="event.note" class="small mt-1">{{ getHistoryNoteText(event) }}</div>
                </div>
              </div>
              <div v-if="orderRevisionSummaries.length > 0" class="small text-muted mt-2">
                Tổng số lần cập nhật dữ liệu: {{ orderRevisionSummaries.length }} revision.
              </div>
              <div class="small text-muted mt-1">
                Tổng số lần hủy: {{ totalCancelAttempts }}.
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
              v-if="canCancelOrder(selectedOrder)"
              @click="cancelOrder(selectedOrder)"
              :disabled="isActionLocked(selectedOrder, 'cancel')"
              class="btn btn-outline-danger">
              <span v-if="isActionLocked(selectedOrder, 'cancel')" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-x-circle me-1"></i>
              {{ isActionLocked(selectedOrder, 'cancel') ? 'Đang xử lý' : 'Hủy đơn hàng' }}
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

    <!-- ===== Complaint Modal ===== -->
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
                <i class="bi bi-info-circle me-1"></i>
                Bạn chỉ có thể gửi khiếu nại khi đơn đang ở trạng thái ĐANG GIAO.
              </div>

              <div v-if="complaintDraftRestoredAt" class="alert alert-info py-2 small mb-3">
                <i class="bi bi-clock-history me-1"></i>
                Đã khôi phục bản nháp lưu lúc {{ formatDate(complaintDraftRestoredAt) }}.
              </div>

              <div class="mb-3">
                <label class="form-label fw-semibold">Mô tả khiếu nại</label>
                <textarea
                  v-model="complaintDescription"
                  class="form-control"
                  rows="3"
                  placeholder="Ví dụ: Thiếu 2 sản phẩm mã XYZ trong kiện hàng..."
                ></textarea>
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
                        <td>
                          <input
                            v-model.number="complaintMissingByItem[item.id]"
                            type="number"
                            min="0"
                            :max="item.quantity || 0"
                            class="form-control form-control-sm"
                          />
                        </td>
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
                    <div
                      v-for="img in complaintExistingImages"
                      :key="`existing-img-${img.id}`"
                      class="complaint-image-tag"
                      :class="{ removed: complaintRemovedImageIds.includes(img.id) }"
                    >
                      <span>{{ img.originalFilename || `Ảnh #${img.id}` }}</span>
                      <button
                        type="button"
                        class="btn btn-sm btn-link text-danger p-0"
                        @click="toggleKeepExistingComplaintImage(img.id)"
                      >
                        {{ complaintRemovedImageIds.includes(img.id) ? 'Giữ lại' : 'Bỏ ảnh' }}
                      </button>
                    </div>
                  </div>
                </div>

                <input
                  type="file"
                  accept="image/*"
                  multiple
                  class="form-control"
                  @change="onComplaintImagesSelected"
                />
                <small class="text-muted">Tối đa 5 ảnh, mỗi ảnh tối đa 5MB.</small>

                <div v-if="complaintNewImages.length > 0" class="mt-2">
                  <div class="small fw-semibold mb-1">Ảnh mới sẽ tải lên</div>
                  <div class="d-flex flex-wrap gap-2">
                    <div
                      v-for="(file, idx) in complaintNewImages"
                      :key="`new-img-${idx}`"
                      class="complaint-image-tag"
                    >
                      <span>{{ file.name }}</span>
                      <button
                        type="button"
                        class="btn btn-sm btn-link text-danger p-0"
                        @click="removeNewComplaintImage(idx)"
                      >
                        Xóa
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
              <button
                type="button"
                class="btn btn-danger"
                :disabled="isSavingComplaint"
                @click="submitComplaint"
              >
                <span v-if="isSavingComplaint" class="spinner-border spinner-border-sm me-2"></span>
                {{ hasComplaintForOrder(complaintOrder.id) ? 'Cập nhật khiếu nại' : 'Gửi khiếu nại' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import apiClient, { ordersAPI } from '../../services/api'
import { Modal } from 'bootstrap'
import PaymentQR from './PaymentQR.vue'
import { getOrderStatusLabel } from '../../constants/orderStatus'

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
const complaintModalRef = ref(null)
const selectedPaymentOrderId = ref(null)
const activeOrderType = ref('CUSTOM_MANUFACTURING')
const getDefaultStatusForType = (type) =>
  type === 'CUSTOM_MANUFACTURING' ? 'PENDING_APPROVAL' : 'AWAITING_PAYMENT'

const activeStatus = ref(getDefaultStatusForType(activeOrderType.value))
const statusCounts = ref({})
const complaintOrder = ref(null)
const complaintDescription = ref('')
const complaintMissingByItem = ref({})
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
let bsModal = null
let bsPaymentModal = null
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
  const orderId = Number(route.query?.orderId || 0)
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

const handleBeforeUnload = (event) => {
  if (!hasComplaintUnsavedChanges.value) return
  event.preventDefault()
  event.returnValue = ''
}

onMounted(async () => {
  restoreViewState()
  window.addEventListener('beforeunload', handleBeforeUnload)

  await Promise.all([
    loadOrders(currentPage.value),
    loadStatusCounts(activeOrderType.value),
  ])
  await openOrderFromQueryIfPresent()
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
    loadOrders(currentPage.value),
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
    await Swal.fire({
      icon: 'success',
      title: 'Đã xác nhận nhận hàng',
      text: `Đơn ${order.orderNumber} đã được chuyển sang hoàn thành.`,
      timer: 2200,
      showConfirmButton: false,
    })
  } catch (error) {
    const msg = error.response?.data?.error || 'Không thể xác nhận nhận hàng'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    setActionLocked(order, 'confirm', false)
  }
}

const openComplaintModal = async (order) => {
  if (!order?.id || isActionLocked(order, 'complaint')) return

  if (order.status !== 'SHIPPING') {
    await Swal.fire('Không hợp lệ', 'Chỉ có thể khiếu nại khi đơn đang giao hàng.', 'warning')
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
    complaintExistingImages.value = Array.isArray(existingComplaint?.images) ? existingComplaint.images : []
    complaintRemovedImageIds.value = []
    complaintNewImages.value = []

    const missingMap = {}
    ;(complaintOrder.value?.items || []).forEach((item) => {
      missingMap[item.id] = 0
    })
    ;(existingComplaint?.missingItems || []).forEach((item) => {
      if (item?.orderItemId != null) {
        missingMap[item.orderItemId] = Number(item.missingQuantity || 0)
      }
    })
    complaintMissingByItem.value = missingMap

    const draft = loadComplaintDraft(order.id)
    if (draft) {
      const restorePrompt = await Swal.fire({
        title: 'Khôi phục bản nháp khiếu nại?',
        text: `Đã tìm thấy bản nháp lưu lúc ${formatDate(draft.savedAt)}.`,
        icon: 'question',
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: 'Khôi phục',
        denyButtonText: 'Xóa nháp',
        cancelButtonText: 'Bỏ qua',
        confirmButtonColor: '#0d6efd',
        denyButtonColor: '#dc2626',
        cancelButtonColor: '#64748b',
      })

      if (restorePrompt.isConfirmed) {
        complaintDescription.value = draft.description || ''

        const normalizedDraftMissing = normalizeComplaintMissingMap(complaintOrder.value, draft.missingByItem)
        const restoredMissingMap = {}
        ;(complaintOrder.value?.items || []).forEach((item) => {
          restoredMissingMap[item.id] = Number(normalizedDraftMissing[item.id] || 0)
        })
        complaintMissingByItem.value = restoredMissingMap

        const existingImageIds = new Set((complaintExistingImages.value || []).map((img) => Number(img.id)))
        complaintRemovedImageIds.value = (draft.removedImageIds || [])
          .map((id) => Number(id))
          .filter((id) => existingImageIds.has(id))
          .sort((a, b) => a - b)

        complaintDraftRestoredAt.value = draft.savedAt || Date.now()
      } else if (restorePrompt.isDenied) {
        clearComplaintDraft(order.id)
      }
    }

    setComplaintInitialState()

    await nextTick()
    if (!bsComplaintModal && complaintModalRef.value) {
      bsComplaintModal = new Modal(complaintModalRef.value)
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
  scheduleComplaintDraftSave()
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
  scheduleComplaintDraftSave()

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
    .map((item) => ({
      orderItemId: item.id,
      missingQuantity: Number(complaintMissingByItem.value[item.id] || 0),
    }))
    .filter((item) => item.missingQuantity > 0)

  if (missingItems.length === 0) {
    Swal.fire('Thiếu dữ liệu', 'Vui lòng nhập số lượng thiếu cho ít nhất 1 sản phẩm.', 'warning')
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

    clearComplaintDraft(complaintOrder.value.id)
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
  } finally {
    setActionLocked(order, 'cancel', false)
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
  max-height: 220px;
  overflow-y: auto;
}

.order-history-item {
  padding: 10px 12px;
  border-bottom: 1px dashed #e2e8f0;
}

.order-history-item:last-child {
  border-bottom: none;
}
</style>
