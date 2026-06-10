/**
 * 认证相关 API 接口（使用统一请求工具）
 */

import request from '@/utils/request'

/**
 * 发送短信验证码
 * @param {string} phone 手机号
 */
export const sendCode = (data) => {
  return request({
    url: '/auth/send-code',
    method: 'POST',
    data: data,
    loadingText: '发送中...'
  })
}

/**
 * 用户注册
 * @param {object} data 注册信息
 */
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'POST',
    data: data,
    loadingText: '注册中...'
  })
}

/**
 * 用户登录
 * @param {object} data 登录信息
 */
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'POST',
    data: data,
    loadingText: '登录中...'
  })
}

/**
 * 获取当前用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/users/me',
    method: 'GET'
  })
}
