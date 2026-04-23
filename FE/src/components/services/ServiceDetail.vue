<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// Import ảnh không có số cho banner
import cokhiImg from '../../assets/picture/serviceimg/Cokhixaydung.jpg';
import xulynuocImg from '../../assets/picture/serviceimg/Xulynuocthai.png';
import giacongImg from '../../assets/picture/serviceimg/Giacongchankimloai.jpg';
import nuocuongImg from '../../assets/picture/serviceimg/Sanxuatnuocuong.png';

// Import ảnh có số cho phần giới thiệu
import cokhi1 from '../../assets/picture/serviceimg/Cokhixaydung1.png';
import cokhi2 from '../../assets/picture/serviceimg/Cokhixaydung2.png';
import xulynuoc1 from '../../assets/picture/serviceimg/Xulynuocthai1.png';
import giacong1 from '../../assets/picture/serviceimg/Giacongchankimloai1.png';
import giacong2 from '../../assets/picture/serviceimg/Giacongchankimloai2.png';
import nuocuong1 from '../../assets/picture/serviceimg/Sanxuatnuocuong1.webp';

const route = useRoute();
const router = useRouter();

// Dữ liệu chi tiết dịch vụ - Đơn giản hóa
const servicesData = {
  'co-khi-xay-dung': {
    id: 'co-khi-xay-dung',
    title: 'Cơ Khí Xây Dựng',
    description: 'Tư vấn, thiết kế, thi công lắp đặt các sản phẩm cơ khí xây dựng, hệ thống đường ống công nghiệp, kết cấu thép nhà xưởng đảm bảo tiêu chuẩn an toàn.',
    image: cokhiImg,
    icon: 'fas fa-industry',
    content: 'UIS Store cung cấp giải pháp cơ khí xây dựng toàn diện từ khâu tư vấn, thiết kế đến thi công và bảo trì. Với đội ngũ kỹ sư giàu kinh nghiệm và trang thiết bị hiện đại, chúng tôi cam kết mang đến những công trình đạt tiêu chuẩn quốc tế.',
    gallery: [cokhi1, cokhi2]
  },
  'xu-ly-nuoc-thai': {
    id: 'xu-ly-nuoc-thai',
    title: 'Xử Lý Nước Thải',
    description: 'Cung cấp giải pháp toàn diện về xử lý nước thải công nghiệp và sinh hoạt. Thiết kế hệ thống lọc, vận hành và bảo trì trạm xử lý nước.',
    image: xulynuocImg,
    icon: 'fas fa-water',
    content: 'UIS Store chuyên thiết kế và thi công hệ thống xử lý nước thải công nghiệp, sinh hoạt đạt tiêu chuẩn QCVN về môi trường. Sử dụng công nghệ tiên tiến từ Nhật Bản, chúng tôi giúp doanh nghiệp bảo vệ môi trường.',
    gallery: [xulynuoc1]
  },
  'gia-cong-cat-chan-kim-loai': {
    id: 'gia-cong-cat-chan-kim-loai',
    title: 'Gia Công Cắt Chấn Kim Loại',
    description: 'Gia công kim loại tấm chính xác cao bằng công nghệ Laser Fiber, chấn CNC, đột dập. Đáp ứng các chi tiết máy phức tạp.',
    image: giacongImg,
    icon: 'fas fa-cut',
    content: 'Với trang thiết bị tiên tiến gồm máy cắt Laser Fiber, máy chấn CNC, UIS Store cung cấp dịch vụ gia công kim loại chính xác cao. Chúng tôi có khả năng gia công inox, thép, nhôm, đồng với nhiều độ dày khác nhau.',
    gallery: [giacong1, giacong2]
  },
  'san-xuat-nuoc-uong': {
    id: 'san-xuat-nuoc-uong',
    title: 'Sản Xuất Nước Uống',
    description: 'Phát triển dây chuyền sản xuất nước uống đóng chai tinh khiết theo tiêu chuẩn Nhật Bản. Cung cấp nước uống chất lượng cao cho doanh nghiệp.',
    image: nuocuongImg,
    icon: 'fas fa-glass-water',
    content: 'UIS Store vận hành dây chuyền sản xuất nước uống đóng chai tinh khiết đạt tiêu chuẩn Nhật Bản với công nghệ lọc RO và hệ thống chiết rót vô trùng. Sản phẩm được kiểm định chặt chẽ, đảm bảo an toàn.',
    gallery: [nuocuong1]
  }
};

// Lấy thông tin dịch vụ hiện tại
const currentService = computed(() => {
  const slug = route.params.slug;
  return servicesData[slug] || null;
});

// Danh sách dịch vụ khác
const otherServices = computed(() => {
  const slug = route.params.slug;
  return Object.values(servicesData).filter(s => s.id !== slug);
});

// Cuộn lên đầu khi component mount
onMounted(() => {
  window.scrollTo(0, 0);
});

// Hàm chuyển sang dịch vụ khác
const goToService = (slug) => {
  router.push({ name: 'service-detail', params: { slug } });
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// Quay lại trang danh sách
const goBack = () => {
  router.push({ name: 'service' });
};
</script>

<template>
  <div class="service-detail-page">
    <Navbar />

    <!-- Không tìm thấy dịch vụ -->
    <div v-if="!currentService" class="container py-5 text-center">
      <div class="alert alert-warning alert-fit-content">
        <i class="fas fa-exclamation-triangle me-2"></i>
        Dịch vụ không tồn tại
      </div>
      <button @click="goBack" class="btn btn-primary">
        <i class="fas fa-arrow-left me-2"></i>Quay lại
      </button>
    </div>

    <!-- Nội dung dịch vụ -->
    <div v-else>
      <!-- Banner -->
      <div class="banner">
        <img :src="currentService.image" :alt="currentService.title">
        <div class="banner-overlay"></div>
        <div class="banner-text">
          <h1><i :class="currentService.icon"></i> {{ currentService.title }}</h1>
          <p>{{ currentService.description }}</p>
        </div>
      </div>

      <!-- Giới thiệu -->
      <section class="intro-section py-5">
        <div class="container">
        <div class="row align-items-center">
          <div :class="currentService.gallery.length === 2 ? 'col-lg-6' : 'col-lg-6'" class="mb-4 mb-lg-0">
            <div class="row g-3">
              <div 
                v-for="(img, index) in currentService.gallery" 
                :key="index" 
                :class="currentService.gallery.length === 2 ? 'col-12' : 'col-12'"
              >
                <div class="intro-image-wrapper">
                  <img :src="img" :alt="`${currentService.title} - ${index + 1}`" class="intro-image">
                  <div class="image-decoration"></div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-6">
            <div class="intro-content">
              <h2 class="mb-4">Giới Thiệu Dịch Vụ</h2>
              <p class="lead">{{ currentService.content }}</p>
              <div class="intro-features mt-4">
                <div class="feature-item">
                  <i class="fas fa-check-circle text-primary me-2"></i>
                  <span>Đội ngũ chuyên nghiệp, giàu kinh nghiệm</span>
                </div>
                <div class="feature-item">
                  <i class="fas fa-check-circle text-primary me-2"></i>
                  <span>Thiết bị hiện đại, công nghệ tiên tiến</span>
                </div>
                <div class="feature-item">
                  <i class="fas fa-check-circle text-primary me-2"></i>
                  <span>Tiêu chuẩn quốc tế, chất lượng đảm bảo</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        </div>
      </section>

      <!-- Call to Action -->
      <section class="cta text-white text-center py-5">
        <div class="container">
          <h2 class="mb-3">Quan Tâm Đến Dịch Vụ Này?</h2>
          <p class="mb-4">Liên hệ với chúng tôi để được tư vấn chi tiết</p>
          <div class="d-flex gap-3 justify-content-center flex-wrap">
            <a href="/contact" class="btn btn-light btn-lg">
              <i class="fas fa-phone me-2"></i>Liên Hệ Ngay
            </a>
            <button @click="goBack" class="btn btn-outline-light btn-lg">
              <i class="fas fa-arrow-left me-2"></i>Dịch Vụ Khác
            </button>
          </div>
        </div>
      </section>

      <!-- Dịch vụ khác -->
      <section v-if="otherServices.length > 0" class="other-services py-5">
        <div class="container">
          <h2 class="text-center mb-5">Dịch Vụ Khác</h2>
          <div class="row g-4">
            <div v-for="service in otherServices" :key="service.id" class="col-md-4">
              <div class="service-box" @click="goToService(service.id)">
                <div class="service-icon mb-3">
                  <i :class="service.icon"></i>
                </div>
                <h5>{{ service.title }}</h5>
                <p class="small text-muted">{{ service.description }}</p>
                <button class="btn btn-sm btn-outline-primary mt-2">
                  Xem chi tiết <i class="fas fa-arrow-right ms-1"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <Footer />
  </div>
</template>

<style scoped>
/* Banner */
.banner {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.7);
}

.banner-text {
  position: absolute;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  color: white;
  width: 90%;
  max-width: 800px;
}

.banner-text h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.banner-text p {
  font-size: 1.155rem;
  font-weight: 600;
  font-family: inherit;
  opacity: 0.9;
}

/* Intro section */
.intro-section {
  background: linear-gradient(to bottom, #ffffff 0%, #f8fafc 100%);
  position: relative;
}

.intro-image-wrapper {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.intro-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.intro-image-wrapper:hover .intro-image {
  transform: scale(1.05);
}

.image-decoration {
  position: absolute;
  bottom: -10px;
  right: -10px;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #0d6efd 0%, #1e3a8a 100%);
  border-radius: 15px;
  z-index: -1;
}

.intro-content h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  position: relative;
  display: inline-block;
}

.intro-content h2::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #0d6efd, #1e3a8a);
  border-radius: 2px;
}

.intro-features {
  margin-top: 2rem;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 1.05rem;
  font-weight: 500;
  color: #475569;
}

.feature-item i {
  font-size: 1.2rem;
}

.lead {
  font-size: 1.3125rem;
  font-weight: 600;
  font-family: inherit;
  line-height: 1.6;
  color: #475569;
}

/* CTA */
.cta {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
}

/* Other Services */
.service-box {
  padding: 2rem;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.service-box h5 {
  font-size: 1.3125rem;
  font-weight: 600;
  font-family: inherit;
}

.service-box .small {
  font-size: 0.9188rem;
  font-weight: 600;
  font-family: inherit;
}

.service-box:hover {
  border-color: #0d6efd;
  transform: translateY(-5px);
}

.service-icon {
  width: 60px;
  height: 60px;
  background: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #1e293b;
}

.service-box:hover .service-icon {
  background: #0d6efd;
  color: white;
}

/* Responsive */
@media (max-width: 768px) {
  .banner {
    height: 300px;
  }
  
  .banner-text h1 {
    font-size: 1.8rem;
  }
  
  .banner-text p {
    font-size: 1.05rem;
    font-weight: 600;
  }

  .intro-image {
    height: 250px;
  }

  .intro-content h2 {
    font-size: 1.5rem;
    margin-top: 1rem;
  }

  .feature-item {
    font-size: 0.95rem;
  }

  .lead {
    font-size: 1.1rem;
  }
}
</style>
