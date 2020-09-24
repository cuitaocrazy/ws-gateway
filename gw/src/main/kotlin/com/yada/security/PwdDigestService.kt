package com.yada.security

import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Autowired

interface IPwdDigestService {
    fun getDefaultPwdDigest(username: String): String
    fun getPwdDigest(username: String, pwdPlaintext: String): String
    fun checkPwdDigest(username: String, pwdPlaintext: String, pwdDigest: String): Boolean
}

class PwdDigestService @Autowired constructor(
        private val defaultPwd: String,
        private val stringEncryptor: StringEncryptor
) : IPwdDigestService {
    override fun getDefaultPwdDigest(username: String): String = getPwdDigest(username, defaultPwd)

    // 配合迁移UAA做的改造
    override fun getPwdDigest(username: String, pwdPlaintext: String): String =
            stringEncryptor.encrypt(username + pwdPlaintext)

    override fun checkPwdDigest(username: String, pwdPlaintext: String, pwdDigest: String): Boolean =
            (username + pwdPlaintext) == stringEncryptor.decrypt(pwdDigest)
}