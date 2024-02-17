package org.jungmha.constants

import io.micronaut.core.annotation.Introspected

@Introspected
interface EnumField {
    val fieldName: String
}