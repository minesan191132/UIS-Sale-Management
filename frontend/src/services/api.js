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
    const token = localStorage.getItem('authToken');
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
    if (error.response?.status === 401 || error.response?.status === 403) {
      // Token expired or invalid - clear storage and redirect to login
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
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
    const response = await axios.post(`${API_BASE_URL}/auth/register`, data);
    return response.data;
  },

  /**
   * Login user
   */
  login: async (email, password) => {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, {
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
 * Save auth token and user info to localStorage
 */
export const saveAuthData = (authResponse) => {
  localStorage.setItem('authToken', authResponse.token);
  localStorage.setItem('user', JSON.stringify({
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
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

/**
 * Check if user is authenticated
 */
export const isAuthenticated = () => {
  return !!localStorage.getItem('authToken');
};

/**
 * Logout - clear all auth data
 */
export const logout = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  window.location.href = '/login';
};

export default apiClient;
