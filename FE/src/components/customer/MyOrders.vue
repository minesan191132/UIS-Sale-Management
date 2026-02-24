<template>
  <div class="container py-5 mt-5">
    <h2 class="mb-4">
      <i class="bi bi-list-check me-2"></i>Đơn Hàng Của Tôi
    </h2>

    <!-- Loading -->
    <div v-if="isLoading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
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
    <div v-else>
      <div class="row g-3">
        <div v-for="order in orders" :key="order.id" class="col-12">
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
                    <button @click="viewOrderDetail(order)" class="btn btn-outline-primary btn-sm">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'

const orders = ref([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)

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

const viewOrderDetail = (order) => {
  let currentPage = 0
  const itemsPerPage = 5
  const totalPages = Math.ceil((order.items?.length || 0) / itemsPerPage)

  const showPage = (page) => {
    currentPage = page
    const startIdx = page * itemsPerPage
    const endIdx = startIdx + itemsPerPage
    const pageItems = order.items.slice(startIdx, endIdx)

    // Build paginated items HTML with all 8 columns
    const itemsHtml = pageItems.map((item, idx) => `
      <tr>
        <td class="text-center" style="width: 50px;">${startIdx + idx + 1}</td>
        <td style="width: 100px;"><small>${item.itemCode || '-'}</small></td>
        <td style="width: 120px;"><small>${item.drawingNumber || '-'}</small></td>
        <td>${item.itemName}</td>
        <td style="width: 150px;"><small>${item.specification || '-'}</small></td>
        <td style="width: 100px;"><small>${item.materialType || item.material || '-'}</small></td>
        <td class="text-center" style="width: 80px;">${item.quantity}</td>
        <td style="width: 100px;"><small>${extractDeliveryDate(item.notes)}</small></td>
      </tr>
    `).join('')

    // Pagination controls
    const paginationHtml = totalPages > 1 ? `
      <nav class="mt-3">
        <ul class="pagination pagination-sm justify-content-center mb-0">
          <li class="page-item ${page === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${page - 1}">
              <i class="bi bi-chevron-left"></i>
            </a>
          </li>
          ${Array.from({ length: totalPages }, (_, i) => `
            <li class="page-item ${i === page ? 'active' : ''}">
              <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>
          `).join('')}
          <li class="page-item ${page >= totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${page + 1}">
              <i class="bi bi-chevron-right"></i>
            </a>
          </li>
        </ul>
      </nav>
    ` : ''

    Swal.fire({
      title: `Chi tiết đơn hàng ${order.orderNumber}`,
      html: `
        <div class="text-start" style="max-height: 70vh; overflow-y: auto;">
          <!-- Header Info -->
          <div class="mb-3 pb-3 border-bottom">
            <div class="row g-2" style="font-size: 0.9rem;">
              <div class="col-md-6">
                <p class="mb-1"><strong>Trạng thái:</strong> ${getStatusText(order.status)}</p>
                <p class="mb-1"><strong>Ngày tạo:</strong> ${formatDate(order.createdAt)}</p>
              </div>
              <div class="col-md-6">
                ${order.totalPrice ? `<p class="mb-1"><strong>Tổng giá trị:</strong> ${formatCurrency(order.totalPrice)}</p>` : ''}
                ${order.depositAmount ? `<p class="mb-1"><strong>Cọc trước:</strong> ${formatCurrency(order.depositAmount)}</p>` : ''}
              </div>
            </div>
            <!-- VNN, NO - không lặp lại mỗi row -->
            <div class="row g-2 mt-2" style="font-size: 0.85rem;">
              <div class="col-md-6">
                <p class="mb-0"><strong>VNN:</strong> ${order.customer?.companyName || order.companyName || '-'}</p>
              </div>
              <div class="col-md-6">
                <p class="mb-0"><strong>NO:</strong> ${order.orderNumber}</p>
              </div>
            </div>
          </div>

          <!-- Items Table -->
          <h6 class="mb-2">Danh sách sản phẩm (${order.items?.length || 0} items, trang ${page + 1}/${totalPages})</h6>
          <div class="table-responsive">
            <table class="table table-sm table-bordered" style="font-size: 0.85rem;">
              <thead class="table-light">
                <tr>
                  <th>STT</th>
                  <th>Item Code</th>
                  <th>Drawing Number<br/><small class="text-muted">図面</small></th>
                  <th>Parts Name (JAPAN)<br/><small>品名 (日本)</small></th>
                  <th>Spec.型式</th>
                  <th>Material材質</th>
                  <th>QTY</th>
                  <th>納期時間<br/><small>Delivery</small></th>
                </tr>
              </thead>
              <tbody>
                ${itemsHtml}
              </tbody>
            </table>
          </div>

          ${paginationHtml}
        </div>
      `,
      width: '900px',
      confirmButtonText: 'Đóng',
      didOpen: () => {
        // Add pagination click handlers
        document.querySelectorAll('.page-link').forEach(link => {
          link.addEventListener('click', (e) => {
            e.preventDefault()
            const targetPage = parseInt(e.currentTarget.dataset.page)
            if (targetPage >= 0 && targetPage < totalPages) {
              showPage(targetPage)
            }
          })
        })
      }
    })
  }

  showPage(0) // Start with first page
}

const extractDeliveryDate = (notes) => {
  if (!notes) return '-'
  const match = notes.match(/Ngày giao:\s*(.+)/)
  return match ? match[1] : notes
}

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
      // User claims they paid, we can show a message or notify admin
      Swal.fire('Cảm ơn!', 'Chúng tôi sẽ xác nhận thanh toán của bạn trong thời gian sớm nhất.', 'success')
      loadOrders() // Reload to check if status changed
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

const formatCurrency = (amount) => {
  if (!amount) return '-'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}
</script>

<style scoped>
.hover-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.hover-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}
</style>
