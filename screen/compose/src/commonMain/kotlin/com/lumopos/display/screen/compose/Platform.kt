package com.lumopos.display.screen.compose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform