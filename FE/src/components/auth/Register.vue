<template>
  <div class="register-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow register-card">
      <div class="card-body p-4 p-md-5">
        
        <h2 class="text-center mb-4 register-title">Register</h2>
        <hr class="mb-4">

        <form @submit.prevent="handleRegister">
          <div class="mb-3 text-start">
            <label class="form-label text-muted">Họ và Tên*</label>
            <input type="text" class="form-control" v-model="form.fullName" required>
          </div>

          <div class="mb-3 text-start">
            <label class="form-label text-muted">Email*</label>
            <input type="email" class="form-control" v-model="form.email" required>
          </div>

          <div class="row mb-3">
            <div class="col-md-6 text-start">
              <label class="form-label text-muted">Mã số thuế công ty*</label>
              <input type="text" class="form-control" v-model="form.taxCode" required 
                     pattern="[0-9]{10,13}" title="Mã số thuế phải có 10-13 chữ số">
            </div>
            <div class="col-md-6 text-start">
              <label class="form-label text-muted">Số điện thoại công ty*</label>
              <input type="tel" class="form-control" v-model="form.companyPhone" required
                     pattern="0[0-9]{9,10}" title="SĐT phải bắt đầu bằng 0 và có 10-11 số">
            </div>
          </div>

          <div class="mb-3 text-start">
            <label class="form-label text-muted">Email công ty (tùy chọn)</label>
            <input type="email" class="form-control" v-model="form.companyEmail">
          </div>

          <div class="row mb-3">
            <div class="col-6 text-start">
              <label class="form-label text-muted">Mật khẩu*</label>
              <input type="password" class="form-control" v-model="form.password" required
                     minlength="6">
            </div>
            <div class="col-6 text-start">
              <label class="form-label text-muted">Nhập lại mật khẩu*</label>
              <input type="password" class="form-control" v-model="form.confirmPassword" required>
            </div>
          </div>

<hr>
          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-brown text-white py-2">
              {{ isLoading ? 'Đang đăng ký...' : 'Register' }}
            </button>
          </div>

          <div class="d-grid gap-2 mb-4">
            <button type="button" class="btn btn-orange text-white py-2">
              Register with Google
            </button>
          </div>

          <div class="text-center text-secondary mt-4 footer-links">
            <span class="footer-link">Already have an account? </span>
            <span>|</span>
            <router-link class="footer-link text-decoration-none text-secondary" to="/login">Login</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();

const form = reactive({
  fullName: '',
  email: '',
  taxCode: '',
  companyPhone: '',
  companyEmail: '',
  password: '',
  confirmPassword: ''
});

const isLoading = ref(false);

const isFormValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const phoneRegex = /^0[0-9]{9,10}$/;
  const taxCodeRegex = /^[0-9]{10,13}$/;

  return (
    form.fullName.length > 0 &&
    emailRegex.test(form.email) &&
    taxCodeRegex.test(form.taxCode) &&
    phoneRegex.test(form.companyPhone) &&
    form.password.length >= 6 &&
    form.password === form.confirmPassword
  );
});

const handleRegister = async () => {
  if (!isFormValid.value) {
    Swal.fire('Lỗi', 'Vui lòng kiểm tra lại thông tin!', 'warning');
    return;
  }

  isLoading.value = true;

  try {
    // Prepare DTO matching backend RegisterDTO
    const registerData = {
      email: form.email,
      password: form.password,
      fullName: form.fullName,
      taxCode: form.taxCode,
      companyPhone: form.companyPhone,
      companyEmail: form.companyEmail || null
    };

    await authAPI.register(registerData);

    Swal.fire({
      icon: 'success',
      title: 'Đăng ký thành công!',
      text: 'Bạn có thể đăng nhập ngay bây giờ',
      timer: 2000,
      showConfirmButton: false,
    });

    // Redirect to login after 2 seconds
    setTimeout(() => {
      router.push('/login');
    }, 2000);

  } catch (error) {
    console.error('Register error:', error);
    
    let errorMessage = 'Đăng ký thất bại';
    if (error.response?.data?.error) {
      errorMessage = error.response.data.error;
    } else if (error.response?.data) {
      errorMessage = error.response.data;
    }

    Swal.fire({
      icon: 'error',
      title: 'Lỗi đăng ký',
      text: errorMessage,
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Màu sắc theo thiết kế */
.register-container {
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

.home-icon:hover {
  transform: scale(1.1);
}

.register-card {
  width: 100%;
  max-width: 550px; /* Rộng hơn một chút để chứa 2 cột password */
  border: none;
  border-radius: 8px;
  background-color: #fdfdfd;
}

.register-title {
  font-weight: 500;
  color: #333;
}

/* Tùy chỉnh Input */
.form-control {
  border: 1px solid #ced4da;
  padding: 10px;
}

/* Nút bấm */
.btn-brown {
  background-color: #1e3a8a ;
  border: none;
}

.btn-brown:hover {
  background-color: #182f6e;
}

.btn-orange {
  background-color: #0f172a;
  border: none;
}

.btn-orange:hover {
  background-color: #080c16;
}

/* Footer Links */
.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.footer-text {
  display: inline;
  font-size: 0.95rem;
}

.footer-link {
  display: inline;
  font-size: 0.95rem;
  cursor: pointer;
  transition: color 0.2s;
  color: #6c757d !important;
}

.footer-link:hover {
  color: #3E2723 !important;
  text-decoration: underline !important;
}

/* Link Login */
.orange-link {
  color: #0f172a;
}

.orange-link:hover {
  text-decoration: underline !important;
}

hr {
  opacity: 0.1;
}

/* Responsive cho mobile: Password xuống hàng thay vì chia cột quá hẹp */
@media (max-width: 576px) {
  .col-6 {
    width: 100%;
    margin-bottom: 1rem;
  }
}
</style>