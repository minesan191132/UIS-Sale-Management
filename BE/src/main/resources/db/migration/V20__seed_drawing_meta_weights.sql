-- V4 seed drawing weight data from Kho App reference
-- Inserts weight data into drawing_meta table

-- Seed drawing records from master Excel (UTSU company)
-- Generated from: 01. Mass UTSU- KIV.xlsx

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0023', 16.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0001A', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0002A', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0003A', 4.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0004', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0004A', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0005', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0005A', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0006', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0006A', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0007A', 5.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0008A', 5.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0009A', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0010A', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0011A', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0012A', 6.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0013A', 7.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0014A', 7.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0015A', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0016A', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0017A', 8.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0018A', 8.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0022', 0.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0023', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0024', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0025', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0026', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0027', 0.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0028', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0029', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0036', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0037', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0047', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0049', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0065', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0090', 2.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0091', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0093', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0094', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0096', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0097', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0098', 0.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0200', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0201', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0202', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2001A', 8.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2003', 7.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2005', 11.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2005A', 11.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2006', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2007', 3.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2008', 8.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2012', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2017', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2053', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2061', 9.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2061A', 9.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2064', 6.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2067', 6.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2092', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2093', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2094', 6.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2095', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2110', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4001A', 9.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4002', 8.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4003', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4005', 12.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4006', 4.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4007', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4008', 8.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4012', 3.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4017', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4061', 10.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4070', 10.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4092', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4093', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX4094', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6001A', 9.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6002', 9.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6003', 8.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6005', 13.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6005A', 13.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6006', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6007', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6008', 9.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6012', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6017', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6053', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6054', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6061A', 11.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6064', 8.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6067', 9.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6070', 10.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6076', 8.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6079', 9.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6090', 8.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6092', 5.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6093', 5.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6094', 7.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6095', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX6110', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('85AF0262', 10.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ABAAA0000A', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ABAAB0000', 1.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AEBAB0020', 15.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AEBAC0020', 6.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AEBAE0001', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AEBAE2000', 2.35, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.35, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJBAA0016', 14.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJBAA0018', 14.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJBAA0020', 14.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJBAA0022', 15.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJBAE0014', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJBEA0014A', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJCAA0016', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJCAA0018', 5.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJCAA0020', 6.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJCAA0022', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0001', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0002', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0003', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0090', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0091', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0092', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AJDAA0093', 0.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AKBBA0010', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AKBBA0012', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AKBBA0015', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ALBAB0000', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ALBAB0018', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ALBAB0020', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ALBAB0022', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AUGAA2224', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AUGBA2224', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AVAAF0007', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AVAAF0009', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AVAAG0007', 3.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AVAAG0009', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAG0018', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAG0018A', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAG0020', 10.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAG0022', 11.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAH0014', 7.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAH0016', 11.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYAAH0020A', 13.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAA0010A', 8.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAA0012A', 11.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAA0015', 17.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAA0015A', 14.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAB0012A', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAB0015', 17.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAB0015A', 14.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAC0008', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAC0015', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAC0015A', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAE0008B', 7.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAF0010', 11.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAF0010A', 9.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAF0012', 12.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAG0008', 7.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAJ0012', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAC0015B', 21.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 21.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAD0020B', 28.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 28.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAE0012', 25.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAE0015B', 27.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAE0020B', 31.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 31.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAE0025C', 42.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 42.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAE0030A', 45.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 45.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAF0000A', 10.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAF0025', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYDAF0030A', 3.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYESL0001', 7.53, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.53, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYESL0002', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYESL0003', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYESL0004', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYESL0005', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAC0030', 5.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAC0032', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAC0034', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAC0036', 7.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAD0000', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAL1015', 18.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAL1018', 20.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAL2015', 18.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAL2018', 20.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0016', 11.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0018', 12.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0020', 13.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0022', 14.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0122', 12.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0222', 14.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN0322', 12.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN2041', 13.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN2041A', 13.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAR0029', 9.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAR0041A', 10.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAT0038A', 10.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAT0041A', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBA1027', 26.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBA1031', 28.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 28.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBA2027', 26.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBA2031', 28.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 28.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBA2033', 29.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBB1027', 25.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBB2027', 25.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0022', 19.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0025', 21.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 21.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0027', 22.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0031', 24.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0033', 25.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0036', 35.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 35.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0038', 36.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 36.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCA0041', 38.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 38.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0022', 18.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0025', 20.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0027', 21.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 21.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0028', 22.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0033', 27.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0038', 33.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGCB0041', 34.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 34.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDA0016A', 9.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDA0018A', 10.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDA0020A', 11.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDA0022A', 12.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDB0016', 7.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDB0018', 8.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDB0020', 10.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYKDB0022', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAB0036', 19.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAC0036', 10.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAD0036', 19.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABA1000', 8.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABA2000', 8.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABE0000', 1.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABF1036', 19.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABF2036', 19.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABG1000', 8.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFABG2000', 8.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBN0001B', 4.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBN0002B', 4.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBN0003B', 5.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBN0004B', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBN0021', 2.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBP0009A', 14.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBP0010C', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBP0011C', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGBP0031', 6.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGCN0013B', 26.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGCN0021C', 22.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGCN0027B', 26.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGCN0029A', 9.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGCP0012A', 19.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0001C', 6.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0004C', 7.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0006C', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0007B', 8.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0010C', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0013C', 5.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0016C', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGDN0019C', 5.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0004', 0.57, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.57, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0006', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0006A', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0007', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0007A', 0.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0008', 0.87, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.87, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0008A', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0009', 0.95, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.95, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0010', 1.02, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.02, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0010A', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0011', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0011A', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0012', 1.17, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.17, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0012A', 1.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0013', 1.25, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.25, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0013A', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGGN0002', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGJP0001', 10.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGJP0002', 12.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGJP0003', 13.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGJP0004', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGLN0005A', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGLN0006A', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGLN0007A', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGLN0008A', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGNN0004', 9.67, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.67, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHCP0002C', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHCP0007', 0.23, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.23, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHCP0112', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHCP0113', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHCP0114', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHDP0001A', 0.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHDP0004A', 0.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHFP0001', 4.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHGP0002', 0.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHGP0003', 1.15, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.15, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHGP0004', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHGP0004A', 1.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHGP0013A', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BMAAA0020', 213.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 213.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BMBAA0020', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BMBAC0000A', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BMCAA0020', 20.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BNAAA1020A', 16.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BNAAA2020A', 16.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAA0017', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAA0038', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAA0041', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAB0020', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAB0022', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAC0010', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAC0012', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAD0005', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAD0010', 0.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAD0012', 0.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAE0020', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAE0022A', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAG0010', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CBCAG0012', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAB0018', 23.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 23.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAB0020A', 25.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAB0022A', 26.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAC0018', 5.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAC0020', 6.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAC0022', 6.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAD1024', 26.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAD1026', 27.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAD2024', 26.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CCAAD2026', 27.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAC0032', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAC0034', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAC0036', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAD0001A', 2.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAD0001B', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAD0002A', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAD0002B', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAE0032A', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAE0032B', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAE0034B', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAE0036A', 4.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDAAE0036B', 3.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDKW0009', 0.43, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.43, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDKW0015', 1.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CFAAA2020', 4.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CFAAB2020', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CHAAE0000', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CJAAA0010', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CJAAA0012', 10.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CJAAA0015', 13.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('DAAAB0000', 26.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('FBCN0001A', 27.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('FHDP0001', 0.19, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.19, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('J1G302', 0.12, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.12, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('J3P200', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('J5N050A', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JBBP0002B', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JBBP0003B', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JBBP0004B', 3.41, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.41, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JDCP0009', 10.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JDCP0010', 12.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JDCP0012', 11.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JDCP0013', 13.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JDCP0015', 12.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JDCP0016', 13.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0001B', 7.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0002B', 7.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0004A', 12.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0005B', 16.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0007B', 7.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0009B', 17.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0012A', 16.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0013B', 20.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0015A', 13.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0016A', 24.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0017C', 29.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0020A', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('JWBN0021', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAE0000A', 10.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAF0000', 3.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAG0000', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAH0016A', 7.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAH0018A', 7.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAH0020A', 8.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAH0022A', 9.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAJ0010B', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAJ0015B', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAK0014', 8.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAK0016', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAK0018', 9.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAM0014B', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAM0016B', 7.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAM0018B', 8.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAN0000', 10.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAP0000', 3.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAQ0010', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAQ0012', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAS0014C', 15.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAS0016C', 16.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAS0018C', 17.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAS0020A', 18.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAS0022A', 19.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAT0014A', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAT0020A', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAT0022A', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('L1D200', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('L1D201', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('L1D300', 5.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('L1D418', 6.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LDAJ0000', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA1015C', 18.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA1018B', 20.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA1020B', 21.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 21.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA1022C', 22.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA2015C', 18.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA2018B', 20.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAA2020B', 21.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 21.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAB0022A', 7.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAD0014', 3.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAE0014', 4.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAE0016', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAE0018', 5.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAE0020', 6.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAE0022', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAF0014', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAF0016', 7.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAF0018', 8.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAF0020', 9.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAF0022', 10.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAN0014A', 4.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAA0014', 7.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAA0016', 8.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAA0018', 9.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAB0014', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAC0005', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAC0007', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAC0010', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAC0012A', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAD0000', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAG0000C', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAK0000C', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAL0000', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAL0010', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAL0018', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAL0021', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAL0022', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAL0025', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0018', 10.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0020', 11.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0022', 12.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0023', 12.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0025A', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0026', 13.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0036', 23.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 23.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0038', 24.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0041', 26.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAT0000', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAZ0016', 0.87, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.87, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAZ0020', 1.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LHAE2516', 7.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LHAE3318', 13.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LHAE3320', 14.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LHAE3322', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MABN0010', 0.25, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.25, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MABN0012', 0.29, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.29, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MABN0015', 0.33, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.33, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAC0010', 9.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAC0012', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAE0010A', 9.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAE0012A', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAJ0000', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAK0016', 3.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAK0018', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAK0020', 5.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAK0022', 6.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAL0016', 8.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAL0018', 9.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAL0020', 10.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAL0022', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAP0016', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAP0018', 10.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAP0020', 11.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAP0022', 11.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('NCAC0018', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('NCAC0022', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('NCAC0025', 3.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('NCAC0027', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('NCAC0040', 5.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('NCAC0045', 5.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PAAC1020', 58.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 58.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAC0018B', 48.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 48.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAC0020A', 50.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 50.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAC0020C', 52.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 52.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAC1014', 34.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 34.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAC1016', 36.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 36.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAD0000', 6.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAD0018', 10.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAQ0055A', 11.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAQ1016', 11.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAQ1022', 11.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAQ1055', 11.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PCAC0012A', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PCAC0014C', 2.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PCAC0016C', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PCAC0018C', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PCAC0020D', 5.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PEAD0000', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PFAE0000A', 0.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PFAG0042', 0.15, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.15, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAAAG000', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAAAH032B', 15.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAAAJ018', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAB027A', 5.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAC018', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAD018A', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAE032', 4.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAHAA016', 29.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAHAB014', 5.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAKAA000', 0.05, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.05, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAA032', 10.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAB018', 7.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAD018', 6.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAE018', 5.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAF014', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABKAB010', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCADAA127A', 19.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCADAA227A', 19.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCAGAH025', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCAGAK022', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCAGAK025', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCAKAC018', 5.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCBAAM000A', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCBJAB010', 10.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RAAA1032', 27.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RAAA2032', 27.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RAAG0032', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAA0014', 2.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAA0016', 3.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAA0018', 3.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAA0020', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAA0022', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAB0000', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAC0000A', 2.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAD0014', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAD0016', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAD0018', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAD0020', 4.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAD0022', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAE0016', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAE0018', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAE0022', 2.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAF0005B', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAF0008A', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAF0010A', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAF0012A', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAH0000B', 0.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RLAH0000C', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAA0014A', 11.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAA0018A', 14.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAA1128B', 15.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0019A', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0023A', 16.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0027A', 17.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0028A', 18.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0030A', 18.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0032A', 19.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0033A', 19.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0036', 29.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0038', 31.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 31.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0041', 33.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAC0024', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAD0025A', 16.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAD0030A', 18.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAH2022', 12.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAJ0000', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAK0002', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAK0003', 2.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAK0004', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAK0005', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAK0006', 4.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAL0002', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAL0003', 1.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAL0004', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAL0005', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAL0006', 3.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RQAA0000', 1.65, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.65, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RQAB0016A', 12.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RQAB0018A', 9.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RQAB0022B', 11.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RSFA0020', 16.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RTAE0001C', 3.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RVAE0016', 5.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('SCZBA020', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('SMCZAZ045', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADCA005', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADCA006', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADDA017A', 3.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADEA014', 6.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADEA015', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADEA016', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TADZA122', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFDA169', 10.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFDA237', 9.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFDA238', 24.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFDA239', 24.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFEA275', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFZA562', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFZA563', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFZA564', 6.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAFZA565', 6.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRN783A', 7.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRN784A', 7.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRN785A', 11.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA128', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA197', 4.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA198', 4.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA199', 16.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 16.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA200', 19.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA201', 6.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA202', 5.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA203', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA672', 5.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA934', 5.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA935', 15.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA936', 6.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA937', 10.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGRNA938', 7.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSA730', 6.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSA904', 10.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB724', 36.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 36.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB725', 45.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 45.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB726', 51.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 51.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB727', 12.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB735', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB736', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB737', 12.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB746', 13.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB800', 7.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB801', 7.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB802', 6.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB803', 6.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB804', 33.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB805', 40.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 40.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB806', 49.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 49.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB807', 12.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB808', 12.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAGZSB809', 12.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA145', 5.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA309', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA387', 6.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA834', 2.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA835', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA845', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRA846', 1.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRN405A', 22.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRN946A', 23.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 23.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA067A', 24.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA070A', 27.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA246', 20.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA422', 24.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA554', 24.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA572', 25.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA573', 24.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA640', 17.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA642', 19.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA660', 33.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA672', 20.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHZA411', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHZA696', 96.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 96.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHZA712', 149.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 149.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TELKAA096', 28.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 28.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TELKAA097', 28.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 28.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKBEA013', 14.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPFA314', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPFA314A', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPFA315', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPFA315A', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA137', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA171A', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA196', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA327', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA332A', 1.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA334', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPHA335', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPZA157', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPZA275', 0.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKPZA276', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKWEA028', 15.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKZEA368', 19.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKZEA573', 17.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKZEA574', 17.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 17.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLDBA021', 10.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLEEA052', 20.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLEEA059', 24.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLEZA266', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLFZA097A', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLFZA098', 0.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLFZA099', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLFZN167', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGFA063', 0.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA383', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA441', 5.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA442', 5.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA443', 4.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA444', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA449', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA450', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA451', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLGZA452', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMCA082', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMCH008', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA037', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA042B', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA077', 3.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA077A', 3.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA083', 0.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA084', 7.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA085', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA098C', 7.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA099B', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA101B', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA105A', 6.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA115A', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA125A', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA126', 5.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA210', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMZA098', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMZA190', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLNEA117', 14.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQRN033A', 36.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 36.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQRN059B', 39.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 39.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQRN059C', 39.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 39.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQRN071', 33.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQRN092', 37.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 37.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQRN107', 40.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 40.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZDA075A', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMAHG214', 14.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMAHG246', 10.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMAHG323', 36.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 36.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMAHG327', 14.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMBGG051C', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMBGG052B', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMBHG235', 10.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMBHG287', 14.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMCAA003B', 10.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMCBA018', 10.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMCEA028', 26.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMCHG029D', 12.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMCHG109', 9.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMCHG113', 9.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEEA061', 9.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG305', 22.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG306', 44.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 44.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG307', 51.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 51.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG312', 44.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 44.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG313', 70.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 70.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG314', 35.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 35.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMEHG315', 19.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMFFA159', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMFFA161', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMFFA162', 0.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMFZA706', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMFZA707', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMGFG013A', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMJHG015B', 8.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMJHG085', 32.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 32.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMJHG086', 33.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMKGG272', 7.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMKGG438', 12.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMKGG442', 12.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMKHG067', 31.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 31.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMKHG068', 14.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMMHA032', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMVFG203', 7.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TMWFG0467', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA002B', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA029A', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA042', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA043', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA044', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA045', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA046', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA047', 2.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA049', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACB057', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACB058', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACB059', 1.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACB060', 1.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACB061', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACB062', 1.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRADA003B', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRADA027B', 0.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA022A', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA026A', 7.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA032A', 2.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA044', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA045', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA046', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA055', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA056', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA057', 3.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA058', 3.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA024', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA033', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA054', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA055', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA056', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA057', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA077', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA078', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA079', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA080', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA081', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA082', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA083', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA084', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZB111', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZB112', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCA003A', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCB064', 5.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCB065', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCB066', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCB067', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCB068', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCCB069', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCDB055', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCDB056', 5.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA001B', 3.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA004', 6.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA005', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA006', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA007', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA008', 10.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA009', 10.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCEA010', 6.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCZA001', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCZA002', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCZA003', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRCZA004', 2.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRDCA015B', 6.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRDCA016B', 5.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRDCA017', 3.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRDCA044', 31.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 31.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRFEA007A', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRZDA032A', 1.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('WMCJCA005', 14.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('WMCMBB009', 378.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 378.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('WMCZAZ029', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('WMCZAZ036', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA085', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA088', 29.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA089', 33.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA100', 22.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA101', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA102', 4.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA104', 46.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 46.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA105', 46.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 46.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA106', 48.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 48.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA108', 89.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 89.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZBA110', 14.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZCA017A', 0.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA097', 27.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA157', 51.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 51.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA161', 27.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA162', 54.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 54.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA163', 44.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 44.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA164', 39.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 39.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA165', 51.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 51.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA166', 94.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 94.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('XKZDA167', 94.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 94.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QBBJAB012', 11.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QBBJAA012', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABKAB012', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCADAA125A', 18.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCADAA225A', 18.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 18.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAB125', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAB225', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCAGAH022', 2.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAHAA020', 42.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 42.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAHAA018', 39.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 39.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDKW0016', 1.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRZEA062A', 7.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMEA133A', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMZA235', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ATADA0000', 15.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('ATADA0000-01', 15.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA198', 29.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA199', 9.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZEA357', 5.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAEA060', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA109', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA110', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA111', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA112', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA125', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA126', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA127', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA128', 1.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0041', 26.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 26.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA196', 27.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 27.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA197', 8.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA113', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA114', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA115', 0.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA116', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0038', 24.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA174A', 25.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA175', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0036', 23.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 23.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA194', 23.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 23.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA195', 7.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA121', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA122', 3.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA123', 2.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA124', 3.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0026', 15.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA187', 22.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 22.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA188', 6.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGEN0009A', 1.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAA036A', 11.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA180', 19.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA062', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA064', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA063', 1.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA065', 2.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QABAAA032A', 10.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZCA154', 4.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRACA051', 3.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0020', 13.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLZZAA181', 19.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA101', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA103', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA102', 1.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TRAZA104', 2.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('0LFAM0018', 12.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2130', 11.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2131', 6.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2133', 7.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0220', 7.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0221', 7.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0222', 7.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX2134', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('65XX0223', 1.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAD018B', 4.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA095', 4.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA096', 3.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA097', 0.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLMDA098', 8.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('CDKW0001', 0.85, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.85, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAM0015', 9.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 9.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAR0000A', 0.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AFAAA1000', 2.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AFAAA2000', 2.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHEP0006B', 4.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHEP0008B', 4.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGLN0019', 6.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BGLN0020', 6.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGBA1033', 29.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 29.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LEAG0014', 4.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRN784A', 25.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 25.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYBAF0012A', 12.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAA0022E', 15.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAB0022E', 10.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAC0022E', 13.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('KAAT0022', 3.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 3.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAD0018A', 6.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('PBAQ0022C', 11.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TKZEA308', 15.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQNA035B', 10.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQNA105B', 33.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TLQNA106B', 33.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 33.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RQAB0118', 11.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAHAB014A', 6.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 6.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RSFA0018', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('Y0CC0020', 4.73, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.73, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAAHAA012A', 20.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RMAB0016A', 13.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 13.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BHEP0007B', 4.2, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 4.2, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAM0000B', 5.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAR0000A', 7.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAA0000', 8.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAH0014', 20.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 20.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAG0000', 11.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 11.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAQ1400', 0.3, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.3, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAK0014', 2.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 2.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAL0014', 8.1, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 8.1, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAA1000', 7.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAA2000', 7.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 7.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAE0000', 1.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 1.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAF1036', 12.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAF2036', 12.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('BFAAH0036', 19.4, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 19.4, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCBJAB010A', 10.5, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 10.5, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('MBAH0018', 24.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('LFAQ1800', 0.45, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 0.45, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('T0012A', 24.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('T0193', 12.78, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.78, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QAADAD022B', 5.6, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 5.6, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRNA67A', 24.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 24.9, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('RQAB0116', 15.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 15.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('QCBJAB012A', 12.8, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 12.8, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('AYGAN2241', 14.7, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 14.7, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('TAHRN1000', 23.0, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 23.0, updated_at = NOW();

INSERT INTO drawing_meta (drawing_number, weight, stock, created_at, updated_at)
VALUES ('70XX0167', 21.9, 0, NOW(), NOW())
ON CONFLICT (drawing_number) DO UPDATE SET weight = 21.9, updated_at = NOW();



