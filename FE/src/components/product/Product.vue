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

// --- BIẾN TRẠNG THÁI CHO BỘ LỌC ---
const keyword = ref('');
const minPrice = ref('');
const maxPrice = ref('');
const inStockOnly = ref(false);
const sortBy = ref('newest');

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
  currentPage.value = 0; 
  fetchProducts();
};

const clearFilter = () => {
  keyword.value = '';
  minPrice.value = '';
  maxPrice.value = '';
  inStockOnly.value = false;
  sortBy.value = 'newest';
  applyFilter();
};

const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    fetchProducts();
    scrollToTop();
  }
};

onMounted(() => fetchProducts());
watch(sortBy, () => applyFilter());
</script>

<template>
  <div class="product-page d-flex flex-column min-vh-100">
    <Navbar />

    <div class="page-header py-5 text-center text-white">
      <div class="container py-4">
        <h1 class="fw-bolder text-uppercase display-5 animate-up letter-spacing-1">Danh Mục Sản Phẩm</h1>
        <div class="divider mx-auto my-3"></div>
        <p class="lead text-white-50 animate-up delay-1 fw-medium">Cung cấp các sản phẩm phôi thép, gia công cơ khí chính xác</p>
      </div>
    </div>

    <div class="container py-5 flex-grow-1">
      <div class="row g-4">

        <aside class="col-lg-3 col-md-4">
          <div class="sidebar-wrapper bg-white shadow-sm rounded-4 p-4 border h-100 sticky-sidebar">
            <div class="sidebar-header border-bottom border-light pb-3 mb-4 d-flex align-items-center">
              <i class="fas fa-filter text-navy fs-5 me-2"></i>
              <h5 class="fw-bolder text-uppercase m-0 text-navy">Bộ Lọc</h5>
            </div>
            
            <div class="mb-4">
              <label class="form-label fw-bold small text-muted text-uppercase">Tìm kiếm</label>
              <div class="position-relative">
                <i class="fas fa-search position-absolute text-muted" style="top: 50%; left: 14px; transform: translateY(-50%); font-size: 0.9rem;"></i>
                <input type="text" class="form-control custom-input ps-5" placeholder="Tên, mã SP..." v-model="keyword" @keyup.enter="applyFilter">
              </div>
            </div>

            <div class="mb-4">
              <label class="form-label fw-bold small text-muted text-uppercase">Khoảng giá (VNĐ)</label>
              <div class="d-flex align-items-center gap-2">
                <input type="number" class="form-control custom-input text-center px-1" placeholder="Tối thiểu" v-model="minPrice">
                <span class="text-muted fw-bold">-</span>
                <input type="number" class="form-control custom-input text-center px-1" placeholder="Tối đa" v-model="maxPrice">
              </div>
            </div>

            <div class="mb-4 border-bottom border-light pb-4">
              <label class="form-label fw-bold small text-muted text-uppercase">Sắp xếp theo</label>
              <select class="form-select custom-input fw-medium text-dark" v-model="sortBy">
                <option value="newest">Hàng mới nhất</option>
                <option value="price_asc">Giá: Thấp đến Cao</option>
                <option value="price_desc">Giá: Cao đến Thấp</option>
              </select>
            </div>

            <div class="mb-4 d-flex align-items-center justify-content-between px-1">
              <label class="fw-bold text-dark mb-0" style="cursor:pointer; font-size: 0.95rem;" for="hideStockToggle">Ẩn hàng đã hết</label>
              <div class="form-check form-switch m-0 fs-5">
                <input class="form-check-input custom-toggle shadow-none" type="checkbox" id="hideStockToggle" v-model="inStockOnly" @change="applyFilter">
              </div>
            </div>

            <button class="btn btn-navy w-100 mb-2 fw-bold rounded-pill py-2 shadow-sm hover-lift" @click="applyFilter">
              <i class="fas fa-sliders-h me-1"></i> Áp dụng bộ lọc
            </button>
            <button class="btn btn-light border text-secondary w-100 fw-bold rounded-pill py-2 hover-lift" @click="clearFilter">
              Xóa bộ lọc
            </button>
          </div>
        </aside>

        <main class="col-lg-9 col-md-8">
          <div class="row g-4">
            <div v-for="(product, index) in products" :key="index" class="col-xl-4 col-sm-6 col-12">
              
              <div class="product-card h-100 card border-0 shadow-sm rounded-4 overflow-hidden" :class="{'is-out-of-stock': product.status === 'Hết hàng'}">
                
                <div class="img-wrapper position-relative overflow-hidden bg-light">
                  <img :src="product.image" class="card-img-top product-img" :alt="product.name">
                  
                  <div v-if="product.status === 'Hết hàng'" class="out-of-stock-layer d-flex align-items-center justify-content-center">
                    <span class="out-of-stock-text">HẾT HÀNG</span>
                  </div>

                  <div v-if="product.status === 'Còn hàng'" class="in-stock-bar">
                    <i class="fas fa-check-circle me-1 stock-icon"></i> Đang còn hàng
                  </div>
                </div>

                <div class="card-body d-flex flex-column p-4">
                  <h5 class="product-title text-truncate-2 mb-3" :title="product.name">{{ product.name }}</h5>

                  <div class="product-specs mb-3">
                    <div class="spec-row d-flex align-items-center text-muted small mb-2">
                      <div class="spec-icon bg-light rounded-circle d-flex align-items-center justify-content-center me-2">
                        <i class="fas fa-barcode text-warning"></i>
                      </div>
                      <span class="fw-medium text-secondary">Mã: <b class="text-dark font-monospace">{{ product.code }}</b></span>
                    </div>
                    <div class="spec-row d-flex align-items-center text-muted small">
                      <div class="spec-icon bg-light rounded-circle d-flex align-items-center justify-content-center me-2">
                        <i class="fas fa-cube text-warning"></i>
                      </div>
                      <span class="fw-medium text-secondary">Vật liệu: <b class="text-dark">{{ product.origin }}</b></span>
                    </div>
                  </div>

                  <div class="mt-auto border-top border-light pt-3 d-flex justify-content-between align-items-center">
                    <span class="price fw-bolder fs-5" :class="product.status === 'Hết hàng' ? 'text-muted' : 'text-danger'">
                      {{ product.price ? product.price.toLocaleString('vi-VN') + ' ₫' : 'Liên hệ' }}
                    </span>
                    
                    <router-link :to="`/product/${product.id}`" class="btn btn-sm rounded-pill px-4 fw-bold hover-lift"
                      :class="product.status === 'Hết hàng' ? 'btn-outline-secondary' : 'btn-outline-navy'">
                      Chi tiết
                    </router-link>
                  </div>
                </div>
              </div>
              
            </div>
          </div>

          <div v-if="products.length === 0" class="text-center py-5 bg-white rounded-4 border shadow-sm mt-2">
            <div class="bg-light rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3" style="width: 80px; height: 80px;">
              <i class="fas fa-box-open fs-1 text-muted opacity-50"></i>
            </div>
            <h5 class="text-dark fw-bold">Không tìm thấy sản phẩm nào</h5>
            <p class="text-muted small">Thử thay đổi từ khoá hoặc điều chỉnh lại bộ lọc nhé.</p>
            <button class="btn btn-navy rounded-pill px-4 mt-2 shadow-sm hover-lift" @click="clearFilter">Xóa bộ lọc</button>
          </div>

          <nav class="mt-5 d-flex justify-content-center" v-if="totalPages > 1">
            <ul class="pagination custom-pagination gap-2">
              <li class="page-item" :class="{ disabled: currentPage === 0 }">
                <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)"><i class="fas fa-chevron-left me-1"></i> Trước</a>
              </li>
              <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page - 1 }">
                <a class="page-link" href="#" @click.prevent="changePage(page - 1)">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
                <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">Sau <i class="fas fa-chevron-right ms-1"></i></a>
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
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.btn-navy { background-color: #0b2e59; color: #fff; border: none; transition: 0.3s; }
.btn-navy:hover { background-color: #173b6c; color: #fff; }
.btn-outline-navy { color: #0b2e59; border: 1.5px solid #0b2e59; background: transparent; transition: 0.3s; }
.btn-outline-navy:hover { background: #0b2e59; color: #fff; }

.hover-lift { transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.2s; }
.hover-lift:hover:not(:disabled) { transform: translateY(-3px); box-shadow: 0 8px 15px rgba(0,0,0,0.06) !important; }

.letter-spacing-1 { letter-spacing: 1px; }

.product-page { background-color: #f8fafc; color: #333; }
.page-header { background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%); position: relative; margin-bottom: 2rem; }
.divider { width: 80px; height: 4px; background-color: #f59e0b; border-radius: 2px; }

.animate-up { animation: fadeUp 0.8s forwards; opacity: 0; transform: translateY(20px); }
.delay-1 { animation-delay: 0.2s; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }

/* SIDEBAR */
.sticky-sidebar { position: sticky; top: 20px; }
.custom-input { border: 1px solid #e2e8f0; border-radius: 10px; padding: 0.6rem 1rem; transition: 0.2s; box-shadow: none !important; }
.custom-input:focus { border-color: #0b2e59 !important; box-shadow: 0 0 0 3px rgba(11, 46, 89, 0.1) !important; background-color: #fff; }
.custom-toggle { width: 2.8rem; height: 1.4rem; cursor: pointer; }
.custom-toggle:checked { background-color: #0b2e59; border-color: #0b2e59; }

/* PRODUCT CARD */
.product-card { transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); border: 1px solid #f1f5f9 !important; }
.product-card:hover { transform: translateY(-6px); box-shadow: 0 15px 30px rgba(0, 0, 0, 0.08) !important; border-color: #dbeafe !important; }

.img-wrapper { height: 240px; display: flex; align-items: center; justify-content: center; }
.product-img { height: 100%; width: 100%; object-fit: cover; transition: transform 0.5s cubic-bezier(0.25, 0.8, 0.25, 1); }
.product-card:not(.is-out-of-stock):hover .product-img { transform: scale(1.08); }

.product-title { font-weight: 800; font-size: 1.1rem; color: #1e293b; height: 2.6rem; overflow: hidden; line-height: 1.3rem; transition: 0.2s; }
.product-card:hover .product-title { color: #0b2e59; }

.spec-icon { width: 28px; height: 28px; }

/* IN STOCK HOVER */
.in-stock-bar {
  position: absolute; bottom: -45px; left: 0; width: 100%;
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
  color: white; text-align: center; padding: 12px 0;
  font-weight: 700; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px;
  transition: bottom 0.35s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 -5px 15px rgba(16, 185, 129, 0.3);
  z-index: 5;
}
.product-card:not(.is-out-of-stock):hover .in-stock-bar { bottom: 0; }
.stock-icon { animation: bounceIcon 2s infinite; }
@keyframes bounceIcon {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-3px); }
  60% { transform: translateY(-2px); }
}

/* OUT OF STOCK - HIỆU ỨNG GLOW RED KHI HOVER */
.is-out-of-stock { 
  filter: grayscale(35%); 
  box-shadow: none !important; 
  transition: all 0.4s ease;
}
.is-out-of-stock:hover { 
  filter: grayscale(0%); /* Trả lại một chút màu để màu đỏ nổi bật */
  transform: translateY(-2px); 
  border-color: #fee2e2 !important; 
  box-shadow: 0 10px 25px rgba(239, 68, 68, 0.15) !important;
}

.out-of-stock-layer {
  position: absolute; inset: 0; 
  background: rgba(15, 23, 42, 0.55); /* Màn sương đen mặc định */
  z-index: 10; backdrop-filter: blur(2px);
  transition: background 0.4s ease;
}
.is-out-of-stock:hover .out-of-stock-layer {
  background: rgba(239, 68, 68, 0.25); /* Chuyển sang màn sương đỏ nhẹ khi hover */
}

.out-of-stock-text {
  font-size: 1.6rem; 
  font-weight: 900; 
  color: #f8fafc; /* Chữ trắng mặc định */
  letter-spacing: 4px; 
  text-transform: uppercase;
  text-shadow: 0 4px 15px rgba(0, 0, 0, 0.8); /* Bóng đổ xám/đen */
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.is-out-of-stock:hover .out-of-stock-text { 
  color: #ef4444; /* Đổi màu đỏ rực khi hover */
  text-shadow: 0 0 20px rgba(239, 68, 68, 0.8); /* Phát sáng đỏ */
  transform: scale(1.08); /* Phóng to nhẹ */
}

/* PHÂN TRANG (Custom Pill Pagination) */
.custom-pagination .page-link {
  border-radius: 50px !important;
  border: none;
  color: #64748b;
  font-weight: 600;
  padding: 0.5rem 1rem;
  background: #fff;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  transition: all 0.2s ease;
}
.custom-pagination .page-link:hover {
  background: #f1f5f9;
  color: #0b2e59;
  transform: translateY(-2px);
}
.custom-pagination .page-item.active .page-link {
  background: #0b2e59;
  color: #fff;
  box-shadow: 0 5px 15px rgba(11, 46, 89, 0.3);
  transform: translateY(-2px);
}
.custom-pagination .page-item.disabled .page-link {
  background: transparent;
  box-shadow: none;
  opacity: 0.6;
}
</style>