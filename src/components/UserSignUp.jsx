import React, {useState} from 'react'
import {motion} from 'framer-motion';
import SignUpBanner1 from './SignUpBanner1';
import classes from './UserSignUp.module.css';
import logo from '../assets/Logo.svg';
import axios from "axios";
import EllipticCurve from "../../utils/SecureKey.js";
import AES from "../../utils/AES.js";
import {useNavigate} from "react-router-dom";
import {Buffer} from "buffer";

function UserSignUp() {
    const navigate = useNavigate("");

    const toLogin = () => {
        navigate("/login/user");
    };

    const [ username, setUsername ] = useState("");
    const [ pass, setPass ] = useState("");
    const [conPass, setConPass] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [pubKey, setPubKey] = useState("");
    const [serverPubKey, setServerPubKey] = useState("");
    const userType = "Normal";


    const aes = AES()
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

    const passCheck = () => {
         if(conPass != pass) {
            alert("Password and Confirm Password is not the same! Please Try again.");
            navigate("/register/user");

         }


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

    const dataEncrypt = () => {
        const sharedKey = ec.calculateSharedKey(
            pubKey,
            serverPubKey
        );

        let data = {
            "firstName": firstName,
            "lastName": lastName,
            "email": email,
            "phoneNumber": phoneNumber,
            "userType": userType
        }

        const jsonString = JSON.stringify(data);
        console.log(data);

        let dataToSend = aes.encrypt(jsonString, sharedKey);
        console.log('Encrypted data:', dataToSend);
    }
    const onSubmitData = (e) => {
        e.preventDefault();
        passCheck();
        const privateKey = ec.genPrivateKey(pass);
        console.log("Private key", privateKey);
        const publicKey = ec.generateKeyPair(privateKey);
        console.log("Public Key", publicKey);

        setPubKey(publicKey) ;

        let payload = {
            "userName": username,
            "authenKey": publicKey
        };

        const postResponse = axios.post(connection, payload)
            .then(console.log("Data posted!")).catch((err) => console.log(err));


        const serverPublicKey = postResponse.data["publicKey"]; // Bug อยู่ตรงนี้อ่ะ มันบอกว่า properties of undefined ('reading publicKey)
        setServerPubKey(serverPublicKey);
        console.log("Server PublicKey: ", serverPublicKey);
        dataEncrypt();

    };

  return (
    <>
      <div className={classes.box}>
        <SignUpBanner1/>
        <motion.div
          className="mx-auto"
          initial={{ x: 500, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          exit={{ x: -500, opacity: 0 }}
          transition={{ duration: 1 }}
        >

         <div className="flex m-10 w-[900px] justify-between items-center p-10">
              <img src={logo} alt="jungmha" />
              <p className="text-lg text-[#718096]">Become our caretaker <a href="/register/caretaker" className="text-[#45BBBD] underline">here</a></p>

          </div>
          <div className="flex flex-col justify-between m-10 p-10">
            <h1 className="text-4xl font-extrabold text-[#064E5C] mb-2">Create An Account</h1>
            <h2 className="text-2xl font-bold text-[#1999B2] mb-2">Information:</h2>
            <div className="flex justify-between mt-2">
                <form method="POST">
                    <input type="text" className={classes.inputInfo} placeholder="username" onChange={onUsernameEnter}
                           required="required"/>
                    <input type="text" className={classes.inputInfo3} placeholder="firstname" onChange={onFnameEnter}
                           required="required"/>
                    <input type="text" className={classes.inputInfo2} placeholder="lastname" onChange={onLnameEnter}
                           required="required"/>
                    <input type="email" className={classes.inputInfo3} placeholder="email" onChange={onEmailEnter}
                           required="required"/>
                    <input type="text" className={classes.inputInfo2} placeholder="phone number"
                           onChange={onNumberEnter} required="required"/>
                    <input type="password" className={classes.inputInfo3} placeholder="password" onChange={onPassEnter}
                           required="required" autoComplete="on"/>
                    <input type="password" className={classes.inputInfo2} placeholder="confirm password"
                           onChange={onConPassEnter} required="required" autoComplete="on"/>
                    <input type="text" className={classes.inputInfo} placeholder="address"/>

                    <p className="text-xl text-[#718096] mb-5">Already have an account? <a href="/login/user"
                                                                                           className="text-[#45BBBD] underline">Login</a>
                    </p>
                    <button
                        type="button"
                        className="mx-auto btn bg-[#45BBBD] text-lg text-[#fff] w-[600px] border-0 hover:bg-white hover:text-black"
                        onClick={
                            onSubmitData
                        }>Sign Up
                    </button>
                </form>

            </div>

          </div>


        </motion.div>

      </div>

    </>
  );
}

export default UserSignUp;