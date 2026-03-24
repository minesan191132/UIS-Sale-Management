<template>
  <div class="products-container p-4">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Quản lý sản phẩm</h2>

    <!-- Thống kê -->
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

    <!-- Danh sách -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 class="fw-bold m-0">Danh sách sản phẩm</h5>
          <small class="text-muted">Quản lý và sửa thông tin sản phẩm</small>
        </div>
        <button class="btn btn-primary btn-sm px-3 rounded-pill fw-bold" @click="openAddModal">
          <i class="bi bi-plus-lg me-1"></i> Thêm sản phẩm mới
        </button>
      </div>

      <!-- Bộ lọc -->
      <div class="row g-2 mb-4">
        <div class="col-md-4">
          <input type="text" class="form-control form-control-sm bg-light border-0"
                 placeholder="Tìm kiếm theo tên..." v-model="filter.keyword">
        </div>
        <div class="col-md-3">
          <select class="form-select form-select-sm bg-light border-0" v-model="filter.status">
            <option value="">Tất cả trạng thái</option>
            <option value="in_stock">Còn hàng</option>
            <option value="low_stock">Sắp hết hàng</option>
            <option value="out_of_stock">Hết hàng</option>
          </select>
        </div>
        <div class="col-md-3">
          <select class="form-select form-select-sm bg-light border-0" v-model="filter.categoryId">
            <option :value="null">Tất cả danh mục</option>
            <option value="1">Linh kiện thép</option>
            <option value="2">Linh kiện nhôm</option>
          </select>
        </div>
        <div class="col-md-2">
          <button class="btn btn-primary btn-sm w-100 fw-bold" @click="loadProducts">Tìm kiếm</button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2 text-muted">Đang tải...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

      <!-- Product list -->
      <div v-else class="product-list d-flex flex-column gap-3">
        <div v-if="products.length === 0" class="text-center text-muted py-5">
          Không tìm thấy sản phẩm nào.
        </div>
        <div v-for="item in products" :key="item.id"
             class="product-item p-3 border rounded-4 bg-white shadow-sm hover-scale transition">
          <div class="row align-items-center">
            <div class="col-md-1 text-center">
              <img v-if="item.imageUrl" :src="item.imageUrl" alt="img"
                   class="rounded-3 border" style="width:60px;height:60px;object-fit:cover"/>
              <div v-else class="bg-light rounded-3 py-3 text-muted small border">HÌNH</div>
            </div>
            <div class="col-md-4">
              <h6 class="fw-bold mb-1">{{ item.name }}</h6>
              <p class="small text-muted mb-0">SKU: <span class="fw-bold">{{ item.sku || '—' }}</span></p>
              <h5 class="fw-bold text-danger mt-1 mb-0">
                {{ formatPrice(item.price) }}
                <small class="text-muted fs-6 fw-normal ms-2">Tồn kho: {{ item.stockQuantity ?? 0 }}</small>
              </h5>
            </div>
            <div class="col-md-3 text-center">
              <span :class="statusClass(item.status)" class="badge px-4 py-2 rounded-pill fw-normal">
                {{ item.status }}
              </span>
            </div>
            <div class="col-md-4 text-end d-flex gap-2 justify-content-end">
              <button class="btn btn-light btn-sm border px-4 rounded-pill" @click="openEditModal(item)">Sửa</button>
              <button class="btn btn-danger btn-sm border px-4 rounded-pill" @click="handleDelete(item)">Xoá</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Phân trang -->
      <div v-if="totalPages > 1" class="d-flex justify-content-center gap-2 mt-4">
        <button class="btn btn-outline-primary btn-sm" :disabled="currentPage === 0" @click="changePage(currentPage - 1)">‹</button>
        <span class="btn btn-sm disabled">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button class="btn btn-outline-primary btn-sm" :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">›</button>
      </div>
    </div>

    <!-- Modal Thêm/Sửa -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box card shadow-lg p-4 rounded-4" style="max-width:540px;width:100%">
        <h5 class="fw-bold mb-3">{{ editingId ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới' }}</h5>
        <form @submit.prevent="handleSave">
          <div class="mb-2">
            <label class="form-label small fw-bold">Tên sản phẩm *</label>
            <input class="form-control form-control-sm" v-model="form.name" required />
          </div>
          <div class="row g-2 mb-2">
            <div class="col">
              <label class="form-label small fw-bold">SKU</label>
              <input class="form-control form-control-sm" v-model="form.sku" />
            </div>
            <div class="col">
              <label class="form-label small fw-bold">Giá (VNĐ)</label>
              <input class="form-control form-control-sm" type="number" v-model="form.price" />
            </div>
          </div>
          <div class="row g-2 mb-2">
            <div class="col">
              <label class="form-label small fw-bold">Tồn kho</label>
              <input class="form-control form-control-sm" type="number" v-model="form.stockQuantity" />
            </div>
            <div class="col">
              <label class="form-label small fw-bold">Danh mục</label>
              <select class="form-select form-select-sm" v-model="form.categoryId">
                <option :value="null">-- Chọn --</option>
                <option value="1">Linh kiện thép</option>
                <option value="2">Linh kiện nhôm</option>
              </select>
            </div>
          </div>
          <div class="mb-2">
            <label class="form-label small fw-bold">URL Hình ảnh</label>
            <input class="form-control form-control-sm" v-model="form.imageUrl" />
          </div>
          <div class="mb-2">
            <label class="form-label small fw-bold">Mô tả</label>
            <textarea class="form-control form-control-sm" rows="2" v-model="form.description"></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label small fw-bold">Chất liệu mặc định</label>
            <input class="form-control form-control-sm" v-model="form.defaultMaterial" />
          </div>
          <div v-if="saveError" class="alert alert-danger py-2 small">{{ saveError }}</div>
          <div class="d-flex gap-2 justify-content-end">
            <button type="button" class="btn btn-light btn-sm px-4 rounded-pill border" @click="closeModal">Huỷ</button>
            <button type="submit" class="btn btn-primary btn-sm px-4 rounded-pill fw-bold" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
              {{ editingId ? 'Lưu thay đổi' : 'Thêm mới' }}
            </button>
          </div>
        </form>
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

// ─── Stats Cards ─────────────────────────────────────────────
const productStats = computed(() => [
  { label: 'Tổng sản phẩm',  value: stats.total,       bgClass: 'bg-danger',  icon: 'bi bi-box-seam' },
  { label: 'Đang hoạt động', value: stats.active,      bgClass: 'bg-success', icon: 'bi bi-check-circle' },
  { label: 'Hết hàng',       value: stats.outOfStock,  bgClass: 'bg-purple',  icon: 'bi bi-slash-circle' },
  { label: 'Sắp hết hàng',   value: stats.lowStock,    bgClass: 'bg-indigo',  icon: 'bi bi-exclamation-triangle' },
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

async function loadProducts() {
  loading.value = true;
  error.value = '';
  try {
    const data = await productAdminAPI.getAll({
      keyword: filter.keyword,
      categoryId: filter.categoryId,
      status: filter.status,
      page: currentPage.value,
      size: 10,
    });
    products.value = data.content ?? [];
    totalPages.value = data.totalPages ?? 0;
  } catch (e) {
    error.value = 'Không thể tải danh sách sản phẩm. Vui lòng thử lại.';
    console.error('loadProducts error', e);
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

function openAddModal() {
  resetForm();
  editingId.value = null;
  showModal.value = true;
}

function openEditModal(item) {
  resetForm();
  editingId.value = item.id;
  Object.assign(form, {
    name: item.name || '',
    sku: item.sku || '',
    price: item.price,
    stockQuantity: item.stockQuantity,
    categoryId: item.categoryId,
    imageUrl: item.imageUrl || '',
    description: item.description || '',
    defaultMaterial: item.defaultMaterial || '',
    defaultSpecification: item.defaultSpecification || '',
    slug: item.slug || '',
    isActive: item.isActive !== false,
  });
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
}

async function handleSave() {
  saving.value = true;
  saveError.value = '';
  try {
    if (editingId.value) {
      await productAdminAPI.update(editingId.value, { ...form });
    } else {
      await productAdminAPI.create({ ...form });
    }
    closeModal();
    await Promise.all([loadStats(), loadProducts()]);
  } catch (e) {
    saveError.value = e.response?.data?.error || 'Lỗi khi lưu sản phẩm';
  } finally {
    saving.value = false;
  }
}

async function handleDelete(item) {
  if (!confirm(`Bạn có chắc muốn xoá sản phẩm "${item.name}"?`)) return;
  try {
    await productAdminAPI.delete(item.id);
    await Promise.all([loadStats(), loadProducts()]);
  } catch (e) {
    alert('Xoá thất bại: ' + (e.response?.data?.error || e.message));
  }
}

// ─── Helpers ─────────────────────────────────────────────────
function formatPrice(price) {
  if (price == null) return '—';
  return new Intl.NumberFormat('vi-VN').format(price) + '₫';
}

function statusClass(status) {
  if (status === 'Còn hàng') return 'bg-success text-white';
  if (status === 'Sắp hết hàng') return 'bg-warning text-dark';
  return 'bg-secondary text-white';
}

// ─── Init ────────────────────────────────────────────────────
onMounted(async () => {
  await Promise.all([loadStats(), loadProducts()]);
});
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
  border-color: #0d6efd !important;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  z-index: 1050;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}
.modal-box {
  background: #fff;
  animation: fadeIn .2s ease;
  max-height: 90vh;
  overflow-y: auto;
}
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: none; } }
</style>