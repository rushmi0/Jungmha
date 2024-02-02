import React from 'react'
import logo from '../assets/logo.svg';

function Navbar() {
  return (
    <>
        <div className="navbar bg-[#7F7F7] shadow-md z-40 relative">
            <div className='container mx-auto '>
                <div className="flex-1">
                    <a href="/"><img src={logo} alt="jungmha"/></a>
                </div>
                <div className="flex-none">
                    <a href="/" className='text-lg font-bold mr-10'>Home</a>
                    <a className='btn btn-accent rounded rounded-e-none px-5 hover:bg-[#A6E2E3] hover:border-accent hover:text-[#064E5C]' href="/login/user">Login</a>
                    <a className='btn btn-accent rounded rounded-s-none px-5 hover:bg-[#A6E2E3] hover:border-accent hover:text-[#064E5C] ' href="/register/user">Register</a>
                </div>
            </div>
            
        </div>
    </>
  )
}

export default Navbar