<template>
  <div class="auth-bg">
    <a href="/" class="home-btn">
      <i class="bi bi-house-door-fill"></i>
    </a>

    <div class="auth-card">
      <div class="card-header-bar">
        <div class="brand-logo">UIS</div>
        <div class="brand-sub">Quên mật khẩu</div>
      </div>

      <div class="card-body-inner">
        <p class="page-desc">Nhập email đăng ký của bạn. Chúng tôi sẽ gửi mã OTP để xác thực.</p>

        <form @submit.prevent="handleSendOtp" novalidate>

          <div class="field-group">
            <label for="email" class="field-label">Email đăng ký <span class="required">*</span></label>
            <input
              type="email" class="field-input"
              :class="{ 'input-error': errors.email }"
              id="email" v-model="email" placeholder="abc@gmail.com"
            >
            <span v-if="errors.email" class="error-msg">{{ errors.email }}</span>
          </div>

          <button type="submit" class="btn-primary" :disabled="isLoading">
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
            {{ isLoading ? 'Đang gửi...' : 'Gửi mã OTP' }}
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
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();
const email = ref('');
const isLoading = ref(false);
const errors = ref({});

const handleSendOtp = async () => {
  errors.value = {};
  if (!email.value) { errors.value.email = 'Vui lòng nhập email'; return; }

  isLoading.value = true;
  try {
    await authAPI.forgotPassword(email.value);
    router.push({ path: '/forgot-password/verify', query: { email: email.value } });
  } catch (error) {
    Swal.fire({
      icon: 'error', title: 'Gửi OTP thất bại',
      text: error.response?.data?.message || 'Không thể kết nối đến máy chủ',
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
  border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden;
}
.card-header-bar {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 28px 36px; text-align: center;
}
.brand-logo { font-size: 26px; font-weight: 700; color: #fff; letter-spacing: 3px; }
.brand-sub { font-size: 12px; color: #93c5fd; margin-top: 4px; }

.card-body-inner { padding: 30px 36px 32px; }

.page-desc {
  font-size: 14px; color: #64748b; margin-bottom: 24px;
  line-height: 1.6; text-align: center;
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
.error-msg { display: block; font-size: 12px; color: #ef4444; margin-top: 4px; }

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
