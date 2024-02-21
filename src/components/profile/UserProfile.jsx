import React, {useEffect, useState} from 'react';
import classes from './UserProfile.module.css'
import defaultImg from "../../assets/user.svg"
import {BASE_URL} from "../../../constants/BaseEndpoint.js";
import chaCha20 from "../../../utils/ChaCha20.js";
import EllipticCurve from "../../../utils/SecureKey.js";
import axios from "axios";
function UserProfile() {
    // const [data, setData] = useState([]);
    // const img = defaultImg;
    // const token = JSON.parse(localStorage.getItem("user-token"));
    // const viewToken = token.token.view;
    // const base_url = BASE_URL["baseEndpoint"];
    // const url = base_url + "api/v1/auth/user/normal";
    //
    // const priKey = localStorage.getItem("private-key");
    // const serverPubKey = token.serverPublicKey.publicKey;
    // console.log("priKey: ",priKey);
    // console.log("ServPub: ", serverPubKey);
    //
    //
    // const ec = EllipticCurve();
    // const cha = chaCha20();
    // const sharedKey = ec.calculateSharedKey(
    //     priKey,
    //     serverPubKey
    // )
    //
    //
    // console.log("sharedKey: ",sharedKey.toString("hex"));
    //
    // const header = {
    //     "Access-Token" : viewToken
    // }
    // let enData = "";
    //
    // useEffect(() => {
    //     axios.get(url, {
    //         headers:header
    //     }).then((res) => {
    //         enData = res.data.content;
    //         console.log("Encrypt Data: ", enData);
    //         let decryptData = cha.decrypt(enData, sharedKey);
    //         setData(decryptData);
    //         console.log("Data 1: ",res.data.content);
    //     }).catch((err) => {
    //         if(err.response.status === 400) {
    //             alert("Please login!");
    //         } else {
    //             console.error(err);
    //         }
    //     });
    //
    // }, []);








    return (
        <>

            <div className={classes.bannerProfile}>
                <div className={classes.profile}>
                    <div className={classes.imgGrid}>
                        <img className={classes.profileImg} src={img}/>
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