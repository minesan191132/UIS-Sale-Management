<template>
  <div class="container py-5 mt-5">
    <div class="row justify-content-center">
      <div class="col-lg-10">
        <!-- Header -->
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body text-center py-5">
            <i class="bi bi-tools fs-1 text-primary mb-3"></i>
            <h2 class="mb-3">Đặt Hàng Gia Công</h2>
            <p class="text-muted">Gửi yêu cầu gia công bằng cách tải lên file Excel theo mẫu</p>
          </div>
        </div>

        <!-- Instructions -->
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header bg-light">
            <h5 class="mb-0"><i class="bi bi-info-circle me-2"></i>Hướng Dẫn Đặt Hàng</h5>
          </div>
          <div class="card-body">
            <ol class="mb-0">
              <li class="mb-2"><strong>Tải file mẫu Excel:</strong> Click nút bên dưới để tải file template.</li>
              <li class="mb-2"><strong>Điền thông tin đơn hàng:</strong> Nhập đầy đủ thông tin vật tư cần gia công theo mẫu.</li>
              <li class="mb-2"><strong>Upload file:</strong> Kéo thả hoặc click để chọn file đã điền thông tin.</li>
              <li class="mb-2"><strong>Chờ báo giá:</strong> Admin sẽ xem đơn và báo giá cho bạn.</li>
              <li class="mb-0"><strong>Thanh toán cọc:</strong> Sau khi nhận báo giá, thanh toán 70% để bắt đầu gia công.</li>
            </ol>
          </div>
        </div>

        <!-- Template Download -->
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body text-center py-4">
            <h5 class="mb-3">Bước 1: Tải File Mẫu</h5>
            <button @click="downloadTemplate" class="btn btn-outline-primary btn-lg">
              <i class="bi bi-download me-2"></i>Tải File Template Excel
            </button>
            <p class="text-muted small mt-2">File: Don_gia_cong_mau.xlsx</p>
          </div>
        </div>

        <!-- File Upload -->
        <div class="card shadow-sm border-0">
          <div class="card-body">
            <h5 class="mb-4">Bước 2: Upload Đơn Hàng</h5>
            
            <!-- Drag & Drop Zone -->
            <div 
              class="upload-zone border rounded p-5 text-center mb-3"
              :class="{ 'drag-over': isDragOver, disabled: isUploading }"
              @dragover.prevent="handleDragOver"
              @dragleave.prevent="handleDragLeave"
              @drop.prevent="handleDrop">
              <i class="bi bi-cloud-upload fs-1 text-muted mb-3"></i>
              <h5 class="mb-2">Kéo thả file vào đây</h5>
              <p class="text-muted mb-3">hoặc</p>
              <input 
                type="file" 
                ref="fileInput" 
                accept=".xlsx,.xls" 
                @change="handleFileSelect" 
                :disabled="isUploading"
                class="d-none">
              <button @click="triggerFileInput" :disabled="isUploading" class="btn btn-primary">
                <i class="bi bi-folder2-open me-2"></i>Chọn File
              </button>
            </div>

            <!-- Selected File Info -->
            <div v-if="selectedFile" class="alert alert-info">
              <i class="bi bi-file-earmark-excel me-2"></i>
              <strong>{{ selectedFile.name }}</strong> 
              ({{ formatFileSize(selectedFile.size) }})
              <button @click="clearFile" :disabled="isUploading" class="btn btn-sm btn-outline-danger float-end">
                <i class="bi bi-x"></i> Xóa
              </button>
            </div>

            <div v-if="hasPendingUpload" class="alert alert-warning py-2 px-3 small">
              <i class="bi bi-exclamation-circle me-1"></i>
              Bạn đang có file chưa tải lên. Đừng quên bấm "Tải Lên" trước khi rời trang.
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="alert alert-danger">
              <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="alert alert-success">
              <i class="bi bi-check-circle me-2"></i>{{ successMessage }}
            </div>

            <!-- Action Buttons -->
            <div class="d-flex gap-2 justify-content-end">
              <button @click="confirmCancelAndLeave" :disabled="isUploading" class="btn btn-outline-secondary">
                <i class="bi bi-x me-2"></i>Hủy
              </button>
              <button 
                @click="uploadOrder" 
                :disabled="!selectedFile || isUploading" 
                class="btn btn-primary">
                <span v-if="isUploading" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-upload me-2"></i>
                {{ isUploading ? 'Đang tải lên...' : 'Tải Lên' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
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

const downloadTemplate = () => {
  // Placeholder: User will provide real template later
  Swal.fire({
    icon: 'info',
    title: 'Template đang chuẩn bị',
    text: 'File mẫu sẽ được cung cấp sau. Hiện tại vui lòng sử dụng file Excel với các cột: Tên vật tư, Quy cách, Số lượng, Đơn vị, Ghi chú',
    confirmButtonText: 'OK'
  })
}

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
  const validExtensions = ['.xlsx', '.xls']
  const fileName = file.name.toLowerCase()
  const isValid = validExtensions.some(ext => fileName.endsWith(ext))

  if (!isValid) {
    errorMessage.value = 'Vui lòng chọn file Excel (.xlsx hoặc .xls)'
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
.upload-zone {
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-zone:hover,
.upload-zone.drag-over {
  background: #e7f1ff;
  border-color: #0d6efd;
}

.upload-zone.disabled {
  opacity: 0.6;
  pointer-events: none;
}
</style>
