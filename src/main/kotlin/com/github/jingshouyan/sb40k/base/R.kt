package com.github.jingshouyan.sb40k.base

class R(
    val code: Int,
    val msg: String?,
    val data: Any?
) {
    fun success(): Boolean {
        return code == RC.SUCCESS
    }


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
    const val SERVER_ERROR = 500
    const val PARAM_INVALID = 1001
    const val PARAM_UNBIND = 1002
    const val PARAM_ERROR = 1003
    const val ALREADY_EXISTS = 1004

    const val PASSWORD_INCORRECT = 10001
    const val USER_LOCKED = 10002

    val MSG_MAP = mapOf(
        SUCCESS to "success",
        ERROR to "error",
        NOT_FOUND to "not found",
        SERVER_ERROR to "server error",
        PARAM_INVALID to "param invalid",
        PARAM_UNBIND to "param unbind",
        PARAM_ERROR to "param error",
        ALREADY_EXISTS to "already exists",

        PASSWORD_INCORRECT to "password incorrect",
        USER_LOCKED to "user locked",

        )

}