<template>
  <div class="users-container p-4 min-vh-100 d-flex flex-column w-100" style="background-color: #f8f9fa; max-width: 100%;">
    
    <div class="d-flex justify-content-between align-items-end mb-4 pb-2">
      <div>
        <h2 class="fw-bolder mb-1 text-dark fs-3 text-uppercase">Danh sách User</h2>
        <p class="text-muted mb-0">Quản lý phân quyền, thông tin liên hệ và trạng thái tài khoản khách hàng.</p>
      </div>
    </div>

    <div class="main-grid-section flex-grow-1 d-flex flex-column">
      
      <div class="filter-bar bg-white p-3 rounded-pill shadow-sm mb-4 d-flex gap-3 align-items-center">
        <div style="width: 180px;">
          <select v-model="filters.role" @change="fetchUsers" class="form-select modern-admin-select shadow-none fw-medium text-secondary py-2 px-4">
            <option value="all">Vai trò: Tất cả</option>
            <option value="ADMIN">Admin</option>
            <option value="CUSTOMER">Customer</option>
          </select>
        </div>
        
        <div class="search-box flex-grow-1 position-relative">
          <i class="bi bi-search position-absolute text-muted" style="top: 50%; left: 20px; transform: translateY(-50%);"></i>
          <input v-model="filters.search" @keyup.enter="fetchUsers" type="text" 
                 class="form-control border-0 bg-light rounded-pill shadow-none ps-5 py-2 fw-medium"
                 placeholder="Tìm kiếm theo email hoặc tên người dùng..." />
        </div>
        
        <div class="search-box flex-grow-1 position-relative">
          <i class="bi bi-buildings position-absolute text-muted" style="top: 50%; left: 20px; transform: translateY(-50%);"></i>
          <input v-model="filters.companySearch" @keyup.enter="fetchUsers" type="text" 
                 class="form-control border-0 bg-light rounded-pill shadow-none ps-5 py-2 fw-medium"
                 placeholder="Tìm kiếm theo tên công ty..." />
        </div>
        
        <button class="btn btn-navy rounded-pill px-4 py-2 fw-bold shadow-sm" @click="fetchUsers">
          <i class="bi bi-funnel-fill me-1"></i> Lọc
        </button>
      </div>
    </div>

      <div v-if="loading" class="text-center py-5 flex-grow-1 d-flex flex-column justify-content-center">
        <div class="spinner-grow text-primary mx-auto" role="status" style="width: 3rem; height: 3rem;"></div>
        <p class="mt-3 text-muted fw-bold">Đang tải dữ liệu người dùng...</p>
      </div>

      <div v-else-if="error" class="alert alert-danger alert-fit-content rounded-4 shadow-sm border-0 py-3 fw-medium">
        <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
      </div>

      <div v-else class="table-responsive px-1 pb-4 flex-grow-1">
        <table class="table modern-table mb-0 w-100 align-middle">
          <thead>
            <tr>
              <th class="ps-4" style="width: 60px;">#</th>
              <th>Người dùng</th>
              <th>Liên hệ</th>
              <th>Doanh nghiệp</th>
              <th class="text-center">Vai trò</th>
              <th class="text-center">Trạng thái</th>
              <th class="pe-4 text-end">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="users.length === 0">
              <td colspan="7" class="text-center py-5 bg-white rounded-4 shadow-sm">
                <div class="d-inline-flex align-items-center justify-content-center bg-light rounded-circle mb-3" style="width: 80px; height: 80px;">
                  <i class="bi bi-people fs-1 text-muted opacity-50"></i>
                </div>
                <h5 class="text-muted fw-bold">Không có dữ liệu!</h5>
                <p class="text-muted small mb-0">Không tìm thấy người dùng nào khớp với bộ lọc.</p>
              </td>
            </tr>
            
            <tr v-for="(user, index) in users" :key="user.id" class="shadow-sm bg-white hover-lift">
              <td class="ps-4 py-3 text-muted fw-bold small">
                {{ page * pageSize + index + 1 }}
              </td>
              
              <td class="py-3">
                <div class="d-flex align-items-center gap-3">
                  <div class="avatar-circle text-white fw-bold fs-5 d-flex align-items-center justify-content-center flex-shrink-0 shadow-sm" 
                       :style="{ backgroundColor: getAvatarColor(user.email) }" style="width: 48px; height: 48px; border-radius: 14px;">
                    {{ getInitials(user.fullName, user.email) }}
                  </div>
                  <div>
                    <h6 class="mb-1 fw-bolder text-dark">{{ user.fullName || 'Chưa cập nhật tên' }}</h6>
                    <span class="text-muted small fw-medium"><i class="bi bi-envelope me-1"></i>{{ user.email }}</span>
                  </div>
                </div>
              </td>
              
              <td class="py-3">
                <div class="fw-semibold text-secondary">
                  <i class="bi bi-telephone-fill text-muted me-1 opacity-50"></i> 
                  {{ user.phone || '—' }}
                </div>
              </td>
              
              <td class="py-3">
                <div class="company-name fw-bold text-dark mb-1" :title="user.companyName">{{ user.companyName || '—' }}</div>
                <span v-if="user.companyTaxCode" class="badge bg-light text-secondary border px-2 py-1 fw-medium me-2" style="font-size: 0.7rem;">
                  MST: {{ user.companyTaxCode }}
                </span>
                <div class="small text-muted mt-2">
                  <i class="bi bi-envelope me-1"></i> {{ user.companyEmail || '—' }}
                </div>
                <div class="small text-muted">
                  <i class="bi bi-telephone me-1"></i> {{ user.companyPhone || '—' }}
                </div>
              </td>
              
              <td class="py-3 text-center">
                <span class="badge rounded-pill px-3 py-2 fw-bold" :class="roleBadgeClass(user.role)" style="font-size: 0.75rem;">
                  <i class="me-1" :class="user.role === 'ADMIN' ? 'bi-shield-lock-fill' : 'bi-person-badge'"></i> 
                  {{ roleLabel(user.role) }}
                </span>
              </td>
              
              <td class="py-3 text-center">
                <div class="d-flex align-items-center justify-content-center gap-2">
                  <span class="glowing-dot" :class="user.isActive ? 'bg-success shadow-success' : 'bg-danger shadow-danger'"></span>
                  <span class="fw-bold" :class="user.isActive ? 'text-success' : 'text-danger'" style="font-size: 0.85rem;">
                    {{ user.isActive ? 'Hoạt động' : 'Đã khoá' }}
                  </span>
                </div>
              </td>
              
              <td class="pe-4 py-3 text-end">
                <div class="d-flex justify-content-end gap-2">
                  <button v-if="!(user.role === 'ADMIN' && user.isActive)"
                          class="btn btn-action-circle" 
                          :class="user.isActive ? 'bg-light text-warning border' : 'bg-light text-success border'"
                          :title="user.isActive ? 'Khoá tài khoản' : 'Mở khoá tài khoản'"
                          @click="handleToggleActive(user)">
                    <i class="bi" :class="user.isActive ? 'bi-lock-fill' : 'bi-unlock-fill'"></i>
                  </button>
                  <button v-else
                          class="btn btn-action-circle bg-light text-muted border opacity-50" 
                          style="cursor:not-allowed;" title="Không thể khoá Admin">
                    <i class="bi bi-shield-fill-check"></i>
                  </button>
                  
                  <button class="btn btn-action-circle bg-light text-primary border" @click="openEdit(user)" title="Xem & Chỉnh sửa">
                    <i class="bi bi-pencil-fill"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="totalPages > 1" class="d-flex justify-content-between align-items-center mt-2 pb-4 px-2">
        <span class="text-muted fw-medium">Tổng số: <b class="text-dark">{{ totalElements }}</b> tài khoản</span>
        <div class="d-flex align-items-center gap-2">
          <button class="btn btn-white shadow-sm border rounded-circle w-40 h-40 d-flex align-items-center justify-content-center hover-lift" :disabled="page === 0" @click="goPage(page - 1)">
            <i class="bi bi-chevron-left"></i>
          </button>
          <span class="px-3 fw-bold text-navy">Trang {{ page + 1 }} / {{ totalPages }}</span>
          <button class="btn btn-white shadow-sm border rounded-circle w-40 h-40 d-flex align-items-center justify-content-center hover-lift" :disabled="page >= totalPages - 1" @click="goPage(page + 1)">
            <i class="bi bi-chevron-right"></i>
          </button>
        </div>
      </div>

    </div>

    <div v-if="editUser" class="modal-overlay" @click.self="editUser = null">
      <div class="modal-fullpage shadow-lg bg-white rounded-4 border-0">
        <div class="bg-navy text-white px-5 py-4 d-flex align-items-center justify-content-between">
          <div>
            <h4 class="fw-bolder mb-0"><i class="bi bi-person-lines-fill me-2"></i> Hồ sơ người dùng</h4>
            <p class="text-white-50 small mb-0 mt-1">Xem chi tiết và cập nhật thông tin liên hệ</p>
          </div>
          <button class="btn-close btn-close-white shadow-none" @click="editUser = null"></button>
        </div>

        <div class="modal-body-scroll px-5 py-4 bg-slate-50">
          <div class="row g-5">

            <div class="col-md-6">
              <div class="card border-0 shadow-sm rounded-4 p-4 h-100">
                <h6 class="fw-bolder text-navy text-uppercase small mb-4 d-flex align-items-center">
                  <i class="bi bi-person-badge fs-5 me-2"></i> Thông tin cơ bản
                </h6>

                <div class="mb-4">
                  <label class="form-label fw-bold text-secondary small">Địa chỉ Email <i class="bi bi-lock-fill text-muted ms-1" title="Không thể sửa"></i></label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted px-3"><i class="bi bi-envelope-at"></i></span>
                    <input class="form-control custom-input bg-light border-start-0 ps-0 text-muted fw-medium" :value="editUser.email" readonly disabled />
                  </div>
                </div>

                <div class="mb-4">
                  <label class="form-label fw-bold text-secondary small">Họ và tên <i class="bi bi-lock-fill text-muted ms-1"></i></label>
                  <input :value="editUser.fullName || '—'" class="form-control custom-input bg-light text-muted fw-medium" readonly disabled />
                </div>

                <div class="mb-4">
                  <label class="form-label fw-bold text-secondary small">Số điện thoại liên hệ <i class="bi bi-lock-fill text-muted ms-1" title="Không thể sửa"></i></label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted px-3"><i class="bi bi-telephone"></i></span>
                    <input :value="editUser.phone || '—'" type="tel" class="form-control custom-input bg-light border-start-0 ps-0 text-muted fw-medium" readonly disabled />
                  </div>
                </div>
              </div>
            </div>

            <div class="col-md-6">
              <div class="card border-0 shadow-sm rounded-4 p-4 h-100">
                <h6 class="fw-bolder text-navy text-uppercase small mb-4 d-flex align-items-center">
                  <i class="bi bi-shield-check fs-5 me-2"></i> Bảo mật & Doanh nghiệp
                </h6>

                <div class="mb-4">
                  <label class="form-label fw-bold text-secondary small">Vai trò hệ thống <i class="bi bi-lock-fill text-muted ms-1"></i></label>
                  <input :value="roleLabel(editUser.role)" class="form-control custom-input bg-light text-muted fw-bold" readonly disabled />
                </div>

                <div class="mb-4">
                  <label class="form-label fw-bold text-secondary small">Thông tin Công ty <i class="bi bi-lock-fill text-muted ms-1"></i></label>
                  <div class="p-3 border rounded-3 bg-light">
                    <div class="fw-bold text-dark mb-1">{{ editUser.companyName || 'Khách lẻ / Chưa cập nhật' }}</div>
                    <div v-if="editUser.companyTaxCode" class="badge bg-white text-secondary border px-2 py-1">MST: {{ editUser.companyTaxCode }}</div>
                  </div>
                </div>

                <div class="mb-2">
                  <label class="form-label fw-bold text-secondary small">Trạng thái truy cập</label>
                  <div class="p-3 rounded-3 d-flex align-items-center justify-content-between" :class="editUser.isActive ? 'bg-success bg-opacity-10 border border-success border-opacity-25' : 'bg-danger bg-opacity-10 border border-danger border-opacity-25'">
                    <div class="d-flex align-items-center gap-2">
                      <span class="glowing-dot" :class="editUser.isActive ? 'bg-success shadow-success' : 'bg-danger shadow-danger'"></span>
                      <span class="fw-bold" :class="editUser.isActive ? 'text-success' : 'text-danger'">
                        {{ editUser.isActive ? 'Tài khoản đang Hoạt động' : 'Tài khoản đang Bị khoá' }}
                      </span>
                    </div>
                  </div>
                  <div class="form-text mt-2 small"><i class="bi bi-info-circle me-1"></i>Dùng nút khoá ngoài danh sách để thay đổi.</div>
                </div>

              </div>
            </div>
          </div>

          <div v-if="saveError" class="alert alert-danger alert-fit-content mt-4 rounded-3 border-0 fw-medium shadow-sm"><i class="bi bi-exclamation-triangle-fill me-2"></i>{{ saveError }}</div>
        </div>

        <div class="px-5 py-4 border-top bg-white d-flex justify-content-end gap-3 rounded-bottom-4">
          <button class="btn btn-light border rounded-pill px-5 py-2 fw-bold" @click="editUser = null">Đóng</button>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { usersAPI } from '../../services/api.js';
import Swal from 'sweetalert2';

const users = ref([]);
const loading = ref(true);
const error = ref('');
const page = ref(0);
const pageSize = 20;
const totalPages = ref(0);
const totalElements = ref(0);

const filters = reactive({ search: '', role: 'all', companySearch: '' });

const editUser = ref(null);
const editForm = reactive({ phone: '' });
const saving = ref(false);
const saveError = ref('');

// ─── API LOGIC (GIỮ NGUYÊN 100%) ─────────────────────────────
const fetchUsers = async () => {
  loading.value = true;
  error.value = '';
  try {
    const data = await usersAPI.getAll(filters.search, filters.role, page.value, pageSize, filters.companySearch);
    users.value = data.content || [];
    totalPages.value = data.totalPages || 0;
    totalElements.value = data.totalElements || 0;
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tải danh sách người dùng.';
  } finally {
    loading.value = false;
  }
};

const goPage = (p) => {
  if (p < 0 || p >= totalPages.value) return;
  page.value = p;
  fetchUsers();
};

const openEdit = (user) => {
  editUser.value = user;
  editForm.phone = user.phone || '';
  saveError.value = '';
};

const saveUser = async () => {
  saving.value = true;
  saveError.value = '';
  try {
    const payload = { phone: editForm.phone };
    const updated = await usersAPI.updateUser(editUser.value.id, payload);
    const idx = users.value.findIndex(u => u.id === editUser.value.id);
    if (idx !== -1) {
      users.value[idx] = { ...users.value[idx], ...updated };
    }
    editUser.value = null;
  } catch (err) {
    saveError.value = err.response?.data?.message || 'Không thể cập nhật người dùng.';
  } finally {
    saving.value = false;
  }
};

const handleToggleActive = async (user) => {
  const actionText = user.isActive ? 'khoá' : 'mở khoá';
  const confirmColor = user.isActive ? '#dc3545' : '#10b981';

  const result = await Swal.fire({
    title: `Xác nhận ${actionText}?`,
    html: `Bạn có chắc chắn muốn ${actionText} tài khoản <br><b class="text-navy">${user.email}</b>?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: confirmColor,
    cancelButtonColor: '#64748b',
    confirmButtonText: `Đồng ý ${actionText}`,
    cancelButtonText: 'Huỷ bỏ',
    customClass: {
      popup: 'rounded-4 shadow-lg border-0'
    }
  });

  if (!result.isConfirmed) return;

  try {
    const updated = await usersAPI.toggleActive(user.id);
    user.isActive = updated.isActive;
    
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: 'success',
      title: `Đã ${actionText} thành công!`,
      showConfirmButton: false,
      timer: 3000
    });
  } catch {
    Swal.fire('Lỗi hệ thống', 'Không thể thay đổi trạng thái tài khoản lúc này.', 'error');
  }
};

const handleDeleteUser = async (user) => {
  const result = await Swal.fire({
    title: 'Xoá tài khoản người dùng?',
    html: `Bạn có chắc chắn muốn xoá tài khoản <br><b class="text-danger">${user.email}</b>?<br><small class="text-muted">Hành động này không thể hoàn tác.</small>`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xoá tài khoản',
    cancelButtonText: 'Huỷ bỏ',
    customClass: {
      popup: 'rounded-4 shadow-lg border-0'
    }
  });

  if (!result.isConfirmed) return;

  try {
    await usersAPI.deleteUser(user.id);
    users.value = users.value.filter(u => u.id !== user.id);
    totalElements.value--;
    
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: 'success',
      title: 'Đã xoá tài khoản thành công!',
      showConfirmButton: false,
      timer: 3000
    });
  } catch (err) {
    Swal.fire('Lỗi hệ thống', err.response?.data?.error || 'Không thể xoá tài khoản lúc này.', 'error');
  }
};

const roleBadgeClass = (role) => {
  if (role === 'ADMIN') return 'bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25';
  return 'bg-info bg-opacity-10 text-info border border-info border-opacity-25';
};

const roleLabel = (role) => {
  if (role === 'ADMIN') return 'Quản trị viên';
  return 'Khách hàng';
};

// Auto generate avatar initials (Lấy chữ cái đầu)
const getInitials = (name, email) => {
  if (name) {
    const names = name.trim().split(' ');
    if (names.length >= 2) return (names[0][0] + names[names.length - 1][0]).toUpperCase();
    return name.substring(0, 2).toUpperCase();
  }
  return email.substring(0, 2).toUpperCase();
};

// Auto generate colors for avatars based on email length
const getAvatarColor = (email) => {
  const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6'];
  const index = email.length % colors.length;
  return colors[index];
};

onMounted(fetchUsers);
</script>

<style scoped>
/* 1. MÀU SẮC CƠ BẢN */
.text-navy { color: #1e3a8a !important; }
.bg-navy { background-color: #3b82f6 !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.btn-navy { background-color: #3b82f6; color: #fff; border: none; transition: 0.3s; }
.btn-navy:hover { background-color: #2563eb; color: #fff; box-shadow: 0 4px 10px rgba(59, 130, 246, 0.25); }

.btn-gradient-primary { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); color: white; border: none; transition: 0.3s; }
.btn-gradient-primary:hover { background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); box-shadow: 0 8px 20px rgba(37,99,235,0.3) !important; color: white; }

/* Hiệu ứng Hover chung */
.hover-lift { transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.3s; }
.hover-lift:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(0,0,0,0.08) !important; }

/* 2. BẢNG DỮ LIỆU SEPARATED ROW */
.modern-table { border-collapse: separate; border-spacing: 0 12px; }
.modern-table thead th { border: none; color: #94a3b8; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px; padding-bottom: 0; }
.modern-table tbody tr { border-radius: 16px; transition: transform 0.2s, box-shadow 0.2s; }
.modern-table tbody td { border: none; background: #fff; }
.modern-table tbody td:first-child { border-top-left-radius: 16px; border-bottom-left-radius: 16px; }
.modern-table tbody td:last-child { border-top-right-radius: 16px; border-bottom-right-radius: 16px; }

/* Cắt chữ tên công ty dài */
.company-name { display: -webkit-box; -webkit-line-clamp: 1; line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; max-width: 250px; }

/* Glowing Dots (Chấm sáng lân quang) */
.glowing-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
.shadow-success { box-shadow: 0 0 10px #10b981; }
.shadow-danger { box-shadow: 0 0 10px #ef4444; }

/* Nút Action Hình Tròn */
.btn-action-circle {
  width: 40px; height: 40px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center;
  transition: all 0.2s; font-size: 1.1rem; text-decoration: none;
}
.btn-action-circle.text-primary:hover { background-color: #dbeafe !important; color: #1d4ed8 !important; transform: scale(1.1); border-color: #bfdbfe !important; }
.btn-action-circle.text-danger:hover { background-color: #fee2e2 !important; color: #dc2626 !important; transform: scale(1.1); border-color: #fecaca !important; }
.btn-action-circle.text-warning:hover { background-color: #fef3c7 !important; color: #d97706 !important; transform: scale(1.1); border-color: #fde68a !important; }
.btn-action-circle.text-success:hover { background-color: #d1fae5 !important; color: #059669 !important; transform: scale(1.1); border-color: #a7f3d0 !important; }
.w-40 { width: 40px; } .h-40 { height: 40px; }

/* Form Inputs Modal */
.custom-input { border: 1px solid #cbd5e1; border-radius: 10px; padding: 0.6rem 1rem; transition: 0.2s; }
.custom-input:focus, .search-box input:focus { border-color: #3b82f6 !important; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15) !important; background-color: #fff !important; }

/* 3. MODAL FULLPAGE */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.7); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; z-index: 1050; padding: 20px;
}
.modal-fullpage {
  width: 100%; max-width: 900px; max-height: 90vh; display: flex; flex-direction: column;
  animation: modal-in 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes modal-in {
  from { opacity: 0; transform: scale(0.96) translateY(20px); }
  to   { opacity: 1; transform: scale(1) translateY(0); }
}
.modal-body-scroll { flex: 1; overflow-y: auto; }
</style>