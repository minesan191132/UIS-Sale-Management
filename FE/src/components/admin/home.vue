<template>
  <div class="p-4 bg-light min-vh-100">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-start mb-4">
      <div>
        <h2 class="fw-bold text-dark m-0 fs-4">
          <i class="bi bi-box-seam me-2"></i>TỔNG KHO VẬT TƯ
        </h2>
        <p class="text-muted small mb-0 mt-1">Tổng hợp vật tư theo mã bản vẽ — dữ liệu khối lượng từ V4 seed</p>
      </div>
      <div class="d-flex gap-2 align-items-center">
        <select v-model="companyFilter" @change="loadData" class="form-select form-select-sm" style="width: 180px">
          <option value="">Tất cả công ty</option>
          <option v-for="c in companies" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <div class="input-group" style="width: 280px;">
          <span class="input-group-text bg-white border-end-0 py-1"><i class="bi bi-search"></i></span>
          <input type="text" class="form-control border-start-0 py-1 shadow-none"
            placeholder="Tìm mã bản vẽ, linh kiện..." v-model="search" @input="onSearch">
        </div>
      </div>
    </div>

    <!-- Stats -->
    <div class="row g-3 mb-4">
      <div class="col-md-3" v-for="(stat, i) in stats" :key="i">
        <div class="card border-0 shadow-sm p-3 h-100">
          <div class="d-flex align-items-center gap-3">
            <div class="stat-icon" :style="stat.style">
              <i :class="stat.icon"></i>
            </div>
            <div>
              <div class="text-muted small fw-bold text-uppercase" style="font-size:0.72rem;">{{ stat.label }}</div>
              <div class="fw-bold fs-5">{{ stat.value }}<small v-if="stat.unit" class="text-muted ms-1">{{ stat.unit }}</small></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">Đang tải dữ liệu kho...</p>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredItems.length === 0" class="card border-0 shadow-sm">
      <div class="card-body text-center py-5">
        <i class="bi bi-inbox fs-1 text-muted"></i>
        <p class="text-muted mt-3">Chưa có dữ liệu vật tư. Hãy import đơn hàng trước!</p>
      </div>
    </div>

    <!-- Table -->
    <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
          <thead class="bg-light">
            <tr class="text-dark fw-bold small">
              <th style="width: 45px;" class="ps-3 text-center">STT</th>
              <th style="width: 14%;">DRAWING NO</th>
              <th style="width: 22%;">PARTS NAME</th>
              <th style="width: 12%;">SPEC</th>
              <th style="width: 10%;">MATERIAL</th>
              <th style="width: 10%;" class="text-center">TỔNG QTY</th>
              <th style="width: 11%;" class="text-center">KL (kg)</th>
              <th style="width: 9%;" class="text-center">TỒN KHO</th>
              <th style="width: 7%;" class="text-center">SỐ ĐƠN</th>
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
                <td class="ps-3 text-center text-muted">{{ index + 1 }}</td>
                <td class="fw-bold text-primary text-truncate">{{ item.drawingNumber }}</td>
                <td class="fw-medium text-truncate" v-html="item._highlightedName || item.partName || '—'"></td>
                <td class="text-muted text-truncate">{{ item.specification || '' }}</td>
                <td>
                  <span class="badge bg-light text-dark border-0 fw-normal px-2 py-1">{{ item.material || '' }}</span>
                </td>
                <td class="text-center">
                  <span class="badge bg-primary-subtle text-primary px-3 py-1 fw-bold">{{ item.totalQty }}</span>
                </td>
                <td class="text-center" @click.stop>
                  <input type="number" step="0.1" min="0"
                    class="inline-input weight-input"
                    :class="{ 'weight-empty': item.weight == null }"
                    :value="item.weight"
                    :placeholder="item.weight == null ? 'Chưa có' : ''"
                    @change="updateMeta(item.drawingNumber, 'weight', $event.target.value)">
                </td>
                <td class="text-center" @click.stop>
                  <input type="number" step="1" min="0"
                    class="inline-input stock-input"
                    :value="item.stock"
                    @change="updateMeta(item.drawingNumber, 'stock', $event.target.value)">
                </td>
                <td class="text-center">
                  <span class="badge bg-success-subtle text-success px-2 py-1">{{ item.orderCount }}</span>
                </td>
              </tr>

              <!-- Expanded order breakdown -->
              <tr v-if="expandedDrawing === item.drawingNumber">
                <td colspan="9" class="p-0 border-0">
                  <div class="expand-content bg-white border-start border-4 border-primary ms-4 p-3 my-1 rounded shadow-sm">
                    <div class="fw-bold small mb-2 text-muted">
                      <i class="bi bi-diagram-3 me-1"></i>
                      {{ item.drawingNumber }} — {{ item.partName || '—' }}
                      <span class="ms-2">Tổng: <strong class="text-primary">{{ item.totalQty }}</strong></span>
                      <span class="ms-2" v-if="item.weight != null">| KL: <strong class="text-info">{{ item.weight }} kg</strong></span>
                      <span class="ms-2 text-warning fst-italic" v-else>| KL: Chưa có</span>
                    </div>
                    <div v-for="ord in item.orders" :key="ord.orderId" class="d-flex align-items-center gap-2 py-1 small text-secondary">
                      <i class="bi bi-arrow-return-right text-muted" style="font-size:0.7rem;"></i>
                      <span class="fw-bold text-primary">{{ ord.orderNumber }}</span>
                      <span class="fw-medium">{{ ord.quantity }} cái</span>
                      <span v-if="ord.createdAt" class="text-muted" style="font-size:0.78rem;">{{ formatDate(ord.createdAt) }}</span>
                    </div>
                    <div v-if="!item.orders?.length" class="text-muted small fst-italic">Không có dữ liệu đơn hàng</div>
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
  const totalWeight = items.reduce((sum, i) => sum + ((i.weight || 0) * (i.totalQty || 0)), 0);

  return [
    { label: 'Mã bản vẽ', value: items.length, icon: 'bi bi-grid-3x3-gap', style: 'background:#e8f4fd;color:#0d6efd;' },
    { label: 'Tổng sản phẩm', value: totalQty.toLocaleString(), icon: 'bi bi-box', style: 'background:#e8f8e8;color:#198754;' },
    { label: 'Số đơn hàng', value: orderIds.size, icon: 'bi bi-file-earmark-text', style: 'background:#fff3e0;color:#fd7e14;' },
    { label: 'Tổng khối lượng', value: totalWeight.toFixed(1), unit: 'kg', icon: 'bi bi-speedometer2', style: 'background:#fce4ec;color:#dc3545;' }
  ];
});

const toggleExpand = (drawingNumber) => {
  expandedDrawing.value = expandedDrawing.value === drawingNumber ? null : drawingNumber;
};

const updateMeta = async (drawingNumber, field, value) => {
  try {
    const body = {};
    body[field] = field === 'weight' ? parseFloat(value) || 0 : parseInt(value) || 0;
    await apiClient.put(`/warehouse/meta/${encodeURIComponent(drawingNumber)}`, body);
    const item = allItems.value.find(i => i.drawingNumber === drawingNumber);
    if (item) item[field] = body[field];
  } catch (e) {
    console.error('Failed to update meta:', e);
  }
};

const formatDate = (d) => d ? new Date(d).toLocaleDateString('vi-VN') : '';
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }
.table-active-row { background-color: #e3f2fd !important; border-left: 4px solid #0d6efd !important; }
.group-bg-even td { background-color: #e8edf5; }
.group-bg-odd td { background-color: #ffffff; }

.stat-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.1rem; flex-shrink: 0;
}

.inline-input {
  width: 75px; padding: 3px 6px;
  border: 1px solid transparent; border-radius: 6px;
  text-align: center; font-size: 0.85rem; font-weight: 500;
  background: transparent; transition: all 0.15s;
  color: #333;
}
.inline-input:hover { border-color: #dee2e6; background: #fff; }
.inline-input:focus { outline: none; border-color: #86b7fe; background: #fff; box-shadow: 0 0 0 2px rgba(13,110,253,.1); }
.weight-input { color: #0dcaf0; }
.weight-empty { border: 1px dashed #ffc107 !important; background: rgba(255,193,7,.05); color: #ffc107; }
.weight-empty::placeholder { color: #ffc107; opacity: 0.7; font-size: 0.75rem; }
.stock-input { color: #198754; }
</style>