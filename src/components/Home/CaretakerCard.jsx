import React , { useState, useEffect} from 'react';
import classes from "./CaretakerCard.module.css";
import { FaRegCheckCircle } from "react-icons/fa";
import axios from "axios";


function CaretakerCard({name, price, location, verify}) {

    console.log(name);
    console.log(price);
    console.log(location);
    console.log(verify);
    const [data, setData] = useState([]);
    const [isLoad, setLoad] = useState(false);
    const loadData = async () => {
        setLoad(true);
        await axios.get("http://localhost:8080/api/v1/home/filter")
            .then((res) => {
                setData(res.data);
                setLoad(false)
            }).catch((err) => console.log(err));
    }
    useEffect(() => {
        loadData();
    }, []);
    console.log(data)


    function renderStars(rating) {
        const fullStars = Math.floor(rating);
        const halfStars = Math.round(rating * 2) % 2;

        const starIcons = [];
        for (let i = 0; i < fullStars; i++) {
            starIcons.push(<input key={i}  type="radio" name="rating-2"
                   className="mask mask-star-2 bg-orange-400" checked/>);
        }
        if (halfStars) {
            starIcons.push(<input key={fullStars}  type="radio" name="rating-2"
                                  className="mask mask-half-1 bg-orange-400" checked/>);
        }
        if(rating === 0) {
            starIcons.push(<p className="text-lg text-[#8C7979]"> No rating</p>
            );
        }

        return <div className="mx-auto">{starIcons}</div>;
    }

    function renderVerify(ver) {
        const verifyIcons = [];
        if(ver === true) {
            verifyIcons.push(<p className="text-accent"><FaRegCheckCircle size={35}/></p>);
        } else {
            verifyIcons.push();
        }
        return <>{verifyIcons}</>;
    }

    return (
        <>
            <div className="bg-[#DBF3FF] p-14">
                <div className={classes.cardGrid}>
                    {!isLoad && (
                        <>
                            {data.filter((val) => {
                                if (verify === "Verify") {
                                    return val.detail.verify === true;
                                } else if (verify === "Not Verify") {
                                    return val.detail.verify === false;
                                } else if (verify === "All") {
                                    return val.detail.verify === true || val.detail.verify === false;
                                }

                            }).filter((val) => {
                                // eslint-disable-next-line react/prop-types
                                if (name.toLowerCase() === "") {
                                    return val;
                                } else {
                                    return val.detail.name.toLowerCase().includes(name);
                                }
                            }).filter((val) => {
                                if (location === val.detail.location) {
                                    return val.detail.location === location;
                                } else if (location === "Any") {
                                    return val.detail.location;
                                }
                            }).map((careTakerInfo) => (
                                <div key={careTakerInfo.walkerId} className={classes.cardBg}>
                                    <div className={classes.cardContent}>
                                        <div className={classes.cardImg}>
                                            <div className={classes.cardImgGrid}>
                                                <img src={careTakerInfo.detail.profileImage}
                                                     className={classes.userImg}
                                                     alt="profile"/>

                                                <p className={classes.rateHeader}>Rating</p>

                                                <p className={classes.rateHeader}>Activity</p>
                                                <div className="rating">

                                                    {renderStars(careTakerInfo.totalReview)}

                                                </div>
                                                <p className="text-accent mx-auto text-xl">90%</p>
                                            </div>
                                        </div>
                                        <div
                                            className={classes.cardDetails}>
                                            <div className="flex justify-between items-center">
                                                <p className="pb-2 text-xl">{careTakerInfo.detail.name}</p>
                                                {renderVerify(careTakerInfo.detail.verify)}
                                            </div>

                                            <div className={classes.cardDetailsGrid}>
                                                <p className={classes.infoHeader}>Province</p>
                                                <p className={classes.info}>{careTakerInfo.detail.location}</p>
                                                <p className={classes.infoHeader}>Small</p>
                                                <p className={classes.info}>{careTakerInfo.detail.price.small}</p>
                                                <p className={classes.infoHeader}>Medium</p>
                                                <p className={classes.info}>{careTakerInfo.detail.price.medium}</p>
                                                <p className={classes.infoHeader}>Big</p>
                                                <p className={classes.info}>{careTakerInfo.detail.price.big}</p>
                                            </div>
                                            <div className="flex justify-end w-full">
                                                <a className='btn bg-[#45BBBD] text-xl text-[#f7f7f7] border-0 rounded hover:bg-[#A6E2E3] hover:text-[#064E5C] mt-3 font-medium '
                                                   href="#caretaker">View Details</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </>
                    )}
                    {isLoad && (
                        <>
                            <div className="col-span-2 mx-auto">
                                <h1 className="text-2xl ">Loading</h1>
                                <span className="loading loading-spinner loading-lg w-full mt-6"></span>
                            </div>

                        </>
                    )}


                </div>
                {/*end grid card*/}


            </div>


        </>
    );
}

export default CaretakerCard;