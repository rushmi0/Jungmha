import React from 'react'
import { motion } from 'framer-motion'
import logo from '../../assets/Logo.svg'
import classes from './CaretakerLogin.module.css'

function CaretakerLogin() {
  return (
    <>
        <motion.div className="grid grid-cols-2" 
        initial={{ x: 700, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        exit={{ x: -700, opacity: 0 }}
        transition={{ duration: 1 }}>

            
            <div className={classes.logGuide}>
                
                <div className='relative bg-[#f7f7f7] m-[10rem] p-[2rem] rounded-lg z-40 shadow-md
}'>
                    <p className='text-4xl text-[#45BBBD] font-bold mb-3 text-center'>Be a part of our pawsome team</p>
                    <p className={classes.txtDes}>Do you love dogs? Do you have a passion for animal care? If so, we want you to join our team of dog sitters! <br/><br/>
                    We are looking for responsible, loving, and experienced dog sitters to join our growing team. Our sitters provide a safe and nurturing environment for dogs of all breeds and sizes. <br/><br/> Here are some of the benefits of being a dog sitter with us:</p>
                    <ul className='list-inside list-disc ms-6'>
                        <li className={classes.txtDes}>Flexible hours</li>
                        <li className={classes.txtDes}>Competitive pay</li>
                        <li className={classes.txtDes}>Opportunity to meet new people and furry friends</li>
                    </ul>
                    <p className={classes.txtDes}>If you are interested in becoming a dog sitter, please visit our website or contact us today. We would love to hear from you!</p>
                    <a href="/" className={classes.logBtn}>See more</a>
                </div>
                
                <div dir="rtl"> 
                    <div className='absolute w-[600px] h-[600px] bg-[#fff] rounded-r-full rounded-b-full opacity-15 top-0 end-0 z-10 shadow-lg'>
                    </div>                                        
                </div>
                
            </div>
            <div className={classes.logForm}>
                <div className="flex justify-between items-center">
                    <img src={logo} alt="jungmha" />
                    <p className="text-lg text-[#718096]">Login as user <a href="/login/user" className="text-[#45BBBD] underline">here</a></p>
                </div>
                <h1 className={classes.logHeader}>Login As a <a href="/login/caretaker" className='text-[#1999B2]'>Caretaker</a></h1>
                <form action="" className='px-[10rem] py-6'>
                    <p className={classes.subHead}>Username</p>
                    <input type="text" className={classes.inputInfo} />
                    <p className={classes.subHead}>Password</p>
                    <input type="password" className={classes.inputInfo} />
                    <div className='flex justify-between'>
                        <div className='flex items-center'>
                            <span className="label-text font-light me-5">Remember me</span>
                            <input type="checkbox" className="checkbox checkbox-accent" />
                        </div>
                        <div className='flex items-center'>
                            <a href='/recovery' className={classes.link}>Forgot password?</a>
                        </div>                             
                    </div>
                    <p className="text-[#718096] mt-6">Dont have an account? <a href='/register/caretaker' className={classes.link}>Create now</a></p>

                    <a href='/' className={classes.logBtn}>Login</a>
                </form>
                
            </div>

        </motion.div>
    </>
  )
}

export default CaretakerLogin