import React from 'react';
import classes from './CaretakerProfile.module.css'
import proImg from "../../assets/profile.jpg";

function CaretakerProfile() {
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
                                <div className="flex justify-start gap-6 mt-4">
                                    <a className="btn btn-accent px-6">Schedule</a>
                                    <a className="btn btn-accent px-6">Info</a>
                                    <a className="btn btn-accent px-6">Contact</a>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default CaretakerProfile;