package com.phunguyen.stackoverflowuser.util

import com.phunguyen.stackoverflowuser.AppExecutors

import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}
