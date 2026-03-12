<template>
  <div class="account-page-root">
    <Navbar />

    <div class="account-bg">
      <div class="account-container fade-in">

        <!-- ── Breadcrumb ── -->
        <nav class="breadcrumb-bar" aria-label="breadcrumb">
          <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item">
              <router-link to="/" class="bc-link">
                <i class="bi bi-house-door me-1"></i>Trang chủ
              </router-link>
            </li>
            <li class="breadcrumb-item active">Tài Khoản</li>
          </ol>
        </nav>

        <div class="account-layout">

          <!-- ──────── SIDEBAR ──────── -->
          <aside class="sidebar slide-up">

            <!-- User info -->
            <div class="user-info">
              <div class="avatar-wrapper">
                <img :src="avatarSrc" alt="Avatar" class="avatar" />
                <span class="avatar-online"></span>
              </div>
              <div class="user-meta">
                <span class="username">{{ currentUser?.fullName || 'Người dùng' }}</span>
                <button class="edit-profile-btn" @click="activeSection = 'profile'; accountOpen = true">
                  <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                  Sửa Hồ Sơ
                </button>
              </div>
            </div>

            <div class="sidebar-divider"></div>

            <!-- Navigation -->
            <nav class="sidebar-nav">

              <!-- Thông Báo -->
              <a href="#" class="nav-item" :class="{ active: activeSection === 'notifications' }"
                @click.prevent="activeSection = 'notifications'">
                <span class="nav-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
                </span>
                <span class="nav-label">Thông Báo</span>
                <span class="notif-dot" title="Có thông báo mới"></span>
              </a>

              <!-- Tài Khoản Của Tôi -->
              <div class="nav-group">
                <div class="nav-item nav-group-header" :class="{ active: isAccountSection }" @click="toggleAccount">
                  <span class="nav-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  </span>
                  <span class="nav-label">Tài Khoản Của Tôi</span>
                  <svg class="chevron" :class="{ open: accountOpen }" xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"/></svg>
                </div>
                <transition name="submenu">
                  <div class="nav-submenu" v-show="accountOpen">
                    <a href="#" class="submenu-item" :class="{ active: activeSection === 'profile' }" @click.prevent="activeSection = 'profile'">Hồ Sơ</a>
                    <a href="#" class="submenu-item" :class="{ active: activeSection === 'address' }" @click.prevent="activeSection = 'address'">Địa Chỉ</a>
                    <a href="#" class="submenu-item" :class="{ active: activeSection === 'password' }" @click.prevent="activeSection = 'password'">Đổi Mật Khẩu</a>
                  </div>
                </transition>
              </div>

              <!-- Đơn Mua -->
              <a href="#" class="nav-item" :class="{ active: activeSection === 'orders' }"
                @click.prevent="activeSection = 'orders'">
                <span class="nav-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 12h6M9 16h4"/></svg>
                </span>
                <span class="nav-label">Lịch Sử Đơn Mua</span>
              </a>

            </nav>
          </aside>

          <!-- ──────── MAIN CONTENT ──────── -->
          <main class="main-content slide-up" style="animation-delay: 0.1s;">

            <!-- Hồ Sơ Section -->
            <section v-if="activeSection === 'profile'" class="content-section">
              <div class="section-header">
                <h1 class="section-title">Hồ Sơ Của Tôi</h1>
                <p class="section-desc">Quản lý thông tin hồ sơ để bảo mật tài khoản</p>
              </div>
              <div class="section-divider"></div>

              <div class="profile-layout">
                <form class="profile-form" @submit.prevent="saveProfile">
                  <div class="form-row">
                    <label class="form-label">Tên đăng nhập</label>
                    <div class="form-input-wrap">
                      <input type="text" class="form-input form-input--readonly" :value="currentUser?.email || ''" readonly />
                    </div>
                  </div>
                  <div class="form-row">
                    <label class="form-label">Họ và tên</label>
                    <div class="form-input-wrap">
                      <input type="text" class="form-input" v-model="form.fullName" placeholder="Nhập tên của bạn" />
                    </div>
                  </div>
                  <div class="form-row">
                    <label class="form-label">Email</label>
                    <div class="form-input-wrap">
                      <input type="email" class="form-input" v-model="form.email" placeholder="Nhập email của bạn" />
                    </div>
                  </div>
                  <div class="form-row">
                    <label class="form-label">Số điện thoại</label>
                    <div class="form-input-wrap">
                      <input type="tel" class="form-input" v-model="form.phone" placeholder="Nhập số điện thoại" />
                    </div>
                  </div>
                  <div class="form-row form-actions">
                    <label class="form-label"></label>
                    <div class="form-input-wrap">
                      <button type="submit" class="btn-save" :disabled="isLoading">
                        {{ isLoading ? 'Đang lưu...' : 'Lưu' }}
                      </button>
                    </div>
                  </div>
                </form>

                <!-- Avatar Panel -->
                <div class="avatar-section">
                  <img :src="avatarSrc" alt="Avatar" class="avatar-large" />
                  <p class="avatar-name">{{ currentUser?.fullName || '' }}</p>
                  <button type="button" class="avatar-upload-btn" @click="$refs.avatarInput.click()">
                    Chọn Ảnh
                  </button>
                  <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
                  <p class="avatar-hint">Dung lượng tối đa 1 MB<br>Định dạng: JPEG, PNG</p>
                </div>
              </div>
            </section>

            <!-- Đổi Mật Khẩu Section -->
            <PasswordSection v-else-if="activeSection === 'password'" />

            <!-- Địa Chỉ Section -->
            <AddressSection v-else-if="activeSection === 'address'" />

            <!-- Thông Báo Section -->
            <NotificationsSection v-else-if="activeSection === 'notifications'" />

            <!-- Lịch Sử Đơn Mua Section -->
            <OrderHistorySection v-else-if="activeSection === 'orders'" />

          </main>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStoredUser, userAPI } from '../../services/api'
import Swal from 'sweetalert2'
import Navbar from '../base/Navbar.vue'
import Footer from '../base/Footer.vue'
import PasswordSection from './password.vue'
import AddressSection from './address.vue'
import NotificationsSection from './notifications.vue'
import OrderHistorySection from './orderhistory.vue'

const activeSection = ref('profile')
const accountOpen = ref(true)
const currentUser = ref(null)
const isLoading = ref(false)

const isAccountSection = computed(() =>
  ['profile', 'address', 'password'].includes(activeSection.value)
)



const toggleAccount = () => {
  accountOpen.value = !accountOpen.value
  if (!isAccountSection.value) activeSection.value = 'profile'
}

const avatarSrc = ref('https://ui-avatars.com/api/?name=User&background=1e3a8a&color=fff&size=150')

const form = ref({
  fullName: '',
  email: '',
  phone: '',
  gender: '',
  dobDay: '',
  dobMonth: '',
  dobYear: '',
})


const onAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) avatarSrc.value = URL.createObjectURL(file)
}

const loadUserProfile = async () => {
  const storedUser = getStoredUser()
  if (storedUser) {
    currentUser.value = storedUser
    form.value.fullName = storedUser.fullName || ''
    form.value.email = storedUser.email || ''
    const name = encodeURIComponent(storedUser.fullName || 'User')
    avatarSrc.value = `https://ui-avatars.com/api/?name=${name}&background=1e3a8a&color=fff&size=150`
  }

  try {
    const profile = await userAPI.getProfile()
    currentUser.value = { ...currentUser.value, ...profile }
    form.value.fullName = profile.fullName || form.value.fullName
    form.value.email = profile.email || form.value.email
    form.value.phone = profile.phone || ''
    form.value.gender = profile.gender || ''
    form.value.dobDay = profile.dobDay || ''
    form.value.dobMonth = profile.dobMonth || ''
    form.value.dobYear = profile.dobYear || ''
  } catch {
    console.warn('Could not load full profile from API, using stored data.')
  }
}

const saveProfile = async () => {
  isLoading.value = true
  try {
    const updated = await userAPI.updateProfile({
      fullName: form.value.fullName,
      email: form.value.email,
      phone: form.value.phone,
      gender: form.value.gender,
      dobDay: form.value.dobDay || null,
      dobMonth: form.value.dobMonth || null,
      dobYear: form.value.dobYear || null,
    })

    // Sync stored user with updated values
    const storedUser = getStoredUser()
    if (storedUser) {
      storedUser.fullName = updated.fullName || form.value.fullName
      storedUser.email = updated.email || form.value.email
      const storage = sessionStorage.getItem('user') ? sessionStorage : localStorage
      storage.setItem('user', JSON.stringify(storedUser))
      currentUser.value = { ...currentUser.value, ...storedUser, ...form.value, email: updated.email || form.value.email }
      const name = encodeURIComponent(form.value.fullName || 'User')
      avatarSrc.value = `https://ui-avatars.com/api/?name=${name}&background=1e3a8a&color=fff&size=150`
    }

    Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Hồ sơ đã được cập nhật.', timer: 1500, showConfirmButton: false })
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: err.response?.data?.error || 'Không thể cập nhật hồ sơ.' })
  } finally {
    isLoading.value = false
  }
}

onMounted(() => loadUserProfile())
</script>


<style scoped>
/* ── Page Shell ── */
.account-page-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: 'Roboto', -apple-system, BlinkMacSystemFont, sans-serif;
}

.account-bg {
  flex: 1;
  background: #f1f5f9;
  padding: 24px 16px 48px;
}

.account-container {
  max-width: 1160px;
  margin: 0 auto;
}

/* ── Breadcrumb ── */
.breadcrumb-bar {
  margin-bottom: 16px;
}

.breadcrumb {
  background: none;
  padding: 0;
  font-size: 13px;
}

.bc-link {
  color: #2563eb;
  text-decoration: none;
  transition: color 0.2s;
}

.bc-link:hover {
  color: #1d4ed8;
}

/* ── Layout ── */
.account-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

/* ── SIDEBAR ── */
.sidebar {
  width: 236px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.07);
  padding-bottom: 20px;
  position: sticky;
  top: 80px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px 16px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #2563eb;
}

.avatar-online {
  position: absolute;
  bottom: 1px;
  right: 1px;
  width: 10px;
  height: 10px;
  background: #22c55e;
  border-radius: 50%;
  border: 2px solid #fff;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 0;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.edit-profile-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  color: #64748b;
  padding: 0;
  transition: color 0.2s;
}

.edit-profile-btn:hover {
  color: #2563eb;
}

.sidebar-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0;
}

/* Nav */
.sidebar-nav {
  padding: 6px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  cursor: pointer;
  text-decoration: none;
  color: #334155;
  font-size: 13.5px;
  transition: background 0.15s, color 0.15s;
  user-select: none;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: #eff6ff;
  color: #2563eb;
}

.nav-item.active {
  background: #eff6ff;
  color: #2563eb;
  border-left-color: #2563eb;
}

.nav-item.active .nav-label {
  font-weight: 600;
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: inherit;
}

.chevron {
  margin-left: auto;
  transition: transform 0.25s;
  flex-shrink: 0;
}

.chevron.open {
  transform: rotate(180deg);
}

/* Notification indicator dot */
.notif-dot {
  margin-left: auto;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ef4444;
  border: 1.5px solid #fff;
  flex-shrink: 0;
}

/* Submenu */
.nav-submenu {
  padding: 2px 0 4px 46px;
}

.submenu-item {
  display: block;
  padding: 6px 8px;
  font-size: 13px;
  color: #64748b;
  text-decoration: none;
  border-radius: 4px;
  transition: color 0.15s, background 0.15s;
}

.submenu-item:hover {
  color: #2563eb;
  background: #f8fafc;
}

.submenu-item.active {
  color: #2563eb;
  font-weight: 600;
}

/* Submenu transition */
.submenu-enter-active,
.submenu-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.submenu-enter-from,
.submenu-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ── MAIN CONTENT ── */
.main-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.07);
  overflow: hidden;
  min-height: 480px;
}

.content-section {
  padding: 28px 32px;
}

.section-header {
  margin-bottom: 10px;
}

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
  margin: 0 0 0;
}

/* Profile Layout */
.profile-layout {
  display: flex;
  gap: 48px;
  padding-top: 24px;
}

.profile-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 440px;
}

/* Form rows */
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
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #fff;
}

.form-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-input--readonly {
  background: #f8fafc;
  color: #94a3b8;
  cursor: not-allowed;
}

.form-value {
  font-size: 14px;
  color: #334155;
}


/* Btn save */
.form-actions {
  align-items: center;
}

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

.btn-save:active:not(:disabled) {
  transform: scale(0.97);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Avatar section */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding-top: 8px;
  border-left: 1px solid #f1f5f9;
  padding-left: 48px;
}

.avatar-large {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e2e8f0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: border-color 0.2s;
}

.avatar-large:hover {
  border-color: #2563eb;
}

.avatar-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.avatar-upload-btn {
  padding: 7px 20px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
  background: #fff;
  transition: border-color 0.2s, color 0.2s, background 0.2s;
}

.avatar-upload-btn:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

.avatar-hint {
  font-size: 11.5px;
  color: #94a3b8;
  text-align: center;
  line-height: 1.6;
  margin: 0;
}

/* Placeholder */
.placeholder-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 16px;
}

.placeholder-title {
  font-size: 18px;
  color: #475569;
  margin: 0;
  font-weight: 500;
}

.placeholder-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

/* ── Animations ── */
.fade-in {
  animation: fadeIn 0.4s ease-out forwards;
}

.slide-up {
  opacity: 0;
  transform: translateY(18px);
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to   { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(18px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ── Responsive ── */
@media (max-width: 900px) {
  .account-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    position: static;
  }

  .profile-layout {
    flex-direction: column;
  }

  .avatar-section {
    border-left: none;
    border-top: 1px solid #f1f5f9;
    padding-left: 0;
    padding-top: 24px;
  }
}

@media (max-width: 576px) {
  .content-section {
    padding: 20px 16px;
  }

  .form-label {
    width: 110px;
    font-size: 12.5px;
  }
}
</style>
