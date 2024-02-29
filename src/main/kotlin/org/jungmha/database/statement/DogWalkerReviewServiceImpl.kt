package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jungmha.database.field.DogWalkerReviewField
import org.jungmha.database.form.DogWalkerReviewForm
import org.jungmha.infra.database.tables.Dogwalkerreviews.DOGWALKERREVIEWS
import org.jungmha.database.service.DogWalkerReviewService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class DogWalkerReviewServiceImpl @Inject constructor(
    private val query: DSLContext,
    taskDispatcher: CoroutineDispatcher?
) : DogWalkerReviewService {

    private val dispatcher: CoroutineDispatcher = taskDispatcher ?: Dispatchers.IO

    override suspend fun dogWalkerReviewAll(): List<DogWalkerReviewField> {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Retrieve dog walker reviews operation started on thread [$currentThreadName]")

                val data = query.select()
                    .from(DOGWALKERREVIEWS)

                val result = data.map { record ->
                    DogWalkerReviewField(
                        record[DOGWALKERREVIEWS.REVIEW_ID],
                        record[DOGWALKERREVIEWS.WALKER_ID],
                        record[DOGWALKERREVIEWS.USER_ID],
                        record[DOGWALKERREVIEWS.RATING],
                        record[DOGWALKERREVIEWS.REVIEW_TEXT]
                    )
                }

                if (result.isNotEmpty()) {
                    LOG.info("Retrieve dog walker reviews operation successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("No dog walker reviews found on thread [$currentThreadName]")
                }

                return@withContext result
            } catch (e: Exception) {
                LOG.error("Error during retrieve dog walker reviews operation on thread [$currentThreadName]", e)
                return@withContext emptyList()
            }
        }
    }

    override suspend fun insert(payload: DogWalkerReviewForm): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Insert dog walker review operation started on thread [$currentThreadName]")

                val result = query.insertInto(
                    DOGWALKERREVIEWS,
                    DOGWALKERREVIEWS.WALKER_ID,
                    DOGWALKERREVIEWS.USER_ID,
                    DOGWALKERREVIEWS.RATING,
                    DOGWALKERREVIEWS.REVIEW_TEXT
                )
                    .values(
                        DSL.`val`(payload.walkerID),
                        DSL.`val`(payload.userID),
                        DSL.`val`(payload.rating),
                        DSL.`val`(payload.reviewText)
                    )
                    .execute()

                if (result > 0) {
                    LOG.info("Insert dog walker review successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("Insert dog walker review did not affect any rows on thread [$currentThreadName]")
                }

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error("Error during insert dog walker review operation on thread [$currentThreadName]", e)
                return@withContext false
            }
        }
    }


    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                val affectedRows = when (fieldName) {
                    "walkerID" -> query.update(DOGWALKERREVIEWS)
                        .set(DOGWALKERREVIEWS.WALKER_ID, DSL.`val`(Integer.valueOf(newValue)))
                        .where(DOGWALKERREVIEWS.REVIEW_ID.eq(id))
                        .execute()

                    "userID" -> query.update(DOGWALKERREVIEWS)
                        .set(DOGWALKERREVIEWS.USER_ID, DSL.`val`(Integer.valueOf(newValue)))
                        .where(DOGWALKERREVIEWS.REVIEW_ID.eq(id))
                        .execute()

                    "rating" -> query.update(DOGWALKERREVIEWS)
                        .set(DOGWALKERREVIEWS.RATING, DSL.`val`(Integer.valueOf(newValue)))
                        .where(DOGWALKERREVIEWS.REVIEW_ID.eq(id))
                        .execute()

                    "reviewText" -> query.update(DOGWALKERREVIEWS)
                        .set(DOGWALKERREVIEWS.REVIEW_TEXT, DSL.`val`(newValue))
                        .where(DOGWALKERREVIEWS.REVIEW_ID.eq(id))
                        .execute()

                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                if (affectedRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for Dog Walker Review ID [$id]")
                } else {
                    LOG.error("Update did not affect any rows for field [$fieldName] with new value [$newValue] for Dog Walker Review ID [$id]")
                }

                return@withContext affectedRows > 0
            } catch (e: Exception) {
                LOG.error("An error occurred during update", e)
                return@withContext false
            }
        }
    }


    override suspend fun delete(id: Int): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Delete dog walker review operation started on thread [$currentThreadName]")

                val result = query.deleteFrom(DOGWALKERREVIEWS)
                    .where(DOGWALKERREVIEWS.REVIEW_ID.eq(DSL.`val`(id))) // ใช้ DSL.`val` เพื่อทำเป็น bind parameter
                    .execute()

                if (result > 0) {
                    LOG.info("Delete dog walker review successful for Dog Walker Review ID [$id] on thread [$currentThreadName]")
                } else {
                    LOG.warn("Delete dog walker review did not affect any rows for Dog Walker Review ID [$id] on thread [$currentThreadName]")
                }

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error(
                    "Error during delete dog walker review operation for Dog Walker Review ID [$id] on thread [$currentThreadName]",
                    e
                )
                return@withContext false
            }
        }
    }


    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkerReviewServiceImpl::class.java)
    }

}
