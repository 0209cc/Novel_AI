/**
 * 统一请求工具
 * 包含请求拦截、响应拦截、错误处理
 */

const BASE_URL = 'http://localhost:8080/novel'

/**
 * 请求拦截器
 */
const requestInterceptor = (config) => {
  // 添加 Token
  const token = uni.getStorageSync('token')
  if (token) {
    config.header = config.header || {}
    config.header['Authorization'] = `Bearer ${token}`
  }

  // 添加通用请求头
  config.header = config.header || {}
  config.header['Content-Type'] = config.header['Content-Type'] || 'application/json'

  return config
}

/**
 * 响应拦截器
 */
const responseInterceptor = (response) => {
  const { statusCode, data } = response

  // 请求成功
  if (statusCode >= 200 && statusCode < 300) {
    return data
  }

  // 处理错误响应
  handleErrorResponse(statusCode, data)
}

/**
 * 处理错误响应
 */
const handleErrorResponse = (statusCode, data) => {
  const errorMessage = data?.message || '请求失败'

  switch (statusCode) {
    case 400:
      // 请求参数错误
      showErrorMessage(errorMessage)
      break

    case 401:
      // 未授权 - Token 过期或无效
      handleUnauthorized()
      break

    case 403:
      // 禁止访问
      showErrorMessage('没有权限访问该资源')
      break

    case 404:
      // 资源不存在
      showErrorMessage('请求的资源不存在')
      break

    case 500:
      // 服务器内部错误
      showErrorMessage('服务器内部错误，请稍后重试')
      break

    default:
      showErrorMessage(errorMessage)
  }

  // 抛出错误
  throw new Error(errorMessage)
}

/**
 * 处理未授权
 */
const handleUnauthorized = () => {
  // 清除本地存储的 Token
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')

  // 显示提示
  uni.showModal({
    title: '登录已过期',
    content: '请重新登录',
    showCancel: false,
    success: () => {
      // 跳转到登录页
      uni.redirectTo({
        url: '/pages/login/login'
      })
    }
  })
}

/**
 * 显示错误消息
 */
const showErrorMessage = (message) => {
  uni.showToast({
    title: message,
    icon: 'none',
    duration: 3000
  })
}

/**
 * 显示成功消息
 */
const showSuccessMessage = (message) => {
  uni.showToast({
    title: message,
    icon: 'success',
    duration: 2000
  })
}

/**
 * 显示加载提示
 */
const showLoading = (message = '加载中...') => {
  uni.showLoading({
    title: message,
    mask: true
  })
}

/**
 * 隐藏加载提示
 */
const hideLoading = () => {
  uni.hideLoading()
}

/**
 * 统一请求方法
 */
const request = async (options) => {
  // 请求拦截
  const config = requestInterceptor({
    url: BASE_URL + options.url,
    method: options.method || 'GET',
    data: options.data,
    header: options.header || {}
  })

  // 显示加载提示（可选）
  if (options.showLoading !== false) {
    showLoading(options.loadingText)
  }

  try {
    // 发起请求
    const response = await new Promise((resolve, reject) => {
      uni.request({
        ...config,
        success: (res) => resolve(res),
        fail: (err) => reject(err)
      })
    })

    // 响应拦截
    const result = responseInterceptor(response)

    // 隐藏加载提示
    hideLoading()

    return result
  } catch (error) {
    // 隐藏加载提示
    hideLoading()

    // 网络错误
    if (error.errMsg?.includes('request:fail')) {
      showErrorMessage('网络连接失败，请检查网络')
    }

    throw error
  }
}

// 导出
export default request
export {
  showErrorMessage,
  showSuccessMessage,
  showLoading,
  hideLoading
}
