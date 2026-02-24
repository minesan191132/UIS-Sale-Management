<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase">Quản lý xuất hoá đơn</h2>

    <div class="card border-0 shadow-sm p-3 mb-4">
      <div class="row g-3 align-items-end justify-content-between">
        <div class="col-md-9">
          <div class="row g-2">
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Tìm kiếm từ khoá</label>
              <input type="text" class="form-control form-control-sm" placeholder="Tìm VNN No...">
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Trạng thái</label>
              <select class="form-select form-select-sm">
                <option>Đang thực hiện</option>
                <option>Hoàn thành</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Từ ngày</label>
              <input type="date" class="form-control form-control-sm">
            </div>
            <div class="col-md-3">
              <label class="small fw-bold text-muted mb-1">Đến ngày</label>
              <input type="date" class="form-control form-control-sm">
            </div>
          </div>
        </div>
        <div class="col-md-3 d-flex gap-2 justify-content-end">
          <button class="btn btn-primary btn-sm px-3">Tìm kiếm</button>
          <button class="btn btn-outline-secondary btn-sm px-3">Đặt lại</button>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <table class="table align-middle mb-0 custom-hover-table" style="table-layout: fixed;">
        <thead class="bg-light shadow-sm">
          <tr class="small text-muted text-uppercase">
            <th style="width: 50px;" class="text-center">
              <input type="checkbox" class="form-check-input" v-model="selectAll" @change="toggleSelectAll">
            </th>
            <th style="width: 250px;">Mã đơn (VNN NO)</th>
            <th>Ngày giao hàng</th>
            <th>Số lượng Item</th>
            <th style="width: 150px;">Trạng thái</th>
          </tr>
        </thead>
        <tbody v-for="order in orders" :key="order.id">
          <tr @click="toggleDetails(order.id)" class="cursor-pointer" :class="{'table-active-row': expandedRows.includes(order.id)}">
            <td class="text-center" @click.stop>
              <input type="checkbox" class="form-check-input" v-model="order.selected">
            </td>
            <td class="fw-bold text-primary">{{ order.vnnNo }}</td>
            <td>{{ order.deliveryDate }}</td>
            <td>
              <span class="badge bg-info-subtle text-info px-3">
                {{ order.details.length }} chi tiết
              </span>
            </td>
            <td>
              <span class="badge bg-danger px-3">Active</span>
            </td>
          </tr>

          <tr v-if="expandedRows.includes(order.id)">
            <td colspan="5" class="p-0 border-0 shadow-inner">
              <div class="bg-white p-4 border-start border-4 border-primary ms-5 my-2 rounded shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <h6 class="fw-bold m-0 text-muted"><i class="bi bi-box-seam me-2"></i>Chi tiết đơn hàng {{ order.vnnNo }}</h6>
                </div>
                <table class="table table-sm table-bordered mb-0">
                  <thead class="table-dark">
                    <tr class="text-center small">
                      <th>ITEM CODE</th>
                      <th>BẢN VẼ</th>
                      <th>TÊN LINH KIỆN</th>
                      <th>SL</th>
                    </tr>
                  </thead>
                  <tbody class="text-center">
                    <tr v-for="detail in order.details" :key="detail.itemCode">
                      <td class="fw-bold">{{ detail.itemCode }}</td>
                      <td>{{ detail.drawing }}</td>
                      <td class="text-start">{{ detail.name }}</td>
                      <td class="fw-bold text-primary">{{ detail.quantity }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="p-3 bg-white border-top d-flex justify-content-between align-items-center">
        <span class="fw-bold text-primary fs-6">
          <i class="bi bi-check-all me-1"></i> Đã chọn: {{ selectedCount }} đơn hàng
        </span>
        <button class="btn btn-success btn-sm px-4" :disabled="selectedCount === 0">
          <i class="bi bi-file-earmark-excel me-2"></i>Tạo phiếu xuất kho
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const selectAll = ref(false);
const expandedRows = ref([]);

const orders = ref([
  {
    id: 1,
    vnnNo: '25-A0225-25-0064',
    deliveryDate: '19/01/2026',
    selected: false,
    details: [
      { itemCode: '653006007', drawing: 'KAAS0022A', name: 'AB(36L)', quantity: 12 },
      { itemCode: '653006009', drawing: '65XXX6093', name: 'Tấm chặn (22W)', quantity: 12 },
      { itemCode: '702900283', drawing: 'BGJF0004', name: 'Đệm cao su (36L)', quantity: 6 }
    ]
  },
  {
    id: 2,
    vnnNo: '26-B0126-26-1199',
    deliveryDate: '29/01/2026',
    selected: false,
    details: [
      { itemCode: '702900555', drawing: 'XYZ-123', name: 'Ốc vít 5mm', quantity: 100 }
    ]
  }
]);

const toggleSelectAll = () => {
  orders.value.forEach(order => order.selected = selectAll.value);
};

const selectedCount = computed(() => {
  return orders.value.filter(o => o.selected).length;
});

const toggleDetails = (id) => {
  const index = expandedRows.value.indexOf(id);
  if (index > -1) {
    expandedRows.value.splice(index, 1);
  } else {
    expandedRows.value.push(id);
  }
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.table-active-row {
  background-color: #f8f9fa !important;
  border-left: 4px solid #0d6efd !important;
}
.custom-hover-table tbody tr:hover {
  background-color: #f1f8ff;
}
.shadow-inner {
  background-color: #fbfcfd;
}
.form-check-input {
  cursor: pointer;
}
</style>