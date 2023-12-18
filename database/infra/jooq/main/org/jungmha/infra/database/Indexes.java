/*
 * This file is generated by jOOQ.
 */
package org.jungmha.infra.database;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jungmha.infra.database.tables.Dogwalkerreviews;
import org.jungmha.infra.database.tables.Userprofiles;


/**
 * A class modelling indexes of tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index IDX_REVIEWS_USERID = Internal.createIndex(DSL.name("idx_reviews_userid"), Dogwalkerreviews.DOGWALKERREVIEWS, new OrderField[] { Dogwalkerreviews.DOGWALKERREVIEWS.USER_ID }, false);
    public static final Index IDX_REVIEWS_WALKERID = Internal.createIndex(DSL.name("idx_reviews_walkerid"), Dogwalkerreviews.DOGWALKERREVIEWS, new OrderField[] { Dogwalkerreviews.DOGWALKERREVIEWS.WALKER_ID }, false);
    public static final Index IDX_USERS_EMAIL = Internal.createIndex(DSL.name("idx_users_email"), Userprofiles.USERPROFILES, new OrderField[] { Userprofiles.USERPROFILES.EMAIL }, false);
    public static final Index IDX_USERS_PUBLIC_KEY = Internal.createIndex(DSL.name("idx_users_public_key"), Userprofiles.USERPROFILES, new OrderField[] { Userprofiles.USERPROFILES.AUTHEN_KEY }, false);
}
