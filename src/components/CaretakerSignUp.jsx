import React, {useEffect, useState} from 'react'
import {motion} from 'framer-motion';
import SignUpBanner2 from './SignUpBanner2';
import classes from './UserSignUp.module.css';
import logo from '../assets/Logo.svg';
import {useNavigate} from "react-router-dom";
import ChaCha20 from "../../utils/ChaCha20.js";
import EllipticCurve from "../../utils/SecureKey.js";
import axios from "axios";

function CaretakerSignUp() {

    const navigate = useNavigate("");

    const  toHome = async ()=> {
        navigate("/");
    }

    const [ username, setUsername ] = useState("");
    const [ pass, setPass ] = useState("");
    const [conPass, setConPass] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [addr, setAddr] = useState("");
    const userType = "DogWalkers";
    const [amphure, setAmphure] = useState([]);

    //const aes = AES()
    const chacha = ChaCha20()
    const ec = EllipticCurve();
    const url = "http://localhost:8080/api/v1/auth/sign-up";
    const connection = "http://localhost:8080/api/v1/auth/open-channel";

    const onUsernameEnter = (e) => {
        setUsername(e.target.value);
    };

    const onPassEnter = (e) => {
        setPass(e.target.value);
    };

    const onConPassEnter = (e) => {
        setConPass(e.target.value);

    }

    const passCheck = (e) => {
        if(pass !== conPass) {
            document.getElementById("pass").style.border = "2px solid #FF4A47";
            document.getElementById("con_pass").style.border = "2px solid #FF4A47";
            alert("Password and Confirm Password not match! Please Try again.");
            e.preventDefault();
        } else {
            document.getElementById("pass").style.border = "";
            document.getElementById("con_pass").style.border = "";
        }

    }

    const onAddrEnter = (e) => {
        setAddr(e.target.value);
    }

    const onFnameEnter = (e) => {
        setFirstName(e.target.value);
    };

    const onLnameEnter = (e) => {
        setLastName(e.target.value);
    };

    const onEmailEnter = (e) => {
        setEmail(e.target.value);
    };

    const onNumberEnter = (e) => {
        setPhoneNumber(e.target.value);
    };


    const onSubmitData = async () => {
        let isLoad = false;
        let condition = username.trim().length !== 0 || pass.trim().length !== 0 || conPass.trim().length !== 0 || firstName.trim().length !== 0 || lastName.trim().length !== 0 || email.trim().length !== 0 || phoneNumber.trim().length !== 0;
        if(condition) {
            passCheck();
            const privateKey = ec.genPrivateKey(pass);
            console.log("Private key", privateKey);
            const publicKey = ec.generateKeyPair(privateKey);
            console.log("Public Key length: ", publicKey.length);
            console.log("Public Key: ", publicKey);
            console.log("Username", username);


            let payload = {
                "userName": username,
                "authenKey": publicKey
            };


            let serverPubKey = "";
            await axios.post(connection, payload).then((res) => {
                console.log("serverPubKey:", res.data["publicKey"]);
                serverPubKey = res.data["publicKey"];
                isLoad = true;
                return res.data
            }).catch((err) => console.log(err));


            console.log("Server PublicKey: ", serverPubKey);


            const sharedKey = ec.calculateSharedKey(
                privateKey,
                serverPubKey

            );

            let data = {
                "firstName": firstName,
                "lastName": lastName,
                "locationName": addr,
                "email": email,
                "phoneNumber": phoneNumber,
                "userType": userType
            }
            console.log("share key: ", sharedKey.toString("hex"));
            console.log("share key length: ", sharedKey.length);
            const jsonString = JSON.stringify(data);
            console.log(data);
            let dataToSend = chacha.encrypt(jsonString, sharedKey);
            console.log('Encrypted data:', dataToSend);

            const headers = {
                'Content-Type': 'application/json',
                "UserName": username
            }

            const sendDataEncrypt = {
                "content": dataToSend
            }
            console.log("data to send: ",sendDataEncrypt);
            let userToken = [];
            if(isLoad) {
                await axios.put(url, sendDataEncrypt, {
                    headers: headers
                }).then((res) => {
                    console.log("User Info: ", res.data);
                    userToken = JSON.stringify(res.data);
                    localStorage.setItem("user-token", userToken);
                    localStorage.setItem("private-key", privateKey);
                    localStorage.setItem("type", "DogWalkers");
                    localStorage.setItem("status", "login");
                    toHome();
                }).catch((err) => console.log(err));
            } else {
                alert("Error!")
            }

        } else {
            alert("Please fill up all informations!")
        }
    };

    useEffect(() => {
        axios.get('https://raw.githubusercontent.com/kongvut/thai-province-data/master/api_amphure.json')
            .then((res) => {
                setAmphure(res.data);
            });
    }, [])
  return (
    <>
      <div className={classes.box}>
        <SignUpBanner2/>

        <motion.div
          className="mx-auto"
          initial={{ x: -500, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          exit={{ x: 500, opacity: 0 }}
          transition={{ duration: 1 }}
        >
         
         <div className={classes.logoHeader}>
              <img src={logo} alt="jungmha" />
              <p className={classes.textChange}>Register as user <a href="/register/user" className="text-[#45BBBD] underline">here</a></p>
          </div>
            <div className={classes.infoBox}>
                <h1 className="text-4xl font-extrabold text-[#064E5C] mb-2 mx-auto">Create An Account</h1>
                <h2 className="text-2xl font-bold text-[#1999B2] mb-2 mx-auto">Information:</h2>
                <div className="flex justify-between mt-2">
                    <form method="POST" className="mx-auto">
                        <input type="text" className={classes.inputInfo} placeholder="username" onChange={onUsernameEnter}/>
                        <div className="grid grid-cols-2 gap-x-4">
                            <input type="text" className={classes.inputInfo3} placeholder="firstname" onChange={onFnameEnter}/>
                            <input type="text" className={classes.inputInfo2} placeholder="lastname" onChange={onLnameEnter}/>
                            <input type="email" className={classes.inputInfo3} placeholder="email" onChange={onEmailEnter}/>
                            <input type="text" className={classes.inputInfo2} placeholder="phone number" onChange={onNumberEnter}/>
                            <input id="pass" type="password" className={classes.inputInfo3} placeholder="password" onChange={onPassEnter}/>
                            <input id="con_pass" type="password" className={classes.inputInfo2} placeholder="confirm password" onChange={onConPassEnter}/>

                        </div>
                        <select defaultValue="" className={classes.inputInfo} onChange={onAddrEnter}>
                            <option disabled selected>Amphure</option>
                            <option selected>Any</option>
                            {amphure.filter((prev) => {
                                if (prev.province_id === 1) {
                                    return prev
                                }
                            }).map((prov) => (
                                <option key={prov.id}>
                                    {prov.name_th}
                                </option>
                            ))}

                        </select>
                        <button
                            type="button"
                            className="btn bg-[#45BBBD] text-lg text-[#fff] w-full border-0 hover:bg-white hover:text-black"
                            onClick={
                                onSubmitData
                            }>Sign
                            Up
                        </button>
                    </form>
                </div>

            </div>


        </motion.div>

      </div>

    </>
  )
}

export default CaretakerSignUp