<template>
  <div class="auth-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow auth-card">
      <div class="card-body p-4 p-md-5">

        <h2 class="text-center mb-4 auth-title">Đặt mật khẩu mới</h2>
        <p class="text-center text-muted mb-4 small">
          Mật khẩu phải có 8–32 ký tự, chứa chữ hoa, chữ thường, số và ký tự đặc biệt.
        </p>
        <hr class="mb-4">

        <form @submit.prevent="handleResetPassword" novalidate>

          <div class="mb-3 text-start">
            <label for="newPassword" class="form-label text-muted">Mật khẩu mới*</label>
            <div class="input-group">
              <input
                :type="showPassword ? 'text' : 'password'"
                class="form-control"
                :class="{ 'is-invalid': errors.newPassword }"
                id="newPassword"
                v-model="newPassword"
                autocomplete="new-password"
              >
              <button
                class="btn bg-white border border-start-0 text-secondary"
                type="button"
                @click="showPassword = !showPassword"
                :style="{ borderColor: errors.newPassword ? '#dc3545' : '#ced4da' }"
              >
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
            <span v-if="errors.newPassword" class="text-danger small mt-1 d-block">{{ errors.newPassword }}</span>
          </div>

          <div class="mb-4 text-start">
            <label for="confirmPassword" class="form-label text-muted">Xác nhận mật khẩu*</label>
            <div class="input-group">
              <input
                :type="showConfirm ? 'text' : 'password'"
                class="form-control"
                :class="{ 'is-invalid': errors.confirmPassword }"
                id="confirmPassword"
                v-model="confirmPassword"
                autocomplete="new-password"
              >
              <button
                class="btn bg-white border border-start-0 text-secondary"
                type="button"
                @click="showConfirm = !showConfirm"
                :style="{ borderColor: errors.confirmPassword ? '#dc3545' : '#ced4da' }"
              >
                <i :class="showConfirm ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
            <span v-if="errors.confirmPassword" class="text-danger small mt-1 d-block">{{ errors.confirmPassword }}</span>
          </div>

          <hr>

          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-brown text-white py-2" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ isLoading ? 'Đang cập nhật...' : 'Đặt lại mật khẩu' }}
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
import { useRouter, useRoute } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();
const route = useRoute();

const resetToken = route.query.token || '';
const newPassword = ref('');
const confirmPassword = ref('');
const showPassword = ref(false);
const showConfirm = ref(false);
const isLoading = ref(false);
const errors = ref({});

const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{8,32}$/;

const handleResetPassword = async () => {
  errors.value = {};

  if (!newPassword.value) {
    errors.value.newPassword = 'Vui lòng nhập mật khẩu mới';
    return;
  }
  if (!passwordRegex.test(newPassword.value)) {
    errors.value.newPassword = 'Mật khẩu phải chứa ít nhất một chữ hoa, chữ thường, số và ký tự đặc biệt';
    return;
  }
  if (newPassword.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Mật khẩu xác nhận không khớp';
    return;
  }
  if (!resetToken) {
    Swal.fire({ icon: 'error', title: 'Phiên hết hạn', text: 'Vui lòng thực hiện lại từ đầu.' });
    router.push('/forgot-password');
    return;
  }

  isLoading.value = true;
  try {
    await authAPI.resetPassword(resetToken, newPassword.value);
    await Swal.fire({
      icon: 'success',
      title: 'Đặt lại mật khẩu thành công!',
      text: 'Vui lòng đăng nhập bằng mật khẩu mới.',
      confirmButtonText: 'Đăng nhập ngay',
      confirmButtonColor: '#1e3a8a',
    });
    router.push('/login');
  } catch (error) {
    Swal.fire({
      icon: 'error',
      title: 'Thất bại',
      text: error.response?.data?.message || 'Token không hợp lệ hoặc đã hết hạn. Vui lòng thực hiện lại.',
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

.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
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
