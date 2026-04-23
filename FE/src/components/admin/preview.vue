<template>
  <div class="invoice-preview-page p-4 min-vh-100 d-flex flex-column" style="background-color: #f8f9fa;">
    
    <div v-if="!shipment" class="d-flex align-items-center justify-content-center flex-grow-1" style="min-height: 70vh;">
      <div class="text-center empty-preview-card floating-card">
        <div class="icon-circle bg-primary bg-opacity-10 text-primary mx-auto mb-4 d-flex align-items-center justify-content-center" style="width: 90px; height: 90px; border-radius: 50%;">
          <i class="bi bi-file-earmark-x fs-1"></i>
        </div>
        <h4 class="fw-bolder text-dark mb-2">Chưa có phiếu xuất kho</h4>
        <p class="text-muted small mb-4 px-3">
          Bạn cần chọn ít nhất 1 sản phẩm ở trang <b>"Quản lý xuất hoá đơn"</b> rồi bấm nút <b>"Tạo phiếu xuất kho"</b> để xem trước tại đây.
        </p>
        <router-link to="/admin/invoice-management" class="btn btn-navy rounded-pill px-4 py-2 fw-bold shadow-sm hover-lift">
          <i class="bi bi-arrow-left me-2"></i> Quay lại trang Đơn hàng
        </router-link>
      </div>
    </div>

    <div v-else class="flex-grow-1 d-flex flex-column">
      
      <div class="d-flex flex-column flex-lg-row justify-content-between align-items-lg-end mb-4 pb-2 border-bottom border-secondary border-opacity-25">
        <div class="mb-3 mb-lg-0">
          <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">
            <i class="bi bi-truck me-2 text-navy"></i> Phiếu xuất kho
          </h2>
          <p class="text-muted small mb-0">Xem trước thông tin và xuất dữ liệu ra file Excel.</p>
        </div>
        <div class="d-flex gap-2 preview-actions">
          <router-link to="/admin/invoice-management" class="btn btn-light border rounded-pill px-4 py-2 fw-bold text-secondary hover-lift">
            <i class="bi bi-arrow-left me-1"></i> Quay lại
          </router-link>
          <button @click="exportExcel" class="btn btn-success rounded-pill px-4 py-2 fw-bold shadow-sm hover-lift d-flex align-items-center" :disabled="exporting">
            <i class="bi bi-file-earmark-excel-fill me-2 fs-5"></i>
            <span v-if="exporting" class="spinner-border spinner-border-sm me-2"></span>
            {{ exporting ? 'Đang xử lý...' : 'Xuất file Excel' }}
          </button>
        </div>
      </div>

      <div class="card border-0 shadow-sm mb-4 rounded-4 bg-white overflow-hidden">
        <div class="bg-navy text-white px-4 py-3 d-flex align-items-center justify-content-between receipt-header">
          <h5 class="fw-bolder mb-0 fs-5 text-uppercase letter-spacing-1"><i class="bi bi-receipt-cutoff me-2"></i> THÔNG TIN PHIẾU XUẤT</h5>
          <span class="badge bg-white text-navy px-3 py-2 fw-bolder fs-6 rounded-pill shadow-sm">Tổng SL: {{ shipment.totalQty }}</span>
        </div>
        
        <div class="card-body p-4 bg-slate-50">
          <div class="row align-items-center">
            <div class="col-md-7">
              <div class="mb-2">
                <span class="text-muted small fw-bolder text-uppercase me-2">Mã phiếu xuất:</span>
                <span class="fw-bold text-dark font-monospace fs-6">{{ shipment.shipmentCode }}</span>
              </div>
              <div>
                <span class="text-muted small fw-bolder text-uppercase me-2">Tiêu đề / Ghi chú:</span>
                <span class="text-secondary fw-medium">{{ shipment.title }}</span>
              </div>
            </div>
            
            <div class="col-md-5 text-md-end mt-3 mt-md-0">
              <div class="mb-2">
                <span class="text-muted small fw-bolder text-uppercase me-2">Thời gian xuất:</span>
                <span class="badge bg-info bg-opacity-10 text-info border border-info border-opacity-25 px-3 py-1 fw-bold rounded-pill fs-6">{{ dateRangeDisplay }}</span>
              </div>
              <div>
                <span class="text-muted small fw-bolder text-uppercase me-2">Tổng mã bản vẽ:</span>
                <span class="fw-bolder text-dark fs-5 align-middle">{{ shipment.totalItems }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white flex-grow-1">
        <div class="table-responsive pb-2">
          <table class="table modern-table align-middle mb-0 w-100" style="table-layout: fixed;">
            <thead class="bg-light">
              <tr>
                <th rowspan="2" class="text-center py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="width: 50px;">STT</th>
                <th rowspan="2" class="py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="width: 140px;">Drawing No</th>
                <th rowspan="2" class="py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="max-width: 180px;">Parts Name</th>
                <th rowspan="2" class="py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="max-width: 120px;">Spec</th>
                <th rowspan="2" class="py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="max-width: 90px;">Material</th>
                <th rowspan="2" class="text-center py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="width: 70px;">Tổng</th>
                
                <th v-for="grp in dateHeaderGroups" :key="grp.deliveryDate" :colspan="grp.colspan" class="text-center py-2 text-navy fw-bolder small text-uppercase border-bottom border-light">
                  <i class="bi bi-calendar-event me-1 opacity-50"></i> {{ grp.dateDisplay || 'Chưa xếp lịch' }}
                </th>
                
                <th rowspan="2" class="text-center py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="width: 80px;">Tồn kho</th>
                <th rowspan="2" class="text-center py-3 text-muted fw-bold small text-uppercase border-bottom-0" style="width: 80px;">KL (kg)</th>
              </tr>
              <tr>
                <th v-for="col in orderColumnsData" :key="col.colKey" class="text-center py-2 text-primary fw-bold font-monospace bg-primary bg-opacity-10 border-top-0" style="font-size: 0.75rem;">
                  {{ formatVnnShort(col.vnnNo) }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in displayItems" :key="item.drawingNumber" class="table-row-hover border-bottom border-light" :class="{ 'bg-slate-50': index % 2 === 0, 'bg-white': index % 2 !== 0 }">
                <td class="text-muted text-center fw-medium">{{ index + 1 }}</td>
                <td class="fw-bolder text-navy font-monospace small text-truncate">{{ item.drawingNumber }}</td>
                <td class="fw-bold text-dark text-truncate" :title="item.partName">{{ item.partName || '—' }}</td>
                <td class="text-secondary fw-medium small text-truncate" :title="item.specification">{{ item.specification || '—' }}</td>
                <td class="text-muted small text-truncate">{{ item.material || '—' }}</td>
                <td class="text-center">
                  <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 px-2 py-1 fw-bold">{{ item.totalQty }}</span>
                </td>
                
                <td v-for="col in orderColumnsData" :key="col.colKey" class="text-center fw-bold text-dark bg-white">
                  {{ getQty(item.drawingNumber, col.colKey) || '—' }}
                </td>
                
                <td class="text-center bg-white" @click.stop>
                  <input type="text" class="form-control form-control-sm text-center fw-bold text-success shadow-none border-success border-opacity-25 bg-success bg-opacity-10" v-model="stockValues[item.drawingNumber]" placeholder="—" style="max-width: 60px; margin: 0 auto;">
                </td>
                
                <td class="text-center fw-bold text-info bg-white">
                  {{ item.weight || 0 }}
                </td>
              </tr>
            </tbody>
            <tfoot v-if="displayItems.length > 0">
              <tr class="fw-bold bg-light">
                <td colspan="5" class="text-end py-3 text-muted text-uppercase small letter-spacing-1">Tổng cộng:</td>
                <td class="text-center py-3 fs-6 text-primary">{{ shipment.totalQty }}</td>
                <td v-for="col in orderColumnsData" :key="col.colKey" class="text-center py-3 text-dark">
                  {{ getTotalForCol(col.colKey) }}
                </td>
                <td class="text-center py-3 text-muted">—</td>
                <td class="text-center py-3 text-info fs-6">{{ totalWeight.toFixed(1) }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import apiClient from '../../services/api';

// KEEPS ALL YOUR ORIGINAL SCRIPT LOGIC EXACTLY INTACT
const shipment = ref(null);
const stockValues = reactive({});
const exporting = ref(false);

onMounted(() => {
  const data = sessionStorage.getItem('shipmentPreview');
  if (data) {
    shipment.value = JSON.parse(data);
    shipment.value.items?.forEach(item => {
      stockValues[item.drawingNumber] = '';
    });
  }
});

const displayItems = computed(() => {
  if (!shipment.value?.items) return [];
  return [...shipment.value.items].sort((a, b) =>
    String(a.drawingNumber || '').localeCompare(String(b.drawingNumber || ''))
  );
});

// Dynamic order columns
const orderColumnsData = computed(() => {
  if (!shipment.value?.items) return [];
  const colMap = new Map();
  shipment.value.items.forEach(item => {
    item.orderBreakdown?.forEach(ob => {
      const colKey = ob.vnnNo + '|' + (ob.deliveryDate || '');
      if (!colMap.has(colKey)) {
        colMap.set(colKey, {
          colKey,
          vnnNo: ob.vnnNo,
          deliveryDate: ob.deliveryDate,
          dateDisplay: ob.deliveryDate ? formatDate(ob.deliveryDate) : ''
        });
      }
    });
  });
  return [...colMap.values()].sort((a, b) => {
    const dateA = a.deliveryDate || '';
    const dateB = b.deliveryDate || '';
    if (dateA !== dateB) return dateA.localeCompare(dateB);
    return a.vnnNo.localeCompare(b.vnnNo);
  });
});

const dateHeaderGroups = computed(() => {
  const cols = orderColumnsData.value;
  if (!cols.length) return [];
  const groups = [];
  let cur = { deliveryDate: cols[0].deliveryDate, dateDisplay: cols[0].dateDisplay, colspan: 1 };
  for (let i = 1; i < cols.length; i++) {
    if (cols[i].deliveryDate === cur.deliveryDate) cur.colspan++;
    else {
      groups.push({ ...cur });
      cur = { deliveryDate: cols[i].deliveryDate, dateDisplay: cols[i].dateDisplay, colspan: 1 };
    }
  }
  groups.push({ ...cur });
  return groups;
});

// Qty lookup
const qtyLookup = computed(() => {
  const map = new Map();
  if (!displayItems.value.length) return map;
  displayItems.value.forEach(item => {
    const inner = new Map();
    item.orderBreakdown?.forEach(ob => {
      const colKey = ob.vnnNo + '|' + (ob.deliveryDate || '');
      inner.set(colKey, (inner.get(colKey) || 0) + ob.quantity);
    });
    map.set(item.drawingNumber, inner);
  });
  return map;
});

const getQty = (drawingNumber, colKey) => qtyLookup.value.get(drawingNumber)?.get(colKey) || '';
const getTotalForCol = (colKey) => {
  let total = 0;
  displayItems.value.forEach(item => {
    total += qtyLookup.value.get(item.drawingNumber)?.get(colKey) || 0;
  });
  return total || '';
};

const dateRangeDisplay = computed(() => {
  if (!shipment.value?.items) return '—';
  const dates = [];
  shipment.value.items.forEach(i => {
    i.orderBreakdown?.forEach(ob => {
      if (ob.deliveryDate) dates.push(ob.deliveryDate);
    });
  });
  if (!dates.length) return '—';
  dates.sort();
  const min = formatDate(dates[0]), max = formatDate(dates[dates.length - 1]);
  return min === max ? min : `${min} — ${max}`;
});

const totalWeight = computed(() => {
  if (!displayItems.value.length) return 0;
  return displayItems.value.reduce((sum, i) => sum + (i.weight || 0) * (i.totalQty || 0), 0);
});

const formatVnnShort = (vnn) => {
  if (!vnn) return '';
  const p = vnn.split('-');
  return p.length > 1 ? p.slice(0, 2).join('-') : vnn;
};

const formatDate = (d) => {
  if (!d) return '—';
  if (d.includes('-')) {
    const [year, month, day] = d.split('-');
    return `${day}/${month}/${year}`;
  }
  return new Date(d).toLocaleDateString('vi-VN');
};

const exportExcel = async () => {
  const itemIdsRaw = sessionStorage.getItem('shipmentItemIds');
  const orderIdsRaw = sessionStorage.getItem('shipmentOrderIds');

  let requestBody = {};
  if (itemIdsRaw) {
    requestBody = { itemIds: JSON.parse(itemIdsRaw) };
  } else if (orderIdsRaw) {
    requestBody = { orderIds: JSON.parse(orderIdsRaw) };
  } else {
    alert('Không tìm thấy danh sách sản phẩm. Vui lòng quay lại và chọn lại.');
    return;
  }

  exporting.value = true;
  try {
    const res = await apiClient.post('/shipments/export-excel', requestBody, { responseType: 'blob' });
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `phieu-xuat-kho-${shipment.value.shipmentCode}.xlsx`;
    link.click();
    window.URL.revokeObjectURL(url);
  } catch (e) {
    alert('Lỗi xuất Excel: ' + (e.response?.data?.error || e.message));
  } finally {
    exporting.value = false;
  }
};
</script>

<style scoped>
/* ─── MÀU SẮC CHUNG ─── */
.text-navy { color: #0b2e59 !important; }
.bg-navy { background-color: #0b2e59 !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.letter-spacing-1 { letter-spacing: 1px; }

/* ─── NÚT BẤM ─── */
.btn-navy { background-color: #0b2e59; color: #fff; border: none; transition: 0.3s; }
.btn-navy:hover { background-color: #173b6c; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(11, 46, 89, 0.2); }

.hover-lift { transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-3px); box-shadow: 0 8px 15px rgba(0,0,0,0.06) !important; }

/* ─── EMPTY STATE (Floating Card) ─── */
.floating-card {
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: #ffffff;
  padding: 3rem 2rem;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  animation: float 4s ease-in-out infinite; /* Hiệu ứng lơ lửng */
  max-width: 450px;
}
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

/* ─── INFO CARD (Receipt Style) ─── */
.receipt-header {
  border-bottom: 2px dashed rgba(255,255,255,0.3); /* Đường đứt nét tạo cảm giác xé biên lai */
}

/* ─── BẢNG DỮ LIỆU (Modern Table) ─── */
.modern-table { border-collapse: separate; border-spacing: 0; }
.modern-table thead th { vertical-align: middle; border-bottom: 2px solid #e2e8f0 !important; }
.modern-table tbody td { vertical-align: middle; }
.table-row-hover { transition: background-color 0.2s ease; }
.table-row-hover:hover { background-color: #f1f5f9 !important; }

/* Border radius cho các góc của thẻ thead */
.modern-table thead tr:first-child th:first-child { border-top-left-radius: 12px; }
.modern-table thead tr:first-child th:last-child { border-top-right-radius: 12px; }

/* ─── RESPONSIVE ─── */
@media (max-width: 992px) {
  .border-end-md { border-right: none !important; border-bottom: 1px solid #dee2e6; padding-bottom: 1rem; margin-bottom: 1rem; }
  .ps-md-4 { padding-left: 0 !important; }
}

@media (max-width: 768px) {
  .preview-actions { width: 100%; flex-direction: column; }
  .preview-actions .btn { width: 100%; justify-content: center; }
}
</style>