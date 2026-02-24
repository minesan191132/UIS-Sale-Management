<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Danh sách User</h2>
    <div class="card border-0 shadow-sm p-4 bg-white rounded-4">
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <select class="form-select border-0 bg-light py-2 shadow-none">
            <option value="all">All Roles</option>
            <option value="admin">Admin</option>
            <option value="user">User</option>
          </select>
        </div>
        <div class="col-md-7">
          <input type="text" class="form-control border-0 bg-light py-2 shadow-none" placeholder="Tìm theo email hoặc tên...">
        </div>
        <div class="col-md-2 text-end">
          <button class="btn btn-primary w-100 py-2 fw-bold shadow-sm">Tìm kiếm</button>
        </div>
      </div>
      <div class="table-responsive rounded-3 overflow-hidden">
        <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
          <thead class="table-dark shadow-sm">
            <tr class="small text-uppercase fw-bold">
              <th style="width: 60px;" class="ps-4">STT</th>
              <th style="width: 25%;">Email</th>
              <th style="width: 20%;">Tên</th>
              <th style="width: 15%;">Số điện thoại</th>
              <th style="width: 15%;">Vai trò</th>
              <th style="width: 120px;" class="text-end pe-4">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(user, index) in users" :key="index">
              <td class="ps-4 text-muted small">{{ index + 1 }}</td>
              <td class="fw-bold text-dark">{{ user.email }}</td>
              <td>{{ user.name }}</td>
              <td class="text-muted">{{ user.phone }}</td>
              <td>
                <span class="badge rounded-pill fw-normal px-3 py-2" :class="user.role === 'Admin' ? 'bg-primary' : 'bg-success-subtle text-success border'">
                  {{ user.role }}
                </span>
              </td>
              <td class="text-end pe-4">
                <button class="btn btn-light btn-sm border px-3 rounded-pill fw-bold">Edit</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
const users = [
  { email: 'admin@gmail.com', name: 'Nguyễn Admin', phone: '0901111111', role: 'Admin' },
  { email: 'user01@gmail.com', name: 'Trần Văn User', phone: '0902222222', role: 'User' },
  { email: 'staff01@gmail.com', name: 'Lê Thị Nhân Viên', phone: '0903333333', role: 'User' }
];
</script>