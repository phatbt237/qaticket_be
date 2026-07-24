# QMS Backend — Garment QA Checking

Spring Boot backend sinh từ đặc tả trong file `source`: 14 bảng (danh mục / đơn hàng / giao dịch),
aggregate root `QaTicket` (Ticket → Defect → Location → Image), REST API và sinh mã ticket tự động.

## Công nghệ

- Java 17, Spring Boot 3.3.4
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL + Flyway (migration ở `src/main/resources/db/migration/V1__init.sql`)
- Lombok

## Chạy local

1. Tạo database Postgres tên `qms` (hoặc set biến môi trường khác).
2. `application.properties` (đã commit) chỉ chứa placeholder đọc từ biến môi trường, không có secret
   thật nào. Secret thật (DB Neon, R2, JWT) đặt trong `application-local.properties` — file này
   **không commit** (đã gitignore), profile `local` sẽ tự động dùng nó khi chạy `mvn spring-boot:run`.
   - `DB_HOST` (localhost), `DB_PORT` (5432), `DB_NAME` (qms), `DB_USER` (postgres), `DB_PASSWORD` (postgres)
   - `JWT_SECRET` **bắt buộc phải set** (không có default) — đặt trong `application-local.properties`
     hoặc export biến môi trường trước khi chạy.
3. Chạy:
   ```
   mvn spring-boot:run
   ```
   Flyway sẽ tự tạo toàn bộ schema (14 bảng + sequence `qa_ticket_seq`) khi khởi động.

## Deploy Docker / Render

```
docker build -t qms-backend .
docker run -p 8080:8080 --env-file .env qms-backend
```

Trên Render: tạo **Web Service** kiểu **Docker**, trỏ vào repo này (Render tự nhận `Dockerfile` ở
gốc repo). App đọc cổng qua `$PORT` mà Render tự inject, không cần set `SERVER_PORT` thủ công.

Biến môi trường cần set trong Render dashboard (Settings → Environment):

- `SPRING_PROFILES_ACTIVE` — đặt bất kỳ giá trị khác `local` (vd `render`), tránh nhầm lẫn tên thôi,
  vì `application-local.properties` không có trong image nên dù để `local` cũng không lỗi.
- `JWT_SECRET` — **bắt buộc**, chuỗi ngẫu nhiên đủ dài (vd `openssl rand -base64 48`).
- Database: hoặc set `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD` (khớp pattern
  URL không query param), hoặc set thẳng `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` /
  `SPRING_DATASOURCE_PASSWORD` nếu DB cần query param như Neon (`?sslmode=require&...`) — env var
  này override toàn bộ `spring.datasource.url` trong file.
- `R2_ENDPOINT`, `R2_BUCKET`, `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_PUBLIC_BASE_URL` — cấu hình
  Cloudflare R2 để upload ảnh (`/api/uploads/images`).
- Tùy chọn: `JWT_EXPIRATION_MS` (mặc định 3600000 = 1h), `JWT_REFRESH_EXPIRATION_MS` (mặc định
  2592000000 = 30 ngày), `R2_UPLOAD_THREAD_POOL_SIZE` (mặc định 8).

## Cấu trúc chính

```
entity/          14 JPA entity (master data + PO + QA ticket aggregate)
entity/enums/     StaffRole, InspectionStage, TicketStatus
repository/       Spring Data JPA repository cho từng entity
dto/master/       Response DTO cho các API /api/master/*
dto/po/           Response DTO cho /api/purchase-orders
dto/ticket/       Request/Response DTO lồng nhau cho QA ticket (ticket → defect → location → image)
service/          QaTicketService (create/update/get/list/delete), TicketCodeGenerator, Specification filter
controller/       QaTicketController, MasterDataController, PurchaseOrderController
exception/        ResourceNotFoundException, InvalidTicketStateException, GlobalExceptionHandler
config/           CORS cho FE gọi API
```

## Đăng nhập

Toàn bộ API `/api/**` (trừ `/api/auth/**` và Swagger) yêu cầu JWT trong header
`Authorization: Bearer <accessToken>`. Tài khoản đăng nhập dùng `code` của bảng `staff` làm username.
Mật khẩu lưu dạng BCrypt (`staff.password`), mật khẩu mặc định cho toàn bộ tài khoản seed là `123456`.

```
POST /api/auth/login
{ "username": "QA001", "password": "123456" }
→ { accessToken, refreshToken, tokenType, expiresInMs, staffId, code, fullName, role }
```

- `accessToken` (JWT) hết hạn sau **1 giờ** (`jwt.expiration-ms`).
- `refreshToken` (chuỗi ngẫu nhiên lưu ở bảng `refresh_token`) hết hạn sau **30 ngày**
  (`jwt.refresh-expiration-ms`), dùng để lấy access token mới mà không cần đăng nhập lại:

```
POST /api/auth/refresh
{ "refreshToken": "..." }
→ { accessToken, refreshToken, ... }   (refresh token cũ bị thu hồi ngay - rotation)

POST /api/auth/logout
{ "refreshToken": "..." }
→ 204, thu hồi refresh token (đăng xuất thật sự trên server)
```

## Upload ảnh (Cloudflare R2)

```
POST /api/uploads/images   (multipart/form-data, field "files", nhiều file)
→ ["https://pub-xxxx.r2.dev/qa-ticket/<uuid>.jpg", ...]   (cùng thứ tự với files gửi lên)
```

Nhiều ảnh trong 1 request được upload **song song** (thread pool cố định, `r2.upload-thread-pool-size`,
mặc định 8 luồng) thay vì tuần tự — xem `R2StorageService.uploadAll`. Chỉ nhận `image/jpeg|png|webp|gif`,
tối đa 10MB/ảnh, 50MB/request (`spring.servlet.multipart.*`). URL trả về dùng trực tiếp trong
`images` của `QaTicketDefectLocationRequest` khi tạo/sửa ticket. Cấu hình R2 (`r2.endpoint`, `r2.bucket`,
`r2.access-key`, `r2.secret-key`, `r2.public-base-url`) đặt trong `application-local.properties`
(không commit) hoặc qua biến môi trường `R2_*`.

## API

```
POST   /api/auth/login              đăng nhập, trả accessToken + refreshToken
POST   /api/auth/refresh            lấy accessToken mới từ refreshToken (rotation)
POST   /api/auth/logout             thu hồi refreshToken

POST   /api/uploads/images          upload nhiều ảnh song song lên R2, trả về URL

POST   /api/qa-tickets              tạo mới (payload lồng nhau: ticket + defects + locations + images)
PUT    /api/qa-tickets/{id}         cập nhật toàn bộ (dùng cho sửa/lưu draft)
GET    /api/qa-tickets/{id}         chi tiết 1 phiếu (đầy đủ cây con)
GET    /api/qa-tickets              danh sách, filter factoryId/lineId/staffId/status/dateFrom/dateTo, có phân trang
DELETE /api/qa-tickets/{id}         xóa (chỉ khi status = DRAFT, ngược lại trả 409)

GET    /api/master/staff
GET    /api/master/factories
GET    /api/master/lines?factoryId=
GET    /api/master/groups?lineId=
GET    /api/master/customers
GET    /api/master/garment-types
GET    /api/master/garment-locations?garmentTypeId=
GET    /api/master/defects

GET    /api/purchase-orders?search=
```

Mã ticket được sinh tự động qua sequence Postgres riêng `qa_ticket_seq`, format `I%05d`
(vd `I00003`) — xem `TicketCodeGenerator` và `QaTicketRepository#nextTicketSequence`.

Toàn bộ cây con (`defects` → `locations` → `images`) được lưu/xóa trong cùng 1 transaction nhờ
`cascade = CascadeType.ALL, orphanRemoval = true`; khi PUT lại một ticket, danh sách con cũ bị
xóa (orphan removal) và được dựng lại từ payload mới.

## Test

```
mvn test
```

Test khởi động context bằng H2 in-memory (profile `test`, xem `application-test.properties`), Flyway bị
tắt trong test — schema được Hibernate tự tạo để kiểm tra mapping.
