<template>
  <div class="auth-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow auth-card">
      <div class="card-body p-4 p-md-5">

        <h2 class="text-center mb-4 auth-title">Nhập mã OTP</h2>
        <p class="text-center text-muted mb-1 small">
          Mã OTP đã được gửi đến <strong>{{ email }}</strong>.
        </p>
        <p class="text-center text-muted mb-4 small">Mã có hiệu lực trong 5 phút.</p>
        <hr class="mb-4">

        <form @submit.prevent="handleVerifyOtp" novalidate>

          <div class="mb-4 text-start">
            <label for="otp" class="form-label text-muted">Mã OTP (6 chữ số)*</label>
            <input
              type="text"
              class="form-control otp-input text-center"
              :class="{ 'is-invalid': errors.otp }"
              id="otp"
              v-model="otp"
              maxlength="6"
              placeholder="• • • • • •"
              autocomplete="one-time-code"
            >
            <span v-if="errors.otp" class="text-danger small mt-1 d-block">{{ errors.otp }}</span>
          </div>

          <hr>

          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-brown text-white py-2" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ isLoading ? 'Đang xác thực...' : 'Xác nhận OTP' }}
            </button>
          </div>

          <div class="text-center text-secondary mt-4 footer-links d-flex flex-wrap justify-content-center">
            <router-link class="text-decoration-none text-secondary custom-link" to="/forgot-password">
              <i class="bi bi-envelope me-1"></i> Gửi lại mã OTP
            </router-link>
            <span class="mx-2">|</span>
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
import { useRouter, useRoute } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();
const route = useRoute();

const email = route.query.email || '';
const otp = ref('');
const isLoading = ref(false);
const errors = ref({});

const handleVerifyOtp = async () => {
  errors.value = {};
  if (!otp.value || otp.value.length !== 6) {
    errors.value.otp = 'OTP phải có đúng 6 chữ số';
    return;
  }

  isLoading.value = true;
  try {
    const response = await authAPI.verifyOtp(email, otp.value);
    const resetToken = response.data.resetToken;
    router.push({ path: '/reset-password', query: { token: resetToken } });
  } catch (error) {
    errors.value.otp = error.response?.data?.message || 'OTP không hợp lệ hoặc đã hết hạn';
    Swal.fire({
      icon: 'error',
      title: 'Xác thực thất bại',
      text: errors.value.otp,
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

.otp-input {
  font-size: 1.4rem;
  letter-spacing: 0.5rem;
  font-weight: 600;
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

.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  font-size: 0.95rem;
}

.custom-link {
  transition: color 0.2s;
}

.custom-link:hover {
  color: #3E2723 !important;
  text-decoration: underline !important;
}

hr { opacity: 0.1; }
</style>
