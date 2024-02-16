import React, {useState} from 'react'
import classes from './UserLogin.module.css'
import logo from '../../assets/Logo.svg'
import { motion } from 'framer-motion'
import EllipticCurve from "../../../utils/SecureKey.js";
import axios from "axios";
import {useNavigate} from "react-router-dom";

function UserLogin() {
    const navigate = useNavigate("");
    const [username, setUsername] = useState("");
    const [pass, setPassword] = useState("");
    const [signature, setSignature] = useState("");

    const ec = EllipticCurve();


    const toHome = () => {
        navigate("/");
    }
    const usernameEnter = (e) => {
        setUsername(e.target.value);
    }

    const passwordEnter = (e) => {
        setPassword(e.target.value);
    }

    const getTimeStamp = () => {
        return Date.now();
    }



    const signMessage = async () => {
        const privateKey = ec.genPrivateKey(pass)
        console.log("private key: ",privateKey)

        const publicKey = ec.generateKeyPair(privateKey)
        console.log("public key: ",publicKey)

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\ 037678a280c054e2371c23ba16b4a9bba6b0194f3a405f0743ba45cce91732a8cb
        let timeStamp = getTimeStamp();
        const url = `http://localhost:8080/api/v1/auth/sign-in/${username}/${timeStamp}`;
        const url_sign = `/auth/sign-in/${username}/${timeStamp}`;

        const sign = ec.signMessage(url_sign, privateKey);
        console.log("sign: ", sign)
        setSignature(sign)
        console.log("signature: ", signature)

        const header = {

            "Signature": sign
        }

        axios.get(url,{
            headers: header
        }).then((res) => {
            console.log("User Info: ", res.data);
            localStorage.setItem("user-token", res.data);
            toHome();
        }).catch((err) => console.error(err));

    }



  return (
    <>
    
        <motion.div className={classes.logContainer}
        initial={{ x: -700, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        exit={{ x: 700, opacity: 0 }}
        transition={{ duration: 1}}>
            
            <div className={classes.logForm}>
                <div className="flex justify-between items-center">
                    <img src={logo} alt="jungmha" />
                    <p className="text-lg text-[#718096]">Login as caretaker <a href="/login/caretaker" className="text-[#45BBBD] underline">here</a></p>

                </div>
                <h1 className={classes.logHeader}>Login As a <a href="/login/user" className='text-[#1999B2]'>User</a></h1>
                <form className='px-[10rem] py-6' method="GET">
                    <p className={classes.subHead}>Username</p>
                    <input type="text" className={classes.inputInfo} onChange={usernameEnter}/>
                    <p className={classes.subHead}>Password</p>
                    <input type="password" className={classes.inputInfo} onChange={passwordEnter}/>
                    <div className='flex justify-between'>
                        <div className='flex items-center'>
                            <span className="label-text font-light me-5">Remember me</span>
                            <input type="checkbox" className="checkbox checkbox-accent" />
                        </div>
                        <div className='flex items-center'>
                            <a href='/recovery' className={classes.link}>Forgot password?</a>
                        </div>                             
                    </div>
                    <p className="text-[#718096] mt-6">Dont have an account? <a href='/register/user' className={classes.link}>Create now</a></p>

                    <a className={classes.logBtn} onClick={signMessage}>Login</a>
                </form>
                
            </div>
            <div className={classes.logGuide}>
                
                <div className='relative bg-[#f7f7f7] m-[10rem] p-[2rem] rounded-lg z-40 shadow-md'>
                    <p className='text-4xl text-[#45BBBD] font-bold mb-3'>No time no problem</p>
                    <p className='text-lg text-[#718096]'>Worried about leaving your furry friend behind? Let's us take care of your dog while you busy. Whether you're heading out for a day trip, a work conference, or a long-awaited vacation, rest assured your pup is in the best paws possible. We offer a variety of services to cater to every dog's needs, from playful daycare adventures to cozy home boarding with loving families.</p>
                    <a href="/" className={classes.logBtn}>See more</a>
                </div>
                <p className='text-2xl text-center text-[#f7f7f7]'>Leave your dog with us and let them have the time of their lives!</p>
                <p className='text-lg text-center text-[#CFD9E0]'>We're the dog experts who will make sure your dog has a great experience.</p>
                <div dir="rtl" id={classes.circle}>
                    <div className='absolute w-[600px] h-[600px] bg-[#fff] rounded-l-full rounded-b-full opacity-15 top-0 start-0 z-10 shadow-lg'>
                    </div>                                        
                </div>
                
            </div>

            
        </motion.div>
    </>
  )
}

export default UserLogin