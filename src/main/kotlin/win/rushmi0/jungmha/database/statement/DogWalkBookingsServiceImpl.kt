package win.rushmi0.jungmha.database.statement

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
import win.rushmi0.jungmha.database.field.DogWalkBookingsField
import win.rushmi0.jungmha.database.record.DogWalkBookings
import win.rushmi0.jungmha.database.service.DogWalkBookingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import win.rushmi0.jungmha.infra.database.tables.Dogwalkbookings.DOGWALKBOOKINGS
import java.time.LocalTime
import java.time.OffsetDateTime


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class DogWalkBookingsServiceImpl @Inject constructor(
    private val query: DSLContext,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DogWalkBookingsService {


    override suspend fun bookingsAll(): List<DogWalkBookingsField> {
        return withContext(dispatcher) {
            try {
                LOG.info("Retrieve bookings operation started on thread [${Thread.currentThread().name}]")

                val data = query.select()
                    .from(DOGWALKBOOKINGS)

                /**
                 * SELECT *
                 * FROM dogwalkbookings;
                 */
                LOG.info("\n${data.fetch()}")

                val result = data.map { record ->
                    DogWalkBookingsField(
                        record[DOGWALKBOOKINGS.BOOKING_ID],
                        record[DOGWALKBOOKINGS.WALKER_ID],
                        record[DOGWALKBOOKINGS.USER_ID],
                        record[DOGWALKBOOKINGS.DOG_ID],
                        record[DOGWALKBOOKINGS.STATUS],
                        record[DOGWALKBOOKINGS.BOOKING_DATE],
                        record[DOGWALKBOOKINGS.TIME_START],
                        record[DOGWALKBOOKINGS.TIME_END],
                        record[DOGWALKBOOKINGS.DURATION],
                        record[DOGWALKBOOKINGS.TOTAL],
                        record[DOGWALKBOOKINGS.TIMESTAMP]
                    )
                }

                if (result.isNotEmpty()) {
                    LOG.info("Retrieve bookings operation successful")
                } else {
                    LOG.warn("No bookings found")
                }

                return@withContext result
            } catch (e: Exception) {
                LOG.error("Error during retrieve bookings operation", e)
                emptyList()
            }
        }
    }



    override suspend fun insert(userID: Int, payload: DogWalkBookings): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Insert operation started")

                /**
                 * INSERT INTO dogwalkbookings
                 * (walker_id, user_id, dog_id, booking_date, time_start, time_end)
                 * VALUES
                 * (:payload.walkerID, :userID, :payload.dogID, :payload.bookingDate, :payload.timeStart, :payload.timeEnd);
                 */
                val result = query.insertInto(
                    DOGWALKBOOKINGS,
                    DOGWALKBOOKINGS.WALKER_ID,
                    DOGWALKBOOKINGS.USER_ID,
                    DOGWALKBOOKINGS.DOG_ID,
                    DOGWALKBOOKINGS.BOOKING_DATE,
                    DOGWALKBOOKINGS.TIME_START,
                    DOGWALKBOOKINGS.TIME_END,
                )
                    .values(
                        DSL.`val`(payload.walkerID),
                        DSL.`val`(userID),
                        DSL.`val`(payload.dogID),
                        DSL.`val`(payload.bookingDate),
                        DSL.`val`(payload.timeStart),
                        DSL.`val`(payload.timeEnd),
                    )
                    .execute()

                if (result > 0) {
                    LOG.info("Insert successful")
                } else {
                    LOG.warn("Insert did not affect any rows")
                }

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error("Error during insert operation", e)
                false
            }
        }
    }



    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                val updateQuery = when (fieldName) {
                    "dogID" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.DOG_ID, DSL.`val`(Integer.valueOf(newValue)))

                    "status" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.STATUS, DSL.`val`(newValue))

                    "timeStart" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TIME_START, DSL.`val`(LocalTime.parse(newValue)))

                    "timeEnd" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TIME_END, DSL.`val`(LocalTime.parse(newValue)))

                    "duration" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.DURATION, DSL.`val`(LocalTime.parse(newValue)))

                    "total" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TOTAL, DSL.`val`(Integer.valueOf(newValue)))

                    "timeStamp" -> query.update(DOGWALKBOOKINGS)
                        .set(DOGWALKBOOKINGS.TIMESTAMP, DSL.`val`(OffsetDateTime.parse(newValue)))

                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                val affectedRows = updateQuery.where(DOGWALKBOOKINGS.BOOKING_ID.eq(id)).execute()

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
        return withContext(dispatcher) {
            val stackTrace = Thread.currentThread().stackTrace
            try {
                LOG.info("Delete operation started from [$stackTrace]")

                /**
                 * DELETE FROM dogwalkbookings
                 * WHERE booking_id = :id;
                 */
                val deletedRows = query.deleteFrom(DOGWALKBOOKINGS)
                    .where(DOGWALKBOOKINGS.BOOKING_ID.eq(DSL.`val`(id)))
                    .execute()

                if (deletedRows > 0) {
                    LOG.info("Delete successful for booking with ID [$id]")
                } else {
                    LOG.warn("Delete did not affect any rows for booking with ID [$id] from [$stackTrace]")
                }

                return@withContext deletedRows > 0
            } catch (e: Exception) {
                LOG.error("Error during delete operation for booking with ID [$id] from [$stackTrace]", e)
                false
            }
        }
    }



    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkBookingsServiceImpl::class.java)
    }

}