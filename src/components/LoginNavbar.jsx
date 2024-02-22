import React, {useEffect, useState} from 'react'
import logo from '../assets/Logo.svg'
import axios from "axios";
import defaultImg from "../assets/user.svg"
import {BASE_URL} from "../../constants/BaseEndpoint.js";
import EllipticCurve from "../../utils/SecureKey.js";
import chaCha20 from "../../utils/ChaCha20.js";


function Navbar() {
    const [data, setData] = useState(null);

    const [proImg, setProImg] = useState("");
    const token = JSON.parse(localStorage.getItem("user-token"));
    const viewToken = token.token.view;
    const base_url = BASE_URL["baseEndpoint"];
    const url = base_url + "/api/v1/auth/user/normal";
    const [render, setRender] = useState(false);


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
        axios.get(url, {
            headers:header
        }).then((res) => {
            enData = res.data.content;
            console.log("Encrypt Data: ", enData);
            dcData = cha.decrypt(enData, sharedKey);
            setData(dcData);
            console.log("Decrypt Data: ", dcData);
            console.log(dcData.accountType);
            localStorage.setItem("user-id", dcData.userID);
            setProImg(dcData.profileImage);
            setRender(true);
        }).catch((err) => {
            console.error(err);
        });
    }
    useEffect(() => {
        getData();
        console.log("Data: ", data);
    }, []);

    const [file, setFile] = useState(null);
    const img = defaultImg;
    const [previewImage, setPreviewImage] = useState(img);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        setFile(selectedFile);
        setPreviewImage(URL.createObjectURL(selectedFile));
    };

    const editToken = token.token.edit;
    const editHeader = {
        'Content-Type': 'multipart/form-data',
        "Access-Token": editToken
    }

    const handleUpload = async () => {
        if (file) {
            try {
                const formData = new FormData();
                formData.append('file', file);

                const apiUrl = base_url + '/api/v1/auth/user/upload';

                await axios.post(apiUrl, formData, {
                    headers: editHeader
                });
                getData();
                alert('Image uploaded successfully');
            } catch (error) {
                console.error('Error uploading image:', error);
            }
        } else {
            console.error('No file selected.');
        }
    };




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
                                         src={proImg}/>
                                </div>
                            </div>
                            <ul tabIndex={0}
                                className="mt-[10rem] z-[1] p-2 shadow menu menu-sm dropdown-content bg-[#f7f7f7] rounded-box w-52 border-2 border-accent">
                                <li>
                                    <a className="justify-between"
                                       onClick={() => document.getElementById('userModal').showModal()}>
                                        Profile
                                    </a>
                                </li>
                                <li><a onClick={logOut}>Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            {render && (
                <>
                    <dialog id="userModal" className="modal">
                        <div className="modal-box bg-transparent shadow-none">
                            <div className="grid p-2 items-center gap-4">
                                <div className="bg-[#f7f7f7] col-span-1 p-6 px-8 w-full rounded-2xl shadow-md">
                                    <img src={proImg}
                                         className="w-[180px] h-[180px] rounded-full border-2 border-accent mb-4 mx-auto"/>
                                    <div className="text-lg">
                                        <h3 className="text-center text-2xl mb-4">{data.userName}</h3>
                                        <p>Tel: {data.phoneNumber}</p>
                                        <p>Email: {data.email}</p>
                                    </div>
                                    <div className="modal-action">
                                        <button className="btn btn-accent" onClick={() => {
                                           document.getElementById('editUserModal').showModal()
                                        }}>Edit Profile
                                        </button>
                                        <form method="dialog">
                                            {/* if there is a button, it will close the modal */}
                                            <button className="btn btn-error">Close</button>
                                        </form>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <dialog id="editUserModal" className="modal">
                            <div className="modal-box bg-transparent shadow-none">
                                <div className="grid p-2 items-center gap-4">
                                    <div className="bg-[#f7f7f7] col-span-1 p-6 px-8 w-full rounded-2xl shadow-md">
                                        <img src={previewImage}
                                             className="w-[180px] h-[180px] rounded-full border-2 border-accent mb-4 mx-auto"/>
                                        <div className="text-lg">
                                            <input type="file"
                                                   accept="image/*"
                                                   className="file-input file-input-bordered file-input-accent bg-[#CBD5E0] w-full hover:file-input-info"
                                            onChange={handleFileChange}/>
                                        </div>
                                        <div className="modal-action">
                                            <button className="btn btn-accent" onClick={handleUpload}>Upload</button>
                                            <form method="dialog">
                                                {/* if there is a button, it will close the modal */}
                                                <button className="btn btn-error">Close</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </dialog>
                    </dialog>

                </>
            )}
        </>
    )
}

export default Navbar