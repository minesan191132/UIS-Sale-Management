const fs = require('fs');
const content = fs.readFileSync('MyOrders.vue', 'utf8');

const newCss = `
/* ===== NEW TABLE DESIGN STYLES ===== */

/* Page Layout */
.myorders-page {
  min-height: 100vh;
  background: #f0f4f8;
}

/* Hero Section */
.orders-hero {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  padding: 3rem 0 2.5rem;
  border-bottom: 4px solid #f59e0b;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.hero-title {
  font-size: 2rem;
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.5px;
}

.hero-desc {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.95rem;
  max-width: 600px;
  margin: 0;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn-hero-primary {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  color: #0f172a;
  font-weight: 700;
  padding: 0.6rem 1.4rem;
  border-radius: 8px;
  border: none;
  display: inline-flex;
  align-items: center;
  text-decoration: none;
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.35);
  transition: all 0.25s ease;
}

.btn-hero-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.5);
  color: #0f172a;
}

/* Content Area */
.orders-content {
  padding-top: 2rem;
  padding-bottom: 3rem;
}

/* Status Filter Pills */
.status-filter-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 15px;
  border: 1.5px solid #cbd5e1;
  background: #ffffff;
  color: #475569;
  font-size: 0.88rem;
  font-weight: 500;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.status-pill:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #eff6ff;
}

.status-pill.active {
  background: #1e3a8a;
  color: white;
  border-color: #1e3a8a;
  box-shadow: 0 2px 8px rgba(30, 58, 138, 0.3);
}

.pill-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 5px;
  border-radius: 999px;
  font-size: 0.72rem;
  font-weight: 700;
  background: rgba(0, 0, 0, 0.12);
  color: currentColor;
}

.status-pill.active .pill-count {
  background: rgba(255, 255, 255, 0.2);
}

/* Orders Table Card */
.orders-table-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.orders-table-header {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-bottom: 2px solid #e2e8f0;
  padding: 0;
}

.col-header {
  padding: 14px 20px;
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

/* Order Table Rows */
.order-table-row {
  display: flex;
  align-items: flex-start;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s ease;
  padding: 0;
}

.order-table-row:last-child {
  border-bottom: none;
}

.order-table-row:hover {
  background: #f8faff;
}

.row-cell {
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* Number Cell */
.order-number-text {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1e3a8a;
  letter-spacing: 0.3px;
}

.order-items-count {
  font-size: 0.8rem;
  color: #94a3b8;
  margin-top: 3px;
}

/* Mini alerts inside table rows */
.mini-alert {
  display: inline-block;
  font-size: 0.75rem;
  padding: 3px 8px;
  border-radius: 6px;
  line-height: 1.4;
}

.mini-alert-warning {
  background: rgba(245, 158, 11, 0.12);
  color: #92400e;
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.mini-alert-danger {
  background: rgba(239, 68, 68, 0.1);
  color: #991b1b;
  border: 1px solid rgba(239, 68, 68, 0.25);
}

.mini-alert-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

/* Status badge in table */
.status-badge-lg {
  padding: 5px 12px;
  font-size: 0.78rem;
  font-weight: 600;
  border-radius: 6px;
  letter-spacing: 0.3px;
}

/* Date Cell */
.date-primary {
  font-size: 0.9rem;
  font-weight: 600;
  color: #334155;
}

.date-secondary {
  font-size: 0.78rem;
  color: #94a3b8;
  margin-top: 2px;
}

.delivery-date {
  font-size: 0.78rem;
  font-weight: 500;
}

/* Price Cell */
.price-total {
  font-size: 1rem;
  font-weight: 700;
  color: #1e3a8a;
}

.price-sub {
  font-size: 0.78rem;
  color: #64748b;
}

/* Action Cell */
.action-cell {
  align-items: flex-start;
}

.action-link-btn {
  display: inline-flex;
  align-items: center;
  font-size: 0.82rem;
  font-weight: 600;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s ease;
  line-height: 1.4;
}

.primary-action { color: #2563eb; }
.primary-action:hover { color: #1e40af; }
.danger-action { color: #dc2626; }
.danger-action:hover { color: #991b1b; }
.success-action { color: #16a34a; }
.success-action:hover { color: #14532d; }
.warning-action { color: #d97706; }
.warning-action:hover { color: #92400e; }

.action-link-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Skeleton loading for table */
.skeleton-row .sk {
  background: #e2e8f0;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
}

.skeleton-row .sk::after {
  content: "";
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.6) 50%, transparent 100%);
  animation: shimmer 1.5s infinite;
}

.sk-title { height: 18px; width: 80%; }
.sk-sub { height: 12px; width: 50%; }
.sk-badge { height: 24px; width: 80px; border-radius: 6px; }
.sk-line { height: 14px; width: 70%; }
.sk-btn { height: 20px; width: 90px; }

/* Empty State Card */
.empty-state-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
  padding: 4rem 2rem;
  text-align: center;
}

.empty-icon {
  font-size: 3.5rem;
  color: #cbd5e1;
  display: block;
  margin-bottom: 1rem;
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #334155;
  margin-bottom: 0.5rem;
}

.empty-desc {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

/* Pagination */
.table-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
  background: #f8fafc;
  flex-wrap: wrap;
  gap: 8px;
}

.pagination-info {
  font-size: 0.84rem;
  color: #64748b;
}

.pagination-controls {
  display: flex;
  gap: 4px;
  align-items: center;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 8px;
  border: 1.5px solid #e2e8f0;
  background: #ffffff;
  color: #475569;
  font-size: 0.88rem;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #eff6ff;
}

.page-btn.active {
  background: #1e3a8a;
  border-color: #1e3a8a;
  color: white;
  box-shadow: 0 2px 8px rgba(30, 58, 138, 0.3);
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.ellipsis-btn {
  border: none;
  background: transparent;
  cursor: default;
}
`;

// Append CSS before </style>
const styleClose = '</style>';
const idx = content.lastIndexOf(styleClose);
console.log('</style> found at index:', idx);

const newContent = content.substring(0, idx) + newCss + '\n' + styleClose;
fs.writeFileSync('MyOrders.vue', newContent, 'utf8');
console.log('Done! New file length:', newContent.length);
