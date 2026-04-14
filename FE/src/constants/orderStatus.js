export const UNKNOWN_STATUS_LABEL = 'Không xác định'

export const ORDER_STATUS_LABELS = Object.freeze({
  PENDING_APPROVAL: 'Chờ duyệt đơn',
  PENDING_QUOTE: 'Chờ báo giá',
  AWAITING_CONTRACT: 'Chờ xác nhận hợp đồng',
  AWAITING_PAYMENT: 'Chờ thanh toán',
  DEPOSITED: 'Đã cọc',
  PROCESSING: 'Đang gia công',
  AWAITING_REMAINING_PAYMENT: 'Chờ thanh toán đợt 2',
  AWAITING_DELIVERY: 'Chờ giao hàng',
  SHIPPING: 'Đang giao hàng',
  COMPLETED: 'Hoàn thành',
  CANCELLED: 'Đã hủy',
})

export const REVIEW_STATUS_LABELS = Object.freeze({
  PENDING_REVIEW: 'Chờ đánh giá',
  APPROVED: 'Đã duyệt',
  REJECTED: 'Từ chối',
  NEED_DISCUSSION: 'Cần trao đổi',
})

export const getOrderStatusLabel = (status, overrides = null) => {
  if (!status) return UNKNOWN_STATUS_LABEL

  if (overrides && overrides[status]) {
    return overrides[status]
  }

  return ORDER_STATUS_LABELS[status] || UNKNOWN_STATUS_LABEL
}

export const getReviewStatusLabel = (status) => {
  if (!status) return UNKNOWN_STATUS_LABEL
  return REVIEW_STATUS_LABELS[status] || UNKNOWN_STATUS_LABEL
}
