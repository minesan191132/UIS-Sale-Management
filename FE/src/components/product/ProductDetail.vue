<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { addToCart } from '../../store/cart.js';
import Swal from 'sweetalert2';

import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';

const route = useRoute();
const router = useRouter();

const product = ref(null);
const isLoading = ref(true);
const orderQuantity = ref(1);

const scrollToTop = () => window.scrollTo({ top: 0, behavior: "smooth" });

const fetchProductDetail = async () => {
  const productId = route.params.id;
  try {
    const response = await axios.get(`http://localhost:8080/api/products/${productId}`);
    const data = response.data;
    
    // Đã thêm các lớp bảo vệ (||) để phòng trường hợp backend trả về dữ liệu rỗng
    product.value = {
      id: data.id,
      name: data.name || "Đang cập nhật",
      code: data.sku || "N/A",
      status: data.stockQuantity > 0 ? "Còn hàng" : "Hết hàng",
      image: data.imageUrl || `https://placehold.co/600x600/e2e8f0/1e293b?text=${encodeURIComponent(data.sku || 'IMG')}`,
      origin: data.defaultMaterial || "Đang cập nhật",
      price: data.price || 0,
      stockQuantity: data.stockQuantity || 0,
      description: data.description || "Sản phẩm được gia công chính xác theo tiêu chuẩn công nghiệp. Đảm bảo độ bền và hiệu suất hoạt động cao trong môi trường khắc nghiệt.",
      specification: data.defaultSpecification || "Theo tiêu chuẩn nội bộ UPEC"
    };
  } catch (error) {
    console.error("Lỗi khi tải chi tiết sản phẩm:", error);
  } finally {
    isLoading.value = false;
  }
};

const goBack = () => {
  router.push('/products');
};

const handleAddToCart = () => {
  addToCart(product.value, orderQuantity.value);
  
  Swal.fire({
    title: 'Đã thêm vào giỏ!',
    text: `Bạn vừa thêm ${orderQuantity.value} x ${product.value.name}`,
    icon: 'success',
    showCancelButton: true,
    confirmButtonColor: '#0b2e59',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Đến giỏ hàng',
    cancelButtonText: 'Mua tiếp'
  }).then((result) => {
    if (result.isConfirmed) {
      router.push('/cart');
    }
  });
};

onMounted(() => {
  scrollToTop();
  fetchProductDetail();
});
</script>

<template>
  <div class="product-detail-page d-flex flex-column min-vh-100 bg-light">
    <Navbar />

    <div class="breadcrumb-area bg-white border-bottom py-3 shadow-sm">
      <div class="container">
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb m-0">
            <li class="breadcrumb-item"><router-link to="/" class="text-decoration-none text-muted"><i class="fas fa-home"></i> Trang chủ</router-link></li>
            <li class="breadcrumb-item"><router-link to="/products" class="text-decoration-none text-muted">Sản phẩm</router-link></li>
            <li class="breadcrumb-item active fw-bold" aria-current="page" v-if="product">{{ product.code }}</li>
          </ol>
        </nav>
      </div>
    </div>

    <div class="container py-5 flex-grow-1">
      
      <button @click="goBack" class="btn btn-link text-decoration-none text-muted p-0 mb-4 fw-bold">
        <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
      </button>

      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
      </div>

      <div v-else-if="product" class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5">
        <div class="row g-0">
          
          <div class="col-lg-5 p-4 p-md-5 d-flex align-items-center justify-content-center bg-white border-end">
            <img :src="product.image" class="product-detail-img rounded-4 shadow-sm" :alt="product.name">
          </div>
          
          <div class="col-lg-7 p-4 p-md-5 bg-white">
            
            <div class="mb-3">
              <span v-if="product.status === 'Còn hàng'" class="badge bg-success bg-opacity-10 text-success border border-success rounded-pill px-3 py-2">
                <i class="fas fa-check-circle me-1"></i> Sẵn sàng giao hàng
              </span>
              <span v-else class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary rounded-pill px-3 py-2">
                <i class="fas fa-times-circle me-1"></i> Tạm hết hàng
              </span>
            </div>

            <h1 class="fw-bold mb-3 product-title-main">{{ product.name }}</h1>
            <p class="text-muted fs-5 mb-4">Mã SKU: <span class="text-dark fw-bold">{{ product.code }}</span></p>
            
            <div class="price-box bg-light rounded-3 p-4 mb-4 border">
              <span class="text-muted d-block mb-1">Giá bán tham khảo:</span>
              <h2 class="text-danger fw-bold m-0 display-6">
                {{ product.price > 0 ? product.price.toLocaleString('vi-VN') + ' ₫' : 'Liên hệ nhận báo giá' }}
              </h2>
            </div>

            <div class="row g-3 mb-4">
              <div class="col-sm-6">
                <div class="border rounded-3 p-3 d-flex align-items-center">
                  <div class="icon-box bg-warning bg-opacity-10 text-warning rounded-circle me-3 d-flex align-items-center justify-content-center" style="width:45px; height:45px;">
                    <i class="fas fa-cube fs-5"></i>
                  </div>
                  <div>
                    <span class="d-block text-muted small">Vật liệu</span>
                    <strong class="text-dark">{{ product.origin }}</strong>
                  </div>
                </div>
              </div>
              <div class="col-sm-6">
                <div class="border rounded-3 p-3 d-flex align-items-center">
                  <div class="icon-box bg-info bg-opacity-10 text-info rounded-circle me-3 d-flex align-items-center justify-content-center" style="width:45px; height:45px;">
                    <i class="fas fa-boxes fs-5"></i>
                  </div>
                  <div>
                    <span class="d-block text-muted small">Tồn kho hiện tại</span>
                    <strong class="text-dark">{{ product.stockQuantity }} chiếc</strong>
                  </div>
                </div>
              </div>
            </div>

            <hr class="my-4">

            <div class="d-flex flex-column flex-md-row gap-3 align-items-md-center">
              <div class="quantity-selector d-flex align-items-center border rounded-3 overflow-hidden" style="width: 140px; height: 50px;">
                <button class="btn btn-light border-0 h-100 px-3 fw-bold fs-5" @click="orderQuantity > 1 ? orderQuantity-- : null">-</button>
                <input type="number" class="form-control border-0 text-center fw-bold fs-5 h-100" v-model="orderQuantity" min="1">
                <button class="btn btn-light border-0 h-100 px-3 fw-bold fs-5" @click="orderQuantity++">+</button>
              </div>
              
              <button @click="handleAddToCart" class="btn btn-add-cart flex-grow-1 fw-bold text-white fs-5 rounded-3 d-flex align-items-center justify-content-center" style="height: 50px; background-color: #f59e0b;">
                <i class="fas fa-shopping-cart me-2"></i> THÊM VÀO BÁO GIÁ
              </button>
            </div>

          </div>
        </div>
      </div>
      
      <div v-else class="text-center py-5">
        <h3 class="text-muted">Không tìm thấy thông tin sản phẩm.</h3>
        <button @click="goBack" class="btn btn-primary mt-3">Quay lại</button>
      </div>

      <div v-if="product" class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5">
        <div class="card-header bg-white border-bottom p-4">
          <h4 class="fw-bold m-0" style="color: #0b2e59;">Mô tả chi tiết</h4>
        </div>

        <div class="card-body p-4 p-md-5 text-secondary">
          
          <p style="line-height: 1.8; font-size: 1.1rem; text-align: justify;">
            {{ product.description }}
          </p>

          <div class="mt-4 pt-3 border-top text-muted" style="font-size: 0.9rem; font-style: italic;">
            <i class="fas fa-info-circle me-1"></i>
            * Lưu ý: Hình ảnh minh họa bề mặt vật liệu. Tất cả sản phẩm đều qua kiểm định QC. Vui lòng liên hệ xác nhận kích thước L x W x H chính xác trước khi chốt đơn.
          </div>

        </div>
      </div>

    </div>

    <Footer />
  </div>
</template>

<style scoped>
.product-detail-page {
  font-family: Arial, sans-serif;
}

.product-title-main {
  color: #0b2e59;
  font-size: 2rem;
  line-height: 1.3;
}

.btn-add-cart {
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(245, 158, 11, 0.2);
  border: none;
}

.btn-add-cart:hover {
  background-color: #d97706 !important;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(245, 158, 11, 0.4);
}

.quantity-selector input[type="number"]::-webkit-inner-spin-button,
.quantity-selector input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.quantity-selector input[type="number"] {
  appearance: textfield;
  -moz-appearance: textfield;
}

.quantity-selector .btn-light {
  background-color: #f8f9fa;
  color: #495057;
}

.quantity-selector .btn-light:hover {
  background-color: #e2e6ea;
}

.product-detail-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  object-position: center;
  background-color: #f8f9fa;
}
</style>