<template>
  <div class="processing-page p-4 min-vh-100 d-flex flex-column" style="background-color: #f8f9fa;">
    
    <div class="d-flex flex-column flex-lg-row justify-content-between align-items-lg-end mb-4 pb-2 border-bottom">
      <div class="mb-3 mb-lg-0">
        <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">
          <i class="bi bi-file-earmark-ruled-fill me-2 text-navy"></i> Quản lý Xuất Hoá Đơn
        </h2>
        <p class="text-muted small mb-0">Theo dõi đơn gia công, review vật tư và xuất kho theo lô ngày giao.</p>
      </div>
      
      <div class="d-flex gap-2 flex-wrap align-items-center stat-pill-group">
        <div class="stat-pill text-primary border-primary">
          <i class="bi bi-layers-fill"></i> Đơn hiển thị: <b class="fs-6 ms-1">{{ orders.length }}</b>
        </div>
        <div class="stat-pill text-success border-success">
          <i class="bi bi-check-all"></i> Đã chọn: <b class="fs-6 ms-1">{{ selectedItemIds.length }}</b> <span class="small ms-1 fw-medium">vật tư</span>
        </div>
        <button class="btn btn-navy rounded-pill px-4 py-2 fw-bold shadow-sm hover-lift ms-2" @click="openAdminImportDialog" :disabled="importing">
          <i class="bi bi-upload me-2"></i>
          <span v-if="importing" class="spinner-border spinner-border-sm me-1"></span>
          {{ importing ? 'Đang xử lý...' : 'Import Excel' }}
        </button>
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
            <option value="PENDING_APPROVAL">Chờ duyệt đơn</option>
            <option value="PENDING_QUOTE">Chờ báo giá</option>
            <option value="AWAITING_PAYMENT">Chờ thanh toán</option>
            <option value="DEPOSITED">Đã cọc</option>
            <option value="PROCESSING">Đang gia công</option>
            <option value="AWAITING_REMAINING_PAYMENT">Chờ thanh toán đợt 2</option>
            <option value="AWAITING_DELIVERY">Chờ giao hàng</option>
            <option value="SHIPPING">Đang giao hàng</option>
            <option value="COMPLETED">Hoàn thành</option>
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
      <p class="text-muted mt-3 fw-bold">Đang tải dữ liệu đơn hàng...</p>
    </div>

    <div v-else-if="orders.length === 0" class="card border-0 shadow-sm rounded-4 flex-grow-1">
      <div class="card-body text-center py-5 d-flex flex-column align-items-center justify-content-center">
        <div class="rounded-circle bg-light d-flex align-items-center justify-content-center mb-3" style="width: 80px; height: 80px;">
          <i class="bi bi-inbox fs-1 text-muted opacity-50"></i>
        </div>
        <h5 class="fw-bold text-dark">Chưa có đơn hàng nào</h5>
        <p class="text-muted">Không tìm thấy dữ liệu khớp với bộ lọc của bạn.</p>
      </div>
    </div>

    <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white flex-grow-1 d-flex flex-column">
      <div class="table-responsive flex-grow-1 pb-2">
        <table class="table modern-table align-middle mb-0 w-100" style="table-layout: fixed;">
          <thead class="bg-light">
            <tr>
              <th style="width: 50px;" class="text-center py-3">
                <i class="bi bi-chevron-down text-muted" style="font-size: 0.8rem;"></i>
              </th>
              <th class="py-3 text-muted fw-bold text-uppercase" style="font-size: 0.8rem; width: 180px;">Mã đơn</th>
              <th class="py-3 text-muted fw-bold text-uppercase" style="font-size: 0.8rem;">Khách hàng</th>
              <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem;">Sản phẩm</th>
              <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem;">Review</th>
              <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem; width: 150px;">Trạng thái</th>
              <th class="py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 150px;">Giá trị</th>
              <th class="py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 110px;">Ngày tạo</th>
              <th class="pe-4 py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 180px;">Thao tác</th>
            </tr>
          </thead>
          <tbody v-for="order in orders" :key="`${order.isTempImport ? 'imp' : 'ord'}-${order.id}`">
            <tr @click="toggleDetails(order.id)" class="cursor-pointer table-row-hover shadow-sm border" :class="{ 'table-active-row': expandedOrderId === order.id }">
              <td class="text-center py-3">
                <i class="bi text-navy fw-bold" :class="expandedOrderId === order.id ? 'bi-chevron-down' : 'bi-chevron-right'" style="font-size: 0.9rem;"></i>
              </td>
              <td class="py-3">
                <span class="badge bg-light text-navy border px-2 py-1 fw-bold font-monospace fs-6 shadow-sm">
                  {{ order.orderNumber }}
                </span>
              </td>
              <td class="py-3">
                <h6 class="mb-0 fw-bold text-dark fs-6">{{ order.userName }}</h6>
                <span class="text-muted small fw-medium text-truncate d-block"><i class="bi bi-buildings me-1"></i>{{ order.companyName }}</span>
              </td>
              <td class="py-3 text-center">
                <span class="badge bg-light border border-secondary border-opacity-25 text-secondary px-3 py-2 fw-bold rounded-pill">
                  {{ order.items?.length || 0 }} SP
                </span>
              </td>
              <td class="py-3 text-center">
                <span v-if="order.status === 'PENDING_QUOTE'" class="badge bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50 px-2 py-1">
                  {{ getReviewProgress(order) }}
                </span>
                <span v-else class="text-muted small">—</span>
              </td>
              <td class="py-3 text-center">
                <div class="d-flex align-items-center justify-content-center gap-2">
                  <span class="glowing-dot" :class="getStatusDotClass(order.status)"></span>
                  <span class="badge rounded-pill px-2 py-1 fw-bold" :class="getStatusBadgeClass(order.status)" style="font-size: 0.75rem;">
                    {{ getStatusText(order.status) }}
                  </span>
                </div>
              </td>
              <td class="py-3 text-end fw-bolder text-dark fs-6">{{ formatCurrency(order.totalPrice) }}</td>
              <td class="py-3 text-end text-secondary fw-medium small">{{ formatDate(order.createdAt) }}</td>
              <td class="pe-4 py-3 text-end" @click.stop>
                <div class="d-flex flex-wrap gap-1 justify-content-end action-group-wrap">
                  <button @click="openReviewModal(order)" class="btn btn-action-circle bg-light text-primary border" :title="order.status === 'PENDING_APPROVAL' ? 'Xem chi tiết đơn' : 'Xem & Review'">
                    <i class="bi bi-eye-fill"></i>
                  </button>
                  <button v-if="order.status === 'PENDING_APPROVAL'" @click="approveOrder(order)" class="btn btn-action-circle bg-light text-warning border" title="Duyệt đơn">
                    <i class="bi bi-check2-circle"></i>
                  </button>
                  <button v-if="order.status === 'PENDING_APPROVAL' && order.isTempImport" @click="rejectOrder(order)" class="btn btn-action-circle bg-light text-danger border" title="Từ chối đơn">
                    <i class="bi bi-x-circle"></i>
                  </button>
                  <button v-if="canAdminCancelRegularOrder(order)" @click="cancelRegularOrder(order)" class="btn btn-action-circle bg-light text-danger border" title="Hủy đơn">
                    <i class="bi bi-slash-circle"></i>
                  </button>
                  <button v-if="order.status === 'PENDING_QUOTE' && isAllReviewed(order)" @click="submitQuote(order)" class="btn btn-action-circle bg-light text-success border" title="Gửi báo giá">
                    <i class="bi bi-currency-dollar"></i>
                  </button>
                  <button v-if="order.status === 'DEPOSITED'" @click="startProcessing(order)" class="btn btn-action-circle bg-light text-navy border" title="Bắt đầu gia công">
                    <i class="bi bi-play-fill"></i>
                  </button>
                  <button v-if="order.status === 'PROCESSING'" @click="finishProcessing(order)" class="btn btn-action-circle bg-light text-warning border" title="Hoàn thành gia công" :disabled="finishing">
                    <i class="bi bi-check-circle-fill"></i>
                  </button>
                  <button v-if="order.status === 'AWAITING_DELIVERY'" @click="markAsShipping(order)" class="btn btn-action-circle bg-light text-info border" title="Bàn giao vận chuyển" :disabled="shipping">
                    <i class="bi bi-truck"></i>
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="expandedOrderId === order.id">
              <td colspan="9" class="p-0 border-0 bg-light">
                <div class="expand-content bg-white border-start border-4 border-navy ms-5 my-3 me-3 p-4 rounded-4 shadow-sm position-relative overflow-hidden">
                  
                  <div v-if="detailLoading" class="text-center py-4">
                    <div class="spinner-border spinner-border-sm text-primary"></div>
                    <span class="ms-2 text-muted fw-medium small">Đang tải dữ liệu chi tiết...</span>
                  </div>

                  <template v-else-if="orderDetail">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3 pb-2 border-bottom">
                      <h6 class="fw-bolder m-0 text-dark fs-5">
                        <i class="bi bi-box-seam me-2 text-navy"></i>Chi tiết đơn <span class="text-navy font-monospace">{{ order.orderNumber }}</span>
                      </h6>
                      <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 px-3 py-2 mt-2 mt-md-0 fs-6 rounded-pill">
                        <i class="bi bi-check2-square me-1"></i> Đã chọn: {{ getSelectedCountForOrder(order.id) }} / {{ orderDetail.items?.length || 0 }}
                      </span>
                    </div>

                    <div v-if="!canSelectShipmentItems(order)" class="alert alert-warning py-2 px-3 small mb-3 border-warning border-opacity-25 bg-warning bg-opacity-10 rounded-3 text-dark fw-medium">
                      <i class="bi bi-info-circle-fill me-2 text-warning"></i>Chỉ được chọn sản phẩm để xuất hóa đơn khi đơn đã được cọc.
                    </div>

                    <div v-if="canSelectShipmentItems(order) && currentDateGroups.length > 0" class="date-group-bar bg-slate-50 p-3 rounded-4 mb-3 d-flex flex-wrap align-items-center gap-2 border">
                      <span class="text-muted small fw-bolder text-uppercase me-2"><i class="bi bi-calendar2-check me-1"></i>Lọc nhanh theo ngày:</span>
                      <button v-for="dg in currentDateGroups" :key="dg.date"
                        @click="toggleDateGroup(dg.date)"
                        class="btn rounded-pill fw-medium border shadow-sm btn-sm transition-all"
                        :class="isDateGroupSelected(dg.date) ? 'btn-navy text-white border-navy' : 'btn-white text-secondary hover-lift'">
                        <i class="bi" :class="isDateGroupSelected(dg.date) ? 'bi-check-circle-fill' : 'bi-circle'"></i>
                        {{ dg.label }} <span class="ms-1" :class="isDateGroupSelected(dg.date) ? 'text-white-50' : 'text-muted'">({{ dg.items.length }})</span>
                      </button>
                      <div class="vr mx-2 text-muted opacity-25"></div>
                      <button @click="selectAllItems" class="btn btn-outline-success btn-sm rounded-pill fw-bold hover-lift"><i class="bi bi-check-all"></i> Tất cả</button>
                      <button v-if="getSelectedCountForOrder(order.id) > 0" @click="clearItemSelection" class="btn btn-outline-danger btn-sm rounded-pill fw-bold hover-lift"><i class="bi bi-x-lg"></i> Bỏ chọn</button>
                    </div>

                    <div class="table-responsive rounded-4 border shadow-sm" style="max-height: 450px; overflow-y: auto; overflow-x: auto;">
                      <table class="table modern-table-small align-middle mb-0">
                        <thead class="bg-light" style="position: sticky; top: 0; z-index: 10;">
                          <tr class="text-center small text-muted text-uppercase fw-bold">
                            <th v-if="canSelectShipmentItems(order)" class="bg-light border-bottom" style="width: 40px;">
                              <input type="checkbox" class="form-check-input shadow-none" :checked="allItemsInOrderSelected" @change="toggleAllItemsInOrder($event)">
                            </th>
                            <th class="bg-light border-bottom">ITEM CODE</th>
                            <th class="bg-light border-bottom text-start">BẢN VẼ</th>
                            <th class="bg-light border-bottom text-start">TÊN LINH KIỆN</th>
                            <th class="bg-light border-bottom">SL</th>
                            <th class="bg-light border-bottom" style="width: 120px;">NGÀY XUẤT</th>
                            <th class="bg-light border-bottom" style="width: 110px;">KHỐI LƯỢNG</th>
                            <th class="bg-light border-bottom text-end">ĐƠN GIÁ</th>
                            <th class="bg-light border-bottom text-end pe-4">THÀNH TIỀN</th>
                            <th class="bg-light border-bottom">REVIEW</th>
                          </tr>
                        </thead>
                        <tbody class="text-center bg-white">
                          <tr v-for="item in orderDetail.items" :key="item.id" 
                              class="border-bottom"
                              :class="[getItemRowClass(item), { 'bg-primary bg-opacity-10': selectedItemIds.includes(item.id) }]">
                            <td v-if="canSelectShipmentItems(order)" @click.stop>
                              <input type="checkbox" class="form-check-input shadow-none" :checked="selectedItemIds.includes(item.id)" @change="toggleItemSelection(item.id)">
                            </td>
                            <td class="fw-bold text-secondary small">{{ item.itemCode || '—' }}</td>
                            <td class="text-start text-primary fw-bold font-monospace small">{{ item.drawingNumber || '—' }}</td>
                            <td class="text-start fw-bold text-dark">{{ item.itemName || '—' }}</td>
                            <td><span class="badge bg-light text-dark border px-2 py-1">{{ item.quantity }}</span></td>
                            <td>
                              <span v-if="item.deliveryDate" class="badge bg-info-subtle text-info border border-info border-opacity-25">{{ formatDateShort(item.deliveryDate) }}</span>
                              <span v-else class="text-muted">—</span>
                            </td>
                            <td class="fw-medium text-secondary">{{ formatWeight(item.weight) || '—' }}</td>
                            <td class="text-end fw-medium text-secondary">{{ item.unitPrice ? formatNumber(item.unitPrice) : '—' }}</td>
                            <td class="text-end pe-4 fw-bolder text-dark">{{ item.totalItemPrice ? formatNumber(item.totalItemPrice) : '—' }}</td>
                            <td>
                              <span class="badge rounded-pill" :class="getReviewBadgeClass(item.reviewStatus)" style="font-size: 0.7rem">
                                {{ getReviewStatusText(item.reviewStatus) }}
                              </span>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </template>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="p-3 bg-white border-top d-flex justify-content-between align-items-center mt-auto sticky-bottom" style="box-shadow: 0 -4px 10px rgba(0,0,0,0.03);">
        <span class="fw-bold text-navy fs-6">
          <i class="bi bi-check-circle-fill me-1 text-success"></i> Đã chọn tổng cộng: <b class="fs-5">{{ selectedItemIds.length }}</b> sản phẩm
        </span>
        <button class="btn btn-success rounded-pill px-4 py-2 fw-bold shadow-sm hover-lift" :disabled="selectedItemIds.length === 0" @click="createShipment">
          <i class="bi bi-box-arrow-right me-2"></i>Tạo Phiếu Xuất Kho
        </button>
      </div>
    </div>

    <div v-if="totalPages > 1" class="d-flex justify-content-between align-items-center mt-4 pb-2 px-2">
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

    <div class="modal fade" id="reviewModal" tabindex="-1" ref="reviewModalRef">
      <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable review-modal-wide">
        <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden bg-slate-50">
          
          <div class="modal-header bg-navy text-white px-4 py-3 border-0">
            <h5 class="modal-title fw-bolder mb-0 d-flex align-items-center">
              <i class="bi bi-clipboard-check me-2 fs-4"></i>
              <span class="font-monospace me-2">{{ selectedOrder?.orderNumber }}</span> — 
              <span class="ms-1 fw-medium fs-6 mt-1">{{ selectedOrder?.status === 'PENDING_APPROVAL' ? 'Chi tiết đơn chờ duyệt' : 'Review & Báo giá' }}</span>
            </h5>
            <button type="button" class="btn-close btn-close-white shadow-none" data-bs-dismiss="modal"></button>
          </div>
          
          <div class="modal-body p-4 bg-slate-50">
            <div class="card border-0 shadow-sm rounded-4 p-4 mb-4 bg-white">
              <div class="row g-4">
                <div class="col-md-4 border-end">
                  <p class="mb-1 text-muted small fw-bold text-uppercase">Khách hàng</p>
                  <h6 class="fw-bold text-dark mb-1">{{ selectedOrder?.userName }}</h6>
                  <p class="mb-0 text-secondary small"><i class="bi bi-buildings me-1"></i>{{ selectedOrder?.companyName }}</p>
                </div>
                <div class="col-md-4 border-end">
                  <p class="mb-1 text-muted small fw-bold text-uppercase">Thông tin đơn</p>
                  <div class="d-flex align-items-center gap-2 mb-1">
                    <span class="badge rounded-pill px-2 py-1" :class="getStatusBadgeClass(selectedOrder?.status)">
                      {{ getStatusText(selectedOrder?.status) }}
                    </span>
                  </div>
                  <p class="mb-0 text-secondary small fw-medium">Ngày tạo: {{ formatDate(selectedOrder?.createdAt) }}</p>
                </div>
                <div class="col-md-4">
                  <p v-if="selectedOrder?.status !== 'PENDING_APPROVAL'" class="mb-2 text-muted small fw-bold text-uppercase">Tiến trình review: <span class="badge bg-warning text-dark ms-1 fs-6">{{ getReviewProgress(selectedOrder) }}</span></p>
                  <p class="mb-0 text-muted small fw-bold text-uppercase" v-if="selectedOrder?.totalPrice">
                    Tổng giá trị: <strong class="text-danger fs-5 d-block mt-1">{{ formatCurrency(selectedOrder?.totalPrice) }}</strong>
                  </p>
                </div>
              </div>
            </div>

            <div class="d-flex gap-2 mb-3 border-bottom pb-2" role="tablist">
              <button type="button" role="tab" class="btn rounded-pill px-4 fw-bold transition-all"
                :class="activeDetailTab === 'history' ? 'btn-navy shadow-sm' : 'btn-light border text-secondary hover-lift'"
                @click="activeDetailTab = 'history'">
                <i class="bi bi-clock-history me-1"></i>Lịch sử đơn
                <span class="badge rounded-pill ms-1" :class="activeDetailTab === 'history' ? 'bg-white text-navy' : 'bg-secondary text-white'">{{ sortedOrderHistoryEvents.length }}</span>
              </button>
              <button type="button" role="tab" class="btn rounded-pill px-4 fw-bold transition-all"
                :class="activeDetailTab === 'materials' ? 'btn-navy shadow-sm' : 'btn-light border text-secondary hover-lift'"
                @click="activeDetailTab = 'materials'">
                <i class="bi bi-grid-1x2 me-1"></i>Chi tiết vật tư & Báo giá
                <span class="badge rounded-pill ms-1" :class="activeDetailTab === 'materials' ? 'bg-white text-navy' : 'bg-secondary text-white'">{{ selectedOrder?.items?.length || 0 }}</span>
              </button>
            </div>

            <transition name="modal-tab-fade" mode="out-in">
              <div v-if="activeDetailTab === 'history'" id="history-panel" key="history" class="bg-white rounded-4 border p-4 shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                  <h6 class="fw-bolder m-0 text-navy text-uppercase"><i class="bi bi-journal-text me-2"></i>Nhật ký xử lý</h6>
                  <div class="small fw-medium">
                    <span class="badge bg-info bg-opacity-10 text-info border border-info border-opacity-25 px-2 py-1 me-2">Revisions: {{ orderRevisionSummaries.length }}</span>
                    <span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 px-2 py-1">Hủy: {{ totalCancelAttempts }}</span>
                  </div>
                </div>

                <div v-if="isLoadingOrderHistory" class="text-center py-4 text-muted fw-medium">
                  <span class="spinner-border spinner-border-sm me-2 text-primary"></span>Đang tải lịch sử...
                </div>
                <div v-else-if="sortedOrderHistoryEvents.length === 0" class="text-center py-4 text-muted">
                  <i class="bi bi-inbox fs-3 d-block mb-2 opacity-50"></i> Chưa có lịch sử.
                </div>
                
                <div v-else class="history-timeline px-2" style="max-height: 400px; overflow-y: auto;">
                  <div v-for="event in sortedOrderHistoryEvents" :key="`admin-history-${event.id}`" class="d-flex mb-3 position-relative">
                    <div class="d-flex flex-column align-items-center me-3">
                      <div class="rounded-circle bg-navy" style="width: 12px; height: 12px; z-index: 2; box-shadow: 0 0 0 3px #dbeafe;"></div>
                      <div class="bg-light flex-grow-1 my-1" style="width: 2px;"></div>
                    </div>
                    <div class="bg-light p-3 rounded-3 border flex-grow-1 shadow-sm">
                      <div class="d-flex justify-content-between align-items-start mb-1">
                        <span class="fw-bold text-dark fs-6">{{ getHistoryEventTitle(event) }}</span>
                        <span class="badge bg-white text-secondary border small"><i class="bi bi-clock me-1"></i>{{ formatDate(event.createdAt) }}</span>
                      </div>
                      <div class="small text-muted fw-medium mb-1">Thao tác bởi: <b class="text-navy">{{ getHistoryActorText(event) }}</b>
                        <span v-if="event.revisionNo" class="ms-1 text-info">(Rev #{{ event.revisionNo }})</span>
                        <span v-if="getCancelSequence(event) !== null" class="ms-1 text-danger">(Hủy lần #{{ getCancelSequence(event) }})</span>
                      </div>
                      <div v-if="event.note" class="small mt-2 p-2 bg-white border rounded text-dark fst-italic"><i class="bi bi-quote text-muted me-1"></i>{{ getHistoryNoteText(event) }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else id="materials-panel" key="materials">
                <div class="d-flex justify-content-between align-items-end mb-3">
                  <p class="small text-muted fw-medium mb-0"><i class="bi bi-arrows-move me-1"></i> Cuộn ngang để xem đầy đủ các cột dữ liệu.</p>
                  <div v-if="selectedOrder?.status !== 'PENDING_APPROVAL'" class="btn-group bg-white border p-1 rounded-pill shadow-sm">
                    <button class="btn btn-sm rounded-pill fw-bold" :class="reviewDensity === 'comfortable' ? 'btn-navy' : 'btn-white text-muted'" @click="reviewDensity = 'comfortable'">Rộng rãi</button>
                    <button class="btn btn-sm rounded-pill fw-bold" :class="reviewDensity === 'compact' ? 'btn-navy' : 'btn-white text-muted'" @click="reviewDensity = 'compact'">Thu gọn</button>
                  </div>
                </div>

                <div class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white">
                  <div class="table-responsive" style="max-height: 500px; overflow-y: auto;">
                    <table class="table table-bordered align-middle mb-0 review-table" :class="reviewDensity === 'compact' ? 'table-sm review-table-compact' : 'review-table-comfortable'">
                      <thead class="table-light sticky-top" style="z-index: 10;">
                        <tr class="text-center small text-muted text-uppercase fw-bold">
                          <th class="sticky-col-left-stt bg-light" style="width: 50px;">STT</th>
                          <th style="min-width: 120px;">VNN_NO</th>
                          <th style="min-width: 95px;">Item Code</th>
                          <th class="sticky-col-left-drawing bg-light" style="min-width: 105px;">Bản Vẽ</th>
                          <th style="min-width: 180px;">Tên linh kiện</th>
                          <th style="min-width: 140px;">Spec</th>
                          <th style="min-width: 95px;">Vật liệu</th>
                          <th style="width: 65px;">SL</th>
                          <th style="min-width: 100px;">Ngày xuất</th>
                          <th style="min-width: 95px;">Khối lượng</th>
                          <th style="min-width: 95px;">Review</th>
                          <th style="min-width: 130px;">Đơn giá</th>
                          <th style="min-width: 130px;">Thành tiền</th>
                          <th class="sticky-col-right-actions bg-light" style="min-width: 140px;">Thao tác</th>
                        </tr>
                      </thead>
                      <tbody class="text-center bg-white">
                        <tr v-for="(item, index) in selectedOrder?.items" :key="item.id" :class="getItemRowClass(item)">
                          <td class="sticky-col-left-stt bg-white fw-bold text-muted">{{ item.stt || (index + 1) }}</td>
                          <td class="small">{{ item.unit || '—' }}</td>
                          <td class="small text-secondary fw-bold">{{ item.itemCode || '—' }}</td>
                          <td class="sticky-col-left-drawing bg-white font-monospace text-primary fw-bold small">{{ item.drawingNumber || '—' }}</td>
                          <td class="text-start fw-bold text-dark">{{ item.itemName || '—' }}</td>
                          <td class="text-start small text-secondary">{{ item.specification || '—' }}</td>
                          <td class="small">{{ item.material || '—' }}</td>
                          <td><span class="badge bg-light text-dark border px-2 py-1">{{ item.quantity }}</span></td>
                          <td><span v-if="item.deliveryDate" class="badge bg-info-subtle text-info border border-info border-opacity-25">{{ formatDateShort(item.deliveryDate) }}</span><span v-else class="text-muted">—</span></td>
                          <td class="small fw-medium">{{ formatWeight(item.weight) }}</td>
                          <td><span class="badge rounded-pill" :class="getReviewBadgeClass(item.reviewStatus)" style="font-size: 0.7rem">{{ getReviewStatusText(item.reviewStatus) }}</span></td>
                          
                          <td class="text-end">
                            <template v-if="selectedOrder?.status === 'PENDING_QUOTE'">
                              <input type="number" class="form-control form-control-sm text-end fw-bold text-primary shadow-none border-secondary border-opacity-25" :value="getReviewDraft(item).unitPrice ?? ''" @input="setDraftUnitPrice(item, $event.target.value)" min="0" step="1000" placeholder="0 ₫" />
                            </template>
                            <template v-else>
                              <span v-if="item.unitPrice" class="fw-medium text-secondary">{{ formatNumber(item.unitPrice) }}</span>
                              <span v-else class="text-muted">—</span>
                            </template>
                          </td>
                          
                          <td class="text-end pe-3 fw-bolder text-dark">{{ item.totalItemPrice ? formatNumber(item.totalItemPrice) : '—' }}</td>
                          
                          <td class="sticky-col-right-actions bg-white">
                            <div class="d-flex flex-column align-items-center gap-2 py-1">
                              <div v-if="selectedOrder?.status === 'PENDING_QUOTE'" class="d-flex gap-1">
                                <button @click="reviewItem(item, 'APPROVED')" class="btn btn-outline-success btn-sm rounded-circle shadow-sm" style="width: 32px; height: 32px; padding:0;" title="Duyệt"><i class="bi bi-check-lg"></i></button>
                                <button @click="reviewItem(item, 'REJECTED')" class="btn btn-outline-danger btn-sm rounded-circle shadow-sm" style="width: 32px; height: 32px; padding:0;" title="Từ chối"><i class="bi bi-x-lg"></i></button>
                                <button @click="reviewItem(item, 'NEED_DISCUSSION')" class="btn btn-outline-warning btn-sm rounded-circle shadow-sm" style="width: 32px; height: 32px; padding:0;" title="Cần trao đổi"><i class="bi bi-chat-dots"></i></button>
                              </div>
                              
                              <button v-if="selectedOrder?.status === 'PENDING_QUOTE'" type="button" class="btn btn-light border btn-sm rounded-pill fw-medium text-secondary w-100" @click="openAdminNoteEditor(item)">
                                <i class="bi bi-chat-left-text me-1"></i>{{ getReviewDraft(item).adminNote ? 'Sửa Note' : 'Thêm Note' }}
                              </button>
                              <button v-else-if="item.adminNote" type="button" class="btn btn-light border btn-sm rounded-pill fw-medium text-secondary w-100" @click="openItemNotePopup(item)">
                                <i class="bi bi-chat-left-text me-1"></i>Xem Note
                              </button>
                              <span v-else class="text-muted small">—</span>
                            </div>
                          </td>
                        </tr>
                      </tbody>
                      <tfoot v-if="calculatedTotal > 0">
                        <tr class="bg-light fw-bold">
                          <td colspan="12" class="text-end py-3 text-uppercase text-secondary fs-6">Tổng (sản phẩm đã duyệt):</td>
                          <td class="text-end pe-3 py-3 text-danger fs-5">{{ formatCurrency(calculatedTotal) }}</td>
                          <td class="bg-light"></td>
                        </tr>
                      </tfoot>
                    </table>
                  </div>
                </div>

                <div v-if="selectedOrder?.items?.some(item => item.adminNote)" class="mt-4 bg-white p-3 rounded-4 border shadow-sm">
                  <div class="small text-navy fw-bold text-uppercase mb-2"><i class="bi bi-stickies me-1"></i>Tổng hợp Ghi chú sản phẩm:</div>
                  <div class="d-flex flex-wrap gap-2">
                    <button v-for="item in selectedOrder.items" :key="'note-' + item.id" v-show="item.adminNote" type="button" class="btn btn-light border btn-sm rounded-pill hover-lift text-dark fw-medium" @click="openItemNotePopup(item)">
                      {{ item.itemName || 'Sản phẩm' }} <i class="bi bi-arrow-right-short text-muted mx-1"></i> <i class="bi bi-chat-text text-primary"></i>
                    </button>
                  </div>
                </div>
              </div>
            </transition>
          </div>

          <div class="modal-footer border-top px-4 py-3 bg-white justify-content-between">
            <button type="button" class="btn btn-light border rounded-pill px-4 fw-bold hover-lift" data-bs-dismiss="modal">Đóng cửa sổ</button>
            <div class="d-flex gap-2">
              <button v-if="selectedOrder?.status === 'PENDING_APPROVAL'" @click="approveOrder(selectedOrder)" class="btn btn-warning rounded-pill px-4 fw-bold text-dark shadow-sm hover-lift"><i class="bi bi-check2-circle me-1"></i>Duyệt Đơn</button>
              <button v-if="selectedOrder?.status === 'PENDING_APPROVAL' && selectedOrder?.isTempImport" @click="rejectOrder(selectedOrder)" class="btn btn-outline-danger rounded-pill px-4 fw-bold hover-lift"><i class="bi bi-x-circle me-1"></i>Từ chối đơn</button>
              <button v-if="canAdminCancelRegularOrder(selectedOrder)" @click="cancelRegularOrder(selectedOrder)" class="btn btn-outline-danger rounded-pill px-4 fw-bold hover-lift"><i class="bi bi-slash-circle me-1"></i>Hủy đơn</button>
              <button v-if="selectedOrder?.status === 'PENDING_QUOTE' && isAllReviewed(selectedOrder)" @click="submitQuoteFromModal()" class="btn btn-success rounded-pill px-4 fw-bold shadow-sm hover-lift"><i class="bi bi-send me-2"></i>Gửi Báo Giá <span class="badge bg-white text-success ms-1">{{ formatCurrency(calculatedTotal) }}</span></button>
              <button v-if="selectedOrder?.status === 'AWAITING_DELIVERY'" @click="markAsShipping(selectedOrder)" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm hover-lift" :disabled="shipping"><i class="bi bi-truck me-1"></i>Bàn giao Giao hàng</button>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import apiClient from '../../services/api';
import { Modal } from 'bootstrap';
import { getOrderStatusLabel, getReviewStatusLabel } from '../../constants/orderStatus';

// KEEPS ALL YOUR ORIGINAL SCRIPT LOGIC EXACTLY INTACT
const router = useRouter();

const orders = ref([]);
const isLoading = ref(false);
const currentPage = ref(0);
const totalPages = ref(0);
const selectedOrder = ref(null);
const reviewModalRef = ref(null);
const expandedOrderId = ref(null);
const orderDetail = ref(null);
const detailLoading = ref(false);
const searchKeyword = ref('');
const filterStatus = ref('');
const dateFrom = ref('');
const dateTo = ref('');
const importing = ref(false);
const companiesForImport = ref([]);
const reviewDrafts = ref({});
const reviewDensity = ref('comfortable');
const activeDetailTab = ref('materials');
const orderHistoryEvents = ref([]);
const orderRevisionSummaries = ref([]);
const isLoadingOrderHistory = ref(false);
let bsModal = null;

const selectedItemIds = ref([]);
const shipmentEligibleStatuses = ['DEPOSITED', 'PROCESSING', 'COMPLETED'];

onMounted(() => {
  loadCompaniesForImport();
  loadOrders();
});

const loadCompaniesForImport = async () => {
  try {
    const res = await apiClient.get('/companies', { params: { page: 0, size: 200 } });
    const content = res.data?.content || [];
    companiesForImport.value = Array.isArray(content) ? content : [];
  } catch (error) { companiesForImport.value = []; }
};

const loadOrders = async (page = 0) => {
  isLoading.value = true;
  try {
    if (filterStatus.value === 'PENDING_APPROVAL') {
      const params = { page, size: 15 };
      if (searchKeyword.value?.trim()) params.keyword = searchKeyword.value.trim();

      const response = await apiClient.get('/orders/imports/pending', { params });
      const data = response.data.content || response.data;
      orders.value = (Array.isArray(data) ? data : []).map(o => ({ ...o, selected: false, isTempImport: true }));
      currentPage.value = response.data.number || 0;
      totalPages.value = response.data.totalPages || 1;
    } else {
      const params = { page, size: 15, orderType: 'CUSTOM_MANUFACTURING' };
      if (searchKeyword.value?.trim()) params.keyword = searchKeyword.value.trim();
      if (filterStatus.value) params.status = filterStatus.value;
      if (dateFrom.value) params.dateFrom = dateFrom.value;
      if (dateTo.value) params.dateTo = dateTo.value;

      const response = await apiClient.get('/orders', { params });
      const data = response.data.content || response.data;
      const mainOrders = (Array.isArray(data) ? data : []).map(o => ({ ...o, selected: false, isTempImport: false }));

      if (!filterStatus.value && page === 0) {
        const pendingParams = { page: 0, size: 50 };
        if (searchKeyword.value?.trim()) pendingParams.keyword = searchKeyword.value.trim();

        const pendingResponse = await apiClient.get('/orders/imports/pending', { params: pendingParams });
        const pendingData = pendingResponse.data.content || pendingResponse.data;
        const pendingOrders = (Array.isArray(pendingData) ? pendingData : []).map(o => ({ ...o, selected: false, isTempImport: true }));

        orders.value = [...pendingOrders, ...mainOrders].sort((a, b) => {
          const timeA = a?.createdAt ? new Date(a.createdAt).getTime() : 0;
          const timeB = b?.createdAt ? new Date(b.createdAt).getTime() : 0;
          return timeB - timeA;
        });
      } else {
        orders.value = mainOrders;
      }
      currentPage.value = response.data.number || 0;
      totalPages.value = response.data.totalPages || 1;
    }

    if (expandedOrderId.value && !orders.value.some(o => o.id === expandedOrderId.value)) {
      expandedOrderId.value = null; orderDetail.value = null;
    }
  } catch (error) { Swal.fire('Lỗi', 'Không thể tải danh sách đơn hàng', 'error'); } 
  finally { isLoading.value = false; }
};

const resetFilters = () => { searchKeyword.value = ''; filterStatus.value = ''; dateFrom.value = ''; dateTo.value = ''; loadOrders(0); };
const onDateFilterChanged = async () => { if (dateFrom.value && dateTo.value && dateFrom.value > dateTo.value) { await Swal.fire('Khoảng ngày không hợp lệ', 'Từ ngày phải nhỏ hơn hoặc bằng Đến ngày', 'warning'); return; } await loadOrders(0); };
const escapeHtml = (value) => { if (!value) return ''; return String(value).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;'); };

const openAdminImportDialog = async () => {
  if (!companiesForImport.value.length) await loadCompaniesForImport();
  if (!companiesForImport.value.length) { Swal.fire('Lỗi', 'Không tải được danh sách công ty', 'error'); return; }

  const options = companiesForImport.value.map(c => `<option value="${c.id}">${escapeHtml(c.companyName)}${c.taxCode ? ` - ${escapeHtml(c.taxCode)}` : ''}</option>`).join('');

  const { value } = await Swal.fire({
    title: 'Import đơn hàng từ Excel',
    html: `
      <div class="text-start">
        <label class="form-label fw-bold mb-1 small text-muted text-uppercase">Công ty nhận đơn</label>
        <select id="swal-company" class="form-select mb-3">${options}</select>
        <label class="form-label fw-bold mb-1 small text-muted text-uppercase">File Excel (.xlsx/.xls)</label>
        <input id="swal-import-file" type="file" class="form-control" accept=".xlsx,.xls">
      </div>
    `,
    showCancelButton: true, confirmButtonText: 'Bắt đầu Import', confirmButtonColor: '#0b2e59', cancelButtonText: 'Hủy',
    preConfirm: () => {
      const companyId = Number(document.getElementById('swal-company')?.value || 0);
      const file = document.getElementById('swal-import-file')?.files?.[0];
      if (!companyId) { Swal.showValidationMessage('Vui lòng chọn công ty'); return false; }
      if (!file) { Swal.showValidationMessage('Vui lòng chọn file Excel'); return false; }
      return { companyId, file };
    },
  });

  if (value) await submitAdminImport(value.companyId, value.file);
};

const submitAdminImport = async (companyId, file) => {
  importing.value = true;
  try {
    const formData = new FormData();
    formData.append('companyId', String(companyId)); formData.append('file', file);
    await apiClient.post('/orders/admin-import', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
    selectedItemIds.value = []; expandedOrderId.value = null; orderDetail.value = null;
    await loadOrders(0);
    Swal.fire('Thành công', 'Import vào bảng tạm thành công. Đơn đang chờ duyệt.', 'success');
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể import đơn hàng', 'error'); } 
  finally { importing.value = false; }
};

const toggleDetails = async (id) => {
  if (expandedOrderId.value === id) { expandedOrderId.value = null; orderDetail.value = null; return; }
  expandedOrderId.value = id; detailLoading.value = true;
  try {
    const row = orders.value.find(o => o.id === id);
    const detailUrl = row?.isTempImport ? `/orders/imports/${id}` : `/orders/${id}`;
    const response = await apiClient.get(detailUrl);
    orderDetail.value = response.data;
    if (!canSelectShipmentItems(response.data)) { selectedItemIds.value = selectedItemIds.value.filter(itemId => !(orderDetail.value?.items || []).some(i => i.id === itemId)); }
  } catch (error) { console.error('Failed to load order detail:', error); } 
  finally { detailLoading.value = false; }
};

const canSelectShipmentItems = (order) => { return shipmentEligibleStatuses.includes(order?.status); };

const currentDateGroups = computed(() => {
  if (!orderDetail.value?.items) return [];
  const groups = {};
  for (const item of orderDetail.value.items) {
    const key = item.deliveryDate || '__none__';
    if (!groups[key]) { groups[key] = { date: key, label: key === '__none__' ? 'Chưa có ngày' : formatDateShort(key), items: [] }; }
    groups[key].items.push(item);
  }
  return Object.values(groups).sort((a, b) => {
    if (a.date === '__none__') return 1; if (b.date === '__none__') return -1;
    return a.date.localeCompare(b.date);
  });
});

const isDateGroupSelected = (date) => {
  const group = currentDateGroups.value.find(g => g.date === date);
  if (!group) return false; return group.items.every(item => selectedItemIds.value.includes(item.id));
};

const toggleDateGroup = (date) => {
  const group = currentDateGroups.value.find(g => g.date === date);
  if (!group) return;
  const groupItemIds = group.items.map(i => i.id);
  if (isDateGroupSelected(date)) { selectedItemIds.value = selectedItemIds.value.filter(id => !groupItemIds.includes(id)); } 
  else { const newIds = groupItemIds.filter(id => !selectedItemIds.value.includes(id)); selectedItemIds.value = [...selectedItemIds.value, ...newIds]; }
};

const selectAllItems = () => {
  if (!orderDetail.value?.items) return;
  const allIds = orderDetail.value.items.map(i => i.id);
  const newIds = allIds.filter(id => !selectedItemIds.value.includes(id));
  selectedItemIds.value = [...selectedItemIds.value, ...newIds];
};

const clearItemSelection = () => {
  if (!orderDetail.value?.items) return;
  const orderItemIds = orderDetail.value.items.map(i => i.id);
  selectedItemIds.value = selectedItemIds.value.filter(id => !orderItemIds.includes(id));
};

const allItemsInOrderSelected = computed(() => {
  if (!orderDetail.value?.items?.length) return false; return orderDetail.value.items.every(i => selectedItemIds.value.includes(i.id));
});

const toggleAllItemsInOrder = (e) => { if (e.target.checked) selectAllItems(); else clearItemSelection(); };
const toggleItemSelection = (itemId) => { const idx = selectedItemIds.value.indexOf(itemId); if (idx >= 0) selectedItemIds.value.splice(idx, 1); else selectedItemIds.value.push(itemId); };
const getSelectedCountForOrder = (orderId) => { if (!orderDetail.value?.items || expandedOrderId.value !== orderId) return 0; return orderDetail.value.items.filter(i => selectedItemIds.value.includes(i.id)).length; };

const calculatedTotal = computed(() => {
  if (!selectedOrder.value?.items) return 0;
  return selectedOrder.value.items.filter(item => item.reviewStatus === 'APPROVED' && item.totalItemPrice).reduce((sum, item) => sum + Number(item.totalItemPrice), 0);
});

const openReviewModal = async (order) => {
  try {
    const detailUrl = order?.isTempImport ? `/orders/imports/${order.id}` : `/orders/${order.id}`;
    const response = await apiClient.get(detailUrl);
    selectedOrder.value = { ...response.data, isTempImport: !!order?.isTempImport };
    activeDetailTab.value = 'materials';
    initializeReviewDrafts(selectedOrder.value.items || []);
    await preloadDefaultPricesForDrafts(selectedOrder.value.items || []);
    await nextTick();
    if (!bsModal && reviewModalRef.value) { bsModal = new Modal(reviewModalRef.value); }
    bsModal?.show();
    loadOrderHistory(selectedOrder.value);
  } catch (error) { Swal.fire('Lỗi', 'Không thể tải chi tiết đơn hàng', 'error'); }
};

const loadOrderHistory = async (order) => {
  if (!order?.id || order?.isTempImport) { orderHistoryEvents.value = []; orderRevisionSummaries.value = []; return; }
  isLoadingOrderHistory.value = true;
  try {
    const [history, revisions] = await Promise.all([ apiClient.get(`/orders/${order.id}/history`), apiClient.get(`/orders/${order.id}/revisions`) ]);
    orderHistoryEvents.value = Array.isArray(history.data) ? history.data : [];
    orderRevisionSummaries.value = Array.isArray(revisions.data) ? revisions.data : [];
  } catch (error) { orderHistoryEvents.value = []; orderRevisionSummaries.value = []; } 
  finally { isLoadingOrderHistory.value = false; }
};

const isCancelEventType = (eventType) => { return eventType === 'ORDER_CANCELLED' || eventType === 'IMPORT_REJECTED'; };

const historyEventMetaById = computed(() => {
  const source = Array.isArray(orderHistoryEvents.value) ? [...orderHistoryEvents.value] : [];
  source.sort((a, b) => { const ta = a?.createdAt ? new Date(a.createdAt).getTime() : 0; const tb = b?.createdAt ? new Date(b.createdAt).getTime() : 0; if (ta !== tb) return ta - tb; return Number(a?.id || 0) - Number(b?.id || 0); });
  let cancelCount = 0; const result = {};
  for (const event of source) {
    const eventId = event?.id; if (!eventId) continue;
    const isCancelEvent = isCancelEventType(event?.eventType);
    if (isCancelEvent) cancelCount += 1;
    result[eventId] = { cancelNo: isCancelEvent ? cancelCount : null };
  }
  return result;
});

const getHistoryMeta = (event) => { if (!event?.id) return {}; return historyEventMetaById.value[event.id] || {}; };
const getCancelSequence = (event) => { const value = getHistoryMeta(event).cancelNo; return Number.isFinite(value) ? value : null; };
const totalCancelAttempts = computed(() => { return (orderHistoryEvents.value || []).filter((event) => isCancelEventType(event?.eventType)).length; });

const sortedOrderHistoryEvents = computed(() => {
  const source = Array.isArray(orderHistoryEvents.value) ? [...orderHistoryEvents.value] : [];
  source.sort((a, b) => { const ta = a?.createdAt ? new Date(a.createdAt).getTime() : 0; const tb = b?.createdAt ? new Date(b.createdAt).getTime() : 0; if (ta !== tb) return tb - ta; return Number(b?.id || 0) - Number(a?.id || 0); });
  return source;
});

const getHistoryNoteText = (event) => {
  const note = event?.note ? String(event.note).trim() : ''; if (!note) return '';
  if (event?.eventType === 'ORDER_CANCELLED') return `Lý do hủy: ${note}`; return note;
};

const getHistoryEventTitle = (event) => {
  if (!event) return 'Cập nhật đơn hàng';
  const fromStatus = event.fromStatus ? getOrderStatusLabel(event.fromStatus) : null;
  const toStatus = event.toStatus ? getOrderStatusLabel(event.toStatus) : null;
  switch (event.eventType) {
    case 'IMPORT_APPROVED': return 'Đã duyệt import dữ liệu đơn';
    case 'IMPORT_REJECTED': return 'Import chờ duyệt đã bị hủy';
    case 'ORDER_CANCELLED': return 'Đơn hàng đã bị hủy';
    case 'CUSTOMER_CONFIRMED_RECEIVED': return 'Khách hàng xác nhận đã nhận hàng';
    case 'QUOTE_SET': return 'Đã gửi báo giá';
    case 'PAYMENT_CONFIRMED': return 'Đã xác nhận thanh toán';
    case 'AUTO_COMPLETED': return 'Hệ thống tự động hoàn thành đơn';
    default:
      if (fromStatus && toStatus) return `Chuyển trạng thái: ${fromStatus} -> ${toStatus}`;
      if (toStatus) return `Cập nhật trạng thái: ${toStatus}`;
      return 'Cập nhật đơn hàng';
  }
};

const getHistoryActorText = (event) => {
  if (event?.actorName) return event.actorName;
  if (event?.actorRole === 'ADMIN') return 'Admin';
  if (event?.actorRole === 'CUSTOMER') return 'Khách hàng';
  return 'Hệ thống';
};

const initializeReviewDrafts = (items, options = {}) => {
  const { preserveExisting = false } = options; const nextDrafts = {};
  for (const item of items) {
    const existingDraft = reviewDrafts.value[item.id] || {};
    nextDrafts[item.id] = { unitPrice: item.unitPrice ?? (preserveExisting ? (existingDraft.unitPrice ?? '') : ''), adminNote: item.adminNote || (preserveExisting ? (existingDraft.adminNote || '') : '') };
  }
  reviewDrafts.value = nextDrafts;
};

const preloadDefaultPricesForDrafts = async (items) => {
  const targets = (items || []).filter(item => item?.drawingNumber && (reviewDrafts.value[item.id]?.unitPrice === '' || reviewDrafts.value[item.id]?.unitPrice === null));
  const uniqueDrawingNumbers = [...new Set(targets.map(item => item.drawingNumber))];
  if (!uniqueDrawingNumbers.length) return;
  await Promise.all(uniqueDrawingNumbers.map(async (drawingNumber) => {
    try {
      const res = await apiClient.get(`/warehouse/categories/by-drawing/${encodeURIComponent(drawingNumber)}`);
      const defaultPrice = Number(res.data?.defaultPrice || 0);
      if (defaultPrice > 0) {
        for (const item of targets) {
          if (item.drawingNumber === drawingNumber) {
            const draft = ensureDraft(item);
            if (draft.unitPrice === '' || draft.unitPrice === null) { draft.unitPrice = defaultPrice; }
          }
        }
      }
    } catch (e) { }
  }));
};

const ensureDraft = (item) => {
  if (!reviewDrafts.value[item.id]) { reviewDrafts.value[item.id] = { unitPrice: item.unitPrice ?? '', adminNote: item.adminNote || '' }; }
  return reviewDrafts.value[item.id];
};

const getReviewDraft = (item) => ensureDraft(item);
const setDraftUnitPrice = (item, value) => { const draft = ensureDraft(item); draft.unitPrice = value; };
const setDraftNote = (item, value) => { const draft = ensureDraft(item); draft.adminNote = value; };

const syncUpdatedOrderState = (updatedOrder) => {
  selectedOrder.value = updatedOrder;
  initializeReviewDrafts(updatedOrder.items || [], { preserveExisting: true });
  const idx = orders.value.findIndex(o => o.id === updatedOrder.id);
  if (idx !== -1) { orders.value[idx] = { ...updatedOrder, selected: orders.value[idx].selected }; }
  if (expandedOrderId.value === updatedOrder.id) { orderDetail.value = { ...updatedOrder }; }
};

const reviewItem = async (item, status) => {
  const draft = ensureDraft(item);
  const note = (draft.adminNote || '').trim();

  if (status === 'APPROVED') {
    let unitPrice = Number(draft.unitPrice || 0);
    if (unitPrice <= 0 && item.drawingNumber) {
      try {
        const res = await apiClient.get(`/warehouse/categories/by-drawing/${encodeURIComponent(item.drawingNumber)}`);
        if (res.data?.defaultPrice && Number(res.data.defaultPrice) > 0) {
          unitPrice = Number(res.data.defaultPrice); draft.unitPrice = unitPrice;
        }
      } catch (e) {}
    }
    if (unitPrice <= 0) { await Swal.fire('Thiếu đơn giá', 'Vui lòng nhập đơn giá hợp lệ trước khi duyệt', 'warning'); return; }
    await sendReview(item.id, 'APPROVED', unitPrice, note || null);
    return;
  }

  if (!note) { await Swal.fire('Thiếu ghi chú', 'Vui lòng nhập lý do khi từ chối hoặc cần trao đổi', 'warning'); return; }
  await sendReview(item.id, status, null, note);
};

const sendReview = async (itemId, reviewStatus, unitPrice, adminNote) => {
  try {
    const response = await apiClient.put(`/orders/${selectedOrder.value.id}/items/${itemId}/review`, { reviewStatus, unitPrice, adminNote });
    syncUpdatedOrderState(response.data);
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể review sản phẩm', 'error'); }
};

const approveOrder = async (order) => {
  const result = await Swal.fire({ title: 'Duyệt đơn hàng?', html: `<p>Đơn <strong>${order.orderNumber}</strong> sẽ chuyển sang <strong>Chờ báo giá</strong>.</p>`, icon: 'question', showCancelButton: true, confirmButtonText: '✅ Duyệt đơn', confirmButtonColor: '#f59e0b' });
  if (!result.isConfirmed) return;
  try {
    if (order?.isTempImport) { await apiClient.post(`/orders/imports/${order.id}/approve`); } 
    else { await apiClient.put(`/orders/${order.id}/status`, null, { params: { status: 'PENDING_QUOTE' } }); }
    Swal.fire('Thành công', 'Đơn hàng đã chuyển sang trạng thái Chờ báo giá', 'success');
    loadOrders(currentPage.value); bsModal?.hide();
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể duyệt đơn hàng', 'error'); }
};

const rejectOrder = async (order) => {
  if (order?.status !== 'PENDING_APPROVAL') return;
  if (!order?.isTempImport) { await Swal.fire('Không hỗ trợ', 'Chỉ có thể từ chối đơn import đang chờ duyệt.', 'info'); return; }
  const result = await Swal.fire({ title: 'Từ chối đơn hàng?', input: 'textarea', inputLabel: 'Lý do từ chối', inputValidator: (value) => (!value || !value.trim()) ? 'Vui lòng nhập lý do' : null, icon: 'warning', showCancelButton: true, confirmButtonText: 'Từ chối đơn', confirmButtonColor: '#dc3545' });
  if (!result.isConfirmed) return;
  try {
    await apiClient.put(`/orders/imports/${order.id}/cancel`, { reason: (result.value || '').trim() });
    await Swal.fire('Đã từ chối', 'Đơn hàng chờ duyệt đã được từ chối.', 'success');
    await loadOrders(currentPage.value); bsModal?.hide();
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể từ chối', 'error'); }
};

const canAdminCancelRegularOrder = (order) => {
  if (!order || order?.isTempImport) return false;
  return ['PENDING_APPROVAL', 'PENDING_QUOTE', 'AWAITING_PAYMENT', 'DEPOSITED', 'PROCESSING', 'AWAITING_REMAINING_PAYMENT'].includes(order.status);
};

const cancelRegularOrder = async (order) => {
  if (!canAdminCancelRegularOrder(order)) return;
  const result = await Swal.fire({ title: 'Hủy đơn hàng?', input: 'textarea', inputLabel: 'Lý do hủy đơn', inputValidator: (value) => (!value || !value.trim()) ? 'Vui lòng nhập lý do' : null, icon: 'warning', showCancelButton: true, confirmButtonText: 'Hủy đơn', confirmButtonColor: '#dc3545' });
  if (!result.isConfirmed) return;
  try {
    await apiClient.put(`/orders/${order.id}/status`, { reason: (result.value || '').trim() }, { params: { status: 'CANCELLED' } });
    await Swal.fire('Thành công', `Đơn ${order.orderNumber} đã được hủy.`, 'success');
    await loadOrders(currentPage.value); bsModal?.hide();
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể hủy đơn', 'error'); }
};

const submitQuoteFromModal = async () => { await submitQuote(selectedOrder.value); };

const submitQuote = async (order) => {
  const total = calculatedTotal.value || calculateOrderTotal(order);
  const { value: notes } = await Swal.fire({ title: `Gửi báo giá — ${order.orderNumber}`, html: `<p>Tổng: <strong>${formatCurrency(total)}</strong></p><p class="text-muted small">Cọc (70%): ${formatCurrency(total * 0.7)}</p>`, input: 'textarea', inputLabel: 'Ghi chú cho khách hàng', showCancelButton: true, confirmButtonText: 'Gửi báo giá', confirmButtonColor: '#198754' });
  if (notes !== undefined) {
    try {
      await apiClient.put(`/orders/${order.id}/quote`, { notes: notes || null });
      Swal.fire('Thành công', 'Báo giá đã được gửi', 'success');
      bsModal?.hide(); loadOrders(currentPage.value);
    } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể gửi báo giá', 'error'); }
  }
};

const startProcessing = async (order) => {
  const result = await Swal.fire({ title: 'Bắt đầu gia công?', icon: 'question', showCancelButton: true, confirmButtonText: 'Bắt đầu gia công', confirmButtonColor: '#198754' });
  if (result.isConfirmed) {
    try {
      await apiClient.post(`/orders/${order.id}/payment-confirm`);
      Swal.fire('Thành công!', 'Đơn hàng chuyển sang Đang gia công', 'success');
      loadOrders(currentPage.value);
    } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật', 'error'); }
  }
};

const finishing = ref(false);
const shipping = ref(false);

const finishProcessing = async (order) => {
  const result = await Swal.fire({ title: 'Hoàn thành gia công?', icon: 'question', showCancelButton: true, confirmButtonText: 'Xác nhận hoàn thành', confirmButtonColor: '#f59e0b' });
  if (!result.isConfirmed) return;
  finishing.value = true;
  try {
    await apiClient.put(`/orders/${order.id}/finish-processing`);
    Swal.fire('Thành công!', `Đơn ${order.orderNumber} chờ thanh toán đợt 2`, 'success');
    loadOrders(currentPage.value);
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Lỗi xử lý', 'error'); } 
  finally { finishing.value = false; }
};

const markAsShipping = async (order) => {
  const result = await Swal.fire({ title: 'Xác nhận bàn giao vận chuyển?', icon: 'question', showCancelButton: true, confirmButtonText: 'Xác nhận', confirmButtonColor: '#3b82f6' });
  if (!result.isConfirmed) return;
  shipping.value = true;
  try {
    await apiClient.put(`/orders/${order.id}/ship`);
    await Swal.fire('Thành công', `Đơn ${order.orderNumber} đang giao hàng`, 'success');
    await loadOrders(currentPage.value); bsModal?.hide();
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể chuyển trạng thái', 'error'); } 
  finally { shipping.value = false; }
};

const createShipment = async () => {
  if (selectedItemIds.value.length === 0) return;
  try {
    const res = await apiClient.post('/shipments/preview', { itemIds: selectedItemIds.value });
    sessionStorage.setItem('shipmentPreview', JSON.stringify(res.data));
    sessionStorage.setItem('shipmentItemIds', JSON.stringify(selectedItemIds.value));
    router.push('/admin/invoice-preview');
  } catch (error) { Swal.fire('Lỗi', error.response?.data?.error || 'Không thể tạo phiếu', 'error'); }
};

const calculateOrderTotal = (order) => {
  if (!order?.items) return 0;
  return order.items.filter(item => item.reviewStatus === 'APPROVED' && item.totalItemPrice).reduce((sum, item) => sum + Number(item.totalItemPrice), 0);
};

const getReviewProgress = (order) => {
  if (!order?.items?.length) return '0/0';
  const reviewed = order.items.filter(i => i.reviewStatus && i.reviewStatus !== 'PENDING_REVIEW').length;
  return `${reviewed}/${order.items.length}`;
};

const isAllReviewed = (order) => {
  if (!order?.items?.length) return false;
  return order.items.every(i => i.reviewStatus && i.reviewStatus !== 'PENDING_REVIEW');
};

const getReviewStatusText = (status) => { return getReviewStatusLabel(status); };
const getReviewBadgeClass = (status) => {
  const map = { PENDING_REVIEW: 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25', APPROVED: 'bg-success bg-opacity-10 text-success border border-success border-opacity-25', REJECTED: 'bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25', NEED_DISCUSSION: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50' };
  return map[status] || 'bg-secondary';
};

const getItemRowClass = (item) => {
  const map = { APPROVED: '', REJECTED: 'bg-danger bg-opacity-10', NEED_DISCUSSION: 'bg-warning bg-opacity-10' };
  return map[item.reviewStatus] || '';
};

const openAdminNoteEditor = async (item) => {
  if (selectedOrder.value?.status !== 'PENDING_QUOTE') return;
  const draft = ensureDraft(item);
  const itemName = item?.itemName ? String(item.itemName).trim() : 'Sản phẩm';
  const { value } = await Swal.fire({ title: `Ghi chú - ${itemName}`, input: 'textarea', inputPlaceholder: 'Nhập ghi chú...', inputValue: draft.adminNote || '', showCancelButton: true, confirmButtonText: 'Lưu', confirmButtonColor: '#0b2e59' });
  if (value === undefined) return; setDraftNote(item, value);
};

const openItemNotePopup = async (item) => {
  const note = item?.adminNote ? String(item.adminNote).trim() : ''; if (!note) return;
  const itemName = item?.itemName ? String(item.itemName).trim() : 'Sản phẩm';
  await Swal.fire({ title: `Ghi chú - ${itemName}`, html: `<div style="white-space: pre-wrap; text-align: left;">${escapeHtml(note)}</div>`, width: 650, confirmButtonText: 'Đóng', confirmButtonColor: '#0b2e59' });
};

const getStatusText = (status) => { return getOrderStatusLabel(status, { DEPOSITED: 'Đã cọc' }); };

const getStatusBadgeClass = (status) => {
  const map = {
    PENDING_APPROVAL: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
    PENDING_QUOTE: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
    AWAITING_PAYMENT: 'bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    DEPOSITED: 'bg-success bg-opacity-10 text-success border border-success border-opacity-25',
    PROCESSING: 'bg-navy bg-opacity-10 text-navy border border-navy border-opacity-25',
    AWAITING_REMAINING_PAYMENT: 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-50',
    AWAITING_DELIVERY: 'bg-info bg-opacity-10 text-info border border-info border-opacity-25',
    SHIPPING: 'bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25',
    COMPLETED: 'bg-success bg-opacity-10 text-success border border-success border-opacity-25',
    CANCELLED: 'bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25',
  };
  return map[status] || 'bg-secondary bg-opacity-10 text-secondary border border-secondary';
};

const getStatusDotClass = (status) => {
  const map = {
    PENDING_APPROVAL: 'bg-warning shadow-warning',
    PENDING_QUOTE: 'bg-warning shadow-warning',
    AWAITING_PAYMENT: 'bg-info shadow-info',
    DEPOSITED: 'bg-success shadow-success',
    PROCESSING: 'bg-navy shadow-navy',
    AWAITING_REMAINING_PAYMENT: 'bg-warning shadow-warning',
    AWAITING_DELIVERY: 'bg-info shadow-info',
    SHIPPING: 'bg-primary shadow-primary',
    COMPLETED: 'bg-success shadow-success',
    CANCELLED: 'bg-danger shadow-danger',
  };
  return map[status] || 'bg-secondary';
};

const formatDate = (dateStr) => { if (!dateStr) return ''; return new Date(dateStr).toLocaleDateString('vi-VN'); };
const formatDateShort = (dateStr) => { if (!dateStr) return ''; if (dateStr.includes('-')) { const [year, month, day] = dateStr.split('-'); return `${day}/${month}/${year}`; } return new Date(dateStr).toLocaleDateString('vi-VN'); };
const formatCurrency = (amount) => { if (!amount) return ''; return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount); };
const formatNumber = (amount) => { if (!amount) return ''; return new Intl.NumberFormat('vi-VN').format(amount); };
const formatWeight = (weight) => { if (weight === null || weight === undefined || weight === '') return ''; return `${new Intl.NumberFormat('vi-VN').format(weight)} kg`; };
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
.hover-lift:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.06) !important; }

/* Pill Stats */
.stat-pill-group { perspective: 1000px; }
.stat-pill {
  background: #ffffff; border-radius: 50rem; padding: 10px 22px; 
  border: 1px solid #e2e8f0; display: flex; align-items: center; 
  font-weight: 600; cursor: default; position: relative; overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 2px 5px rgba(0,0,0,0.02); z-index: 1;
}
.stat-pill::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: currentColor; opacity: 0; z-index: -1; transition: opacity 0.3s ease;
}
.stat-pill:hover { transform: translateY(-4px) scale(1.02); box-shadow: 0 10px 20px rgba(0,0,0,0.08); border-color: currentColor; }
.stat-pill:hover::before { opacity: 0.08; }

/* Bảng Separated Row (Bảng ngoài) */
.modern-table { border-collapse: separate; border-spacing: 0 12px; }
.modern-table thead th { border: none; padding-bottom: 0; }
.modern-table tbody tr { border-radius: 12px; transition: all 0.2s; border: 1px solid #f1f5f9; background: #fff; }
.modern-table tbody td { border: none; }
.modern-table tbody td:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.modern-table tbody td:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }
.table-row-hover:hover { transform: translateX(4px); border-color: #cbd5e1 !important; box-shadow: 0 4px 12px rgba(0,0,0,0.05) !important; }
.table-active-row { border-left: 4px solid #0b2e59 !important; background-color: #f8fafc !important; }

/* Form Input */
.custom-input { border: 1px solid #e2e8f0; border-radius: 10px; padding: 0.6rem 1rem; transition: 0.2s; box-shadow: none !important; }
.custom-input:focus { border-color: #0b2e59 !important; box-shadow: 0 0 0 3px rgba(11, 46, 89, 0.1) !important; background-color: #fff; }

/* Nút Action Hình Tròn */
.action-group-wrap { min-width: 0; }
.btn-action-circle {
  width: 36px; height: 36px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center;
  transition: all 0.2s; font-size: 1.05rem; padding: 0;
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

/* Cursor & Form Check */
.cursor-pointer { cursor: pointer; }
.form-check-input { cursor: pointer; width: 1.2em; height: 1.2em; border-radius: 4px; }
.form-check-input:checked { background-color: #0b2e59; border-color: #0b2e59; }

/* Bảng Review bên trong (Expanded & Modal) */
.modern-table-small th { border-bottom: 2px solid #f1f5f9; padding-bottom: 12px; }
.modern-table-small td { border-bottom: 1px solid #f1f5f9; padding-top: 12px; padding-bottom: 12px; }

.review-table th, .review-table td { vertical-align: middle; }
.review-table thead th { white-space: nowrap; font-size: 0.78rem; border-bottom: 2px solid #e2e8f0; }
.review-table tbody td { font-size: 0.84rem; border-bottom: 1px solid #f1f5f9; }

.review-table-comfortable td { padding: 0.75rem 0.5rem; }
.review-table-compact td { padding: 0.4rem 0.35rem; }
.review-table-compact tbody td { font-size: 0.78rem; }

/* Cột dính (Sticky Columns) */
.review-table .sticky-col-left-stt,
.review-table .sticky-col-left-drawing,
.review-table .sticky-col-right-actions { position: sticky; z-index: 3; }
.review-table .sticky-col-left-stt { left: 0; min-width: 52px; }
.review-table .sticky-col-left-drawing { left: 267px; }
.review-table .sticky-col-right-actions { right: 0; }
.review-table thead .sticky-col-left-stt,
.review-table thead .sticky-col-left-drawing,
.review-table thead .sticky-col-right-actions { z-index: 5; }
.review-table tbody .sticky-col-left-drawing { box-shadow: 2px 0 5px rgba(0,0,0,0.02); }
.review-table tbody .sticky-col-right-actions { box-shadow: -2px 0 5px rgba(0,0,0,0.02); }

/* Animation Modal Tabs */
.modal-tab-fade-enter-active, .modal-tab-fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.modal-tab-fade-enter-from, .modal-tab-fade-leave-to { opacity: 0; transform: translateY(5px); }

.review-modal-wide { max-width: 96vw; }

@media (max-width: 992px) {
  .review-table .sticky-col-left-drawing { left: auto; position: static; box-shadow: none; }
}
</style>