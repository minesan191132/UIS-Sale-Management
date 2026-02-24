<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase fs-4 text-dark">Lịch sử nhập kho</h2>
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
          <thead class="table-dark shadow-sm">
            <tr class="small text-uppercase fw-bold">
              <th style="width: 180px;" class="ps-4">Mã nhập kho</th>
              <th>Tên sản phẩm</th>
              <th style="width: 150px;">Số lượng</th>
              <th style="width: 200px;" class="text-end pe-4">Ngày nhập</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="n in 3" :key="n">
              <td class="ps-4 text-muted small fw-bold">#NK-100{{ n }}</td>
              <td class="fw-bold text-dark">Thép AB(36L)</td>
              <td><span class="badge bg-success px-3 py-2 fw-normal text-white">+{{ n * 5 }}</span></td>
              <td class="text-end pe-4 text-muted small">27/01/2026</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>