# Personnel Insight — Spring Boot + đăng nhập + database

## Bản này làm được gì?
- Kế thừa giao diện dashboard, thống kê, biểu mẫu báo cáo, danh sách và biểu đồ từ bản HTML gốc.
- Không có dữ liệu giả được tạo tự động. Chỉ thống kê hồ sơ do bạn nhập.
- Đăng nhập tài khoản admin, mật khẩu BCrypt trong RAM; quản lý phiên bằng Spring Security và CSRF token.
- API thêm/sửa/xóa hồ sơ, lưu vào database trên ổ đĩa; làm mới trình duyệt không mất dữ liệu.
- Xuất CSV/JSON, nhập sao lưu JSON vào database.

**Đây là bản prototype phát triển/kiểm thử, KHÔNG dùng hồ sơ quân nhân thật hoặc đưa ra Internet.** Chưa có MFA, RBAC nhiều tài khoản, audit trail, backup tự động, TLS, quy trình phê duyệt, hardening máy chủ.

## Bắt đầu trong IntelliJ IDEA
1. Giải nén, `File > Open` chọn thư mục `PersonnelInsight_SpringBoot` (nơi chứa `pom.xml`). Đợi Maven tải dependencies (cần Internet để tải lần đầu).
2. Dùng JDK **17 hoặc 21** (Spring Boot 3.3.x).
3. `Run > Edit Configurations > PersonnelInsightApplication` > **Environment variables**: `APP_ADMIN_USER=admin;APP_ADMIN_PASSWORD=MatKhauDaiTu12KyTuTroLen` (**tự thay bằng mật khẩu mạnh**; không ghi mật khẩu trong code).
4. Run `PersonnelInsightApplication.java`.
5. Mở `http://127.0.0.1:8080/` -> tự chuyển đến trang đăng nhập. Đăng nhập bằng tài khoản bạn đặt.
6. Nhập vài hồ sơ **giả lập** để kiểm tra biểu đồ, sửa/xóa, lưu qua database.

> Dữ liệu của bản demo H2 nằm ở thư mục `data/` trong **working directory** của Run Configuration. Không xóa thư mục này nếu muốn giữ dữ liệu thử nghiệm.

## Kết nối MySQL (khi đã được cho phép và đã kiểm tra bảo mật)
Có profile `mysql`: tạo DB rỗng `personnel_insight` UTF8MB4, tài khoản DB có quyền SELECT/INSERT/UPDATE/DELETE/CREATE trên database đó (CREATE chỉ cần lúc khởi tạo schema). Chọn active profile `mysql` và cấu hình biến môi trường `DB_URL`, `DB_USER`, `DB_PASSWORD`. Không ghi mật khẩu vào Git. `schema.sql` tạo bảng `personnel_records` nếu chưa tồn tại. Không dùng lại database `floweradmin`.

**Không chuyển dữ liệu thật lên hosting công khai / GitHub Pages.** Khi triển khai, cần phê duyệt của đơn vị, hạ tầng được phép, HTTPS, VPN/kiểm soát truy cập phù hợp, phân quyền, nhật ký, backup, rà soát bảo mật. `server.address=127.0.0.1` hiện mặc định chỉ truy cập trên máy chạy ứng dụng.

## API
- `GET /api/csrf` : CSRF token dùng cho thao tác ghi / đăng xuất
- `GET /api/people` : tất cả hồ sơ (sau đăng nhập)
- `POST /api/people` : thêm
- `PUT /api/people/{id}` : sửa
- `DELETE /api/people/{id}` : xóa
- `PUT /api/people/bulk` : thay thế dữ liệu khi import JSON
- `GET /api/me` : tài khoản hiện tại

Mã nguồn đặt tại `src/main/java/com/personnel/insight`; giao diện tại `src/main/resources/static`.
