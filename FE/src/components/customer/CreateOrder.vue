<template>
  <div class="create-order-page bg-white d-flex flex-column min-vh-100">
    <Navbar />
    <!-- Header Banner -->
    <div class="header-banner text-white text-center py-5 mb-5">
      <div class="container mt-5 mb-3">
        <h1 class="fw-bold mb-4" style="letter-spacing: 1px;">ĐẶT HÀNG GIA CÔNG</h1>
        <p class="mb-0" style="color: #8c9eb5; font-size: 1.1rem;">Giải pháp gia công cơ khí chính xác theo tiêu chuẩn Nhật Bản</p>
      </div>
    </div>
    
    <!-- Process Steps -->
    <div class="container mb-5">
      <div class="d-flex align-items-center mb-4">
        <h5 class="fw-bold mb-0 me-3">QUY TRÌNH ĐẶT HÀNG</h5>
        <div class="flex-grow-1 border-top" style="border-width: 2px !important; border-color: #f0f0f0 !important;"></div>
      </div>
      
      <div class="row g-3">
        <div class="col-md">
          <div class="step-card h-100 p-4 rounded text-start" style="background-color: #f8f9fb;">
            <i class="bi bi-download step-icon mb-3 d-block"></i>
            <h6 class="fw-bold" style="color: #1e293b; font-size: 0.9rem;">BƯỚC 1</h6>
            <p class="small text-muted mb-0">Tải xuống file mẫu yêu cầu gia công từ hệ thống.</p>
          </div>
        </div>
        <div class="col-md">
          <div class="step-card h-100 p-4 rounded text-start" style="background-color: #f8f9fb;">
            <i class="bi bi-file-earmark-text-fill step-icon mb-3 d-block"></i>
            <h6 class="fw-bold" style="color: #1e293b; font-size: 0.9rem;">BƯỚC 2</h6>
            <p class="small text-muted mb-0">Điền thông tin chi tiết: kích thước, vật liệu và số lượng.</p>
          </div>
        </div>
        <div class="col-md">
          <div class="step-card h-100 p-4 rounded text-start" style="background-color: #f8f9fb;">
            <i class="bi bi-file-earmark-arrow-up-fill step-icon mb-3 d-block"></i>
            <h6 class="fw-bold" style="color: #1e293b; font-size: 0.9rem;">BƯỚC 3</h6>
            <p class="small text-muted mb-0">Tải file đã điền và bản vẽ kỹ thuật (nếu có) lên hệ thống.</p>
          </div>
        </div>
        <div class="col-md">
          <div class="step-card h-100 p-4 rounded text-start" style="background-color: #f8f9fb;">
            <i class="bi bi-box-seam-fill step-icon mb-3 d-block"></i>
            <h6 class="fw-bold" style="color: #1e293b; font-size: 0.9rem;">BƯỚC 4</h6>
            <p class="small text-muted mb-0">Kiểm tra thông tin đơn hàng và xác nhận báo giá sơ bộ.</p>
          </div>
        </div>
        <div class="col-md">
          <div class="step-card h-100 p-4 rounded text-start" style="background-color: #f8f9fb;">
            <i class="bi bi-patch-check-fill step-icon mb-3 d-block"></i>
            <h6 class="fw-bold" style="color: #1e293b; font-size: 0.9rem;">BƯỚC 5</h6>
            <p class="small text-muted mb-0">Đơn hàng được khởi tạo và chuyển đến bộ phận kỹ thuật.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Upload section -->
    <div class="container pb-5">
      <div class="mx-auto bg-white shadow-sm rounded-4 p-4 p-md-5" style="max-width: 900px; border: 1px solid #f0f0f0;">
        <!-- Tải File Mẫu -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2">
          <div class="mb-3 mb-md-0">
            <h5 class="fw-bold text-dark mb-2">Tải File Mẫu</h5>
            <p class="text-muted small mb-0">Sử dụng đúng định dạng mẫu để chúng tôi xử lý đơn hàng nhanh nhất.</p>
          </div>
          <a href="/file_mau.xlsx" download="file_mau.xlsx" class="btn fw-bold text-dark px-4 py-3 d-flex align-items-center justify-content-center" style="background-color: #faa41a; border: none; white-space: nowrap; border-radius: 4px;">
            <i class="bi bi-download me-2" style="font-size: 1.1rem;"></i> TẢI XUỐNG FILE MẪU
          </a>
        </div>

        <div class="border-top mb-4" style="border-color: #eaeaea !important;"></div>

        <!-- Khu vực Tải lên -->
        <h5 class="fw-bold text-dark mb-3">Khu Vực Tải Lên</h5>
        
        <div 
          class="upload-zone rounded-3 p-5 text-center mb-4"
          :class="{ 'drag-over': isDragOver, 'disabled': isUploading }"
          @dragover.prevent="handleDragOver"
          @dragleave.prevent="handleDragLeave"
          @drop.prevent="handleDrop"
          @click="triggerFileInput">
          
          <div class="mb-3">
            <div class="d-inline-flex align-items-center justify-content-center bg-secondary bg-opacity-10 rounded-3" style="width: 56px; height: 56px;">
              <i class="bi bi-cloud-arrow-up-fill fs-3" style="color: #1a2f4c;"></i>
            </div>
          </div>
          <h6 class="fw-bold text-dark mb-2">Kéo & Thả File Tại Đây</h6>
          <p class="text-muted small mb-0">Hoặc nhấn để chọn file từ máy tính (Hỗ trợ .xlsx, .pdf, .step)</p>
          
          <input 
            type="file" 
            ref="fileInput" 
            accept=".xlsx,.xls,.pdf,.step" 
            @change="handleFileSelect" 
            :disabled="isUploading"
            class="d-none">
        </div>

        <!-- File đã chọn -->
        <div v-if="selectedFile" class="d-flex justify-content-between align-items-center p-3 rounded mb-4" style="background-color: #f5ece4; border: 1px solid #e1d3c1; border-left: 4px solid #b77926;">
          <div class="d-flex align-items-center">
            <i class="bi bi-file-earmark-text-fill fs-4 me-3" style="color: #af7c31;"></i>
            <div>
              <h6 class="mb-0 fw-bold" style="color: #2c2c2c; font-size: 0.95rem;">{{ selectedFile.name }}</h6>
              <div class="text-muted" style="font-size: 0.8rem;">{{ formatFileSize(selectedFile.size) }} • Đã sẵn sàng</div>
            </div>
          </div>
          <button @click.stop="clearFile" :disabled="isUploading" class="btn text-danger p-2" title="Xóa file">
            <i class="bi bi-x mb-0 fw-bold" style="font-size: 1.2rem;"></i>
          </button>
        </div>

        <div v-if="hasPendingUpload" class="alert py-2 px-3 small mb-4" style="background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba;">
          <i class="bi bi-exclamation-circle me-1"></i>
          Bạn đang có file chưa tải lên. Đừng quên bấm "XÁC NHẬN TẢI LÊN" trước khi rời trang.
        </div>

        <!-- Error / Success Messages -->
        <div v-if="errorMessage" class="alert alert-danger px-3 py-2 small mb-4">
          <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
        </div>
        <div v-if="successMessage" class="alert alert-success px-3 py-2 small mb-4">
          <i class="bi bi-check-circle me-2"></i>{{ successMessage }}
        </div>

        <!-- Action Buttons -->
        <div class="d-flex justify-content-end gap-3 mt-4">
          <button @click="confirmCancelAndLeave" :disabled="isUploading" class="btn btn-outline-dark fw-bold px-4 py-2" style="border-radius: 4px; border-color: #4a5568; color: #4a5568;">
            HỦY BỎ
          </button>
          <button 
            @click="uploadOrder" 
            :disabled="!selectedFile || isUploading" 
            class="btn fw-bold px-4 py-2 text-white" 
            style="background-color: #0d2950; border: none; border-radius: 4px;">
            <span v-if="isUploading" class="spinner-border spinner-border-sm me-2"></span>
            {{ isUploading ? 'ĐANG TẢI LÊN...' : 'XÁC NHẬN TẢI LÊN' }}
          </button>
        </div>
      </div>
    </div>
    <div class="mt-auto">
      <Footer />
    </div>
  </div>
</template>

<script setup>
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'

const router = useRouter()

const fileInput = ref(null)
const selectedFile = ref(null)
const isDragOver = ref(false)
const isUploading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const bypassLeaveGuard = ref(false)

const hasPendingUpload = computed(() => {
  return !!selectedFile.value && !isUploading.value
})

const handleBeforeUnload = (event) => {
  if (!hasPendingUpload.value && !isUploading.value) return
  event.preventDefault()
  event.returnValue = ''
}

onMounted(() => {
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

onBeforeRouteLeave(async () => {
  if (bypassLeaveGuard.value) return true

  if (isUploading.value) {
    await Swal.fire('Đang tải lên', 'Vui lòng chờ upload hoàn tất trước khi rời trang.', 'info')
    return false
  }

  if (!hasPendingUpload.value) return true

  const confirmLeave = await Swal.fire({
    title: 'Rời trang này?',
    text: 'Bạn đã chọn file nhưng chưa tải lên. Nếu rời trang, bạn sẽ phải chọn lại file.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Vẫn rời trang',
    cancelButtonText: 'Ở lại',
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
  })

  return confirmLeave.isConfirmed
})



const triggerFileInput = () => {
  if (isUploading.value) return
  fileInput.value.click()
}

const handleDragOver = () => {
  if (isUploading.value) return
  isDragOver.value = true
}

const handleDragLeave = () => {
  isDragOver.value = false
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  validateAndSetFile(file)
}

const handleDrop = (event) => {
  if (isUploading.value) return
  isDragOver.value = false
  const file = event.dataTransfer.files[0]
  validateAndSetFile(file)
}

const validateAndSetFile = (file) => {
  if (isUploading.value) return
  errorMessage.value = ''
  
  if (!file) return

  // Validate file type
  const validExtensions = ['.xlsx', '.xls', '.pdf', '.step']
  const fileName = file.name.toLowerCase()
  const isValid = validExtensions.some(ext => fileName.endsWith(ext))

  if (!isValid) {
    errorMessage.value = 'Vui lòng chọn file hợp lệ (Excel, PDF, STEP)'
    return
  }

  // Validate file size (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    errorMessage.value = 'File quá lớn. Vui lòng chọn file nhỏ hơn 5MB'
    return
  }

  selectedFile.value = file
}

const clearFile = () => {
  if (isUploading.value) return
  selectedFile.value = null
  errorMessage.value = ''
  successMessage.value = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const confirmCancelAndLeave = async () => {
  if (isUploading.value) return

  if (!hasPendingUpload.value) {
    bypassLeaveGuard.value = true
    router.push('/')
    return
  }

  const result = await Swal.fire({
    title: 'Hủy thao tác upload?',
    text: 'Bạn đang có file chưa tải lên. Nếu thoát, file đã chọn sẽ bị mất.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Thoát trang',
    cancelButtonText: 'Ở lại',
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
  })

  if (!result.isConfirmed) return

  clearFile()
  bypassLeaveGuard.value = true
  router.push('/')
}

const uploadOrder = async () => {
  if (isUploading.value) return

  if (!selectedFile.value) {
    errorMessage.value = 'Vui lòng chọn file'
    return
  }

  isUploading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const response = await apiClient.post('/orders/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    successMessage.value = `Đơn hàng ${response.data.orderNumber} đã được tạo thành công!`
    
    await Swal.fire({
      icon: 'success',
      title: 'Đặt hàng thành công!',
      html: `Mã đơn hàng: <strong>${response.data.orderNumber}</strong><br>Vui lòng chờ Admin báo giá.`,
      timer: 3000
    })

    // Redirect to My Orders
    clearFile()
    bypassLeaveGuard.value = true
    router.push('/my-orders')

  } catch (error) {
    console.error('Upload error:', error)
    errorMessage.value = error.response?.data?.error || 'Có lỗi xảy ra khi tải file. Vui lòng thử lại.'
  } finally {
    isUploading.value = false
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}
</script>

<style scoped>
.header-banner {
  background-color: #0d284f;
  margin-top: -3rem; /* Vượt lên phần navbar nếu cần */
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.step-icon {
  color: #bf8026;
  font-size: 1.5rem;
}

.upload-zone {
  background-color: #f6f8fb;
  border: 2px dashed #d0d7e0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-zone:hover,
.upload-zone.drag-over {
  background-color: #eef2f7 !important;
  border-color: #0d284f !important;
}

.upload-zone.disabled {
  opacity: 0.6;
  pointer-events: none;
}
</style>
