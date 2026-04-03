<template>
  <div class="login-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow login-card">
      <div class="card-body p-4 p-md-5">
        
        <h2 class="text-center mb-4 login-title">Login</h2>
        <hr class="mb-4">

        <form @submit.prevent="handleLogin" novalidate>
          
          <div class="mb-3 text-start">
            <label for="email" class="form-label text-muted">Email</label>
            <input 
              type="email" 
              class="form-control" 
              :class="{ 'is-invalid': errors.email }"
              id="email" 
              v-model="email"
            >
            <span v-if="errors.email" class="text-danger small mt-1 d-block">
              {{ errors.email }}
            </span>
          </div>

          <div class="mb-3 text-start">
            <label for="password" class="form-label text-muted">Password</label>
            <div class="input-group">
              <input 
                :type="showPassword ? 'text' : 'password'" 
                class="form-control" 
                :class="{ 'is-invalid': errors.password }"
                id="password" 
                v-model="password"
                autocomplete="new-password"
              >
              <button 
                class="btn bg-white border border-start-0 text-secondary" 
                type="button" 
                @click="showPassword = !showPassword"
                :style="{ borderColor: errors.password ? '#dc3545' : '#ced4da' }"
              >
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
            <span v-if="errors.password" class="text-danger small mt-1 d-block">
              {{ errors.password }}
            </span>
          </div>

          <div class="mb-4 text-start">
            <div class="form-check">
              <input class="form-check-input" type="checkbox" id="rememberMe" v-model="rememberMe">
              <label class="form-check-label text-secondary" for="rememberMe">
                Remember Me
              </label>
            </div>
          </div>
<hr>
          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-brown text-white py-2" :disabled="isLoading">
              {{ isLoading ? 'Đang đăng nhập...' : 'Login' }}
            </button>
          </div>

          <div class="d-grid gap-2 mb-4">
            <button type="button" class="btn btn-orange text-white py-2">
              Login with Google
            </button>
          </div>

          <div class="text-center text-secondary mt-4 footer-links d-flex flex-wrap justify-content-center">
            <router-link class="text-decoration-none text-secondary custom-link" to="/forgot-password">Forgot password?</router-link>
            <span class="mx-2 d-none d-sm-inline">|</span>
            <router-link class="text-decoration-none text-secondary custom-link" to="/resend-verification">Gửi lại email</router-link>
            <span class="mx-2 d-none d-sm-inline">|</span>
            <router-link class="text-decoration-none text-secondary custom-link font-weight-bold" to="/register">Register</router-link>
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

  if (!email.value) {
    errors.value.email = 'Vui lòng nhập email';
    hasError = true;
  }
  if (!password.value) {
    errors.value.password = 'Vui lòng nhập mật khẩu';
    hasError = true;
  }

  if (hasError) {
    Swal.fire({
      icon: 'error',
      title: 'Đăng nhập thất bại',
      text: 'Vui lòng điền đầy đủ thông tin vào các ô màu đỏ'
    });
    return;
  }

  isLoading.value = true;

  try {
    const response = await authAPI.login(email.value, password.value);
    
    saveAuthData(response, rememberMe.value);

    loadCart();

    Swal.fire({
      icon: 'success',
      title: 'Đăng nhập thành công!',
      text: `Chào mừng ${response.fullName}`,
      timer: 1500,
      showConfirmButton: false,
    });

    setTimeout(() => {
      router.push('/admin/dashboard');
    }, 1500);

  } catch (error) {
    errors.value = {};

    if (error.response?.status === 400 && error.response?.data?.details) {
      errors.value = error.response.data.details;
    }

    Swal.fire({
      icon: 'error',
      title: 'Đăng nhập thất bại',
      text: error.response?.data?.message || 'Không thể kết nối đến máy chủ',
    });
  } finally {
    isLoading.value = false;
  }
};
</script>
<style scoped>
/* Màu sắc chủ đạo từ hình ảnh */
:root {
  --bg-orange: #F57F17; /* Màu nền cam */
  --btn-brown: #3E2723; /* Màu nút Login */
  --btn-google-orange: #E67E22; /* Màu nút Google */
}

.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
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

.login-card {
  width: 100%;
  max-width: 450px;
  border: none;
  border-radius: 8px;
  background-color: #fdfdfd; /* Màu kem nhạt giống ảnh */
}

.login-title {
  color: #333;
  font-weight: 500;
}

/* Custom Input Styles */
.form-control {
  background-color: #fff;
  border: 1px solid #ced4da;
  padding: 10px 15px;
}

.form-control:focus {
  box-shadow: none;
  border-color: #E67E22;
}

/* Custom Button Styles */
.btn-brown {
  background-color: #1e3a8a ;
  border: none;
  font-weight: 500;
}

.btn-brown:hover {
  background-color: #182f6e;
  color: #fff;
}

.btn-orange {
  background-color: #0f172a; /* Màu cam đậm hơn nền một chút */
  border: none;
  font-weight: 500;
}

.btn-orange:hover {
  background-color: #0b1730;
  color: #fff;
}

.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.footer-links a,
.footer-links router-link {
  display: inline;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.footer-links a:hover,
.footer-links router-link:hover {
  color: #3E2723 !important;
  text-decoration: underline !important;
}

hr {
  opacity: 0.1;
}
</style>