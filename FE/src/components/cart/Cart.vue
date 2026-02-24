<script setup>
import { ref } from 'vue';

// Dữ liệu cứng (Hardcoded)
const cartItems = ref([
  {
    id: 1,
    name: "Thép cán nóng (Hot Rolled Steel)",
    code: "SS400",
    category: "Cơ khí xây dựng",
    price: 15000000,
    quantity: 2,
    total: 30000000,
    image: "https://placehold.co/150x150/e2e8f0/1e293b?text=Thep+Can+Nong"
  },
  {
    id: 2,
    name: "Bồn lọc áp lực công nghiệp",
    code: "TANK-01",
    category: "Xử lý nước thải",
    price: 55000000,
    quantity: 1,
    total: 55000000,
    image: "https://placehold.co/150x150/e2e8f0/1e293b?text=Bon+Loc"
  },
  {
    id: 3,
    name: "Ống Inox 304 Công Nghiệp",
    code: "SUS304",
    category: "Vật tư đường ống",
    price: 8500000,
    quantity: 5,
    total: 42500000,
    image: "https://placehold.co/150x150/e2e8f0/1e293b?text=Ong+Inox"
  }
]);

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
</script>

<template>
  <div class="cart-page min-vh-100 bg-light d-flex flex-column">
    
    <div class="cart-header bg-white shadow-sm py-3 sticky-top">
      <div class="container d-flex justify-content-between align-items-center">
        <div class="d-flex align-items-center gap-3">
            <h3 class="fw-bold text-navy mb-0 text-uppercase">
                <i class="fas fa-shopping-cart me-2 text-warning"></i>Giỏ Hàng
            </h3>
        </div>

        <router-link to="/" class="btn btn-outline-navy rounded-pill px-4 fw-bold">
            <i class="fas fa-home me-2"></i>Về Trang Chủ
        </router-link>
      </div>
    </div>

    <div class="container py-5 flex-grow-1">
      <div class="row g-5">
        
        <div class="col-lg-8">
          <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="bg-navy text-white">
                  <tr>
                    <th scope="col" class="py-3 ps-4" style="min-width: 300px;">Sản phẩm</th>
                    <th scope="col" class="py-3 text-center">Đơn giá</th>
                    <th scope="col" class="py-3 text-center">SL</th>
                    <th scope="col" class="py-3 text-end pe-4">Thành tiền</th>
                    <th scope="col" class="py-3"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cartItems" :key="item.id">
                    <td class="ps-4 py-3">
                      <div class="d-flex align-items-center">
                        <div class="img-thumbnail-wrapper me-3">
                          <img :src="item.image" alt="Product" class="cart-img">
                        </div>
                        <div>
                          <h6 class="mb-1 fw-bold text-dark">{{ item.name }}</h6>
                          <div class="badge bg-light text-dark border mb-1">Mã: {{ item.code }}</div>
                        </div>
                      </div>
                    </td>
                    
                    <td class="text-center fw-semibold text-muted">
                      {{ formatCurrency(item.price) }}
                    </td>
                    
                    <td>
                      <div class="quantity-control d-flex justify-content-center align-items-center mx-auto">
                        <button class="btn btn-sm btn-light border">-</button>
                        <input type="text" class="form-control form-control-sm text-center mx-1 border-0 fw-bold" :value="item.quantity" readonly style="width: 40px;">
                        <button class="btn btn-sm btn-light border">+</button>
                      </div>
                    </td>
                    
                    <td class="text-end pe-4 fw-bold text-primary">
                      {{ formatCurrency(item.total) }}
                    </td>
                    
                    <td class="text-center">
                      <button class="btn btn-link text-danger p-0">
                        <i class="fas fa-trash-alt"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card border-0 shadow-sm summary-card">
            <div class="card-header bg-white border-bottom py-3">
              <h5 class="mb-0 fw-bold text-uppercase text-dark">Tổng đơn hàng</h5>
            </div>
            <div class="card-body p-4">
              <div class="d-flex justify-content-between mb-3">
                <span class="text-muted">Tạm tính:</span>
                <span class="fw-bold text-dark">127.500.000 ₫</span>
              </div>
              <div class="d-flex justify-content-between mb-3">
                <span class="text-muted">Thuế VAT (8%):</span>
                <span class="fw-bold text-dark">10.200.000 ₫</span>
              </div>
              
              <div class="divider my-3"></div>
              
              <div class="d-flex justify-content-between align-items-center mb-4">
                <span class="h6 mb-0 fw-bold text-uppercase">Tổng cộng:</span>
                <span class="h4 text-danger fw-bold mb-0">137.700.000 ₫</span>
              </div>

              <button class="btn btn-primary w-100 py-3 fw-bold text-uppercase rounded-1 mb-2 shadow-sm btn-checkout">
                Xác nhận đặt hàng
              </button>
              
              <div class="mt-4 p-3 bg-light rounded small text-muted">
                <i class="fas fa-lock me-1"></i> 
                Thông tin đơn hàng sẽ được gửi về phòng kinh doanh để xử lý.
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
    
  </div>
</template>

<style scoped>

.cart-page {
  background-color: #f1f5f9; /* Màu nền xám nhẹ */
}

/* MÀU SẮC */
.text-navy { color: #0f172a; }
.bg-navy { background-color: #0f172a; }
.text-primary { color: #0f172a !important; }

/* Header riêng của trang Cart */
.cart-header {
    border-bottom: 4px solid #f59e0b; /* Viền cam đặc trưng */
}

.btn-outline-navy {
    color: #0f172a;
    border: 2px solid #0f172a;
    transition: all 0.3s;
}
.btn-outline-navy:hover {
    background-color: #0f172a;
    color: white;
}

/* Nút Checkout */
.btn-primary { 
  background-color: #0f172a; 
  border-color: #0f172a;
}
.btn-primary:hover {
  background-color: #f59e0b;
  border-color: #f59e0b;
  color: #000;
}

/* Ảnh sản phẩm */
.img-thumbnail-wrapper {
  width: 60px; height: 60px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  background: #fff;
}
.cart-img {
  width: 100%; height: 100%; object-fit: cover;
}

/* Card Tổng */
.summary-card {
  border-top: 4px solid #f59e0b !important;
}
.divider {
  border-top: 2px dashed #e2e8f0;
}

/* Quantity Control */
.quantity-control {
  border: 1px solid #dee2e6;
  border-radius: 4px;
  padding: 2px;
  background: white;
  width: fit-content;
}

/* Responsive Table */
@media (max-width: 768px) {
  .table thead { display: none; }
  .table tr {
    display: block; border-bottom: 2px solid #eee; padding-bottom: 1rem; margin-bottom: 1rem; background: white; padding: 10px; border-radius: 8px;
  }
  .table td {
    display: flex; justify-content: space-between; align-items: center; border: none; padding: 0.5rem 0;
  }
  .table td::before {
    content: attr(data-label); font-weight: bold; text-transform: uppercase; margin-right: 10px;
  }
  .table td:first-child { display: block; }
}
</style>