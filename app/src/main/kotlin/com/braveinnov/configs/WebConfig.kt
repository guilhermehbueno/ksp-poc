package com.braveinnov.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setIncludeHeaders(false)
        loggingFilter.setMaxPayloadLength(10000)
        loggingFilter.setAfterMessagePrefix("REQUEST DATA : ")
        return loggingFilter
    }
}