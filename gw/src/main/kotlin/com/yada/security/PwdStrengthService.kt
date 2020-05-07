package com.yada.security

import com.nulabinc.zxcvbn.Zxcvbn

interface IPwdStrengthService {
    /**
     * 检查密码强度
     *
     * 返回true通过，否则不通过
     */
    fun checkStrength(pwd: String): Boolean
}

class PwdStrengthService(private val pwdStrength: Int) : IPwdStrengthService {
    private val zxcvbn = Zxcvbn()

    override fun checkStrength(pwd: String): Boolean = zxcvbn.measure(pwd).score >= pwdStrength
}