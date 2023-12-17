/*
 * This file is generated by jOOQ.
 */
package org.jungmha.infra.database.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
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
import org.jungmha.infra.database.Keys;
import org.jungmha.infra.database.Public;
import org.jungmha.infra.database.tables.records.DogsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Dogs extends TableImpl<DogsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.dogs</code>
     */
    public static final Dogs DOGS = new Dogs();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DogsRecord> getRecordType() {
        return DogsRecord.class;
    }

    /**
     * The column <code>public.dogs.dog_id</code>.
     */
    public final TableField<DogsRecord, Integer> DOG_ID = createField(DSL.name("dog_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.dogs.dog_image</code>.
     */
    public final TableField<DogsRecord, String> DOG_IMAGE = createField(DSL.name("dog_image"), SQLDataType.VARCHAR(255).nullable(false).defaultValue(DSL.field(DSL.raw("'N/A'::character varying"), SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>public.dogs.breed_name</code>.
     */
    public final TableField<DogsRecord, String> BREED_NAME = createField(DSL.name("breed_name"), SQLDataType.VARCHAR(255).nullable(false).defaultValue(DSL.field(DSL.raw("'N/A'::character varying"), SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>public.dogs.size</code>.
     */
    public final TableField<DogsRecord, String> SIZE = createField(DSL.name("size"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    private Dogs(Name alias, Table<DogsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dogs(Name alias, Table<DogsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.dogs</code> table reference
     */
    public Dogs(String alias) {
        this(DSL.name(alias), DOGS);
    }

    /**
     * Create an aliased <code>public.dogs</code> table reference
     */
    public Dogs(Name alias) {
        this(alias, DOGS);
    }

    /**
     * Create a <code>public.dogs</code> table reference
     */
    public Dogs() {
        this(DSL.name("dogs"), null);
    }

    public <O extends Record> Dogs(Table<O> child, ForeignKey<O, DogsRecord> key) {
        super(child, key, DOGS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<DogsRecord, Integer> getIdentity() {
        return (Identity<DogsRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<DogsRecord> getPrimaryKey() {
        return Keys.DOGS_PKEY;
    }

    @Override
    public List<Check<DogsRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("dogs_size_check"), "(((size)::text = ANY ((ARRAY['Small'::character varying, 'Medium'::character varying, 'Big'::character varying])::text[])))", true)
        );
    }

    @Override
    public Dogs as(String alias) {
        return new Dogs(DSL.name(alias), this);
    }

    @Override
    public Dogs as(Name alias) {
        return new Dogs(alias, this);
    }

    @Override
    public Dogs as(Table<?> alias) {
        return new Dogs(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dogs rename(String name) {
        return new Dogs(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dogs rename(Name name) {
        return new Dogs(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dogs rename(Table<?> name) {
        return new Dogs(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super Integer, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super Integer, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}