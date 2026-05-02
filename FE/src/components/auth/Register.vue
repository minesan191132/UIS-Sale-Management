<template>
  <div class="auth-bg">
    <a href="/" class="home-btn">
      <i class="bi bi-house-door-fill"></i>
    </a>

    <div class="auth-card">
      <!-- Card Header -->
      <div class="card-header-bar">
        <div class="brand-logo">UIS</div>
        <div class="brand-sub">Tạo tài khoản mới</div>
      </div>

      <div class="card-body-inner">
        <form @submit.prevent="handleRegister" novalidate>

          <!-- Thông tin cá nhân -->
          <div class="section-label">Thông tin cá nhân</div>

          <div class="field-group">
            <label class="field-label">Họ và Tên <span class="required">*</span></label>
            <input type="text" class="field-input" :class="{ 'input-error': errors.fullName }"
              v-model="form.fullName" placeholder="Nguyễn Văn A">
            <span v-if="errors.fullName" class="error-msg">{{ errors.fullName }}</span>
          </div>

          <div class="field-row">
            <div class="field-group">
              <label class="field-label">Email <span class="required">*</span></label>
              <input type="email" class="field-input" :class="{ 'input-error': errors.email }"
                v-model="form.email" placeholder="abc@gmail.com">
              <span v-if="errors.email" class="error-msg">{{ errors.email }}</span>
            </div>
            <div class="field-group">
              <label class="field-label">Số điện thoại cá nhân <span class="required">*</span></label>
              <input type="tel" class="field-input" :class="{ 'input-error': errors.phone }"
                v-model="form.phone" placeholder="0901234567">
              <span v-if="errors.phone" class="error-msg">{{ errors.phone }}</span>
            </div>
          </div>

          <!-- Thông tin công ty -->
          <div class="section-label" style="margin-top:8px;">Thông tin công ty</div>

          <div class="field-row">
            <div class="field-group">
              <label class="field-label">Mã số thuế <span class="required">*</span></label>
              <input type="text" class="field-input" :class="{ 'input-error': errors.taxCode }"
                v-model="form.taxCode" placeholder="0123456789">
              <span v-if="errors.taxCode" class="error-msg">{{ errors.taxCode }}</span>
            </div>
            <div class="field-group">
              <label class="field-label">SĐT công ty <span class="required">*</span></label>
              <input type="tel" class="field-input" :class="{ 'input-error': errors.companyPhone }"
                v-model="form.companyPhone" placeholder="0281234567">
              <span v-if="errors.companyPhone" class="error-msg">{{ errors.companyPhone }}</span>
            </div>
          </div>

          <div class="field-group">
            <label class="field-label">Email công ty <span class="optional">(tùy chọn)</span></label>
            <input type="email" class="field-input" :class="{ 'input-error': errors.companyEmail }"
              v-model="form.companyEmail" placeholder="info@congty.com">
            <span v-if="errors.companyEmail" class="error-msg">{{ errors.companyEmail }}</span>
          </div>

          <!-- Mật khẩu -->
          <div class="section-label" style="margin-top:8px;">Mật khẩu</div>

          <div class="field-row">
            <div class="field-group">
              <label class="field-label">Mật khẩu <span class="required">*</span></label>
              <div class="input-with-icon">
                <input :type="showPassword ? 'text' : 'password'" class="field-input"
                  :class="{ 'input-error': errors.password }" v-model="form.password"
                  placeholder="••••••••" autocomplete="new-password">
                <button type="button" class="eye-btn" @click="showPassword = !showPassword">
                  <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
              <span v-if="errors.password" class="error-msg">{{ errors.password }}</span>
            </div>
            <div class="field-group">
              <label class="field-label">Nhập lại mật khẩu <span class="required">*</span></label>
              <div class="input-with-icon">
                <input :type="showConfirmPassword ? 'text' : 'password'" class="field-input"
                  :class="{ 'input-error': errors.confirmPassword }" v-model="form.confirmPassword"
                  placeholder="••••••••" autocomplete="new-password">
                <button type="button" class="eye-btn" @click="showConfirmPassword = !showConfirmPassword">
                  <i :class="showConfirmPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
              <span v-if="errors.confirmPassword" class="error-msg">{{ errors.confirmPassword }}</span>
            </div>
          </div>

          <button type="submit" class="btn-primary" :disabled="isLoading">
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
            {{ isLoading ? 'Đang đăng ký...' : 'Đăng ký tài khoản' }}
          </button>

          <div class="footer-links">
            <span class="footer-text">Đã có tài khoản?</span>
            <span class="divider">|</span>
            <router-link to="/login" class="footer-link">Đăng nhập</router-link>
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
  fullName: '', email: '', phone: '',
  taxCode: '', companyPhone: '', companyEmail: '',
  password: '', confirmPassword: ''
});

const isLoading = ref(false);
const errors = ref({});

// --- CÁC BIỂU THỨC CHÍNH QUY (REGEX) ---
// Regex cho Email cá nhân: Chỉ chấp nhận 4 đuôi phổ biến (Giống hệt Backend)
const emailRegex = /^[a-zA-Z0-9._%+-]+@(gmail\.com|yahoo\.com|hotmail\.com|outlook\.com)$/;
// Regex cho SĐT: Bắt đầu bằng số 0, đúng 10 chữ số
const phoneRegex = /^0[0-9]{9}$/;
// Regex cho Email công ty (Vì là đuôi tên miền riêng nên dùng chuẩn email quốc tế cơ bản)
const companyEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const handleRegister = async () => {
  errors.value = {};
  let hasError = false;

  // 1. Validate Họ và Tên
  if (!form.fullName.trim()) { 
    errors.value.fullName = 'Vui lòng nhập họ và tên'; 
    hasError = true; 
  }

  // 2. Validate Email cá nhân (Đồng bộ Backend)
  if (!form.email.trim()) { 
    errors.value.email = 'Vui lòng nhập email'; 
    hasError = true; 
  } else if (!emailRegex.test(form.email.trim())) {
    errors.value.email = 'Email không đúng định dạng'; 
    hasError = true;
  }

  // 3. Validate SĐT cá nhân
  if (!form.phone.trim()) {
    errors.value.phone = 'Vui lòng nhập số điện thoại cá nhân'; 
    hasError = true;
  } else if (!phoneRegex.test(form.phone.trim())) {
    errors.value.phone = 'Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số'; 
    hasError = true;
  }

  // 4. Validate Mã số thuế
  if (!form.taxCode.trim()) { 
    errors.value.taxCode = 'Vui lòng nhập mã số thuế'; 
    hasError = true; 
  }

  // 5. Validate SĐT công ty
  if (!form.companyPhone.trim()) { 
    errors.value.companyPhone = 'Vui lòng nhập số điện thoại công ty'; 
    hasError = true; 
  } else if (!phoneRegex.test(form.companyPhone.trim())) {
    errors.value.companyPhone = 'SĐT công ty phải bắt đầu bằng 0 và có đúng 10 chữ số'; 
    hasError = true;
  }

  // 6. Validate Email công ty (Tùy chọn: Nhập thì mới check)
  if (form.companyEmail && form.companyEmail.trim() !== '') {
    if (!companyEmailRegex.test(form.companyEmail.trim())) {
      errors.value.companyEmail = 'Email công ty không đúng định dạng';
      hasError = true;
    }
  }

  if (!form.password) { errors.value.password = 'Vui lòng nhập mật khẩu'; hasError = true; }

  if (!form.confirmPassword) {
    errors.value.confirmPassword = 'Vui lòng nhập lại mật khẩu'; hasError = true;
  } else if (form.password !== form.confirmPassword) {
    errors.value.confirmPassword = 'Mật khẩu nhập lại không khớp'; hasError = true;
  }

  // NẾU CÓ LỖI -> DỪNG LẠI NGAY LẬP TỨC
  if (hasError) {
    Swal.fire({ 
      icon: 'error', 
      title: 'Dữ liệu không hợp lệ', 
      text: 'Vui lòng kiểm tra lại các thông tin trên form.' 
    });
    return;
  }

  // NẾU PASS HẾT -> GỌI API
  isLoading.value = true;
  try {
    const registerData = {
      email: form.email, password: form.password, fullName: form.fullName,
      phone: form.phone, taxCode: form.taxCode,
      companyPhone: form.companyPhone, companyEmail: form.companyEmail || null
    };
    const response = await authAPI.register(registerData);
    const successMessage = response.data?.message || 'Vui lòng kiểm tra email để kích hoạt tài khoản.';
    Swal.fire({
      icon: 'success', title: 'Đăng ký thành công!',
      text: successMessage, confirmButtonText: 'Đã hiểu'
    }).then(() => { router.push('/login'); });
  } catch (error) {
    if (error.response?.status === 400 && error.response?.data?.details) {
      errors.value = error.response.data.details;
    }
    Swal.fire({
      icon: 'error', title: 'Đăng ký thất bại',
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
  width: 100%; max-width: 600px;
  background: #fff; border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden;
}

.card-header-bar {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 24px 36px; text-align: center;
}
.brand-logo { font-size: 26px; font-weight: 700; color: #fff; letter-spacing: 3px; }
.brand-sub { font-size: 12px; color: #93c5fd; margin-top: 4px; }

.card-body-inner { padding: 28px 36px 32px; }

.section-label {
  font-size: 11px; font-weight: 700; color: #1e3a8a;
  text-transform: uppercase; letter-spacing: 1px;
  border-bottom: 2px solid #e0e7ff;
  padding-bottom: 6px; margin-bottom: 16px;
}

.field-row {
  display: grid; grid-template-columns: 1fr 1fr; gap: 14px;
}

.field-group { margin-bottom: 16px; }

.field-label {
  display: block; font-size: 13px; font-weight: 700;
  color: #1e293b; margin-bottom: 6px; letter-spacing: 0.2px;
}
.required { color: #ef4444; margin-left: 2px; }
.optional { font-weight: 400; color: #94a3b8; font-size: 12px; }

.field-input {
  width: 100%; padding: 10px 13px;
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
.input-with-icon .field-input { padding-right: 40px; }
.eye-btn {
  position: absolute; right: 11px; top: 50%;
  transform: translateY(-50%); background: none; border: none;
  color: #94a3b8; cursor: pointer; font-size: 15px; transition: color 0.2s;
}
.eye-btn:hover { color: #1e3a8a; }
.error-msg { display: block; font-size: 12px; color: #ef4444; margin-top: 4px; }

.btn-primary {
  display: block; width: 100%; padding: 12px;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  color: #fff; font-size: 15px; font-weight: 600;
  border: none; border-radius: 8px; cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
  margin-top: 8px; margin-bottom: 20px;
}
.btn-primary:hover:not(:disabled) { opacity: 0.9; transform: translateY(-1px); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.footer-links {
  display: flex; align-items: center; justify-content: center;
  gap: 6px; font-size: 13px;
}
.footer-text { color: #64748b; }
.footer-link { color: #64748b; text-decoration: none; font-weight: 600; transition: color 0.2s; }
.footer-link:hover { color: #1e3a8a; text-decoration: underline; }
.divider { color: #cbd5e1; }

@media (max-width: 540px) {
  .field-row { grid-template-columns: 1fr; }
  .card-body-inner { padding: 22px 20px 28px; }
}
</style>