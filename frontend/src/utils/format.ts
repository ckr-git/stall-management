import dayjs from 'dayjs'

export const formatDateTime = (date: string | Date | undefined | null): string => {
  if (!date) return '-'
  const d = dayjs(date)
  return d.isValid() ? d.format('YYYY-MM-DD HH:mm') : '-'
}

export const formatDate = (date: string | Date | undefined | null): string => {
  if (!date) return '-'
  const d = dayjs(date)
  return d.isValid() ? d.format('YYYY-MM-DD') : '-'
}
