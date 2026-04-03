<script setup>
import { ref, computed, watch } from 'vue';
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// Import dữ liệu giỏ hàng từ bộ nhớ (store)
import { cartState, removeFromCart } from '../../store/cart.js';

// Mảng chứa ID của các sản phẩm được tick chọn
const selectedIds = ref([]);

// Logic: Tick chọn tất cả
const selectAll = computed({
  get: () => {
    if (cartState.items.length === 0) return false;
    return selectedIds.value.length === cartState.items.length;
  },
  set: (value) => {
    if (value) {
      selectedIds.value = cartState.items.map(item => item.id); // Chọn hết
    } else {
      selectedIds.value = []; // Bỏ chọn hết
    }
  }
});

// Tính tổng tiền CHỈ cho những món được tick
const selectedTotalPrice = computed(() => {
  return cartState.items
    .filter(item => selectedIds.value.includes(item.id))
    .reduce((total, item) => total + (item.price * item.quantity), 0);
});

// Hàm tăng giảm số lượng
const updateQuantity = (item, amount) => {
  // LỚP PHÒNG THỦ: Không cho tăng số lượng lớn hơn tồn kho thực tế
  if (amount > 0 && item.quantity >= (item.stockQuantity || 99999)) {
    Swal.fire({
      toast: true, position: 'top-end', icon: 'error',
      title: `Sản phẩm này chỉ còn ${item.stockQuantity} cái trong kho!`,
      showConfirmButton: false, timer: 3000
    });
    return;
  }
  
  if (item.quantity + amount > 0) {
    item.quantity += amount;
    localStorage.setItem('upec_cart', JSON.stringify(cartState.items));
  }
};

// Xóa 1 món (Nếu xóa thì cũng gỡ nó ra khỏi danh sách đang tick)
const handleRemove = (id) => {
  selectedIds.value = selectedIds.value.filter(selectedId => selectedId !== id);
  removeFromCart(id);
};
</script>

<template>
  <div class="cart-page d-flex flex-column min-vh-100 bg-light">
    <Navbar />
    
    <div class="container py-5 mt-4 flex-grow-1 page-container">
      
      <div class="d-flex justify-content-center align-items-center mb-4 fade-in">
        <h2 class="mb-0 fw-bold" style="color: #0b2e59;">
          <i class="bi bi-cart3 me-2 text-primary"></i>Giỏ Hàng Vật Tư
        </h2>
      </div>

      <div v-if="cartState.items.length === 0" class="row justify-content-center py-5 slide-up">
        <div class="col-md-6 col-lg-5 text-center">
          <div class="bg-white border shadow-sm rounded-4 p-5">
            <div class="bg-light rounded-circle d-inline-flex align-items-center justify-content-center mb-4" style="width: 100px; height: 100px;">
              <i class="bi bi-cart-x text-muted" style="font-size: 3.5rem;"></i>
            </div>
            <h4 class="text-dark fw-bold mb-3">Giỏ hàng của bạn đang trống</h4>
            <p class="text-muted mb-4">Có vẻ như bạn chưa chọn vật tư nào. Hãy tham khảo các mác thép và linh kiện chất lượng cao của UPEC nhé!</p>
            <router-link to="/products" class="btn text-white rounded-3 px-4 py-2 fw-bold hover-elevate w-100" style="background-color: #0b2e59;">
              Vào kho vật tư ngay
            </router-link>
          </div>
        </div>
      </div>

      <div v-else class="row g-4 slide-up">
        <div class="col-lg-8">
          <div class="card shadow-sm border-0 rounded-3 overflow-hidden">
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0 uniform-table">
                <thead class="table-light text-muted small text-uppercase">
                  <tr>
                    <th class="px-4 py-3" style="width: 40px;">
                      <div class="form-check">
                        <input class="form-check-input" type="checkbox" v-model="selectAll" style="cursor: pointer;">
                      </div>
                    </th>
                    <th class="py-3 w-50">Sản phẩm / Mác vật liệu</th>
                    <th class="py-3 text-center">Đơn giá</th>
                    <th class="py-3 text-center" style="width: 140px;">Số lượng</th>
                    <th class="py-3 text-end px-4">Xóa</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cartState.items" :key="item.id">
                    
                    <td class="px-4 border-bottom-0">
                      <div class="form-check">
                        <input class="form-check-input" type="checkbox" :value="item.id" v-model="selectedIds" style="cursor: pointer;">
                      </div>
                    </td>

                    <td class="py-4 d-flex align-items-center border-bottom-0">
                      <img :src="item.image" style="width: 65px; height: 65px; object-fit: cover;" class="border rounded-2 me-3 shadow-sm bg-light">
                      <div>
                        <h6 class="mb-1 fw-bold text-dark" style="font-size: 1.05rem;">{{ item.name }}</h6>
                        <small class="text-muted"><i class="bi bi-upc-scan me-1"></i> SKU: {{ item.code }}</small>
                      </div>
                    </td>

                    <td class="text-center fw-bold text-danger border-bottom-0">
                      {{ item.price > 0 ? item.price.toLocaleString('vi-VN') + ' ₫' : 'Liên hệ' }}
                    </td>

                    <td class="text-center border-bottom-0">
                      <div class="input-group input-group-sm rounded-2 overflow-hidden border">
                        <button class="btn btn-light border-0 fw-bold px-3 text-secondary" @click="updateQuantity(item, -1)">-</button>
                        <input type="text" class="form-control text-center border-0 fw-bold bg-white" readonly :value="item.quantity">
                        <button class="btn btn-light border-0 fw-bold px-3 text-secondary" @click="updateQuantity(item, 1)">+</button>
                      </div>
                    </td>

                    <td class="text-end px-4 border-bottom-0">
                      <button class="btn btn-sm btn-outline-danger rounded-2 hover-elevate" @click="handleRemove(item.id)">
                        <i class="bi bi-trash3"></i>
                      </button>
                    </td>

                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card shadow-sm border-0 sticky-top" style="top: 80px;">
            <div class="card-body p-4">
              <h5 class="fw-bold mb-4 border-bottom pb-3 text-uppercase" style="color: #0b2e59;">Tóm tắt yêu cầu</h5>
              
              <div class="d-flex justify-content-between mb-3 text-muted">
                <span>Số loại vật tư đã chọn:</span>
                <span class="fw-bold text-dark">{{ selectedIds.length }} loại</span>
              </div>
              
              <div class="d-flex justify-content-between mb-4 border-bottom pb-4">
                <span class="text-muted mt-1">Tạm tính:</span>
                <span class="fw-bold text-danger fs-4">
                  {{ selectedTotalPrice > 0 ? selectedTotalPrice.toLocaleString('vi-VN') + ' ₫' : '0 ₫' }}
                </span>
              </div>
              
              <router-link to="/checkout" 
                class="btn w-100 fw-bold text-white rounded-2 py-3 text-uppercase fs-6 shadow-sm hover-elevate mb-3 d-flex justify-content-center align-items-center" 
                :class="{ 'disabled bg-secondary': selectedIds.length === 0 }"
                :style="selectedIds.length > 0 ? 'background-color: #0b2e59;' : ''">
                Tiến hành thanh toán <i class="bi bi-arrow-right-circle ms-2"></i>
              </router-link>
              
              <p class="text-center text-muted small mb-0">
                <i class="bi bi-shield-check text-success me-1"></i> Hỗ trợ xuất hóa đơn VAT (Nếu cần)
              </p>
            </div>
          </div>
        </div>
      </div>

    </div>
    <Footer />
  </div>
</template>

<style scoped>
/* Animation */
.page-container { animation: fadeIn 0.4s ease-out forwards; }
.fade-in { animation: fadeIn 0.6s ease-out forwards; }
.slide-up { opacity: 0; transform: translateY(20px); animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* Hover Effects */
.hover-elevate { transition: all 0.2s ease; }
.hover-elevate:hover { transform: translateY(-3px); }

/* Table styling */
.uniform-table th, .uniform-table td { vertical-align: middle; }
.form-check-input { width: 1.2em; height: 1.2em; }
.form-check-input:checked { background-color: #0b2e59; border-color: #0b2e59; }

/* Nút Disable */
.disabled { pointer-events: none; opacity: 0.6; }
</style>