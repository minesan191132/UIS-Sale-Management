<template>
  <div class="inventory-container p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold text-dark m-0 fs-4">TỔNG KHO VẬT TƯ (INVENTORY)</h2>
      <div class="d-flex gap-2">
        <div class="input-group" style="width: 250px;">
          <span class="input-group-text bg-white border-end-0 py-1"><i class="bi bi-search"></i></span>
          <input type="text" class="form-control border-start-0 py-1 shadow-none" placeholder="Tìm kiếm...">
        </div>
        <button class="btn btn-success px-3 py-1 shadow-sm small">
          <i class="bi bi-plus-lg me-1"></i>Nhập dữ liệu
        </button>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-lg-7">
        <div class="row g-3">
          <div class="col-md-6" v-for="(stat, index) in stats" :key="index">
            <div class="card stat-card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
              <div class="position-relative" style="z-index: 2;">
                <p class="text-muted small fw-bold text-uppercase mb-1">{{ stat.label }}</p>
                <h3 class="fw-bold m-0">{{ stat.value }}</h3>
                <div class="mt-2 d-flex align-items-center">
                  <span :class="stat.trendClass" class="fw-bold me-2 small">
                    <i :class="stat.trendIcon"></i> {{ stat.trend }}
                  </span>
                  <span class="text-muted" style="font-size: 0.7rem;">so với tháng trước</span>
                </div>
              </div>
              <i :class="stat.mainIcon" class="position-absolute end-0 bottom-0 mb-n2 me-n2 display-4 opacity-10 text-dark"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-5">
        <div class="card border-0 shadow-sm p-3 h-100">
          <h6 class="fw-bold small mb-3 text-muted">PHÂN BỔ VẬT TƯ (PIE CHART)</h6>
          <div class="chart-wrapper">
            <canvas id="pieChart"></canvas>
          </div>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mt-3">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
      <thead class="bg-light">
        <tr class="text-dark fw-bold small">
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
        <tr v-for="(item, index) in inventoryData" :key="index">
          <td class="ps-4 text-center text-muted">{{ index + 1 }}</td>
          <td class="fw-bold text-primary text-truncate">{{ item.code }}</td>
          <td class="text-muted text-truncate">{{ item.drawing }}</td>
          <td class="fw-medium text-truncate">{{ item.name }}</td>
          
          <td class="text-muted text-truncate">{{ item.spec || '-' }}</td>
          
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
import { onMounted } from 'vue';
import Chart from 'chart.js/auto';

const inventoryData = [
  { code: '653006007', drawing: 'KAAS0022A', name: 'AB(36L)', spec: 'Standard', material: 'Thép', quantity: '12' },
  { code: '653006008', drawing: 'KAAS0023B', name: 'BC(40R)', spec: 'Special', material: 'Nhôm', quantity: '85' },
  { code: '653006009', drawing: 'KAAS0024C', name: 'XY(12)', spec: 'Speacial', material: 'Nhựa', quantity: '40' },
  { code: '653006010', drawing: 'KAAS0025D', name: 'ZZ(01)', spec: 'Standard', material: 'Đồng', quantity: '150' }
];

const stats = [
  { label: 'Tổng số mã vật tư', value: '3,000', trend: '2.16%', trendClass: 'text-success', trendIcon: 'bi-arrow-up-short', mainIcon: 'bi-box-seam' },
  { label: 'Số lượng tồn kho', value: '20', trend: '30.02%', trendClass: 'text-danger', trendIcon: 'bi-arrow-down-short', mainIcon: 'bi-warehouse' },
  { label: 'Vật tư sắp hết', value: '4', trend: '3%', trendClass: 'text-danger', trendIcon: 'bi-arrow-down-short', mainIcon: 'bi-exclamation-octagon' }
];

onMounted(() => {
  const ctx = document.getElementById('pieChart');
  new Chart(ctx, {
    type: 'pie',
    data: {
      labels: ['Thép', 'Nhôm', 'Nhựa', 'Khác'],
      datasets: [{
        data: [45, 25, 20, 10],
        backgroundColor: ['#0d6efd', '#6610f2', '#fd7e14', '#adb5bd'],
        borderWidth: 2,
        borderColor: '#ffffff'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'right', labels: { boxWidth: 12, font: { size: 10 } } }
      }
    }
  });
});
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  height: 180px;
}
</style>