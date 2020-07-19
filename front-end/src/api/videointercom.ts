import request from '../util/request'

// 获取入场人员照片
export function getAdmissionPersonnel() {
    return request({
        url: 'get_admission_personnel',
        method: 'get'
    })
}

// 获取识别失败照片
export function getRecognitionFailed() {
    return request({
        url: 'get_recognition_failed',
        method: 'get'
    })
}

// 获取人员管理
export function getPersonnelManagement() {
    return request({
        url: 'get_personnel_management',
        method: 'get'
    })
}