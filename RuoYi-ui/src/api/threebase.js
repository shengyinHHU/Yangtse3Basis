import request from '@/utils/request'

// 查询体系三基平台模块
export function listThreebaseModules() {
  return request({
    url: '/system/threebase/modules',
    method: 'get'
  })
}

// 查询模块下的功能清单
export function listThreebaseFeatures(moduleCode) {
  return request({
    url: `/system/threebase/modules/${moduleCode}/features`,
    method: 'get'
  })
}

// 查询首页驾驶舱统计
export function getThreebaseDashboard() {
  return request({
    url: '/system/threebase/dashboard',
    method: 'get'
  })
}

// 查询外部系统集成状态
export function listThreebaseIntegrations() {
  return request({
    url: '/system/threebase/integrations',
    method: 'get'
  })
}
// 查询功能业务记录
export function listThreebaseRecords(params) {
  return request({
    url: '/system/threebase/records',
    method: 'get',
    params
  })
}

// 查询业务记录详情
export function getThreebaseRecord(id) {
  return request({
    url: `/system/threebase/records/${id}`,
    method: 'get'
  })
}

// 新增业务记录
export function addThreebaseRecord(data) {
  return request({
    url: '/system/threebase/records',
    method: 'post',
    data
  })
}

// 修改业务记录
export function updateThreebaseRecord(id, data) {
  return request({
    url: `/system/threebase/records/${id}`,
    method: 'put',
    data
  })
}

// 删除业务记录
export function deleteThreebaseRecord(ids) {
  return request({
    url: `/system/threebase/records/${ids}`,
    method: 'delete'
  })
}

// 执行业务流程动作
export function actionThreebaseRecord(id, action) {
  return request({
    url: `/system/threebase/records/${id}/action/${action}`,
    method: 'post'
  })
}

// 查询待办事项
export function listThreebaseTodos() {
  return request({
    url: '/system/threebase/todos',
    method: 'get'
  })
}
