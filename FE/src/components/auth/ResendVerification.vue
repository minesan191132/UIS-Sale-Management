<template>
    <div class="resend-container">
      <a href="/" class="home-icon text-white">
        <i class="bi bi-house-door-fill fs-2"></i>
      </a>
  
      <div class="card shadow resend-card">
        <div class="card-body p-4 p-md-5">
          
          <h2 class="text-center mb-4 resend-title">Gửi lại Email kích hoạt</h2>
          <p class="text-center text-muted mb-4 small">
            Vui lòng nhập email bạn đã dùng để đăng ký. Chúng tôi sẽ gửi lại một đường link kích hoạt mới.
          </p>
          <hr class="mb-4">
  
          <form @submit.prevent="handleResend" novalidate>
            
            <div class="mb-4 text-start">
              <label for="email" class="form-label text-muted">Email đăng ký*</label>
              <input 
                type="email" 
                class="form-control" 
                :class="{ 'is-invalid': errors.email }"
                id="email" 
                v-model="email"
                placeholder="Ví dụ: admin@congty.com"
              >
              <span v-if="errors.email" class="text-danger small mt-1 d-block">
                {{ errors.email }}
              </span>
            </div>
  
            <div class="d-grid gap-2 mb-4">
              <button type="submit" class="btn btn-brown text-white py-2" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                {{ isLoading ? 'Đang gửi...' : 'Gửi lại link kích hoạt' }}
              </button>
            </div>
  
            <div class="text-center mt-4">
              <router-link class="text-decoration-none text-secondary custom-link" to="/login">
                <i class="bi bi-arrow-left me-1"></i> Quay lại Đăng nhập
              </router-link>
            </div>
  
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import Swal from 'sweetalert2';
  import { authAPI } from '../../services/api';
  
  const router = useRouter();
  const route = useRoute();
  
  const email = ref('');
  const isLoading = ref(false);
  const errors = ref({});
  
  onMounted(() => {
    if (route.query.error === 'expired') {
      Swal.fire({
        icon: 'warning',
        title: 'Link đã hết hạn',
        text: 'Đường link kích hoạt cũ đã hết hạn. Vui lòng nhập email để nhận link mới.',
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 4000
      });
      router.replace('/resend-verification');
    }
  });
  
  const handleResend = async () => {
    errors.value = {};
    
    if (!email.value) {
      errors.value.email = 'Vui lòng nhập email để tiếp tục';
      return;
    }
  
    isLoading.value = true;
  
    try {
      const response = await authAPI.resendVerification({ email: email.value });
      
      Swal.fire({
        icon: 'success',
        title: 'Đã gửi thành công!',
        text: response.data?.message || 'Vui lòng kiểm tra hộp thư đến (và thư rác) của bạn.',
        confirmButtonText: 'Về trang Đăng nhập'
      }).then(() => {
        router.push('/login');
      });
  
    } catch (error) {
      errors.value = {};
      console.error('Resend error:', error);
  
      if (error.response?.status === 400 && error.response?.data?.details) {
        errors.value = error.response.data.details;
      }
  
      Swal.fire({
        icon: 'error',
        title: 'Gửi thất bại',
        text: error.response?.data?.message || error.response?.data?.error || 'Không thể kết nối đến máy chủ',
      });
    } finally {
      isLoading.value = false;
    }
  };
  </script>
  
  <style scoped>
  .resend-container {
    min-height: 100vh;
    width: 100%;
    background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
    background-size: cover;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    padding: 20px;
  }
  
  .home-icon {
    position: absolute;
    top: 20px;
    left: 20px;
    cursor: pointer;
    transition: transform 0.2s;
  }
  
  .home-icon:hover {
    transform: scale(1.1);
  }
  
  .resend-card {
    width: 100%;
    max-width: 450px;
    border: none;
    border-radius: 8px;
    background-color: #fdfdfd;
  }
  
  .resend-title {
    color: #333;
    font-weight: 500;
  }
  
  .form-control {
    background-color: #fff;
    border: 1px solid #ced4da;
    padding: 10px 15px;
  }
  
  .form-control:focus {
    box-shadow: none;
    border-color: #1e3a8a;
  }
  
  .btn-brown {
    background-color: #1e3a8a;
    border: none;
    font-weight: 500;
  }
  
  .btn-brown:hover {
    background-color: #182f6e;
    color: #fff;
  }
  
  .custom-link {
    transition: color 0.2s;
  }
  
  .custom-link:hover {
    color: #1e3a8a !important;
    text-decoration: underline !important;
  }
  </style>