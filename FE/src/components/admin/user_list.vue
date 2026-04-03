<template>
  <div class="p-4 bg-light min-vh-100">
    <h2 class="fw-bold mb-4 text-uppercase fs-4">Danh sách User</h2>

    <div class="card border-0 shadow-sm p-4 bg-white rounded-4">
      <!-- Search & Filter -->
      <div class="row g-3 mb-4">
        <div class="col-md-2">
          <select v-model="filters.role" class="form-select border-0 bg-light py-2 shadow-none">
            <option value="all">Tất cả vai trò</option>
            <option value="ADMIN">Admin</option>
            <option value="CUSTOMER">Customer</option>
          </select>
        </div>
        <div class="col-md-4">
          <input
            v-model="filters.search"
            @keyup.enter="fetchUsers"
            type="text"
            class="form-control border-0 bg-light py-2 shadow-none"
            placeholder="Tìm theo email hoặc tên..."
          />
        </div>
        <div class="col-md-4">
          <input
            v-model="filters.companySearch"
            @keyup.enter="fetchUsers"
            type="text"
            class="form-control border-0 bg-light py-2 shadow-none"
            placeholder="Tìm theo tên công ty..."
          />
        </div>
        <div class="col-md-2 text-end">
          <button class="btn btn-primary w-100 py-2 fw-bold shadow-sm" @click="fetchUsers">
            Tìm kiếm
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2 text-muted">Đang tải...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

      <!-- Table -->
      <div v-else class="table-responsive rounded-3 overflow-hidden">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-dark">
            <tr class="small text-uppercase fw-bold">
              <th class="ps-4" style="width:50px">STT</th>
              <th style="width:18%">Email</th>
              <th style="width:13%">Họ và tên</th>
              <th style="width:11%">SĐT</th>
              <th style="width:15%">Công ty</th>
              <th style="width:10%">Vai trò</th>
              <th style="width:9%">Trạng thái</th>
              <th style="width:10%">Ngày tạo</th>
              <th class="text-end pe-4" style="width:120px">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="users.length === 0">
              <td colspan="9" class="text-center py-5 text-muted">Không tìm thấy người dùng nào</td>
            </tr>
            <tr v-for="(user, index) in users" :key="user.id">
              <td class="ps-4 text-muted small">{{ page * pageSize + index + 1 }}</td>
              <td>
                <div class="fw-semibold text-dark small">{{ user.email }}</div>
              </td>
              <td>
                <div class="fw-bold">{{ user.fullName || '—' }}</div>
              </td>
              <td>
                <div class="small text-muted">{{ user.phone || '—' }}</div>
              </td>
              <td>
                <div class="small text-muted">{{ user.companyName || '—' }}</div>
                <div v-if="user.companyTaxCode" class="small text-muted fst-italic">MST: {{ user.companyTaxCode }}</div>
              </td>
              <td>
                <span
                  class="badge rounded-pill fw-normal px-3 py-2"
                  :class="roleBadgeClass(user.role)"
                >
                  {{ roleLabel(user.role) }}
                </span>
              </td>
              <td>
                <span
                  class="badge rounded-pill fw-normal px-2 py-1"
                  :class="user.isActive ? 'bg-success-subtle text-success border border-success' : 'bg-danger-subtle text-danger border border-danger'"
                >
                  {{ user.isActive ? 'Hoạt động' : 'Bị khoá' }}
                </span>
              </td>
              <td class="small text-muted">{{ formatDate(user.createdAt) }}</td>
              <td class="text-end pe-4">
                <div class="d-flex gap-1 justify-content-end">
                  <!-- Ẩn nút khoá nếu là Admin đang hoạt động -->
                  <button
                    v-if="!(user.role === 'ADMIN' && user.isActive)"
                    class="btn btn-sm btn-outline-warning rounded-pill px-2"
                    title="Khoá / Mở khoá tài khoản"
                    @click="handleToggleActive(user)"
                  >
                    {{ user.isActive ? '🔓' : '🔒' }}
                  </button>
                  <span
                    v-else
                    class="btn btn-sm rounded-pill px-2 text-muted"
                    style="cursor:not-allowed; opacity:0.4;"
                    title="Không thể khoá tài khoản Admin"
                  >🔒</span>
                  <button
                    class="btn btn-light btn-sm border px-3 rounded-pill fw-bold"
                    @click="openEdit(user)"
                  >
                   XEM CHI TIET
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="d-flex justify-content-between align-items-center mt-4">
        <span class="text-muted small">Tổng: {{ totalElements }} người dùng</span>
        <nav>
          <ul class="pagination pagination-sm mb-0">
            <li class="page-item" :class="{ disabled: page === 0 }">
              <button class="page-link" @click="goPage(page - 1)">‹</button>
            </li>
            <li
              v-for="p in totalPages"
              :key="p"
              class="page-item"
              :class="{ active: page === p - 1 }"
            >
              <button class="page-link" @click="goPage(p - 1)">{{ p }}</button>
            </li>
            <li class="page-item" :class="{ disabled: page === totalPages - 1 }">
              <button class="page-link" @click="goPage(page + 1)">›</button>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- Edit Modal - Full Page Style -->
    <div v-if="editUser" class="modal-backdrop-custom" @click.self="editUser = null">
      <div class="modal-fullpage shadow-lg bg-white">
        <!-- Header -->
        <div class="modal-header-bar d-flex align-items-center justify-content-between px-5 py-4 border-bottom">
          <div>
            <h4 class="fw-bold mb-0">✏️ Chỉnh sửa người dùng</h4>
            <p class="text-muted small mb-0 mt-1">Cập nhật thông tin tài khoản người dùng</p>
          </div>
          <button class="btn-close" @click="editUser = null"></button>
        </div>

        <!-- Body -->
        <div class="modal-body-scroll px-5 py-4">
          <div class="row g-4">

            <!-- Left column -->
            <div class="col-md-6">
              <h6 class="fw-bold text-uppercase text-muted small mb-3 border-bottom pb-2">Thông tin cơ bản</h6>

              <!-- Email (locked) -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Email <span class="badge bg-secondary ms-1 fw-normal small">Không thể sửa</span></label>
                <div class="input-group">
                  <span class="input-group-text bg-light border-0">
                    <svg width="16" height="16" fill="currentColor" class="text-muted" viewBox="0 0 16 16">
                      <path d="M0 4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V4Zm2-1a1 1 0 0 0-1 1v.217l7 4.2 7-4.2V4a1 1 0 0 0-1-1H2Zm13 2.383-4.708 2.825L15 11.105V5.383Zm-.034 6.876-5.64-3.471L8 9.583l-1.326-.795-5.64 3.47A1 1 0 0 0 2 13h12a1 1 0 0 0 .966-.741Z"/>
                    </svg>
                  </span>
                  <input class="form-control bg-light border-0 text-muted" :value="editUser.email" readonly disabled />
                </div>
              </div>

              <!-- Họ và tên (readonly) -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Họ và tên <span class="badge bg-secondary ms-1 fw-normal small">Không thể sửa</span></label>
                <input
                  :value="editUser.fullName || '—'"
                  type="text"
                  class="form-control border-0 bg-light text-muted"
                  readonly
                  disabled
                />
              </div>

              <!-- SĐT (có thể sửa) -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Số điện thoại</label>
                <input
                  v-model="editForm.phone"
                  type="tel"
                  class="form-control border-0 bg-light"
                  placeholder="Nhập số điện thoại..."
                />
              </div>

              <!-- Vai trò (readonly) -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Vai trò <span class="badge bg-secondary ms-1 fw-normal small">Không thể sửa</span></label>
                <input
                  :value="roleLabel(editUser.role)"
                  class="form-control border-0 bg-light text-muted"
                  readonly
                  disabled
                />
              </div>
            </div>

            <!-- Right column -->
            <div class="col-md-6">
              <h6 class="fw-bold text-uppercase text-muted small mb-3 border-bottom pb-2">Bảo mật tài khoản</h6>

              <!-- Trạng thái -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Trạng thái tài khoản</label>
                <div class="p-3 rounded-3 bg-light d-flex align-items-center gap-3">
                  <span
                    class="badge rounded-pill px-3 py-2"
                    :class="editUser.isActive ? 'bg-success' : 'bg-danger'"
                  >
                    {{ editUser.isActive ? '✅ Đang hoạt động' : '🔒 Đang bị khoá' }}
                  </span>
                  <span class="text-muted small">Dùng nút khoá/mở khoá ngoài bảng để thay đổi</span>
                </div>
              </div>

              <!-- Công ty -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Công ty <span class="badge bg-secondary ms-1 fw-normal small">Không thể sửa</span></label>
                <input
                  class="form-control border-0 bg-light text-muted"
                  :value="editUser.companyName || '—'"
                  readonly
                  disabled
                />
                <div v-if="editUser.companyTaxCode" class="form-text">MST: {{ editUser.companyTaxCode }}</div>
              </div>

            </div>
          </div>

          <!-- Error alert -->
          <div v-if="saveError" class="alert alert-danger mt-2">{{ saveError }}</div>
        </div>

        <!-- Footer -->
        <div class="modal-footer-bar d-flex justify-content-end gap-3 px-5 py-4 border-top">
          <button class="btn btn-light border rounded-pill px-5 py-2" @click="editUser = null">Huỷ</button>
          <button
            class="btn btn-primary rounded-pill px-5 py-2 fw-bold"
            :disabled="saving"
            @click="saveUser"
          >
            <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
            {{ saving ? 'Đang lưu...' : '💾 Lưu thay đổi' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { usersAPI } from '../../services/api.js';

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
  const action = user.isActive ? 'khoá' : 'mở khoá';
  if (!confirm(`Bạn có chắc muốn ${action} tài khoản ${user.email}?`)) return;
  try {
    const updated = await usersAPI.toggleActive(user.id);
    user.isActive = updated.isActive;
  } catch {
    alert('Không thể thay đổi trạng thái tài khoản.');
  }
};

const roleBadgeClass = (role) => {
  if (role === 'ADMIN') return 'bg-primary text-white';
  return 'bg-success-subtle text-success border border-success';
};

const roleLabel = (role) => {
  if (role === 'ADMIN') return 'Admin';
  return 'Customer';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

onMounted(fetchUsers);
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  padding: 20px;
}

.modal-fullpage {
  width: 100%;
  max-width: 860px;
  max-height: 90vh;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: modal-in 0.2s ease;
}

@keyframes modal-in {
  from { opacity: 0; transform: scale(0.96) translateY(-10px); }
  to   { opacity: 1; transform: scale(1) translateY(0); }
}

.modal-header-bar {
  background: #fff;
  flex-shrink: 0;
}

.modal-body-scroll {
  flex: 1;
  overflow-y: auto;
}

.modal-footer-bar {
  background: #f8f9fa;
  flex-shrink: 0;
}
</style>