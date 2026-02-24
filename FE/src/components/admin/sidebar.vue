<template>
  <div class="sidebar d-flex flex-column flex-shrink-0 p-3 bg-light" style="width: 280px; height: 100vh; border-right: 1px solid #dee2e6;">
    
    <a href="/" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-decoration-none">
      <span class="fs-4 fw-bold text-primary">Admin Shop</span>
    </a>
    <hr>
    
    <ul class="nav nav-pills flex-column mb-auto">
      <li class="nav-item" v-if="isAdmin">
        <RouterLink to="/admin/dashboard" class="nav-link link-dark" active-class="active bg-primary text-white">
          <i class="bi bi-speedometer2 me-2"></i> Thống kê
        </RouterLink>
      </li>
      <li v-if="isAdmin">
        <RouterLink to="/admin/products" class="nav-link link-dark" active-class="active bg-primary text-white">
          <i class="bi bi-box-seam me-2"></i> Sản phẩm
        </RouterLink>
      </li>
      <li v-if="isAdmin">
        <RouterLink to="/admin/users" class="nav-link link-dark" active-class="active bg-primary text-white">
          <i class="bi bi-cart me-2"></i> Danh sách user
        </RouterLink>
      </li>
      <li v-if="isAdmin">
        <RouterLink to="/admin/orders" class="nav-link link-dark" active-class="active bg-primary text-white">
          <i class="bi bi-clipboard-check me-2"></i> Quản lý đơn hàng
        </RouterLink>
      </li>
      <li>
        <a @click="toggleProcessingDropdown" class="nav-link link-dark" style="cursor: pointer;">
          <i class="bi bi-cart me-2"></i> Sản phẩm gia công
          <i :class="isProcessingDropdownOpen ? 'bi bi-chevron-up' : 'bi bi-chevron-down'" class="float-end"></i>
        </a>
        <ul v-show="isProcessingDropdownOpen" class="submenu list-unstyled ps-4">
          <li class="my-1">
            <RouterLink to="/admin/inventory" class="nav-link link-dark py-2" active-class="active bg-primary text-white">
              <i class="bi bi-box me-2"></i> Tổng kho vật tư
            </RouterLink>
          </li>
          <li class="my-1">
            <RouterLink to="/admin/invoice-management" class="nav-link link-dark py-2" active-class="active bg-primary text-white">
              <i class="bi bi-file-earmark-text me-2"></i> Quản lý xuất hoá đơn
            </RouterLink>
          </li>
          <li class="my-1">
            <RouterLink to="/admin/invoice-preview" class="nav-link link-dark py-2" active-class="active bg-primary text-white">
              <i class="bi bi-eye me-2"></i> Xem trước hoá đơn
            </RouterLink>
          </li>
        </ul>
      </li>
    </ul>
    <hr>
    
    <div class="dropdown">
      <a href="#" class="d-flex align-items-center link-dark text-decoration-none dropdown-toggle" id="dropdownUser2" data-bs-toggle="dropdown" aria-expanded="false">
        <img src="https://github.com/mdo.png" alt="" width="32" height="32" class="rounded-circle me-2">
        <div class="d-flex flex-column">
          <strong>{{ user?.fullName || 'User' }}</strong>
          <small class="text-muted" style="font-size: 0.75rem;">{{ user?.role || 'Member' }}</small>
        </div>
      </a>
      <ul class="dropdown-menu text-small shadow" aria-labelledby="dropdownUser2">
        <li><a class="dropdown-item" href="#">Thông tin cá nhân</a></li>
        <li><hr class="dropdown-divider"></li>
        <li><a class="dropdown-item text-danger" href="#" @click.prevent="handleLogout">Đăng xuất</a></li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getStoredUser, logout } from '../../services/api'

const isProcessingDropdownOpen = ref(false)
const user = ref(null)

const isAdmin = computed(() => {
  const role = user.value?.role;
  return role && role.toUpperCase() === 'ADMIN';
})

const toggleProcessingDropdown = () => {
  isProcessingDropdownOpen.value = !isProcessingDropdownOpen.value
}

const handleLogout = () => {
  logout()
}

onMounted(() => {
  user.value = getStoredUser()
})
</script>

<style scoped>
.sidebar {
  position: sticky;
  top: 0;
  left: 0;
}

.nav-link {
  border-radius: 0.375rem;
  margin-bottom: 0.25rem;
  transition: all 0.2s ease;
}

.nav-link:hover {
  background-color: #e9ecef !important;
}

.submenu .nav-link {
  font-size: 0.9rem;
  padding-left: 2.5rem;
}
</style>