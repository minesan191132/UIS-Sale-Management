<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, watch, computed } from 'vue';
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import Navbar from '../base/Navbar.vue';
import Footer from '../base/Footer.vue';
import { cartState, cartTotalPrice, loadCart } from '../../store/cart.js';
import { isAuthenticated, getStoredUser, userAPI, ordersAPI } from '../../services/api';
import Swal from 'sweetalert2';

const router = useRouter();
const CHECKOUT_DRAFT_STORAGE_PREFIX = 'checkout.formDraft.v1.';
const CHECKOUT_DRAFT_MAX_AGE_MS = 3 * 24 * 60 * 60 * 1000;

const selectedPayment = ref('BANK');
const isSubmitting = ref(false);
const initialCheckoutState = ref(null);
const checkoutDraftRestoredAt = ref(null);
const bypassCheckoutLeaveGuard = ref(false);
let checkoutDraftSaveTimer = null;

const form = reactive({
  name: '',
  phone: '',
  orderEmail: '',
  address: '',
  companyName: '',
  taxId: '',
  invoiceEmail: '',
  note: ''
});

const getCheckoutDraftKey = () => {
  const identity = String(getStoredUser()?.email || 'guest').toLowerCase();
  return `${CHECKOUT_DRAFT_STORAGE_PREFIX}${identity}`;
};

const normalizeCheckoutState = (state) => {
  const source = state || {};
  const sourceForm = source.form || {};

  return {
    selectedPayment: source.selectedPayment === 'COD' ? 'COD' : 'BANK',
    form: {
      name: String(sourceForm.name || ''),
      phone: String(sourceForm.phone || ''),
      orderEmail: String(sourceForm.orderEmail || ''),
      address: String(sourceForm.address || ''),
      companyName: String(sourceForm.companyName || ''),
      taxId: String(sourceForm.taxId || ''),
      invoiceEmail: String(sourceForm.invoiceEmail || ''),
      note: String(sourceForm.note || ''),
    },
  };
};

const getCurrentCheckoutState = () => {
  return normalizeCheckoutState({
    selectedPayment: selectedPayment.value,
    form,
  });
};

const getComparableCheckoutState = (state) => {
  const normalized = normalizeCheckoutState(state);
  return {
    selectedPayment: normalized.selectedPayment,
    form: {
      name: normalized.form.name.trim(),
      phone: normalized.form.phone.trim(),
      orderEmail: normalized.form.orderEmail.trim(),
      address: normalized.form.address.trim(),
      companyName: normalized.form.companyName.trim(),
      taxId: normalized.form.taxId.trim(),
      invoiceEmail: normalized.form.invoiceEmail.trim(),
      note: normalized.form.note.trim(),
    },
  };
};

const hasMeaningfulCheckoutDraft = (state) => {
  const comparable = getComparableCheckoutState(state);
  if (comparable.selectedPayment !== 'BANK') return true;
  return Object.values(comparable.form).some((value) => String(value || '').trim().length > 0);
};

const applyCheckoutState = (state) => {
  const normalized = normalizeCheckoutState(state);
  selectedPayment.value = normalized.selectedPayment;
  Object.assign(form, normalized.form);
};

const setInitialCheckoutState = () => {
  initialCheckoutState.value = getComparableCheckoutState(getCurrentCheckoutState());
};

const hasCheckoutUnsavedChanges = computed(() => {
  if (!initialCheckoutState.value) return false;
  const current = getComparableCheckoutState(getCurrentCheckoutState());
  return JSON.stringify(current) !== JSON.stringify(initialCheckoutState.value);
});

const clearCheckoutDraft = () => {
  try {
    localStorage.removeItem(getCheckoutDraftKey());
  } catch (error) {
    console.warn('Failed to clear checkout draft:', error);
  }
  checkoutDraftRestoredAt.value = null;
};

const saveCheckoutDraft = () => {
  if (!initialCheckoutState.value) return;

  const snapshot = getCurrentCheckoutState();
  if (!hasMeaningfulCheckoutDraft(snapshot)) {
    clearCheckoutDraft();
    return;
  }

  const payload = {
    ...snapshot,
    savedAt: Date.now(),
  };

  try {
    localStorage.setItem(getCheckoutDraftKey(), JSON.stringify(payload));
  } catch (error) {
    console.warn('Failed to save checkout draft:', error);
  }
};

const loadCheckoutDraft = () => {
  try {
    const raw = localStorage.getItem(getCheckoutDraftKey());
    if (!raw) return null;

    const parsed = JSON.parse(raw);
    const savedAt = Number(parsed?.savedAt || 0);

    if (savedAt > 0 && Date.now() - savedAt > CHECKOUT_DRAFT_MAX_AGE_MS) {
      clearCheckoutDraft();
      return null;
    }

    return {
      ...normalizeCheckoutState(parsed),
      savedAt,
    };
  } catch (error) {
    console.warn('Failed to load checkout draft:', error);
    return null;
  }
};

const scheduleCheckoutDraftSave = () => {
  if (!initialCheckoutState.value) return;

  if (checkoutDraftSaveTimer) {
    window.clearTimeout(checkoutDraftSaveTimer);
  }

  checkoutDraftSaveTimer = window.setTimeout(() => {
    saveCheckoutDraft();
  }, 350);
};

const handleBeforeUnload = (event) => {
  if (isSubmitting.value || !hasCheckoutUnsavedChanges.value) return;
  event.preventDefault();
  event.returnValue = '';
};

watch(form, () => {
  scheduleCheckoutDraftSave();
}, { deep: true });

watch(selectedPayment, () => {
  scheduleCheckoutDraftSave();
});

onBeforeRouteLeave(async () => {
  if (bypassCheckoutLeaveGuard.value) return true;

  if (isSubmitting.value) {
    await Swal.fire('Đang xử lý đơn hàng', 'Vui lòng chờ hệ thống hoàn tất thao tác.', 'info');
    return false;
  }

  if (!hasCheckoutUnsavedChanges.value) return true;

  saveCheckoutDraft();
  const confirmLeave = await Swal.fire({
    title: 'Rời trang thanh toán?',
    text: 'Thông tin đã được lưu nháp. Nếu rời trang, bạn có thể khôi phục khi quay lại.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Vẫn rời trang',
    cancelButtonText: 'Ở lại',
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
  });

  return confirmLeave.isConfirmed;
});

const hydrateCheckoutForm = async () => {
  if (isAuthenticated()) {
    const basicUser = getStoredUser();
    if (basicUser) {
      form.name = basicUser.fullName || '';
      form.orderEmail = basicUser.email || '';
    }
    try {
      const fullProfile = await userAPI.getProfile();
      form.name = fullProfile.fullName || form.name;
      form.phone = fullProfile.phone || '';
      form.companyName = fullProfile.companyName || '';
      form.taxId = fullProfile.taxCode || fullProfile.taxId || '';
      form.invoiceEmail = fullProfile.companyEmail || form.orderEmail;

      // --- LẤY ĐỊA CHỈ MẶC ĐỊNH ---
      const addresses = await userAPI.getAddresses();
      if (addresses && addresses.length > 0) {
        // Ưu tiên tìm địa chỉ có isDefault = true, nếu không có thì lấy đại cái đầu tiên
        const defaultAddr = addresses.find(a => a.isDefault) || addresses[0];
        
        // Ghi đè Tên, SĐT và Địa chỉ theo Sổ địa chỉ
        form.name = defaultAddr.fullName;
        form.phone = defaultAddr.phone;
        form.address = `${defaultAddr.detail}, ${defaultAddr.ward}, ${defaultAddr.district}, ${defaultAddr.province}`;
      }

    } catch (error) {
      console.error('Không thể lấy full thông tin user:', error);
    }
  }
};

const tryRestoreCheckoutDraft = async () => {
  const draft = loadCheckoutDraft();
  if (!draft) return;

  const restore = await Swal.fire({
    title: 'Khôi phục thông tin thanh toán?',
    text: `Đã tìm thấy bản nháp lưu lúc ${new Date(draft.savedAt || Date.now()).toLocaleString('vi-VN')}.`,
    icon: 'question',
    showDenyButton: true,
    showCancelButton: true,
    confirmButtonText: 'Khôi phục',
    denyButtonText: 'Xóa nháp',
    cancelButtonText: 'Bỏ qua',
    confirmButtonColor: '#0d6efd',
    denyButtonColor: '#dc2626',
    cancelButtonColor: '#64748b',
  });

  if (restore.isConfirmed) {
    applyCheckoutState(draft);
    checkoutDraftRestoredAt.value = draft.savedAt || Date.now();
  } else if (restore.isDenied) {
    clearCheckoutDraft();
  }
};

onMounted(async () => {
  window.addEventListener('beforeunload', handleBeforeUnload);
  await hydrateCheckoutForm();
  await tryRestoreCheckoutDraft();
  setInitialCheckoutState();
});

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload);
  if (checkoutDraftSaveTimer) {
    window.clearTimeout(checkoutDraftSaveTimer);
    checkoutDraftSaveTimer = null;
  }
});

async function submitOrder() {
  if (isSubmitting.value) return;

  // Validate required fields
  if (!form.name.trim()) {
    Swal.fire('Thiếu thông tin', 'Vui lòng nhập tên người nhận.', 'warning');
    return;
  }
  if (!form.phone.trim()) {
    Swal.fire('Thiếu thông tin', 'Vui lòng nhập số điện thoại.', 'warning');
    return;
  }
  if (!form.address.trim()) {
    Swal.fire('Thiếu thông tin', 'Vui lòng nhập địa chỉ nhận hàng.', 'warning');
    return;
  }
  if (cartState.items.length === 0) {
    Swal.fire('Giỏ hàng trống', 'Vui lòng thêm sản phẩm vào giỏ hàng.', 'warning');
    return;
  }
  if (!isAuthenticated()) {
    saveCheckoutDraft();
    bypassCheckoutLeaveGuard.value = true;
    router.push({ path: '/login', query: { redirect: '/checkout' } });
    return;
  }

  const confirmSubmit = await Swal.fire({
    title: 'Xác nhận đặt hàng?',
    text: selectedPayment.value === 'BANK'
      ? 'Bạn sẽ được chuyển đến trang QR để thanh toán ngay sau khi tạo đơn.'
      : 'Đơn hàng COD sẽ được xác nhận và nhân viên sẽ liên hệ với bạn.',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Đặt hàng',
    cancelButtonText: 'Xem lại',
    confirmButtonColor: '#0b2e59',
    cancelButtonColor: '#64748b',
  });

  if (!confirmSubmit.isConfirmed) return;

  isSubmitting.value = true;
  try {
    const payload = {
      items: cartState.items.map(item => ({
        productId: item.id,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
        image: item.image
      })),
      shippingInfo: {
        recipientName: form.name,
        phone: form.phone,
        email: form.orderEmail,
        address: form.address,
        companyName: form.companyName,
        taxId: form.taxId,
        invoiceEmail: form.invoiceEmail,
        note: form.note,
        paymentMethod: selectedPayment.value
      }
    };

    const order = await ordersAPI.createFromCart(payload);

    // Clear cart after order created
    cartState.items = [];
    localStorage.removeItem(`upec_cart_${getStoredUser()?.email}`);
    loadCart();
    clearCheckoutDraft();
    bypassCheckoutLeaveGuard.value = true;

    if (selectedPayment.value === 'BANK') {
      // Redirect to QR payment page
      router.push({ name: 'payment-qr', params: { orderId: order.id } });
    } else {
      // COD — show success and go to order history
      await Swal.fire({
        icon: 'success',
        title: 'Đặt hàng thành công!',
        text: `Mã đơn hàng: ${order.orderNumber}. Nhân viên sẽ liên hệ xác nhận.`,
        confirmButtonText: 'Xem đơn hàng'
      });
      router.push('/account');
    }
  } catch (err) {
    const msg = err.response?.data?.error || err.message || 'Có lỗi xảy ra, vui lòng thử lại.';
    Swal.fire('Đặt hàng thất bại', msg, 'error');
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<template>
  <div class="checkout-page d-flex flex-column min-vh-100 bg-light">
    <Navbar />
    
    <div class="container py-5 mt-4 flex-grow-1 page-container">
      
      <div class="d-flex align-items-center mb-4 fade-in">
        <router-link to="/cart" class="text-decoration-none text-muted me-3 hover-elevate">
          <i class="bi bi-arrow-left-circle-fill fs-3"></i>
        </router-link>
        <h2 class="mb-0 fw-bold" style="color: #0b2e59;">Xác nhận đặt hàng</h2>
      </div>

      <div class="alert alert-primary alert-fit-content py-2 px-3 small mb-3">
        <i class="bi bi-shield-check me-1"></i>
        Thông tin thanh toán được lưu nháp tự động để tránh mất dữ liệu khi rời trang.
      </div>

      <div v-if="checkoutDraftRestoredAt" class="alert alert-info alert-fit-content py-2 px-3 small mb-3">
        <i class="bi bi-clock-history me-1"></i>
        Đã khôi phục bản nháp lưu lúc {{ new Date(checkoutDraftRestoredAt).toLocaleString('vi-VN') }}.
      </div>

      <div class="row g-4 slide-up">
        
        <div class="col-lg-7">
          
          <div class="card shadow-sm border-0 rounded-4 mb-4">
            <div class="card-body p-4 p-md-5">
              <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-4">
                <h5 class="fw-bold text-uppercase m-0" style="color: #0b2e59;">
                  <i class="bi bi-geo-alt-fill text-danger me-2"></i>Thông tin giao nhận
                </h5>
                <span class="badge bg-success bg-opacity-10 text-success border border-success px-2 py-1">
                  <i class="bi bi-person-check-fill me-1"></i> Đã điền từ tài khoản
                </span>
              </div>
              
              <form class="row g-3">
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-muted">Người nhận <span class="text-danger">*</span></label>
                  <input type="text" class="form-control rounded-3 bg-light border-0 py-2" v-model="form.name">
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-muted">Số điện thoại liên hệ <span class="text-danger">*</span></label>
                  <input type="tel" class="form-control rounded-3 bg-light border-0 py-2" v-model="form.phone">
                </div>
                <div class="col-12">
                  <label class="form-label small fw-bold text-muted">Email nhận thông báo đơn hàng</label>
                  <input type="email" class="form-control rounded-3 bg-light border-0 py-2" v-model="form.orderEmail">
                </div>
                <div class="col-12">
                  <label class="form-label small fw-bold text-muted">Địa chỉ nhận hàng chi tiết <span class="text-danger">*</span></label>
                  <input type="text" class="form-control rounded-3 bg-light border-0 py-2" placeholder="Ví dụ: Xưởng 2, Lô B2 KCN Tân Bình..." v-model="form.address">
                </div>

                <div class="col-12 mt-4">
                  <div class="bg-light p-4 rounded-4 border border-secondary border-opacity-10">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                      <label class="form-label small fw-bold text-dark m-0">Thông tin xuất hóa đơn (VAT)</label>
                      <small class="text-muted fst-italic">Có thể chỉnh sửa</small>
                    </div>
                    <div class="row g-3">
                      <div class="col-md-7">
                        <label class="form-label small text-muted mb-1">Tên Doanh nghiệp</label>
                        <input type="text" class="form-control rounded-3 border-0 py-2 shadow-none" v-model="form.companyName">
                      </div>
                      <div class="col-md-5">
                        <label class="form-label small text-muted mb-1">Mã số thuế</label>
                        <input type="text" class="form-control rounded-3 border-0 py-2 shadow-none" v-model="form.taxId">
                      </div>
                      <div class="col-12">
                        <label class="form-label small text-muted mb-1">Email nhận hóa đơn điện tử</label>
                        <input type="email" class="form-control rounded-3 border-0 py-2 shadow-none" v-model="form.invoiceEmail">
                      </div>
                    </div>
                  </div>
                </div>

                <div class="col-12 mt-4">
                  <label class="form-label small fw-bold text-muted">Ghi chú yêu cầu kỹ thuật</label>
                  <textarea class="form-control rounded-3 bg-light border-0" rows="3" placeholder="Ví dụ: Cắt phôi dài 2 mét, cấp chứng chỉ CO/CQ..." v-model="form.note"></textarea>
                </div>
              </form>
            </div>
          </div>

          <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-4 p-md-5">
              <h5 class="fw-bold mb-4 text-uppercase border-bottom pb-3" style="color: #0b2e59;">
                <i class="bi bi-wallet2 text-success me-2"></i>Phương thức thanh toán
              </h5>
              
              <div class="d-flex flex-column gap-3">
                <label class="border rounded-3 p-3 d-flex align-items-center cursor-pointer transition-all" 
                       :class="{ 'border-primary bg-primary bg-opacity-10': selectedPayment === 'BANK' }">
                  <input class="form-check-input mt-0 me-3" type="radio" value="BANK" v-model="selectedPayment" style="transform: scale(1.2);">
                  <div class="d-flex flex-grow-1 justify-content-between align-items-center">
                    <div>
                      <h6 class="mb-0 fw-bold text-dark">Chuyển khoản ngân hàng (SePay)</h6>
                      <small class="text-muted">Quét mã QR tự động xác nhận nhanh chóng</small>
                    </div>
                    <i class="bi bi-qr-code fs-3 text-primary opacity-75"></i>
                  </div>
                </label>

                <label class="border rounded-3 p-3 d-flex align-items-center cursor-pointer transition-all"
                       :class="{ 'border-primary bg-primary bg-opacity-10': selectedPayment === 'COD' }">
                  <input class="form-check-input mt-0 me-3" type="radio" value="COD" v-model="selectedPayment" style="transform: scale(1.2);">
                  <div class="d-flex flex-grow-1 justify-content-between align-items-center">
                    <div>
                      <h6 class="mb-0 fw-bold text-dark">Thanh toán khi nhận hàng (COD)</h6>
                      <small class="text-muted">Thanh toán bằng tiền mặt khi xe tải giao hàng đến</small>
                    </div>
                    <i class="bi bi-truck fs-3 text-success opacity-75"></i>
                  </div>
                </label>
              </div>
            </div>
          </div>

        </div>

        <div class="col-lg-5">
          <div class="card shadow-sm border-0 rounded-4 sticky-top" style="top: 80px;">
            <div class="card-body p-4 p-md-5">
              <h5 class="fw-bold mb-4 text-uppercase border-bottom pb-3" style="color: #0b2e59;">
                <i class="bi bi-receipt me-2"></i>Đơn hàng của bạn
              </h5>
              
              <div class="order-items-scroll mb-4 pe-2" style="max-height: 350px; overflow-y: auto;">
                <div v-for="item in cartState.items" :key="item.id" class="d-flex align-items-center mb-3 pb-3 border-bottom">
                  <img :src="item.image" style="width: 55px; height: 55px; object-fit: cover;" class="border rounded-3 me-3 bg-light">
                  <div class="flex-grow-1">
                    <h6 class="mb-1 fw-bold text-dark" style="font-size: 0.95rem;">{{ item.name }}</h6>
                    <div class="d-flex justify-content-between align-items-center mt-1">
                      <small class="text-muted">SL: <span class="fw-bold">{{ item.quantity }}</span></small>
                      <small class="fw-bold text-danger">{{ (item.price * item.quantity).toLocaleString('vi-VN') }} ₫</small>
                    </div>
                  </div>
                </div>
              </div>

              <div class="d-flex justify-content-between mb-2 text-muted">
                <span>Tạm tính ({{ cartState.items.length }} loại):</span>
                <span class="fw-bold text-dark">{{ cartTotalPrice.toLocaleString('vi-VN') }} ₫</span>
              </div>
              <div class="d-flex justify-content-between mb-3 text-muted">
                <span>Phí vận chuyển:</span>
                <span class="fw-bold text-success">Thỏa thuận sau</span>
              </div>
              
              <div class="d-flex justify-content-between mb-4 border-top pt-3">
                <span class="fw-bold text-dark fs-5 mt-1">TỔNG CỘNG:</span>
                <span class="fw-bold text-danger fs-3">{{ cartTotalPrice.toLocaleString('vi-VN') }} ₫</span>
              </div>

              <button 
                @click="submitOrder"
                :disabled="isSubmitting"
                class="btn w-100 fw-bold text-white rounded-3 py-3 text-uppercase fs-6 shadow-sm btn-glow hover-elevate" 
                style="background-color: #0b2e59;">
                <span v-if="isSubmitting">
                  <span class="spinner-border spinner-border-sm me-2" role="status"></span>Đang xử lý...
                </span>
                <span v-else>
                  Xác nhận đặt hàng <i class="bi bi-check2-circle ms-2"></i>
                </span>
              </button>

              <p v-if="hasCheckoutUnsavedChanges && !isSubmitting" class="text-warning small mt-2 mb-0">
                <i class="bi bi-save2 me-1"></i>Thông tin thay đổi sẽ được lưu nháp tự động.
              </p>
              
              <p class="text-center text-muted small mt-3 mb-0">
                <i class="bi bi-shield-lock text-success me-1"></i> Thông tin của bạn được bảo mật tuyệt đối.
              </p>
            </div>
          </div>
        </div>

      </div>
    </div>
    
    <Footer />
  </div>
</template>

<style scoped>
/* Animation & Hiệu ứng */
.page-container { animation: fadeIn 0.4s ease-out forwards; }
.fade-in { animation: fadeIn 0.6s ease-out forwards; }
.slide-up { opacity: 0; transform: translateY(20px); animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.hover-elevate { transition: transform 0.2s ease; }
.hover-elevate:hover { transform: translateY(-3px); }
.cursor-pointer { cursor: pointer; }
.transition-all { transition: all 0.2s ease; }

input.form-control:focus, textarea.form-control:focus {
  background-color: #fff !important;
  border-color: #0b2e59;
  box-shadow: 0 0 0 0.25rem rgba(11, 46, 89, 0.1);
}

.btn-glow { position: relative; overflow: hidden; }
.btn-glow::after {
  content: ''; position: absolute; top: 50%; left: 50%; width: 120%; height: 120%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, transparent 60%);
  transform: translate(-50%, -50%) scale(0); opacity: 0; transition: transform 0.4s ease, opacity 0.4s ease;
}
.btn-glow:hover::after { transform: translate(-50%, -50%) scale(1); opacity: 1; }

.order-items-scroll::-webkit-scrollbar { width: 5px; }
.order-items-scroll::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 10px; }
.order-items-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.order-items-scroll::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>