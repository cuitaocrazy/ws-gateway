package com.yada.web.handlers

data class LoginData(val username: String?, val password: String?)
data class ChangePwdData(val oldPwd: String?, val newPwd: String?)