package org.jungmha.constants

object Waring {

    val AUTH_SUCCESSFUL = "User [%s] Authentication successful"

    val BAD_REQUEST_USER_NOT_FOUND = "User not found"
    val BAD_REQUEST_XSS_DETECTED = "Cross-site scripting detected"
    val OK_UPDATE_SUCCESSFUL = "Finished updating %s field"
    val BAD_REQUEST_UPDATE_FAILED = "Failed to update %s field: %s"
    val OK_ALL_FIELDS_UPDATED = "All fields updated successfully"

    val INVALID_TOKEN = "Invalid token or insufficient permission for user: %s"
    val INVALID_TOKEN_ = "Invalid token or insufficient permission"

    val ERROR_PROCESSING = "Error processing %s"
    val INTERNAL_SERVER_ERROR = "Internal server error: %s"

    val USER_INFO_NOT_FOUND = "User info not found for user: %s"
    val USER_INFO_NOT_FOUND_ = "User info not found"

    val ERROR_UPDATING_FIELD = "Error updating field [%s] for user ID [%s] %s"


    val INVALID_FIELD = "Invalid Field %S"
    val INVALID_FIELD_ = "Invalid Field"

    val THREAD_STACK_TRACE = "Executing ${Thread.currentThread().stackTrace[1]}"
}