<template>
  <nav class="navbar navbar-expand-lg navbar-dark modern-navbar sticky-top">
    <div class="container">
      <router-link class="navbar-brand d-flex align-items-center gap-2" to="/">
        <span class="brand-text">UIS <span class="fw-light">STORE</span></span>
      </router-link>

      <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarContent">
        <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/">Trang chủ</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/services">Dịch vụ</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/products">Sản phẩm</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link custom-link" to="/contact">Liên hệ</router-link>
          </li>
        </ul>

          <div class="d-flex align-items-center gap-3 actions-menu">
            <router-link to="/cart" class="cart-btn position-relative me">
              <i class="fas fa-shopping-bag"></i>
              <span v-if="cartCount > 0" class="cart-badge">
                {{ cartCount }}
              </span>
            </router-link>

            <!-- Auth Buttons -->
            <div v-if="!user" class="auth-buttons d-flex align-items-center gap-2">
              <router-link to="/login" class="btn btn-link text-white text-decoration-none fw-500">
                Đăng nhập
              </router-link>
              
              <router-link to="/register" class="btn btn-primary btn-glow px-4 rounded-pill">
                Đăng ký
              </router-link>
            </div>

            <!-- User Dropdown (Logged In) -->
            <div v-else class="dropdown">
              <button class="btn btn-outline-light border-0 dropdown-toggle d-flex align-items-center gap-2" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <img src="https://github.com/mdo.png" alt="Avatar" width="32" height="32" class="rounded-circle border border-2 border-white">
                <div class="d-none d-md-flex flex-column text-start lh-1">
                  <span class="fw-bold small">{{ user.fullName }}</span>
                  <span class="opacity-75" style="font-size: 0.7rem;">{{ user.role }}</span>
                </div>
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow border-0" aria-labelledby="userDropdown">
                <!-- Admin Menu -->
                <li v-if="user.role === 'ADMIN'">
                  <router-link to="/admin/dashboard" class="dropdown-item">
                    <i class="bi bi-speedometer2 me-2"></i>Quản trị
                  </router-link>
                </li>
                
                <!-- Customer Menu -->
                <li v-if="user.role === 'CUSTOMER'">
                  <router-link to="/create-order" class="dropdown-item">
                    <i class="bi bi-plus-circle me-2"></i>Đặt gia công
                  </router-link>
                </li>
                <li v-if="user.role === 'CUSTOMER'">
                  <router-link to="/my-orders" class="dropdown-item">
                    <i class="bi bi-list-check me-2"></i>Đơn hàng của tôi
                  </router-link>
                </li>

                <li><hr class="dropdown-divider"></li>
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
  import { ref, onMounted } from 'vue';
  import { getStoredUser, logout, isAuthenticated } from '../../services/api';
  
  const cartCount = ref(3);
  const user = ref(null);

  const handleLogout = () => {
    logout();
  };

  onMounted(() => {
    if (isAuthenticated()) {
      user.value = getStoredUser();
    }
  });
  </script>

<style scoped>
.modern-navbar {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 1rem 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease-in-out;
}

.brand-text {
  font-weight: 700;
  letter-spacing: 0.5px;
  font-size: 1.3rem;
}

.custom-link {
  font-size: 0.95rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.75) !important;
  margin: 0 10px;
  position: relative;
  transition: color 0.3s;
}

.custom-link:hover, .router-link-active {
  color: #fff !important;
}

.custom-link::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: 0;
  left: 50%;
  background-color: #38bdf8;
  transition: all 0.3s ease-in-out;
  transform: translateX(-50%);
}

.custom-link:hover::after, .router-link-active::after {
  width: 100%;
}

.cart-btn {
  color: white;
  font-size: 1.2rem;
  padding: 8px;
  transition: transform 0.2s;
  display: flex;
  align-items: center;
}

.cart-btn:hover {
  transform: translateY(-2px);
  color: #38bdf8;
}

.cart-badge {
  position: absolute;
  top: 0;
  right: -5px;
  background-color: #ef4444; 
  color: white;
  font-size: 0.7rem;
  font-weight: bold;
  height: 18px;
  width: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 2px solid #0f172a; 
}

.btn-link:hover {
  color: #93c5fd !important;
}

.btn-glow {
  background: #2563eb;
  border: none;
  font-weight: 600;
  box-shadow: 0 0 10px rgba(37, 99, 235, 0.5);
  transition: all 0.3s ease;
}

.btn-glow:hover {
  background: #3b82f6;
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.6);
}

@media (max-width: 991.98px) {
  .actions-menu {
    flex-direction: column;
    align-items: flex-start !important;
    margin-top: 1.5rem;
    padding-top: 1.5rem;
    border-top: 1px solid rgba(255,255,255,0.1);
    width: 100%;
  }
  
  .auth-buttons {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 15px; 
    margin-top: 1rem;
  }
  
  .auth-buttons .btn {
    width: 100%;
    text-align: center;
    justify-content: center;
  }

  .cart-btn {
    margin-bottom: 0.5rem;
    align-self: flex-start;
  }
}
</style>