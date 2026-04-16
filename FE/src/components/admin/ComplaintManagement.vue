<template>
  <div class="complaints-mgmt p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
      <h4 class="mb-0 text-navy fw-bold"><i class="bi bi-exclamation-triangle-fill me-2 text-warning"></i>Quản lý Khiếu Nại</h4>
      <div>
        <select v-model="filterStatus" class="form-select form-select-sm" @change="loadComplaints(0)">
          <option value="">-- Tất cả trạng thái --</option>
          <option value="OPEN">Mới (Open)</option>
          <option value="IN_REVIEW">Đang xử lý</option>
          <option value="RESOLVED">Đã giải quyết</option>
          <option value="REJECTED">Từ chối</option>
        </select>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-4">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0 text-center" style="font-size: 0.9rem;">
          <thead class="table-light">
            <tr>
              <th class="py-3 px-4"># Mã</th>
              <th class="py-3 text-start">Đơn KH</th>
              <th class="py-3">Loại Khiếu Nại</th>
              <th class="py-3 text-start">Nội dung</th>
              <th class="py-3">Trạng thái</th>
              <th class="py-3">Ngày tạo</th>
              <th class="py-3 text-end px-4">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" class="text-center">
              <td colspan="7" class="py-5"><div class="spinner-border text-primary"></div></td>
            </tr>
            <tr v-else-if="complaints.length === 0" class="text-center">
              <td colspan="7" class="py-5 text-muted">Không có khiếu nại nào mảng này.</td>
            </tr>
            <tr v-for="c in complaints" :key="c.id" v-else>
              <td class="fw-bold text-navy px-4">KN-{{ c.id }}</td>
              <td class="text-start">
                <div class="fw-semibold">Đơn O-{{ c.orderId }}</div>
                <small class="text-muted" v-if="c.customerName">{{ c.customerName }}</small>
              </td>
              <td>
                <span class="badge" :class="getTypeBadge(c.type)">{{ getTypeLabel(c.type) }}</span>
              </td>
              <td class="text-start" style="max-width: 200px;">
                <div class="text-truncate" :title="c.description">{{ c.description }}</div>
              </td>
              <td>
                <span class="badge" :class="getStatusBadge(c.status)">{{ getStatusLabel(c.status) }}</span>
              </td>
              <td class="text-muted small">{{ formatDate(c.createdAt) }}</td>
              <td class="text-end px-4">
                <button class="btn btn-sm btn-outline-primary rounded-pill px-3" @click="openActionModal(c)">
                  Xử lý / Xem
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div class="d-flex justify-content-end" v-if="totalPages > 1">
      <ul class="pagination pagination-sm">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button class="page-link" @click="loadComplaints(currentPage - 1)">Sau</button>
        </li>
        <li class="page-item" v-for="p in totalPages" :key="p" :class="{ active: (p - 1) === currentPage }">
          <button class="page-link" @click="loadComplaints(p - 1)">{{ p }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
          <button class="page-link" @click="loadComplaints(currentPage + 1)">Trước</button>
        </li>
      </ul>
    </div>

    <!-- Action Modal -->
    <Teleport to="body">
      <div class="modal fade" id="adminComplaintModal" tabindex="-1" ref="modalRef">
        <div class="modal-dialog modal-lg modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg" v-if="selectedComplaint">
            <div class="modal-header bg-navy text-white">
              <h5 class="modal-title">Xử lý Khiếu Nại KN-{{ selectedComplaint.id }}</h5>
              <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body bg-light p-4">
              
              <div class="row g-4 mb-4">
                <div class="col-md-6">
                  <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body">
                      <h6 class="fw-bold text-navy border-bottom pb-2 mb-3">Thông tin chi tiết</h6>
                      <p class="mb-1 small"><strong>Mã đơn hàng:</strong> O-{{ selectedComplaint.orderId }}</p>
                      <p class="mb-1 small"><strong>Loại:</strong> <span class="badge" :class="getTypeBadge(selectedComplaint.type)">{{ getTypeLabel(selectedComplaint.type) }}</span></p>
                      <p class="mb-1 small"><strong>Trạng thái HT:</strong> <span class="badge" :class="getStatusBadge(selectedComplaint.status)">{{ getStatusLabel(selectedComplaint.status) }}</span></p>
                      <p class="mb-1 small mt-3"><strong>Nội dung KH báo:</strong></p>
                      <div class="bg-light p-2 rounded small text-secondary custom-scrollbar" style="max-height: 150px; overflow-y: auto; word-break: break-word; white-space: pre-wrap;">
                        {{ selectedComplaint.description }}
                      </div>
                    </div>
                  </div>
                </div>

                <div class="col-md-6">
                  <div class="card h-100 border-0 shadow-sm">
                    <div class="card-body">
                      <h6 class="fw-bold text-navy border-bottom pb-2 mb-3">Thông tin vật phẩm báo lỗi/thiếu</h6>
                      <ul class="list-group list-group-flush small custom-scrollbar" v-if="selectedComplaint.missingItems && selectedComplaint.missingItems.length" style="max-height: 280px; overflow-y: auto; padding-right: 5px;">
                        <li class="list-group-item px-0" v-for="mi in selectedComplaint.missingItems" :key="mi.id">
                          <div class="fw-bold text-dark mb-1">
                            {{ mi.itemName || 'Sản phẩm ID: ' + mi.orderItemId }}
                            <small class="text-muted font-monospace ms-1" v-if="mi.itemCode">[{{ mi.itemCode }}]</small>
                          </div>
                          <div v-if="mi.missingQuantity > 0" class="text-danger small fw-medium"><i class="bi bi-box-seam me-1"></i>Thiếu: {{ mi.missingQuantity }}</div>
                          <div v-if="mi.defectiveQuantity > 0" class="text-warning text-darken small fw-medium mt-1">
                            <i class="bi bi-exclamation-octagon me-1"></i>Lỗi/Hỏng: {{ mi.defectiveQuantity }} 
                            <span v-if="mi.reasonNote" class="fst-italic text-muted fw-normal ms-1">- {{ mi.reasonNote }}</span>
                          </div>
                        </li>
                      </ul>
                      <div v-else class="text-muted small">Không có dữ liệu SP</div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- HÌnh ảnh -->
              <div class="card border-0 shadow-sm mb-4" v-if="selectedComplaint.images && selectedComplaint.images.length">
                <div class="card-body">
                  <h6 class="fw-bold text-navy border-bottom pb-2 mb-3">Ảnh đính kèm</h6>
                  <div class="d-flex flex-wrap gap-2">
                    <a :href="getFullImageUrl(img.imageUrl)" target="_blank" v-for="img in selectedComplaint.images" :key="img.id" class="img-thumbnail" style="width: 100px; height: 100px; overflow: hidden; display:inline-block">
                      <img :src="getFullImageUrl(img.imageUrl)" class="img-fluid" style="object-fit: cover; width: 100%; height: 100%;" />
                    </a>
                  </div>
                </div>
              </div>

              <!-- Lịch sử (Admin view) -->
              <div class="card border-0 shadow-sm mb-4" v-if="selectedComplaint.history && selectedComplaint.history.length">
                <div class="card-header bg-white"><h6 class="fw-bold text-navy mb-0">Lịch sử quy trình</h6></div>
                <div class="card-body p-0">
                  <div class="list-group list-group-flush" style="max-height:180px; overflow-y:auto">
                    <div class="list-group-item small" v-for="h in selectedComplaint.history" :key="h.id">
                      <div class="d-flex justify-content-between">
                        <strong>{{h.actionType}} -> <span class="badge bg-secondary">{{h.newStatus}}</span></strong>
                        <span class="text-muted">{{ formatDate(h.createdAt) }}</span>
                      </div>
                      <div class="text-muted">Bởi: {{h.actionByUserName || 'Unknown'}} - Ghi chú: {{h.note || 'Không có'}}</div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="card border-info shadow-sm">
                <div class="card-header bg-info text-white fw-bold">Admin Cập Nhật Xử Lý</div>
                <div class="card-body">
                  <div class="mb-3">
                    <label class="form-label fw-semibold small">Trạng thái mới</label>
                    <select v-model="updateForm.status" class="form-select form-select-sm">
                      <option value="OPEN">OPEN (Chưa xử lý)</option>
                      <option value="IN_REVIEW">IN_REVIEW (Đang xử lý)</option>
                      <option value="RESOLVED">RESOLVED (Đã giải quyết)</option>
                      <option value="REJECTED">REJECTED (Từ chối KQ)</option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold small">Ghi chú của Admin (Khách sẽ thấy)</label>
                    <textarea v-model="updateForm.adminNote" class="form-control form-control-sm" rows="3" placeholder="Nhập hướng giải quyết hoặc lý do từ chối..."></textarea>
                  </div>
                </div>
              </div>

            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
              <button type="button" class="btn btn-primary" :disabled="saving" @click="saveComplaint">
                <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span> Lưu xử lý
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import apiClient from '../../services/api'
import Swal from 'sweetalert2'

const complaints = ref([])
const currentPage = ref(0)
const totalPages = ref(1)
const loading = ref(false)
const filterStatus = ref('')
const selectedComplaint = ref(null)
const modalRef = ref(null)
let bsModal = null

const updateForm = ref({
  status: 'OPEN',
  adminNote: ''
})
const saving = ref(false)

const loadComplaints = async (page = 0) => {
  loading.value = true
  try {
    const params = { page, size: 15 }
    if (filterStatus.value) params.status = filterStatus.value
    
    // Using apiClient directly for /api/admin/complaints
    const res = await apiClient.get('/admin/complaints', { params })
    complaints.value = res.data.content || []
    currentPage.value = res.data.number || 0
    totalPages.value = res.data.totalPages || 1
  } catch (error) {
    console.error(error)
    Swal.fire('Lỗi', 'Không thể tải danh sách khiếu nại', 'error')
  } finally {
    loading.value = false
  }
}

const openActionModal = (complaint) => {
  selectedComplaint.value = { ...complaint }
  updateForm.value.status = complaint.status
  updateForm.value.adminNote = complaint.adminNote || ''
  
  if (!bsModal && modalRef.value) {
    bsModal = new Modal(modalRef.value)
  }
  bsModal?.show()
}

const saveComplaint = async () => {
  if (!selectedComplaint.value) return
  saving.value = true
  
  try {
    const payload = {
      status: updateForm.value.status,
      adminNote: updateForm.value.adminNote
    }
    await apiClient.put(`/admin/complaints/${selectedComplaint.value.id}/status`, payload)
    
    Swal.fire({
      icon: 'success',
      title: 'Thành công',
      text: 'Đã cập nhật khiếu nại',
      timer: 1500,
      showConfirmButton: false
    })
    
    bsModal?.hide()
    loadComplaints(currentPage.value)
  } catch (error) {
    console.error(error)
    const msg = error.response?.data?.error || 'Lỗi khi lưu xử lý khiếu nại'
    Swal.fire('Lỗi', msg, 'error')
  } finally {
    saving.value = false
  }
}

// Formatters
const getFullImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  return baseUrl.replace('/api', '') + path;
}
const getTypeLabel = (type) => {
  const map = { MISSING_ITEM: 'Thiếu hàng', DEFECTIVE_ITEM: 'Hàng lỗi', OTHER: 'Khác' }
  return map[type] || 'Không rõ'
}
const getTypeBadge = (type) => {
  const map = { MISSING_ITEM: 'bg-warning text-dark', DEFECTIVE_ITEM: 'bg-danger', OTHER: 'bg-secondary' }
  return map[type] || 'bg-light text-dark'
}
const getStatusLabel = (s) => {
  const map = { OPEN: 'Mới', IN_REVIEW: 'Đang xử lý', RESOLVED: 'Giải quyết', REJECTED: 'Từ chối' }
  return map[s] || s
}
const getStatusBadge = (s) => {
  const map = { OPEN: 'bg-primary', IN_REVIEW: 'bg-info text-dark', RESOLVED: 'bg-success', REJECTED: 'bg-dark' }
  return map[s] || 'bg-secondary'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('vi-VN', {
    hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
  })
}

onMounted(() => {
  loadComplaints(0)
})
</script>

<style scoped>
.complaints-mgmt {
  background: white;
  min-height: calc(100vh - 60px);
  border-radius: 12px;
}
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
</style>
