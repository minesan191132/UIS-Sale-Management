<script setup>
import { ref } from 'vue';

import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// IMPORT ẢNH TỪ THƯ MỤC ASSETS (BẠN HÃY SỬA LẠI TÊN FILE ẢNH CHO ĐÚNG VỚI ẢNH BẠN CÓ NHÉ)
import sp1Img from '../../assets/picture/homepageimg/FanGuard.jpg';
import sp2Img from '../../assets/picture/homepageimg/StrainerPipe.jpg';
import sp3Img from '../../assets/picture/homepageimg/PipeSupport.jpg';
import sp4Img from '../../assets/picture/homepageimg/CrossBrace.jpg';

// Dữ liệu 4 sản phẩm gia công tiêu biểu (Showroom)
const featuredProducts = ref([
  { 
    id: 1, 
    name: 'Lưới bảo vệ quạt công nghiệp', 
    desc: 'Gia công cắt lốc, đột dập CNC chính xác. Bề mặt sơn tĩnh điện chống gỉ sét, đảm bảo an toàn tối đa',
    price: 'Sản phẩm mẫu', 
    image: sp1Img 
  },
  { 
    id: 2, 
    name: 'Ống lọc công nghiệp', 
    desc: 'Chế tạo từ Inox SUS 304/316 dập lỗ chuẩn xác, chịu áp lực cao, chuyên dụng cho mảng xử lý nước thải',
    price: 'Sản phẩm mẫu', 
    image: sp2Img 
  },
  { 
    id: 3, 
    name: 'Giá đỡ đường ống', 
    desc: 'Gia công hàn kết cấu thép SS400 vững chắc, phủ sơn Epoxy bảo vệ. Chịu tải trọng lớn cho các cụm ống',
    price: 'Sản phẩm mẫu', 
    image: sp3Img 
  },
  { 
    id: 4, 
    name: 'Thanh giằng chéo / Thanh chống', 
    desc: 'Cắt xẻ phôi, chấn dập lỗ lắp ghép đồng đều. Sử dụng thép mạ kẽm gia tăng độ cứng vững cho khung nhà xưởng',
    price: 'Sản phẩm mẫu', 
    image: sp4Img 
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
            <!-- <span class="badge bg-warning text-dark mb-3 px-3 py-2 fw-bold">CHẤT LƯỢNG JIS</span> -->
            <h1 class="display-4 fw-bold mb-3 animate-up text-uppercase">
              Gia Công Chính Xác<br>
              <span class="text-gradient">Tiêu Chuẩn Nhật Bản</span>
            </h1>
            <p class="lead mb-4 text-white-50 animate-up delay-1">
              Đối tác tin cậy chuyên cung cấp giải pháp gia công cơ khí, cắt chấn kim loại và xử lý nước thải cho doanh nghiệp.
            </p>
          </div>
        </div>
      </div>
    </section>

    <section class="products-section py-5">
      <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-5">
          <div>
            <h6 class="text-primary fw-bold text-uppercase mb-2">Năng Lực Gia Công & Chế Tạo
</h6>
            <h2 class="fw-bold text-dark">Sản phẩm chủ lực</h2>
            <div class="title-underline"></div>
          </div>
          <router-link to="/products" class="btn btn-link text-decoration-none fw-bold">Xem kho vật tư <i class="fas fa-arrow-right"></i></router-link>
        </div>

        <div class="row g-4">
          <div v-for="product in featuredProducts" :key="product.id" class="col-12 col-sm-6 col-lg-3">
            <div class="card product-card h-100 border-0">
              <div class="card-img-wrapper">
                <img :src="product.image" class="card-img-top" :alt="product.name">
              </div>
              
              <div class="card-body d-flex flex-column">
                <h5 class="card-title fw-bold text-dark mb-2" style="display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; min-height: 2.5rem;">
                  {{ product.name }}
                </h5>
                
                <p class="text-muted small mb-3">{{ product.desc }}</p>
                
                <div class="mt-auto d-flex justify-content-between align-items-center pt-3 border-top">
                  <span class="price-tag fw-bold text-secondary">{{ product.price }}</span>
                  <router-link to="/contact" class="btn btn-sm btn-outline-primary rounded-pill px-3 fw-bold">
                    Liên hệ
                  </router-link>
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
            <div class="p-4 border rounded-0 feature-box h-100">
              <i class="fas fa-cogs fa-3x text-primary mb-3"></i>
              <h5 class="fw-bold text-uppercase">Công nghệ hiện đại</h5>
              <p class="text-muted">Hệ thống máy CNC, cắt Laser Fiber và chấn gập công nghệ cao, đáp ứng dung sai khắt khe.</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="p-4 border rounded-0 feature-box h-100">
              <i class="fas fa-shield-alt fa-3x text-primary mb-3"></i>
              <h5 class="fw-bold text-uppercase">Kiểm định QC 100%</h5>
              <p class="text-muted">Mọi sản phẩm gia công đều trải qua khâu kiểm tra chất lượng nghiêm ngặt trước khi xuất xưởng.</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="p-4 border rounded-0 feature-box h-100">
              <i class="fas fa-handshake fa-3x text-primary mb-3"></i>
              <h5 class="fw-bold text-uppercase">Dịch vụ B2B chuyên nghiệp</h5>
              <p class="text-muted">Hỗ trợ tư vấn kỹ thuật, báo giá nhanh chóng và cung cấp chứng chỉ vật liệu (CO/CQ) đầy đủ.</p>
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
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 60%, #334155 100%);
  min-height: 600px;
  position: relative;
  overflow: hidden;
}

.text-gradient {
  background: linear-gradient(to right, #fbbf24, #f59e0b);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.animate-up {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 0.8s forwards;
}
.delay-1 { animation-delay: 0.2s; }

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

/* --- STYLE CHO SẢN PHẨM --- */
.products-section {
  background-color: #f8fafc; 
}

.title-underline {
  width: 80px;
  height: 4px;
  background-color: #f59e0b; 
  margin-top: 8px;
}

.product-card {
  transition: all 0.3s ease;
  background: white;
  border: 1px solid rgba(0,0,0,0.08) !important;
  border-radius: 0; /* Bo góc 0 để hợp với phong cách B2B Cơ khí */
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05) !important;
  border-color: #0b2e59 !important; 
}

.card-img-wrapper {
  position: relative;
  background: #f1f5f9;
  height: 220px;
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
  transform: scale(1.08);
}

/* --- FEATURE BOX --- */
.feature-box {
  background: white;
  transition: 0.3s;
  border-color: rgba(0,0,0,0.1) !important;
}
.feature-box:hover {
  border-color: #0b2e59 !important;
  box-shadow: 0 10px 20px rgba(11, 46, 89, 0.05);
}
</style>