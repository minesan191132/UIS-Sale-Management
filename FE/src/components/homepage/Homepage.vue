<script setup>
import { ref, onMounted } from 'vue';

import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// IMPORT ẢNH TỪ THƯ MỤC ASSETS
import sp1Img from '../../assets/picture/homepageimg/FanGuard.jpg';
import sp2Img from '../../assets/picture/homepageimg/StrainerPipe.jpg';
import sp3Img from '../../assets/picture/homepageimg/PipeSupport.jpg';
import sp4Img from '../../assets/picture/homepageimg/CrossBrace.jpg';

// Thêm 2 biến này vào dưới mảng dữ liệu để điều khiển Popup
const selectedProduct = ref(null);
const showModal = ref(false);

const openDetail = (product) => {
  selectedProduct.value = product;
  showModal.value = true;
  document.body.style.overflow = 'hidden'; // Khóa cuộn trang nền
};

const closeModal = () => {
  showModal.value = false;
  setTimeout(() => { selectedProduct.value = null; }, 300); // Đợi hiệu ứng đóng xong mới xóa data
  document.body.style.overflow = 'auto'; // Mở lại cuộn trang
};

// DỮ LIỆU ĐÃ ĐƯỢC NÂNG CẤP CHUẨN KỸ THUẬT
const featuredProducts = ref([
  { 
    id: 1, 
    name: 'Lưới bảo vệ quạt công nghiệp', 
    desc: 'Gia công hàn lưới thép định hình, cắt chấn CNC khung bảo vệ với độ chính xác cao.',
    image: sp1Img,
    materials: ['Thép đen (SS400)', 'Inox SUS 304', 'Dây thép mạ kẽm'],
    specs: 'Đường kính 300mm - 1500mm. Dung sai: ±0.5mm',
    processes: [
      { name: 'Cắt phôi Laser', icon: 'fas fa-laser' },
      { name: 'Uốn định hình vòng CNC', icon: 'fas fa-sync' },
      { name: 'Hàn chập / Hàn TIG Robot', icon: 'fas fa-fire' },
      { name: 'Sơn tĩnh điện chống gỉ', icon: 'fas fa-paint-roller' }
    ]
  },
  { 
    id: 2, 
    name: 'Ống lọc khe công nghiệp', 
    desc: 'Chế tạo ống lọc lưới inox chuyên dụng cho xử lý nước thải, công nghiệp thực phẩm, hóa chất.',
    image: sp2Img,
    materials: ['Inox SUS 304', 'Inox SUS 316L (Chống ăn mòn)'],
    specs: 'Khe hở lọc: 0.1mm - 5mm. Đường kính: 50mm - 500mm',
    processes: [
      { name: 'Đột dập khe rãnh siêu nhỏ', icon: 'fas fa-microchip' },
      { name: 'Cuộn tròn hình trụ CNC', icon: 'fas fa-circle-notch' },
      { name: 'Hàn Argon (TIG) vát mép', icon: 'fas fa-fire' },
      { name: 'Đánh bóng điện hóa (Electropolishing)', icon: 'fas fa-sparkles' }
    ]
  },
  { 
    id: 3, 
    name: 'Giá đỡ hệ thống đường ống', 
    desc: 'Gia công kết cấu thép chịu tải trọng cao cho các hệ thống ống dẫn khí, dầu, nước nhà máy.',
    image: sp3Img,
    materials: ['Thép hình (U, I, V) SS400', 'Thép tấm cường độ cao'],
    specs: 'Khả năng chịu tải: 1 Tấn - 50 Tấn/Giá đỡ',
    processes: [
      { name: 'Cắt Plasma / Cưa vòng', icon: 'fas fa-cut' },
      { name: 'Phay CNC bề mặt gối đỡ', icon: 'fas fa-cogs' },
      { name: 'Hàn kết cấu (MIG/MAG) siêu âm', icon: 'fas fa-fire-alt' },
      { name: 'Mạ kẽm nhúng nóng (HDG)', icon: 'fas fa-shield-alt' }
    ]
  },
  { 
    id: 4, 
    name: 'Hệ giằng chéo khung nhà xưởng', 
    desc: 'Gia công tự động hàng loạt thanh giằng chéo, thanh chống cho kết cấu thép tiền chế.',
    image: sp4Img,
    materials: ['Thép chữ C', 'Thép ống mạ kẽm', 'Thép góc (V)'],
    specs: 'Chiều dài tối đa: 12 mét. Dung sai tâm lỗ: ±0.2mm',
    processes: [
      { name: 'Cắt xẻ phôi theo quy cách', icon: 'fas fa-ruler-combined' },
      { name: 'Đột dập lỗ bulong tự động', icon: 'fas fa-compress-arrows-alt' },
      { name: 'Chấn gập định hình', icon: 'fas fa-layer-group' },
      { name: 'Kiểm tra dung sai lỗ & Đóng gói', icon: 'fas fa-check-double' }
    ]
  },
]);

// ==========================================
// 4. DỮ LIỆU: GIÁ TRỊ CỐT LÕI (CHUẨN U-PEC)
// ==========================================
const realUpecCoreValues = ref([
  { 
    titleVi: 'Chất lượng và hiệu quả', 
    titleEn: 'QUALITY AND EFFICIENCY', 
    desc: 'UIS STORE luôn cam kết cung cấp các dịch vụ gia công cơ khí, cắt, chấn, dập kim loại tấm chất lượng cao, hiệu quả, tiết kiệm chi phí cho khách hàng.', 
    icon: 'fas fa-award' 
  },
  { 
    titleVi: 'Tuân thủ pháp luật', 
    titleEn: 'LEGAL COMPLIANCE', 
    desc: 'UIS STORE hoạt động tuân thủ mọi quy định của pháp luật hiện hành liên quan.', 
    icon: 'fas fa-balance-scale' 
  },
  { 
    titleVi: 'Bảo vệ môi trường', 
    titleEn: 'ENVIRONMENTAL PROTECTION', 
    desc: 'Sản phẩm của UIS STORE được tạo ra bằng phương pháp và công nghệ tối ưu nhất nhằm bảo vệ môi trường, phát triển bền vững.', 
    icon: 'fas fa-leaf' 
  },
  { 
    titleVi: 'Đóng góp cho cộng đồng', 
    titleEn: 'COMMUNITY CONTRIBUTION', 
    desc: 'UIS STORE luôn gắn kết sự phát triển bền vững của doanh nghiệp với trách nhiệm xã hội, tạo thêm nhiều giá trị tích cực cho cộng đồng.', 
    icon: 'fas fa-hand-holding-heart' 
  },
]);

// DỮ LIỆU CHO 4 Ô TRONG PHẦN GIỚI THIỆU CÔNG TY (Lấy ý từ Tầm nhìn, Sứ mệnh, Triết lý)
const coreValues = ref([
  { 
    title: 'Tầm Nhìn', 
    desc: 'Trở thành thương hiệu đáng tin cậy, phát triển đúng hướng để đáp ứng trọn vẹn nhu cầu của đối tác.', 
    icon: 'fas fa-eye' 
  },
  { 
    title: 'Sứ Mệnh', 
    desc: 'Đóng góp cho hành tinh nước, hướng tới mục tiêu chung xây dựng "Tương lai của trái đất xanh".', 
    icon: 'fas fa-globe-asia' 
  },
  { 
    title: 'Triết Lý Cơ Bản', 
    desc: 'Làm việc bằng cả trái tim. Luôn kiên định đi theo con đường đúng đắn và chính trực.', 
    icon: 'fas fa-heart' 
  },
  { 
    title: 'Giá Trị Nhân Văn', 
    desc: 'Trân trọng con người, luôn suy nghĩ tích cực và không ngừng nuôi dưỡng "trái tim làm người".', 
    icon: 'fas fa-seedling' 
  }
]);

// Ảnh Slideshow (Khai báo import rõ ràng để Vue nhận diện được ảnh)
import slide1 from '../../assets/picture/homepageimg/gia-cong-co-khi-chinh-xac-2.jpg';
import slide2 from '../../assets/picture/homepageimg/gia-cong-co-khi-chinh-xac-3.jpg';
import slide3 from '../../assets/picture/homepageimg/gia-cong-co-khi-chinh-xac-4.jpg';

// Mảng Slideshow sử dụng các biến đã import ở trên
const factoryImages = ref([slide1, slide2, slide3]);
const currentImageIndex = ref(0);

// ==========================================
// 3. HIỆU ỨNG KHI TRANG LOAD LÊN
// ==========================================
onMounted(() => {
  // Cuộn chuột tới đâu hiện tới đó
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('show');
      }
    });
  }, { threshold: 0.1 });

  const hiddenElements = document.querySelectorAll('.reveal');
  hiddenElements.forEach((el) => observer.observe(el));

  // Chạy Slideshow chuyển ảnh (4 giây/lần)
  setInterval(() => {
    currentImageIndex.value = (currentImageIndex.value + 1) % factoryImages.value.length;
  }, 4000);
});

// Hiệu ứng cuộn chuột (Scroll Animation)
onMounted(() => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('show');
      }
    });
  }, { threshold: 0.1 });

  const hiddenElements = document.querySelectorAll('.reveal');
  hiddenElements.forEach((el) => observer.observe(el));
});

// Hàm cuộn mượt xuống
const scrollToCapabilities = () => {
  const el = document.getElementById('nang-luc-gia-cong');
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
};
</script>

<template>
  <div class="home-page">
    <Navbar />
    
    <section class="hero-banner d-flex align-items-center">
      <div class="container position-relative z-1">
        <div class="row">
          <div class="col-lg-8">
            <h1 class="display-3 fw-bold mb-4 text-white text-uppercase reveal fade-bottom hero-text-shadow" style="line-height: 1.2;">
              Gia Công Chính Xác<br>
              <span class="text-gold">Tiêu Chuẩn Nhật Bản</span>
            </h1>
            <p class="lead mb-5 text-light opacity-75 reveal fade-bottom delay-1" style="max-width: 600px;">
              Gia công theo bản vẽ kỹ thuật và cung cấp sản phẩm phôi có sẵn cho doanh nghiệp sản xuất cần tiến độ nhanh, kiểm soát chất lượng chặt chẽ.
            </p>
            <div class="d-flex flex-wrap gap-2 gap-md-3 mb-4 reveal fade-bottom delay-1 hero-pill-list">
              <span class="hero-pill">Gia công theo bản vẽ</span>
              <span class="hero-pill">Sản phẩm phôi có sẵn</span>
            </div>
            <div class="d-flex flex-wrap gap-3 reveal fade-bottom delay-2 hero-actions">
              <router-link to="/create-order" class="btn btn-gold btn-lg fw-bold rounded-1 px-4 py-3 text-uppercase shadow-sm">
                Đặt Gia Công Ngay <i class="fas fa-arrow-right ms-2"></i>
              </router-link>
              <router-link to="/products" class="btn btn-outline-light btn-lg fw-bold rounded-1 px-4 py-3 text-uppercase">
                Xem Sản Phẩm Phôi
              </router-link>
              <router-link to="/contact" class="btn btn-outline-light btn-lg fw-bold rounded-1 px-4 py-3 text-uppercase">
                Liên Hệ Tư Vấn
              </router-link>
            </div>
            <div class="mt-3 reveal fade-bottom delay-2">
              <button @click="scrollToCapabilities" class="btn btn-link hero-scroll-link p-0 fw-semibold">
                Xem năng lực gia công <i class="fas fa-arrow-down ms-1"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="about-section py-5 bg-white position-relative overflow-hidden border-bottom border-light">
      <div class="container py-5 position-relative z-1">
        <div class="row align-items-center g-5">
          
          <div class="col-lg-6 reveal fade-right order-lg-2">
            <div class="about-image-wrapper position-relative p-3 p-md-4 mt-4 mt-lg-0">
              <div class="image-backdrop bg-navy rounded-3 position-absolute"></div>
              
              <div class="overflow-hidden rounded-3 shadow-lg position-relative z-1 slideshow-container" style="height: 420px;">
                <img v-for="(img, index) in factoryImages" 
                     :key="index" 
                     :src="img" 
                     alt="Nhà máy UIS" 
                     class="img-fluid slideshow-img"
                     :class="{ 'active': index === currentImageIndex }">
              </div>

              <div class="floating-badge bg-navy p-3 p-md-4 shadow-lg rounded-3 position-absolute z-2 d-flex align-items-center gap-3 border-start border-gold border-4">
                <i class="fas fa-handshake-alt display-4 text-gold mb-0"></i>
                <div class="text-start">
                  <h5 class="fw-bold text-white mb-1 text-uppercase">Sự An Tâm Tuyệt Đối</h5>
                  <p class="text-light opacity-75 small mb-0">Đúng Dung Sai - Đúng Hẹn - Chuẩn Chất Lượng</p>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-6 reveal fade-left order-lg-1">
            <h6 class="text-gold fw-bold text-uppercase mb-2 letter-spacing-1">Giới Thiệu UIS Store</h6>
            <h2 class="fw-bold text-navy mb-4 text-uppercase" style="font-size: 2.5rem; line-height: 1.2;">Nền Tảng Cơ Khí<br>Chất Lượng Vượt Trội</h2>
            <div class="title-underline mb-4"></div>
            
            <p class="text-muted mb-4 fs-6" style="line-height: 1.8; text-align: justify; max-width: 600px;">
              Với kinh nghiệm nhiều năm trong ngành công nghiệp phụ trợ, <strong>UIS Store</strong> tự hào sở hữu hệ thống nhà xưởng quy mô lớn, trang bị máy móc CNC, Laser Fiber và chấn gập hiện đại nhất. Chúng tôi không chỉ cung cấp vật tư thép tiêu chuẩn mà còn nhận gia công bóc tách bản vẽ theo đúng yêu cầu kỹ thuật khắt khe của các đối tác Nhật Bản và FDI tại Việt Nam.
            </p>
            
            <div class="row g-4 mt-2">
              <div v-for="(value, index) in coreValues" :key="index" class="col-sm-6">
                <div class="core-value-card border p-3 rounded-3 h-100 d-flex flex-column align-items-start shadow-sm position-relative overflow-hidden bg-light">
                  <div class="bg-card-wave position-absolute text-navy" style="opacity: 0.03; bottom: -15px; right: -15px; z-index: 0; font-size: 6rem;">
                    <i :class="value.icon"></i>
                  </div>
                  <div class="icon-box-small mb-3 bg-white text-navy rounded-circle shadow-sm d-flex align-items-center justify-content-center position-relative z-1" style="width: 50px; height: 50px;">
                    <i :class="value.icon" class="fs-5"></i>
                  </div>
                  <h6 class="fw-bold text-navy mb-2 position-relative z-1">{{ value.title }}</h6>
                  <p class="text-muted small mb-0 flex-grow-1 position-relative z-1">{{ value.desc }}</p>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </section>

    <section id="nang-luc-gia-cong" class="products-section py-5 bg-silver">
      <div class="container py-5">
        
        <div class="text-center mb-5 reveal fade-bottom">
          <h6 class="text-gold fw-bold text-uppercase mb-2 letter-spacing-1">Sản phẩm tiêu biểu</h6>
          <h2 class="fw-bold text-navy text-uppercase mb-3" style="font-size: 2.5rem;">Năng lực gia công & Chế tạo</h2>
          <div class="title-underline mx-auto bg-gold"></div>
          <p class="text-muted mt-3 mx-auto" style="max-width: 700px;">
            Các dự án dưới đây thể hiện độ phức tạp trong chế tạo, từ khâu chọn phôi, gia công CNC đến hoàn thiện bề mặt mà UIS STORE đã thực hiện.
          </p>
        </div>

        <div class="row g-4">
          <div v-for="(product, index) in featuredProducts" :key="product.id" class="col-12 col-sm-6 col-lg-3 reveal fade-bottom" :style="`transition-delay: ${index * 0.1}s`">
            <div class="card product-card h-100 bg-white border-0 shadow-sm">
              <div class="card-img-wrapper">
                <img :src="product.image" class="card-img-top" :alt="product.name">
                <div class="overlay-action">
                  <button @click="openDetail(product)" class="btn btn-gold btn-sm fw-bold px-3 py-2 text-uppercase rounded-1">
                    <i class="fas fa-search-plus me-1"></i> Chi tiết gia công
                  </button>
                </div>
              </div>
              <div class="card-body p-4 d-flex flex-column text-center">
                <h5 class="card-title fw-bold text-navy mb-2 product-title">
                  {{ product.name }}
                </h5>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="features py-5 bg-navy text-white position-relative overflow-hidden">
      <div class="bg-watermark position-absolute w-100 h-100 top-0 start-0" style="background-image: url('https://images.unsplash.com/photo-1504917595217-d4dc5ebe6122?auto=format&fit=crop&q=80'); opacity: 0.05; background-size: cover; background-position: center; mix-blend-mode: luminosity;"></div>
      
      <div class="container py-5 position-relative z-1">
        <div class="text-center mb-5 reveal fade-bottom">
          <h6 class="text-gold fw-bold text-uppercase mb-2 letter-spacing-1">Vì sao chọn UIS Store?</h6>
          <h2 class="fw-bold text-white text-uppercase" style="font-size: 2.8rem;">Giá Trị Cốt Lõi</h2>
          <div class="title-underline mx-auto mt-3 bg-gold"></div>
        </div>

        <div class="row text-center g-4 justify-content-center">
          <div v-for="(val, idx) in realUpecCoreValues" :key="idx" class="col-sm-6 col-lg-3 reveal fade-bottom" :style="`transition-delay: ${idx * 0.1}s`">
            
            <div class="p-4 h-100 feature-box d-flex flex-column align-items-center rounded-3" style="border: 1px solid rgba(255,255,255,0.1); background: rgba(255,255,255,0.02);">
              
              <div class="icon-circle mx-auto mb-4" style="width: 80px; height: 80px; border-radius: 50%; background: rgba(245, 158, 11, 0.1); border: 1px solid rgba(245, 158, 11, 0.3); display: flex; align-items: center; justify-content: center;">
                <i :class="val.icon" class="fa-2x text-gold"></i>
              </div>
              
              <h6 class="fw-bold text-gold opacity-75 small text-uppercase letter-spacing-1 mb-1">{{ val.titleEn }}</h6>
              <h5 class="fw-bold text-white text-uppercase mb-3" style="font-size: 1.1rem;">{{ val.titleVi }}</h5>
              <p class="text-light opacity-75 small mb-0" style="line-height: 1.6;">{{ val.desc }}</p>
              
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <section class="cta-section py-5 bg-silver border-top">
      <div class="container text-center py-5 reveal fade-bottom">
        <h2 class="fw-bold text-navy mb-3 text-uppercase">Bắt Đầu Dự Án Của Bạn</h2>
        <p class="text-secondary mb-5 fs-5">Hãy chọn luồng dịch vụ phù hợp với nhu cầu hiện tại của doanh nghiệp bạn.</p>
        
        <div class="row justify-content-center g-4">
          <div class="col-md-5 col-lg-4">
            <div class="bg-white p-4 rounded-2 shadow-sm h-100 d-flex flex-column align-items-center justify-content-center border-top border-gold border-4 hover-card">
              <i class="fas fa-file-signature fa-3x text-navy mb-3"></i>
              <h5 class="fw-bold text-dark mb-2">Gia Công Theo Bản Vẽ</h5>
              <p class="text-muted small mb-4">Upload bản vẽ CAD/Excel để đội ngũ kỹ sư bóc tách và báo giá chi tiết.</p>
              <router-link to="/create-order" class="btn btn-outline-navy w-100 fw-bold py-3 text-uppercase mt-auto">
                Gửi Yêu Cầu Gia Công
              </router-link>
            </div>
          </div>

          <div class="col-md-5 col-lg-4">
            <div class="bg-white p-4 rounded-2 shadow-sm h-100 d-flex flex-column align-items-center justify-content-center border-top border-gold border-4 hover-card">
              <i class="fas fa-cubes fa-3x text-navy mb-3"></i>
              <h5 class="fw-bold text-dark mb-2">Mua Sản Phẩm Phôi Có Sẵn</h5>
              <p class="text-muted small mb-4">Lựa chọn các loại thép hình, thép tấm, linh kiện đạt chuẩn từ kho của chúng tôi.</p>
              <router-link to="/products" class="btn btn-outline-navy w-100 fw-bold py-3 text-uppercase mt-auto">
                Xem Sản Phẩm Phôi
              </router-link>
            </div>
          </div>
        </div>

      </div>
    </section>

    <div class="capability-modal" :class="{ 'active': showModal }" @click.self="closeModal">
      <div class="modal-dialog-custom bg-white rounded-3 shadow-lg overflow-hidden d-flex flex-column flex-lg-row">
        <button class="btn-close-modal" @click="closeModal"><i class="fas fa-times"></i></button>

        <div class="modal-image-col bg-light d-flex align-items-center justify-content-center p-4">
          <img v-if="selectedProduct" :src="selectedProduct.image" :alt="selectedProduct.name" class="img-fluid rounded shadow-sm border">
        </div>

        <div class="modal-info-col p-4 p-lg-5 d-flex flex-column overflow-auto">
          <div v-if="selectedProduct">
            <h3 class="fw-bold text-navy mb-3 text-uppercase border-bottom border-warning border-3 pb-2 d-inline-block">{{ selectedProduct.name }}</h3>
            <p class="text-muted mb-4">{{ selectedProduct.desc }}</p>

            <div class="mb-4 bg-light p-3 rounded border-start border-navy border-4">
              <h6 class="fw-bold text-dark mb-2"><i class="fas fa-cube text-gold me-2"></i> Vật liệu ứng dụng:</h6>
              <ul class="text-muted mb-0 small ps-4">
                <li v-for="(mat, idx) in selectedProduct.materials" :key="idx">{{ mat }}</li>
              </ul>
            </div>

            <div class="mb-4">
              <h6 class="fw-bold text-dark mb-2"><i class="fas fa-ruler-combined text-gold me-2"></i> Quy cách / Dung sai:</h6>
              <p class="text-muted small m-0">{{ selectedProduct.specs }}</p>
            </div>

            <div>
              <h6 class="fw-bold text-dark mb-3"><i class="fas fa-cogs text-gold me-2"></i> Quy trình gia công chuẩn:</h6>
              <div class="process-timeline ps-3 border-start border-2 border-warning">
                <div v-for="(step, idx) in selectedProduct.processes" :key="idx" class="process-step position-relative mb-3 ps-3">
                  <div class="step-dot bg-navy text-white d-flex align-items-center justify-content-center position-absolute rounded-circle">
                    <i :class="step.icon" style="font-size: 0.7rem;"></i>
                  </div>
                  <span class="text-dark small fw-bold">Bước {{ idx + 1 }}:</span> <span class="text-muted small">{{ step.name }}</span>
                </div>
              </div>
            </div>

            <div class="mt-5 text-end">
              <router-link to="/create-order" class="btn btn-navy fw-bold text-uppercase px-4 py-2 rounded-1">
                Gửi bản vẽ yêu cầu báo giá <i class="fas fa-arrow-right ms-2"></i>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<style scoped>
/* BẢNG MÀU CHỦ ĐẠO MỚI */
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.text-gold { color: #f59e0b !important; }
.bg-gold { background-color: #f59e0b !important; }
.bg-silver { background-color: #f8fafc !important; } 

.letter-spacing-1 { letter-spacing: 1px; }

/* NÚT BẤM */
.btn-gold {
  background-color: #f59e0b; color: #fff; border: 1px solid #f59e0b; transition: all 0.3s ease;
}
.btn-gold:hover { background-color: #d97706; border-color: #d97706; color: #fff; }

.btn-navy {
  background-color: #0b2e59; color: #fff; border: 1px solid #0b2e59; transition: all 0.3s ease;
}
.btn-navy:hover { background-color: #1e3a8a; color: #fff; }

.btn-outline-navy {
  background-color: transparent; color: #0b2e59; border: 2px solid #0b2e59; transition: all 0.3s ease;
}
.btn-outline-navy:hover { background-color: #0b2e59; color: #fff; }

/* ================================================= */
/* HERO BANNER - CÓ ẢNH NỀN MỜ MỜ (WATERMARK)        */
/* ================================================= */
.hero-banner {
  position: relative;
  min-height: 85vh;
  /* Màu nền chủ đạo (Xanh đậm) */
  background: linear-gradient(135deg, #0b2e59 0%, #173b6c 50%, #1e4b85 100%);
  overflow: hidden;
}

.hero-text-shadow {
  text-shadow: 2px 4px 10px rgba(0, 0, 0, 0.6);
}

.hero-pill-list {
  max-width: 640px;
}

.hero-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.08);
  color: #f8fafc;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.hero-scroll-link {
  color: #bfdbfe;
  text-decoration: none;
}

.hero-scroll-link:hover {
  color: #dbeafe;
}

/* Đây chính là lớp ảnh in chìm (Mờ mờ) nằm phía sau chữ */
.hero-banner::before {
  content: "";
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: url('../../assets/picture/HomePageimg/gia-cong-co-khi-chinh-xac.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed; /* Parallax effect */
  opacity: 0.3; /* Làm mờ ảnh, chỉ để lại bóng mờ như Watermark */
  filter: grayscale(100%) blur(0px); /* Đổi sang trắng đen và làm hơi nhòe đi */
  z-index: 0;
}

@media (max-width: 767.98px) {
  .hero-actions .btn {
    width: 100%;
  }
}

/* ================================================= */
/* SẢN PHẨM & NĂNG LỰC                               */
/* ================================================= */
.title-underline { width: 80px; height: 4px; background-color: #f59e0b; }

.product-card {
  border: 1px solid #e2e8f0; border-radius: 4px; transition: all 0.4s ease; position: relative;
}
.product-card:hover {
  transform: translateY(-8px); box-shadow: 0 15px 30px rgba(11,46,89,0.1); border-color: #cbd5e1;
}

.card-img-wrapper {
  position: relative; height: 240px; overflow: hidden; border-radius: 4px 4px 0 0; background-color: #f1f5f9;
}
.card-img-top {
  width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s ease;
}
.product-card:hover .card-img-top { transform: scale(1.1); }

.overlay-action {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(11,46,89,0.7); display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.3s ease;
}
.product-card:hover .overlay-action { opacity: 1; }

.product-title {
  display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical;
  overflow: hidden; min-height: 2.8rem; transition: color 0.2s;
}
.product-card:hover .product-title { color: #f59e0b !important; }

/* ================================================= */
/* FEATURE BOX & CTA                                 */
/* ================================================= */
.feature-box {
  border: 1px solid rgba(255,255,255,0.05); border-radius: 4px; background: rgba(255,255,255,0.02); transition: all 0.3s ease;
}
.feature-box:hover {
  background: rgba(255,255,255,0.05); transform: translateY(-5px); border-color: #f59e0b;
}

.icon-circle {
  width: 80px; height: 80px; border-radius: 50%; background: rgba(245, 158, 11, 0.1);
  display: flex; align-items: center; justify-content: center; border: 1px solid rgba(245, 158, 11, 0.3);
}

.hover-card { transition: all 0.3s ease; }
.hover-card:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(0,0,0,0.1) !important; }

/* ================================================= */
/* ANIMATION CLASSES (HIỆU ỨNG CUỘN CHUỘT)           */
/* ================================================= */
.reveal { opacity: 0; transition: all 0.8s cubic-bezier(0.5, 0, 0, 1); }
.reveal.show { opacity: 1; transform: translate(0, 0); }
.fade-bottom { transform: translateY(40px); }
.fade-left { transform: translateX(40px); }
.fade-right { transform: translateX(-40px); }
.delay-1 { transition-delay: 0.2s; }
.delay-2 { transition-delay: 0.4s; }

/* ================================================= */
/* MODAL NĂNG LỰC GIA CÔNG CỰC CHẤT                  */
/* ================================================= */
.capability-modal {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(11, 46, 89, 0.85); /* Nền mờ xanh navy */
  backdrop-filter: blur(5px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  padding: 20px;
}

.capability-modal.active {
  opacity: 1;
  visibility: visible;
}

.modal-dialog-custom {
  width: 100%;
  max-width: 1000px;
  max-height: 90vh;
  transform: translateY(30px);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
}

.capability-modal.active .modal-dialog-custom {
  transform: translateY(0);
}

.btn-close-modal {
  position: absolute;
  top: 15px; right: 15px;
  background: #f8d7da;
  color: #dc3545;
  border: none;
  width: 35px; height: 35px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.2rem;
  z-index: 10;
  transition: all 0.2s;
}
.btn-close-modal:hover { background: #dc3545; color: #fff; transform: rotate(90deg); }

.modal-image-col { width: 100%; flex: 1; }
.modal-info-col { width: 100%; flex: 1.2; }

@media (min-width: 992px) {
  .modal-image-col { width: 45%; }
  .modal-info-col { width: 55%; }
}

.process-step .step-dot {
  width: 24px; height: 24px;
  left: -13px; top: 0;
  border: 3px solid #fff;
}

/* ========================================== */
/* PHẦN GIỚI THIỆU (SLIDESHOW & CORE VALUES)  */
/* ========================================== */

/* Card Core Values mới (Bố cục 2x2) */
.core-value-card {
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent !important;
}
.core-value-card:hover {
  transform: translateY(-5px);
  border-bottom-color: #f59e0b !important;
  box-shadow: 0 10px 20px rgba(11, 46, 89, 0.1) !important;
}
.core-value-card:hover .icon-box-small {
  background-color: #0b2e59 !important;
  color: #fff !important;
  transition: all 0.3s ease;
}

/* Slideshow Ảnh */
.slideshow-container { width: 100%; }
.slideshow-img {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; opacity: 0; transition: opacity 1.5s ease-in-out; z-index: 0;
}
.slideshow-img.active { opacity: 1; z-index: 1; }
.image-backdrop { top: 0; right: 0; bottom: 30px; left: 30px; z-index: 0; }

/* Hộp Sự An Tâm nổi lơ lửng */
.floating-badge {
  bottom: 8%; left: -8%;
  animation: floatUpDown 4s ease-in-out infinite;
}
@keyframes floatUpDown {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}

@media (max-width: 991px) {
  .floating-badge { left: 5%; right: 5%; bottom: -5%; justify-content: center; }
}

/* ================================================= */
/* HIỆU ỨNG CHO PHẦN GIỚI THIỆU (ABOUT US)           */
/* ================================================= */

/* Khung ảnh và hiệu ứng trỏ chuột */
.about-image-wrapper {
  z-index: 1;
}
.image-backdrop {
  top: 0; right: 0; bottom: 40px; left: 40px;
  z-index: 0;
  transition: all 0.4s ease;
}
.image-container {
  /* Khóa ảnh không cho tràn ra ngoài khung khi zoom */
  display: block;
}
.about-img {
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
}
.about-image-wrapper:hover .about-img {
  transform: scale(1.08); /* Zoom nhẹ ảnh lên khi di chuột vào */
}
.about-image-wrapper:hover .image-backdrop {
  top: -10px; right: -10px; /* Khung nền Xanh nẩy nhẹ ra ngoài */
}
</style>