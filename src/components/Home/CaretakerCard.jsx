import React , { useState, useEffect} from 'react';
import profile from "../../assets/profile.jpg";
import classes from "./CaretakerCard.module.css";
import axios from "axios";
function CaretakerCard() {

    const [data, setData] = useState([]);
    useEffect(() => {
        axios.get("http://localhost:8080/api/v1/auth/user/dogwalkers",
            { headers: { "Access-Token": "eyJ1c2VyTmFtZSI6InVzZXJUZXN0MDM3IiwicGVybWlzc2lvbiI6InZpZXciLCJleHAiOjE3MDc5MzMyOTc1NzksImlhdCI6MTcwNzkzMDcwNTU3OSwic2lnbmF0dXJlIjoiMzA0NDAyMjA0MzIyNzg1YTA3YTUzMGEwZWE1ODVkMDQzMzczMGRlNWEwZjU1NjEwOTk1OWJjODQwOTU0ZmU1MzhjZTIxMmI2MDIyMDI2MmNmMjI4YTJjM2RlY2YzZmNhNDg1NDYyMzc5MzlhZjc5ZThkMjgwZmE5NmY1NGY4OWU3ZDMwZWMwNTA2NGUifQ==" } }
        ).then((res) => {
            setData(res.data);
        });
    }, []);

    return (
        <>
            <div className="bg-[#DBF3FF] p-14">
                <div className={classes.cardGrid}>
                    {data.map((careTakerInfo) => (
                        <div key={careTakerInfo.UserId} className={classes.cardBg}>
                            <div className={classes.cardContent}>
                                <div className={classes.cardImg}>
                                    <div className={classes.cardImgGrid}>
                                        <img src={careTakerInfo.profileImage}
                                             className={classes.userImg}
                                             alt="profile"/>

                                        <p className={classes.rateHeader}>Rating</p>

                                        <p className={classes.rateHeader}>Activity</p>
                                        <div className="rating">
                                            <input type="radio" name="rating-2"
                                                   className="mask mask-star-2 bg-orange-400"/>
                                            <input type="radio" name="rating-2"
                                                   className="mask mask-star-2 bg-orange-400"
                                            />
                                            <input type="radio" name="rating-2"
                                                   className="mask mask-star-2 bg-orange-400"/>
                                            <input type="radio" name="rating-2"
                                                   className="mask mask-star-2 bg-orange-400"/>
                                            <input type="radio" name="rating-2"
                                                   className="mask mask-star-2 bg-orange-400"/>
                                        </div>
                                        <p className="text-accent mx-auto text-xl">90%</p>
                                    </div>
                                </div>
                                <div
                                    className={classes.cardDetails}>
                                    <p className="pb-2 text-xl"> <p>{}</p> Yaranika</p>
                                    <div className={classes.cardDetailsGrid}>
                                        <p className={classes.infoHeader}>Email</p>
                                        <p className={classes.info}>Kawasaki@gmail.com</p>
                                        <p className={classes.infoHeader}>Province</p>
                                        <p className={classes.info}>Bangkok</p>
                                        <p className={classes.infoHeader}>Phone</p>
                                        <p className={classes.info}>(+66)642152232</p>
                                        <p className={classes.infoHeader}>Price</p>
                                        <p className={classes.info}>120/hr</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}


                </div>
                {/*end grid card*/}


            </div>


        </>
    );
}

export default CaretakerCard;