<template>
  <div class="products-container p-4 min-vh-100 d-flex flex-column w-100" style="background-color: #f8f9fa; max-width: 100%;">
    
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">Quản lý sản phẩm</h2>
        <p class="text-muted mb-0">Quản lý danh sách vật tư và cập nhật tồn kho hệ thống</p>
      </div>
      <button class="btn btn-navy px-4 py-2 rounded-pill fw-bold shadow-sm d-flex align-items-center" @click="openAddModal">
        <i class="bi bi-plus-lg me-2"></i> Thêm sản phẩm mới
      </button>
    </div>

    <div class="row g-4 mb-4">
      <div class="col-md-3" v-for="(stat, index) in productStats" :key="index">
        <div class="card border-0 shadow-sm rounded-4 h-100 stat-card" :class="stat.theme">
          <div class="card-body p-4 d-flex justify-content-between align-items-center">
            <div class="stat-content">
              <p class="stat-label fw-bold text-uppercase mb-1" style="font-size: 0.8rem; letter-spacing: 0.5px;">{{ stat.label }}</p>
              <h2 class="stat-value fw-bolder mb-0 display-6">{{ stat.value }}</h2>
            </div>
            <div class="icon-box rounded-circle d-flex justify-content-center align-items-center" style="width: 60px; height: 60px;">
              <i :class="stat.icon" class="fs-3 stat-icon"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 bg-white flex-grow-1 w-100">
      
      <div class="card-header bg-white border-bottom py-4 px-4">
        <div class="row g-3 align-items-center">
          <div class="col-md-4">
            <div class="search-box position-relative">
              <i class="bi bi-search position-absolute text-muted" style="top: 50%; left: 15px; transform: translateY(-50%);"></i>
              <input type="text" class="form-control bg-light border-0 rounded-pill shadow-none ps-5 py-2 fw-medium"
                     placeholder="Tìm kiếm theo tên hoặc mã SKU..." v-model="filter.keyword" @keyup.enter="searchProducts">
            </div>
          </div>
          <div class="col-md-3">
            <select class="form-select bg-light border-0 rounded-pill shadow-none fw-medium text-secondary py-2 px-4" v-model="filter.status" @change="searchProducts">
              <option value="">Tất cả trạng thái</option>
              <option value="in_stock">Còn hàng</option>
              <option value="low_stock">Sắp hết hàng</option>
              <option value="out_of_stock">Hết hàng</option>
            </select>
          </div>
          <div class="col-md-3">
            <select class="form-select bg-light border-0 rounded-pill shadow-none fw-medium text-secondary py-2 px-4" v-model="filter.categoryId" @change="searchProducts">
              <option :value="null">Tất cả danh mục</option>
              <option :value="1">Phôi Sắt</option>
              <option :value="2">Phôi Thép</option>
              <option :value="3">Phôi Inox</option>
            </select>
          </div>
          <div class="col-md-2">
            <button class="btn btn-primary w-100 py-2 rounded-pill fw-bold text-white shadow-sm" style="background-color: #2563eb; border: none;" @click="searchProducts">
              <i class="bi bi-funnel-fill me-1"></i> Lọc kết quả
            </button>
          </div>
        </div>
      </div>

      <div class="table-responsive w-100">
        <table class="table align-middle mb-0 custom-table w-100">
          <thead class="bg-light">
            <tr>
              <th class="px-4 py-4 text-muted fw-bold text-uppercase" style="font-size: 0.85rem;">Sản phẩm</th>
              <th class="py-4 text-muted fw-bold text-uppercase" style="font-size: 0.85rem;">Mã SKU</th>
              <th class="py-4 text-muted fw-bold text-uppercase" style="font-size: 0.85rem;">Giá bán</th>
              <th class="py-4 text-muted fw-bold text-uppercase text-center" style="font-size: 0.85rem;">Tồn kho</th>
              <th class="py-4 text-muted fw-bold text-uppercase text-center" style="font-size: 0.85rem;">Trạng thái</th>
              <th class="px-4 py-4 text-muted fw-bold text-uppercase text-end" style="font-size: 0.85rem;">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="6" class="text-center py-5">
                <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;"></div>
              </td>
            </tr>
            <tr v-else-if="error">
              <td colspan="6" class="text-center py-4 text-danger fw-bold">{{ error }}</td>
            </tr>
            <tr v-else-if="products.length === 0">
              <td colspan="6" class="text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted d-block mb-3 opacity-50"></i>
                <h5 class="text-muted fw-bold">Không tìm thấy sản phẩm</h5>
              </td>
            </tr>
            
            <tr v-for="item in products" :key="item.id" class="table-row-hover">
              <td class="px-4 py-3">
                <div class="d-flex align-items-center gap-4">
                  <div class="img-box rounded-3 border shadow-sm flex-shrink-0 bg-white overflow-hidden" style="width: 80px; height: 80px;">
                    <img v-if="item.imageUrl" :src="item.imageUrl" class="w-100 h-100" style="object-fit: cover;"/>
                    <div v-else class="w-100 h-100 d-flex align-items-center justify-content-center text-muted bg-light"><i class="bi bi-image fs-3"></i></div>
                  </div>
                  <div>
                    <h6 class="mb-1 fw-bold text-dark fs-5 product-name">{{ item.name }}</h6>
                  </div>
                </div>
              </td>
              <td class="py-3 text-secondary fw-bold">{{ item.sku || '—' }}</td>
              <td class="py-3 fw-bolder text-dark fs-5">{{ formatPrice(item.price) }}</td>
              <td class="py-3 text-center">
                <span class="fw-bolder fs-5" :class="item.stockQuantity > 0 ? 'text-dark' : 'text-danger'">{{ item.stockQuantity ?? 0 }}</span>
              </td>
              <td class="py-3 text-center">
                <span class="badge rounded-pill px-3 py-2 fw-bold" :class="statusClass(item.status)" style="font-size: 0.85rem;">
                  <i class="me-1" :class="statusIcon(item.status)"></i> {{ item.status }}
                </span>
              </td>
              <td class="px-4 py-3 text-end">
                <div class="d-flex gap-2 justify-content-end">
                  <button class="btn btn-action bg-light text-primary border" @click="openEditModal(item)" title="Sửa">
                    <i class="bi bi-pencil-square"></i>
                  </button>
                  <button class="btn btn-action bg-light text-danger border" @click="handleDelete(item)" title="Xóa">
                    <i class="bi bi-trash3-fill"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="totalPages > 1" class="card-footer bg-white border-top py-4 px-4">
        <div class="d-flex justify-content-between align-items-center">
          <span class="text-muted fw-medium">Hiển thị trang <b class="text-dark fs-5">{{ currentPage + 1 }}</b> / {{ totalPages }}</span>
          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary px-4 py-2 rounded-pill fw-bold" :disabled="currentPage === 0" @click="changePage(currentPage - 1)">
              <i class="bi bi-chevron-left me-1"></i> Trang trước
            </button>
            <button class="btn btn-outline-secondary px-4 py-2 rounded-pill fw-bold" :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">
              Trang sau <i class="bi bi-chevron-right ms-1"></i>
            </button>
          </div>
        </div>
      </div>

    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box card border-0 shadow-lg p-0 rounded-4" style="max-width:600px;width:100%">
        <div class="bg-light p-4 border-bottom rounded-top-4 d-flex justify-content-between align-items-center">
          <h4 class="fw-bolder mb-0 text-dark">
            {{ editingId ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}
          </h4>
          <button class="btn-close shadow-none fs-5" @click="closeModal"></button>
        </div>

        <div class="p-4">
          <form @submit.prevent="handleSave">
            <div class="mb-3">
              <label class="form-label fw-bold text-secondary mb-1">Tên sản phẩm <span class="text-danger">*</span></label>
              <input class="form-control custom-input" v-model="form.name" required />
            </div>
            
            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <label class="form-label fw-bold text-secondary mb-1">Mã SKU</label>
                <input class="form-control custom-input" v-model="form.sku" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-bold text-secondary mb-1">Giá bán (VNĐ)</label>
                <input class="form-control custom-input" type="number" v-model="form.price" />
              </div>
            </div>
            
            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <label class="form-label fw-bold text-secondary mb-1">Số lượng tồn kho</label>
                <input class="form-control custom-input" type="number" v-model="form.stockQuantity" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-bold text-secondary mb-1">Danh mục</label>
                <select class="form-select custom-input" v-model="form.categoryId">
                  <option :value="null">-- Chọn --</option>
                  <option :value="1">Phôi Sắt</option>
                  <option :value="2">Phôi Thép</option>
                  <option :value="3">Phôi Inox</option>
                </select>
              </div>
            </div>
            
            <div class="mb-3">
              <label class="form-label fw-bold text-secondary mb-1">URL Hình ảnh</label>
              <input class="form-control custom-input" v-model="form.imageUrl" />
            </div>

            <div class="mb-3">
              <label class="form-label fw-bold text-secondary mb-1">Chất liệu mặc định</label>
              <input class="form-control custom-input" v-model="form.defaultMaterial" />
            </div>
            
            <div class="mb-4">
              <label class="form-label fw-bold text-secondary mb-1">Mô tả ngắn</label>
              <textarea class="form-control custom-input" rows="2" v-model="form.description"></textarea>
            </div>
            
            <div v-if="saveError" class="alert alert-danger alert-fit-content py-2 rounded-3">{{ saveError }}</div>
            
            <div class="d-flex gap-3 justify-content-end pt-3 border-top mt-4">
              <button type="button" class="btn btn-light px-4 py-2 rounded-pill fw-bold border" @click="closeModal">Huỷ bỏ</button>
              <button type="submit" class="btn btn-navy px-4 py-2 rounded-pill fw-bold shadow-sm" :disabled="saving">
                <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                {{ editingId ? 'Lưu thay đổi' : 'Xác nhận Thêm' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { productAdminAPI } from '../../services/api.js';

// ─── State ───────────────────────────────────────────────────
const products = ref([]);
const loading = ref(false);
const error = ref('');
const currentPage = ref(0);
const totalPages = ref(0);
const filter = reactive({ keyword: '', status: '', categoryId: null });

const stats = reactive({ total: 0, active: 0, outOfStock: 0, lowStock: 0 });

const showModal = ref(false);
const editingId = ref(null);
const saving = ref(false);
const saveError = ref('');
const form = reactive({
  name: '', sku: '', price: null, stockQuantity: null,
  categoryId: null, imageUrl: '', description: '', defaultMaterial: '',
  defaultSpecification: '', slug: '', isActive: true,
});

// ─── Khai báo Theme cho các ô thống kê ──────────────────────
const productStats = computed(() => [
  { label: 'Tổng sản phẩm',  value: stats.total,      theme: 'theme-primary', icon: 'bi bi-box-seam' },
  { label: 'Đang hoạt động', value: stats.active,     theme: 'theme-success', icon: 'bi bi-check-circle' },
  { label: 'Sắp hết hàng',   value: stats.lowStock,   theme: 'theme-warning', icon: 'bi bi-exclamation-triangle' },
  { label: 'Hết hàng',       value: stats.outOfStock, theme: 'theme-danger',  icon: 'bi bi-x-circle' },
]);

// ─── Load Data ───────────────────────────────────────────────
async function loadStats() {
  try {
    const data = await productAdminAPI.getStats();
    stats.total = data.total ?? 0;
    stats.active = data.active ?? 0;
    stats.outOfStock = data.outOfStock ?? 0;
    stats.lowStock = data.lowStock ?? 0;
  } catch (e) {
    console.error('loadStats error', e);
  }
}

function searchProducts() {
  currentPage.value = 0;
  loadProducts();
}

async function loadProducts() {
  loading.value = true;
  error.value = '';
  try {
    const data = await productAdminAPI.getAll({
      keyword: filter.keyword,
      categoryId: filter.categoryId,
      status: filter.status,
      page: currentPage.value,
      size: 5, 
    });
    
    products.value = data.content ?? [];
    const totalElements = data.totalElements ?? (data.page?.totalElements ?? 0);
    totalPages.value = data.totalPages ?? (data.page?.totalPages ?? Math.ceil(totalElements / 5));
  } catch (e) {
    error.value = 'Không thể tải danh sách sản phẩm. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
}

function changePage(p) {
  currentPage.value = p;
  loadProducts();
}

// ─── Modal ───────────────────────────────────────────────────
function resetForm() {
  Object.assign(form, {
    name: '', sku: '', price: null, stockQuantity: null,
    categoryId: null, imageUrl: '', description: '',
    defaultMaterial: '', defaultSpecification: '', slug: '', isActive: true,
  });
  saveError.value = '';
}

function openAddModal() { resetForm(); editingId.value = null; showModal.value = true; }

function openEditModal(item) {
  resetForm(); editingId.value = item.id;
  Object.assign(form, {
    name: item.name || '', sku: item.sku || '', price: item.price, stockQuantity: item.stockQuantity,
    categoryId: item.categoryId, imageUrl: item.imageUrl || '', description: item.description || '',
    defaultMaterial: item.defaultMaterial || '', defaultSpecification: item.defaultSpecification || '',
    slug: item.slug || '', isActive: item.isActive !== false,
  });
  showModal.value = true;
}

function closeModal() { showModal.value = false; }

async function handleSave() {
  saving.value = true; saveError.value = '';
  try {
    if (editingId.value) { await productAdminAPI.update(editingId.value, { ...form }); } 
    else { await productAdminAPI.create({ ...form }); }
    closeModal();
    await Promise.all([loadStats(), loadProducts()]);
  } catch (e) { saveError.value = e.response?.data?.error || 'Lỗi khi lưu sản phẩm'; } 
  finally { saving.value = false; }
}

async function handleDelete(item) {
  if (!confirm(`Bạn có chắc muốn xoá sản phẩm "${item.name}"?`)) return;
  try {
    await productAdminAPI.delete(item.id);
    await Promise.all([loadStats(), loadProducts()]);
  } catch (e) { alert('Xoá thất bại: ' + (e.response?.data?.error || e.message)); }
}

// ─── Helpers: Pastel Badges ──────────────────────────────────
function formatPrice(price) {
  if (price == null) return '—';
  return new Intl.NumberFormat('vi-VN').format(price) + ' ₫';
}

function statusClass(status) {
  if (status === 'Còn hàng') return 'bg-success bg-opacity-10 text-success border border-success border-opacity-25';
  if (status === 'Sắp hết hàng') return 'bg-warning bg-opacity-10 text-warning border border-warning border-opacity-25';
  return 'bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25';
}

function statusIcon(status) {
  if (status === 'Còn hàng') return 'bi bi-check2-circle';
  if (status === 'Sắp hết hàng') return 'bi bi-exclamation-circle';
  return 'bi bi-x-circle';
}

onMounted(async () => {
  await Promise.all([loadStats(), loadProducts()]);
});
</script>

<style scoped>
.text-navy { color: #0b2e59 !important; }
.btn-navy { background-color: #0b2e59; color: #fff; border: none; transition: 0.3s; }
.btn-navy:hover { background-color: #173b6c; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(11, 46, 89, 0.2); }

/* --- 1. HIỆU ỨNG THỐNG KÊ (HOVER ĐỔI MÀU + RUNG ICON) --- */
.stat-card {
  background-color: #fff;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
  overflow: hidden;
}
.stat-label { color: #6c757d; }
.stat-value { color: #212529; }
.icon-box { background-color: #f8f9fa; transition: all 0.4s; }

/* Set màu mặc định cho Icon */
.theme-primary .stat-icon { color: #0d6efd; }
.theme-success .stat-icon { color: #198754; }
.theme-warning .stat-icon { color: #ffc107; }
.theme-danger  .stat-icon { color: #dc3545; }

/* Trạng thái Hover: Đổi nền, chuyển chữ sang trắng */
.stat-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important; }
.stat-card:hover .stat-label, .stat-card:hover .stat-value, .stat-card:hover .stat-icon { color: #fff !important; }
.stat-card:hover .icon-box { background-color: rgba(255,255,255,0.2) !important; }

/* Gắn màu nền tương ứng khi Hover */
.theme-primary:hover { background-color: #0d6efd !important; }
.theme-success:hover { background-color: #198754 !important; }
.theme-warning:hover { background-color: #ffc107 !important; }
.theme-danger:hover  { background-color: #dc3545 !important; }

/* Icon nhảy múa (Wiggle Animation) */
.stat-card:hover .stat-icon { animation: wiggle 0.6s ease-in-out infinite alternate; }

@keyframes wiggle {
  0% { transform: rotate(0deg) scale(1); }
  25% { transform: rotate(-10deg) scale(1.1); }
  50% { transform: rotate(10deg) scale(1.1); }
  75% { transform: rotate(-5deg) scale(1.1); }
  100% { transform: rotate(0deg) scale(1); }
}

/* --- 2. BẢNG DỮ LIỆU (TRÀN VIỀN, ẢNH TO) --- */
.custom-table { width: 100%; border-collapse: collapse; }
.custom-table th { border-bottom: 2px solid #edf2f7; background-color: #f8fafc; }
.custom-table td { border-bottom: 1px solid #edf2f7; vertical-align: middle; }
.table-row-hover { transition: background-color 0.2s ease; }
.table-row-hover:hover { background-color: #f1f5f9; }

.product-name {
  display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
  max-width: 300px; line-height: 1.4;
}

/* Nút Action vuông bo góc */
.btn-action {
  width: 40px; height: 40px; display: inline-flex; align-items: center; justify-content: center;
  border-radius: 10px; transition: 0.2s; font-size: 1.1rem;
}
.btn-action.text-primary:hover { background-color: #eff6ff !important; border-color: #bfdbfe !important; transform: translateY(-2px); }
.btn-action.text-danger:hover { background-color: #fef2f2 !important; border-color: #fecaca !important; transform: translateY(-2px); }

/* Form Modal */
.custom-input { border: 1px solid #cbd5e1; border-radius: 8px; padding: 0.7rem 1rem; }
.custom-input:focus { border-color: #0b2e59; box-shadow: 0 0 0 3px rgba(11, 46, 89, 0.1); }

/* Modal Overlay */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.5); backdrop-filter: blur(2px);
  z-index: 1050; display: flex; align-items: center; justify-content: center; padding: 1rem;
}
.modal-box { animation: scaleIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes scaleIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
</style>