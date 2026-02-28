// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页请求 (MyBatis-Plus: pageNum 1-based, pageSize)
export interface PageQuery {
  pageNum?: number
  pageSize?: number
}

// 分页响应 (MyBatis-Plus IPage format)
export interface PageResult<T> {
  records: T[]
  total: number
  pages: number
  size: number
  current: number
}

// 用户相关
export interface User {
  id: number
  username: string
  nickname: string
  phone: string
  idCard: string
  role: 'USER' | 'ADMIN'
  status: number
  createTime: string
  updateTime: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string
  role: string
}

export interface RegisterForm {
  username: string
  password: string
  nickname: string
  phone: string
}

// 摊位类型
export interface StallType {
  id: number
  name: string
  description: string
  sortOrder: number
  createTime: string
  updateTime: string
}

// 摊位
export interface Stall {
  id: number
  stallNo: string
  name: string
  typeId: number
  location: string
  latitude: number
  longitude: number
  area: number
  rentPrice: number
  status: number
  description: string
  image: string
  createTime: string
  updateTime: string
  // transient
  typeName: string
}

export type StallStatus = 0 | 1 | 2 // 0-空闲 1-已租 2-维护中

// 申请
export interface Application {
  id: number
  applicationNo: string
  userId: number
  stallId: number
  startDate: string
  endDate: string
  businessType: string
  businessLicense: string
  reason: string
  status: number
  reviewOpinion: string
  reviewerId: number
  reviewTime: string
  createTime: string
  updateTime: string
  // transient
  username: string
  stallName: string
  stallNo: string
}

export type ApplicationStatus = 0 | 1 | 2 | 3 // 0-待审核 1-通过 2-拒绝 3-已取消

// 租赁
export interface Rental {
  id: number
  applicationId: number
  userId: number
  stallId: number
  startDate: string
  endDate: string
  rentAmount: number
  deposit: number
  paymentStatus: number
  status: number
  createTime: string
  updateTime: string
  // transient
  username: string
  stallName: string
  stallNo: string
}

// 卫生检查
export interface Hygiene {
  id: number
  stallId: number
  inspectorId: number
  inspectionDate: string
  score: number
  result: string
  problems: string
  suggestions: string
  images: string
  status: number
  createTime: string
  updateTime: string
  // transient
  stallName: string
  stallNo: string
  inspectorName: string
}

// 反馈
export interface Feedback {
  id: number
  userId: number
  stallId: number
  type: number
  title: string
  content: string
  images: string
  contactPhone: string
  status: number
  reply: string
  handlerId: number
  handleTime: string
  createTime: string
  updateTime: string
  // transient
  username: string
  stallName: string
  handlerName: string
}

// 公告
export interface Announcement {
  id: number
  title: string
  content: string
  type: number
  priority: number
  publisherId: number
  status: number
  publishTime: string
  createTime: string
  updateTime: string
  // transient
  publisherName: string
}
