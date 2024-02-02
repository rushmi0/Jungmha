import React from 'react'
import {motion} from 'framer-motion';
import SignUpBanner2 from './SignUpBanner2';
import classes from './UserSignUp.module.css';
import logo from '../assets/logo.svg';

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
         
         <div className="flex m-10 w-[900px] justify-between items-center p-10">
              <img src={logo} alt="jungmha" />
              <p className="text-lg text-[#718096]">Register as user <a href="/register/user" className="text-[#45BBBD] underline">here</a></p>
          </div>
          <div className="flex flex-col justify-between m-10 p-10">
            <h1 className="text-4xl font-extrabold text-[#064E5C] mb-2">Create An Account</h1>
            <h2 className="text-2xl font-bold text-[#1999B2] mb-2">Information:</h2>
            <div className="flex justify-between mt-2">
            <form action="">
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[810px] bg-[#fff] p-2 mb-5" placeholder="username" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[370px] bg-[#fff] p-2 mb-5 me-[4.3rem]" placeholder="firstname" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[370px] bg-[#fff] p-2 mb-5" placeholder="lastname" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[370px] bg-[#fff] p-2 mb-5 me-[4.3rem]" placeholder="email" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[370px] bg-[#fff] p-2 mb-5" placeholder="phone number" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[370px] bg-[#fff] p-2 mb-5 me-[4.3rem]" placeholder="password" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[370px] bg-[#fff] p-2 mb-5" placeholder="confirm password" />
              <input type="text" className="border-2 border-[#CBD5E0] rounded text-lg w-[810px] bg-[#fff] p-2 mb-5" placeholder="address" />
              
            </form>
            
            </div>
            <div className="flex justify-between items-center">
                <p className="text-xl text-[#718096] mb-5">Upload your resume here: </p>
                <input type="file" className="file-input file-input-bordered file-input-accent bg-[#CBD5E0] w-full max-w-xs me-3 mb-5 hover:file-input-info" />
            </div>
            
            <button className="mx-auto btn bg-[#45BBBD] text-lg text-[#fff] w-[600px] border-0 hover:bg-white hover:text-black">Sign Up</button>
          </div>
            
          
        </motion.div>
        
      </div>
      
    </>
  )
}

export default CaretakerSignUp