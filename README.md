# 📚 VieTruyen – WEB ĐỌC TRUYỆN TRANH ONLINE
  VieTruyen là một website đọc truyện tranh online tập trung giải quyết các bài toán thực tế ở backend như: phân quyền, caching, cron job, mua bán vật phẩm, lưu lịch sử đọc, quản lý nội dung và tối ưu hiệu năng.

Mục tiêu dự án:

1. Là cơ sở để xây dựng đồ án tốt nghiệp 

2. Áp dụng các nghiệp vụ gần với hệ thống thực tế

## 🔥 II. Mô tả một số bài toán nổi bật đã xử lý

### 1. Xác thực và phân quyền người dùng (RBAC + Token Whitelist)

Hệ thống áp dụng mô hình **RBAC (Role-Based Access Control)** với 3 actor chính:

- **Admin**: quản lý hệ thống, duyệt nội dung
- **Author**: đăng và quản lý truyện/chapter của mình
- **User**: đọc và tương tác nội dung

Quy trình xác thực sử dụng **JWT**, kết hợp với cơ chế **Token Whitelist** để tăng cường bảo mật.  
Mỗi token hợp lệ phải tồn tại trong whitelist, cho phép hệ thống:

- Chủ động **thu hồi token** khi người dùng đăng xuất
- Kiểm soát phiên đăng nhập trên nhiều thiết bị
- Giảm rủi ro khi token bị lộ

Phân quyền được kiểm soát ở cả **route-level** và **business-level**, đảm bảo mỗi actor chỉ có thể truy cập đúng phạm vi chức năng được cấp.

---

###  2. Quy trình đăng truyện có lịch phát hành và kiểm duyệt

Hệ thống hỗ trợ **Author đăng truyện/chapter kèm lịch phát hành (schedule publish)** theo quy trình sau:

1. Author tạo truyện/chapter và thiết lập thời điểm phát hành mong muốn
2. Nội dung được chuyển sang trạng thái **chờ duyệt**
3. **Admin thực hiện kiểm duyệt nội dung** trước khi cho phép phát hành

Để đảm bảo luồng phát hành không bị gián đoạn:
- Nếu **quá thời điểm phát hành đã định mà Admin chưa duyệt**, hệ thống sẽ **tự động gửi thông báo nhắc nhở đến Admin**
- Sau khi Admin duyệt thành công, **thời gian phát hành được tự động cộng thêm 1 ngày**, đảm bảo nội dung vẫn được hiển thị hợp lệ và không bị “miss lịch”

Giải pháp này giúp:
- Tách biệt rõ trách nhiệm Author – Admin
- Tránh tình trạng nội dung bị treo do chậm duyệt
- Giữ trải nghiệm nhất quán cho người đọc

---

## 3. Nested Comment – Thiết kế tối ưu cho đọc dữ liệu (Read-heavy)

### 3.1 Bài toán đặt ra

- Hệ thống hỗ trợ **comment dạng cây**, reply **không giới hạn cấp**
- Comment được load **mỗi lần người dùng mở chapter**
- Tần suất **đọc comment cao hơn rất nhiều so với ghi**

➡️ Mục tiêu: **đọc nhanh – truy vấn ít – dữ liệu nhất quán**

---

### 3.2 Mô hình dữ liệu sử dụng

Hệ thống sử dụng **Nested Set Model**, phù hợp với bài toán **đọc nhiều – ghi ít**.

```text
comment
- comment_id
- parent_comment_id
- story_id
- chapter_id
- content
- comment_left
- comment_right
- user_id
- created_at
```
---
  
**Note"** Các solution đã được áp dụng vào repo đồ án tốt nghiệp (branch develop) based trên repo này: https://github.com/helloVietTran/graduate-project

## III. Tính năng chính
- Tìm kiếm truyện theo nhiều tiêu chí (số chapter, thời gian đăng, truyện hot hay không)
- Lưu lịch sử đọc truyện theo 2 cách: lưu lịch sử theo thiết bị (local storage) và lưu lịch sử theo tài khoản
- Chức năng kiếm coin bằng cách đăng nhập hàng ngày
- Xây dựng chức năng mua vật phẩm cửa hàng (mua khung avatar, hiệu ứng text) 
- Cung cấp API quản lý truyện, quản lý chapter, quản lý người dùng, ... và quản lý tệp ảnh trên cloud
- Xây dựng tính năng tính toán level bằng cách đọc truyện, có hiệu ứng tên khác biệt giữa các level
- Phân quyền theo vai trò, có 3 vai trò user, author và admin.
- Tính năng bảng xếp hạng người dùng thúc đẩy người dùng ở lại trang web ( xếp người dùng theo số điểm và theo level) được tối ưu bằng redis, revalidate theo từng ngày.
- Chức năng bình luận theo nested set model tối ưu cho việc đọc

## 🛠️ IV. Công nghệ nổi bật
     Spring Boot, Spring Security, Spring JPA, Spring Thymeleaf, Redis, MySQL
     
## Cài đặt & chạy dự án

### Yêu cầu môi trường

- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### Bước 1: Clone source

```bash
   git clone https://github.com/helloVietTran/reading-story-web-be-java
   cd reading-story-web-be-java
```
### Bước 2: Chỉnh sửa cấu hình kết nối với MySQL, Redis cho phù hợp trong file application.yml

### Bước 3: Build & chạy

```bash
   mvn clean install
   mvn spring-boot:run
```
