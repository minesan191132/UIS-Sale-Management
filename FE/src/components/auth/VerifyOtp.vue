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

          <div class="text-center mt-3">
            <button
              type="button"
              class="btn btn-resend"
              :disabled="countdown > 0 || isResending"
              @click="handleResendOtp"
            >
              <span v-if="isResending" class="spinner-border spinner-border-sm me-1" role="status"></span>
              <span v-if="countdown > 0">
                <i class="bi bi-clock me-1"></i>Gửi lại sau {{ countdown }} giây
              </span>
              <span v-else>
                <i class="bi bi-envelope me-1"></i>Gửi lại mã OTP
              </span>
            </button>
          </div>

          <div class="text-center text-secondary mt-3 footer-links d-flex flex-wrap justify-content-center">
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
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();
const route = useRoute();

const email = route.query.email || '';
const otp = ref('');
const isLoading = ref(false);
const isResending = ref(false);
const errors = ref({});

// === Đếm ngược ===
const COUNTDOWN_SECONDS = 60;
const countdown = ref(COUNTDOWN_SECONDS);
let countdownTimer = null;

const startCountdown = () => {
  clearInterval(countdownTimer);
  countdown.value = COUNTDOWN_SECONDS;
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(countdownTimer);
    }
  }, 1000);
};

onMounted(() => {
  startCountdown();
});

onUnmounted(() => {
  clearInterval(countdownTimer);
});

// === Xác thực OTP ===
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

// === Gửi lại OTP ===
const handleResendOtp = async () => {
  if (countdown.value > 0 || isResending.value) return;

  isResending.value = true;
  try {
    await authAPI.forgotPassword(email);

    // Xóa trắng ô OTP để không nhập lại mã cũ
    otp.value = '';
    errors.value = {};

    // Reset đếm ngược
    startCountdown();

    Swal.fire({
      icon: 'success',
      title: 'Đã gửi lại mã OTP!',
      text: `Mã OTP mới đã được gửi đến ${email}. Vui lòng kiểm tra hộp thư.`,
      timer: 3000,
      showConfirmButton: false,
    });
  } catch (error) {
    Swal.fire({
      icon: 'error',
      title: 'Gửi lại thất bại',
      text: error.response?.data?.message || 'Không thể gửi lại mã OTP. Vui lòng thử lại.',
    });
  } finally {
    isResending.value = false;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.auth-container {
  min-height: 100vh; width: 100%;
  background:
    linear-gradient(135deg, rgba(15,23,42,0.82) 0%, rgba(30,58,138,0.78) 100%),
    url('@/assets/picture/HomePageimg/gia-cong-co-khi-chinh-xac.jpg') center / cover no-repeat;
  display: flex; justify-content: center; align-items: center;
  position: relative; padding: 24px 16px;
  font-family: 'Inter', sans-serif;
}

.home-icon {
  position: absolute; top: 20px; left: 20px;
  color: rgba(255,255,255,0.85); font-size: 1.4rem;
  text-decoration: none; transition: color 0.2s, transform 0.2s;
}
.home-icon:hover { color: #fff; transform: scale(1.15); }

.auth-card {
  width: 100%; max-width: 450px; background: #fff;
  border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden;
}

/* Thêm class này vào template nếu cần header giống các trang khác */
.auth-title {
  font-size: 20px; font-weight: 700; color: #1e3a8a;
  text-align: center; margin-bottom: 8px;
}

.form-label {
  display: block; font-size: 13px; font-weight: 700;
  color: #1e293b; margin-bottom: 6px;
}

.form-control {
  width: 100%; padding: 11px 14px;
  border: 1.5px solid #e2e8f0; border-radius: 8px;
  font-size: 14px; color: #1e293b; background: #f8fafc;
  transition: all 0.2s; outline: none;
}
.form-control:focus {
  border-color: #1e3a8a; background: #fff;
  box-shadow: 0 0 0 3px rgba(30,58,138,0.12);
}
.form-control.is-invalid { border-color: #ef4444; background: #fff5f5; }

.otp-input {
  font-size: 1.6rem; letter-spacing: 10px; font-weight: 700;
  text-align: center;
}

.btn-brown {
  display: block; width: 100%; padding: 12px;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  color: #fff; font-size: 15px; font-weight: 600;
  border: none; border-radius: 8px; cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
}
.btn-brown:hover:not(:disabled) { opacity: 0.9; transform: translateY(-1px); }
.btn-brown:disabled { opacity: 0.6; cursor: not-allowed; }

/* Nút gửi lại OTP */
.btn-resend {
  background: none; border: 1.5px solid #e2e8f0;
  border-radius: 8px; color: #64748b;
  font-size: 13px; font-weight: 500;
  padding: 8px 18px; transition: all 0.2s; cursor: pointer;
}
.btn-resend:not(:disabled):hover {
  border-color: #1e3a8a; color: #1e3a8a; background: #f0f4ff;
}
.btn-resend:disabled { opacity: 0.55; cursor: not-allowed; }

.footer-links {
  display: flex; align-items: center; justify-content: center;
  gap: 6px; font-size: 13px;
}
.custom-link {
  color: #64748b; text-decoration: none; font-weight: 500; transition: color 0.2s;
}
.custom-link:hover { color: #1e3a8a !important; text-decoration: underline !important; }

hr { opacity: 0.08; }
</style>

