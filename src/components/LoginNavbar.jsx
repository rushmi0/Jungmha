import React, {useEffect, useState} from 'react'
import logo from '../assets/Logo.svg'
import classes from './LoginNavbar.module.css'
import profile from '../assets/profile.jpg';
import axios from "axios";
import defaultImg from "../assets/user.svg"
import {BASE_URL} from "../../constants/BaseEndpoint.js";
import EllipticCurve from "../../utils/SecureKey.js";
import chaCha20 from "../../utils/ChaCha20.js";


function Navbar() {
    const [data, setData] = useState([]);
    const img = defaultImg;
    const [proImg, setProImg] = useState("");
    const token = JSON.parse(localStorage.getItem("user-token"));
    const viewToken = token.token.view;
    const base_url = BASE_URL["baseEndpoint"];
    const url = base_url + "/api/v1/auth/user/normal";

    const header = {
        "Access-Token" : viewToken
    }
    let enData = "";

    const logOut = () => {
        localStorage.clear();
        window.location.reload();
    }

    const priKey = localStorage.getItem("private-key");
    const serverPubKey = token.serverPublicKey.publicKey;
    // console.log("priKey: ",priKey);
    // console.log("ServPub: ", serverPubKey);

    const ec = EllipticCurve();
    const cha = chaCha20();
    const sharedKey = ec.calculateSharedKey(
        priKey,
        serverPubKey
    )

    // console.log("sharedKey: ",sharedKey.toString("hex"));

    const getData = async () => {
        let dcData = [];
        await axios.get(url, {
            headers:header
        }).then((res) => {
            enData = res.data.content;
            console.log("Encrypt Data: ", enData);
            let decryptData = cha.decrypt(enData, sharedKey);
            dcData = decryptData;
            setData(dcData);
            console.log("Decrypt Data: ", dcData);
            console.log(dcData.accountType);
            setProImg(dcData.profileImage);
        }).catch((err) => {
            if(err.response.status === 400) {
                alert("Please login!");
            } else {
                console.error(err);
            }
        });

    }

    useEffect(() => {
        getData();
        console.log("Data: ", data);
    }, []);

    const editToken = token.token.edit;

    const editHeader = {
        "Access-Token": editToken
    }



    return (
        <>
            <div className="navbar bg-[#7F7F7] shadow-md z-50 relative">
                <div className='container mx-auto '>
                    <div className="flex-1">
                        <a href="/"><img src={logo} alt="jungmha"/></a>
                    </div>
                    {/*<div className={classes.mainMenu}>*/}
                    {/*    <a href="/" className='text-lg font-bold mr-10'>Home</a>*/}
                    {/*    <img src={profile} className={classes.profileNav}/>*/}
                    {/*</div>*/}
                    <div className="dropdown dropdown-end">
                        <div className="flex items-center">
                            <a href="/" className='text-lg font-bold mr-10'>Home</a>
                            <a href="/" className='text-lg font-bold mr-10'>Booking</a>
                            <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar">
                                <div className="w-10 rounded-full">
                                    <img id="profileImg" alt="Tailwind CSS Navbar component"
                                         src={img}/>
                                </div>
                            </div>
                            <ul tabIndex={0}
                                className="mt-[10rem] z-[1] p-2 shadow menu menu-sm dropdown-content bg-[#f7f7f7] rounded-box w-52 border-2 border-accent">
                                <li>
                                    <a className="justify-between" onClick={() => document.getElementById('userModal').showModal()}>
                                        Profile
                                    </a>
                                </li>
                                <li><a onClick={logOut}>Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
                <>
                    <dialog id="userModal" className="modal">
                        <div className="modal-box bg-transparent shadow-none">
                            <div className="grid p-2 items-center gap-4">
                                <div className="bg-[#f7f7f7] col-span-1 p-6 px-8 w-full rounded-2xl shadow-md">
                                    <img src={img}
                                         className="w-[180px] rounded-full border-2 border-accent mb-4 mx-auto"/>
                                    <div className="text-lg">
                                        <h3 className="text-center text-2xl mb-4">{data.userName}</h3>
                                        <p>Tel: {data.phoneNumber}</p>
                                        <p>Email: {data.email}</p>
                                        <p>Location: Amphures</p>
                                    </div>
                                    <div className="modal-action">
                                        <button className="btn btn-accent">Edit Profile</button>
                                        <form method="dialog">
                                            {/* if there is a button, it will close the modal */}
                                            <button className="btn btn-error">Close</button>
                                        </form>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </dialog>
                </>

                {/*// <>*/}
                {/*//     <dialog id="userModal" className="modal">*/}
                {/*//         <div className="modal-box w-11/12 max-w-7xl bg-transparent shadow-none">*/}
                {/*//             <div className="grid grid-cols-3 p-2 items-center gap-4 h-full">*/}
                {/*//                 <div className="bg-[#f7f7f7] col-span-1 p-6 px-8 w-full h-full rounded-s-2xl shadow-md">*/}
                {/*//                     <img src={proImg}*/}
                {/*//                          className="w-[180px] rounded-full border-2 border-accent mb-4 mx-auto"/>*/}
                {/*//                     <div className="text-lg">*/}
                {/*//                         <h3 className="text-center text-2xl mb-4">{data.userName}</h3>*/}
                {/*//                         <p>Name: {data.firstName} {data.lastName}</p>*/}
                {/*//                         <div className="rating">*/}
                {/*//                             <p>Rating: </p>*/}
                {/*//                             {renderStars(data.insights.totalReview)}*/}
                {/*//                         </div>*/}
                {/*//                         <p>Tel: {data.phoneNumber}</p>*/}
                {/*//                         <p>Email: {data.email}</p>*/}
                {/*//                         <p>Location: {data.insights.locationName}</p>*/}
                {/*//                         <p>Price small: {data.insights.price.small}</p>*/}
                {/*//                         <p>Price medium: {data.insights.price.medium}</p>*/}
                {/*//                         <p>Price big: {data.insights.price.big}</p>*/}
                {/*//*/}
                {/*//                         <div className="flex justify-start items-end">*/}
                {/*//*/}
                {/*//                         </div>*/}
                {/*//                     </div>*/}
                {/*//*/}
                {/*//                 </div>*/}
                {/*//*/}
                {/*//                 <div className="bg-[#f7f7f7] col-span-2 w-full p-6 px-8 w-full rounded-e-2xl shadow-md">*/}
                {/*//                     <div className="p-2">*/}
                {/*//                         <h3 className="mb-4 text-2xl">Caretaker's Schedule for April 10 to April 17</h3>*/}
                {/*//                         <div>*/}
                {/*//                             <div className="overflow-x-auto">*/}
                {/*//                                 <table className="table">*/}
                {/*//                                     /!* head *!/*/}
                {/*//                                     <thead className="bg-sky-200 text-base-300">*/}
                {/*//                                     <tr>*/}
                {/*//                                         <th>Day/Time</th>*/}
                {/*//                                         <th>8:00-9:00</th>*/}
                {/*//                                         <th>9:00-10:00</th>*/}
                {/*//                                         <th>10:00-11:00</th>*/}
                {/*//                                         <th>11:00-12:00</th>*/}
                {/*//                                         <th>12:00-13:00</th>*/}
                {/*//                                         <th>13:00-14:00</th>*/}
                {/*//                                         <th>14:00-15:00</th>*/}
                {/*//                                         <th>15:00-16:00</th>*/}
                {/*//                                         <th>16:00-17:00</th>*/}
                {/*//                                         <th>17:00-18:00</th>*/}
                {/*//                                     </tr>*/}
                {/*//                                     </thead>*/}
                {/*//                                     <tbody>*/}
                {/*//                                     /!* row 1 *!/*/}
                {/*//                                     <tr>*/}
                {/*//                                         <td className={classes.verticalHeader}>Monday</td>*/}
                {/*//                                         <td className="bg-rose-500" colSpan="2">User's name</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     /!* row 2 *!/*/}
                {/*//                                     <tr className="hover:bg-accent">*/}
                {/*//                                         <td className={classes.verticalHeader}>Tuesday</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     /!* row 3 *!/*/}
                {/*//                                     <tr>*/}
                {/*//                                         <td className={classes.verticalHeader}>Wednesday</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     <tr>*/}
                {/*//                                         <td className={classes.verticalHeader}>Thursday</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     <tr>*/}
                {/*//                                         <td className={classes.verticalHeader}>Friday</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     <tr>*/}
                {/*//                                         <td className={classes.verticalHeader}>Saturday</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     <tr>*/}
                {/*//                                         <td className={classes.verticalHeader}>Sunday</td>*/}
                {/*//                                     </tr>*/}
                {/*//                                     </tbody>*/}
                {/*//                                 </table>*/}
                {/*//                             </div>*/}
                {/*//                         </div>*/}
                {/*//*/}
                {/*//                         <div className="flex justify-end">*/}
                {/*//                             <button*/}
                {/*//                                 className="btn btn-accent mt-4 text-sm rounded-none me-4">Edit*/}
                {/*//                                 Profile*/}
                {/*//                             </button>*/}
                {/*//                             <form method="dialog">*/}
                {/*//                                 /!* if there is a button, it will close the modal *!/*/}
                {/*//                                 <button className="btn btn-error mt-4">Close</button>*/}
                {/*//                             </form>*/}
                {/*//                         </div>*/}
                {/*//*/}
                {/*//                     </div>*/}
                {/*//                 </div>*/}
                {/*//             </div>*/}
                {/*//*/}
                {/*//         </div>*/}
                {/*//     </dialog>*/}
                {/*// </>*/}


        </>
    )
}

export default Navbar