import React from 'react';
import classes from './UserProfile.module.css'
import proImg from '../../assets/profile.jpg'
//
function UserProfile() {
    return (
        <>

            <div className={classes.bannerProfile}>
                <div className={classes.profile}>
                    <div className={classes.imgGrid}>
                        <img className={classes.profileImg} src={proImg}/>
                    </div>
                    <div className={classes.profileInfo}>
                        <div className={classes.profileInfoGrid}>
                            <div className={classes.editBtnFlex}>
                                <a className={classes.editBtn}>Edit Profile</a>
                            </div>
                            <div className={classes.nameFlex}>
                                <p className={classes.nameText}>Albus Doubledoor</p>
                                <p className={classes.locationText}>Bangkok, pattanakarn 32</p>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default UserProfile;