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
   * Update user profile
   */
  updateProfile: async (data) => {
    const response = await apiClient.put('/auth/profile', data);
    return response.data;
  },

  /**
   * Change password
   */
  changePassword: async (data) => {
    const response = await apiClient.put('/auth/change-password', data);
    return response.data;
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
