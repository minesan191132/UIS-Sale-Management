<template>
  <div class="sidebar-wrapper d-flex flex-column p-3 bg-white shadow-sm h-100 flex-shrink-0" 
       style="width: 280px; min-width: 280px; position: sticky; top: 0; left: 0; border-right: 1px solid #e2e8f0; z-index: 1040; overflow-y: auto;">
    
    <router-link to="/admin/dashboard" class="brand-box d-flex align-items-center mb-4 px-2 text-decoration-none">
      <div class="logo-icon bg-navy text-white rounded-3 d-flex align-items-center justify-content-center me-3 shadow-sm flex-shrink-0" style="width: 40px; height: 40px;">
        <i class="bi bi-shield-check fs-5"></i>
      </div>
      <span class="fs-5 fw-bolder text-navy text-uppercase tracking-wide" style="white-space: nowrap;">Admin Panel</span>
    </router-link>
    
    <ul class="nav flex-column gap-2 mb-auto w-100">
      <li class="nav-item">
        <RouterLink to="/admin/dashboard" class="sidebar-link" style="white-space: nowrap;" active-class="active">
          <i class="bi bi-speedometer2 flex-shrink-0"></i> <span>Thống kê</span>
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/admin/products" class="sidebar-link" style="white-space: nowrap;" active-class="active">
          <i class="bi bi-box-seam flex-shrink-0"></i> <span>Sản phẩm vật tư</span>
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/admin/users" class="sidebar-link" style="white-space: nowrap;" active-class="active">
          <i class="bi bi-people flex-shrink-0"></i> <span>Danh sách User</span>
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/admin/complaints" class="sidebar-link" style="white-space: nowrap;" active-class="active">
          <i class="bi bi-exclamation-triangle flex-shrink-0"></i> <span>Quản lý Khiếu nại</span>
        </RouterLink>
      </li>

      <li class="nav-item mt-2 w-100">
        <div class="sidebar-link d-flex justify-content-between align-items-center cursor-pointer w-100" 
             :class="{ 
               'active-parent': isProcessingActive, 
               'opened-parent': isProcessingDropdownOpen && !isProcessingActive 
             }"
             @click="toggleProcessingDropdown">
          <div class="d-flex align-items-center" style="white-space: nowrap;">
            <i class="bi bi-tools flex-shrink-0" style="margin-right: 12px;"></i> 
            <span>Sản phẩm gia công</span>
          </div>
          <i class="bi transition-transform flex-shrink-0 ms-2" :class="isProcessingDropdownOpen ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
        </div>
        
        <transition name="slide-fade">
          <ul v-if="isProcessingDropdownOpen" class="submenu list-unstyled ms-3 ps-3 border-start border-2 mt-2">
            <li class="mb-1">
              <RouterLink to="/admin/inventory" class="sidebar-sublink" style="white-space: nowrap;" active-class="active-sub">
                <i class="bi bi-layers flex-shrink-0"></i> <span>Tổng kho vật tư</span>
              </RouterLink>
            </li>
            <li class="mb-1">
              <RouterLink to="/admin/invoice-management" class="sidebar-sublink" style="white-space: nowrap;" active-class="active-sub">
                <i class="bi bi-receipt flex-shrink-0"></i> <span>Quản lý xuất hoá đơn</span>
              </RouterLink>
            </li>
            <li class="mb-1">
              <RouterLink to="/admin/product-orders" class="sidebar-sublink" style="white-space: nowrap;" active-class="active-sub">
                <i class="bi bi-cart-check flex-shrink-0"></i> <span>Quản lý đơn hàng</span>
              </RouterLink>
            </li>
            <li class="mb-1">
              <RouterLink to="/admin/invoice-preview" class="sidebar-sublink" style="white-space: nowrap;" active-class="active-sub">
                <i class="bi bi-eye flex-shrink-0"></i> <span>Xem trước hoá đơn</span>
              </RouterLink>
            </li>
          </ul>
        </transition>
      </li>
    </ul>
    
    <div class="user-profile mt-4 pt-3 border-top position-relative w-100">
      <a href="#" class="d-flex align-items-center text-decoration-none p-2 rounded-3 hover-bg w-100" @click.prevent="toggleProfile">
        <div class="avatar-circle bg-navy text-white fw-bold d-flex align-items-center justify-content-center rounded-circle me-3 flex-shrink-0 shadow-sm" style="width: 36px; height: 36px; font-size: 0.9rem;">
          {{ getInitials(user?.fullName || 'Admin') }}
        </div>
        <div class="d-flex flex-column flex-grow-1 overflow-hidden" style="min-width: 0;">
          <strong class="text-dark text-truncate" style="font-size: 0.9rem;">{{ user?.fullName || 'Administrator' }}</strong>
          <small class="text-muted fw-bold" style="font-size: 0.7rem; text-transform: uppercase;">{{ user?.role || 'SYSTEM' }}</small>
        </div>
      </a>

      <transition name="slide-up">
        <ul v-if="isProfileOpen" class="dropdown-menu shadow-lg border-0 rounded-4 p-2 m-0 position-absolute w-100 d-block" style="bottom: 115%; left: 0;">
          <li><a class="dropdown-item py-2 text-danger fw-bold rounded-3" href="#" @click.prevent="handleLogout"><i class="bi bi-box-arrow-right me-2"></i> Đăng xuất</a></li>
        </ul>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getStoredUser, logout } from '../../services/api'

const route = useRoute()
const user = ref(getStoredUser())

const isProcessingDropdownOpen = ref(false)
const isProfileOpen = ref(false)

const processingPaths = ['/admin/inventory', '/admin/invoice-management', '/admin/product-orders', '/admin/invoice-preview']

const isProcessingActive = computed(() => {
  return processingPaths.some(p => route.path.includes(p))
})

const isProcessingMenuActive = computed(() => {
  return processingPaths.some((path) => route.path.startsWith(path))
})

const toggleProcessingDropdown = () => {
  isProcessingDropdownOpen.value = !isProcessingDropdownOpen.value
}

const toggleProfile = (e) => {
  e.stopPropagation()
  isProfileOpen.value = !isProfileOpen.value
}

const closeProfileDropdown = () => {
  isProfileOpen.value = false
}

const handleLogout = () => {
  logout()
}

const getInitials = (name) => {
  if (!name) return 'A';
  const names = name.trim().split(' ');
  if (names.length >= 2) return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  return name.substring(0, 2).toUpperCase();
}

onMounted(() => {
  isProcessingDropdownOpen.value = isProcessingActive.value
  document.addEventListener('click', closeProfileDropdown)
})

onUnmounted(() => {
  document.removeEventListener('click', closeProfileDropdown)
})

watch(() => route.path, () => {
  if (isProcessingActive.value) {
    isProcessingDropdownOpen.value = true 
  } else {
    isProcessingDropdownOpen.value = false 
  }
})

watch(
  () => route.path,
  (path) => {
    if (processingPaths.some((candidate) => path.startsWith(candidate))) {
      isProcessingDropdownOpen.value = true
    }
  },
)
</script>

<style scoped>
/* ─── MÀU SẮC CHUNG ─── */
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.tracking-wide { letter-spacing: 0.5px; }
.cursor-pointer { cursor: pointer; }
.text-nowrap { white-space: nowrap; }

/* ─── CUSTOM SCROLLBAR ─── */
.sidebar-wrapper::-webkit-scrollbar { width: 4px; }
.sidebar-wrapper::-webkit-scrollbar-track { background: transparent; }
.sidebar-wrapper::-webkit-scrollbar-thumb { background-color: #cbd5e1; border-radius: 10px; }

/* ─── HIỆU ỨNG NHẢY MÚA CHO ICON (WIGGLE) ─── */
@keyframes iconWiggle {
  0% { transform: rotate(0deg) scale(1); }
  25% { transform: rotate(-15deg) scale(1.15); }
  50% { transform: rotate(10deg) scale(1.15); }
  75% { transform: rotate(-5deg) scale(1.15); }
  100% { transform: rotate(0deg) scale(1.15); } /* Giữ nguyên độ lớn 1.15 khi đang hover */
}

/* ─── LINK MENU CHÍNH ─── */
.sidebar-link {
  display: flex; align-items: center; padding: 12px 16px; color: #64748b; font-weight: 600;
  text-decoration: none; border-radius: 12px; transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.sidebar-link i { font-size: 1.1rem; margin-right: 12px; transition: all 0.3s ease; }

/* Hover Menu Chính */
.sidebar-link:hover { 
  background-color: #f8fafc; 
  color: #0b2e59; 
  transform: translateX(6px); /* Trượt nhẹ sang phải */
}
.sidebar-link:hover i {
  animation: iconWiggle 0.4s ease-in-out forwards; /* Gọi hiệu ứng Wiggle */
  color: #0b2e59;
}

/* Trạng thái Active (Đang đứng ở trang đó) */
.sidebar-link.active, .sidebar-link.active-parent {
  background-color: #0b2e59; color: #ffffff; 
  box-shadow: 0 6px 15px rgba(11, 46, 89, 0.25);
  transform: translateY(-2px); /* Nhấc nổi hẳn lên */
}
.sidebar-link.active i, .sidebar-link.active-parent i { color: #60a5fa; }

/* Trạng thái mở Menu nhưng đứng trang ngoài */
.sidebar-link.opened-parent {
  background-color: #eff6ff; 
  color: #0b2e59;
  box-shadow: inset 4px 0 0 #0b2e59; 
}
.sidebar-link.opened-parent i { color: #0b2e59; }

/* ─── LINK MENU CON (SUBMENU) ─── */
.sidebar-sublink {
  display: flex; align-items: center; padding: 10px 16px; color: #64748b; font-weight: 500; font-size: 0.9rem;
  text-decoration: none; border-radius: 8px; transition: all 0.2s ease;
}
.sidebar-sublink i { font-size: 1rem; margin-right: 10px; transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275); }

/* Hover Menu Con */
.sidebar-sublink:hover { 
  background-color: #f1f5f9; 
  color: #0b2e59; 
  transform: translateX(8px); /* Trượt xa hơn xíu tạo điểm nhấn */
}
.sidebar-sublink:hover i {
  transform: scale(1.25); /* Icon to lên */
  color: #0b2e59;
}

.sidebar-sublink.active-sub { color: #0b2e59; font-weight: 700; background-color: #eff6ff; }
.sidebar-sublink.active-sub i { color: #0b2e59; }

/* ─── ANIMATION XỔ MENU ─── */
.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.35s ease-in-out; overflow: hidden; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(-15px); max-height: 0; }
.slide-fade-enter-to, .slide-fade-leave-from { opacity: 1; transform: translateY(0); max-height: 400px; }

.slide-up-enter-active, .slide-up-leave-active { transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translateY(15px); }
.slide-up-enter-to, .slide-up-leave-from { opacity: 1; transform: translateY(0); }

/* ─── USER PROFILE (AVATAR POP) ─── */
.hover-bg { transition: all 0.3s ease; }
.avatar-circle { transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }

/* Hover nguyên cái cục User Profile */
.hover-bg:hover { background-color: #f1f5f9; transform: translateY(-2px); }
.hover-bg:hover .avatar-circle {
  transform: scale(1.15) rotate(-10deg); /* Avatar nảy to lên và nghiêng nhẹ */
  box-shadow: 0 6px 12px rgba(11, 46, 89, 0.3) !important;
}

.dropdown-item { transition: all 0.2s; }
.dropdown-item:hover { background-color: #f8fafc; transform: translateX(4px); }
.dropdown-item.text-danger:hover { background-color: #fef2f2; }
</style>