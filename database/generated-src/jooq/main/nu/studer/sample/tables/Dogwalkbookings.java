/*
 * This file is generated by jOOQ.
 */
package nu.studer.sample.tables;


import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import nu.studer.sample.Keys;
import nu.studer.sample.Public;
import nu.studer.sample.tables.records.DogwalkbookingsRecord;

import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function10;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row10;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Dogwalkbookings extends TableImpl<DogwalkbookingsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.dogwalkbookings</code>
     */
    public static final Dogwalkbookings DOGWALKBOOKINGS = new Dogwalkbookings();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DogwalkbookingsRecord> getRecordType() {
        return DogwalkbookingsRecord.class;
    }

    /**
     * The column <code>public.dogwalkbookings.booking_id</code>.
     */
    public final TableField<DogwalkbookingsRecord, Integer> BOOKING_ID = createField(DSL.name("booking_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.dogwalkbookings.walker_id</code>.
     */
    public final TableField<DogwalkbookingsRecord, Integer> WALKER_ID = createField(DSL.name("walker_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.dogwalkbookings.user_id</code>.
     */
    public final TableField<DogwalkbookingsRecord, Integer> USER_ID = createField(DSL.name("user_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.dogwalkbookings.dog_id</code>.
     */
    public final TableField<DogwalkbookingsRecord, Integer> DOG_ID = createField(DSL.name("dog_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.dogwalkbookings.status</code>.
     */
    public final TableField<DogwalkbookingsRecord, String> STATUS = createField(DSL.name("status"), SQLDataType.VARCHAR(255).nullable(false).defaultValue(DSL.field(DSL.raw("'Pending'::character varying"), SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>public.dogwalkbookings.time_start</code>.
     */
    public final TableField<DogwalkbookingsRecord, LocalTime> TIME_START = createField(DSL.name("time_start"), SQLDataType.LOCALTIME(6), this, "");

    /**
     * The column <code>public.dogwalkbookings.time_end</code>.
     */
    public final TableField<DogwalkbookingsRecord, LocalTime> TIME_END = createField(DSL.name("time_end"), SQLDataType.LOCALTIME(6), this, "");

    /**
     * The column <code>public.dogwalkbookings.duration</code>.
     */
    public final TableField<DogwalkbookingsRecord, LocalTime> DURATION = createField(DSL.name("duration"), SQLDataType.LOCALTIME(6), this, "");

    /**
     * The column <code>public.dogwalkbookings.timestamp</code>.
     */
    public final TableField<DogwalkbookingsRecord, OffsetDateTime> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).defaultValue(DSL.field(DSL.raw("now()"), SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "");

    /**
     * The column <code>public.dogwalkbookings.total</code>.
     */
    public final TableField<DogwalkbookingsRecord, Integer> TOTAL = createField(DSL.name("total"), SQLDataType.INTEGER, this, "");

    private Dogwalkbookings(Name alias, Table<DogwalkbookingsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dogwalkbookings(Name alias, Table<DogwalkbookingsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.dogwalkbookings</code> table reference
     */
    public Dogwalkbookings(String alias) {
        this(DSL.name(alias), DOGWALKBOOKINGS);
    }

    /**
     * Create an aliased <code>public.dogwalkbookings</code> table reference
     */
    public Dogwalkbookings(Name alias) {
        this(alias, DOGWALKBOOKINGS);
    }

    /**
     * Create a <code>public.dogwalkbookings</code> table reference
     */
    public Dogwalkbookings() {
        this(DSL.name("dogwalkbookings"), null);
    }

    public <O extends Record> Dogwalkbookings(Table<O> child, ForeignKey<O, DogwalkbookingsRecord> key) {
        super(child, key, DOGWALKBOOKINGS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<DogwalkbookingsRecord, Integer> getIdentity() {
        return (Identity<DogwalkbookingsRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<DogwalkbookingsRecord> getPrimaryKey() {
        return Keys.DOGWALKBOOKINGS_PKEY;
    }

    @Override
    public List<ForeignKey<DogwalkbookingsRecord, ?>> getReferences() {
        return Arrays.asList(Keys.DOGWALKBOOKINGS__DOGWALKBOOKINGS_WALKER_ID_FKEY, Keys.DOGWALKBOOKINGS__DOGWALKBOOKINGS_USER_ID_FKEY, Keys.DOGWALKBOOKINGS__DOGWALKBOOKINGS_DOG_ID_FKEY);
    }

    private transient Dogwalkers _dogwalkers;
    private transient Userprofiles _userprofiles;
    private transient Dogs _dogs;

    /**
     * Get the implicit join path to the <code>public.dogwalkers</code> table.
     */
    public Dogwalkers dogwalkers() {
        if (_dogwalkers == null)
            _dogwalkers = new Dogwalkers(this, Keys.DOGWALKBOOKINGS__DOGWALKBOOKINGS_WALKER_ID_FKEY);

        return _dogwalkers;
    }

    /**
     * Get the implicit join path to the <code>public.userprofiles</code> table.
     */
    public Userprofiles userprofiles() {
        if (_userprofiles == null)
            _userprofiles = new Userprofiles(this, Keys.DOGWALKBOOKINGS__DOGWALKBOOKINGS_USER_ID_FKEY);

        return _userprofiles;
    }

    /**
     * Get the implicit join path to the <code>public.dogs</code> table.
     */
    public Dogs dogs() {
        if (_dogs == null)
            _dogs = new Dogs(this, Keys.DOGWALKBOOKINGS__DOGWALKBOOKINGS_DOG_ID_FKEY);

        return _dogs;
    }

    @Override
    public List<Check<DogwalkbookingsRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("dogwalkbookings_status_check"), "(((status)::text = ANY ((ARRAY['Confirm'::character varying, 'Cancel'::character varying, 'Pending'::character varying])::text[])))", true)
        );
    }

    @Override
    public Dogwalkbookings as(String alias) {
        return new Dogwalkbookings(DSL.name(alias), this);
    }

    @Override
    public Dogwalkbookings as(Name alias) {
        return new Dogwalkbookings(alias, this);
    }

    @Override
    public Dogwalkbookings as(Table<?> alias) {
        return new Dogwalkbookings(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dogwalkbookings rename(String name) {
        return new Dogwalkbookings(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dogwalkbookings rename(Name name) {
        return new Dogwalkbookings(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dogwalkbookings rename(Table<?> name) {
        return new Dogwalkbookings(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Integer, Integer, Integer, Integer, String, LocalTime, LocalTime, LocalTime, OffsetDateTime, Integer> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function10<? super Integer, ? super Integer, ? super Integer, ? super Integer, ? super String, ? super LocalTime, ? super LocalTime, ? super LocalTime, ? super OffsetDateTime, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function10<? super Integer, ? super Integer, ? super Integer, ? super Integer, ? super String, ? super LocalTime, ? super LocalTime, ? super LocalTime, ? super OffsetDateTime, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
