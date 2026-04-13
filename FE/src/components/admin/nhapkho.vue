<template>
  <div class="warehouse-container p-4 min-vh-100 d-flex flex-column w-100" style="background-color: #f8f9fa; max-width: 100%;">
    
    <div class="d-flex justify-content-between align-items-end mb-4 pb-2">
      <div>
        <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">Quản lý Kho</h2>
        <p class="text-muted mb-0">Cập nhật số lượng vật tư và theo dõi lịch sử giao dịch kho.</p>
      </div>
    </div>

    <ul class="nav nav-pills custom-tabs mb-4 gap-2">
      <li class="nav-item">
        <button class="nav-link fw-bold px-4 py-2 rounded-pill transition-all" 
                :class="{ 'active shadow-sm': activeTab === 'import' }"
                @click="activeTab = 'import'">
          <i class="bi bi-box-arrow-in-down me-2"></i>Nhập Kho Vật Tư
        </button>
      </li>
      <li class="nav-item">
        <button class="nav-link fw-bold px-4 py-2 rounded-pill transition-all" 
                :class="{ 'active shadow-sm': activeTab === 'history' }"
                @click="activeTab = 'history'">
          <i class="bi bi-clock-history me-2"></i>Lịch Sử Giao Dịch
        </button>
      </li>
    </ul>

    <div v-if="activeTab === 'import'" class="tab-content flex-grow-1 d-flex flex-column animation-fade-in">
      
      <div class="card border-0 shadow-sm rounded-4 bg-white p-4 mb-4">
        <div class="row g-4 align-items-end">
          <div class="col-md-5">
            <label class="form-label small fw-bolder text-muted mb-2 text-uppercase letter-spacing-1">Chọn vật tư cần nhập</label>
            <select class="form-select custom-input bg-light border-0 fw-bold text-dark fs-6 py-3">
              <option>Thép AB(36L) - Tồn: 20</option>
              <option>Nhôm BC(40R) - Tồn: 0</option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label small fw-bolder text-muted mb-2 text-uppercase letter-spacing-1">Số lượng nhập thêm</label>
            <input type="number" class="form-control custom-input bg-light border-0 fw-bold text-primary fs-5 text-center py-3" placeholder="0" min="1">
          </div>
          <div class="col-md-3">
            <button class="btn btn-navy w-100 py-3 rounded-3 fw-bold shadow-sm hover-lift fs-6 text-uppercase letter-spacing-1">
              <i class="bi bi-check2-circle me-1"></i> Xác nhận nhập
            </button>
          </div>
        </div>
      </div>

      <div class="card border-0 shadow-sm rounded-4 bg-white flex-grow-1 overflow-hidden">
        <div class="card-header bg-white border-bottom py-3 px-4 d-flex justify-content-between align-items-center">
          <h6 class="fw-bolder text-dark mb-0 fs-5">Danh sách vật tư chờ nhập</h6>
        </div>
        
        <div class="table-responsive w-100 pb-3">
          <table class="table modern-table align-middle mb-0 w-100">
            <thead class="bg-light">
              <tr>
                <th class="ps-4 py-3 text-muted fw-bold text-uppercase" style="font-size: 0.8rem; width: 80px;">Hình</th>
                <th class="py-3 text-muted fw-bold text-uppercase" style="font-size: 0.8rem;">Tên Sản Phẩm</th>
                <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem; width: 15%;">SL Tồn</th>
                <th class="py-3 text-muted fw-bold text-uppercase text-center" style="font-size: 0.8rem; width: 20%;">Nhập Nhanh</th>
                <th class="pe-4 py-3 text-muted fw-bold text-uppercase text-end" style="font-size: 0.8rem; width: 150px;">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="n in 3" :key="n" class="shadow-sm bg-white hover-lift border">
                <td class="ps-4 py-3">
                  <div class="img-box rounded-3 border bg-light flex-shrink-0 d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
                    <i class="bi bi-box fs-3 text-muted opacity-50"></i>
                  </div>
                </td>
                <td class="py-3">
                  <h6 class="mb-1 fw-bold text-dark fs-5">Thép Đặc Biệt Loại {{ n }}</h6>
                  <span class="text-muted small fw-medium"><i class="bi bi-upc-scan me-1"></i> SKU: THEP-00{{ n }}</span>
                </td>
                <td class="py-3 text-center">
                  <span class="badge bg-light border px-3 py-2 fw-bolder fs-5" :class="n === 2 ? 'text-danger border-danger border-opacity-50' : 'text-dark'">{{ n === 2 ? 0 : n * 15 }}</span>
                </td>
                <td class="py-3 text-center px-4">
                  <input type="number" class="form-control custom-input text-center fw-bold text-primary py-2" placeholder="0" min="1">
                </td>
                <td class="pe-4 py-3 text-end">
                  <button class="btn btn-light border text-success fw-bold px-4 py-2 rounded-pill hover-success transition-all">
                    Lưu
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-if="activeTab === 'history'" class="tab-content flex-grow-1 d-flex flex-column animation-fade-in">
      <div class="card border-0 shadow-sm rounded-4 bg-white flex-grow-1 overflow-hidden">
        
        <div class="card-header bg-white border-bottom py-3 px-4 d-flex justify-content-between align-items-center">
          <h6 class="fw-bolder text-dark mb-0 fs-5">Nhật ký giao dịch gần đây</h6>
          <div class="search-box position-relative" style="width: 300px;">
            <i class="bi bi-search position-absolute text-muted" style="top: 50%; left: 15px; transform: translateY(-50%);"></i>
            <input type="text" class="form-control bg-light border-0 rounded-pill shadow-none ps-5 py-2 fw-medium" placeholder="Tìm mã phiếu nhập...">
          </div>
        </div>

        <div class="table-responsive w-100">
          <table class="table modern-table align-middle mb-0 w-100">
            <thead class="bg-light">
              <tr>
                <th class="ps-4 py-4 text-muted fw-bold text-uppercase" style="font-size: 0.85rem; width: 150px;">Mã Phiếu</th>
                <th class="py-4 text-muted fw-bold text-uppercase" style="font-size: 0.85rem;">Sản Phẩm Nhập</th>
                <th class="py-4 text-muted fw-bold text-uppercase text-center" style="font-size: 0.85rem; width: 150px;">Số Lượng</th>
                <th class="py-4 text-muted fw-bold text-uppercase text-center" style="font-size: 0.85rem; width: 200px;">Người Nhập</th>
                <th class="pe-4 py-4 text-muted fw-bold text-uppercase text-end" style="font-size: 0.85rem; width: 180px;">Thời Gian</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="n in 5" :key="n" class="shadow-sm bg-white hover-lift border">
                <td class="ps-4 py-3">
                  <span class="badge bg-light text-secondary border px-2 py-1 fw-bold font-monospace">#NK-100{{ n }}</span>
                </td>
                <td class="py-3">
                  <h6 class="mb-0 fw-bold text-dark">Thép AB(36L)</h6>
                  <span class="text-muted small">SKU: THEP-AB-01</span>
                </td>
                <td class="py-3 text-center">
                  <span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-3 py-2 fw-bolder fs-6">
                    <i class="bi bi-arrow-up-short"></i> {{ n * 5 }}
                  </span>
                </td>
                <td class="py-3 text-center">
                  <div class="fw-semibold text-dark small">Nguyễn Văn Phúc</div>
                  <div class="text-muted" style="font-size: 0.7rem;">Admin</div>
                </td>
                <td class="pe-4 py-3 text-end">
                  <div class="fw-bold text-secondary small">27/01/2026</div>
                  <div class="text-muted" style="font-size: 0.75rem;">14:30:{{ n }}5</div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue';

// Trạng thái quản lý Tab (import | history)
const activeTab = ref('import');

// ... (Bạn dán các logic gọi API hiện tại của bạn vào đây)
</script>

<style scoped>
/* Màu thương hiệu & Nút */
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.btn-navy { background-color: #0b2e59; color: #fff; border: none; transition: 0.3s; }
.btn-navy:hover { background-color: #173b6c; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(11, 46, 89, 0.2); }

/* Typography */
.letter-spacing-1 { letter-spacing: 1px; }

/* Animation Chung */
.transition-all { transition: all 0.3s ease; }
.hover-lift { transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.06) !important; }
.animation-fade-in { animation: fadeIn 0.3s ease-in-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

/* Custom Tabs */
.custom-tabs .nav-link { color: #64748b; background-color: transparent; border: 1px solid transparent; }
.custom-tabs .nav-link:hover { background-color: #f1f5f9; color: #0f172a; }
.custom-tabs .nav-link.active { background-color: #0b2e59; color: #fff; }

/* Bảng Separated Row */
.modern-table { border-collapse: separate; border-spacing: 0 10px; }
.modern-table thead th { border: none; padding-bottom: 0; }
.modern-table tbody tr { border-radius: 12px; border-color: #f1f5f9 !important; }
.modern-table tbody td { border: none; background: #fff; }
.modern-table tbody td:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.modern-table tbody td:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }

/* Form Inputs */
.custom-input { border-radius: 10px; transition: 0.2s; box-shadow: none !important; }
.custom-input:focus { outline: 2px solid rgba(11, 46, 89, 0.2); outline-offset: -1px; }

/* Nút Hover riêng biệt */
.hover-success:hover { background-color: #198754 !important; color: #fff !important; }
</style>