# Deploy Railway - CHỈ DEMO BẰNG DỮ LIỆU GIẢ

1. Đưa thư mục này vào một GitHub repository **private** (không đưa file .env, dữ liệu thật, mật khẩu lên GitHub).
2. railway.app > New Project > Deploy from GitHub repo > chọn repository.
3. Add New > Database > MySQL. Đặt tên service MySQL (nếu đặt khác, sửa references bên dưới).
4. Service Java > Variables (tạo theo từng tên/giá trị):
   - SPRING_PROFILES_ACTIVE = mysql
   - APP_ADMIN_USER = admin
   - APP_ADMIN_PASSWORD = (mật khẩu riêng >= 12 ký tự)
   - DB_USER = ${{MySQL.MYSQLUSER}}
   - DB_PASSWORD = ${{MySQL.MYSQLPASSWORD}}
   - DB_URL = jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useUnicode=true&characterEncoding=UTF-8&connectionTimeZone=UTC
   - SERVER_ADDRESS = 0.0.0.0
   - JAVA_TOOL_OPTIONS = -XX:MaxRAMPercentage=75.0
5. Apply / Deploy các biến môi trường; xem logs và chờ dòng Started PersonnelInsightApplication.
6. Service Java > Settings > Networking > Generate Domain; mở https://...up.railway.app.
7. Đăng nhập, tạo 1 hồ sơ GIẢ, reload/đăng nhập lại và kiểm tra dữ liệu vẫn còn.
8. MySQL service > Backups > bật lịch backup nếu gói hỗ trợ; kiểm tra khôi phục thử.
9. Kiểm tra chi phí Railway/đặt cảnh báo ngân sách trước khi để luôn bật.

LƯU Ý: profile mysql yêu cầu MySQL service sẵn sàng; không public MySQL; chỉ dùng dữ liệu giả, chưa đủ bảo mật và quy trình để vận hành hồ sơ quân nhân thật. GitHub repository không lưu mật khẩu.
