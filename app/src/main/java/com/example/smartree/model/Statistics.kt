package com.example.smartree.model

data class Statistics (
    var total: Int = 0,
    var healthy: Int = 0,
    var warning: Int = 0,
    val danger: Int = 0
)
