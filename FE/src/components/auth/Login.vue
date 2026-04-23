<template>
  <div class="auth-bg">
    <a href="/" class="home-btn">
      <i class="bi bi-house-door-fill"></i>
    </a>

    <div class="auth-card">
      <!-- Card Header -->
      <div class="card-header-bar">
        <div class="brand-logo">UIS</div>
        <div class="brand-sub">Đăng nhập hệ thống</div>
      </div>

      <div class="card-body-inner">
        <form @submit.prevent="handleLogin" novalidate>

          <div class="field-group">
            <label for="email" class="field-label">Email</label>
            <input
              type="email"
              class="field-input"
              :class="{ 'input-error': errors.email }"
              id="email"
              v-model="email"
              placeholder="Nhập địa chỉ email"
            >
            <span v-if="errors.email" class="error-msg">{{ errors.email }}</span>
          </div>

          <div class="field-group">
            <label for="password" class="field-label">Mật khẩu</label>
            <div class="input-with-icon">
              <input
                :type="showPassword ? 'text' : 'password'"
                class="field-input"
                :class="{ 'input-error': errors.password }"
                id="password"
                v-model="password"
                placeholder="Nhập mật khẩu"
                autocomplete="new-password"
              >
              <button type="button" class="eye-btn" @click="showPassword = !showPassword">
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
            <span v-if="errors.password" class="error-msg">{{ errors.password }}</span>
          </div>

          <div class="remember-row">
            <label class="checkbox-label">
              <input type="checkbox" v-model="rememberMe">
              <span>Ghi nhớ đăng nhập</span>
            </label>
          </div>

          <button type="submit" class="btn-primary" :disabled="isLoading">
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
            {{ isLoading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
          </button>

          <div class="footer-links">
            <router-link to="/forgot-password" class="footer-link">Quên mật khẩu?</router-link>
            <span class="divider">|</span>
            <router-link to="/resend-verification" class="footer-link">Gửi lại email</router-link>
            <span class="divider">|</span>
            <router-link to="/register" class="footer-link">Đăng ký</router-link>
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
import { authAPI, saveAuthData } from '../../services/api';
import { loadCart } from '../../store/cart.js';

const router = useRouter();
const route = useRoute();

const email = ref('');
const password = ref('');
const rememberMe = ref(false);
const isLoading = ref(false);
const errors = ref({});
const showPassword = ref(false);

onMounted(() => {
  if (route.query.verified === 'true') {
    Swal.fire({
      icon: 'success',
      title: 'Kích hoạt thành công!',
      text: 'Tài khoản của bạn đã được kích hoạt. Bạn có thể đăng nhập ngay bây giờ.',
      timer: 3000
    });
    router.replace('/login');
  } else if (route.query.error === 'server') {
    Swal.fire({
      icon: 'error',
      title: 'Kích hoạt thất bại',
      text: 'Có lỗi xảy ra trong quá trình xác thực.',
    });
    router.replace('/login');
  }
});

const handleLogin = async () => {
  errors.value = {};
  let hasError = false;

  if (!email.value) { errors.value.email = 'Vui lòng nhập email'; hasError = true; }
  if (!password.value) { errors.value.password = 'Vui lòng nhập mật khẩu'; hasError = true; }

  if (hasError) return;

  isLoading.value = true;
  try {
    const response = await authAPI.login(email.value, password.value);
    saveAuthData(response, rememberMe.value);
    loadCart();
    Swal.fire({
      icon: 'success', title: 'Đăng nhập thành công!',
      text: `Chào mừng ${response.fullName}`, timer: 1500, showConfirmButton: false,
    });
    setTimeout(() => { router.push('/admin/dashboard'); }, 1500);
  } catch (error) {
    if (error.response?.status === 400 && error.response?.data?.details) {
      errors.value = error.response.data.details;
    }
    Swal.fire({
      icon: 'error', title: 'Đăng nhập thất bại',
      text: error.response?.data?.message || 'Không thể kết nối đến máy chủ',
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

/* ===== BACKGROUND ===== */
.auth-bg {
  min-height: 100vh;
  width: 100%;
  background:
    linear-gradient(135deg, rgba(15,23,42,0.82) 0%, rgba(30,58,138,0.78) 100%),
    url('@/assets/picture/HomePageimg/gia-cong-co-khi-chinh-xac.jpg') center / cover no-repeat;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 24px 16px;
  font-family: 'Inter', sans-serif;
}

/* ===== HOME BUTTON ===== */
.home-btn {
  position: absolute;
  top: 20px; left: 20px;
  color: rgba(255,255,255,0.85);
  font-size: 1.4rem;
  text-decoration: none;
  transition: color 0.2s, transform 0.2s;
}
.home-btn:hover { color: #fff; transform: scale(1.15); }

/* ===== CARD ===== */
.auth-card {
  width: 100%;
  max-width: 460px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  overflow: hidden;
}

/* ===== CARD HEADER BAR ===== */
.card-header-bar {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 28px 36px;
  text-align: center;
}
.brand-logo {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 3px;
}
.brand-sub {
  font-size: 12px;
  color: #93c5fd;
  margin-top: 4px;
  font-weight: 400;
  letter-spacing: 0.5px;
}

/* ===== CARD BODY ===== */
.card-body-inner {
  padding: 32px 36px;
}

/* ===== FIELDS ===== */
.field-group {
  margin-bottom: 20px;
}

.field-label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
  letter-spacing: 0.3px;
}

.field-input {
  width: 100%;
  padding: 11px 14px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1e293b;
  background: #f8fafc;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
  outline: none;
  box-sizing: border-box;
}
.field-input:focus {
  border-color: #1e3a8a;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(30,58,138,0.12);
}
.field-input.input-error {
  border-color: #ef4444;
  background: #fff5f5;
}

.input-with-icon {
  position: relative;
}
.input-with-icon .field-input {
  padding-right: 42px;
}
.eye-btn {
  position: absolute;
  right: 12px; top: 50%;
  transform: translateY(-50%);
  background: none; border: none;
  color: #94a3b8; cursor: pointer;
  padding: 0; font-size: 15px;
  transition: color 0.2s;
}
.eye-btn:hover { color: #1e3a8a; }

.error-msg {
  display: block;
  font-size: 12px;
  color: #ef4444;
  margin-top: 5px;
}

/* ===== REMEMBER ROW ===== */
.remember-row {
  margin-bottom: 22px;
}
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  cursor: pointer;
  user-select: none;
}
.checkbox-label input { accent-color: #1e3a8a; width: 15px; height: 15px; cursor: pointer; }

/* ===== PRIMARY BUTTON ===== */
.btn-primary {
  display: block;
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
  letter-spacing: 0.3px;
  margin-bottom: 24px;
}
.btn-primary:hover:not(:disabled) { opacity: 0.9; transform: translateY(-1px); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

/* ===== FOOTER LINKS ===== */
.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 6px;
  font-size: 13px;
}
.footer-link {
  color: #64748b;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.footer-link:hover { color: #1e3a8a; text-decoration: underline; }
.divider { color: #cbd5e1; }
</style>