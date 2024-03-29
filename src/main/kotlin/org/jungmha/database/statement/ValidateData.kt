package org.jungmha.database.statement

import io.micronaut.core.annotation.Introspected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Introspected
object ValidateData {

    val LOG: Logger = LoggerFactory.getLogger(ValidateData::class.java)

    fun validateAndLogSize(fieldName: String, value: String?, maxSize: Int) {
        if (value != null && value.length > maxSize) {
            LOG.error("Field [$fieldName] has a size exceeding the limit (max: $maxSize): $value")
        }
    }


}