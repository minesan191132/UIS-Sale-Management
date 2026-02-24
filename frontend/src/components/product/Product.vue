<script setup>
import { ref } from 'vue';

// Import Navbar và Footer từ folder base
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// Hàm cuộn lên đầu trang
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// Dữ liệu sản phẩm giả lập (Sau này gọi API thì thay vào đây)
const products = ref([
  {
    id: 1,
    name: "?1",
    code: "SS400 / A36",
    status: "Còn hàng",
    image: "https://placehold.co/400x300/e2e8f0/1e293b?text=?1"
  },
  {
    id: 2,
    name: "?2",
    code: "Q235B / SS400",
    status: "Còn hàng",
    image: "https://placehold.co/400x300/e2e8f0/1e293b?text=?2"
  },
  {
    id: 3,
    name: "?3",
    code: "ASTM A53",
    status: "Hết hàng",
    image: "https://placehold.co/400x300/e2e8f0/1e293b?text=?3"
  },
  {
    id: 4,
    name: "?4",
    code: "Z275 / G450",
    status: "Còn hàng",
    image: "https://placehold.co/400x300/e2e8f0/1e293b?text=?4"
  },
  {
    id: 5,
    name: "?5",
    code: "TCVN",
    status: "Hết hàng",
    image: "https://placehold.co/400x300/e2e8f0/1e293b?text=?5"
  },
  {
    id: 6,
    name: "?6",
    code: "SPHC / SS400",
    status: "Còn hàng",
    image: "https://placehold.co/400x300/e2e8f0/1e293b?text=?6"
  },
]);
</script>

<template>
  <div class="product-page d-flex flex-column min-vh-100">
    <Navbar />

    <div class="page-header py-5 text-center text-white">
      <div class="container">
        <h1 class="fw-bold text-uppercase display-5 animate-up">Danh Mục Sản Phẩm</h1>
        <div class="divider mx-auto my-3"></div>
        <p class="lead text-white-50 animate-up delay-1">Các sản phẩm đã được gia công</p>
      </div>
    </div>

    <div class="container py-5 flex-grow-1">
      <div class="row">

        <aside class="col-lg-3 col-md-4 mb-4">
          <div class="sidebar-wrapper bg-white shadow-sm rounded-3 p-4 border h-100">
            <div class="sidebar-header border-bottom pb-3 mb-3">
              <h5 class="fw-bold text-uppercase m-0">
                <i class="fas fa-filter text-warning me-2"></i>Bộ Lọc
              </h5>
            </div>
          </div>
        </aside>

        <main class="col-lg-9 col-md-8">

          <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
            <span class="text-muted fw-bold">Các sản phẩm</span>
            <select class="form-select form-select-sm w-auto border-secondary">
              <option selected>Mới nhất</option>
              <option value="1">Giá: Thấp đến Cao</option>
              <option value="2">Giá: Cao đến Thấp</option>
            </select>
          </div>

          <div class="row g-4">
            <div v-for="(product, index) in products" :key="index" class="col-xl-4 col-md-6 col-12">
              <div class="product-card h-100 card border-0 shadow-sm">

                <div class="img-wrapper position-relative overflow-hidden">
                  <img :src="product.image" class="card-img-top product-img" :alt="product.name">
                  <div class="badges position-absolute top-0 start-0 m-2">
                    <span v-if="product.status === 'Còn hàng'"
                      class="badge bg-success rounded-0 text-uppercase shadow-sm">
                      <i class="fas fa-check me-1"></i>Còn hàng
                    </span>

                    <span v-else-if="product.status === 'Hết hàng'"
                      class="badge bg-secondary rounded-0 text-uppercase shadow-sm">
                      <i class="fas fa-times me-1"></i>Hết hàng
                    </span>

                    <span v-else class="badge bg-danger rounded-0 text-uppercase shadow-sm">
                      {{ product.status }}
                    </span>
                  </div>
                </div>

                <div class="card-body d-flex flex-column p-3">
                  <h5 class="product-title text-truncate-2 mb-2">{{ product.name }}</h5>

                  <div class="product-specs mb-3">
                    <div class="spec-row d-flex align-items-center text-muted small mb-1">
                      <i class="fas fa-barcode me-2 text-warning" style="width: 16px;"></i>
                      <span>Mã: {{ product.code }}</span>
                    </div>
                    <div class="spec-row d-flex align-items-center text-muted small">
                      <i class="fas fa-globe-asia me-2 text-warning" style="width: 16px;"></i>
                      <span>Xuất xứ: {{ product.origin }}</span>
                    </div>
                  </div>

                  <div class="mt-auto border-top pt-3 d-flex justify-content-between align-items-center">
                    <span class="price text-danger fw-bold">Liên hệ</span>
                    <button class="btn btn-outline-primary btn-sm rounded-pill px-3 fw-bold">
                      Chi tiết
                    </button>
                  </div>
                </div>

              </div>
            </div>
          </div>

          <nav class="mt-5 d-flex justify-content-center">
            <ul class="pagination">
              <li class="page-item disabled"><a class="page-link" href="#">Trước</a></li>
              <li class="page-item active"><a class="page-link" href="#">1</a></li>
              <li class="page-item"><a class="page-link" href="#">2</a></li>
              <li class="page-item"><a class="page-link" href="#">3</a></li>
              <li class="page-item"><a class="page-link" href="#">Sau</a></li>
            </ul>
          </nav>

        </main>
      </div>
    </div>


    <Footer />
  </div>
</template>

<style scoped>
/* --- 1. CẤU HÌNH CHUNG & FONT (Times New Roman) --- */
.product-page {
  background-color: #f8fafc;
  color: #333;
}

/* --- 2. HEADER BANNER --- */
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

/* --- 3. SIDEBAR (BỘ LỌC) --- */
.sidebar-wrapper {
  border: 1px solid rgba(0, 0, 0, 0.08) !important;
}

/* --- 4. PRODUCT CARD --- */
.product-card {
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.08) !important;
  border-radius: 4px;
  /* Bo góc ít để tạo cảm giác công nghiệp cứng cáp */
  background: white;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
  border-color: #0d6efd !important;
}

/* Ảnh sản phẩm */
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

/* Tiêu đề sản phẩm */
.product-title {
  font-weight: 700;
  font-size: 1.15rem;
  color: #1e293b;
  text-transform: uppercase;
  height: 2.8rem;
  /* Giới hạn chiều cao 2 dòng */
  overflow: hidden;
  line-height: 1.4rem;
}

.product-card:hover .product-title {
  color: #0d6efd;
}

/* Thông số kỹ thuật */
.product-specs {
  border-bottom: 1px dashed #e2e8f0;
  padding-bottom: 10px;
}

/* --- 5. FLOATING BUTTONS (Style cũ của bạn) --- */
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
  /* Nút trôi thì dùng Arial cho rõ */
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

/* Responsive */
@media (max-width: 991px) {
  .sidebar-wrapper {
    margin-bottom: 2rem;
  }
}
</style>