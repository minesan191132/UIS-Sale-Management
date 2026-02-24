<script setup>
import { ref } from 'vue';

import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// Dữ liệu giả lập các loại sắt thép phổ biến
const featuredProducts = ref([
  { 
    id: 1, 
    name: 'Thép Cuộn Cán Nóng', 
    desc: 'Mác thép SS400, tiêu chuẩn JIS',
    price: 'Liên hệ báo giá', 
    image: 'https://placehold.co/300x300/e2e8f0/1e293b?text=Thep+Cuon' // Ảnh minh họa màu xám công nghiệp
  },
  { 
    id: 2, 
    name: 'Thép Hình Chữ I/H', 
    desc: 'Chịu lực cao, đa dạng kích thước',
    price: '18.500₫ / kg', 
    image: 'https://placehold.co/300x300/e2e8f0/1e293b?text=Thep+Hinh+I' 
  },
  { 
    id: 3, 
    name: 'Thép Ống Mạ Kẽm', 
    desc: 'Chống ăn mòn, độ bền vượt trội',
    price: '21.000₫ / kg', 
    image: 'https://placehold.co/300x300/e2e8f0/1e293b?text=Thep+Ong' 
  },
  { 
    id: 4, 
    name: 'Tôn Lợp Công Nghiệp', 
    desc: 'Cán sóng vuông, nhiều màu sắc',
    price: 'Liên hệ', 
    image: 'https://placehold.co/300x300/e2e8f0/1e293b?text=Ton+Lop' 
  },
]);
</script>

<template>
  <div class="home-page">

    <Navbar />
    
    <section class="hero-banner d-flex align-items-center text-white">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-lg-7">
            <span class="badge bg-warning text-dark mb-3 px-3 py-2 fw-bold">CHẤT LƯỢNG ISO 9001:2015</span>
            <h1 class="display-4 fw-bold mb-3 animate-up text-uppercase">
              Vững xây công trình<br>
              <span class="text-gradient">Kiến tạo tương lai</span>
            </h1>
            <p class="lead mb-4 text-white-50 animate-up delay-1">
              Đối tác tin cậy cung cấp sắt thép xây dựng, thép hình, thép tấm cho mọi công trình trọng điểm quốc gia và dân dụng.
            </p>
            <div class="d-flex gap-3 animate-up delay-2">
              <button class="btn btn-warning btn-lg px-4 fw-bold text-dark btn-action">
                <i class="fas fa-file-invoice-dollar me-2"></i>Nhận Báo Giá
              </button>
              <button class="btn btn-outline-light btn-lg px-4 fw-bold btn-action">
                <i class="fas fa-warehouse me-2"></i>Xem Danh Mục
              </button>
            </div>
          </div>
          
          <div class="col-lg-5 d-none d-lg-block text-end">
            <img src="https://placehold.co/600x500/transparent/white?text=Steel+Structure" alt="Industrial Steel" class="img-fluid hero-img">
          </div>
        </div>
      </div>
    </section>

    <section class="products-section py-5">
      <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-5">
          <div>
            <h6 class="text-primary fw-bold text-uppercase mb-2">Sản phẩm chủ lực</h6>
            <h2 class="fw-bold text-dark">Vật Liệu Xây Dựng & Công Nghiệp</h2>
            <div class="title-underline"></div>
          </div>
          <a href="#" class="btn btn-link text-decoration-none fw-bold">Xem tất cả <i class="fas fa-arrow-right"></i></a>
        </div>

        <div class="row g-4">
          <div v-for="product in featuredProducts" :key="product.id" class="col-12 col-sm-6 col-lg-3">
            <div class="card product-card h-100 border-0">
              <div class="card-img-wrapper">
                <img :src="product.image" class="card-img-top" :alt="product.name">
                <span class="badge-stock">Còn hàng</span>
              </div>
              
              <div class="card-body">
                <h5 class="card-title fw-bold text-dark mb-1">{{ product.name }}</h5>
                <p class="text-muted small mb-3">{{ product.desc }}</p>
                
                <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                  <span class="price-tag fw-bold">{{ product.price }}</span>
                  <button class="btn btn-sm btn-primary rounded-circle btn-cart">
                    <i class="fas fa-plus"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="features py-5 bg-white">
      <div class="container">
        <div class="row text-center g-4">
          <div class="col-md-4">
            <div class="p-4 border rounded-3 feature-box">
              <i class="fas fa-truck-moving fa-3x text-primary mb-3"></i>
              <h5 class="fw-bold">Giao hàng tận công trình</h5>
              <p class="text-muted">Đội xe vận tải chuyên dụng, hỗ trợ vận chuyển toàn quốc nhanh chóng.</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="p-4 border rounded-3 feature-box">
              <i class="fas fa-shield-alt fa-3x text-primary mb-3"></i>
              <h5 class="fw-bold">Cam kết chính hãng</h5>
              <p class="text-muted">Sản phẩm đầy đủ chứng chỉ CO/CQ từ Hòa Phát, Việt Nhật, Pomina.</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="p-4 border rounded-3 feature-box">
              <i class="fas fa-tags fa-3x text-primary mb-3"></i>
              <h5 class="fw-bold">Giá gốc tại kho</h5>
              <p class="text-muted">Cạnh tranh nhất thị trường, chiết khấu cao cho đơn hàng dự án lớn.</p>
            </div>
          </div>
        </div>
      </div>
    </section>
    <Footer />

  </div>
</template>

<style scoped>
/* --- STYLE CHO BANNER --- */
.hero-banner {
  /* Màu xanh công nghiệp đậm (Deep Navy) kết hợp với màu xám thép */
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 60%, #334155 100%);
  min-height: 600px;
  position: relative;
  overflow: hidden;
}

/* Chữ Gradient màu kim loại */
.text-gradient {
  background: linear-gradient(to right, #fbbf24, #f59e0b);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-img {
  filter: drop-shadow(0 10px 20px rgba(0,0,0,0.3));
  /* Animation nhẹ nhàng */
  animation: slideIn 1s ease-out;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(50px); }
  to { opacity: 1; transform: translateX(0); }
}

.btn-action {
  transition: transform 0.2s;
}
.btn-action:hover {
  transform: translateY(-3px);
}

/* --- STYLE CHO SẢN PHẨM --- */
.products-section {
  background-color: #f1f5f9; /* Màu nền xám nhạt (Slate-100) */
}

.title-underline {
  width: 80px;
  height: 4px;
  background-color: #f59e0b; /* Màu vàng cam công nghiệp */
  margin-top: 8px;
}

.product-card {
  transition: all 0.3s ease;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border-radius: 8px; /* Bo góc ít hơn để tạo cảm giác cứng cáp */
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
  border-color: #f59e0b; /* Hover hiện viền cam */
}

.card-img-wrapper {
  position: relative;
  background: #e2e8f0;
  height: 200px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-img-top {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .card-img-top {
  transform: scale(1.1);
}

.badge-stock {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(16, 185, 129, 0.9); /* Màu xanh lá báo còn hàng */
  color: white;
  font-size: 0.7rem;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.price-tag {
  color: #b45309; /* Màu cam đất đậm */
  font-size: 1.1rem;
}

.btn-cart {
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* --- FEATURE BOX --- */
.feature-box {
  background: white;
  transition: 0.3s;
}
.feature-box:hover {
  border-color: #3b82f6 !important;
  box-shadow: 0 10px 20px rgba(59, 130, 246, 0.1);
}

/* Animation Text */
.animate-up {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 0.8s forwards;
}
.delay-1 { animation-delay: 0.2s; }
.delay-2 { animation-delay: 0.4s; }

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>
