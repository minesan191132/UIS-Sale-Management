<template>
  <div class="register-container">
    <a href="/" class="home-icon text-white">
      <i class="bi bi-house-door-fill fs-2"></i>
    </a>

    <div class="card shadow register-card">
      <div class="card-body p-4 p-md-5">
        
        <h2 class="text-center mb-4 register-title">Register</h2>
        <hr class="mb-4">

        <form @submit.prevent="handleRegister" novalidate>
          
          <div class="mb-3 text-start">
            <label class="form-label text-muted">Họ và Tên*</label>
            <input 
              type="text" 
              class="form-control" 
              :class="{ 'is-invalid': errors.fullName }"
              v-model="form.fullName"
            >
            <span v-if="errors.fullName" class="text-danger small mt-1 d-block">
              {{ errors.fullName }}
            </span>
          </div>

          <div class="mb-3 text-start">
            <label class="form-label text-muted">Email*</label>
            <input 
              type="email" 
              class="form-control" 
              :class="{ 'is-invalid': errors.email }"
              v-model="form.email"
            >
            <span v-if="errors.email" class="text-danger small mt-1 d-block">
              {{ errors.email }}
            </span>
          </div>

          <div class="mb-3 text-start">
            <label class="form-label text-muted">Số điện thoại cá nhân*</label>
            <input 
              type="tel" 
              class="form-control" 
              :class="{ 'is-invalid': errors.phone }"
              v-model="form.phone"
            >
            <span v-if="errors.phone" class="text-danger small mt-1 d-block">
              {{ errors.phone }}
            </span>
          </div>

          <div class="row mb-3">
            <div class="col-md-6 text-start">
              <label class="form-label text-muted">Mã số thuế công ty*</label>
              <input 
                type="text" 
                class="form-control" 
                :class="{ 'is-invalid': errors.taxCode }"
                v-model="form.taxCode"
              >
              <span v-if="errors.taxCode" class="text-danger small mt-1 d-block">
                {{ errors.taxCode }}
              </span>
            </div>
            <div class="col-md-6 text-start">
              <label class="form-label text-muted">Số điện thoại công ty*</label>
              <input 
                type="tel" 
                class="form-control" 
                :class="{ 'is-invalid': errors.companyPhone }"
                v-model="form.companyPhone"
              >
              <span v-if="errors.companyPhone" class="text-danger small mt-1 d-block">
                {{ errors.companyPhone }}
              </span>
            </div>
          </div>

          <div class="mb-3 text-start">
            <label class="form-label text-muted">Email công ty (tùy chọn)</label>
            <input 
              type="email" 
              class="form-control" 
              :class="{ 'is-invalid': errors.companyEmail }"
              v-model="form.companyEmail"
            >
            <span v-if="errors.companyEmail" class="text-danger small mt-1 d-block">
              {{ errors.companyEmail }}
            </span>
          </div>

          <div class="row mb-3">
            <div class="col-6 text-start">
              <label class="form-label text-muted">Mật khẩu*</label>
              <div class="input-group">
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.password }"
                  v-model="form.password"
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

            <div class="col-6 text-start">
              <label class="form-label text-muted">Nhập lại mật khẩu*</label>
              <div class="input-group">
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'" 
                  class="form-control" 
                  :class="{ 'is-invalid': errors.confirmPassword }"
                  v-model="form.confirmPassword"
                  autocomplete="new-password"
                >
                <button 
                  class="btn bg-white border border-start-0 text-secondary" 
                  type="button" 
                  @click="showConfirmPassword = !showConfirmPassword"
                  :style="{ borderColor: errors.confirmPassword ? '#dc3545' : '#ced4da' }"
                >
                  <i :class="showConfirmPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
              <span v-if="errors.confirmPassword" class="text-danger small mt-1 d-block">
                {{ errors.confirmPassword }}
              </span>
            </div>
          </div>

          <hr>
          
          <div class="d-grid gap-2 mb-3">
            <button type="submit" class="btn btn-brown text-white py-2" :disabled="isLoading">
              {{ isLoading ? 'Đang đăng ký...' : 'Register' }}
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
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { authAPI } from '../../services/api';

const router = useRouter();
const showPassword = ref(false);
const showConfirmPassword = ref(false);

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  taxCode: '',
  companyPhone: '',
  companyEmail: '',
  password: '',
  confirmPassword: ''
});

const isLoading = ref(false);
const errors = ref({}); 

const handleRegister = async () => {
  errors.value = {}; 
  let hasError = false;

  if (!form.fullName) {
    errors.value.fullName = 'Vui lòng nhập họ và tên';
    hasError = true;
  }
  if (!form.email) {
    errors.value.email = 'Vui lòng nhập email';
    hasError = true;
  }
  if (!form.phone) {
    errors.value.phone = 'Vui lòng nhập số điện thoại cá nhân';
    hasError = true;
  } else if (!/^0[0-9]{9}$/.test(form.phone)) {
    errors.value.phone = 'Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số';
    hasError = true;
  }
  if (!form.taxCode) {
    errors.value.taxCode = 'Vui lòng nhập mã số thuế';
    hasError = true;
  }
  if (!form.companyPhone) {
    errors.value.companyPhone = 'Vui lòng nhập số điện thoại';
    hasError = true;
  }
  if (!form.password) {
    errors.value.password = 'Vui lòng nhập mật khẩu';
    hasError = true;
  }
  
  if (!form.confirmPassword) {
    errors.value.confirmPassword = 'Vui lòng nhập lại mật khẩu';
    hasError = true;
  } else if (form.password !== form.confirmPassword) {
    errors.value.confirmPassword = 'Mật khẩu nhập lại không khớp';
    hasError = true;
  }

  if (hasError) {
    Swal.fire({
      icon: 'error',
      title: 'Dữ liệu không hợp lệ',
      text: 'Vui lòng điền đầy đủ thông tin'
    });
    return; 
  }

  isLoading.value = true;

  try {
    const registerData = {
      email: form.email,
      password: form.password,
      fullName: form.fullName,
      phone: form.phone,
      taxCode: form.taxCode,
      companyPhone: form.companyPhone,
      companyEmail: form.companyEmail || null
    };

    const response = await authAPI.register(registerData);

    const successMessage = response.data?.message || 'Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt.';

    Swal.fire({
      icon: 'success',
      title: 'Đăng ký thành công!',
      text: successMessage,
      showConfirmButton: true,
      confirmButtonText: 'Đã hiểu'
    }).then(() => {
      router.push('/login');
    });

  } catch (error) {
    errors.value = {};

    if (error.response?.status === 400 && error.response?.data?.details) {
      errors.value = error.response.data.details;
    }

    Swal.fire({
      icon: 'error',
      title: 'Đăng ký thất bại',
      text: error.response?.data?.message || 'Không thể kết nối đến máy chủ',
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