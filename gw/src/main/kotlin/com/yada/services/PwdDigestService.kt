package com.yada.services

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service

interface IPwdDigestService {
    fun getDefaultPwdDigest(username: String): String
    fun getPwdDigest(username: String, pwdPlaintext: String): String
}

@Service
class PwdDigestService : IPwdDigestService {
    override fun getDefaultPwdDigest(username: String): String = getPwdDigest(username, "changepwd")

    override fun getPwdDigest(username: String, pwdPlaintext: String): String = Base64.encodeBase64String(DigestUtils.sha1(username + pwdPlaintext))
}