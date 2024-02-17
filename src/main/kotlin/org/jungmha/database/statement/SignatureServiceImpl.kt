package org.jungmha.database.statement

import io.micronaut.core.annotation.Introspected
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jungmha.database.field.SignatureField
import org.jungmha.database.form.SignatureForm
import org.jungmha.database.service.SignatureService
import org.jungmha.infra.database.Tables.SIGNATURE
import org.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Introspected
class SignatureServiceImpl @Inject constructor(
    private val query: DSLContext,
    taskDispatcher: CoroutineDispatcher?
) : SignatureService {

    private val dispatcher: CoroutineDispatcher = taskDispatcher ?: Dispatchers.IO

    override suspend fun signAll(): List<SignatureField> {
        TODO("Not yet implemented")
    }

    override suspend fun checkSign(userName: String, signature: String): Boolean {
        return withContext(dispatcher) {
            try {
                val st = SIGNATURE
                val u = USERPROFILES

                val records = query.select(
                    st.SIGNATURE_
                )
                    .from(st)
                    .join(u)
                    .on(u.USER_ID.eq(st.USER_ID))
                    .where(u.USERNAME.eq(userName).and(st.SIGNATURE_.eq(DSL.`val`(signature))))
                    .fetch()


                LOG.info("\n$records")

                // ตรวจสอบว่าไม่มี signature ใดที่ signature ตรงกับที่รับเข้ามา
                return@withContext records.none()
            } catch (e: Exception) {
                LOG.error("Error checking signature for user: $userName", e)
                false
            }
        }
    }


    override suspend fun insert(payload: SignatureForm): Boolean {
        return withContext(dispatcher) {
            try {
                val record = query.insertInto(
                    SIGNATURE,
                    SIGNATURE.USER_ID,
                    SIGNATURE.SIGNATURE_
                )
                    .values(
                        DSL.`val`(payload.userID),
                        DSL.`val`(payload.signature)
                    )

                val result = record.execute()

                val success = result > 0

                if (success) {
                    LOG.info("Insert Signature successful for User ID: ${payload.userID}")
                    LOG.info("\n$record")
                } else {
                    LOG.warn("No rows inserted for User ID: ${payload.userID}")
                }

                success
            } catch (e: Exception) {
                LOG.error("Error inserting Signature: $e")
                false
            }
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(SignatureServiceImpl::class.java)
    }


}