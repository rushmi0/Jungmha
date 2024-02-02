import React from 'react'
import {motion} from 'framer-motion';
import SignUpBanner1 from './SignUpBanner1';
import classes from './UserSignUp.module.css';
import logo from '../assets/logo.svg';

function UserSignUp() {
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
            <p className="text-xl text-[#718096] mb-5">Already have an account? <a href="/login/user" className="text-[#45BBBD] underline">Login</a></p>
            <button className="mx-auto btn bg-[#45BBBD] text-lg text-[#fff] w-[600px] border-0 hover:bg-white hover:text-black">Sign Up</button>
          </div>
            
          
        </motion.div>
        
      </div>
      
    </>
  );
}

export default UserSignUp;