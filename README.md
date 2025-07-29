### 📚 Về dự án - Web đọc truyện - VStory
   Dự án được clone một cách tối đa từ một web đọc truyện nổi tiếng, cung cấp nhiều tính năng cho người dùng 

### Tính năng chính
- Tìm kiếm truyện theo nhiều tiêu chí (số chapter, thời gian đăng, truyện hot hay không)
- Lưu lịch sử đọc truyện theo 2 cách: lưu lịch sử theo thiết bị (local storage) và lưu lịch sử theo tài khoản
- Chức năng kiếm xu bằng cách đăng nhập hàng ngày
- Xây dựng chức năng mua vật phẩm (mua khung avatar) để giữ chân người dùng
- Cung cấp API quản lý truyện, quản lý chapter, quản lý người dùng, ... và quản lý tệp ảnh trên cloud
- Xây dựng tính năng tính toán level bằng cách đọc truyện, có hiệu ứng tên khác biệt giữa các level
- Phân quyền theo vai trò, chỉ có admin mới có quyền đăng truyện tranh mới
- Tính năng bảng xếp hạng người dùng thúc đẩy người dùng ở lại trang web ( xếp người dùng theo số điểm và theo level)
- Các tính năng: quên mật khẩu, tùy chỉnh thông tin cá nhân
- Thực hiện kiểm tra xem người dùng đã đọc chapter chưa, nếu đọc rồi sẽ báo cho người đọc
- Dark - Light theme
- Chức năng bình luận
- Hệ thống alert rõ ràng

### Công việc ở backend
- Phân tích bài toán website đọc truyện tranh và xây dựng các bảng có mối quan hệ phù hợp để lưu dữ liệu đáp ứng bài toán
- Xử lý một số nghiệp vụ có độ thực tế cao: Dựa vào số chapter người dùng đọc => chuyển nó thành điểm kinh nghiệm cho người dùng để lên level, chức năng quên mật khẩu
- Cron một số công việc: đồng bộ dữ liệu lượt thích từ Redis vào MySQL (tuy nhiên chưa xử lý được issue sâp server gây mất dữ liệu trong Redis), đánh giá truyện hot
- Tích hợp với hệ thống bên thứ 3: Email Service (Brevo) + Cloudinary (lưu file ảnh và quản lý thêm, xóa file ảnh)
- Caching các dữ liệu bảng xếp hạng như top truyện được đọc nhiều nhất để tối ưu hiệu năng
- Quản lý các template .html để gửi qua mail cho người dùng
- Cấu hình xác thực và phân quyền bằng JWT dùng Spring Security
- Xử lý tìm kiếm đa tiêu chí bằng cách custom phương thức trong repository
- Xử lý exception tốt, mỗi request thất bại đều có mã lỗi và message phù hợp để dễ debug
- Phân trang dữ liệu danh sách
- Validate request và xây dựng các mapper
- Thử viết dockerfile và dockerize dự án 

  
## 🛠️ Công nghệ nổi bật
     Spring Boot, Spring Security, Spring JPA, Spring Thymeleaf, Redis, MySQL

### 👤 Thông tin tài khoản test 
📌 Admin: tài khoản: admin123@gmail.com | mật khẩu: adminweb123
