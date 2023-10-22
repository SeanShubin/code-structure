package com.seanshubin.code.structure.domain

interface ErrorHandler {
    fun handleErrors(old: Errors?, current: Errors): Int
}