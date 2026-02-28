# Implementation Plan: Core Fixes (Option A)

## Execution Order
P0 → P1-3 → P1-5 → P1-2 → P1-4 → P1-6

---

## 1. P0: Admin Route Guard Hardening

### Frontend Changes
**File: `frontend/src/router/index.ts`**
- Make `beforeEach` async
- If token exists but `userStore.user` is null, call `await userStore.fetchUserInfo()` before admin check
- Add `message.warning('您无权访问管理后台')` for non-admin access attempts
- Import `message` from `ant-design-vue`

### Backend Changes
- None required (existing `hasRole("ADMIN")` on `/*/admin/**` pattern is sufficient)
- Backend already returns 403 for non-admin access

---

## 2. P1-3: Mobile Responsive AdminLayout

### File: `frontend/src/components/layout/AdminLayout.vue`

**Template:**
- Add `breakpoint="lg"` and `collapsed-width="0"` to `<a-layout-sider>`
- Add `MenuOutlined` hamburger toggle in header for mobile
- Import `MenuOutlined` icon

**Script:**
- Add `isMobile` reactive state based on window width (breakpoint: 992px)
- Add `onMounted`/`onUnmounted` resize listener OR use `@collapse` event from sider

**CSS:**
- Add media query `@media (max-width: 992px)`:
  - `.header { margin-left: 0 !important; padding: 0 16px; }`
  - `.content { margin-left: 0 !important; margin: 16px; padding: 16px; }`
  - `.sider { z-index: 1000; }` (overlay mode)
- When collapsed + mobile: `margin-left: 0` for header and content

---

## 3. P1-5: ECharts Mobile Fix

### File: `frontend/src/views/admin/Dashboard.vue`

**Template:**
- Replace fixed `:span="6"` with responsive `:xs="24" :sm="12" :lg="6"` on stat cards
- Replace `:span="16"` with `:xs="24" :lg="16"` on chart area
- Replace `:span="8"` with `:xs="24" :lg="8"` on pie chart
- Replace `:span="12"` with `:xs="24" :lg="12"` on lists

**CSS:**
- Add `min-width: 0;` to `.chart-container` (prevents flexbox collapse)
- Add `min-height: 280px;` to `.chart`
- Add `width: 100%; display: block;` to `.chart`

---

## 4. P1-2: Date Formatting

### New File: `frontend/src/utils/format.ts`
```ts
import dayjs from 'dayjs'

export const formatDateTime = (date: string | Date | undefined | null): string => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

export const formatDate = (date: string | Date | undefined | null): string => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}
```

### Files to update (import and apply formatDateTime):
**Admin views:** AnnouncementManage, ApplicationManage, FeedbackManage, HygieneManage, RentalManage, StallTypeManage, UserManage
**User views:** AnnouncementList, AnnouncementDetail, ApplicationDetail, MyApplication, MyFeedback, MyRental, Home

Apply via:
- Table columns: `customRender` or `<template #bodyCell>` interception
- Template bindings: `{{ formatDateTime(item.createTime) }}`

---

## 5. P1-4: Dynamic Copyright Year

### File: `frontend/src/components/layout/UserLayout.vue`
- Line 76: Replace `© 2024` with `© {{ new Date().getFullYear() }}`

---

## 6. P1-6: Announcement Link Accessibility

### File: `frontend/src/views/user/AnnouncementList.vue`
- Line 10: Replace `<a @click="router.push(...)">` with `<router-link :to="...">`
- Add scoped CSS for `.announcement-link` styling (inherit theme colors)

### File: `frontend/src/views/user/Home.vue` (if applicable)
- Check and fix similar `@click` patterns for announcement titles
