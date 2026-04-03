-- V14: Seed bảng products với dữ liệu chi tiết Mác Vật Liệu (Phôi lẻ & Tiêu chuẩn)

INSERT INTO products (sku, name, default_material, stock_quantity, price, image_url, description, is_active) VALUES

-- Nhóm Thép Carbon
('MAT-SS400-01', 'Phôi Thép Tấm Kết Cấu SS400 / CT3', 'Thép SS400', 50, 45000, '/images/SS400-CT3-thep-tam.jpg', 'Thuộc khối thép Carbon: Thép tấm, thép hình kết cấu chung. Có đặc tính mềm, rẻ, dễ hàn cắt. Rất hay có phôi lẻ rớt lại từ các bản mã dự án.', true),
('MAT-C45-02', 'Phôi Thép Tròn Đặc C45 / S45C (Phi 60)', 'Thép C45', 30, 85000, '/images/C45-S45C-thep-carbon.png', 'Thuộc khối thép chế tạo máy: Thép carbon cường độ trung bình. Chuyên dùng để đưa vào máy tiện CNC tiện trục máy, bánh răng tải trọng nhẹ.', true),

-- Nhóm Thép Công Cụ (Làm khuôn)
('MAT-SKD11-03', 'Phôi Thép Làm Khuôn Dập Nguội SKD11 / D2', 'Thép SKD11', 5, 250000, '/images/SKD11-D2-thep-khuon-nhapnguoi.png', 'Hàng giá trị cao thuộc khối thép công cụ: Thép làm khuôn dập nguội. Cực kỳ cứng, phay tiện xong phải đem đi nhiệt luyện. Phôi dư của loại này bán rất được giá.', true),
('MAT-SKD61-04', 'Phôi Thép Làm Khuôn Dập Nóng SKD61 / H13', 'Thép SKD61', 8, 320000, '/images/SKD61-H13-thep-khuondapnong.jpg', 'Hàng giá trị cao thuộc khối thép công cụ: Thép làm khuôn dập nóng, khuôn đúc áp lực. Chịu được nhiệt độ cao cực tốt.', true),
('MAT-S50C-05', 'Thép Làm Đế Khuôn S50C', 'Thép S50C', 12, 110000, '/images/S50C-thep-dekhuon.jpg', 'Thuộc khối thép công cụ: Chuyên dùng làm áo khuôn, đế khuôn nhựa hoặc khuôn dập nhờ độ cứng và khả năng gia công ổn định.', true),

-- Nhóm Inox / Thép Không Gỉ
('MAT-SUS304-06', 'Phôi Inox Quốc Dân SUS 304', 'Inox SUS304', 15, 120000, '/images/SUS304-inox-quocdan.png', 'Thuộc khối Inox / Thép không gỉ: Đây là loại Inox quốc dân. Chống gỉ tốt, được UPEC sử dụng nhiều để làm bồn bể, đường ống xử lý nước thải.', true),
('MAT-SUS316-07', 'Phôi Inox Cao Cấp SUS 316 / 316L', 'Inox SUS316', 10, 195000, '/images/SUS316-inox-caocap.png', 'Thuộc khối Inox / Thép không gỉ: Dòng Inox cao cấp chuyên dụng. Chịu được axit, hóa chất, nước biển mặn. Thường dùng trong y tế hoặc thực phẩm.', true),
('MAT-SUS201-08', 'Phôi Inox Giá Rẻ SUS 201', 'Inox SUS201', 20, 65000, '/images/SUS201-inox-giare.avif', 'Thuộc khối Inox / Thép không gỉ: Dòng Inox giá rẻ. Chỉ dùng làm khung bệ trong nhà vì dễ bị gỉ sét hơn 304 nếu để ngoài trời.', true),
('MAT-SUS430-09', 'Tấm Inox Hít Từ Tính SUS 430', 'Inox SUS430', 25, 55000, '/images/SUS430-inox-hittutinh.webp', 'Thuộc khối Inox / Thép không gỉ: Dòng Inox đặc biệt có tính từ (hít nam châm). Ít dùng trong gia công hạng nặng, chủ yếu dập vỏ máy.', true),

-- Nhóm Hàng Tiêu Chuẩn (Thành phẩm gia công sẵn)
('FLG-SUS304-10', 'Mặt Bích Inox SUS304 (Chuẩn JIS 10K)', 'Inox SUS304', 100, 150000, '/images/SUS304-inox-matbich.jpg', 'Nhóm kết nối đường ống (Mảng Xử lý nước): Mặt bích mù (Blind Flange) Inox 304 chuẩn công nghiệp JIS 10K.', true),
('GEAR-S45C-11', 'Bánh Răng Thẳng M2.0 (Spur Gear)', 'Thép C45', 200, 185000, '/images/GEAR-S45C-tieuchuan.avif', 'Nhóm chi tiết truyền động: Bánh răng thẳng mô-đun M2.0, gia công phay lăn răng chính xác từ thép C45.', true);