import React, {useState, useEffect, createContext} from 'react';
import classes from "./CaretakerCard.module.css";
import { FaRegCheckCircle } from "react-icons/fa";
import axios from "axios";
import {BASE_URL} from "../../../constants/BaseEndpoint.js";
import {Link, useNavigate} from "react-router-dom";
import chaCha20 from "../../../utils/ChaCha20.js";
import EllipticCurve from "../../../utils/SecureKey.js";

export function renderStars(rating) {
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
export const AppContext = createContext(null);
// eslint-disable-next-line react/prop-types
function CaretakerCard({name, location, verify}) {
    const navigate = useNavigate('');
    const toLogin = () => {
        navigate('/login/user');
    }
    //console.log(name);
    //console.log(price);
    //console.log(location);
    //console.log(verify);
    const [data, setData] = useState([]);
    const [breed, setBreed] = useState([]);
    const [isLoad, setLoad] = useState(false);
    const base_url = BASE_URL["baseEndpoint"];
    const url = base_url + "/api/v1/home/filter";
    const urlBreed = base_url + "/api/v1/dogs";

    const [selectEmp, setSelectEmp] = useState(0);


    const loadData = async () => {
        await axios.get(url)
            .then((res) => {
                setData(res.data);
                setLoad(true)
            }).catch((err) => console.log(err));
    }
    const loadBreed = async () => {
        await axios.get(urlBreed)
            .then((res) => {
                setBreed(res.data);
                console.log("breed: ", res.data);
            });
    }

    useEffect(() => {
        loadData();
        loadBreed();
        console.log("data: ", data);
    }, []);

    const [timeStart, setTimeStart] = useState("");
    const [timeEnd, setTimeEnd] = useState("");
    const [bookDate, setbookDate] = useState("");
    const [bookBreed, setbookBreed] = useState();

    useEffect(() => {
        console.log("selectIndex: ",selectEmp + 1);

    }, [selectEmp]);


    const book_url = base_url + "/api/v1/auth/user/booking";
    let eToken = "";
    let uID = "";
    let sKey = "";
    let status = "";
    const ec = EllipticCurve();
        if(!JSON.parse(localStorage.getItem("user-token"))) {
            console.log("No token!");
        } else {
            const t = JSON.parse(localStorage.getItem("user-token"));
            const privateKey = localStorage.getItem("private-key");
            const userId = localStorage.getItem("user-id");
            const login = localStorage.getItem("status");
            const editToken = t.token.edit
            const serverPubKey = t.serverPublicKey.publicKey;
            const shared_key = ec.calculateSharedKey(
                privateKey,
                serverPubKey
            )
            status = login;
            eToken = editToken
            uID = userId
            sKey = shared_key

        }


    const cha = chaCha20()

    let dataEmp = {}
    if(isLoad) {
        dataEmp = data[selectEmp].walkerID
    } else {
        console.log("Loading data...")
    }
    const book_payload = {
        "walkerID": parseInt(dataEmp),
        "userID": uID,
        "dogID": parseInt(bookBreed),
        "bookingDate": bookDate,
        "timeStart": timeStart,
        "timeEnd": timeEnd
    }
    const json_payload = JSON.stringify(book_payload);

    const encryptPayload = cha.encrypt(json_payload, sKey)

    const body_payload = {
        "content": encryptPayload
    }

    const header = {
        "Access-Token": eToken
    }

    const bookingSubmit = async () => {
        console.log("booking data: ", book_payload)
        await axios.post(book_url, body_payload,{
            headers: header
        }).then((res) => {
            console.log(res);
            navigate('/');
            alert("Booking Success!");
        }).catch((err) => {
            console.log(err)
        })
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
                    {isLoad && (
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
                            }).map((careTakerInfo, i) => (
                                <div key={i} className={classes.cardBg}>
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
                                                <p className={classes.infoHeader}>Amphure</p>
                                                <p className={classes.info}>{careTakerInfo.detail.location}</p>
                                                <p className={classes.infoHeader}>Small</p>
                                                <p className={classes.info}>{careTakerInfo.detail.price.small}</p>
                                                <p className={classes.infoHeader}>Medium</p>
                                                <p className={classes.info}>{careTakerInfo.detail.price.medium}</p>
                                                <p className={classes.infoHeader}>Big</p>
                                                <p className={classes.info}>{careTakerInfo.detail.price.big}</p>
                                            </div>
                                            <div className="flex justify-end w-full">
                                                <a className='btn bg-[#45BBBD] text-xl text-[#f7f7f7] border-0 rounded hover:bg-[#A6E2E3] hover:text-[#064E5C] mt-3 font-medium'
                                                   onClick={() => {
                                                       status === "login" ? document.getElementById('book_modal').showModal() : document.getElementById('book_modal_not_login').showModal()
                                                       setSelectEmp(i)
                                                   }}>VIEW
                                                    DETAILS
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}

                            {isLoad && (
                                <dialog id="book_modal" className="modal modal-bottom sm:modal-middle">
                                    <div className="p-10 bg-[#f7f7f7] rounded shadow-md">
                                        <div className="container mx-auto bg-accent p-10 shadow-md">

                                            <div className="p-12 h-full">
                                                <div className="mb-6">
                                                    <h1 className="text-4xl font-bold text-[#f7f7f7]">Select
                                                        the time
                                                        for {data[selectEmp].detail.name}</h1>
                                                </div>
                                                <div
                                                    className={classes.gridBooking}>
                                                    <div
                                                        className="bg-[#f7F7F7] p-4 rounded border-2 border-accent">
                                                        <h3 className="text-2xl mb-2 font-bold text-[#718096]">Breed</h3>
                                                        <select
                                                            className="select w-full max-w-xs bg-transparent"
                                                            onChange={(e) => setbookBreed(e.target.value)}
                                                        >
                                                            <option disabled selected>Breed</option>
                                                            {breed.map((bre, i) => (
                                                                <option key={i} value={i}>
                                                                    {bre.breedName}
                                                                </option>
                                                            ))}
                                                        </select>
                                                    </div>
                                                    <div
                                                        className="bg-[#f7F7F7] p-4 rounded border-2 border-accent">
                                                        <h3 className="text-2xl mb-2 font-bold text-[#718096]">Size</h3>
                                                        <select
                                                            className="select w-full max-w-xs bg-transparent"
                                                        >
                                                            <option disabled selected>size</option>
                                                            <option>Small</option>
                                                            <option>Medium</option>
                                                            <option>Big</option>
                                                        </select>
                                                    </div>
                                                    <div
                                                        className="bg-[#f7F7F7] p-4 rounded border-2 border-accent">
                                                        <h3 className="text-2xl mb-2 font-bold text-[#718096]">Time</h3>
                                                        <input
                                                            className="input bg-[#f7f7f7] input-accent me-2"
                                                            type="time"
                                                            id="starttime"
                                                            name="starttime"
                                                            onChange={(e) => setTimeStart(e.target.value)}/>
                                                        <input className="input bg-[#f7f7f7] input-accent"
                                                               type="time"
                                                               id="endtime"
                                                               name="endtime"
                                                               onChange={(e) => setTimeEnd(e.target.value)}/>
                                                    </div>
                                                    <div
                                                        className="bg-[#f7F7F7] p-4 rounded border-2 border-accent">
                                                        <h3 className="text-2xl mb-2 font-bold text-[#718096]">Date</h3>
                                                        <input className="input bg-[#f7f7f7] text-[#718096]"
                                                               type="date"
                                                               id="bookingtime" name="bookingtime"
                                                               onChange={(e) => setbookDate(e.target.value)}/>
                                                    </div>
                                                </div>
                                                <div className="flex justify-between ">
                                                    <form method="dialog">
                                                        {/* if there is a button in form, it will close the modal */}
                                                        <button
                                                            className="btn bg-rose-400 border-0 text-base-300 mt-4 rounded-none hover:text-[#f7f7f7]">Cancel
                                                        </button>
                                                    </form>
                                                    <button
                                                        className="btn bg-[#f7f7f7] border-0 text-base-300 mt-4 rounded-none hover:text-[#f7f7f7]"
                                                        onClick={bookingSubmit}>Confirm
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </dialog>

                            )}
                            <dialog id="book_modal_not_login" className="modal modal-bottom sm:modal-middle">
                                <div className="modal-box bg-[#f7f7f7]">
                                    <h3 className="font-bold text-lg">Please login!</h3>
                                    <p className="py-4">Please login to use this feature!</p>
                                    <div className="modal-action">
                                        <button className="btn btn-accent" onClick={toLogin}>Login</button>
                                        <form method="dialog">
                                            {/* if there is a button in form, it will close the modal */}
                                            <button className="btn hover:bg-rose-500 hover:text-[#f7f7f7] border-0">Close</button>
                                        </form>
                                    </div>
                                </div>
                            </dialog>
                        </>
                    )}


                    {!isLoad && (
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

