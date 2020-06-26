package com.yada.gateways

import com.yada.security.UserInfo

val UserInfo.orgId: String
    get() = this.profiles.getOrDefault("orgId", "")