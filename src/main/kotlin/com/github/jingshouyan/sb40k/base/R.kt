package com.github.jingshouyan.sb40k.base

class R(
    val code: Int,
    val msg: String?,
    val data: Any?
) {


    companion object {

        fun success(): R {
            return success(null)
        }

        fun success(data: Any?): R {
            return error(RC.SUCCESS, data)
        }

        fun error(code: Int): R {
            return error(code, null)
        }

        fun error(code: Int, data: Any?): R {
            return R(code, RC.MSG_MAP[code], data)
        }
    }
}

object RC {
    const val SUCCESS = 0
    const val ERROR = -1
    const val NOT_FOUND = 404

    const val PASSWORD_INCORRECT = 10001
    const val USER_LOCKED = 10002

    val MSG_MAP = mapOf(
        RC.SUCCESS to "success",
        RC.ERROR to "error",
        RC.NOT_FOUND to "not found"
    )

}