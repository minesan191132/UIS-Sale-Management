<template>
  <div class="login-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow login-card">
      <div class="card-body p-4 p-md-5">
        
        <h2 class="text-center mb-4 login-title">Login</h2>
        <hr class="mb-4">

        <form @submit.prevent="handleLogin">
          <div class="mb-3 text-start">
            <label for="email" class="form-label text-muted">Email</label>
            <input 
              type="email" 
              class="form-control" 
              id="email" 
              v-model="email"
              required
            >
          </div>

          <div class="mb-3 text-start">
            <label for="password" class="form-label text-muted">Password</label>
            <input 
              type="password" 
              class="form-control" 
              id="password" 
              v-model="password"
              required
            >
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

          <div class="text-center text-secondary mt-4 footer-links">
            <a href="#" class="text-decoration-none text-secondary">Forgot password?</a>
            <span class="mx-2">|</span>
            <router-link class="nav-link custom-link text-decoration-none text-secondary" to="/register">Register</router-link>
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
import { authAPI, saveAuthData } from '../../services/api';

const router = useRouter();

const email = ref('');
const password = ref('');
const rememberMe = ref(false);
const isLoading = ref(false);

const handleLogin = async () => {
  if (!email.value || !password.value) {
    Swal.fire('Lỗi', 'Vui lòng nhập đầy đủ thông tin', 'error');
    return;
  }

  isLoading.value = true;

  try {
    const response = await authAPI.login(email.value, password.value);
    
    // Save token and user info
    saveAuthData(response);

    Swal.fire({
      icon: 'success',
      title: 'Đăng nhập thành công!',
      text: `Chào mừng ${response.fullName}`,
      timer: 1500,
      showConfirmButton: false,
    });

    // Redirect to admin dashboard
    setTimeout(() => {
      router.push('/admin/dashboard');
    }, 1500);

  } catch (error) {
    console.error('Login error:', error);
    Swal.fire({
      icon: 'error',
      title: 'Đăng nhập thất bại',
      text: error.response?.data?.error || 'Email hoặc mật khẩu không đúng',
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