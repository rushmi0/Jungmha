package org.jungmha.constants

enum class Warning(val message: String) {

    AUTH_SUCCESSFUL("User [%s] Authentication successful"),

    BAD_REQUEST_USER_NOT_FOUND("User not found"),
    BAD_REQUEST_XSS_DETECTED("Cross-site scripting detected"),
    OK_UPDATE_SUCCESSFUL("Finished updating %s field"),
    BAD_REQUEST_UPDATE_FAILED("Failed to update %s field: %s"),
    OK_ALL_FIELDS_UPDATED("All fields updated successfully"),

    INVALID_TOKEN("Invalid token or insufficient permission for user: %s"),
    INVALID_TOKEN_("Invalid token or insufficient permission"),

    ERROR_PROCESSING("Error processing %s"),
    INTERNAL_SERVER_ERROR("Internal server error: %s"),

    USER_INFO_NOT_FOUND("User info not found for user: %s"),
    USER_INFO_NOT_FOUND_("User info not found"),

    ERROR_UPDATING_FIELD("Error updating field [%s] for user ID [%s] %s"),

    INVALID_FIELD("Invalid Field %S"),
    INVALID_FIELD_("Invalid Field"),

    BAD_REQUEST_XSS_DETECTED_EMAIL_PHONE("Cross-site scripting detected in email or phone number"),
    ALL_FIELDS_UPDATED("All fields updated successfully for user: %s"),
    INVALID_FIELD_UPDATE("Invalid update for field: %s")

}