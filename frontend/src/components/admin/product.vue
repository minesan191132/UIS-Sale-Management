<template>
  <div class="products-container p-4">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Quản lý sản phẩm</h2>

    <div class="row g-3 mb-4">
      <div class="col-md-3" v-for="(stat, index) in productStats" :key="index">
        <div :class="stat.bgClass" class="card border-0 shadow-sm p-3 text-white h-100 position-relative overflow-hidden">
          <div class="position-relative" style="z-index: 2;">
            <h3 class="fw-bold mb-0">{{ stat.value }}</h3>
            <p class="small fw-bold text-uppercase mb-0 opacity-90">{{ stat.label }}</p>
          </div>
          <i :class="stat.icon" class="position-absolute end-0 bottom-0 mb-n2 me-n2 display-4 opacity-25"></i>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 class="fw-bold m-0">Danh sách sản phẩm</h5>
          <small class="text-muted">Quản lý và sửa thông tin sản phẩm</small>
        </div>
        <button class="btn btn-primary btn-sm px-3 rounded-pill fw-bold">
          <i class="bi bi-plus-lg me-1"></i> Thêm sản phẩm mới
        </button>
      </div>

      <div class="row g-2 mb-4">
        <div class="col-md-4">
          <input type="text" class="form-control form-control-sm bg-light border-0" placeholder="Tìm kiếm theo tên...">
        </div>
        <div class="col-md-3">
          <select class="form-select form-select-sm bg-light border-0">
            <option selected>Tất cả danh mục</option>
            <option>Linh kiện thép</option>
            <option>Linh kiện nhôm</option>
          </select>
        </div>
        <div class="col-md-3">
          <select class="form-select form-select-sm bg-light border-0">
            <option selected>Trạng thái</option>
            <option>Còn hàng</option>
            <option>Sắp hết hàng</option>
          </select>
        </div>
        <div class="col-md-2">
          <button class="btn btn-primary btn-sm w-100 fw-bold">Tìm kiếm</button>
        </div>
      </div>

      <div class="product-list d-flex flex-column gap-3">
        <div v-for="item in products" :key="item.id" class="product-item p-3 border rounded-4 bg-white shadow-sm hover-scale transition">
          <div class="row align-items-center">
            <div class="col-md-1 text-center">
              <div class="bg-light rounded-3 py-3 text-muted small border">HÌNH</div>
            </div>
            <div class="col-md-4">
              <h6 class="fw-bold mb-1">{{ item.name }}</h6>
              <p class="small text-muted mb-0">Phân loại: <span class="fw-bold">{{ item.category }}</span></p>
              <h5 class="fw-bold text-danger mt-1 mb-0">{{ item.price }}$ <small class="text-muted fs-6 fw-normal ms-2">Tồn kho: {{ item.stock }}</small></h5>
            </div>
            <div class="col-md-3 text-center">
              <span :class="item.stock > 10 ? 'bg-success text-white' : 'bg-warning text-dark'" class="badge px-4 py-2 rounded-pill fw-normal">
                {{ item.stock > 10 ? 'Còn hàng' : 'Sắp hết hàng' }}
              </span>
            </div>
            <div class="col-md-4 text-end d-flex gap-2 justify-content-end">
              <button class="btn btn-light btn-sm border px-4 rounded-pill">Sửa</button>
              <button class="btn btn-light btn-sm border px-4 rounded-pill">Xoá</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const productStats = [
  { label: 'Tổng sản phẩm', value: '30', bgClass: 'bg-danger', icon: 'bi-box-seam' },
  { label: 'Đang hoạt động', value: '22', bgClass: 'bg-success', icon: 'bi-check-circle' },
  { label: 'Hết hàng', value: '11', bgClass: 'bg-purple', icon: 'bi-slash-circle' }, // bg-purple cần định nghĩa CSS
  { label: 'Sắp hết hàng', value: '9', bgClass: 'bg-indigo', icon: 'bi-exclamation-triangle' }
];

const products = ref([
  { id: 1, name: 'Thép AB(36L)', category: 'Linh kiện thép', price: '30.000.000', stock: 2, status: 'Sắp hết' },
  { id: 2, name: 'Nhôm BC(40R)', category: 'Linh kiện nhôm', price: '45.000.000', stock: 85, status: 'Còn hàng' }
]);
</script>

<style scoped>
.bg-purple { background-color: #9c27b0 !important; }
.bg-indigo { background-color: #6610f2 !important; }

.product-item {
  border: 1px solid #f1f2f4 !important;
  transition: all 0.3s ease;
}

.product-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.05) !important;
  background-color: #fcfcfd !important;
}

.hover-scale:hover {
  border-color: #0d6efd !important;
}
</style>