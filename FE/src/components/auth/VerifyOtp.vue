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

/* Nút gửi lại OTP */
.btn-resend {
  background: none;
  border: 1px solid #ced4da;
  border-radius: 6px;
  color: #6c757d;
  font-size: 0.9rem;
  padding: 6px 16px;
  transition: all 0.2s;
}

.btn-resend:not(:disabled):hover {
  border-color: #1e3a8a;
  color: #1e3a8a;
  background-color: #f0f4ff;
}

.btn-resend:disabled {
  opacity: 0.65;
  cursor: not-allowed;
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
