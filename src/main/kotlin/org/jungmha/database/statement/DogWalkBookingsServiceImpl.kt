package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jungmha.database.field.DogWalkBookingsField
import org.jungmha.infra.database.tables.Dogwalkbookings.DOGWALKBOOKINGS
import org.jungmha.service.DogWalkBookingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalTime
import java.time.OffsetDateTime


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkBookingsServiceImpl @Inject constructor(
    private val query: DSLContext
) : DogWalkBookingsService {

    override suspend fun bookingsAll(): List<DogWalkBookingsField> {
        return withContext(Dispatchers.IO) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Retrieve bookings operation started on thread [$currentThreadName]")

                val data = query.select()
                    .from(DOGWALKBOOKINGS)

                val result = data.map { record ->
                    DogWalkBookingsField(
                        record[DOGWALKBOOKINGS.BOOKING_ID],
                        record[DOGWALKBOOKINGS.WALKER_ID],
                        record[DOGWALKBOOKINGS.USER_ID],
                        record[DOGWALKBOOKINGS.DOG_ID],
                        record[DOGWALKBOOKINGS.STATUS],
                        record[DOGWALKBOOKINGS.TIME_START],
                        record[DOGWALKBOOKINGS.TIME_END],
                        record[DOGWALKBOOKINGS.DURATION],
                        record[DOGWALKBOOKINGS.TOTAL],
                        record[DOGWALKBOOKINGS.TIMESTAMP]
                    )
                }

                LOG.info("Retrieve bookings operation successful on thread [$currentThreadName]")

                result
            } catch (e: Exception) {
                LOG.error("Error during retrieve bookings operation on thread [$currentThreadName]", e)
                emptyList()
            }
        }
    }


    override suspend fun insert(payload: DogWalkBookingsField): Boolean {
        return withContext(Dispatchers.IO) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Insert operation started on thread [$currentThreadName]")

                val result = query.insertInto(
                    DOGWALKBOOKINGS,
                    DOGWALKBOOKINGS.WALKER_ID,
                    DOGWALKBOOKINGS.USER_ID,
                    DOGWALKBOOKINGS.DOG_ID,
                    DOGWALKBOOKINGS.STATUS,
                    DOGWALKBOOKINGS.TIME_START,
                    DOGWALKBOOKINGS.TIME_END,
                    DOGWALKBOOKINGS.DURATION,
                    DOGWALKBOOKINGS.TOTAL,
                    DOGWALKBOOKINGS.TIMESTAMP
                )
                    .values(
                        payload.walkerID,
                        payload.userID,
                        payload.dogID,
                        payload.status,
                        payload.timeStart,
                        payload.timeEnd,
                        payload.duration,
                        payload.total,
                        payload.timeStamp
                    )
                    .execute()

                if (result > 0) {
                    LOG.info("Insert successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("Insert did not affect any rows on thread [$currentThreadName]")
                }

                result > 0
            } catch (e: Exception) {
                LOG.error("Error during insert operation on thread [$currentThreadName]", e)
                false
            }
        }
    }


    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val status = when (fieldName) {
                    "dogID" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.DOG_ID, Integer.valueOf(newValue))

                    "status" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.STATUS, newValue)

                    "timeStart" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TIME_START, LocalTime.parse(newValue))

                    "timeEnd" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TIME_END, LocalTime.parse(newValue))

                    "duration" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.DURATION, LocalTime.parse(newValue))

                    "total" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TOTAL, Integer.valueOf(newValue))

                    "timeStamp" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TIMESTAMP, OffsetDateTime.parse(newValue))

                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                val affectedRows = status.where(DOGWALKBOOKINGS.BOOKING_ID.eq(id)).execute()

                if (affectedRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for DogWalkBooking ID [$id]")
                } else {
                    LOG.error("Update did not affect any rows for field [$fieldName] with new value [$newValue] for DogWalkBooking ID [$id]")
                }

                return@withContext affectedRows > 0
            } catch (e: Exception) {
                LOG.error("An error occurred during update", e)
                return@withContext false
            }
        }
    }


    override suspend fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkBookingsServiceImpl::class.java)
    }

}