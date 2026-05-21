package com.lumopos.display

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform