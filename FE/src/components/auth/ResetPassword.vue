<template>
  <div class="auth-bg">
    <a href="/" class="home-btn">
      <i class="bi bi-house-door-fill"></i>
    </a>

    <div class="auth-card">
      <div class="card-header-bar">
        <div class="brand-logo">UIS</div>
        <div class="brand-sub">Đặt mật khẩu mới</div>
      </div>

      <div class="card-body-inner">
        <p class="page-desc">Mật khẩu phải có 8–32 ký tự, chứa chữ hoa, chữ thường, số và ký tự đặc biệt.</p>

        <form @submit.prevent="handleResetPassword" novalidate>

          <div class="field-group">
            <label for="newPassword" class="field-label">Mật khẩu mới <span class="required">*</span></label>
            <div class="input-with-icon">
              <input
                :type="showPassword ? 'text' : 'password'"
                class="field-input" :class="{ 'input-error': errors.newPassword }"
                id="newPassword" v-model="newPassword"
                placeholder="••••••••" autocomplete="new-password"
              >
              <button type="button" class="eye-btn" @click="showPassword = !showPassword">
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
            <span v-if="errors.newPassword" class="error-msg">{{ errors.newPassword }}</span>
          </div>

          <div class="field-group">
            <label for="confirmPassword" class="field-label">Xác nhận mật khẩu <span class="required">*</span></label>
            <div class="input-with-icon">
              <input
                :type="showConfirm ? 'text' : 'password'"
                class="field-input" :class="{ 'input-error': errors.confirmPassword }"
                id="confirmPassword" v-model="confirmPassword"
                placeholder="••••••••" autocomplete="new-password"
              >
              <button type="button" class="eye-btn" @click="showConfirm = !showConfirm">
                <i :class="showConfirm ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
            <span v-if="errors.confirmPassword" class="error-msg">{{ errors.confirmPassword }}</span>
          </div>

          <button type="submit" class="btn-primary" :disabled="isLoading">
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ isLoading ? 'Đang cập nhật...' : 'Đặt lại mật khẩu' }}
          </button>

          <div class="footer-links">
            <router-link to="/login" class="footer-link">
              <i class="bi bi-arrow-left me-1"></i>Quay lại Đăng nhập
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

  if (!newPassword.value) { errors.value.newPassword = 'Vui lòng nhập mật khẩu mới'; return; }
  if (!passwordRegex.test(newPassword.value)) {
    errors.value.newPassword = 'Mật khẩu phải chứa ít nhất một chữ hoa, chữ thường, số và ký tự đặc biệt'; return;
  }
  if (newPassword.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Mật khẩu xác nhận không khớp'; return;
  }
  if (!resetToken) {
    Swal.fire({ icon: 'error', title: 'Phiên hết hạn', text: 'Vui lòng thực hiện lại từ đầu.' });
    router.push('/forgot-password'); return;
  }

  isLoading.value = true;
  try {
    await authAPI.resetPassword(resetToken, newPassword.value);
    await Swal.fire({
      icon: 'success', title: 'Đặt lại mật khẩu thành công!',
      text: 'Vui lòng đăng nhập bằng mật khẩu mới.',
      confirmButtonText: 'Đăng nhập ngay', confirmButtonColor: '#1e3a8a',
    });
    router.push('/login');
  } catch (error) {
    Swal.fire({
      icon: 'error', title: 'Thất bại',
      text: error.response?.data?.message || 'Token không hợp lệ hoặc đã hết hạn. Vui lòng thực hiện lại.',
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.auth-bg {
  min-height: 100vh; width: 100%;
  background:
    linear-gradient(135deg, rgba(15,23,42,0.82) 0%, rgba(30,58,138,0.78) 100%),
    url('@/assets/picture/HomePageimg/gia-cong-co-khi-chinh-xac.jpg') center / cover no-repeat;
  display: flex; justify-content: center; align-items: center;
  position: relative; padding: 24px 16px;
  font-family: 'Inter', sans-serif;
}

.home-btn {
  position: absolute; top: 20px; left: 20px;
  color: rgba(255,255,255,0.85); font-size: 1.4rem;
  text-decoration: none; transition: color 0.2s, transform 0.2s;
}
.home-btn:hover { color: #fff; transform: scale(1.15); }

.auth-card {
  width: 100%; max-width: 460px; background: #fff;
  border-radius: 16px; box-shadow: 0 24px 64px rgba(0,0,0,0.45); overflow: hidden;
}

.card-header-bar {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 28px 36px; text-align: center;
}
.brand-logo { font-size: 26px; font-weight: 700; color: #fff; letter-spacing: 3px; }
.brand-sub { font-size: 12px; color: #93c5fd; margin-top: 4px; }

.card-body-inner { padding: 30px 36px 32px; }

.page-desc {
  font-size: 13px; color: #64748b; margin-bottom: 24px;
  line-height: 1.6; text-align: center;
  background: #f0f4ff; border-radius: 8px; padding: 10px 14px;
}

.field-group { margin-bottom: 20px; }
.field-label {
  display: block; font-size: 13px; font-weight: 700;
  color: #1e293b; margin-bottom: 6px;
}
.required { color: #ef4444; margin-left: 2px; }

.field-input {
  width: 100%; padding: 11px 14px;
  border: 1.5px solid #e2e8f0; border-radius: 8px;
  font-size: 14px; color: #1e293b; background: #f8fafc;
  transition: all 0.2s; outline: none; box-sizing: border-box;
}
.field-input:focus {
  border-color: #1e3a8a; background: #fff;
  box-shadow: 0 0 0 3px rgba(30,58,138,0.12);
}
.field-input.input-error { border-color: #ef4444; background: #fff5f5; }

.input-with-icon { position: relative; }
.input-with-icon .field-input { padding-right: 42px; }
.eye-btn {
  position: absolute; right: 12px; top: 50%;
  transform: translateY(-50%); background: none; border: none;
  color: #94a3b8; cursor: pointer; font-size: 15px; transition: color 0.2s; padding: 0;
}
.eye-btn:hover { color: #1e3a8a; }
.error-msg { display: block; font-size: 12px; color: #ef4444; margin-top: 5px; }

.btn-primary {
  display: block; width: 100%; padding: 12px;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  color: #fff; font-size: 15px; font-weight: 600;
  border: none; border-radius: 8px; cursor: pointer;
  transition: opacity 0.2s, transform 0.1s; margin-bottom: 22px;
}
.btn-primary:hover:not(:disabled) { opacity: 0.9; transform: translateY(-1px); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.footer-links { text-align: center; }
.footer-link {
  font-size: 13px; color: #64748b; text-decoration: none;
  font-weight: 500; transition: color 0.2s;
}
.footer-link:hover { color: #1e3a8a; text-decoration: underline; }
</style>
