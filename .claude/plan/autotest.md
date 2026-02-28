# Autotest Plan: 摊位管理平台 (Stall Management Platform)

## Project Info
- **Name**: 摊位管理平台
- **Stack**: Vue 3 + Vite (5173) | Spring Boot 3.1.5 (8080, /api) | MySQL 3306 | Redis 6379
- **UI**: Ant Design Vue 4, ECharts, Pinia, dark "Wasteland" theme
- **Auth**: JWT, roles USER/ADMIN
- **Test Accounts**: admin/admin123 (ADMIN), user/user123 (USER)
- **Track**: A (Web, has UI)

---

## Pre-Test Critical Issues (Codex Discovery)

Before autotest can run meaningfully, the following blockers must be addressed:

### Blocker 1: Frontend TypeScript Build Failure
- `TS7031` errors in: `HygieneManage.vue`, `RentalManage.vue`, `StallManage.vue`
- **Action**: Fix implicit `any` type annotations → `vue-tsc && vite build` must pass

### Blocker 2: FE/BE API Contract Drift
- Frontend API paths may not match backend controller paths (e.g., `/stalls` vs `/stall/list`)
- Frontend pagination expects `content` field, backend MyBatis-Plus returns `records/current/size/total`
- **Action**: Verify and fix API contract alignment during TWIGS phase

### Blocker 3: Login Token Parsing
- `Login.vue:96` calls `userStore.login(res.data)` but `res.data` is the full response object (contains token, userId, etc.)
- `user.ts:15` calls `setToken(newToken)` expecting a string but may receive an object
- **Action**: Verify and fix token extraction logic

### Blocker 4: Auth Error Code Mismatch
- Frontend `api/index.ts:31` only handles `code === 401`
- Backend SecurityConfig may emit `403` for forbidden
- **Action**: Verify and fix 403 handling in axios interceptor

---

## Phase 0: ROOTS — Environment Scan

### 0.1 Read Configuration Files (CRITICAL - First Step)
```yaml
Read:
  - pom.xml → Spring Boot 3.1.5, MyBatis-Plus 3.5.4.1, Spring Security, JWT
  - application.yml → port 8080, context-path /api, MySQL stall_platform:3306, Redis localhost:6379
  - frontend/package.json → Vue 3, Ant Design Vue 4, Vite 5
  - frontend/vite.config.ts → port 5173, proxy /api → localhost:8080
  - database.sql → 9 tables schema

Extracted:
  Backend: localhost:8080/api (Spring Boot)
  Frontend: localhost:5173 (Vite dev server)
  Database: MySQL localhost:3306/stall_platform (root/root)
  Cache: Redis localhost:6379
```

### 0.2 Scan Project Structure
```yaml
Backend (Java):
  Controllers: Auth, User, StallType, Stall, Application, HygieneInspection, Feedback, Announcement, RentalRecord
  Entities: User, StallType, Stall, StallApplication, HygieneInspection, Feedback, Announcement, RentalRecord, SystemLog
  Security: JwtUtil, JwtAuthenticationFilter, SecurityConfig, LoginUser, UserDetailsServiceImpl
  Config: MyBatisPlusConfig, CorsConfig, GlobalExceptionHandler

Frontend (Vue 3):
  Layouts: UserLayout (header nav), AdminLayout (sidebar menu)
  Auth Views: Login, Register
  User Views: Home, StallList, StallDetail, AnnouncementList, AnnouncementDetail,
              MyApplication, SubmitApplication, ApplicationDetail, MyRental,
              MyFeedback, SubmitFeedback, Profile
  Admin Views: Dashboard, UserManage, StallManage, StallTypeManage,
               ApplicationManage, RentalManage, HygieneManage, FeedbackManage,
               AnnouncementManage, Profile
  API: auth, user, stall, stallType, application, rental, hygiene, feedback, announcement
  Store: user (Pinia)
  Router: guards for requiresAuth, requiresAdmin
```

### 0.3 Start & Verify Services
```yaml
Check ports (from config, not guessed):
  3306 → MySQL
  6379 → Redis
  8080 → Spring Boot backend
  5173 → Vite frontend

Start sequence:
  1. MySQL must be running (prerequisite)
  2. Redis must be running (prerequisite)
  3. Backend: Start-Process cmd.exe for mvn spring-boot:run
  4. Frontend: Bash(run_in_background) npm run dev in frontend/

Verify:
  - Port checks (3 rounds max: 10s/15s/20s)
  - browser_navigate(http://localhost:5173) → browser_snapshot → confirm page title
```

---

## Phase 1: TRUNK — Route Map + Business Loops

### 1.1 Complete Route Map
```yaml
Public Routes:
  /           → Home (banner + announcements + quick links)
  /stall      → Stall list (browse, filter)
  /stall/:id  → Stall detail
  /announcement    → Announcement list
  /announcement/:id → Announcement detail
  /login      → Login page
  /register   → Register page

Protected Routes (requiresAuth):
  /application     → My applications
  /application/submit/:stallId → Submit application
  /application/:id → Application detail
  /rental          → My rentals
  /feedback        → My feedback
  /feedback/submit → Submit feedback
  /profile         → User profile

Admin Routes (requiresAuth + requiresAdmin):
  /admin              → Dashboard (ECharts)
  /admin/user         → User management CRUD
  /admin/stall        → Stall management CRUD
  /admin/stall/type   → Stall type management CRUD
  /admin/application  → Application review
  /admin/rental       → Rental management
  /admin/hygiene      → Hygiene inspection
  /admin/feedback     → Feedback handling
  /admin/announcement → Announcement management
  /admin/profile      → Admin profile
```

### 1.2 Auth Boundary
```yaml
Public: /, /stall, /stall/:id, /announcement, /announcement/:id, /login, /register
Protected: /application/*, /rental, /feedback/*, /profile
Admin: /admin/**
Guard: router.beforeEach checks userStore.isLoggedIn() and userStore.isAdmin()
```

### 1.3 Business Loops
```yaml
Loop 1 - Auth (P0):
  Steps: Register → Login → Verify session/token → Access protected route → Logout → Verify redirect
  Validation: Token in localStorage, user info fetched, role correct

Loop 2 - Stall Application (P1):
  Steps: User browses /stall → Select stall → Navigate to /application/submit/:stallId → Fill form → Submit
         → Admin login → /admin/application → Review (approve/reject) → Rental record created
  Validation: Application appears in user's list, status changes, rental record generated on approval

Loop 3 - Feedback (P1):
  Steps: User submits feedback (/feedback/submit) → Admin views (/admin/feedback) → Admin replies
         → User sees reply in /feedback
  Validation: Feedback status transitions (待处理→处理中→已处理), reply content visible

Loop 4 - Announcement (P1):
  Steps: Admin creates announcement (/admin/announcement) → User views (/announcement) → User reads detail
  Validation: Announcement appears in list and detail page

Loop 5 - Hygiene Inspection (P2):
  Steps: Admin creates inspection record (/admin/hygiene) → Sets score/result → Marks remediation
  Validation: Status transitions, score display

Loop 6 - User Management CRUD (P2):
  Steps: Admin creates/edits/deletes users, resets passwords
  Validation: Table updates, user status changes

Loop 7 - Stall CRUD (P2):
  Steps: Admin manages stalls and stall types
  Validation: CRUD operations reflect in tables
```

### 1.4 Task Creation (Loops + Branches)
```yaml
Loop Tasks:
  - "Loop: Auth (Register→Login→Logout)"
  - "Loop: Stall Application Lifecycle"
  - "Loop: Feedback Submit→Reply"
  - "Loop: Announcement Publish→View"

Branch Tasks:
  - "Branch: Login/Register Pages"
  - "Branch: Home Page"
  - "Branch: Stall Browsing (List + Detail)"
  - "Branch: Announcement (List + Detail)"
  - "Branch: My Applications"
  - "Branch: My Rentals"
  - "Branch: My Feedback"
  - "Branch: User Profile"
  - "Branch: Admin Dashboard"
  - "Branch: Admin User Management"
  - "Branch: Admin Stall Management"
  - "Branch: Admin Stall Type Management"
  - "Branch: Admin Application Review"
  - "Branch: Admin Rental Management"
  - "Branch: Admin Hygiene Inspection"
  - "Branch: Admin Feedback Handling"
  - "Branch: Admin Announcement Management"
  - "Branch: Admin Profile"
```

---

## Phase 2: BRANCHES — Loop Execution Order

### Round 1: Business Loops (Priority Order)
```yaml
1. Auth Loop (P0) — Foundation for all other tests
2. Stall Application Loop (P1) — Core business flow
3. Feedback Loop (P1) — User-facing service
4. Announcement Loop (P1) — Content management
5. Hygiene Inspection Loop (P2) — Admin governance
```

### Round 2: Branch Exploration (All Pages)
```yaml
Priority:
  1. Login/Register pages — Auth UI verification
  2. Home page — Landing experience
  3. Stall browsing — Core public pages
  4. Admin Dashboard — Admin landing
  5. Admin CRUD pages (user/stall/stallType) — Management operations
  6. Admin review/process pages (application/feedback/announcement) — Workflow pages
  7. User protected pages (application/rental/feedback/profile) — User features
  8. Admin utility pages (hygiene/rental/profile) — Lower priority
```

---

## Phase 3: TWIGS — Per-Branch Test Points

### For Each Page (snapshot-driven discovery):
```yaml
Static Content:
  - All heading/title text (no placeholder, no undefined/null/NaN)
  - Navigation menu items (correct text, correct active state)
  - Footer content (correct year, version)
  - Breadcrumbs (correct hierarchy)
  - Empty state messages (friendly text, not blank)

Interactive Elements:
  - Buttons: click → verify action (modal opens, navigation, form submit)
  - Forms: fill → submit → verify success/error feedback
  - Tables: data display, pagination, sorting, search/filter
  - Modals: open → fill → confirm/cancel → verify result
  - Dropdowns: open → select → verify selection
  - Links: click → verify correct navigation

State Tests (inline):
  - disabled buttons → verify disabled state makes sense
  - required fields → test empty submission
  - loading states → verify spinner during API calls

Boundary Tests (inline):
  - text inputs: empty, max-length (256 chars)
  - number inputs: 0, negative
  - date pickers: past dates, invalid ranges
```

---

## Phase 4: LEAVES — Deep Checks

### P0 Must-Test:
```yaml
Responsive:
  - Desktop (default) → baseline
  - Tablet (768x1024) → layout check
  - Mobile (375x812) → nav collapse, table scroll

Security:
  - XSS: <img onerror=alert(1) src=x> in text inputs
  - Unauthorized access: direct navigate to /admin without login
  - Token check: localStorage content inspection

Accessibility:
  - Form labels associated with inputs
  - Icon buttons have aria-labels/tooltips
  - Tab navigation order
  - Escape closes modals

Browser Behavior:
  - F5 refresh preserves state
  - Back/forward navigation
  - Deep link direct access (e.g., /stall/1)
```

### P1 Should-Test:
```yaml
Concurrency:
  - Double-click submit buttons
  - Rapid tab switching

i18n/Encoding:
  - Chinese input: "测试数据"
  - Emoji: "🎉✅"
  - SQL injection: "' OR 1=1 --"

Performance:
  - Page load time (no white screen >3s)
  - Loading indicators present
  - Large table pagination
```

### P2 Nice-to-Test:
```yaml
- Search with special characters
- Sort column verification
- Multi-step form interruption
- Dark theme consistency (modals, dropdowns, date-pickers inheriting theme)
```

---

## Bug Fix Strategy

```yaml
On Discovery:
  1. Identify: snapshot + console_messages + network_requests
  2. Locate: Grep codebase → Read file context
  3. Classify: frontend UI | frontend logic | backend API | config
  4. Fix: Edit (minimal change)
  5. Verify: Re-run triggering action → snapshot → confirm fix
  6. Regress: Check related tests still pass
  Max 2 fix rounds per bug, then skip with detailed note

Pre-identified Issues to Fix During Test:
  - TS build errors (HygieneManage, RentalManage, StallManage)
  - Login token parsing (Login.vue + user store)
  - API contract alignment (pagination response model)
  - 403 error handling in axios interceptor
  - Dark theme overrides for Ant Design Vue components
```

---

## Execution Constraints

```yaml
Tools:
  - npm/node commands → Claude Code Bash (NOT windows-mcp)
  - Port checks → windows-mcp Get-NetTCPConnection
  - Service startup (Spring Boot) → windows-mcp Start-Process cmd.exe
  - Page testing → Playwright MCP (browser_navigate, browser_snapshot, browser_click, etc.)
  - Code fixes → Read + Edit tools
  - NO browser_take_screenshot → use browser_snapshot
  - NO State-Tool(use_vision=True)
```

---

## SESSION_ID (for /ccg:execute use)
- CODEX_SESSION: 019ca3ef-1d30-7643-bfe9-72803391a589
- GEMINI_SESSION: 1876a496-724b-4a1e-9126-2be1c83d1e6e
