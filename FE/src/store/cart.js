import { reactive, computed } from 'vue';
import { getStoredUser } from '../services/api'; // Import hàm lấy User hiện tại

// Hàm tự động tạo chìa khóa giỏ hàng riêng cho từng người
const getCartKey = () => {
  const user = getStoredUser();
  // Nếu có user, lấy email làm tên giỏ. Nếu không, dùng giỏ khách vãng lai (guest)
  return user && user.email ? `upec_cart_${user.email}` : 'upec_cart_guest';
};

// Khởi tạo giỏ hàng trống
export const cartState = reactive({
  items: []
});

// Hàm nạp giỏ hàng (Sẽ tự động tìm đúng giỏ của người đang đăng nhập)
export const loadCart = () => {
  const savedCart = localStorage.getItem(getCartKey());
  cartState.items = savedCart ? JSON.parse(savedCart) : [];
};

// Chạy hàm nạp ngay khi load web
loadCart();

// Hàm lưu dữ liệu vào đúng giỏ
const saveCart = () => {
  localStorage.setItem(getCartKey(), JSON.stringify(cartState.items));
};

// Hàm thêm vào giỏ
export const addToCart = (product, quantity) => {
  const existingItem = cartState.items.find(item => item.id === product.id);
  if (existingItem) {
    existingItem.quantity += parseInt(quantity);
  } else {
    cartState.items.push({ ...product, quantity: parseInt(quantity) });
  }
  saveCart();
};

// Hàm xóa khỏi giỏ
export const removeFromCart = (productId) => {
  cartState.items = cartState.items.filter(item => item.id !== productId);
  saveCart();
};

// Đếm tổng số lượng (Để hiển thị trên Navbar)
export const cartItemCount = computed(() => {
  return cartState.items.reduce((total, item) => total + parseInt(item.quantity), 0);
});

// Tính tổng tiền
export const cartTotalPrice = computed(() => {
  return cartState.items.reduce((total, item) => total + (item.price * parseInt(item.quantity)), 0);
});