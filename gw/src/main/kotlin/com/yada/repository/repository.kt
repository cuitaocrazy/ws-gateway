package com.yada.repository

import com.yada.model.Org
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrgRepository : ReactiveCrudRepository<Org, String>