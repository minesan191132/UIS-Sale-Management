<script setup>
import { ref } from 'vue';

// 1. Dữ liệu Địa điểm & Liên hệ (Lấy từ code của bạn)
const locations = ref([
  {
    name: "Văn phòng chính",
    address: "70 Xuân Thới Sơn 2B, Ấp 6, X. Xuân Thới Sơn, H. Hóc Môn, TP.HCM",
    contact: "Tel: 0914.771.440 | Hotline: (028) 38.112.449 <br> Email: upecvn@gmail.com",
  },
  {
    name: "Cơ sở sản xuất 1",
    address: "70 Xuân Thới Sơn 2B, Ấp 6, X. Xuân Thới Sơn, H. Hóc Môn, TP.HCM",
  },
  {
    name: "Cơ sở sản xuất 2",
    address: "257 Lê Thị Kim, Ấp 6, X. Xuân Thới Sơn, H. Hóc Môn, TP.HCM",
  },
]);

// 2. Dữ liệu Dịch vụ (Lấy từ mảng services của bạn để làm link)
const serviceLinks = ref([
  "Cơ khí xây dựng",
  "Xử lý nước thải",
  "Gia công cắt chấn kim loại",
  "Sản xuất nước uống",
]);

// 3. Hàm cuộn lên đầu trang
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};
</script>

<template>
  <footer class="industrial-footer text-white pt-5 position-relative">
    
    <button @click="scrollToTop" class="btn-scroll-top" title="Lên đầu trang">
      <i class="fas fa-arrow-up"></i>
    </button>

    <div class="container">
      <div class="row gy-4">
        
        <div class="col-lg-5 col-md-12">
          <div class="footer-brand mb-4">
            <div class="d-flex align-items-center gap-2 mb-2">
              <i class="fas fa-industry text-warning fa-2x"></i>
              <h5 class="fw-bold text-uppercase m-0 spacing-1">UPEC VIETNAM</h5>
            </div>
            <p class="text-white-50 small">Công ty TNHH Utsunomiya Industry Việt Nam</p>
          </div>

          <div class="contact-details">
            <div v-for="(loc, i) in locations" :key="i" class="location-item mb-4 border-start border-warning ps-3">
              <h6 class="text-warning fw-bold mb-1">
                {{ loc.name }}
              </h6>
              <p class="mb-1 text-gray-300 small">
                <i class="fas fa-map-marker-alt me-2 text-secondary"></i>{{ loc.address }}
              </p>
              <p v-if="loc.contact" class="mb-0 text-gray-300 small" v-html="loc.contact"></p>
            </div>
          </div>
        </div>

        <div class="col-lg-3 col-md-6">
          <h6 class="text-uppercase fw-bold mb-4 position-relative section-title">
            Lĩnh vực hoạt động
          </h6>
          <ul class="list-unstyled footer-links">
            <li v-for="(link, index) in serviceLinks" :key="index" class="mb-3">
              <a href="#" class="footer-link d-flex align-items-center">
                <i class="fas fa-caret-right me-2 text-warning"></i>
                {{ link }}
              </a>
            </li>
          </ul>
        </div>

        <div class="col-lg-4 col-md-6">
          <h6 class="text-uppercase fw-bold mb-4 position-relative section-title">
            Kết nối mạng xã hội
          </h6>
          
          <div class="social-card p-3 rounded-3 mb-4">
            <div class="d-flex align-items-center mb-3">
              <div class="fb-avatar me-3 bg-white d-flex align-items-center justify-content-center rounded-circle" style="width: 50px; height: 50px;">
                <i class="fas fa-thumbs-up text-primary fs-4"></i>
              </div>
              <div>
                <h6 class="mb-0 fw-bold text-white">Utsunomiya Industry VN</h6>
                <small class="text-white-50">Theo dõi tin tức mới nhất</small>
              </div>
            </div>
            <button class="btn btn-primary btn-sm w-100 fw-bold">
              <i class="fab fa-facebook-square me-2"></i>Ghé thăm Fanpage
            </button>
          </div>

          <div class="social-circles d-flex gap-2">
            <a href="#" class="social-btn btn-facebook"><i class="fab fa-facebook-f"></i></a>
            <a href="#" class="social-btn btn-zalo fw-bold">Zalo</a>
            <a href="#" class="social-btn btn-email"><i class="fas fa-envelope"></i></a>
          </div>
        </div>
      </div>
    </div>

    <div class="copyright-bar mt-5 py-3">
      <div class="container d-flex flex-column flex-md-row justify-content-between align-items-center">
        <p class="mb-0 small text-white-50">© 2024 Utsunomiya Industry Vietnam. All rights reserved.</p>
        <p class="mb-0 small text-white-50">Designed By Me</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
/* --- CẤU TRÚC CHUNG --- */
.industrial-footer {
  background-color: #0f172a; /* Nền tối (Slate 900) */
  color: #cbd5e1;
  font-size: 0.95rem;
  border-top: 4px solid #f59e0b; /* Viền cam (Amber 500) */
}

.text-gray-300 {
  color: #d1d5db;
}

.spacing-1 {
  letter-spacing: 1px;
}

/* --- TIÊU ĐỀ --- */
.section-title::after {
  content: '';
  display: block;
  width: 50px;
  height: 3px;
  background: #f59e0b;
  margin-top: 10px;
}

/* --- LIÊN KẾT (LINKS) --- */
.footer-link {
  color: #9ca3af;
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.footer-link:hover {
  color: #f59e0b; /* Hover chuyển màu cam */
  transform: translateX(5px);
}

/* --- SOCIAL CARD --- */
.social-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.social-btn {
  width: 40px;
  height: 40px;
  border-radius: 4px; /* Bo góc vuông nhẹ kiểu công nghiệp */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-decoration: none;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.social-btn:hover {
  transform: translateY(-3px);
  background-color: #f59e0b;
}

.btn-facebook:hover { background-color: #1877f2; }
.btn-zalo:hover { background-color: #0068ff; font-size: 0.75rem; }
.btn-email:hover { background-color: #ea4335; }

/* --- COPYRIGHT --- */
.copyright-bar {
  background-color: #020617; /* Đen hơn nền chính */
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

/* --- NÚT SCROLL TO TOP --- */
.btn-scroll-top {
  position: absolute;
  top: -25px;
  right: 50px;
  width: 50px;
  height: 50px;
  background-color: #f59e0b;
  color: #0f172a;
  border: none;
  border-radius: 4px;
  font-size: 1.2rem;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(245, 158, 11, 0.3);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-scroll-top:hover {
  background-color: #fff;
  color: #f59e0b;
  transform: translateY(-5px);
}
</style>