<template>
  <div class="p-4 bg-light min-vh-100">
    <!-- No data -->
    <div v-if="!shipment" class="d-flex align-items-center justify-content-center" style="min-height: 60vh;">
      <div class="text-center" style="max-width: 400px;">
        <i class="bi bi-file-earmark-text fs-1 text-muted d-block mb-3"></i>
        <h5 class="fw-bold">Chưa có phiếu xuất kho</h5>
        <p class="text-muted small">
          Chọn sản phẩm ở trang "Quản lý xuất hoá đơn" rồi bấm "Tạo phiếu xuất kho"
        </p>
        <router-link to="/admin/invoice-management" class="btn btn-primary btn-sm px-4">
          <i class="bi bi-arrow-left me-1"></i>Đi đến trang đơn hàng
        </router-link>
      </div>
    </div>

    <!-- Preview Content -->
    <div v-else>
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h2 class="fw-bold text-dark m-0 fs-4">
            <i class="bi bi-truck me-2"></i>Phiếu xuất kho
          </h2>
          <p class="text-muted small mb-0 mt-1">Xem trước và xuất Excel</p>
        </div>
        <div class="d-flex gap-2">
          <router-link to="/admin/invoice-management" class="btn btn-outline-secondary btn-sm px-3">
            <i class="bi bi-arrow-left me-1"></i>Quay lại
          </router-link>
          <button @click="exportExcel" class="btn btn-success btn-sm px-4" :disabled="exporting">
            <i class="bi bi-file-earmark-excel me-1"></i>
            {{ exporting ? 'Đang xuất...' : 'Xuất Excel' }}
          </button>
        </div>
      </div>

      <!-- Info Card -->
      <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
          <div class="row">
            <div class="col-md-6">
              <h5 class="fw-bold mb-2"><i class="bi bi-file-text me-2"></i>PHIẾU XUẤT KHO</h5>
              <p class="mb-1"><strong>Mã phiếu:</strong> {{ shipment.shipmentCode }}</p>
              <p class="mb-0"><strong>Tiêu đề:</strong> {{ shipment.title }}</p>
            </div>
            <div class="col-md-6 text-md-end">
              <p class="mb-1">
                <strong>Thời gian xuất:</strong>
                <span class="badge bg-primary-subtle text-primary ms-1 px-3 py-1">{{ dateRangeDisplay }}</span>
              </p>
              <p class="mb-1"><strong>Tổng mã bản vẽ:</strong> {{ shipment.totalItems }}</p>
              <p class="mb-0">
                <strong>Tổng SL:</strong>
                <span class="badge bg-primary text-white px-3 py-1 fs-6">{{ shipment.totalQty }}</span>
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Preview Table -->
      <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0 shipment-table">
            <thead class="bg-light">
              <tr class="small fw-bold">
                <th rowspan="2" class="text-center" style="width:36px;">STT</th>
                <th rowspan="2">Drawing No</th>
                <th rowspan="2" style="max-width:120px;">Parts Name</th>
                <th rowspan="2" style="max-width:80px;">Spec</th>
                <th rowspan="2" style="max-width:70px;">Material</th>
                <th rowspan="2" class="text-center" style="width:50px;">Tổng</th>
                <th v-for="grp in dateHeaderGroups" :key="grp.deliveryDate"
                  :colspan="grp.colspan" class="text-center order-col-header">
                  <span class="text-primary">{{ grp.dateDisplay || '—' }}</span>
                </th>
                <th rowspan="2" class="text-center" style="width:70px;">Tồn kho</th>
                <th rowspan="2" class="text-center" style="width:55px;">KL (kg)</th>
              </tr>
              <tr class="small">
                <th v-for="col in orderColumnsData" :key="col.colKey"
                  class="text-center order-col-date">
                  {{ formatVnnShort(col.vnnNo) }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in displayItems" :key="item.drawingNumber"
                :class="{ 'row-even': index % 2 === 0, 'row-odd': index % 2 !== 0 }">
                <td class="text-muted text-center">{{ index + 1 }}</td>
                <td class="fw-semibold text-primary" style="white-space:nowrap;">{{ item.drawingNumber }}</td>
                <td class="text-truncate" style="max-width:120px;">{{ item.partName || '' }}</td>
                <td class="text-muted text-truncate" style="max-width:80px;">{{ item.specification || '' }}</td>
                <td class="text-muted text-truncate" style="max-width:70px;">{{ item.material || '' }}</td>
                <td class="text-center">
                  <span class="badge bg-primary-subtle text-primary px-2 py-1 fw-bold">{{ item.totalQty }}</span>
                </td>
                <td v-for="col in orderColumnsData" :key="col.colKey" class="text-center">
                  {{ getQty(item.drawingNumber, col.colKey) }}
                </td>
                <td class="text-center">
                  <input type="text" class="inline-input stock-input"
                    v-model="stockValues[item.drawingNumber]" placeholder="—">
                </td>
                <td class="text-center">
                  <span class="weight-value">{{ item.weight || 0 }}</span>
                </td>
              </tr>
            </tbody>
            <tfoot>
              <tr class="fw-bold bg-light">
                <td colspan="5" class="text-end">Tổng:</td>
                <td class="text-center">{{ shipment.totalQty }}</td>
                <td v-for="col in orderColumnsData" :key="col.colKey" class="text-center">
                  {{ getTotalForCol(col.colKey) }}
                </td>
                <td class="text-center">—</td>
                <td class="text-center text-info fw-bold">{{ totalWeight.toFixed(1) }}</td>
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
  // Try itemIds first (new flow), fallback to orderIds (legacy)
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
.shipment-table { font-size: 0.82rem; }
.shipment-table td, .shipment-table th { padding: 6px 8px !important; vertical-align: middle; line-height: 1.3; }
.shipment-table tbody tr { height: 36px; }
.row-even td { background-color: #f8fafc; }
.row-odd td { background-color: #ffffff; }
.weight-value { font-weight: 500; color: #0dcaf0; }
.inline-input {
  width: 60px; padding: 4px 4px; border: 1px dashed #dee2e6; border-radius: 6px;
  text-align: center; font-size: 0.8rem; font-weight: 500; background: #fafbfc;
  transition: all 0.15s; color: #333;
}
.inline-input::placeholder { color: #adb5bd; font-weight: 400; }
.inline-input:hover { border-color: #adb5bd; background: #fff; }
.inline-input:focus { outline: none; border-color: #86b7fe; border-style: solid; background: #fff; box-shadow: 0 0 0 2px rgba(13,110,253,.1); }
.stock-input { color: #198754; }
.order-col-header { border-bottom: none !important; padding-bottom: 2px !important; font-weight: 600; }
.order-col-date {
  font-size: 0.78rem !important; font-weight: 600 !important;
  color: #0d6efd !important; background: #e8f4fd !important;
  padding-top: 4px !important; padding-bottom: 4px !important;
  border-top: 1px dashed #dee2e6 !important;
}
</style>