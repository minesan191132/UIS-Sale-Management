<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Chỉnh sửa User</h2>
    <div class="card border-0 shadow-sm p-4 bg-white rounded-4">
      <div class="row g-4">
        <div class="col-md-6">
          <label class="fw-bold small mb-2 text-muted">EMAIL</label>
          <input type="email" class="form-control form-control-lg bg-light border-0 shadow-none fs-6" value="admin@gmail.com">
        </div>
        <div class="col-md-6">
          <label class="fw-bold small mb-2 text-muted">PASSWORD</label>
          <input type="password" class="form-control form-control-lg bg-light border-0 shadow-none fs-6" placeholder="Để trống nếu không đổi">
        </div>
        <div class="col-md-6">
          <label class="fw-bold small mb-2 text-muted">HỌ VÀ TÊN</label>
          <input type="text" class="form-control form-control-lg bg-light border-0 shadow-none fs-6" value="Nguyễn Văn A">
        </div>
        <div class="col-md-6">
          <label class="fw-bold small mb-2 text-muted">SỐ ĐIỆN THOẠI</label>
          <input type="text" class="form-control form-control-lg bg-light border-0 shadow-none fs-6" value="0901111111">
        </div>
        <div class="col-12 mt-4 pt-2">
          <label class="fw-bold small mb-3 d-block text-muted">VAI TRÒ</label>
          <div class="d-flex gap-4">
            <div class="form-check custom-check">
              <input class="form-check-input" type="radio" name="role" id="roleStaff" checked>
              <label class="form-check-label fw-bold" for="roleStaff">Nhân viên</label>
            </div>
            <div class="form-check custom-check">
              <input class="form-check-input" type="radio" name="role" id="roleCustomer">
              <label class="form-check-label fw-bold" for="roleCustomer">Khách hàng</label>
            </div>
          </div>
        </div>
        <div class="col-12 text-end mt-5 pt-3 border-top d-flex gap-2 justify-content-end">
          <button class="btn btn-outline-primary px-4 py-2 fw-bold">Create</button>
          <button class="btn btn-outline-warning px-4 py-2 fw-bold text-dark">Update</button>
          <button class="btn btn-outline-danger px-4 py-2 fw-bold">Delete</button>
          <button class="btn btn-outline-secondary px-4 py-2 fw-bold">Reset</button>
        </div>
      </div>
    </div>
  </div>
</template>