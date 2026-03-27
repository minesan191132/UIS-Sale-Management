# Backend Notes

## Excel Import Header Format

The Excel import flow in the order module reads all business columns by header name, not by fixed column index.

### Required business headers

All fields below are required.
STT is optional and ignored for business mapping.

- VNN_NO
- Item Code
- Drawing Number
- Part Name
- Specification
- Material
- Quantity
- Delivery Date

### Supported header aliases

Each logical field can be matched by common aliases (JP/EN/VI).

- VNN_NO: `vnn_no`, `vnn no`, `vnn`, `受注番号`, `注文番号`, `order no`, `order number`
- Item Code: `item code`, `itemcode`, `品目コード`, `品目cd`, `code`, `ma hang`, `mã hàng`
- Drawing Number: `drawing number`, `drawing no`, `drawing`, `図番`, `ban ve`, `bản vẽ`
- Part Name: `part name`, `item name`, `品名`, `ten chi tiet`, `tên chi tiết`, `ten hang`, `tên hàng`
- Specification: `specification`, `spec`, `型式`, `quy cach`, `quy cách`
- Material: `material`, `material type`, `材質`, `chat lieu`, `chất liệu`
- Quantity: `quantity`, `qty`, `数量`, `so luong`, `số lượng`
- Delivery Date: `希望納期`, `出荷日`, `納期`, `delivery date`, `delivery`, `due date`, `ngay xuat`, `ngày xuất`

### Date handling

Delivery Date supports both string values and Excel numeric date cells.
Parsed value is normalized to ISO date format (`yyyy-MM-dd`).

### Validation behavior

- Missing required headers: request fails with HTTP 400 and type `MISSING_HEADERS`.
- No valid rows after parsing: request fails with HTTP 400 and type `NO_VALID_ROWS`.
- Other import validation errors: request fails with HTTP 400 and type `VALIDATION_ERROR`.

### Endpoints

- Customer import: `POST /api/orders/upload`
- Admin import: `POST /api/orders/admin-import`
