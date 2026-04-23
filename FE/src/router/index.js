import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../services/api'

import AccountSetting from '../components/user/accountsetting.vue'

const HomeView = () => import('../components/homepage/Homepage.vue')
const ServiceView = () => import('../components/services/Service.vue')
const ServiceDetailView = () => import('../components/services/ServiceDetail.vue')
const Product = () => import('../components/product/Product.vue')
const ProductDetail = () => import('../components/product/ProductDetail.vue')
const Contact = () => import('../components/contact/Contact.vue')
const Cart = () => import('../components/cart/Cart.vue')
const Login = () => import('../components/auth/Login.vue')
const Register = () => import('../components/auth/Register.vue')
const CreateOrder = () => import('../components/customer/CreateOrder.vue')
const MyOrders = () => import('../components/customer/MyOrders.vue')
const AdminLayout = () => import('../layouts/AdminLayout.vue')
const InventoryView = () => import('../components/admin/home.vue')
const InvoiceMgmtView = () => import('../components/admin/ExportManagement.vue')
const PreviewInvoiceView = () => import('../components/admin/preview.vue')
const StatisticView = () => import('../components/admin/thongke.vue')
const WarehouseView = () => import('../components/admin/nhapkho.vue')
const WarehouseHistoryView = () => import('../components/admin/lichsunhapkho.vue')
const ProductView = () => import('../components/admin/product.vue')
const UserEditView = () => import('../components/admin/user_edit.vue')
const UserListView = () => import('../components/admin/user_list.vue')
const ProductOrderMgmtView = () => import('../components/admin/ProductOrderManagement.vue')
const ComplaintMgmtView = () => import('../components/admin/ComplaintManagement.vue') // Thêm mới

const ResendVerification = () => import('../components/auth/ResendVerification.vue')
const ForgotPassword = () => import('../components/auth/ForgotPassword.vue')
const VerifyOtp = () => import('../components/auth/VerifyOtp.vue')
const ResetPassword = () => import('../components/auth/ResetPassword.vue')
const Checkout = () => import('../components/payment/Checkout.vue')
const CheckoutPaymentQR = () => import('../components/payment/PaymentQR.vue')

let adminChunksPrefetched = false;
const prefetchAdminChunks = () => {
  if (adminChunksPrefetched || typeof window === 'undefined') return;
  adminChunksPrefetched = true;

  const run = () => {
    Promise.allSettled([
      InventoryView(),
      InvoiceMgmtView(),
      PreviewInvoiceView(),
      StatisticView(),
      ComplaintMgmtView(),
    ]).catch(() => {
      // Ignore prefetch failures to avoid impacting navigation.
    });
  };

  if (typeof window.requestIdleCallback === 'function') {
    window.requestIdleCallback(run, { timeout: 1200 });
    return;
  }

  window.setTimeout(run, 200);
};

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/services',
    name: 'service',
    component: ServiceView
  },
  {
    path: '/services/:slug',
    name: 'service-detail',
    component: ServiceDetailView
  },
  {
    path: '/products',
    name: 'product',
    component: Product
  },
  {
    path: '/product/:id',
    name: 'product-detail',
    component: ProductDetail
  },
  {
    path: '/contact',
    name: 'contact',
    component: Contact
  },
  {
    path: '/cart',
    name: 'cart',
    component: Cart
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: Checkout
  },
  {
    path: '/payment/:orderId',
    name: 'payment-qr',
    component: CheckoutPaymentQR,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '/resend-verification',
    name: 'ResendVerification',
    component: ResendVerification
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword
  },
  {
    path: '/forgot-password/verify',
    name: 'VerifyOtp',
    component: VerifyOtp
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: ResetPassword
  },
  {
    path: '/create-order',
    name: 'create-order',
    component: CreateOrder,
    meta: { requiresAuth: true, roles: ['CUSTOMER'] }
  },
  {
    path: '/my-orders',
    name: 'my-orders',
    component: MyOrders,
    meta: { requiresAuth: true, roles: ['CUSTOMER'] }
  },
  {
    path: '/account',
    name: 'account-setting',
    component: AccountSetting,
    meta: { requiresAuth: true }
  },

  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true }, // Protect all admin routes
    children: [
      { path: 'dashboard', name: 'dashboard', component: StatisticView, meta: { roles: ['ADMIN'] } },
      { path: 'inventory', name: 'inventory', component: InventoryView }, // All authenticated users
      { path: 'invoice-management', name: 'invoice-management', component: InvoiceMgmtView },
      { path: 'product-orders', name: 'product-orders', component: ProductOrderMgmtView },
      { path: 'invoice-preview', name: 'invoice-preview', component: PreviewInvoiceView },
      { path: 'products', name: 'products', component: ProductView, meta: { roles: ['ADMIN'] } },
      { path: 'users', name: 'users', component: UserListView, meta: { roles: ['ADMIN'] } },
      { path: 'user-edit', name: 'user-edit', component: UserEditView, meta: { roles: ['ADMIN'] } },
      { path: 'complaints', name: 'complaints', component: ComplaintMgmtView, meta: { roles: ['ADMIN'] } }, // Thêm route mới

      { path: 'warehouse', name: 'warehouse', component: WarehouseView }, // Needed for import?
      { path: 'warehouse-history', name: 'warehouse-history', component: WarehouseHistoryView }
    ]
  },
  // Redirect từ root đến admin/inventory
  { path: '/', redirect: '/admin/inventory' },
  // Giữ các routes cũ để tương thích ngược
  { path: '/cart', redirect: '/admin/invoice-management' },
  { path: '/preview', redirect: '/admin/invoice-preview' },
  { path: '/statistic', redirect: '/admin/dashboard' },
  { path: '/product', redirect: '/admin/products' },
  { path: '/user_edit', redirect: '/admin/user-edit' },
  { path: '/user_list', redirect: '/admin/users' }

]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // Nếu có savedPosition (khi dùng nút back/forward), quay lại vị trí đó
    if (savedPosition) {
      return savedPosition;
    }
    // Nếu có hash trong URL (ví dụ: #section), scroll đến phần tử đó
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth',
      };
    }
    // Mặc định scroll lên đầu trang với smooth behavior
    return { top: 0, behavior: 'smooth' };
  }
})

// Authentication & Role Guard
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || 'DATT System'
  const userStr = sessionStorage.getItem('user') || localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;
  const isAuthenticatedUser = isAuthenticated();
  if (to.meta.requiresAuth && !isAuthenticatedUser) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return;
  }

  // 2. Check if route requires specific role
  if (to.meta.roles && isAuthenticatedUser) {
    if (!to.meta.roles.includes(user?.role)) {
      // Unauthorized access -> Redirect to safe page based on role
      if (user?.role === 'CUSTOMER') {
        next('/'); // Redirect Customer to Home
      } else {
        next('/admin/dashboard');
      }
      return;
    }
  }



  // 3. Prevent logged-in user from visiting login/register
  if ((to.path === '/login' || to.path === '/register') && isAuthenticatedUser) {
    if (user?.role === 'ADMIN') {
      next('/admin/dashboard');
    } else {
      next('/'); // Redirect Customer to Home
    }
    return;
  }

  // 4. Block Customer from ANY /admin route (even if not explicitly role-guarded)
  if (to.path.startsWith('/admin') && user?.role === 'CUSTOMER') {
    next('/');
    return;
  }

  next()
})

router.afterEach((to) => {
  if (to.path.startsWith('/admin')) {
    prefetchAdminChunks();
  }
});

export default router
