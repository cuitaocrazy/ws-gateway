package com.yada.security

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired

interface IPwdDigestService {
    fun getDefaultPwdDigest(username: String): String
    fun getPwdDigest(username: String, pwdPlaintext: String): String
}

class PwdDigestService @Autowired constructor(
        private val defaultPwd: String
) : IPwdDigestService {
    override fun getDefaultPwdDigest(username: String): String = getPwdDigest(username, defaultPwd)

    override fun getPwdDigest(username: String, pwdPlaintext: String): String =
            Base64.encodeBase64String(DigestUtils.sha1(username + pwdPlaintext))
}