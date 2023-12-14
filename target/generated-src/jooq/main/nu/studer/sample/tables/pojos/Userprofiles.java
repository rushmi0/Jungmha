/*
 * This file is generated by jOOQ.
 */
package nu.studer.sample.tables.pojos;


import java.io.Serializable;
import java.time.OffsetDateTime;

import nu.studer.sample.enums.UserType;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Userprofiles implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer userId;
    private final String imageProfile;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final String authenKey;
    private final OffsetDateTime createdAt;
    private final UserType userType;

    public Userprofiles(Userprofiles value) {
        this.userId = value.userId;
        this.imageProfile = value.imageProfile;
        this.username = value.username;
        this.firstName = value.firstName;
        this.lastName = value.lastName;
        this.email = value.email;
        this.phoneNumber = value.phoneNumber;
        this.authenKey = value.authenKey;
        this.createdAt = value.createdAt;
        this.userType = value.userType;
    }

    public Userprofiles(
        Integer userId,
        String imageProfile,
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String authenKey,
        OffsetDateTime createdAt,
        UserType userType
    ) {
        this.userId = userId;
        this.imageProfile = imageProfile;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.authenKey = authenKey;
        this.createdAt = createdAt;
        this.userType = userType;
    }

    /**
     * Getter for <code>public.userprofiles.user_id</code>.
     */
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * Getter for <code>public.userprofiles.image_profile</code>.
     */
    public String getImageProfile() {
        return this.imageProfile;
    }

    /**
     * Getter for <code>public.userprofiles.username</code>.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Getter for <code>public.userprofiles.first_name</code>.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Getter for <code>public.userprofiles.last_name</code>.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Getter for <code>public.userprofiles.email</code>.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Getter for <code>public.userprofiles.phone_number</code>.
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Getter for <code>public.userprofiles.authen_key</code>.
     */
    public String getAuthenKey() {
        return this.authenKey;
    }

    /**
     * Getter for <code>public.userprofiles.created_at</code>.
     */
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Getter for <code>public.userprofiles.user_type</code>.
     */
    public UserType getUserType() {
        return this.userType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Userprofiles other = (Userprofiles) obj;
        if (this.userId == null) {
            if (other.userId != null)
                return false;
        }
        else if (!this.userId.equals(other.userId))
            return false;
        if (this.imageProfile == null) {
            if (other.imageProfile != null)
                return false;
        }
        else if (!this.imageProfile.equals(other.imageProfile))
            return false;
        if (this.username == null) {
            if (other.username != null)
                return false;
        }
        else if (!this.username.equals(other.username))
            return false;
        if (this.firstName == null) {
            if (other.firstName != null)
                return false;
        }
        else if (!this.firstName.equals(other.firstName))
            return false;
        if (this.lastName == null) {
            if (other.lastName != null)
                return false;
        }
        else if (!this.lastName.equals(other.lastName))
            return false;
        if (this.email == null) {
            if (other.email != null)
                return false;
        }
        else if (!this.email.equals(other.email))
            return false;
        if (this.phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        }
        else if (!this.phoneNumber.equals(other.phoneNumber))
            return false;
        if (this.authenKey == null) {
            if (other.authenKey != null)
                return false;
        }
        else if (!this.authenKey.equals(other.authenKey))
            return false;
        if (this.createdAt == null) {
            if (other.createdAt != null)
                return false;
        }
        else if (!this.createdAt.equals(other.createdAt))
            return false;
        if (this.userType == null) {
            if (other.userType != null)
                return false;
        }
        else if (!this.userType.equals(other.userType))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
        result = prime * result + ((this.imageProfile == null) ? 0 : this.imageProfile.hashCode());
        result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
        result = prime * result + ((this.firstName == null) ? 0 : this.firstName.hashCode());
        result = prime * result + ((this.lastName == null) ? 0 : this.lastName.hashCode());
        result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
        result = prime * result + ((this.phoneNumber == null) ? 0 : this.phoneNumber.hashCode());
        result = prime * result + ((this.authenKey == null) ? 0 : this.authenKey.hashCode());
        result = prime * result + ((this.createdAt == null) ? 0 : this.createdAt.hashCode());
        result = prime * result + ((this.userType == null) ? 0 : this.userType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Userprofiles (");

        sb.append(userId);
        sb.append(", ").append(imageProfile);
        sb.append(", ").append(username);
        sb.append(", ").append(firstName);
        sb.append(", ").append(lastName);
        sb.append(", ").append(email);
        sb.append(", ").append(phoneNumber);
        sb.append(", ").append(authenKey);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(userType);

        sb.append(")");
        return sb.toString();
    }
}
