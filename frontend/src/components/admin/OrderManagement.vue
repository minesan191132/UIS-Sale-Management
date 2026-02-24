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
                <th>Trạng thái</th>
                <th>Giá trị</th>
                <th>Ngày tạo</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id">
                <td><strong>{{ order.orderNumber }}</strong></td>
                <td>{{ order.customerName }}</td>
                <td>{{ order.companyName }}</td>
                <td>{{ order.items?.length || 0 }} SP</td>
                <td><span :class="getStatusBadgeClass(order.status)">{{ getStatusText(order.status) }}</span></td>
                <td>{{ formatCurrency(order.totalPrice) }}</td>
                <td>{{ formatDate(order.createdAt) }}</td>
                <td>
                  <div class="btn-group btn-group-sm">
                    <button @click="viewDetail(order)" class="btn btn-outline-primary" title="Xem chi tiết">
                      <i class="bi bi-eye"></i>
                    </button>
                    <button 
                      v-if="order.status === 'PENDING_QUOTE'" 
                      @click="openQuoteModal(order)" 
                      class="btn btn-outline-success" 
                      title="Báo giá">
                      <i class="bi bi-currency-dollar"></i>
                    </button>
                    <button 
                      v-if="order.status === 'AWAITING_PAYMENT'" 
                      @click="confirmPayment(order)" 
                      class="btn btn-outline-warning" 
                      title="Xác nhận thanh toán">
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
    const response = await apiClient.get('/orders', {
      params: { page, size: 15 }
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

const viewDetail = (order) => {
  const itemsHtml = order.items.map(item => `
    <tr>
      <td class="text-start">${item.itemName}</td>
      <td>${item.specification || '-'}</td>
      <td>${item.quantity} ${item.unit || ''}</td>
      <td>${item.notes || '-'}</td>
    </tr>
  `).join('')

  Swal.fire({
    title: `Chi tiết đơn hàng ${order.orderNumber}`,
    html: `
      <div class="text-start">
        <p><strong>Khách hàng:</strong> ${order.customerName}</p>
        <p><strong>Công ty:</strong> ${order.companyName}</p>
        <p><strong>Trạng thái:</strong> ${getStatusText(order.status)}</p>
        <p><strong>Ngày tạo:</strong> ${formatDate(order.createdAt)}</p>
        ${order.totalPrice ? `<p><strong>Tổng giá trị:</strong> ${formatCurrency(order.totalPrice)}</p>` : ''}
        ${order.notes ? `<p><strong>Ghi chú:</strong> ${order.notes}</p>` : ''}
        <hr>
        <h6>Danh sách sản phẩm:</h6>
        <table class="table table-sm">
          <thead>
            <tr>
              <th class="text-start">Tên vật tư</th>
              <th>Quy cách</th>
              <th>Số lượng</th>
              <th>Ghi chú</th>
            </tr>
          </thead>
          <tbody>
            ${itemsHtml}
          </tbody>
        </table>
      </div>
    `,
    width: '800px',
    confirmButtonText: 'Đóng'
  })
}

const openQuoteModal = async (order) => {
  const { value: formValues } = await Swal.fire({
    title: `Báo giá đơn hàng ${order.orderNumber}`,
    html: `
      <input id="swal-price" class="swal2-input" type="number" placeholder="Tổng giá trị (VNĐ)" step="1000">
      <textarea id="swal-notes" class="swal2-textarea" placeholder="Ghi chú (tùy chọn)"></textarea>
    `,
    focusConfirm: false,
    showCancelButton: true,
    confirmButtonText: 'Gửi báo giá',
    cancelButtonText: 'Hủy',
    preConfirm: () => {
      const price = document.getElementById('swal-price').value
      const notes = document.getElementById('swal-notes').value
      if (!price || parseFloat(price) <= 0) {
        Swal.showValidationMessage('Vui lòng nhập giá trị hợp lệ')
      }
      return { totalPrice: parseFloat(price), notes }
    }
  })

  if (formValues) {
    await submitQuote(order.id, formValues)
  }
}

const submitQuote = async (orderId, quoteData) => {
  try {
    await apiClient.put(`/orders/${orderId}/quote`, quoteData)
    Swal.fire('Thành công', 'Báo giá đã được gửi đến khách hàng', 'success')
    loadOrders()
  } catch (error) {
    console.error('Failed to submit quote:', error)
    Swal.fire('Lỗi', error.response?.data?.error || 'Không thể gửi báo giá', 'error')
  }
}

const confirmPayment = async (order) => {
  const result = await Swal.fire({
    title: 'Xác nhận thanh toán?',
    text: `Khách hàng đã thanh toán ${formatCurrency(order.depositAmount)} cho đơn hàng ${order.orderNumber}?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Xác nhận',
    cancelButtonText: 'Hủy'
  })

  if (result.isConfirmed) {
    try {
      await apiClient.post(`/orders/${order.id}/payment-confirm`)
      Swal.fire('Thành công', 'Đơn hàng đã chuyển sang trạng thái "Đang gia công"', 'success')
      loadOrders()
    } catch (error) {
      console.error('Failed to confirm payment:', error)
      Swal.fire('Lỗi', 'Không thể xác nhận thanh toán', 'error')
    }
  }
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
  return date.toLocaleDateString('vi-VN')
}

const formatCurrency = (amount) => {
  if (!amount) return 'Chưa báo giá'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}
</script>

<style scoped>
.table th {
  font-weight: 600;
}
</style>
