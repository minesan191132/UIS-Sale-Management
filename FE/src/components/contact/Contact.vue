<script setup>
import { ref } from 'vue';
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';
import { contactAPI } from '../../services/api.js';

const form = ref({
  fullName: '',
  phone: '',
  email: '',
  subject: '',
  message: '',
});

const loading = ref(false);
const successMsg = ref('');
const errorMsg = ref('');

const submitForm = async () => {
  successMsg.value = '';
  errorMsg.value = '';
  loading.value = true;

  try {
    const res = await contactAPI.send(form.value);
    successMsg.value = res.message || 'Yêu cầu đã được gửi thành công!';
    // Reset form
    form.value = { fullName: '', phone: '', email: '', subject: '', message: '' };
  } catch (err) {
    errorMsg.value = err.response?.data?.error || 'Gửi thất bại. Vui lòng thử lại sau.';
  } finally {
    loading.value = false;
  }
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
            
            <!-- Alert thành công -->
            <div v-if="successMsg" class="alert alert-success alert-dismissible fade show d-flex align-items-center gap-2" role="alert">
              <i class="fas fa-check-circle"></i>
              <span>{{ successMsg }}</span>
              <button type="button" class="btn-close" @click="successMsg = ''"></button>
            </div>

            <!-- Alert lỗi -->
            <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show d-flex align-items-center gap-2" role="alert">
              <i class="fas fa-exclamation-circle"></i>
              <span>{{ errorMsg }}</span>
              <button type="button" class="btn-close" @click="errorMsg = ''"></button>
            </div>

            <form @submit.prevent="submitForm">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label fw-bold small text-muted">Họ và tên *</label>
                  <input
                    v-model="form.fullName"
                    type="text"
                    class="form-control form-control-lg bg-light fs-6"
                    placeholder="Nhập tên của bạn"
                    required
                  >
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-bold small text-muted">Số điện thoại *</label>
                  <input
                    v-model="form.phone"
                    type="tel"
                    class="form-control form-control-lg bg-light fs-6"
                    placeholder="Nhập SĐT liên hệ"
                    required
                  >
                </div>
                
                <div class="col-md-12">
                  <label class="form-label fw-bold small text-muted">Email</label>
                  <input
                    v-model="form.email"
                    type="email"
                    class="form-control form-control-lg bg-light fs-6"
                    placeholder="example@gmail.com"
                  >
                </div>

                <div class="col-md-12">
                  <label class="form-label fw-bold small text-muted">Chủ đề</label>
                  <input
                    v-model="form.subject"
                    type="text"
                    class="form-control form-control-lg bg-light fs-6"
                    placeholder="Báo giá / Tư vấn kỹ thuật..."
                  >
                </div>

                <div class="col-md-12">
                  <label class="form-label fw-bold small text-muted">Nội dung tin nhắn *</label>
                  <textarea
                    v-model="form.message"
                    class="form-control form-control-lg bg-light fs-6"
                    rows="5"
                    placeholder="Mô tả yêu cầu chi tiết..."
                    required
                  ></textarea>
                </div>

                <div class="col-12 mt-4">
                  <button
                    type="submit"
                    class="btn btn-primary btn-lg w-100 fw-bold text-uppercase py-3 shadow-sm btn-send"
                    :disabled="loading"
                  >
                    <span v-if="loading">
                      <span class="spinner-border spinner-border-sm me-2" role="status"></span>Đang gửi...
                    </span>
                    <span v-else>
                      <i class="fas fa-paper-plane me-2"></i>Gửi yêu cầu
                    </span>
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>

      </div>
    </div>

    <div class="map-wrapper">
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
  background-color: #f8fafc;
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
  background-color: #0f172a;
  border: none;
  transition: all 0.3s;
}
.btn-send:hover:not(:disabled) {
  background-color: #f59e0b;
  color: #000;
  transform: translateY(-2px);
}
.btn-send:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* --- MAP --- */
.map-wrapper {
  filter: grayscale(20%);
}

/* Animation */
.animate-up { animation: fadeUp 0.8s forwards; opacity: 0; transform: translateY(20px); }
.delay-1 { animation-delay: 0.2s; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
</style>