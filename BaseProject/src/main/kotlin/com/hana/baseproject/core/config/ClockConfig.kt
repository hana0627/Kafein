package com.hana.baseproject.core.config

import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class ClockConfig {

    fun getClock(): Clock = Clock.systemDefaultZone()
}