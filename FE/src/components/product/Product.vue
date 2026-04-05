<script setup>
import { ref, onMounted, watch } from 'vue';
import apiClient from '../../services/api';

import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// --- BIẾN TRẠNG THÁI SẢN PHẨM ---
const products = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const sortBy = ref('newest');

// --- BIẾN TRẠNG THÁI CHO BỘ LỌC ---
const keyword = ref('');
const minPrice = ref('');
const maxPrice = ref('');
const inStockOnly = ref(false);

// --- HÀM GỌI API TÍCH HỢP LỌC ---
const fetchProducts = async () => {
  try {
    const response = await apiClient.get('/products', {
      params: { 
        page: currentPage.value, 
        size: 6, 
        sortType: sortBy.value,
        keyword: keyword.value,
        minPrice: minPrice.value ? minPrice.value : null,
        maxPrice: maxPrice.value ? maxPrice.value : null,
        inStockOnly: inStockOnly.value
      }
    });

    products.value = response.data.content.map(item => ({
      id: item.id,
      name: item.name,
      code: item.sku,
      status: item.stockQuantity > 0 ? "Còn hàng" : "Hết hàng",
      image: item.imageUrl || `https://placehold.co/400x300/e2e8f0/1e293b?text=${encodeURIComponent(item.sku)}`,
      origin: item.defaultMaterial || "Đang cập nhật",
      price: item.price,
      stockQuantity: item.stockQuantity 
    }));

    const pageMeta = response.data.page || response.data;
    totalPages.value = pageMeta.totalPages || 0;
  } catch (error) {
    console.error("Lỗi lấy dữ liệu:", error);
  }
};

// --- CÁC HÀM XỬ LÝ SỰ KIỆN ---
const applyFilter = () => {
  currentPage.value = 0; // Lọc thì phải quay về trang 1
  fetchProducts();
};

const clearFilter = () => {
  keyword.value = '';
  minPrice.value = '';
  maxPrice.value = '';
  inStockOnly.value = false;
  applyFilter();
};

const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    fetchProducts();
    scrollToTop();
  }
};

// Lắng nghe sự kiện
onMounted(() => fetchProducts());
watch(sortBy, () => applyFilter());
</script>

<template>
  <div class="product-page d-flex flex-column min-vh-100">
    <Navbar />

    <div class="page-header py-5 text-center text-white">
      <div class="container">
        <h1 class="fw-bold text-uppercase display-5 animate-up">Danh Mục Sản Phẩm</h1>
        <div class="divider mx-auto my-3"></div>
        <p class="lead text-white-50 animate-up delay-1">Các sản phẩm đã được gia công cơ khí chính xác</p>
      </div>
    </div>

    <div class="container py-5 flex-grow-1">
      <div class="row">

        <aside class="col-lg-3 col-md-4 mb-4">
          <div class="sidebar-wrapper bg-white shadow-sm rounded-3 p-4 border h-100">
            <div class="sidebar-header border-bottom pb-3 mb-4">
              <h5 class="fw-bold text-uppercase m-0">
                <i class="fas fa-filter text-warning me-2"></i>Bộ Lọc
              </h5>
            </div>
            
            <div class="mb-4">
              <label class="form-label fw-bold small text-muted">Tìm kiếm</label>
              <input type="text" class="form-control" placeholder="Tên hoặc Mã SP..." v-model="keyword" @keyup.enter="applyFilter">
            </div>

            <div class="mb-4">
              <label class="form-label fw-bold small text-muted">Khoảng giá (VNĐ)</label>
              <div class="d-flex align-items-center gap-2 mb-2">
                <input type="number" class="form-control form-control-sm" placeholder="Từ..." v-model="minPrice">
                <span class="text-muted">-</span>
                <input type="number" class="form-control form-control-sm" placeholder="Đến..." v-model="maxPrice">
              </div>
            </div>

            <div class="form-check mb-4">
              <input class="form-check-input" type="checkbox" id="inStockCheckbox" v-model="inStockOnly">
              <label class="form-check-label small fw-bold text-muted" for="inStockCheckbox">
                Chỉ xem hàng còn tồn kho
              </label>
            </div>

            <button class="btn w-100 mb-2 fw-bold text-white" style="background-color: #0b2e59;" @click="applyFilter">
              Lọc Kết Quả
            </button>
            <button class="btn btn-outline-secondary w-100 btn-sm" @click="clearFilter">
              Xóa lọc
            </button>
          </div>
        </aside>

        <main class="col-lg-9 col-md-8">
          <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
            <span class="text-muted fw-bold">Các sản phẩm</span>
            <select class="form-select form-select-sm w-auto border-secondary" v-model="sortBy">
              <option value="newest">Mới nhất</option>
              <option value="price_asc">Giá: Thấp đến Cao</option>
              <option value="price_desc">Giá: Cao đến Thấp</option>
            </select>
          </div>

          <div class="row g-4">
            <div v-for="(product, index) in products" :key="index" class="col-xl-4 col-md-6 col-12">
              <div class="product-card h-100 card border-0 shadow-sm">
                <div class="img-wrapper position-relative overflow-hidden">
                  <img :src="product.image" class="card-img-top product-img" :alt="product.name">
                  <div class="badges position-absolute top-0 start-0 m-2">
                    <span v-if="product.status === 'Còn hàng'" class="badge bg-success rounded-0 text-uppercase shadow-sm">
                      <i class="fas fa-check me-1"></i>Còn hàng
                    </span>
                    <span v-else class="badge bg-secondary rounded-0 text-uppercase shadow-sm">
                      <i class="fas fa-times me-1"></i>Hết hàng
                    </span>
                  </div>
                </div>

                <div class="card-body d-flex flex-column p-3">
                  <h5 class="product-title text-truncate-2 mb-2" :title="product.name">{{ product.name }}</h5>

                  <div class="product-specs mb-3">
                    <div class="spec-row d-flex align-items-center text-muted small mb-1">
                      <i class="fas fa-barcode me-2 text-warning" style="width: 16px;"></i>
                      <span>Mã: {{ product.code }}</span>
                    </div>
                    <div class="spec-row d-flex align-items-center text-muted small">
                      <i class="fas fa-cube me-2 text-warning" style="width: 16px;"></i>
                      <span>Vật liệu: {{ product.origin }}</span>
                    </div>
                  </div>

                  <div class="mt-auto border-top pt-3 d-flex justify-content-between align-items-center">
                    <span class="price text-danger fw-bold">
                      {{ product.price ? product.price.toLocaleString('vi-VN') + ' ₫' : 'Liên hệ' }}
                    </span>
                    
                    <router-link :to="`/product/${product.id}`" class="btn btn-outline-primary btn-sm rounded-pill px-3 fw-bold">
                      Chi tiết
                    </router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="products.length === 0" class="text-center py-5">
            <h5 class="text-muted">Không tìm thấy sản phẩm nào phù hợp với bộ lọc.</h5>
            <button class="btn btn-link mt-2" @click="clearFilter">Xóa bộ lọc</button>
          </div>

          <nav class="mt-5 d-flex justify-content-center" v-if="totalPages > 1">
            <ul class="pagination">
              <li class="page-item" :class="{ disabled: currentPage === 0 }">
                <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">Trước</a>
              </li>
              <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page - 1 }">
                <a class="page-link" href="#" @click.prevent="changePage(page - 1)">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
                <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">Sau</a>
              </li>
            </ul>
          </nav>
        </main>
      </div>
    </div>

    <Footer />
    </div>
</template>

<style scoped>
/* Toàn bộ CSS của bạn được giữ lại y nguyên 100% */
.product-page {
  background-color: #f8fafc;
  color: #333;
}

.page-header {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  position: relative;
  margin-bottom: 3rem;
  padding: 4rem 0;
}

.divider {
  width: 80px;
  height: 4px;
  background-color: #f59e0b;
  border-radius: 2px;
}

.sidebar-wrapper {
  border: 1px solid rgba(0, 0, 0, 0.08) !important;
}

.product-card {
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.08) !important;
  border-radius: 4px;
  background: white;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
  border-color: #0d6efd !important;
}

.img-wrapper {
  height: 220px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-img {
  height: 100%;
  width: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-img {
  transform: scale(1.08);
}

.product-title {
  font-weight: 700;
  font-size: 1.15rem;
  color: #1e293b;
  text-transform: uppercase;
  height: 2.8rem;
  overflow: hidden;
  line-height: 1.4rem;
}

.product-card:hover .product-title {
  color: #0d6efd;
}

.product-specs {
  border-bottom: 1px dashed #e2e8f0;
  padding-bottom: 10px;
}

.floating-group {
  position: fixed;
  bottom: 30px;
  right: 30px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 1000;
}

.float-btn {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  font-family: Arial, sans-serif;
  font-size: 0.8rem;
}

.animate-up {
  animation: fadeUp 0.8s forwards;
  opacity: 0;
  transform: translateY(20px);
}
.delay-1 { animation-delay: 0.2s; }

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 991px) {
  .sidebar-wrapper {
    margin-bottom: 2rem;
  }
}
</style>