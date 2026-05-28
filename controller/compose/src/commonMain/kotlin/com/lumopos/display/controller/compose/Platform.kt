package com.lumopos.display.controller.compose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform