package com.ucb.bo.sktmsminio.dto
class ResponseDto<T>(
    val data: T?,
    val message: String?,
    val success: Boolean
){
}