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
              :class="{ 'drag-over': isDragOver }"
              @dragover.prevent="isDragOver = true"
              @dragleave.prevent="isDragOver = false"
              @drop.prevent="handleDrop">
              <i class="bi bi-cloud-upload fs-1 text-muted mb-3"></i>
              <h5 class="mb-2">Kéo thả file vào đây</h5>
              <p class="text-muted mb-3">hoặc</p>
              <input 
                type="file" 
                ref="fileInput" 
                accept=".xlsx,.xls" 
                @change="handleFileSelect" 
                class="d-none">
              <button @click="triggerFileInput" class="btn btn-primary">
                <i class="bi bi-folder2-open me-2"></i>Chọn File
              </button>
            </div>

            <!-- Selected File Info -->
            <div v-if="selectedFile" class="alert alert-info">
              <i class="bi bi-file-earmark-excel me-2"></i>
              <strong>{{ selectedFile.name }}</strong> 
              ({{ formatFileSize(selectedFile.size) }})
              <button @click="clearFile" class="btn btn-sm btn-outline-danger float-end">
                <i class="bi bi-x"></i> Xóa
              </button>
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
              <router-link to="/" class="btn btn-outline-secondary">
                <i class="bi bi-x me-2"></i>Hủy
              </router-link>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import apiClient from '../../services/api'

const router = useRouter()

const fileInput = ref(null)
const selectedFile = ref(null)
const isDragOver = ref(false)
const isUploading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

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
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  validateAndSetFile(file)
}

const handleDrop = (event) => {
  isDragOver.value = false
  const file = event.dataTransfer.files[0]
  validateAndSetFile(file)
}

const validateAndSetFile = (file) => {
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
  selectedFile.value = null
  errorMessage.value = ''
  successMessage.value = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const uploadOrder = async () => {
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
</style>
