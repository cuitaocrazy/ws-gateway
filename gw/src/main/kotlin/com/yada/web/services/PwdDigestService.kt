package com.yada.web.services

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

interface IPwdDigestService {
    fun getDefaultPwdDigest(username: String): String
    fun getPwdDigest(username: String, pwdPlaintext: String): String
}

@Service
class PwdDigestService @Autowired constructor(@Value("\${yada.user.defaultPwd:changepwd}") private val defaultPwd: String) : IPwdDigestService {
    override fun getDefaultPwdDigest(username: String): String = getPwdDigest(username, defaultPwd)

    override fun getPwdDigest(username: String, pwdPlaintext: String): String = Base64.encodeBase64String(DigestUtils.sha1(username + pwdPlaintext))
}