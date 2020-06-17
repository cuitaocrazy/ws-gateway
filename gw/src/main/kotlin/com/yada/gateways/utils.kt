package com.yada.gateways

import com.yada.sc2.UserInfo

val UserInfo.orgId: String
    get() = this.profiles.getOrDefault("orgId", "")