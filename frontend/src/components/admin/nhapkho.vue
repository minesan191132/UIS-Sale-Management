<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Nhập kho</h2>
    <div class="card border-0 shadow-sm p-4 mb-4 bg-white rounded-4">
      <div class="row g-3 align-items-end justify-content-between">
        <div class="col-md-5">
          <label class="small fw-bold text-muted mb-2">CHỌN SẢN PHẨM</label>
          <select class="form-select border-0 bg-light py-2 shadow-none">
            <option>Thép AB(36L)</option>
            <option>Nhôm BC(40R)</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="small fw-bold text-muted mb-2">SỐ LƯỢNG NHẬP</label>
          <input type="number" class="form-control border-0 bg-light py-2 shadow-none" placeholder="Nhập số lượng...">
        </div>
        <div class="col-md-2">
          <button class="btn btn-success w-100 py-2 fw-bold shadow-sm">Nhập kho</button>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
          <thead class="table-dark shadow-sm">
            <tr class="small text-uppercase fw-bold">
              <th style="width: 80px;" class="ps-4">HÌNH</th>
              <th style="width: 35%;">Tên sản phẩm</th>
              <th style="width: 15%;">SL tồn</th>
              <th style="width: 15%;">Nhập kho</th>
              <th style="width: 150px;" class="text-end pe-4">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="ps-4">
                <div class="bg-light rounded text-center py-2 border"><i class="bi bi-image text-muted opacity-50"></i></div>
              </td>
              <td class="fw-bold">Thép AB(36L)</td>
              <td class="text-primary fw-bold fs-5">20</td>
              <td><input type="text" class="form-control form-control-sm bg-light border-0" placeholder="SL"></td>
              <td class="text-end pe-4">
                <button class="btn btn-success btn-sm px-4 fw-bold shadow-sm">Nhập</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>