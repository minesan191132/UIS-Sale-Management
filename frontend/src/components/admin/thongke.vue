<template>
  <div class="dashboard-container p-4">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Bảng điều khiển - Thống kê</h2>

    <div class="row g-3 mb-4">
      <div class="col-md-4" v-for="(item, index) in topStats" :key="index">
        <div class="card stat-card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
          <div class="position-relative" style="z-index: 2;">
            <p class="text-muted small fw-bold text-uppercase mb-1">{{ item.label }}</p>
            <h3 class="fw-bold m-0">{{ item.value }}</h3>
            <div class="mt-2 d-flex align-items-center">
              <span :class="item.trendClass" class="fw-bold me-2 small">
                <i :class="item.trendIcon"></i> {{ item.trend }}
              </span>
              <span class="text-muted" style="font-size: 0.7rem;">so với kỳ trước</span>
            </div>
          </div>
          <i :class="item.icon" class="position-absolute end-0 bottom-0 mb-n2 me-n2 display-4 opacity-10 text-dark"></i>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-md-4" v-for="(item, index) in bottomStats" :key="index">
        <div class="card stat-card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden bg-white">
          <div class="position-relative" style="z-index: 2;">
            <p class="text-muted small fw-bold text-uppercase mb-1">{{ item.label }}</p>
            <h3 class="fw-bold m-0">{{ item.value }}</h3>
            <div class="mt-2">
              <span class="badge bg-light text-primary border fw-normal">Hoạt động ổn định</span>
            </div>
          </div>
          <i :class="item.icon" class="position-absolute end-0 bottom-0 mb-n2 me-n2 display-4 opacity-10 text-primary"></i>
        </div>
      </div>
    </div>

    <div class="row g-3">
      <div class="col-lg-8">
        <div class="card border-0 shadow-sm p-4 h-100 rounded-4">
          <h6 class="fw-bold mb-3 text-muted border-bottom pb-2">XU HƯỚNG DOANH THU & SẢN PHẨM</h6>
          <div class="chart-wrapper">
             <canvas id="revenueLineChart"></canvas>
          </div>
        </div>
      </div>
      <div class="col-lg-4">
        <div class="card border-0 shadow-sm p-4 h-100 rounded-4">
          <h6 class="fw-bold mb-3 text-muted border-bottom pb-2">CƠ CẤU KHÁCH HÀNG</h6>
          <div class="chart-wrapper">
             <canvas id="customerPieChart"></canvas>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import Chart from 'chart.js/auto';

const topStats = [
  { label: 'Tổng doanh thu', value: '1.250.000.000đ', trend: '12.5%', trendClass: 'text-success', trendIcon: 'bi-graph-up-arrow', icon: 'bi-currency-dollar' },
  { label: 'Tổng sản phẩm', value: '15,420', trend: '5.2%', trendClass: 'text-success', trendIcon: 'bi-arrow-up-short', icon: 'bi-box-seam' },
  { label: 'Tổng khách hàng', value: '1,280', trend: '2.4%', trendClass: 'text-danger', trendIcon: 'bi-arrow-down-short', icon: 'bi-people' }
];

const bottomStats = [
  { label: 'Tổng nhân viên', value: '45', icon: 'bi-person-badge' },
  { label: 'Tổng tài khoản', value: '150', icon: 'bi-shield-lock' },
  { label: 'Tổng danh mục', value: '24', icon: 'bi-tags' }
];

onMounted(() => {
  new Chart(document.getElementById('revenueLineChart'), {
    type: 'line',
    data: {
      labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6'],
      datasets: [{
        label: 'Doanh thu',
        data: [12, 19, 15, 25, 22, 30],
        borderColor: '#0d6efd',
        backgroundColor: 'rgba(13, 110, 253, 0.1)',
        fill: true,
        tension: 0.4
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });

  new Chart(document.getElementById('customerPieChart'), {
    type: 'doughnut',
    data: {
      labels: ['Mới', 'Thân thiết', 'Tiềm năng'],
      datasets: [{
        data: [300, 50, 100],
        backgroundColor: ['#0d6efd', '#6610f2', '#fd7e14']
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });
});
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  height: 300px;
}
</style>