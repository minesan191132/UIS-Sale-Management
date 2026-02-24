<script setup>
import { ref } from 'vue';

// Import Navbar và Footer (Kiểm tra lại đường dẫn đúng với thư mục của bạn)
// Ví dụ: Nếu file này nằm trong views/, còn Navbar ở components/base/
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// Hàm cuộn lên đầu trang
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// Dữ liệu dịch vụ
const services = ref([
  {
    title: "Cơ khí xây dựng",
    description: "Tư vấn, thiết kế, thi công lắp đặt các sản phẩm cơ khí xây dựng, hệ thống đường ống công nghiệp, kết cấu thép nhà xưởng đảm bảo tiêu chuẩn an toàn.",
    // Bữa sau bạn có ảnh thì thay đường dẫn vào đây (ví dụ: import img1 from...)
    image: "https://placehold.co/600x400/e2e8f0/1e293b?text=Anh+1", 
  },
  {
    title: "Xử lý nước thải",
    description: "Cung cấp giải pháp toàn diện về xử lý nước thải công nghiệp và sinh hoạt. Thiết kế hệ thống lọc, vận hành và bảo trì trạm xử lý nước.",
    image: "https://placehold.co/600x400/e2e8f0/1e293b?text=Anh+2",
  },
  {
    title: "Gia công cắt chấn kim loại",
    description: "Gia công kim loại tấm chính xác cao bằng công nghệ Laser Fiber, chấn CNC, đột dập. Đáp ứng các chi tiết máy phức tạp.",
    image: "https://placehold.co/600x400/e2e8f0/1e293b?text=Anh+3",
  },
  {
    title: "Sản xuất nước uống",
    description: "Phát triển dây chuyền sản xuất nước uống đóng chai tinh khiết theo tiêu chuẩn Nhật Bản. Cung cấp nước uống chất lượng cao cho doanh nghiệp.",
    image: "https://placehold.co/600x400/e2e8f0/1e293b?text=Anh+4",
  },
]);
</script>

<template>
  <div class="service-page d-flex flex-column min-vh-100">
    <Navbar />

    <div class="page-header py-5 text-center text-white">
      <div class="container">
        <h1 class="fw-bold text-uppercase display-5 animate-up">Dịch Vụ Của Chúng Tôi</h1>
        <div class="divider mx-auto my-3"></div>
        <p class="lead text-white-50 animate-up delay-1">Giải pháp kỹ thuật toàn diện - Chất lượng Nhật Bản</p>
      </div>
    </div>

    <section class="container py-5 my-4 flex-grow-1">
      <div class="row g-4">
        <div v-for="(service, index) in services" :key="index" class="col-lg-6">
          
          <div class="card service-card h-100 border-0 shadow-sm">
            <div class="row g-0 h-100">
              <div class="col-md-5 overflow-hidden position-relative image-container">
                <img 
                  :src="service.image" 
                  :alt="service.title" 
                  class="img-fluid h-100 w-100 object-fit-cover service-img"
                />
                <div class="overlay"></div>
              </div>

              <div class="col-md-7">
                <div class="card-body d-flex flex-column h-100 p-4">
                  <div class="mb-auto">
                    <h4 class="card-title fw-bold text-uppercase mb-3 service-title">
                      {{ service.title }}
                    </h4>
                    <div class="title-underline mb-3"></div>
                    <p class="card-text text-muted">
                      {{ service.description }}
                    </p>
                  </div>
                  
                  <div class="mt-3">
                    <button class="btn btn-outline-primary btn-sm fw-bold rounded-pill px-3">
                      Xem chi tiết <i class="fas fa-arrow-right ms-1"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </section>

    <Footer />
  </div>
</template>

<style scoped>
/* --- PAGE HEADER --- */
.page-header {
  /* Gradient đồng bộ với Home/Navbar */
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  position: relative;
  margin-bottom: 3rem;
  padding: 4rem 0;
}

.divider {
  width: 80px;
  height: 4px;
  background-color: #f59e0b; /* Màu vàng cam công nghiệp */
  border-radius: 2px;
}

/* --- SERVICE CARD --- */
.service-card {
  transition: all 0.3s ease;
  background: #fff;
  border-radius: 8px; /* Bo góc nhẹ cho cứng cáp */
  overflow: hidden;
}

.service-card:hover {
  transform: translateY(-5px); /* Nổi lên khi hover */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
}

/* Ảnh dịch vụ */
.service-img {
  transition: transform 0.5s ease;
  min-height: 250px; /* Chiều cao tối thiểu cho ảnh */
}

.service-card:hover .service-img {
  transform: scale(1.1); /* Phóng to ảnh khi hover */
}

/* Gạch chân tiêu đề nhỏ trong card */
.title-underline {
  width: 40px;
  height: 3px;
  background-color: #e2e8f0;
  transition: width 0.3s;
}

.service-card:hover .title-underline {
  width: 60px; /* Dài ra khi hover */
  background-color: #f59e0b;
}

.service-title {
  color: #1e293b; /* Màu xanh đen */
  font-size: 1.3rem
}

.service-card:hover .service-title {
  color: #0d6efd; /* Đổi màu tiêu đề khi hover */
}

/* --- FLOATING BUTTONS (Nút nổi) --- */
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
  text-decoration: none;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2);
  transition: all 0.3s;
  cursor: pointer;
  border: none;
  font-weight: bold;
  font-size: 0.8rem;
}

.float-btn:hover {
  transform: scale(1.1);
  color: white;
}


/* --- ANIMATION --- */
.animate-up {
  animation: fadeUp 0.8s forwards;
  opacity: 0;
  transform: translateY(20px);
}
.delay-1 { animation-delay: 0.2s; }

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

/* Responsive cho Mobile */
@media (max-width: 768px) {
  .service-card .row {
    flex-direction: column; /* Trên mobile xếp dọc */
  }
  .image-container {
    height: 200px;
  }
}
</style>