<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase">Quản lý xuất hoá đơn & Báo giá</h2>

    <!-- Filters -->
    <div class="card border-0 shadow-sm p-3 mb-4">
      <div class="row g-3 align-items-end justify-content-between">
        <div class="col-md-9">
          <div class="row g-2">
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Tìm kiếm từ khoá</label>
              <input type="text" class="form-control form-control-sm" placeholder="Tìm mã đơn, khách hàng..."
                v-model="searchKeyword" @keyup.enter="loadOrders(0)">
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Trạng thái</label>
              <select class="form-select form-select-sm" v-model="filterStatus" @change="loadOrders(0)">
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
          <button class="btn btn-outline-primary btn-sm px-3" @click="openAdminImportDialog" :disabled="importing">
            <i class="bi bi-upload me-1"></i>{{ importing ? 'Đang import...' : 'Import Excel' }}
          </button>
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
        <i class="bi bi-inbox fs-1 text-muted"></i>
        <p class="text-muted mt-3">Chưa có đơn hàng nào</p>
      </div>
    </div>

    <!-- Orders Table -->
    <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <table class="table align-middle mb-0 custom-hover-table" style="table-layout: fixed;">
        <thead class="bg-light shadow-sm">
          <tr class="small text-muted text-uppercase">
            <th style="width: 40px;" class="text-center">
              <i class="bi bi-chevron-down" style="font-size: 0.7rem;"></i>
            </th>
            <th style="width: 200px;">Mã đơn</th>
            <th>Khách hàng</th>
            <th>Sản phẩm</th>
            <th>Review</th>
            <th style="width: 140px;">Trạng thái</th>
            <th style="width: 140px;">Giá trị</th>
            <th style="width: 110px;">Ngày tạo</th>
            <th style="width: 160px;">Thao tác</th>
          </tr>
        </thead>
        <tbody v-for="order in orders" :key="`${order.isTempImport ? 'imp' : 'ord'}-${order.id}`">
          <tr @click="toggleDetails(order.id)" class="cursor-pointer"
            :class="{ 'table-active-row': expandedOrderId === order.id }">
            <td class="text-center">
              <i class="bi" :class="expandedOrderId === order.id ? 'bi-chevron-down' : 'bi-chevron-right'"
                style="font-size: 0.75rem;"></i>
            </td>
            <td class="fw-bold text-primary">{{ order.orderNumber }}</td>
            <td>
              <div>{{ order.userName }}</div>
              <small class="text-muted">{{ order.companyName }}</small>
            </td>
            <td>
              <span class="badge bg-info-subtle text-info px-3">
                {{ order.items?.length || 0 }} sản phẩm
              </span>
            </td>
            <td>
              <span v-if="order.status === 'PENDING_QUOTE'" class="small">
                {{ getReviewProgress(order) }}
              </span>
                      <span v-else class="text-muted small"></span>
            </td>
            <td>
              <span :class="getStatusBadgeClass(order.status)">{{ getStatusText(order.status) }}</span>
            </td>
            <td>{{ formatCurrency(order.totalPrice) }}</td>
            <td>{{ formatDate(order.createdAt) }}</td>
            <td @click.stop>
              <div class="btn-group btn-group-sm">
                <button @click="openReviewModal(order)" class="btn btn-outline-primary"
                  :title="order.status === 'PENDING_APPROVAL' ? 'Xem chi tiết đơn' : 'Xem & Review'">
                  <i class="bi bi-eye"></i>
                </button>
                <button v-if="order.status === 'PENDING_APPROVAL'"
                  @click="approveOrder(order)" class="btn btn-outline-warning" title="Duyệt đơn">
                  <i class="bi bi-check2-circle"></i>
                </button>
                <button v-if="order.status === 'PENDING_APPROVAL' && order.isTempImport"
                  @click="rejectOrder(order)" class="btn btn-outline-danger" title="Từ chối đơn">
                  <i class="bi bi-x-circle"></i>
                </button>
                <button v-if="order.status === 'PENDING_QUOTE' && isAllReviewed(order)"
                  @click="submitQuote(order)" class="btn btn-outline-success" title="Gửi báo giá">
                  <i class="bi bi-currency-dollar"></i>
                </button>
                <button v-if="order.status === 'DEPOSITED'" @click="startProcessing(order)"
                  class="btn btn-success btn-sm" title="Bắt đầu gia công">
                  <i class="bi bi-play-fill me-1"></i>Gia công
                </button>
                <button v-if="order.status === 'PROCESSING'" @click="finishProcessing(order)"
                  class="btn btn-warning btn-sm" title="Hoàn thành gia công" :disabled="finishing">
                  <i class="bi bi-check-circle-fill me-1"></i>Đã hoàn thành gia công
                </button>
                <button v-if="order.status === 'AWAITING_DELIVERY'" @click="markAsShipping(order)"
                  class="btn btn-primary btn-sm" title="Bàn giao vận chuyển" :disabled="shipping">
                  <i class="bi bi-truck me-1"></i>Giao hàng
                </button>
              </div>
            </td>
          </tr>

          <!-- Expanded Detail Row with Item Selection -->
          <tr v-if="expandedOrderId === order.id">
            <td colspan="9" class="p-0 border-0 shadow-inner">
              <div class="bg-white p-4 border-start border-4 border-primary ms-3 my-2 rounded shadow-sm">
                <!-- Loading detail -->
                <div v-if="detailLoading" class="text-center py-3">
                  <div class="spinner-border spinner-border-sm text-primary"></div>
                </div>

                <template v-else-if="orderDetail">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="fw-bold m-0 text-muted">
                      <i class="bi bi-box-seam me-2"></i>Chi tiết {{ order.orderNumber }}
                    </h6>
                    <span class="badge bg-primary-subtle text-primary px-3">
                      Đã chọn: {{ getSelectedCountForOrder(order.id) }} / {{ orderDetail.items?.length || 0 }}
                    </span>
                  </div>

                  <div v-if="!canSelectShipmentItems(order)" class="alert alert-warning py-2 px-3 small mb-3">
                    Chỉ được chọn sản phẩm để xuất hóa đơn khi đơn đã cọc.
                  </div>

                  <!-- Date Group Quick Select Bar -->
                  <div v-if="canSelectShipmentItems(order) && currentDateGroups.length > 0" class="date-group-bar mb-3">
                    <span class="date-group-label">Chọn nhanh:</span>
                    <button v-for="dg in currentDateGroups" :key="dg.date"
                      @click="toggleDateGroup(dg.date)"
                      class="date-group-tag"
                      :class="{ 'date-group-active': isDateGroupSelected(dg.date) }">
                      <i class="bi" :class="isDateGroupSelected(dg.date) ? 'bi-check-square-fill' : 'bi-square'"></i>
                      {{ dg.label }} <span class="date-group-count">({{ dg.items.length }})</span>
                    </button>
                    <button @click="selectAllItems" class="date-group-tag date-group-all">
                      <i class="bi bi-check-all"></i> Tất cả
                    </button>
                    <button v-if="getSelectedCountForOrder(order.id) > 0"
                      @click="clearItemSelection" class="date-group-tag date-group-clear">
                      <i class="bi bi-x-lg"></i> Bỏ chọn
                    </button>
                  </div>

                  <!-- Items Table -->
                  <table class="table table-sm table-bordered mb-0">
                    <thead class="table-dark">
                      <tr class="text-center small">
                        <th v-if="canSelectShipmentItems(order)" style="width: 36px;">
                          <input type="checkbox" class="form-check-input"
                            :checked="allItemsInOrderSelected"
                            @change="toggleAllItemsInOrder($event)">
                        </th>
                        <th>ITEM CODE</th>
                        <th>BẢN VẼ</th>
                        <th>TÊN LINH KIỆN</th>
                        <th>SL</th>
                        <th style="width: 130px;">NGÀY XUẤT</th>
                        <th style="width: 120px;">KHỐI LƯỢNG</th>
                        <th>ĐƠN GIÁ</th>
                        <th>THÀNH TIỀN</th>
                        <th>REVIEW</th>
                      </tr>
                    </thead>
                    <tbody class="text-center">
                      <tr v-for="item in orderDetail.items" :key="item.id"
                        :class="[getItemRowClass(item), { 'item-selected': selectedItemIds.includes(item.id) }]">
                        <td v-if="canSelectShipmentItems(order)" @click.stop>
                          <input type="checkbox" class="form-check-input"
                            :checked="selectedItemIds.includes(item.id)"
                            @change="toggleItemSelection(item.id)">
                        </td>
                        <td class="fw-bold">{{ item.itemCode || '' }}</td>
                        <td>{{ item.drawingNumber || '' }}</td>
                        <td class="text-start">{{ item.itemName || '' }}</td>
                        <td class="fw-bold text-primary">{{ item.quantity }}</td>
                        <td>
                          <span v-if="item.deliveryDate" class="badge bg-info-subtle text-info">
                            {{ formatDateShort(item.deliveryDate) }}
                          </span>
                          <span v-else class="text-muted"></span>
                        </td>
                        <td>{{ formatWeight(item.weight) }}</td>
                        <td>{{ item.unitPrice ? formatNumber(item.unitPrice) : '' }}</td>
                        <td class="fw-bold">{{ item.totalItemPrice ? formatNumber(item.totalItemPrice) : '' }}</td>
                        <td>
                          <span :class="getReviewBadgeClass(item.reviewStatus)" style="font-size: 0.7rem">
                            {{ getReviewStatusText(item.reviewStatus) }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Footer with item selection & export -->
      <div class="p-3 bg-white border-top d-flex justify-content-between align-items-center">
        <span class="fw-bold text-primary fs-6">
          <i class="bi bi-check-all me-1"></i> Đã chọn: {{ selectedItemIds.length }} sản phẩm
        </span>
        <button class="btn btn-success btn-sm px-4" :disabled="selectedItemIds.length === 0"
          @click="createShipment">
          <i class="bi bi-file-earmark-excel me-2"></i>Tạo phiếu xuất kho ({{ selectedItemIds.length }})
        </button>
      </div>

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

    <!-- Review Modal -->
    <div class="modal fade" id="reviewModal" tabindex="-1" ref="reviewModalRef">
      <div class="modal-dialog modal-xl modal-dialog-scrollable review-modal-wide">
        <div class="modal-content" v-if="selectedOrder">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-clipboard-check me-2"></i>
              {{ selectedOrder.orderNumber }} — {{ selectedOrder.status === 'PENDING_APPROVAL' ? 'Chi tiết đơn chờ duyệt' : 'Review & Báo giá' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <!-- Order Info -->
            <div class="row mb-4">
              <div class="col-md-4">
                <p class="mb-1"><strong>Khách hàng:</strong> {{ selectedOrder.userName }}</p>
                <p class="mb-1"><strong>Công ty:</strong> {{ selectedOrder.companyName }}</p>
              </div>
              <div class="col-md-4">
                <p class="mb-1">
                  <strong>Trạng thái:</strong>
                  <span :class="getStatusBadgeClass(selectedOrder.status)">
                    {{ getStatusText(selectedOrder.status) }}
                  </span>
                </p>
                <p class="mb-1"><strong>Ngày tạo:</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
              </div>
              <div class="col-md-4">
                <p v-if="selectedOrder.status !== 'PENDING_APPROVAL'" class="mb-1"><strong>Tiến trình review:</strong> {{ getReviewProgress(selectedOrder) }}</p>
                <p class="mb-1" v-if="selectedOrder.totalPrice">
                  <strong>Tổng giá trị:</strong> {{ formatCurrency(selectedOrder.totalPrice) }}
                </p>
              </div>
            </div>

            <!-- Items Table -->
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-2">
              <div class="small text-muted">
                <i class="bi bi-arrows-move me-1"></i> Bảng có thể cuộn ngang, dữ liệu vẫn giữ đầy đủ.
              </div>
              <div v-if="selectedOrder.status !== 'PENDING_APPROVAL'" class="btn-group btn-group-sm" role="group" aria-label="Mật độ hiển thị bảng review">
                <button
                  type="button"
                  class="btn"
                  :class="reviewDensity === 'comfortable' ? 'btn-primary' : 'btn-outline-primary'"
                  @click="reviewDensity = 'comfortable'">
                  Comfortable
                </button>
                <button
                  type="button"
                  class="btn"
                  :class="reviewDensity === 'compact' ? 'btn-primary' : 'btn-outline-primary'"
                  @click="reviewDensity = 'compact'">
                  Compact
                </button>
              </div>
            </div>
            <div class="table-responsive" style="max-height: 500px; overflow-y: auto;">
              <table class="table table-bordered table-sm align-middle mb-0 review-table"
                :class="reviewDensity === 'compact' ? 'review-table-compact' : 'review-table-comfortable'">
                <thead class="table-light" style="position: sticky; top: 0; z-index: 1;">
                  <tr>
                    <th class="text-center sticky-col-left-stt" style="width: 52px">STT</th>
                    <th style="min-width: 120px">VNN_NO</th>
                    <th style="min-width: 95px">Item Code</th>
                    <th class="sticky-col-left-drawing" style="min-width: 105px">Drawing No.</th>
                    <th style="min-width: 180px">Tên linh kiện</th>
                    <th style="min-width: 140px">Spec</th>
                    <th style="min-width: 95px">Vật liệu</th>
                    <th class="text-center" style="width: 65px">SL</th>
                    <th class="text-center" style="min-width: 100px">Ngày xuất</th>
                    <th class="text-center" style="min-width: 95px">Khối lượng</th>
                    <th class="text-center" style="min-width: 95px">Review</th>
                    <th class="text-center" style="min-width: 130px">Đơn giá</th>
                    <th class="text-center" style="min-width: 130px">Thành tiền</th>
                    <th style="min-width: 190px">Ghi chú</th>
                    <th class="text-center sticky-col-right-actions" style="min-width: 120px">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in selectedOrder.items" :key="item.id" :class="getItemRowClass(item)">
                    <td class="text-center sticky-col-left-stt">{{ item.stt || (index + 1) }}</td>
                      <td style="font-size: 0.8rem" :title="item.unit || ''">{{ item.unit || '' }}</td>
                      <td style="font-size: 0.8rem" :title="item.itemCode || ''">{{ item.itemCode || '' }}</td>
                    <td class="sticky-col-left-drawing" style="font-size: 0.8rem" :title="item.drawingNumber || ''">{{ item.drawingNumber || '' }}</td>
                      <td class="text-wrap" :title="item.itemName || ''">{{ item.itemName || '' }}</td>
                      <td class="text-wrap" :title="item.specification || ''">{{ item.specification || '' }}</td>
                      <td class="text-wrap" :title="item.material || ''">{{ item.material || '' }}</td>
                    <td class="text-center fw-bold">{{ item.quantity }}</td>
                    <td class="text-center">{{ item.deliveryDate ? formatDateShort(item.deliveryDate) : '' }}</td>
                    <td class="text-center">{{ formatWeight(item.weight) }}</td>
                    <td class="text-center">
                      <span :class="getReviewBadgeClass(item.reviewStatus)" style="font-size: 0.7rem">
                        {{ getReviewStatusText(item.reviewStatus) }}
                      </span>
                    </td>
                    <td class="text-end">
                      <template v-if="selectedOrder.status === 'PENDING_QUOTE'">
                        <input
                          type="number"
                          class="form-control form-control-sm text-end"
                          :value="getReviewDraft(item).unitPrice ?? ''"
                          @input="setDraftUnitPrice(item, $event.target.value)"
                          min="0"
                          step="1000"
                          placeholder="Đơn giá" />
                      </template>
                      <template v-else>
                        <span v-if="item.unitPrice">{{ formatNumber(item.unitPrice) }}</span>
                        <span v-else class="text-muted"></span>
                      </template>
                    </td>
                    <td class="text-end">
                      <span v-if="item.totalItemPrice" class="fw-bold">{{ formatNumber(item.totalItemPrice) }}</span>
                      <span v-else class="text-muted"></span>
                    </td>
                      <td>
                        <textarea
                          v-if="selectedOrder.status === 'PENDING_QUOTE'"
                          class="form-control form-control-sm"
                          rows="2"
                          :value="getReviewDraft(item).adminNote || ''"
                          @input="setDraftNote(item, $event.target.value)"
                          placeholder="Ghi chú / lý do"></textarea>
                        <span v-else class="small">{{ item.adminNote || '' }}</span>
                      </td>
                      <td class="text-center sticky-col-right-actions">
                      <div v-if="selectedOrder.status === 'PENDING_QUOTE'" class="btn-group btn-group-sm">
                        <button @click="reviewItem(item, 'APPROVED')" class="btn btn-outline-success btn-sm" title="Duyệt">
                          <i class="bi bi-check-lg"></i>
                        </button>
                        <button @click="reviewItem(item, 'REJECTED')" class="btn btn-outline-danger btn-sm" title="Từ chối">
                          <i class="bi bi-x-lg"></i>
                        </button>
                        <button @click="reviewItem(item, 'NEED_DISCUSSION')" class="btn btn-outline-warning btn-sm" title="Cần trao đổi">
                          <i class="bi bi-chat-dots"></i>
                        </button>
                      </div>
                      <span v-else class="text-muted small"></span>
                    </td>
                  </tr>
                </tbody>
                <tfoot v-if="calculatedTotal > 0">
                  <tr class="table-light fw-bold">
                    <td colspan="13" class="text-end">Tổng (sản phẩm đã duyệt):</td>
                    <td class="text-end">{{ formatNumber(calculatedTotal) }}</td>
                    <td></td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <!-- Admin notes -->
            <div v-for="item in selectedOrder.items" :key="'note-' + item.id">
              <div v-if="item.adminNote" class="alert alert-sm py-1 px-2 mb-1"
                :class="item.reviewStatus === 'REJECTED' ? 'alert-danger' : 'alert-warning'">
                <small><strong>{{ item.itemName }}:</strong> {{ item.adminNote }}</small>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button
              v-if="selectedOrder.status === 'PENDING_APPROVAL'"
              @click="approveOrder(selectedOrder)"
              class="btn btn-warning text-white">
              <i class="bi bi-check2-circle me-1"></i>Duyệt Đơn
            </button>
            <button
              v-if="selectedOrder.status === 'PENDING_APPROVAL' && selectedOrder.isTempImport"
              @click="rejectOrder(selectedOrder)"
              class="btn btn-danger">
              <i class="bi bi-x-circle me-1"></i>Từ chối đơn
            </button>
            <button
              v-if="selectedOrder.status === 'PENDING_QUOTE' && isAllReviewed(selectedOrder)"
              @click="submitQuoteFromModal()"
              class="btn btn-success">
              <i class="bi bi-send me-1"></i>Gửi Báo Giá ({{ formatCurrency(calculatedTotal) }})
            </button>
            <button
              v-if="selectedOrder.status === 'AWAITING_DELIVERY'"
              @click="markAsShipping(selectedOrder)"
              class="btn btn-primary"
              :disabled="shipping">
              <i class="bi bi-truck me-1"></i>Chuyển sang Đang giao hàng
            </button>
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
let bsModal = null;

// Item-level selection (across all orders)
const selectedItemIds = ref([]);
const shipmentEligibleStatuses = ['DEPOSITED', 'PROCESSING', 'COMPLETED'];

onMounted(() => {
  loadCompaniesForImport();
  loadOrders();
});

const loadCompaniesForImport = async () => {
  try {
    const res = await apiClient.get('/companies', {
      params: { page: 0, size: 200 },
    });
    const content = res.data?.content || [];
    companiesForImport.value = Array.isArray(content) ? content : [];
  } catch (error) {
    console.error('Failed to load companies for import:', error);
    companiesForImport.value = [];
  }
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

      const response = await apiClient.get('/orders', {
        params,
      });
      const data = response.data.content || response.data;
      const mainOrders = (Array.isArray(data) ? data : []).map(o => ({ ...o, selected: false, isTempImport: false }));

      // In "Tất cả", show pending import orders on first page so admin can see and process them quickly.
      if (!filterStatus.value && page === 0) {
        const pendingParams = { page: 0, size: 50 };
        if (searchKeyword.value?.trim()) pendingParams.keyword = searchKeyword.value.trim();

        const pendingResponse = await apiClient.get('/orders/imports/pending', { params: pendingParams });
        const pendingData = pendingResponse.data.content || pendingResponse.data;
        const pendingOrders = (Array.isArray(pendingData) ? pendingData : []).map(o => ({
          ...o,
          selected: false,
          isTempImport: true,
        }));

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
      expandedOrderId.value = null;
      orderDetail.value = null;
    }
  } catch (error) {
    console.error('Failed to load orders:', error);
    Swal.fire('Lỗi', 'Không thể tải danh sách đơn hàng', 'error');
  } finally {
    isLoading.value = false;
  }
};

const resetFilters = () => {
  searchKeyword.value = '';
  filterStatus.value = '';
  dateFrom.value = '';
  dateTo.value = '';
  loadOrders(0);
};

const onDateFilterChanged = async () => {
  if (dateFrom.value && dateTo.value && dateFrom.value > dateTo.value) {
    await Swal.fire('Khoảng ngày không hợp lệ', 'Từ ngày phải nhỏ hơn hoặc bằng Đến ngày', 'warning');
    return;
  }
  await loadOrders(0);
};

const escapeHtml = (value) => {
  if (!value) return '';
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
};

const openAdminImportDialog = async () => {
  if (!companiesForImport.value.length) {
    await loadCompaniesForImport();
  }

  if (!companiesForImport.value.length) {
    Swal.fire('Lỗi', 'Không tải được danh sách công ty để import', 'error');
    return;
  }

  const options = companiesForImport.value
    .map(c => `<option value="${c.id}">${escapeHtml(c.companyName)}${c.taxCode ? ` - ${escapeHtml(c.taxCode)}` : ''}</option>`)
    .join('');

  const { value } = await Swal.fire({
    title: 'Import đơn hàng từ Excel',
    html: `
      <div class="text-start">
        <label class="form-label fw-bold mb-1">Công ty nhận đơn</label>
        <select id="swal-company" class="swal2-select" style="display:block;width:100%;margin:0 0 12px 0;">
          ${options}
        </select>
        <label class="form-label fw-bold mb-1">File Excel (.xlsx/.xls)</label>
        <input id="swal-import-file" type="file" class="swal2-file" accept=".xlsx,.xls" style="display:block;width:100%;">
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: 'Import',
    cancelButtonText: 'Hủy',
    preConfirm: () => {
      const companyEl = document.getElementById('swal-company');
      const fileEl = document.getElementById('swal-import-file');
      const companyId = Number(companyEl?.value || 0);
      const file = fileEl?.files?.[0];

      if (!companyId) {
        Swal.showValidationMessage('Vui lòng chọn công ty');
        return false;
      }
      if (!file) {
        Swal.showValidationMessage('Vui lòng chọn file Excel');
        return false;
      }
      return { companyId, file };
    },
  });

  if (value) {
    await submitAdminImport(value.companyId, value.file);
  }
};

const submitAdminImport = async (companyId, file) => {
  importing.value = true;
  try {
    const formData = new FormData();
    formData.append('companyId', String(companyId));
    formData.append('file', file);

    await apiClient.post('/orders/admin-import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    selectedItemIds.value = [];
    expandedOrderId.value = null;
    orderDetail.value = null;
    await loadOrders(0);
    Swal.fire('Thành công', 'Import vào bảng tạm thành công. Đơn đang chờ duyệt.', 'success');
  } catch (error) {
    console.error('Failed to import order by admin:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể import đơn hàng', 'error');
  } finally {
    importing.value = false;
  }
};

// ===== Expand / Detail =====

const toggleDetails = async (id) => {
  if (expandedOrderId.value === id) {
    expandedOrderId.value = null;
    orderDetail.value = null;
    return;
  }
  expandedOrderId.value = id;
  detailLoading.value = true;
  try {
    const row = orders.value.find(o => o.id === id);
    const detailUrl = row?.isTempImport ? `/orders/imports/${id}` : `/orders/${id}`;
    const response = await apiClient.get(detailUrl);
    orderDetail.value = response.data;

    if (!canSelectShipmentItems(response.data)) {
      selectedItemIds.value = selectedItemIds.value.filter(itemId =>
        !(orderDetail.value?.items || []).some(i => i.id === itemId),
      );
    }
  } catch (error) {
    console.error('Failed to load order detail:', error);
  } finally {
    detailLoading.value = false;
  }
};

const canSelectShipmentItems = (order) => {
  return shipmentEligibleStatuses.includes(order?.status);
};

// ===== Date Group Quick Select =====

const currentDateGroups = computed(() => {
  if (!orderDetail.value?.items) return [];
  const groups = {};
  for (const item of orderDetail.value.items) {
    const key = item.deliveryDate || '__none__';
    if (!groups[key]) {
      groups[key] = {
        date: key,
        label: key === '__none__' ? 'Chưa có ngày' : formatDateShort(key),
        items: []
      };
    }
    groups[key].items.push(item);
  }
  return Object.values(groups).sort((a, b) => {
    if (a.date === '__none__') return 1;
    if (b.date === '__none__') return -1;
    return a.date.localeCompare(b.date);
  });
});

const isDateGroupSelected = (date) => {
  const group = currentDateGroups.value.find(g => g.date === date);
  if (!group) return false;
  return group.items.every(item => selectedItemIds.value.includes(item.id));
};

const toggleDateGroup = (date) => {
  const group = currentDateGroups.value.find(g => g.date === date);
  if (!group) return;
  const groupItemIds = group.items.map(i => i.id);
  if (isDateGroupSelected(date)) {
    selectedItemIds.value = selectedItemIds.value.filter(id => !groupItemIds.includes(id));
  } else {
    const newIds = groupItemIds.filter(id => !selectedItemIds.value.includes(id));
    selectedItemIds.value = [...selectedItemIds.value, ...newIds];
  }
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
  if (!orderDetail.value?.items?.length) return false;
  return orderDetail.value.items.every(i => selectedItemIds.value.includes(i.id));
});

const toggleAllItemsInOrder = (e) => {
  if (e.target.checked) selectAllItems();
  else clearItemSelection();
};

const toggleItemSelection = (itemId) => {
  const idx = selectedItemIds.value.indexOf(itemId);
  if (idx >= 0) selectedItemIds.value.splice(idx, 1);
  else selectedItemIds.value.push(itemId);
};

const getSelectedCountForOrder = (orderId) => {
  if (!orderDetail.value?.items || expandedOrderId.value !== orderId) return 0;
  return orderDetail.value.items.filter(i => selectedItemIds.value.includes(i.id)).length;
};

// ===== Review Modal =====

const calculatedTotal = computed(() => {
  if (!selectedOrder.value?.items) return 0;
  return selectedOrder.value.items
    .filter(item => item.reviewStatus === 'APPROVED' && item.totalItemPrice)
    .reduce((sum, item) => sum + Number(item.totalItemPrice), 0);
});

const openReviewModal = async (order) => {
  try {
    const detailUrl = order?.isTempImport ? `/orders/imports/${order.id}` : `/orders/${order.id}`;
    const response = await apiClient.get(detailUrl);
    selectedOrder.value = { ...response.data, isTempImport: !!order?.isTempImport };
    initializeReviewDrafts(selectedOrder.value.items || []);
    await preloadDefaultPricesForDrafts(selectedOrder.value.items || []);
    await nextTick();

    if (!bsModal && reviewModalRef.value) {
      bsModal = new Modal(reviewModalRef.value);
    }
    bsModal?.show();
  } catch (error) {
    console.error('Failed to load order detail:', error);
    Swal.fire('Lỗi', 'Không thể tải chi tiết đơn hàng', 'error');
  }
};

const initializeReviewDrafts = (items, options = {}) => {
  const { preserveExisting = false } = options;
  const nextDrafts = {};
  for (const item of items) {
    const existingDraft = reviewDrafts.value[item.id] || {};
    nextDrafts[item.id] = {
      unitPrice: item.unitPrice ?? (preserveExisting ? (existingDraft.unitPrice ?? '') : ''),
      adminNote: item.adminNote || (preserveExisting ? (existingDraft.adminNote || '') : ''),
    };
  }
  reviewDrafts.value = nextDrafts;
};

const preloadDefaultPricesForDrafts = async (items) => {
  const targets = (items || []).filter(
    item => item?.drawingNumber && (reviewDrafts.value[item.id]?.unitPrice === '' || reviewDrafts.value[item.id]?.unitPrice === null),
  );

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
            if (draft.unitPrice === '' || draft.unitPrice === null) {
              draft.unitPrice = defaultPrice;
            }
          }
        }
      }
    } catch (e) {
      console.warn('Could not preload default price for drawing:', drawingNumber);
    }
  }));
};

const ensureDraft = (item) => {
  if (!reviewDrafts.value[item.id]) {
    reviewDrafts.value[item.id] = {
      unitPrice: item.unitPrice ?? '',
      adminNote: item.adminNote || '',
    };
  }
  return reviewDrafts.value[item.id];
};

const getReviewDraft = (item) => ensureDraft(item);

const setDraftUnitPrice = (item, value) => {
  const draft = ensureDraft(item);
  draft.unitPrice = value;
};

const setDraftNote = (item, value) => {
  const draft = ensureDraft(item);
  draft.adminNote = value;
};

const syncUpdatedOrderState = (updatedOrder) => {
  selectedOrder.value = updatedOrder;
  initializeReviewDrafts(updatedOrder.items || [], { preserveExisting: true });

  const idx = orders.value.findIndex(o => o.id === updatedOrder.id);
  if (idx !== -1) {
    orders.value[idx] = { ...updatedOrder, selected: orders.value[idx].selected };
  }

  if (expandedOrderId.value === updatedOrder.id) {
    orderDetail.value = { ...updatedOrder };
  }
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
          unitPrice = Number(res.data.defaultPrice);
          draft.unitPrice = unitPrice;
        }
      } catch (e) {
        console.warn('Could not fetch default price for drawing:', item.drawingNumber);
      }
    }

    if (unitPrice <= 0) {
      await Swal.fire('Thiếu đơn giá', 'Vui lòng nhập đơn giá hợp lệ trước khi duyệt', 'warning');
      return;
    }

    await sendReview(item.id, 'APPROVED', unitPrice, note || null);
    return;
  }

  if (!note) {
    await Swal.fire('Thiếu ghi chú', 'Vui lòng nhập lý do khi từ chối hoặc cần trao đổi', 'warning');
    return;
  }

  await sendReview(item.id, status, null, note);
};

const sendReview = async (itemId, reviewStatus, unitPrice, adminNote) => {
  try {
    const response = await apiClient.put(
      `/orders/${selectedOrder.value.id}/items/${itemId}/review`,
      { reviewStatus, unitPrice, adminNote },
    );
    syncUpdatedOrderState(response.data);
  } catch (error) {
    console.error('Failed to review item:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể review sản phẩm', 'error');
  }
};

const approveOrder = async (order) => {
  const result = await Swal.fire({
    title: 'Duyệt đơn hàng?',
    html: `
      <p>Đơn <strong>${order.orderNumber}</strong> sẽ chuyển sang <strong>Chờ báo giá</strong>.</p>
      <p class="text-muted small">Sau khi duyệt, admin có thể review item và gửi báo giá.</p>
    `,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '✅ Duyệt đơn',
    confirmButtonColor: '#f59e0b',
    cancelButtonText: 'Hủy',
  });

  if (!result.isConfirmed) return;

  try {
    if (order?.isTempImport) {
      await apiClient.post(`/orders/imports/${order.id}/approve`);
    } else {
      await apiClient.put(`/orders/${order.id}/status`, null, {
        params: { status: 'PENDING_QUOTE' },
      });
    }
    Swal.fire('Thành công', 'Đơn hàng đã chuyển sang trạng thái Chờ báo giá', 'success');
    loadOrders(currentPage.value);
    bsModal?.hide();
  } catch (error) {
    console.error('Failed to approve order:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể duyệt đơn hàng', 'error');
  }
};

const rejectOrder = async (order) => {
  if (order?.status !== 'PENDING_APPROVAL') return;
  if (!order?.isTempImport) {
    await Swal.fire('Không hỗ trợ', 'Chỉ có thể từ chối đơn import đang chờ duyệt.', 'info');
    return;
  }

  const result = await Swal.fire({
    title: 'Từ chối đơn hàng?',
    html: `
      <p>Đơn <strong>${order.orderNumber}</strong> sẽ bị từ chối và không chuyển sang bước báo giá.</p>
      <p class="text-muted small">Bạn vẫn có thể import lại dữ liệu nếu cần.</p>
    `,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Từ chối đơn',
    confirmButtonColor: '#dc3545',
    cancelButtonText: 'Hủy',
  });

  if (!result.isConfirmed) return;

  try {
    await apiClient.put(`/orders/imports/${order.id}/cancel`);
    await Swal.fire('Đã từ chối', 'Đơn hàng chờ duyệt đã được từ chối.', 'success');
    await loadOrders(currentPage.value);
    bsModal?.hide();
  } catch (error) {
    console.error('Failed to reject order:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể từ chối đơn hàng', 'error');
  }
};

const submitQuoteFromModal = async () => {
  await submitQuote(selectedOrder.value);
};

const submitQuote = async (order) => {
  const total = calculatedTotal.value || calculateOrderTotal(order);
  const { value: notes } = await Swal.fire({
    title: `Gửi báo giá — ${order.orderNumber}`,
    html: `
      <p>Tổng giá trị: <strong>${formatCurrency(total)}</strong></p>
      <p class="text-muted small">Tiền cọc (70%): ${formatCurrency(total * 0.7)}</p>
    `,
    input: 'textarea',
    inputLabel: 'Ghi chú cho khách hàng (tùy chọn)',
    inputPlaceholder: 'VD: Thời gian giao hàng dự kiến 2 tuần...',
    showCancelButton: true,
    confirmButtonText: 'Gửi báo giá',
    confirmButtonColor: '#198754',
    cancelButtonText: 'Hủy',
  });

  if (notes !== undefined) {
    try {
      await apiClient.put(`/orders/${order.id}/quote`, { notes: notes || null });
      Swal.fire('Thành công', 'Báo giá đã được gửi đến khách hàng', 'success');
      bsModal?.hide();
      loadOrders(currentPage.value);
    } catch (error) {
      console.error('Failed to submit quote:', error);
      Swal.fire('Lỗi', error.response?.data?.error || 'Không thể gửi báo giá', 'error');
    }
  }
};

const startProcessing = async (order) => {
  const result = await Swal.fire({
    title: 'Bắt đầu gia công?',
    html: `
      <p>Đơn hàng <strong>${order.orderNumber}</strong> đã cọc thành công.</p>
      <p class="text-muted">Xác nhận để chuyển sang trạng thái <strong>Đang gia công</strong>.</p>
    `,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '✅ Bắt đầu gia công',
    confirmButtonColor: '#198754',
    cancelButtonText: 'Hủy',
  });

  if (result.isConfirmed) {
    try {
      await apiClient.post(`/orders/${order.id}/payment-confirm`);
      Swal.fire('Thành công! 🎉', 'Đơn hàng đã chuyển sang "Đang gia công"', 'success');
      loadOrders(currentPage.value);
    } catch (error) {
      console.error('Failed to start processing:', error);
      Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật trạng thái', 'error');
    }
  }
};

const finishing = ref(false);
const shipping = ref(false);

const finishProcessing = async (order) => {
  const result = await Swal.fire({
    title: 'Hoàn thành gia công?',
    html: `
      <p>Đơn hàng <strong>${order.orderNumber}</strong> sẽ chuyển sang <strong>Chờ thanh toán đợt 2</strong>.</p>
      <p class="text-muted small">Khách hàng sẽ thấy nút thanh toán phần còn lại.</p>
    `,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '✅ Xác nhận hoàn thành',
    confirmButtonColor: '#f59e0b',
    cancelButtonText: 'Hủy',
  });

  if (!result.isConfirmed) return;

  finishing.value = true;
  try {
    await apiClient.put(`/orders/${order.id}/finish-processing`);
    Swal.fire('Thành công! 🎉', `Đơn ${order.orderNumber} đã chuyển sang "Chờ thanh toán đợt 2"`, 'success');
    loadOrders(currentPage.value);
  } catch (error) {
    console.error('Failed to finish processing:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể cập nhật trạng thái', 'error');
  } finally {
    finishing.value = false;
  }
};

const markAsShipping = async (order) => {
  const result = await Swal.fire({
    title: 'Xác nhận bàn giao vận chuyển?',
    html: `
      <p>Đơn hàng <strong>${order.orderNumber}</strong> sẽ chuyển sang <strong>Đang giao hàng</strong>.</p>
      <p class="text-muted small">Trạng thái này dùng khi đã bàn giao cho đơn vị vận chuyển.</p>
    `,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '✅ Xác nhận',
    confirmButtonColor: '#0d6efd',
    cancelButtonText: 'Hủy',
  });

  if (!result.isConfirmed) return;

  shipping.value = true;
  try {
    await apiClient.put(`/orders/${order.id}/ship`);
    await Swal.fire('Thành công', `Đơn ${order.orderNumber} đã chuyển sang "Đang giao hàng"`, 'success');
    await loadOrders(currentPage.value);
    bsModal?.hide();
  } catch (error) {
    console.error('Failed to mark order as shipping:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể chuyển trạng thái sang Đang giao hàng', 'error');
  } finally {
    shipping.value = false;
  }
};

// ===== Create Shipment (item-based) =====

const createShipment = async () => {
  if (selectedItemIds.value.length === 0) return;

  try {
    const res = await apiClient.post('/shipments/preview', { itemIds: selectedItemIds.value });
    sessionStorage.setItem('shipmentPreview', JSON.stringify(res.data));
    sessionStorage.setItem('shipmentItemIds', JSON.stringify(selectedItemIds.value));
    router.push('/admin/invoice-preview');
  } catch (error) {
    console.error('Failed to create shipment preview:', error);
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể tạo phiếu xuất kho', 'error');
  }
};

// --- Helpers ---

const calculateOrderTotal = (order) => {
  if (!order?.items) return 0;
  return order.items
    .filter(item => item.reviewStatus === 'APPROVED' && item.totalItemPrice)
    .reduce((sum, item) => sum + Number(item.totalItemPrice), 0);
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

const getReviewStatusText = (status) => {
  return getReviewStatusLabel(status);
};

const getReviewBadgeClass = (status) => {
  const map = { PENDING_REVIEW: 'badge bg-secondary', APPROVED: 'badge bg-success', REJECTED: 'badge bg-danger', NEED_DISCUSSION: 'badge bg-warning text-dark' };
  return map[status] || 'badge bg-secondary';
};

const getItemRowClass = (item) => {
  const map = { APPROVED: '', REJECTED: 'table-danger', NEED_DISCUSSION: 'table-warning' };
  return map[item.reviewStatus] || '';
};

const getStatusText = (status) => {
  return getOrderStatusLabel(status, { DEPOSITED: 'Đã cọc 💳' });
};

const getStatusBadgeClass = (status) => {
  const map = {
    PENDING_APPROVAL: 'badge bg-warning text-dark',
    PENDING_QUOTE: 'badge bg-warning text-dark',
    AWAITING_PAYMENT: 'badge bg-info text-dark',
    DEPOSITED: 'badge bg-success',
    PROCESSING: 'badge bg-primary',
    AWAITING_REMAINING_PAYMENT: 'badge bg-warning text-dark',
    AWAITING_DELIVERY: 'badge bg-info text-dark',
    SHIPPING: 'badge bg-primary',
    COMPLETED: 'badge bg-success',
    CANCELLED: 'badge bg-danger',
  };
  return map[status] || 'badge bg-secondary';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const formatDateShort = (dateStr) => {
  if (!dateStr) return '';
  if (dateStr.includes('-')) {
    const [year, month, day] = dateStr.split('-');
    return `${day}/${month}/${year}`;
  }
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const formatCurrency = (amount) => {
  if (!amount) return '';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

const formatNumber = (amount) => {
  if (!amount) return '';
  return new Intl.NumberFormat('vi-VN').format(amount);
};

const formatWeight = (weight) => {
  if (weight === null || weight === undefined || weight === '') return '';
  return `${new Intl.NumberFormat('vi-VN').format(weight)} kg`;
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.table-active-row {
  background-color: #f8f9fa !important;
  border-left: 4px solid #0d6efd !important;
}
.custom-hover-table tbody tr:hover {
  background-color: #f1f8ff;
}
.shadow-inner {
  background-color: #fbfcfd;
}
.form-check-input {
  cursor: pointer;
}
.table th {
  font-weight: 600;
  font-size: 0.85rem;
}
.table td {
  font-size: 0.9rem;
}
.table-danger {
  background-color: #f8d7da !important;
}
.table-warning {
  background-color: #fff3cd !important;
}
.modal-xl {
  max-width: 1200px;
}

.review-modal-wide {
  max-width: 96vw;
}

.review-table th,
.review-table td {
  vertical-align: middle;
}

.review-table thead th {
  white-space: nowrap;
  font-size: 0.78rem;
}

.review-table tbody td {
  font-size: 0.84rem;
}

.review-table .form-control-sm {
  min-height: 30px;
}

.review-table textarea.form-control-sm {
  min-width: 170px;
  resize: vertical;
}

.review-table .btn-group .btn {
  min-width: 34px;
}

.review-table .badge {
  white-space: nowrap;
}

.review-table-comfortable td {
  padding: 0.55rem 0.35rem;
}

.review-table-compact td {
  padding: 0.32rem 0.28rem;
}

.review-table-compact tbody td {
  font-size: 0.78rem;
}

.review-table .sticky-col-left-stt,
.review-table .sticky-col-left-drawing,
.review-table .sticky-col-right-actions {
  position: sticky;
  z-index: 3;
  background: #fff;
}

.review-table .sticky-col-left-stt {
  left: 0;
  min-width: 52px;
}

.review-table .sticky-col-left-drawing {
  left: 267px;
}

.review-table .sticky-col-right-actions {
  right: 0;
}

.review-table thead .sticky-col-left-stt,
.review-table thead .sticky-col-left-drawing,
.review-table thead .sticky-col-right-actions {
  z-index: 5;
  background: #f8f9fa;
}

.review-table tbody .sticky-col-left-drawing {
  box-shadow: 1px 0 0 #dee2e6;
}

.review-table tbody .sticky-col-right-actions {
  box-shadow: -1px 0 0 #dee2e6;
}
.item-selected {
  background: rgba(13, 110, 253, 0.06) !important;
}

/* Date group bar */
.date-group-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  padding: 10px 14px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
}
.date-group-label {
  font-size: 0.78rem;
  font-weight: 600;
  color: #6c757d;
  margin-right: 4px;
}
.date-group-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 0.78rem;
  font-weight: 500;
  border: 1px solid #dee2e6;
  background: #fff;
  color: #6c757d;
  cursor: pointer;
  transition: all 0.15s;
}
.date-group-tag:hover {
  border-color: #86b7fe;
  color: #0d6efd;
  background: #e8f4fd;
}
.date-group-active {
  border-color: #0d6efd !important;
  color: #0d6efd !important;
  background: #e8f4fd !important;
  font-weight: 600;
}
.date-group-count {
  color: #adb5bd;
}
.date-group-all {
  color: #198754;
  border-color: #198754;
}
.date-group-all:hover {
  background: #d1e7dd;
}
.date-group-clear {
  color: #dc3545;
  border-color: #dc3545;
}
.date-group-clear:hover {
  background: #f8d7da;
}
</style>