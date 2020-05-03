package com.yada.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
open class I18nConfig {
    @Bean
    open fun messageSource(): MessageSource? {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasenames("languages/messages")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }
}