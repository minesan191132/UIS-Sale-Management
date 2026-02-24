<script setup>
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

// Hàm cuộn lên đầu trang (cho nút Floating)
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};
</script>

<template>
  <div class="contact-page d-flex flex-column min-vh-100">
    <Navbar />

    <div class="page-header py-5 text-center text-white">
      <div class="container">
        <h1 class="fw-bold text-uppercase display-5 animate-up">Liên Hệ</h1>
        <div class="divider mx-auto my-3"></div>
        <p class="lead text-white-50 animate-up delay-1">
          Kết nối với chúng tôi để được tư vấn giải pháp tốt nhất
        </p>
      </div>
    </div>

    <div class="container py-5 flex-grow-1">
      <div class="row g-5">
        
        <div class="col-lg-5">
          <div class="info-wrapper h-100">
            <h3 class="text-uppercase fw-bold text-dark mb-4 section-title">Thông tin liên lạc</h3>
            <p class="text-muted mb-4">
              Quý khách hàng có nhu cầu tư vấn kỹ thuật, báo giá hoặc hợp tác, vui lòng liên hệ với chúng tôi qua các kênh dưới đây.
            </p>

            <div class="info-card d-flex align-items-start mb-4 p-3 rounded shadow-sm bg-white border">
              <div class="icon-box me-3">
                <i class="fas fa-map-marker-alt"></i>
              </div>
              <div>
                <h6 class="fw-bold text-uppercase mb-1">Địa chỉ</h6>
                <p class="mb-0 text-muted small">
                  70 Xuân Thới Sơn 2B, Ấp 6, Xã Xuân Thới Sơn,<br>Huyện Hóc Môn, TP.HCM
                </p>
              </div>
            </div>

            <div class="info-card d-flex align-items-start mb-4 p-3 rounded shadow-sm bg-white border">
              <div class="icon-box me-3">
                <i class="fas fa-phone-alt"></i>
              </div>
              <div>
                <h6 class="fw-bold text-uppercase mb-1">Hotline / Fax</h6>
                <p class="mb-0 text-muted small fw-bold text-primary">028.3811.2449</p>
                <p class="mb-0 text-muted small">0914.771.440 (Zalo)</p>
              </div>
            </div>

            <div class="info-card d-flex align-items-start mb-4 p-3 rounded shadow-sm bg-white border">
              <div class="icon-box me-3">
                <i class="fas fa-envelope"></i>
              </div>
              <div>
                <h6 class="fw-bold text-uppercase mb-1">Email hỗ trợ</h6>
                <p class="mb-0 text-muted small">upecvn@gmail.com</p>
                <p class="mb-0 text-muted small">sales@upec.com.vn</p>
              </div>
            </div>

            <div class="info-card d-flex align-items-start p-3 rounded shadow-sm bg-white border">
              <div class="icon-box me-3">
                <i class="fas fa-clock"></i>
              </div>
              <div>
                <h6 class="fw-bold text-uppercase mb-1">Giờ làm việc</h6>
                <p class="mb-0 text-muted small">Thứ 2 - Thứ 6: 08:00 - 17:00</p>
                <p class="mb-0 text-muted small">Thứ 7: 08:00 - 12:00</p>
              </div>
            </div>

          </div>
        </div>

        <div class="col-lg-7">
          <div class="contact-form-wrapper bg-white p-4 p-md-5 rounded shadow h-100 border-top-accent">
            <h3 class="text-uppercase fw-bold text-dark mb-4 section-title">Gửi tin nhắn</h3>
            
            <form @submit.prevent>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label fw-bold small text-muted">Họ và tên *</label>
                  <input type="text" class="form-control form-control-lg bg-light fs-6" placeholder="Nhập tên của bạn">
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-bold small text-muted">Số điện thoại *</label>
                  <input type="tel" class="form-control form-control-lg bg-light fs-6" placeholder="Nhập SĐT liên hệ">
                </div>
                
                <div class="col-md-12">
                  <label class="form-label fw-bold small text-muted">Email</label>
                  <input type="email" class="form-control form-control-lg bg-light fs-6" placeholder="example@gmail.com">
                </div>

                <div class="col-md-12">
                  <label class="form-label fw-bold small text-muted">Chủ đề</label>
                  <input type="text" class="form-control form-control-lg bg-light fs-6" placeholder="Báo giá / Tư vấn kỹ thuật...">
                </div>

                <div class="col-md-12">
                  <label class="form-label fw-bold small text-muted">Nội dung tin nhắn *</label>
                  <textarea class="form-control form-control-lg bg-light fs-6" rows="5" placeholder="Mô tả yêu cầu chi tiết..."></textarea>
                </div>

                <div class="col-12 mt-4">
                  <button class="btn btn-primary btn-lg w-100 fw-bold text-uppercase py-3 shadow-sm btn-send">
                    <i class="fas fa-paper-plane me-2"></i>Gửi yêu cầu
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>

      </div>
    </div>

    <<div class="map-wrapper">
      <iframe
      title="Bản đồ Utsunomiya Industry Vietnam"
      src="https://maps.google.com/maps?q=C%C3%94NG%20TY%20TNHH%20UTSUNOMIYA%20INDUSTRY%20VI%E1%BB%86T%20NAM%2C%2070%20Xu%C3%A2n%20Th%E1%BB%9Bi%20S%C6%A1n%202B%2C%20H%C3%B3c%20M%C3%B4n&t=&z=15&ie=UTF8&iwloc=&output=embed"
      width="100%"
      height="600"
      style="border:0;"
      allowfullscreen=""
      loading="lazy"
      referrerpolicy="no-referrer-when-downgrade">
      </iframe>
    </div>
    <Footer />
  </div>
</template>

<style scoped>
/* --- CẤU HÌNH CHUNG --- */
.contact-page {
  background-color: #f8fafc; /* Nền xám nhạt */
  color: #334155;
}

/* --- HEADER BANNER --- */
.page-header {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  position: relative;
  border-bottom: 4px solid #f59e0b;
}

.divider {
  width: 80px; height: 4px;
  background-color: #f59e0b;
  border-radius: 2px;
}

/* --- INFO CARD (CỘT TRÁI) --- */
.section-title {
  position: relative;
  padding-bottom: 15px;
}
.section-title::after {
  content: ''; position: absolute; left: 0; bottom: 0;
  width: 50px; height: 3px; background: #f59e0b;
}

.info-card {
  transition: transform 0.3s;
  border: 1px solid rgba(0,0,0,0.05) !important;
}
.info-card:hover {
  transform: translateX(5px);
  border-left: 3px solid #f59e0b !important;
}

.icon-box {
  width: 45px; height: 45px;
  background-color: rgba(15, 23, 42, 0.05);
  color: #0f172a;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.2rem;
}

/* --- FORM LIÊN HỆ (CỘT PHẢI) --- */
.contact-form-wrapper {
  border-top: 4px solid #0f172a;
}
.form-control:focus {
  border-color: #f59e0b;
  box-shadow: 0 0 0 0.25rem rgba(245, 158, 11, 0.15);
}
.btn-send {
  background-color: #0f172a; /* Màu xanh đen */
  border: none;
  transition: all 0.3s;
}
.btn-send:hover {
  background-color: #f59e0b; /* Hover chuyển màu cam */
  color: #000;
  transform: translateY(-2px);
}

/* --- MAP --- */
.map-section {
  filter: grayscale(20%);
}

/* --- FLOATING BUTTONS --- */
.floating-group {
  position: fixed; bottom: 30px; right: 30px;
  display: flex; flex-direction: column; gap: 10px; z-index: 1000;
}
.float-btn {
  width: 45px; height: 45px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; font-weight: bold; cursor: pointer;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2); font-size: 0.8rem;
}
.zalo-bg { background-color: #0068ff; }
.messenger-bg { background-color: #1877f2; }
.top-bg { background-color: #f59e0b; font-size: 1.1rem; }

/* Animation */
.animate-up { animation: fadeUp 0.8s forwards; opacity: 0; transform: translateY(20px); }
.delay-1 { animation-delay: 0.2s; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }

</style>