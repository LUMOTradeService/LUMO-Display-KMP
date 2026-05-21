package com.lumopos.display.screen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform