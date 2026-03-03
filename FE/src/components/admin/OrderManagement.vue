<template>
  <div class="container-fluid">
    <h3 class="mb-4">
      <i class="bi bi-clipboard-check me-2"></i>Quản Lý Đơn Hàng
    </h3>

    <!-- Loading -->
    <div v-if="isLoading" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
    </div>

    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="card">
      <div class="card-body text-center py-5">
        <i class="bi bi-inbox fs-1 text-muted"></i>
        <p class="text-muted mt-3">Chưa có đơn hàng nào</p>
      </div>
    </div>

    <!-- Orders Table -->
    <div v-else class="card shadow-sm">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Công ty</th>
                <th>Sản phẩm</th>
                <th>Review</th>
                <th>Trạng thái</th>
                <th>Giá trị</th>
                <th>Ngày tạo</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id">
                <td>
                  <strong>{{ order.orderNumber }}</strong>
                </td>
                <td>{{ order.userName }}</td>
                <td>{{ order.companyName }}</td>
                <td>{{ order.items?.length || 0 }} SP</td>
                <td>
                  <span v-if="order.status === 'PENDING_QUOTE'" class="small">
                    {{ getReviewProgress(order) }}
                  </span>
                  <span v-else class="text-muted small">—</span>
                </td>
                <td>
                  <span :class="getStatusBadgeClass(order.status)">{{
                    getStatusText(order.status)
                  }}</span>
                </td>
                <td>{{ formatCurrency(order.totalPrice) }}</td>
                <td>{{ formatDate(order.createdAt) }}</td>
                <td>
                  <div class="btn-group btn-group-sm">
                    <button
                      @click="openReviewModal(order)"
                      class="btn btn-outline-primary"
                      title="Xem & Review"
                    >
                      <i class="bi bi-eye"></i>
                    </button>
                    <button
                      v-if="
                        order.status === 'PENDING_QUOTE' && isAllReviewed(order)
                      "
                      @click="submitQuote(order)"
                      class="btn btn-outline-success"
                      title="Gửi báo giá"
                    >
                      <i class="bi bi-currency-dollar"></i>
                    </button>
                    <button
                      v-if="order.status === 'AWAITING_PAYMENT'"
                      @click="confirmPayment(order)"
                      class="btn btn-outline-warning"
                      title="Xác nhận thanh toán"
                    >
                      <i class="bi bi-check2"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <nav v-if="totalPages > 1" class="mt-3">
          <ul class="pagination justify-content-center mb-0">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <button class="page-link" @click="loadOrders(currentPage - 1)">
                Trước
              </button>
            </li>
            <li
              v-for="page in totalPages"
              :key="page"
              class="page-item"
              :class="{ active: page - 1 === currentPage }"
            >
              <button class="page-link" @click="loadOrders(page - 1)">
                {{ page }}
              </button>
            </li>
            <li
              class="page-item"
              :class="{ disabled: currentPage >= totalPages - 1 }"
            >
              <button class="page-link" @click="loadOrders(currentPage + 1)">
                Sau
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- Review Modal -->
    <div class="modal fade" id="reviewModal" tabindex="-1" ref="reviewModalRef">
      <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content" v-if="selectedOrder">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-clipboard-check me-2"></i>
              {{ selectedOrder.orderNumber }} — Review Đơn Hàng
            </h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
            ></button>
          </div>
          <div class="modal-body">
            <!-- Order Info -->
            <div class="row mb-4">
              <div class="col-md-4">
                <p class="mb-1">
                  <strong>Khách hàng:</strong> {{ selectedOrder.userName }}
                </p>
                <p class="mb-1">
                  <strong>Công ty:</strong> {{ selectedOrder.companyName }}
                </p>
              </div>
              <div class="col-md-4">
                <p class="mb-1">
                  <strong>Trạng thái:</strong>
                  <span :class="getStatusBadgeClass(selectedOrder.status)">{{
                    getStatusText(selectedOrder.status)
                  }}</span>
                </p>
                <p class="mb-1">
                  <strong>Ngày tạo:</strong>
                  {{ formatDate(selectedOrder.createdAt) }}
                </p>
              </div>
              <div class="col-md-4">
                <p class="mb-1">
                  <strong>Tiến trình review:</strong>
                  {{ getReviewProgress(selectedOrder) }}
                </p>
                <p class="mb-1" v-if="selectedOrder.totalPrice">
                  <strong>Tổng giá trị:</strong>
                  {{ formatCurrency(selectedOrder.totalPrice) }}
                </p>
              </div>
            </div>

            <!-- Items Table -->
            <div class="table-responsive" style="max-height: 500px; overflow-y: auto;">
              <table class="table table-bordered table-sm align-middle mb-0">
                <thead class="table-light" style="position: sticky; top: 0; z-index: 1;">
                  <tr>
                    <th class="text-center" style="width: 45px">STT</th>
                    <th style="width: 80px">VNN_NO</th>
                    <th style="width: 100px">Item Code<br><small class="text-muted fw-normal">品目コード</small></th>
                    <th style="width: 110px">Drawing No.<br><small class="text-muted fw-normal">図番</small></th>
                    <th style="min-width: 160px">Parts Name<br><small class="text-muted fw-normal">品名</small></th>
                    <th style="min-width: 120px">Spec<br><small class="text-muted fw-normal">型式</small></th>
                    <th style="width: 100px">Material<br><small class="text-muted fw-normal">材質</small></th>
                    <th class="text-center" style="width: 55px">QTY</th>
                    <th class="text-center" style="width: 90px">Review</th>
                    <th class="text-center" style="width: 120px">Đơn giá</th>
                    <th class="text-center" style="width: 110px">Thành tiền</th>
                    <th class="text-center" style="width: 130px">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(item, index) in selectedOrder.items"
                    :key="item.id"
                    :class="getItemRowClass(item)"
                  >
                    <td class="text-center">{{ index + 1 }}</td>
                    <td style="max-width: 130px; word-break: break-all; font-size: 0.8rem">{{ item.unit || '—' }}</td>
                    <td style="max-width: 100px; word-break: break-all; font-size: 0.8rem">{{ item.itemCode || '—' }}</td>
                    <td style="max-width: 110px; word-break: break-all; font-size: 0.8rem">{{ item.drawingNumber || '—' }}</td>
                    <td style="word-break: break-all">{{ item.itemName || '—' }}</td>
                    <td style="word-break: break-all">{{ item.specification || '—' }}</td>
                    <td style="word-break: break-all">{{ item.material || '—' }}</td>
                    <td class="text-center fw-bold">{{ item.quantity }}</td>
                    <td class="text-center">
                      <span :class="getReviewBadgeClass(item.reviewStatus)" style="font-size: 0.7rem">
                        {{ getReviewStatusText(item.reviewStatus) }}
                      </span>
                    </td>
                    <td class="text-end">
                      <span v-if="item.unitPrice">{{ formatNumber(item.unitPrice) }}</span>
                      <span v-else class="text-muted">—</span>
                    </td>
                    <td class="text-end">
                      <span v-if="item.totalItemPrice" class="fw-bold">{{ formatNumber(item.totalItemPrice) }}</span>
                      <span v-else class="text-muted">—</span>
                    </td>
                    <td class="text-center">
                      <div
                        v-if="selectedOrder.status === 'PENDING_QUOTE'"
                        class="btn-group btn-group-sm"
                      >
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
                      <span v-else class="text-muted small">—</span>
                    </td>
                  </tr>
                </tbody>
                <tfoot v-if="calculatedTotal > 0">
                  <tr class="table-light fw-bold">
                    <td colspan="10" class="text-end">
                      Tổng (sản phẩm đã duyệt):
                    </td>
                    <td class="text-end">{{ formatNumber(calculatedTotal) }}</td>
                    <td></td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <!-- Admin notes per item -->
            <div v-for="item in selectedOrder.items" :key="'note-' + item.id">
              <div
                v-if="item.adminNote"
                class="alert alert-sm py-1 px-2 mb-1"
                :class="
                  item.reviewStatus === 'REJECTED'
                    ? 'alert-danger'
                    : 'alert-warning'
                "
              >
                <small>
                  <strong>{{ item.itemName }}:</strong> {{ item.adminNote }}
                </small>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-secondary"
              data-bs-dismiss="modal"
            >
              Đóng
            </button>
            <button
              v-if="
                selectedOrder.status === 'PENDING_QUOTE' &&
                isAllReviewed(selectedOrder)
              "
              @click="submitQuoteFromModal()"
              class="btn btn-success"
            >
              <i class="bi bi-send me-1"></i>Gửi Báo Giá ({{
                formatCurrency(calculatedTotal)
              }})
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import Swal from "sweetalert2";
import apiClient from "../../services/api";
import { Modal } from "bootstrap";

const orders = ref([]);
const isLoading = ref(false);
const currentPage = ref(0);
const totalPages = ref(0);
const selectedOrder = ref(null);
const reviewModalRef = ref(null);
let bsModal = null;

onMounted(() => {
  loadOrders();
});

const loadOrders = async (page = 0) => {
  isLoading.value = true;
  try {
    const response = await apiClient.get("/orders", {
      params: { page, size: 15 },
    });
    orders.value = response.data.content || response.data;
    currentPage.value = response.data.number || 0;
    totalPages.value = response.data.totalPages || 1;
  } catch (error) {
    console.error("Failed to load orders:", error);
    Swal.fire("Lỗi", "Không thể tải danh sách đơn hàng", "error");
  } finally {
    isLoading.value = false;
  }
};

const calculatedTotal = computed(() => {
  if (!selectedOrder.value?.items) return 0;
  return selectedOrder.value.items
    .filter((item) => item.reviewStatus === "APPROVED" && item.totalItemPrice)
    .reduce((sum, item) => sum + Number(item.totalItemPrice), 0);
});

const openReviewModal = async (order) => {
  try {
    const response = await apiClient.get(`/orders/${order.id}`);
    selectedOrder.value = response.data;
    await nextTick();

    if (!bsModal && reviewModalRef.value) {
      bsModal = new Modal(reviewModalRef.value);
    }
    bsModal?.show();
  } catch (error) {
    console.error("Failed to load order detail:", error);
    Swal.fire("Lỗi", "Không thể tải chi tiết đơn hàng", "error");
  }
};

const reviewItem = async (item, status) => {
  if (status === "APPROVED") {
    const { value: unitPrice } = await Swal.fire({
      title: `Duyệt: ${item.itemName}`,
      html: `
        <p class="text-start mb-2">Số lượng: <strong>${item.quantity} ${item.unit || ""}</strong></p>
        <p class="text-start mb-2">Vật liệu: <strong>${item.material || "—"}</strong></p>
        <input id="swal-unit-price" class="swal2-input" type="number" placeholder="Đơn giá (VNĐ)" step="1000" min="1">
        <textarea id="swal-note" class="swal2-textarea" placeholder="Ghi chú (tùy chọn)"></textarea>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: "Duyệt",
      confirmButtonColor: "#198754",
      cancelButtonText: "Hủy",
      preConfirm: () => {
        const price = document.getElementById("swal-unit-price").value;
        if (!price || parseFloat(price) <= 0) {
          Swal.showValidationMessage("Vui lòng nhập đơn giá hợp lệ");
          return false;
        }
        return {
          unitPrice: parseFloat(price),
          adminNote: document.getElementById("swal-note").value || null,
        };
      },
    });
    if (unitPrice) {
      await sendReview(
        item.id,
        "APPROVED",
        unitPrice.unitPrice,
        unitPrice.adminNote,
      );
    }
  } else {
    const label = status === "REJECTED" ? "Từ chối" : "Cần trao đổi";
    const { value: note } = await Swal.fire({
      title: `${label}: ${item.itemName}`,
      input: "textarea",
      inputLabel:
        status === "REJECTED" ? "Lý do từ chối" : "Nội dung cần trao đổi",
      inputPlaceholder:
        status === "REJECTED"
          ? "VD: Không nhận gia công nhôm, chỉ nhận sắt/inox"
          : "VD: Cần xác nhận lại quy cách...",
      inputValidator: (value) => {
        if (!value?.trim()) return "Vui lòng nhập lý do";
      },
      showCancelButton: true,
      confirmButtonText: label,
      confirmButtonColor: status === "REJECTED" ? "#dc3545" : "#ffc107",
      cancelButtonText: "Hủy",
    });
    if (note) {
      await sendReview(item.id, status, null, note);
    }
  }
};

const sendReview = async (itemId, reviewStatus, unitPrice, adminNote) => {
  try {
    const response = await apiClient.put(
      `/orders/${selectedOrder.value.id}/items/${itemId}/review`,
      { reviewStatus, unitPrice, adminNote },
    );
    selectedOrder.value = response.data;

    // Also update the order in the list
    const idx = orders.value.findIndex((o) => o.id === selectedOrder.value.id);
    if (idx !== -1) {
      orders.value[idx] = response.data;
    }
  } catch (error) {
    console.error("Failed to review item:", error);
    Swal.fire(
      "Lỗi",
      error.response?.data?.error || "Không thể review sản phẩm",
      "error",
    );
  }
};

const submitQuoteFromModal = async () => {
  await submitQuote(selectedOrder.value);
};

const submitQuote = async (order) => {
  const { value: notes } = await Swal.fire({
    title: `Gửi báo giá — ${order.orderNumber}`,
    html: `
      <p>Tổng giá trị: <strong>${formatCurrency(calculatedTotal.value || calculateOrderTotal(order))}</strong></p>
      <p class="text-muted small">Tiền cọc (70%): ${formatCurrency((calculatedTotal.value || calculateOrderTotal(order)) * 0.7)}</p>
    `,
    input: "textarea",
    inputLabel: "Ghi chú cho khách hàng (tùy chọn)",
    inputPlaceholder: "VD: Thời gian giao hàng dự kiến 2 tuần...",
    showCancelButton: true,
    confirmButtonText: "Gửi báo giá",
    confirmButtonColor: "#198754",
    cancelButtonText: "Hủy",
  });

  if (notes !== undefined) {
    try {
      await apiClient.put(`/orders/${order.id}/quote`, {
        notes: notes || null,
      });
      Swal.fire("Thành công", "Báo giá đã được gửi đến khách hàng", "success");
      bsModal?.hide();
      loadOrders(currentPage.value);
    } catch (error) {
      console.error("Failed to submit quote:", error);
      Swal.fire(
        "Lỗi",
        error.response?.data?.error || "Không thể gửi báo giá",
        "error",
      );
    }
  }
};

const confirmPayment = async (order) => {
  const result = await Swal.fire({
    title: "Xác nhận thanh toán?",
    text: `Khách hàng đã thanh toán ${formatCurrency(order.depositAmount)} cho đơn hàng ${order.orderNumber}?`,
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Xác nhận",
    cancelButtonText: "Hủy",
  });

  if (result.isConfirmed) {
    try {
      await apiClient.post(`/orders/${order.id}/payment-confirm`);
      Swal.fire(
        "Thành công",
        'Đơn hàng đã chuyển sang trạng thái "Đang gia công"',
        "success",
      );
      loadOrders();
    } catch (error) {
      console.error("Failed to confirm payment:", error);
      Swal.fire("Lỗi", "Không thể xác nhận thanh toán", "error");
    }
  }
};

// --- Helpers ---

const calculateOrderTotal = (order) => {
  if (!order?.items) return 0;
  return order.items
    .filter((item) => item.reviewStatus === "APPROVED" && item.totalItemPrice)
    .reduce((sum, item) => sum + Number(item.totalItemPrice), 0);
};

const getReviewProgress = (order) => {
  if (!order?.items?.length) return "0/0";
  const reviewed = order.items.filter(
    (i) => i.reviewStatus && i.reviewStatus !== "PENDING_REVIEW",
  ).length;
  return `${reviewed}/${order.items.length}`;
};

const isAllReviewed = (order) => {
  if (!order?.items?.length) return false;
  return order.items.every(
    (i) => i.reviewStatus && i.reviewStatus !== "PENDING_REVIEW",
  );
};

const getReviewStatusText = (status) => {
  const map = {
    PENDING_REVIEW: "Chờ review",
    APPROVED: "Đã duyệt",
    REJECTED: "Từ chối",
    NEED_DISCUSSION: "Cần trao đổi",
  };
  return map[status] || status;
};

const getReviewBadgeClass = (status) => {
  const map = {
    PENDING_REVIEW: "badge bg-secondary",
    APPROVED: "badge bg-success",
    REJECTED: "badge bg-danger",
    NEED_DISCUSSION: "badge bg-warning text-dark",
  };
  return map[status] || "badge bg-secondary";
};

const getItemRowClass = (item) => {
  const map = {
    APPROVED: "",
    REJECTED: "table-danger",
    NEED_DISCUSSION: "table-warning",
  };
  return map[item.reviewStatus] || "";
};

const getStatusText = (status) => {
  const statusMap = {
    PENDING_QUOTE: "Chờ báo giá",
    AWAITING_PAYMENT: "Chờ thanh toán",
    PROCESSING: "Đang gia công",
    COMPLETED: "Hoàn thành",
    CANCELLED: "Đã hủy",
  };
  return statusMap[status] || status;
};

const getStatusBadgeClass = (status) => {
  const classMap = {
    PENDING_QUOTE: "badge bg-warning text-dark",
    AWAITING_PAYMENT: "badge bg-info",
    PROCESSING: "badge bg-primary",
    COMPLETED: "badge bg-success",
    CANCELLED: "badge bg-danger",
  };
  return classMap[status] || "badge bg-secondary";
};

const formatDate = (dateStr) => {
  if (!dateStr) return "—";
  const date = new Date(dateStr);
  return date.toLocaleDateString("vi-VN");
};

const formatCurrency = (amount) => {
  if (!amount) return "Chưa báo giá";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

const formatNumber = (amount) => {
  if (!amount) return "—";
  return new Intl.NumberFormat("vi-VN").format(amount);
};
</script>

<style scoped>
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
</style>
