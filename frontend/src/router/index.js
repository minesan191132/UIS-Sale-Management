import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../services/api'

import HomeView from '../components/homepage/Homepage.vue'
import ServiceView from '../components/services/Service.vue'
import Product from '../components/product/Product.vue'
import Contact from '../components/contact/Contact.vue'
import Cart from '../components/cart/Cart.vue'
import Login from '../components/auth/Login.vue'
import Register from '../components/auth/Register.vue'
import CreateOrder from '../components/customer/CreateOrder.vue'
import MyOrders from '../components/customer/MyOrders.vue'

import AdminLayout from '../layouts/AdminLayout.vue'

import InventoryView from '../components/admin/home.vue'
import InvoiceMgmtView from '../components/admin/ExportManagement.vue'
import PreviewInvoiceView from '../components/admin/preview.vue'
import StatisticView from '../components/admin/thongke.vue'
import WarehouseView from '../components/admin/nhapkho.vue'
import WarehouseHistoryView from '../components/admin/lichsunhapkho.vue'
import ProductView from '../components/admin/product.vue'
import UserEditView from '../components/admin/user_edit.vue'
import UserListView from '../components/admin/user_list.vue'
import OrderManagementView from '../components/admin/OrderManagement.vue'


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
    path: '/products',
    name: 'product',
    component: Product
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
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true }, // Protect all admin routes
    children: [
      { path: 'dashboard', name: 'dashboard', component: StatisticView, meta: { roles: ['ADMIN'] } },
      { path: 'inventory', name: 'inventory', component: InventoryView }, // All authenticated users
      { path: 'invoice-management', name: 'invoice-management', component: InvoiceMgmtView },
      { path: 'invoice-preview', name: 'invoice-preview', component: PreviewInvoiceView },
      { path: 'products', name: 'products', component: ProductView, meta: { roles: ['ADMIN'] } },
      { path: 'users', name: 'users', component: UserListView, meta: { roles: ['ADMIN'] } },
      { path: 'user-edit', name: 'user-edit', component: UserEditView, meta: { roles: ['ADMIN'] } },
      { path: 'orders', name: 'orders', component: OrderManagementView, meta: { roles: ['ADMIN'] } },
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
  routes
})

// Authentication & Role Guard
router.beforeEach((to, from, next) => {
  // Update page title
  document.title = to.meta.title || 'DATT System'

  const user = JSON.parse(localStorage.getItem('user'));
  const isAuthenticatedUser = isAuthenticated();

  // 1. Check if route requires authentication
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

export default router
