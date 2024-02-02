import React from 'react'
import { motion } from 'framer-motion';
import classes from './SignUpBanner1.module.css';

function SignUpBanner1() {
  return (
    <>
      <motion.div
        className={classes.bgImg}
        initial={{ x: -700, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        exit={{ x: 700, opacity: 0 }}
        transition={{ duration: 1.5 }}
      >
        
      </motion.div>
    </>
  )
}

export default SignUpBanner1