import React from 'react'
import {motion} from 'framer-motion';
import SignUpBanner2 from './SignUpBanner2';
import classes from './UserSignUp.module.css';
import logo from '../assets/Logo.svg';

function CaretakerSignUp() {
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
                    <form action="" className="mx-auto">
                        <input type="text" className={classes.inputInfo} placeholder="username"/>
                        <div className="grid grid-cols-2 gap-x-4">
                            <input type="text" className={classes.inputInfo3} placeholder="firstname"/>
                            <input type="text" className={classes.inputInfo2} placeholder="lastname"/>
                            <input type="text" className={classes.inputInfo3} placeholder="email"/>
                            <input type="text" className={classes.inputInfo2} placeholder="phone number"/>
                            <input type="text" className={classes.inputInfo3} placeholder="password"/>
                            <input type="text" className={classes.inputInfo2} placeholder="confirm password"/>

                        </div>
                        <input type="text" className={classes.inputInfo} placeholder="address"/>
                        <div className="flex justify-between items-center w-full">
                            <p className="text-xl text-[#718096] mb-5">Upload resume: </p>
                            <input type="file"
                                   className="file-input file-input-bordered file-input-accent bg-[#CBD5E0] w-full max-w-xs mb-5 hover:file-input-info"/>
                        </div>
                        <button
                            className="btn bg-[#45BBBD] text-lg text-[#fff] w-full border-0 hover:bg-white hover:text-black">Sign
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