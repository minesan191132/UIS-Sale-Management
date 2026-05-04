<template>
  <div class="inventory-page p-4 min-vh-100 d-flex flex-column w-100">
    <div class="card border-0 shadow-sm mb-4 inventory-hero">
      <div class="card-body">
        <div class="d-flex flex-column flex-xl-row justify-content-between align-items-start align-items-xl-center gap-3">
          <div>
            <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">
              <i class="bi bi-layers-fill me-2 text-primary"></i>Tổng Kho Vật Tư
            </h2>
            <p class="text-muted mb-0">Tổng hợp vật tư theo mã bản vẽ, quản lý nhanh khối lượng và tồn kho từng mã.</p>
          </div>

          <div class="inventory-toolbar d-flex flex-column flex-md-row gap-2 align-items-stretch align-items-md-center">
            <select v-model="companyFilter" @change="loadData" class="form-select company-select modern-admin-select">
              <option value="">Tất cả công ty</option>
              <option v-for="c in companies" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
            <div class="search-box position-relative">
              <i class="bi bi-search position-absolute text-muted search-icon"></i>
              <input type="text" class="form-control search-input"
                placeholder="Tìm mã bản vẽ, linh kiện..." v-model="search" @input="onSearch">
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-md-3" v-for="(stat, i) in stats" :key="i">
        <div class="card border-0 shadow-sm rounded-4 p-3 h-100 inventory-stat-card hover-lift">
          <div class="d-flex align-items-center gap-3">
            <div class="stat-icon" :style="stat.style">
              <i :class="stat.icon" class="fs-4"></i>
            </div>
            <div>
              <div class="text-muted small fw-bolder text-uppercase" style="font-size:0.75rem; letter-spacing: 0.5px;">{{ stat.label }}</div>
              <div class="fw-bolder fs-4 text-dark">{{ stat.value }}<small v-if="stat.unit" class="text-muted ms-1 fs-6">{{ stat.unit }}</small></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5 flex-grow-1 d-flex flex-column justify-content-center">
      <div class="spinner-grow text-primary mx-auto" role="status" style="width: 3rem; height: 3rem;"></div>
      <p class="text-muted mt-3 fw-bold">Đang tải dữ liệu tổng kho...</p>
    </div>

    <div v-else-if="filteredItems.length === 0" class="card border-0 shadow-sm rounded-4 flex-grow-1">
      <div class="card-body text-center py-5 d-flex flex-column align-items-center justify-content-center">
        <i class="bi bi-inbox fs-1 text-muted opacity-50 mb-3"></i>
        <h5 class="fw-bold text-muted">Kho dữ liệu trống</h5>
        <p class="text-muted mt-1">Chưa có dữ liệu vật tư. Hãy import đơn hàng trước!</p>
      </div>
    </div>

    <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden flex-grow-1 inventory-table-card">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0 modern-table" style="table-layout: fixed; width: 100%;">
          <thead class="bg-light">
            <tr>
              <th style="width: 45px;" class="ps-3 text-center text-muted fw-bold small text-uppercase">STT</th>
              <th style="width: 14%;" class="text-muted fw-bold small text-uppercase">DRAWING NO</th>
              <th style="width: 22%;" class="text-muted fw-bold small text-uppercase">PARTS NAME</th>
              <th style="width: 12%;" class="text-muted fw-bold small text-uppercase">SPEC</th>
              <th style="width: 10%;" class="text-muted fw-bold small text-uppercase">MATERIAL</th>
              <th style="width: 10%;" class="text-center text-muted fw-bold small text-uppercase">TỔNG QTY</th>
              <th style="width: 7%;" class="text-center text-muted fw-bold small text-uppercase">SỐ ĐƠN</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(item, index) in filteredItems" :key="item.drawingNumber">
              <tr @click="toggleExpand(item.drawingNumber)" class="cursor-pointer"
                :class="{
                  'table-active-row': expandedDrawing === item.drawingNumber,
                  'group-bg-even': item._groupIdx % 2 === 0,
                  'group-bg-odd': item._groupIdx % 2 !== 0
                }">
                <td class="ps-3 text-center text-muted fw-medium">{{ index + 1 }}</td>
                <td class="fw-bold text-primary text-truncate">{{ item.drawingNumber }}</td>
                <td class="fw-bold text-dark text-truncate" v-html="item._highlightedName || item.partName || '—'"></td>
                <td class="text-secondary fw-medium text-truncate">{{ item.specification || '' }}</td>
                <td>
                  <span class="badge bg-light text-secondary border fw-medium px-2 py-1">{{ item.material || '' }}</span>
                </td>
                <td class="text-center">
                  <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 px-3 py-2 fw-bolder">{{ item.totalQty }}</span>
                </td>
                <td class="text-center">
                  <span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-2 py-1 fw-bold">{{ item.orderCount }}</span>
                </td>
              </tr>

              <tr v-if="expandedDrawing === item.drawingNumber">
                <td colspan="7" class="p-0 border-0">
                  <div class="expand-content inventory-expand-panel bg-white border-start border-4 border-primary ms-4 p-3 my-2 rounded-3 shadow-sm">
                    <div class="fw-bold small mb-2 text-muted d-flex align-items-center">
                      <i class="bi bi-diagram-3 me-2 fs-5"></i>
                      {{ item.drawingNumber }} — {{ item.partName || '—' }}
                      <span class="ms-3 badge bg-light text-dark border">Tổng: <strong class="text-primary">{{ item.totalQty }}</strong></span>
                    </div>
                    <div v-for="ord in item.orders" :key="ord.orderId" class="d-flex align-items-center gap-3 py-2 small text-secondary border-bottom border-light">
                      <i class="bi bi-arrow-return-right text-muted"></i>
                      <span class="badge bg-light text-dark border px-2 py-1 font-monospace fw-bold">{{ ord.orderNumber }}</span>
                      <span class="fw-bold text-primary">{{ ord.quantity }} cái</span>
                      <span v-if="ord.createdAt" class="text-muted ms-auto"><i class="bi bi-clock me-1"></i>{{ formatDate(ord.createdAt) }}</span>
                    </div>
                    <div v-if="!item.orders?.length" class="text-muted small fst-italic py-2">Không có dữ liệu đơn hàng chi tiết</div>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import apiClient from '../../services/api';

const allItems = ref([]);
const companies = ref([]);
const loading = ref(false);
const search = ref('');
const companyFilter = ref('');
const expandedDrawing = ref(null);
let searchTimer = null;

onMounted(() => {
  loadData();
  loadCompanies();
});

const loadData = async () => {
  loading.value = true;
  try {
    const params = {};
    if (companyFilter.value) params.companyId = companyFilter.value;
    if (search.value?.trim()) params.search = search.value.trim();
    const res = await apiClient.get('/warehouse', { params });
    allItems.value = res.data || [];
  } catch (e) {
    console.error('Failed to load warehouse data:', e);
  } finally {
    loading.value = false;
  }
};

const loadCompanies = async () => {
  try {
    const res = await apiClient.get('/companies', { params: { page: 0, size: 200 } });
    const content = res.data?.content || [];
    companies.value = content.map(c => ({ id: c.id, name: c.companyName }));
  } catch (e) {
    console.error('Failed to load companies:', e);
  }
};

const onSearch = () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    loadData();
  }, 300);
};

// Semantic grouping
const extractBaseName = (name) => {
  if (!name) return '';
  let s = name.replace(/^\d+[A-Za-z]+\s*/, '');
  s = s.split(/\(|（/)[0];
  s = s.trim();
  s = s.replace(/\s*\d+段$/, '');
  s = s.replace(/[A-Za-z0-9\-右左]+$/, '');
  return s.trim() || name;
};

const filteredItems = computed(() => {
  let list = [...allItems.value];

  if (search.value) {
    const s = search.value.toLowerCase();
    list = list.filter(item =>
      (item.drawingNumber && item.drawingNumber.toLowerCase().includes(s)) ||
      (item.partName && item.partName.toLowerCase().includes(s)) ||
      (item.material && item.material.toLowerCase().includes(s)) ||
      (item.specification && item.specification.toLowerCase().includes(s))
    );
  }

  // Semantic grouping by base name
  const groups = new Map();
  list.forEach(item => {
    const base = extractBaseName(item.partName);
    if (!groups.has(base)) groups.set(base, []);
    groups.get(base).push(item);
  });

  const sortedBases = Array.from(groups.keys()).sort((a, b) => a.localeCompare(b));
  let groupIndex = 0;
  const finalItems = [];

  for (const base of sortedBases) {
    const groupItems = groups.get(base);
    groupItems.sort((a, b) => (a.partName || '').localeCompare(b.partName || ''));

    const escaped = base.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const hlRegex = new RegExp(`(${escaped})`, 'i');

    for (const item of groupItems) {
      item._groupIdx = groupIndex;
      if (item.partName && base) {
        item._highlightedName = item.partName.replace(hlRegex, match => `<strong>${match}</strong>`);
      } else {
        item._highlightedName = item.partName || '';
      }
      finalItems.push(item);
    }
    groupIndex++;
  }

  return finalItems;
});

const stats = computed(() => {
  const items = filteredItems.value;
  const totalQty = items.reduce((sum, i) => sum + (i.totalQty || 0), 0);
  const orderIds = new Set();
  items.forEach(i => i.orders?.forEach(o => orderIds.add(o.orderId)));

  return [
    { label: 'Mã bản vẽ', value: items.length, icon: 'bi bi-grid-3x3-gap-fill', style: 'background:#e0f2fe;color:#2563eb;' },
    { label: 'Tổng sản phẩm', value: totalQty.toLocaleString(), icon: 'bi bi-box-fill', style: 'background:#dcfce7;color:#059669;' },
    { label: 'Số đơn hàng', value: orderIds.size, icon: 'bi bi-file-earmark-text-fill', style: 'background:#fef3c7;color:#d97706;' }
  ];
});

const toggleExpand = (drawingNumber) => {
  expandedDrawing.value = expandedDrawing.value === drawingNumber ? null : drawingNumber;
};

const formatDate = (d) => d ? new Date(d).toLocaleDateString('vi-VN') : '';
</script>

<style scoped>
.inventory-page {
  background: linear-gradient(180deg, #f3f7ff 0%, #f8fafc 45%, #f3f4f6 100%);
}

.inventory-hero {
  border: 1px solid #dbeafe;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.1), transparent 45%), #ffffff;
}

.inventory-toolbar {
  width: 100%;
}

.company-select {
  width: 180px;
  min-width: 160px;
  max-width: 210px;
  flex: 0 0 auto;
  font-weight: 500;
}

.search-box {
  width: 100%;
  min-width: 280px;
}

.search-icon {
  top: 50%;
  left: 10px;
  transform: translateY(-50%);
}

.search-input {
  padding-left: 32px;
  border-color: #cbd5e1;
  font-weight: 500;
}

.search-input:focus,
.company-select:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 0.2rem rgba(59, 130, 246, 0.15);
}

.inventory-stat-card {
  border: 1px solid #e2e8f0;
  background: #ffffff;
}

.inventory-table-card {
  border: 1px solid #e2e8f0;
  background: #ffffff;
}

.inventory-expand-panel {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

/* Animation Chung */
.hover-lift { transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.06) !important; }

/* Bảng Dữ Liệu */
.modern-table th { border-bottom: 2px solid #f1f5f9; padding-bottom: 12px; }
.modern-table td { border-bottom: 1px solid #f1f5f9; padding-top: 12px; padding-bottom: 12px; }

.cursor-pointer { cursor: pointer; }
.table-active-row { background-color: #f8fafc !important; border-left: 4px solid #3b82f6 !important; }
.group-bg-even td { background-color: #f8fafc; }
.group-bg-odd td { background-color: #ffffff; }

/* Thống kê Icon */
.stat-icon {
  width: 50px; height: 50px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

/* Form Input Tùy chỉnh trực tiếp trong bảng */
.inline-input {
  width: 80px; padding: 6px 8px;
  border: 1px solid transparent; border-radius: 8px;
  text-align: center; font-size: 0.9rem; font-weight: 600;
  background: #f8fafc; transition: all 0.2s;
  color: #334155;
}
.inline-input:hover { border-color: #cbd5e1; background: #fff; }
.inline-input:focus { outline: none; border-color: #3b82f6; background: #fff; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15); }

@media (max-width: 768px) {
  .search-box {
    min-width: 100%;
  }

  .company-select {
    min-width: 100%;
  }
}

@media (max-width: 992px) {
  .inventory-toolbar {
    width: 100%;
  }
}
</style>