# Autotest Plan: Stall Management Platform

## Project Info
- **Stack**: Spring Boot 3.1.5 + Vue 3 + Ant Design Vue + MySQL + Redis
- **Backend**: port 8080, context `/api`
- **Frontend**: port 5173, Vite proxy `/api` → `http://localhost:8080`
- **Auth**: JWT (USER/ADMIN roles), credentials: admin/admin123, user/user123
- **Track**: A (Web/UI)

## Strategy
- Follow autotest tree workflow: ROOTS → TRUNK → BRANCHES → TWIGS → LEAVES → REPORT
- FIX: UI bugs, display issues, broken flows, frontend errors
- DOCUMENT (don't fix): Backend security architecture issues (JWT secret, CORS wildcard, input validation framework)

---

## Phase 0: ROOTS — Environment Scan + Service Startup

1. Read all config files (application.yml, package.json, vite.config.ts)
2. Check MySQL 3306, Redis 6379 connectivity
3. Start backend (mvn spring-boot:run) if not running
4. Start frontend (npm run dev) if not running
5. Verify both services respond

## Phase 1: TRUNK — Project Map + Business Loops

### Business Loops (test end-to-end flows first)
1. **Auth Loop**: register → login → verify session → logout
2. **Application Loop**: browse stalls → apply → admin approve → rental created
3. **Feedback Loop**: user submit → admin reply → user sees reply
4. **Admin CRUD Loop**: create stall type → create stall → create announcement → verify public

### Route Map
**Public**: /, /stall, /stall/:id, /announcement, /announcement/:id
**Auth**: /login, /register
**User (protected)**: /application, /application/submit/:stallId, /rental, /feedback, /feedback/submit, /profile
**Admin (protected)**: /admin, /admin/user, /admin/stall, /admin/stall/type, /admin/application, /admin/rental, /admin/hygiene, /admin/feedback, /admin/announcement, /admin/profile

## Phase 2: BRANCHES — Testing Order

### Order (dependency-aware):
1. Public pages (no auth needed): /, /stall, /announcement
2. Auth flow: /register, /login
3. Admin data seeding: create stall types, stalls, announcements via /admin/*
4. User workflows: apply for stall, submit feedback via user routes
5. Admin processing: approve applications, reply feedback
6. Cross-verification: user sees updated status, dashboard reflects changes

### Branch List:
- Branch: Home Page (/)
- Branch: Auth Flow (/login, /register)
- Branch: Stall Browsing (/stall, /stall/:id)
- Branch: Announcements (/announcement, /announcement/:id)
- Branch: Admin Dashboard (/admin)
- Branch: Admin User Management (/admin/user)
- Branch: Admin Stall Management (/admin/stall, /admin/stall/type)
- Branch: Admin Application Management (/admin/application)
- Branch: Admin Rental Management (/admin/rental)
- Branch: Admin Hygiene Management (/admin/hygiene)
- Branch: Admin Feedback Management (/admin/feedback)
- Branch: Admin Announcement Management (/admin/announcement)
- Branch: User Application (/application, /application/submit/:stallId)
- Branch: User Rental (/rental)
- Branch: User Feedback (/feedback, /feedback/submit)
- Branch: User Profile (/profile)

## Phase 3-4: TWIGS + LEAVES — Per-Branch Testing

Each branch: navigate → snapshot → identify all visible elements → test interactions → check edge cases → responsive check → console/network check

### Deep Checks (P0):
- Responsive: 375px (mobile), 768px (tablet), default (desktop)
- Security: XSS input test, unauthorized route access
- Accessibility: form labels, keyboard nav, ARIA
- Browser: F5 refresh, back/forward, deep links

### Deep Checks (P1):
- Concurrent: double-submit prevention
- Encoding: Chinese chars, emoji, special chars
- Performance: loading states, white screen check
- Error recovery: API error handling display

## Phase 5: REPORT

- Test report with pass/fail/skip/fixed counts
- System improvement document (security issues, missing features)
