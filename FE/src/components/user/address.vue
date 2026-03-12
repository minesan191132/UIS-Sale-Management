<template>
  <section class="content-section">
    <div class="section-header">
      <h1 class="section-title">Địa Chỉ Của Tôi</h1>
      <p class="section-desc">Quản lý thông tin địa chỉ giao hàng của bạn</p>
    </div>
    <div class="section-divider"></div>

    <!-- Address List -->
    <div class="address-list" v-if="!showForm">
      <button class="btn-add" @click="openAddForm">
        <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24"
          fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        Thêm Địa Chỉ Mới
      </button>

      <div v-if="addresses.length === 0" class="empty-state">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24"
          fill="none" stroke="#cbd5e1" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
          <circle cx="12" cy="10" r="3"/>
        </svg>
        <p>Bạn chưa có địa chỉ nào. Hãy thêm địa chỉ mới!</p>
      </div>

      <div v-for="(addr, idx) in addresses" :key="idx" class="address-card">
        <div class="address-card-header">
          <div class="addr-name-row">
            <span class="addr-name">{{ addr.fullName }}</span>
            <span v-if="addr.isDefault" class="badge-default">Mặc định</span>
          </div>
          <div class="addr-actions">
            <button class="btn-link" @click="openEditForm(idx)">Cập nhật</button>
            <span class="sep">|</span>
            <button class="btn-link btn-delete" @click="deleteAddress(idx)" v-if="!addr.isDefault">Xóa</button>
            <button class="btn-link" @click="setDefault(idx)" v-if="!addr.isDefault">Thiết lập mặc định</button>
          </div>
        </div>
        <div class="addr-detail">
          <p class="addr-phone">{{ addr.phone }}</p>
          <p class="addr-text">{{ addr.detail }}, {{ addr.ward }}, {{ addr.district }}, {{ addr.province }}</p>
        </div>
      </div>
    </div>

    <!-- Address Form -->
    <div class="address-form-wrap" v-else>
      <h2 class="form-title">{{ editIndex !== null ? 'Cập Nhật Địa Chỉ' : 'Địa Chỉ Mới' }}</h2>
      <form class="addr-form" @submit.prevent="saveAddress">
        <div class="form-row-2col">
          <div class="form-field">
            <label>Họ và tên</label>
            <input type="text" v-model="form.fullName" placeholder="Nhập họ và tên" required />
          </div>
          <div class="form-field">
            <label>Số điện thoại</label>
            <input type="tel" v-model="form.phone" placeholder="Nhập số điện thoại" required />
          </div>
        </div>

        <div class="form-row-3col">
          <div class="form-field">
            <label>Tỉnh / Thành phố</label>
            <select v-model="form.province" required>
              <option value="">Chọn Tỉnh/TP</option>
              <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
            </select>
          </div>
          <div class="form-field">
            <label>Quận / Huyện</label>
            <input type="text" v-model="form.district" placeholder="Nhập Quận/Huyện" required />
          </div>
          <div class="form-field">
            <label>Phường / Xã</label>
            <input type="text" v-model="form.ward" placeholder="Nhập Phường/Xã" required />
          </div>
        </div>

        <div class="form-field">
          <label>Địa chỉ cụ thể</label>
          <input type="text" v-model="form.detail" placeholder="Số nhà, tên đường..." required />
        </div>

        <div class="form-check">
          <input type="checkbox" id="isDefault" v-model="form.isDefault" />
          <label for="isDefault">Đặt làm địa chỉ mặc định</label>
        </div>

        <div class="form-buttons">
          <button type="button" class="btn-cancel" @click="cancelForm">Trở Lại</button>
          <button type="submit" class="btn-save">Xác nhận</button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userAPI } from '../../services/api'
import Swal from 'sweetalert2'

const showForm = ref(false)
const editIndex = ref(null)
const editId = ref(null)
const addresses = ref([])
const isLoading = ref(false)

const emptyForm = () => ({ fullName: '', phone: '', province: '', district: '', ward: '', detail: '', isDefault: false })
const form = ref(emptyForm())

const provinces = [
  'Hà Nội', 'TP. Hồ Chí Minh', 'Đà Nẵng', 'Hải Phòng', 'Cần Thơ',
  'An Giang', 'Bà Rịa - Vũng Tàu', 'Bắc Giang', 'Bắc Kạn', 'Bạc Liêu',
  'Bắc Ninh', 'Bến Tre', 'Bình Định', 'Bình Dương', 'Bình Phước',
  'Bình Thuận', 'Cà Mau', 'Cao Bằng', 'Đắk Lắk', 'Đắk Nông',
  'Điện Biên', 'Đồng Nai', 'Đồng Tháp', 'Gia Lai', 'Hà Giang',
  'Hà Nam', 'Hà Tĩnh', 'Hải Dương', 'Hậu Giang', 'Hòa Bình',
  'Hưng Yên', 'Khánh Hòa', 'Kiên Giang', 'Kon Tum', 'Lai Châu',
  'Lâm Đồng', 'Lạng Sơn', 'Lào Cai', 'Long An', 'Nam Định',
  'Nghệ An', 'Ninh Bình', 'Ninh Thuận', 'Phú Thọ', 'Phú Yên',
  'Quảng Bình', 'Quảng Nam', 'Quảng Ngãi', 'Quảng Ninh', 'Quảng Trị',
  'Sóc Trăng', 'Sơn La', 'Tây Ninh', 'Thái Bình', 'Thái Nguyên',
  'Thanh Hóa', 'Thừa Thiên Huế', 'Tiền Giang', 'Trà Vinh', 'Tuyên Quang',
  'Vĩnh Long', 'Vĩnh Phúc', 'Yên Bái',
]

const loadAddresses = async () => {
  try {
    addresses.value = await userAPI.getAddresses()
  } catch {
    console.warn('Could not load addresses from API.')
  }
}

const openAddForm = () => { form.value = emptyForm(); editIndex.value = null; editId.value = null; showForm.value = true }
const openEditForm = (idx) => {
  const addr = addresses.value[idx]
  form.value = { ...addr }
  editIndex.value = idx
  editId.value = addr.id
  showForm.value = true
}
const cancelForm = () => { showForm.value = false; editIndex.value = null; editId.value = null }

const saveAddress = async () => {
  isLoading.value = true
  try {
    if (editId.value !== null) {
      const updated = await userAPI.updateAddress(editId.value, form.value)
      addresses.value[editIndex.value] = updated
    } else {
      const created = await userAPI.addAddress(form.value)
      // If marked as default, clear others
      if (created.isDefault) addresses.value.forEach(a => (a.isDefault = false))
      addresses.value.push(created)
    }
    Swal.fire({ icon: 'success', title: 'Đã lưu!', text: 'Địa chỉ đã được cập nhật.', timer: 1500, showConfirmButton: false })
    cancelForm()
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: err.response?.data?.error || 'Không thể lưu địa chỉ.' })
  } finally {
    isLoading.value = false
  }
}

const deleteAddress = (idx) => {
  const addr = addresses.value[idx]
  Swal.fire({
    title: 'Xóa địa chỉ?',
    text: 'Bạn có chắc muốn xóa địa chỉ này không?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#2563eb',
    cancelButtonColor: '#94a3b8',
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
  }).then(async (r) => {
    if (r.isConfirmed) {
      try {
        await userAPI.deleteAddress(addr.id)
        addresses.value.splice(idx, 1)
      } catch (err) {
        Swal.fire({ icon: 'error', title: 'Lỗi', text: err.response?.data?.error || 'Không thể xóa địa chỉ.' })
      }
    }
  })
}

const setDefault = async (idx) => {
  const addr = addresses.value[idx]
  try {
    await userAPI.setDefaultAddress(addr.id)
    addresses.value.forEach((a, i) => (a.isDefault = i === idx))
    Swal.fire({ icon: 'success', title: 'Đã đặt mặc định!', timer: 1200, showConfirmButton: false })
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: err.response?.data?.error || 'Không thể đặt mặc định.' })
  }
}

onMounted(() => loadAddresses())
</script>


<style scoped>
/* Mirrors accountsetting.vue design tokens */
.content-section { padding: 28px 32px; }

.section-header { margin-bottom: 10px; }

.section-title {
  font-size: 19px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px;
}

.section-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.section-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0 0 20px;
}

/* Add button */
.btn-add {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 8px 18px;
  border: 1.5px dashed #2563eb;
  background: #fff;
  color: #2563eb;
  border-radius: 6px;
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  margin-bottom: 20px;
  transition: background 0.2s, border-color 0.2s;
}

.btn-add:hover { background: #eff6ff; }

/* Empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 0;
  color: #94a3b8;
  font-size: 14px;
}

/* Address card */
.address-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 12px;
  transition: box-shadow 0.2s, border-color 0.2s;
}

.address-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  border-color: #bfdbfe;
}

.address-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.addr-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.addr-name {
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
}

.badge-default {
  font-size: 11px;
  border: 1px solid #2563eb;
  color: #2563eb;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.addr-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-link {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 13px;
  color: #2563eb;
  padding: 0;
  transition: opacity 0.15s;
}

.btn-link:hover { opacity: 0.7; }
.btn-delete { color: #ef4444; }
.sep { color: #e2e8f0; font-size: 13px; }

.addr-detail { font-size: 13px; color: #64748b; }
.addr-phone { margin: 0 0 4px; }
.addr-text { margin: 0; }

/* Form */
.address-form-wrap { padding-top: 20px; }

.form-title {
  font-size: 17px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px;
}

.addr-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 700px;
}

.form-row-2col { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-row-3col { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px; }

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field label {
  font-size: 13px;
  color: #475569;
  font-weight: 500;
}

.form-field input,
.form-field select {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  color: #1e293b;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #fff;
}

.form-field input:focus,
.form-field select:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-check {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #475569;
}

.form-check input[type="checkbox"] {
  accent-color: #2563eb;
  width: 15px;
  height: 15px;
}

.form-buttons {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn-cancel {
  padding: 9px 24px;
  background: #fff;
  color: #475569;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s, background 0.2s;
}

.btn-cancel:hover {
  border-color: #94a3b8;
  background: #f8fafc;
}

.btn-save {
  padding: 9px 32px;
  background: linear-gradient(135deg, #1e3a8a, #2563eb);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.btn-save:hover {
  background: linear-gradient(135deg, #1e40af, #1d4ed8);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
}

@media (max-width: 600px) {
  .form-row-2col { grid-template-columns: 1fr; }
  .form-row-3col { grid-template-columns: 1fr; }
}
</style>
