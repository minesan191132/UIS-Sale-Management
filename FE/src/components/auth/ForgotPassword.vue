<template>
  <div class="auth-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow auth-card">
      <div class="card-body p-4 p-md-5">

        <h2 class="text-center mb-4 auth-title">Quên mật khẩu</h2>
        <p class="text-center text-muted mb-4 small">
          Nhập email đăng ký của bạn. Chúng tôi sẽ gửi mã OTP để xác thực.
        </p>
        <hr class="mb-4">

        <form @submit.prevent="handleSendOtp" novalidate>

          <div class="mb-4 text-start">
            <label for="email" class="form-label text-muted">Email đăng ký*</label>
            <input
              type="email"
              class="form-control"
              :class="{ 'is-invalid': errors.email }"
              id="email"
              v-model="email"
              placeholder="Ví dụ: abc@gmail.com"
            >
            <span v-if="errors.email" class="text-danger small mt-1 d-block">{{ errors.email }}</span>
          </div>

          <hr>

          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-brown text-white py-2" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ isLoading ? 'Đang gửi...' : 'Gửi mã OTP' }}
            </button>
          </div>

          <div class="text-center text-secondary mt-4 footer-links d-flex flex-wrap justify-content-center">
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();
const email = ref('');
const isLoading = ref(false);
const errors = ref({});

const handleSendOtp = async () => {
  errors.value = {};
  if (!email.value) {
    errors.value.email = 'Vui lòng nhập email';
    return;
  }

  isLoading.value = true;
  try {
    await authAPI.forgotPassword(email.value);
    router.push({ path: '/forgot-password/verify', query: { email: email.value } });
  } catch (error) {
    Swal.fire({
      icon: 'error',
      title: 'Gửi OTP thất bại',
      text: error.response?.data?.message || 'Không thể kết nối đến máy chủ',
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
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

.home-icon:hover { transform: scale(1.1); }

.auth-card {
  width: 100%;
  max-width: 450px;
  border: none;
  border-radius: 8px;
  background-color: #fdfdfd;
}

.auth-title {
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
  border-color: #E67E22;
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

.footer-links a, .footer-links router-link {
  display: inline;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.custom-link:hover {
  color: #3E2723 !important;
  text-decoration: underline !important;
}

hr { opacity: 0.1; }
</style>
