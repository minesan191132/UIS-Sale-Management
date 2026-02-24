<template>
  <div class="preview-container p-4">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Xem trước hoá đơn</h2>

    <div class="card border-0 shadow-sm p-4 mb-4 bg-white rounded-4">
      <div class="row g-3 align-items-center mb-3">
        <div class="col-md-2">
          <label class="fw-bold text-dark small text-uppercase">Số Hoá Đơn</label>
        </div>
        <div class="col-md-4">
          <input type="text" class="form-control bg-light border-0 py-2 shadow-none" placeholder="Nhập số hóa đơn...">
        </div>
      </div>

      <div class="row g-3 align-items-center mb-4">
        <div class="col-md-2">
          <label class="fw-bold text-dark small text-uppercase">Ngày xuất</label>
        </div>
        <div class="col-md-4">
          <div class="input-group">
            <input type="text" class="form-control bg-light border-0 py-2 shadow-none" value="19/01/2026">
            <span class="input-group-text bg-light border-0">
              <i class="bi bi-calendar-event"></i>
            </span>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-12">
          <button class="btn btn-success px-4 py-2 fw-bold shadow-sm rounded-3">
            <i class="bi bi-file-earmark-spreadsheet me-2"></i>Xuất File Excel
          </button>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
          <thead class="bg-light">
            <tr class="text-secondary small fw-bold">
              <th style="width: 50px;" class="ps-4 text-center">STT</th>
              <th style="width: 15%;">ITEM CODE</th>
              <th style="width: 15%;">DRAWING NO</th>
              <th style="width: 25%;">PART NAME</th>
              <th style="width: 15%;">SPEC</th>
              <th style="width: 12%;">MATERIAL</th>
              <th style="width: 13%;" class="text-end pe-4">TỔNG SỐ LƯỢNG</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in previewItems" :key="index">
              <td class="ps-4 text-center text-muted">{{ index + 1 }}</td>
              <td class="fw-bold text-primary text-truncate">{{ item.code }}</td>
              <td class="text-muted text-truncate">{{ item.drawing }}</td>
              <td class="fw-medium text-truncate">{{ item.name }}</td>
              <td class="text-muted text-truncate">{{ item.spec }}</td>
              <td>
                <span class="badge bg-light text-dark border-0 fw-normal px-2 py-1">
                  {{ item.material }}
                </span>
              </td>
              <td class="text-end pe-4 fw-bold fs-6">{{ item.quantity }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const previewItems = ref([
  { code: '653006007', drawing: 'KAAS0022A', name: 'AB(36L)', spec: 'Standard', material: 'Thép', quantity: '12' },
  { code: '653006009', drawing: '65XXX6093', name: 'Tấm chặn (22W)', spec: 'Standard', material: 'Nhôm', quantity: '12' },
  { code: '653006010', drawing: '65XXX6094', name: 'Bản lề (22W)', spec: '-', material: 'Nhựa', quantity: '12' }
]);
</script>