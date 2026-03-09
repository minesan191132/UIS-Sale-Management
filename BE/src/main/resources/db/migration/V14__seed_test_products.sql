-- V13: Seed bảng products với dữ liệu ngành Cơ khí, Gia công (Sắt, Thép, Inox)
-- Gồm 8 sản phẩm (sẽ chia làm 2 trang trên Web)

INSERT INTO products (sku, name, default_material, stock_quantity, price, is_active) VALUES
('FLG-SS400-01', 'Mặt bích thép tiêu chuẩn JIS 10K', 'Thép SS400', 50, 120000, true),
('SFT-304-02', 'Trục trơn gia công tiện CNC (D20x500)', 'Inox SUS304', 30, 450000, true),
('GER-C45-03', 'Bánh răng thẳng M2.5 20 răng', 'Thép C45', 100, 250000, true),
('PLT-FE-04', 'Bản mã cắt Laser (10x100x100mm)', 'Sắt đen', 200, 45000, true),
('BSH-BRZ-05', 'Ống lót đồng thau đúc nguyên khối', 'Đồng thau', 0, 180000, true), -- Tồn kho 0 sẽ tự hiện "Hết hàng"
('CPL-AL-06', 'Khớp nối trục đàn hồi', 'Nhôm AL6061', 25, 320000, true),
('LSW-316-07', 'Trục vít me bước 4mm (Sang trang 2)', 'Inox SUS316', 15, 850000, true),
('PIN-SKD-08', 'Chốt định vị xử lý nhiệt (Sang trang 2)', 'Thép SKD11', 80, 95000, true);