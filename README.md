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
2. Cấu hình kết nối qua biến môi trường (mặc định trong `application.properties`, file này không commit lên git):
   - `DB_HOST` (localhost), `DB_PORT` (5432), `DB_NAME` (qms), `DB_USER` (postgres), `DB_PASSWORD` (postgres)
3. Chạy:
   ```
   mvn spring-boot:run
   ```
   Flyway sẽ tự tạo toàn bộ schema (14 bảng + sequence `qa_ticket_seq`) khi khởi động.

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

## API

```
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
