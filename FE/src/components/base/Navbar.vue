<template>
  <nav class="navbar navbar-expand-lg navbar-dark modern-navbar sticky-top">
    <div class="container nav-shell">
      <router-link class="navbar-brand brand-link d-flex align-items-center" to="/">
        <span class="brand-logo-wrap" aria-hidden="true">
          <img src="/images/Logo.png" alt="UIS Logo" class="brand-logo-img" />
        </span>
        <span class="brand-text">UIS STORE</span>
      </router-link>

      <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarContent">
        <ul class="navbar-nav nav-main mx-lg-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/">Trang chủ</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/services">Dịch vụ</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/products">Sản phẩm phôi</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/contact">Liên hệ</router-link>
          </li>
        </ul>

        <div class="d-flex align-items-center gap-2 actions-menu">
          <router-link
            v-if="showManufacturingCta"
            to="/create-order"
            class="btn header-cta rounded-pill px-3 py-2 fw-bold text-uppercase"
          >
            Đặt gia công
          </router-link>

          <router-link
            v-if="user && user.role === 'CUSTOMER'"
            to="/my-orders"
            class="header-chip orders-chip position-relative"
          >
            <i class="bi bi-box-seam"></i>
            <span class="orders-badge">
              {{ myOrdersTotalCount > 99 ? '99+' : myOrdersTotalCount }}
            </span>
          </router-link>

          <span class="actions-divider d-none d-lg-inline-flex"></span>

          <router-link to="/cart" class="cart-btn position-relative" aria-label="Giỏ hàng">
            <i class="bi bi-cart3"></i>
            <span v-if="cartItemCount > 0" class="cart-badge">
              {{ cartItemCount > 99 ? '99+' : cartItemCount }}
            </span>
          </router-link>

          <div v-if="!user" class="auth-buttons d-flex align-items-center gap-2">
            <router-link to="/login" class="btn auth-link">Đăng nhập</router-link>
            <router-link to="/register" class="btn auth-register rounded-pill">Đăng ký</router-link>
          </div>

          <div v-else class="dropdown user-dropdown">
            <button
              class="btn user-trigger border-0 d-flex align-items-center gap-2"
              type="button"
              id="userDropdown"
              @click="toggleDropdown"
              aria-expanded="false"
            >
              <span class="user-name d-none d-md-inline">{{ user.fullName }}</span>
              <div class="avatar-wrap">
                <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(user.fullName || 'User')}&background=1e3a8a&color=fff&size=150`" alt="Avatar" width="32" height="32" class="rounded-circle" />
              </div>
            </button>

            <ul class="dropdown-menu dropdown-menu-end user-menu shadow border-0" aria-labelledby="userDropdown">
              <li class="dropdown-header-wrap">
                <p class="menu-caption mb-1">Tài khoản của bạn</p>
                <p class="menu-subcaption mb-0">Quản lý thông tin tài khoản</p>
              </li>

              <li>
                <router-link to="/account" class="dropdown-item">
                  <i class="bi bi-person-circle me-2"></i>Tài khoản
                </router-link>
              </li>
              <li v-if="user.role === 'ADMIN'">
                <router-link to="/admin/dashboard" class="dropdown-item">
                  <i class="bi bi-speedometer2 me-2"></i>Quản trị
                </router-link>
              </li>

              <li><hr class="dropdown-divider" /></li>
              <li>
                <a class="dropdown-item text-danger" href="#" @click.prevent="handleLogout">
                  <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import apiClient, { getStoredUser, logout, isAuthenticated } from '../../services/api';
import { Dropdown } from 'bootstrap';
import { cartItemCount } from '../../store/cart.js';

const user = ref(isAuthenticated() ? getStoredUser() : null);
const myOrdersTotalCount = ref(0);
const showManufacturingCta = computed(() => !user.value || user.value.role === 'CUSTOMER');
let dropdownInstance = null;

const extractTotalCount = (data) => {
  const directTotal = Number(data?.totalElements);
  if (Number.isFinite(directTotal) && directTotal >= 0) return directTotal;

  const nestedTotal = Number(data?.page?.totalElements);
  if (Number.isFinite(nestedTotal) && nestedTotal >= 0) return nestedTotal;

  const numberOfElements = Number(data?.numberOfElements);
  if (Number.isFinite(numberOfElements) && numberOfElements >= 0) return numberOfElements;

  if (Array.isArray(data?.content)) return data.content.length;
  if (Array.isArray(data)) return data.length;

  return 0;
};

const loadMyOrdersTotalCount = async () => {
  if (!user.value || user.value.role !== 'CUSTOMER') {
    myOrdersTotalCount.value = 0;
    return;
  }

  try {
    const [customResult, readyResult, pendingImportResult, rejectedImportResult] = await Promise.allSettled([
      apiClient.get('/orders/my', {
        params: { page: 0, size: 1, orderType: 'CUSTOM_MANUFACTURING' },
      }),
      apiClient.get('/orders/my', {
        params: { page: 0, size: 1, orderType: 'READY_MADE' },
      }),
      apiClient.get('/orders/imports/my'),
      apiClient.get('/orders/imports/my', {
        params: { status: 'REJECTED' },
      }),
    ]);

    let total = 0;

    if (customResult.status === 'fulfilled') {
      total += extractTotalCount(customResult.value?.data);
    }
    if (readyResult.status === 'fulfilled') {
      total += extractTotalCount(readyResult.value?.data);
    }
    if (pendingImportResult.status === 'fulfilled') {
      const pendingImports = Array.isArray(pendingImportResult.value?.data) ? pendingImportResult.value.data : [];
      total += pendingImports.length;
    }
    if (rejectedImportResult.status === 'fulfilled') {
      const rejectedImports = Array.isArray(rejectedImportResult.value?.data) ? rejectedImportResult.value.data : [];
      total += rejectedImports.length;
    }

    myOrdersTotalCount.value = Math.max(0, total);
  } catch (e) {
    myOrdersTotalCount.value = 0;
  }
};

const toggleDropdown = () => {
  const el = document.getElementById('userDropdown');
  if (!dropdownInstance && el) {
    dropdownInstance = new Dropdown(el);
  }
  dropdownInstance?.toggle();
};

const handleLogout = () => {
  logout();
};

onMounted(() => {
  loadMyOrdersTotalCount();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Manrope:wght@600;700;800&display=swap');

.modern-navbar {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
  box-shadow: 0 12px 30px -16px rgba(2, 6, 23, 0.62);
  padding: 0.64rem 0;
  font-family: 'Manrope', 'Inter', sans-serif;
}

.nav-shell {
  min-height: 68px;
}

.brand-link {
  gap: 10px;
}

.navbar-brand {
  margin-right: 0;
}

.brand-logo-wrap {
  width: 1.58rem;
  height: 1.58rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 999px;
  flex-shrink: 0;
}

.brand-logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center;
  transform: scale(2.7);
  transform-origin: center;
}

.brand-text {
  font-size: 1.62rem;
  line-height: 1;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #ffffff;
}

.nav-main {
  gap: 6px;
}

.custom-link {
  position: relative;
  color: #b8c4dc !important;
  font-size: 0.92rem;
  font-weight: 600;
  padding: 0.48rem 0.62rem !important;
  transition: color 0.3s ease;
}

.custom-link::before {
  content: '';
  position: absolute;
  left: 0.5rem;
  right: 0.5rem;
  bottom: 0.12rem;
  height: 13px;
  border-radius: 999px;
  background: radial-gradient(ellipse at center, rgba(255, 183, 125, 0.22) 0%, rgba(255, 183, 125, 0) 78%);
  opacity: 0;
  transform: translateY(4px);
  transition: opacity 0.35s ease, transform 0.35s ease;
}

.custom-link::after {
  content: '';
  position: absolute;
  left: 0.65rem;
  right: 0.65rem;
  bottom: 0.12rem;
  height: 2px;
  border-radius: 99px;
  opacity: 0;
  transform: scaleX(0.4);
  transform-origin: center;
  background: linear-gradient(90deg, #ffb77d 0%, #fd8b00 100%);
  transition: transform 0.34s cubic-bezier(0.22, 1, 0.36, 1), opacity 0.34s ease;
}

.custom-link:hover,
.custom-link.router-link-active,
.custom-link.router-link-exact-active {
  color: #ffe4c8 !important;
}

.custom-link:hover::before,
.custom-link.router-link-active::before,
.custom-link.router-link-exact-active::before {
  opacity: 1;
  transform: translateY(0);
}

.custom-link:hover::after,
.custom-link.router-link-active::after,
.custom-link.router-link-exact-active::after {
  opacity: 1;
  transform: scaleX(1);
}

.actions-menu {
  margin-left: auto;
  gap: 10px !important;
}

.header-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  color: #4d2600;
  font-size: 0.74rem;
  letter-spacing: 0.08em;
  padding-inline: 1.35rem;
  background: linear-gradient(135deg, #ffb77d 0%, #fd8b00 100%);
  box-shadow: 0 10px 24px rgba(249, 115, 22, 0.32);
  transition: transform 0.28s cubic-bezier(0.22, 1, 0.36, 1), box-shadow 0.28s ease;
}

.header-cta:hover {
  color: #4d2600;
  transform: translateY(-1px);
  box-shadow: 0 14px 26px rgba(249, 115, 22, 0.42);
}

.header-chip {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  border-radius: 10px;
  padding: 0.48rem 0.72rem;
  font-size: 0.8rem;
  font-weight: 700;
  color: #95a2ba;
  text-decoration: none;
  background: transparent;
  transition: color 0.26s ease, background-color 0.26s ease, transform 0.26s ease;
}

.header-chip:hover,
.header-chip.router-link-active,
.header-chip.router-link-exact-active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-1px);
}

.orders-badge {
  position: absolute;
  top: -6px;
  right: -7px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #ef4444;
  color: #fff;
  font-size: 0.62rem;
  font-weight: 700;
  border: 2px solid #0b1324;
}

.actions-divider {
  width: 1px;
  height: 22px;
  background: rgba(255, 255, 255, 0.14);
  margin-inline: 0;
}

.cart-btn {
  width: 34px;
  height: 34px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #95a2ba;
  text-decoration: none;
  transition: all 0.26s cubic-bezier(0.22, 1, 0.36, 1);
}

.cart-btn:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.06);
  transform: translateY(-1px);
}

.cart-badge {
  position: absolute;
  top: -2px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  background-color: #ef4444;
  color: #fff;
  font-size: 0.62rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #0f172a;
}

.auth-link {
  color: #dbe2fa;
  border: 1px solid transparent;
  font-weight: 600;
  font-size: 0.85rem;
}

.auth-link:hover {
  color: #ffffff;
  border-color: rgba(255, 255, 255, 0.14);
}

.auth-register {
  border: 1px solid rgba(255, 255, 255, 0.16);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
  font-weight: 700;
  font-size: 0.82rem;
  padding-inline: 1rem;
}

.auth-register:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.user-trigger {
  border-radius: 999px;
  padding: 4px 6px 4px 11px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.14) !important;
  transition: border-color 0.28s ease, background-color 0.28s ease, transform 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.user-trigger:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 183, 125, 0.55) !important;
  transform: translateY(-1px);
}

.user-name {
  color: #ffffff;
  font-size: 0.79rem;
  font-weight: 700;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-wrap {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  overflow: hidden;
  background: #1f2a43;
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.avatar-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-menu {
  width: 260px;
  margin-top: 12px;
  border-radius: 14px;
  padding: 0.4rem;
  background: rgba(19, 27, 45, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  display: block;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px) scale(0.98);
  transform-origin: top right;
  pointer-events: none;
  transition: opacity 0.24s ease, transform 0.24s cubic-bezier(0.22, 1, 0.36, 1), visibility 0.24s ease;
}

.user-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0) scale(1);
  pointer-events: auto;
}

.dropdown-header-wrap {
  padding: 0.65rem 0.72rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.09);
  margin-bottom: 0.25rem;
}

.menu-caption {
  font-size: 0.67rem;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  font-weight: 800;
  color: #ffb77d;
}

.menu-subcaption {
  color: #93a3bf;
  font-size: 0.76rem;
  font-weight: 600;
}

.user-menu .dropdown-item {
  border-radius: 10px;
  color: #dbe2fa;
  font-size: 0.84rem;
  font-weight: 600;
  padding: 0.56rem 0.72rem;
  transition: background-color 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.user-menu .dropdown-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
  transform: translateX(2px);
}

@media (min-width: 992px) {
  .nav-main {
    margin-left: 24px;
  }

  .nav-main .nav-item {
    margin-inline: 1px;
  }

  .actions-menu > * {
    margin: 0;
  }
}

.user-menu .dropdown-divider {
  border-color: rgba(255, 255, 255, 0.1);
  margin: 0.3rem 0;
}

@media (max-width: 991.98px) {
  .navbar-collapse {
    margin-top: 0.9rem;
    padding: 1rem;
    border-radius: 14px;
    border: 1px solid rgba(255, 255, 255, 0.12);
    background: rgba(10, 18, 35, 0.95);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
  }

  .nav-main {
    margin-bottom: 0.8rem !important;
  }

  .custom-link {
    padding-inline: 0.35rem !important;
  }

  .brand-logo-img {
    transform: scale(2.5);
  }

  .actions-menu {
    width: 100%;
    flex-direction: column;
    align-items: stretch !important;
    gap: 0.6rem !important;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
    padding-top: 0.9rem;
  }

  .header-cta,
  .header-chip,
  .auth-link,
  .auth-register,
  .user-trigger {
    width: 100%;
    justify-content: center;
  }

  .auth-buttons {
    width: 100%;
    display: grid !important;
    grid-template-columns: 1fr;
    gap: 0.5rem !important;
  }

  .cart-btn {
    width: 100%;
    border-radius: 10px;
    justify-content: center;
    background: rgba(255, 255, 255, 0.05);
  }

  .user-dropdown {
    width: 100%;
  }
}
</style>