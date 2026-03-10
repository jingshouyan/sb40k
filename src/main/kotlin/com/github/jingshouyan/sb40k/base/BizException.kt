package com.github.jingshouyan.sb40k.base

class BizException : RuntimeException {

    val code: Int

    var data: Any? = null

    constructor(code: Int) : super(RC.MSG_MAP[code]) {
        this.code = code
    }


    constructor(code: Int, data: Any?) : super(RC.MSG_MAP[code]) {
        this.code = code
        this.data = data
    }

    constructor(code: Int, data: Any?, cause: Throwable) : super(RC.MSG_MAP[code], cause) {
        this.code = code
        this.data = data
    }

}