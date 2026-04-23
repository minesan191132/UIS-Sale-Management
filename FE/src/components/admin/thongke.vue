<template>
  <div class="dashboard-container p-4">

    <!-- Header + Period Filter -->
    <div class="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-2">
      <h2 class="fw-bold text-uppercase fs-4 m-0">Bảng Điều Khiển – Thống Kê</h2>
      <div class="btn-group" role="group">
        <button v-for="p in periods" :key="p.value"
          class="btn btn-sm"
          :class="activePeriod === p.value ? 'btn-primary' : 'btn-outline-secondary'"
          @click="changePeriod(p.value)">
          {{ p.label }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted small">Đang tải dữ liệu...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger alert-fit-content">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <template v-else>

      <!-- ===== ACTION ITEMS: Cần xử lý ngay ===== -->
      <p class="text-muted small fw-bold text-uppercase mb-2">Cần xử lý ngay</p>
      <div class="row g-3 mb-4">
        <div class="col-md-4">
          <div class="stat-card card border-0 shadow-sm p-3 h-100 action-card-warning">
            <p class="label">ĐƠN CHỜ BÁO GIÁ</p>
            <h3 class="value">{{ stats.ordersAwaitingQuote }}</h3>
            <p class="sub">Đơn chưa có báo giá từ admin</p>
            <i class="bi bi-hourglass-split bg-icon"></i>
          </div>
        </div>
        <div class="col-md-4">
          <div class="stat-card card border-0 shadow-sm p-3 h-100 action-card-info">
            <p class="label">ĐƠN CHỜ THANH TOÁN</p>
            <h3 class="value">{{ stats.ordersAwaitingPayment }}</h3>
            <p class="sub">Chưa đặt cọc đợt 1</p>
            <i class="bi bi-credit-card bg-icon"></i>
          </div>
        </div>
        <div class="col-md-4">
          <div class="stat-card card border-0 shadow-sm p-3 h-100 action-card-orange">
            <p class="label">CHỜ THANH TOÁN ĐỢT 2</p>
            <h3 class="value">{{ stats.ordersAwaitingRemainingPayment }}</h3>
            <p class="sub">Chưa thanh toán phần còn lại</p>
            <i class="bi bi-credit-card-2-front bg-icon"></i>
          </div>
        </div>
      </div>

      <!-- ===== HÀNG 1: DOANH THU + TỔNG ĐƠN ===== -->
      <p class="text-muted small fw-bold text-uppercase mb-2">💰 Doanh thu & Tổng đơn <span class="text-primary fw-normal">({{ periodLabel }})</span></p>
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">DOANH THU GIA CÔNG</p>
            <h3 class="value text-primary">{{ formatCurrency(stats.revenueCustomManufacturing) }}</h3>
            <p class="sub">Đơn đã giao & thanh toán đủ</p>
            <i class="bi bi-gear-wide-connected bg-icon text-primary"></i>
          </div>
        </div>
        <div class="col-md-3">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">DOANH THU PHÔI NGUYÊN VẬT LIỆU</p>
            <h3 class="value text-success">{{ formatCurrency(stats.revenueReadyMade) }}</h3>
            <p class="sub">Đơn đã giao & thanh toán đủ</p>
            <i class="bi bi-box-seam bg-icon text-success"></i>
          </div>
        </div>
        <div class="col-md-3">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">TỔNG ĐƠN GIA CÔNG</p>
            <h3 class="value text-primary">{{ stats.totalOrdersCustomManufacturing.toLocaleString('vi-VN') }}</h3>
            <p class="sub">Tổng đơn đặt hàng gia công</p>
            <i class="bi bi-file-earmark-text bg-icon text-primary"></i>
          </div>
        </div>
        <div class="col-md-3">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">TỔNG ĐƠN PHÔI NGUYÊN VẬT LIỆU</p>
            <h3 class="value text-success">{{ stats.totalOrdersReadyMade.toLocaleString('vi-VN') }}</h3>
            <p class="sub">Tổng đơn đặt mua phôi</p>
            <i class="bi bi-cart-check bg-icon text-success"></i>
          </div>
        </div>
      </div>

      <!-- ===== HÀNG 2: TRONG KỲ + KHÁCH HÀNG ===== -->
      <div class="row g-3 mb-4">
        <div class="col-md-4">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">ĐƠN GIA CÔNG TRONG KỲ</p>
            <h3 class="value text-warning">{{ stats.newOrdersCustomManufacturing.toLocaleString('vi-VN') }}</h3>
            <p class="sub">{{ periodLabel }}</p>
            <i class="bi bi-file-earmark-plus bg-icon text-warning"></i>
          </div>
        </div>
        <div class="col-md-4">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">ĐƠN PHÔI NVL TRONG KỲ</p>
            <h3 class="value text-info">{{ stats.newOrdersReadyMade.toLocaleString('vi-VN') }}</h3>
            <p class="sub">{{ periodLabel }}</p>
            <i class="bi bi-bag-plus bg-icon text-info"></i>
          </div>
        </div>
        <div class="col-md-4">
          <div class="stat-card card border-0 shadow-sm p-3 h-100">
            <p class="label">TỔNG KHÁCH HÀNG</p>
            <h3 class="value text-dark">{{ stats.totalCustomers.toLocaleString('vi-VN') }}</h3>
            <p class="sub">Tài khoản đã đăng ký</p>
            <i class="bi bi-people bg-icon text-secondary"></i>
          </div>
        </div>
      </div>

      <!-- ===== LINE CHART: DOANH THU THEO THÁNG ===== -->
      <div class="card border-0 shadow-sm p-4 rounded-4 mb-3">
        <div class="d-flex align-items-center justify-content-between mb-3">
          <div>
            <h6 class="fw-bold text-muted m-0">DOANH THU THEO THÁNG — NĂM {{ chartYear }}</h6>
            <small class="text-muted">T1 – T12 | Chỉ tính đơn hoàn thành</small>
          </div>
          <div class="d-flex align-items-center gap-3">
            <!-- Legend -->
            <span class="d-flex align-items-center gap-1 small">
              <span style="width:20px;height:3px;background:#0d6efd;display:inline-block;border-radius:2px"></span> Gia công
            </span>
            <span class="d-flex align-items-center gap-1 small">
              <span style="width:20px;height:3px;background:#198754;display:inline-block;border-radius:2px"></span> Phôi nguyên vật liệu
            </span>
            <!-- Year selector -->
            <select
              class="form-select form-select-sm ms-2"
              style="width:110px"
              :value="chartYear"
              @change="e => changeChartYear(Number(e.target.value))"
            >
              <option v-for="yr in selectableYears" :key="yr" :value="yr">
                {{ yr === currentYear ? yr + ' (Năm nay)' : yr }}
              </option>
            </select>
          </div>
        </div>
        <div style="position:relative;height:280px">
          <canvas id="revenueLineChart"></canvas>
        </div>
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue';
import Chart from 'chart.js/auto';
import { statsAPI } from '../../services/api';

const loading = ref(true);
const error = ref(null);
const activePeriod = ref('THIS_MONTH');
const now = new Date();
const currentYear = now.getFullYear();
const chartYear = ref(currentYear);

const periods = [
  { value: 'THIS_MONTH', label: 'Tháng này' },
  { value: 'LAST_MONTH', label: 'Tháng trước' },
  { value: 'THIS_YEAR',  label: 'Năm nay' },
  { value: 'ALL',        label: 'Tất cả' },
];

const periodLabel = computed(() => periods.find(p => p.value === activePeriod.value)?.label ?? '');

// Year range for the chart: 2020 → currentYear (newest first)
const selectableYears = computed(() => {
  const years = [];
  for (let y = currentYear; y >= 2020; y--) years.push(y);
  return years;
});

const stats = ref({
  ordersAwaitingQuote: 0,
  ordersAwaitingPayment: 0,
  revenueCustomManufacturing: 0,
  revenueReadyMade: 0,
  totalOrdersCustomManufacturing: 0,
  totalOrdersReadyMade: 0,
  newOrdersCustomManufacturing: 0,
  newOrdersReadyMade: 0,
  totalCustomers: 0,
});

const formatCurrency = (value) =>
  Number(value || 0).toLocaleString('vi-VN', {
    style: 'currency', currency: 'VND', maximumFractionDigits: 0,
  });

let lineChart = null;

const renderLineChart = (monthlyData) => {
  if (lineChart) { lineChart.destroy(); lineChart = null; }
  const el = document.getElementById('revenueLineChart');
  if (!el) return;

  const ctx = el.getContext('2d');

  // Gradient fills
  const gradGC = ctx.createLinearGradient(0, 0, 0, 280);
  gradGC.addColorStop(0, 'rgba(13,110,253,0.18)');
  gradGC.addColorStop(1, 'rgba(13,110,253,0)');

  const gradNVL = ctx.createLinearGradient(0, 0, 0, 280);
  gradNVL.addColorStop(0, 'rgba(25,135,84,0.18)');
  gradNVL.addColorStop(1, 'rgba(25,135,84,0)');

  const toM = (v) => Number(v) / 1_000_000;

  lineChart = new Chart(el, {
    type: 'line',
    data: {
      labels: monthlyData.labels,
      datasets: [
        {
          label: 'Gia công',
          data: monthlyData.customManufacturing.map(toM),
          borderColor: '#0d6efd',
          backgroundColor: gradGC,
          borderWidth: 2.5,
          pointRadius: 4,
          pointBackgroundColor: '#0d6efd',
          pointHoverRadius: 6,
          tension: 0.4,
          fill: true,
        },
        {
          label: 'Phôi NVL',
          data: monthlyData.readyMade.map(toM),
          borderColor: '#198754',
          backgroundColor: gradNVL,
          borderWidth: 2.5,
          pointRadius: 4,
          pointBackgroundColor: '#198754',
          pointHoverRadius: 6,
          tension: 0.4,
          fill: true,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => ` ${ctx.dataset.label}: ${ctx.parsed.y.toFixed(1)} triệu đ`,
          },
        },
      },
      scales: {
        x: { grid: { display: false } },
        y: {
          beginAtZero: true,
          ticks: { callback: (v) => v + ' tr' },
          grid: { color: 'rgba(0,0,0,0.05)' },
        },
      },
    },
  });
};

const fetchChartData = async (year) => {
  try {
    const monthlyData = await statsAPI.getMonthlyRevenue(year);
    await nextTick();
    renderLineChart(monthlyData);
  } catch (e) {
    console.error('Chart error:', e);
  }
};

const fetchStats = async (period) => {
  loading.value = true;
  error.value = null;
  try {
    const [dashData, monthlyData] = await Promise.all([
      statsAPI.getDashboard(period),
      statsAPI.getMonthlyRevenue(chartYear.value),
    ]);
    stats.value = dashData;
    loading.value = false;
    await nextTick();
    renderLineChart(monthlyData);
  } catch (e) {
    error.value = 'Không thể tải dữ liệu thống kê. Vui lòng thử lại.';
    loading.value = false;
    console.error('Stats error:', e);
  }
};

const changePeriod = (period) => {
  activePeriod.value = period;
  fetchStats(period);
};

const changeChartYear = (year) => {
  chartYear.value = year;
  fetchChartData(year);
};

onMounted(() => fetchStats(activePeriod.value));
</script>

<style scoped>
.stat-card {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.1) !important;
}
.stat-card .label {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: #6c757d;
  margin-bottom: 4px;
}
.stat-card .value {
  font-size: 1.4rem;
  font-weight: 800;
  margin: 0 0 4px;
}
.stat-card .sub {
  font-size: 0.7rem;
  color: #adb5bd;
  margin: 0;
}
.bg-icon {
  position: absolute;
  right: -8px;
  bottom: -8px;
  font-size: 4rem;
  opacity: 0.08;
}
/* Action cards */
.action-card-warning {
  border-left: 4px solid #ffc107 !important;
  background: linear-gradient(135deg, #fffdf0, #fff);
}
.action-card-warning .value { color: #e6a817; }
.action-card-warning .bg-icon { opacity: 0.12; color: #ffc107; }
.action-card-info {
  border-left: 4px solid #0dcaf0 !important;
  background: linear-gradient(135deg, #f0fbff, #fff);
}
.action-card-info .value { color: #0aa2c0; }
.action-card-info .bg-icon { opacity: 0.12; color: #0dcaf0; }
.action-card-orange {
  border-left: 4px solid #fd7e14 !important;
  background: linear-gradient(135deg, #fff8f0, #fff);
}
.action-card-orange .value { color: #d4620a; }
.action-card-orange .bg-icon { opacity: 0.12; color: #fd7e14; }

.chart-wrapper {
  position: relative;
  height: 280px;
}
</style>