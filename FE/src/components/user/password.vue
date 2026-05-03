<template>
  <section class="content-section">
    <div class="section-header">
      <h1 class="section-title">Đổi Mật Khẩu</h1>
      <p class="section-desc">Để bảo mật tài khoản, vui lòng không chia sẻ mật khẩu cho người khác</p>
    </div>
    <div class="section-divider"></div>

    <div class="pw-layout">
      <form class="pw-form" @submit.prevent="handleChangePassword">

        <div class="form-row">
          <label class="form-label">Mật khẩu hiện tại</label>
          <div class="form-input-wrap">
            <div class="pw-input-group">
              <input
                :type="showCurrentPw ? 'text' : 'password'"
                class="form-input"
                :class="{ 'is-invalid': errors.currentPassword }"
                v-model="passwordForm.currentPassword"
                placeholder="Nhập mật khẩu hiện tại"
              />
              <button type="button" class="pw-toggle" @click="showCurrentPw = !showCurrentPw">
                <svg v-if="!showCurrentPw" xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <div v-if="errors.currentPassword" class="error-message">{{ errors.currentPassword }}</div>
          </div>
        </div>

        <div class="form-row">
          <label class="form-label">Mật khẩu mới</label>
          <div class="form-input-wrap">
            <div class="pw-input-group">
              <input
                :type="showNewPw ? 'text' : 'password'"
                class="form-input"
                :class="{ 'is-invalid': errors.newPassword }"
                v-model="passwordForm.newPassword"
                placeholder="Tối thiểu 6 ký tự"
              />
              <button type="button" class="pw-toggle" @click="showNewPw = !showNewPw">
                <svg v-if="!showNewPw" xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <div v-if="errors.newPassword" class="error-message">{{ errors.newPassword }}</div>
          </div>
        </div>

        <div class="form-row">
          <label class="form-label">Xác nhận mật khẩu</label>
          <div class="form-input-wrap">
            <div class="pw-input-group">
              <input
                :type="showConfirmPw ? 'text' : 'password'"
                class="form-input"
                :class="{ 'is-invalid': errors.confirmPassword }"
                v-model="passwordForm.confirmPassword"
                placeholder="Nhập lại mật khẩu mới"
              />
              <button type="button" class="pw-toggle" @click="showConfirmPw = !showConfirmPw">
                <svg v-if="!showConfirmPw" xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <div v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</div>
          </div>
        </div>

        <div class="form-row form-actions">
          <label class="form-label"></label>
          <div class="form-input-wrap">
            <button type="submit" class="btn-save" :disabled="isChangingPw">
              {{ isChangingPw ? 'Đang xử lý...' : 'Xác nhận' }}
            </button>
          </div>
        </div>

      </form>

      <!-- Security tips panel – mirrors avatar-section from accountsetting -->
      <div class="pw-tips">
        <div class="pw-shield">
          <svg xmlns="http://www.w3.org/2000/svg" width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            <polyline points="9 12 11 14 15 10"/>
          </svg>
        </div>
        <p class="tips-title">Mật khẩu mạnh nên có:</p>
        <ul class="tips-list">
          <li>Ít nhất 6 ký tự</li>
          <li>Ít nhất 1 chữ cái viết hoa (A-Z)</li>
          <li>Ít nhất 1 ký tự đặc biệt (!@#$%^&*...)</li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { userAPI } from '../../services/api'
import Swal from 'sweetalert2'

const passwordForm = ref({ currentPassword: '', newPassword: '', confirmPassword: '' })
const showCurrentPw = ref(false)
const showNewPw = ref(false)
const showConfirmPw = ref(false)
const isChangingPw = ref(false)
const errors = ref({})

const validatePasswords = () => {
  errors.value = {}
  
  if (!passwordForm.value.currentPassword || passwordForm.value.currentPassword.trim() === '') {
    errors.value.currentPassword = 'Mật khẩu hiện tại không được để trống'
  }
  
  if (!passwordForm.value.newPassword || passwordForm.value.newPassword.trim() === '') {
    errors.value.newPassword = 'Mật khẩu mới không được để trống'
  } else if (passwordForm.value.newPassword.length < 6) {
    errors.value.newPassword = 'Mật khẩu mới phải có ít nhất 6 ký tự'
  } else if (!/[A-Z]/.test(passwordForm.value.newPassword)) {
    errors.value.newPassword = 'Mật khẩu mới phải có ít nhất 1 chữ cái viết hoa'
  } else if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(passwordForm.value.newPassword)) {
    errors.value.newPassword = 'Mật khẩu mới phải có ít nhất 1 ký tự đặc biệt (!@#$%^&*...)'
  }
  
  if (!passwordForm.value.confirmPassword || passwordForm.value.confirmPassword.trim() === '') {
    errors.value.confirmPassword = 'Vui lòng xác nhận mật khẩu'
  } else if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    errors.value.confirmPassword = 'Mật khẩu xác nhận không khớp với mật khẩu mới'
  }
  
  return Object.keys(errors.value).length === 0
}

const handleChangePassword = async () => {
  if (!validatePasswords()) {
    return
  }

  isChangingPw.value = true
  try {
    await userAPI.changePassword({
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword,
    })
    Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Mật khẩu đã được đổi thành công.', timer: 1500, showConfirmButton: false })
    passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
    showCurrentPw.value = showNewPw.value = showConfirmPw.value = false
    errors.value = {}
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data?.error
    if (errorMsg && errorMsg.toLowerCase().includes('current') && errorMsg.toLowerCase().includes('password')) {
      errors.value.currentPassword = 'Mật khẩu hiện tại không chính xác'
    } else {
      Swal.fire({ icon: 'error', title: 'Lỗi', text: errorMsg || 'Không thể đổi mật khẩu. Vui lòng thử lại.' })
    }
  } finally {
    isChangingPw.value = false
  }
}
</script>


<style scoped>
/* Mirrors accountsetting.vue design tokens */
.content-section { padding: 28px 32px; }

.section-header { margin-bottom: 10px; }

.section-title {
  font-size: 19px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px;
}

.section-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.section-divider {
  height: 1px;
  background: #f1f5f9;
}

.pw-layout {
  display: flex;
  gap: 48px;
  padding-top: 24px;
}

.pw-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 440px;
}

.form-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.form-label {
  width: 150px;
  flex-shrink: 0;
  text-align: right;
  font-size: 13.5px;
  color: #475569;
  padding-top: 8px;
}

.form-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  color: #1e293b;
  background: #fff;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.pw-input-group {
  position: relative;
  width: 100%;
}

.pw-input-group .form-input {
  padding-right: 40px;
}

.pw-toggle {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.pw-toggle:hover { color: #2563eb; }

.form-actions { align-items: center; }

.btn-save {
  padding: 9px 36px;
  background: linear-gradient(135deg, #1e3a8a, #2563eb);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.btn-save:hover:not(:disabled) {
  background: linear-gradient(135deg, #1e40af, #1d4ed8);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
}

.btn-save:active:not(:disabled) { transform: scale(0.97); }

.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }

.form-input.is-invalid {
  border-color: #ef4444;
  background-color: #fef2f2;
}

.form-input.is-invalid:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.error-message {
  color: #dc2626;
  font-size: 13px;
  margin-top: 4px;
  display: block;
  width: 100%;
}

/* Tips panel – mirrors avatar-section */
.pw-tips {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding-top: 8px;
  border-left: 1px solid #f1f5f9;
  padding-left: 40px;
  min-width: 180px;
}

.pw-shield {
  width: 80px;
  height: 80px;
  background: #eff6ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tips-title {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin: 0;
  text-align: center;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.tips-list li {
  font-size: 12.5px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 7px;
}

.tips-list li::before {
  content: '✓';
  color: #2563eb;
  font-weight: 700;
  font-size: 11px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .pw-layout { flex-direction: column; }

  .pw-tips {
    border-left: none;
    border-top: 1px solid #f1f5f9;
    padding-left: 0;
    padding-top: 20px;
    align-items: flex-start;
  }

  .form-label { width: 110px; font-size: 12.5px; }
}
</style>
