import axios from 'axios';

// API Base URL - Update for production
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add JWT token to all requests
apiClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('authToken') || localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle 401/403 errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const isLoginRequest = error.config && error.config.url && error.config.url.includes('/auth/login');
    if ((error.response?.status === 401 || error.response?.status === 403) && !isLoginRequest) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ==================== AUTH APIs ====================

export const authAPI = {
  /**
   * Register new user with company
   */
  register: async (data) => {
    return await apiClient.post('/auth/register', data);
  },

  /**
   * Login user
   */
  login: async (email, password) => {
    const response = await apiClient.post(`${API_BASE_URL}/auth/login`, {
      email,
      password,
    });
    return response.data;
  },

  /**
   * Get current user info
   */
  getCurrentUser: async () => {
    const response = await apiClient.get('/auth/me');
    return response.data;
  },

  /**
     * Verify email via token
     */
  verifyEmail: async (token) => {
    return await apiClient.get(`/auth/verify/${token}`);
  },

  /**
   * Resend verification email
   */
  resendVerification: async (data) => {
    return await apiClient.post('/auth/resend-verification', data);
  },

  /**
   * Step 1 - Forgot password: send OTP to email
   */
  forgotPassword: async (email) => {
    return await apiClient.post('/auth/forgot-password', { email });
  },

  /**
   * Step 2 - Verify OTP: returns reset token
   */
  verifyOtp: async (email, otp) => {
    return await apiClient.post('/auth/forgot-password/verify', { email, otp });
  },

  /**
   * Step 3 - Reset password using reset token
   */
  resetPassword: async (resetToken, newPassword) => {
    return await apiClient.post('/auth/reset-password', { resetToken, newPassword });
  },
};

// ==================== USER APIs ====================
// Endpoints for authenticated user: profile, password, addresses

export const userAPI = {
  /**
   * Get current user's full profile
   */
  getProfile: async () => {
    const response = await apiClient.get('/user/profile');
    return response.data;
  },

  /**
   * Update user profile (fullName, phone, gender, dob)
   */
  updateProfile: async (data) => {
    const response = await apiClient.put('/user/profile', data);
    return response.data;
  },

  /**
   * Change user password
   */
  changePassword: async (data) => {
    const response = await apiClient.put('/user/change-password', data);
    return response.data;
  },

  /**
   * Get all delivery addresses
   */
  getAddresses: async () => {
    const response = await apiClient.get('/user/addresses');
    return response.data;
  },

  /**
   * Add a new address
   */
  addAddress: async (data) => {
    const response = await apiClient.post('/user/addresses', data);
    return response.data;
  },

  /**
   * Update an existing address
   */
  updateAddress: async (id, data) => {
    const response = await apiClient.put(`/user/addresses/${id}`, data);
    return response.data;
  },

  /**
   * Delete an address
   */
  deleteAddress: async (id) => {
    await apiClient.delete(`/user/addresses/${id}`);
  },

  /**
   * Set an address as default
   */
  setDefaultAddress: async (id) => {
    const response = await apiClient.put(`/user/addresses/${id}/default`);
    return response.data;
  },
};

// ==================== MATERIALS APIs ====================

export const materialsAPI = {
  /**
   * Get materials dashboard with pagination and search
   */
  getDashboard: async (page = 0, size = 15, search = '', companyId = null) => {
    const params = { page, size, search };
    if (companyId !== null) {
      params.companyId = companyId;
    }

    const response = await apiClient.get('/materials/dashboard', { params });
    return response.data;
  },

  /**
   * Import Excel file
   */
  importExcel: async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post('/materials/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },
};

// ==================== COMPANIES APIs ====================

export const companiesAPI = {
  /**
   * Get all companies (for dropdown)
   */
  getAll: async () => {
    const response = await apiClient.get('/companies');
    return response.data.content || response.data;
  },

  /**
   * Search companies
   */
  search: async (keyword = '', page = 0, size = 10) => {
    const response = await apiClient.get('/companies', {
      params: { keyword, page, size }
    });
    return response.data;
  },
};

// ==================== ADMIN USER APIs ====================

export const usersAPI = {
  /**
   * Get all users with optional search/role/company filter and pagination
   */
  getAll: async (search = '', role = '', page = 0, size = 20, companySearch = '') => {
    const params = { page, size };
    if (search) params.search = search;
    if (role && role !== 'all') params.role = role;
    if (companySearch) params.companySearch = companySearch;
    const response = await apiClient.get('/admin/users', { params });
    return response.data;
  },

  /**
   * Update user role
   */
  updateRole: async (userId, role) => {
    const response = await apiClient.put(`/admin/users/${userId}/role`, { role });
    return response.data;
  },

  /**
   * Update user info (phone, fullName, role, password)
   */
  updateUser: async (userId, data) => {
    const response = await apiClient.patch(`/admin/users/${userId}`, data);
    return response.data;
  },

  /**
   * Toggle user active/inactive status
   */
  toggleActive: async (userId) => {
    const response = await apiClient.put(`/admin/users/${userId}/toggle-active`);
    return response.data;
  },
};

// ==================== CONTACT API ====================

export const contactAPI = {
  /**
   * Gửi yêu cầu liên hệ qua email
   */
  send: async (data) => {
    const response = await apiClient.post('/contact/send', data);
    return response.data;
  },
};

// ==================== ADMIN STATS API ====================

export const statsAPI = {
  getDashboard: async (period = 'THIS_MONTH') => {
    const response = await apiClient.get('/admin/stats/dashboard', { params: { period } });
    return response.data;
  },
  getMonthlyRevenue: async (year) => {
    const response = await apiClient.get('/admin/stats/monthly-revenue', { params: { year } });
    return response.data;
  },
};

// ==================== ORDERS API ====================

export const ordersAPI = {
  /**
   * Create order from shopping cart
   */
  createFromCart: async (data) => {
    const response = await apiClient.post('/orders/from-cart', data);
    return response.data;
  },

  /**
   * Customer confirms they have received the shipment
   */
  confirmReceived: async (orderId) => {
    const response = await apiClient.put(`/orders/${orderId}/confirm-received`);
    return response.data;
  },

  /**
   * Get current user's complaint for one order
   */
  getMyComplaint: async (orderId) => {
    const response = await apiClient.get(`/orders/${orderId}/complaint/my`);
    return response.data;
  },

  /**
   * Create or update complaint (multipart)
   */
  upsertMyComplaint: async (orderId, formData) => {
    const response = await apiClient.post(`/orders/${orderId}/complaint/my`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },
};

// ==================== PAYMENT API ====================

export const paymentAPI = {
  /**
   * Get payment info + QR URL for an order
   */
  getPaymentInfo: async (orderId) => {
    const response = await apiClient.get(`/payments/orders/${orderId}/qr`);
    return response.data;
  },
};

// ==================== NOTIFICATION APIs ====================

export const notificationsAPI = {
  getMy: async () => {
    const response = await apiClient.get('/notifications/my');
    return response.data;
  },

  getUnreadCount: async () => {
    const response = await apiClient.get('/notifications/my/unread-count');
    return response.data;
  },

  markRead: async (id) => {
    const response = await apiClient.put(`/notifications/${id}/read`);
    return response.data;
  },

  markAllRead: async () => {
    const response = await apiClient.put('/notifications/read-all');
    return response.data;
  },

  cleanupOld: async (days = 90) => {
    const response = await apiClient.delete('/notifications/cleanup-old', {
      params: { days },
    });
    return response.data;
  },
};

// ==================== PRODUCT ADMIN APIs ====================

export const productAdminAPI = {
  /**
   * Thống kê tổng quan sản phẩm (4 card)
   */
  getStats: async () => {
    const response = await apiClient.get('/productadmin/stats');
    return response.data;
  },

  /**
   * Danh sách sản phẩm (phân trang, tìm kiếm, lọc)
   */
  getAll: async ({ keyword = '', categoryId = null, status = '', page = 0, size = 20 } = {}) => {
    const params = { keyword, status, page, size };
    if (categoryId) params.categoryId = categoryId;
    const response = await apiClient.get('/productadmin', { params });
    return response.data;
  },

  /**
   * Chi tiết 1 sản phẩm
   */
  getById: async (id) => {
    const response = await apiClient.get(`/productadmin/${id}`);
    return response.data;
  },

  /**
   * Thêm sản phẩm mới
   */
  create: async (data) => {
    const response = await apiClient.post('/productadmin', data);
    return response.data;
  },

  /**
   * Cập nhật sản phẩm
   */
  update: async (id, data) => {
    const response = await apiClient.put(`/productadmin/${id}`, data);
    return response.data;
  },

  /**
   * Xoá sản phẩm (soft delete)
   */
  delete: async (id) => {
    const response = await apiClient.delete(`/productadmin/${id}`);
    return response.data;
  },
};

// ==================== HELPERS ====================


/**
 * Save auth token and user info
 * @param {Object} authResponse
 * @param {boolean} rememberMe 
 */
export const saveAuthData = (authResponse, rememberMe = false) => {
  const storage = rememberMe ? localStorage : sessionStorage;
  storage.setItem('authToken', authResponse.token);
  storage.setItem('user', JSON.stringify({
    userId: authResponse.userId,
    email: authResponse.email,
    fullName: authResponse.fullName,
    companyId: authResponse.companyId,
    companyName: authResponse.companyName,
    role: authResponse.role || 'CUSTOMER',

    taxCode: authResponse.taxCode || '', 
    companyPhone: authResponse.companyPhone || authResponse.phone || '', 
    companyEmail: authResponse.companyEmail || '',
  }));
};

/**
 * Get stored user info
 */
export const getStoredUser = () => {
  const userStr = sessionStorage.getItem('user') || localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

/**
 * Check if user is authenticated
 */
export const isAuthenticated = () => {
  return !!(sessionStorage.getItem('authToken') || localStorage.getItem('authToken'));
};

/**
 * Logout - clear all auth data
 */
export const logout = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  sessionStorage.removeItem('authToken');
  sessionStorage.removeItem('user');
  window.location.href = '/login';
};

export default apiClient;
