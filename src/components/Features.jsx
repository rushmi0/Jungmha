import React from 'react'
import classes from './Features.module.css'

import { AiOutlineSchedule,AiOutlineSearch,AiTwotoneEnvironment } from "react-icons/ai";


function Features() {
  return (
    <>
        <div className="container mx-auto border border-slate-300 mt-[3rem] mb-[3rem] rounded">
        <div className="grid xl:grid-cols-3 sm:grid-cols-2 sm:justify-items-start xl:justify-items-center m-5">
          <div className={classes.featureBox}>
            <AiOutlineSearch className={classes.icons}/>
            <div className={classes.desContent}>
              <h1 className={classes.headText}>Find Caretaker</h1>
              <p className={classes.subText}>find caretaker you like to takecare of your dog.</p>
            </div>
          </div>
          <div className={classes.featureBox}>
            <AiOutlineSchedule className={classes.icons}/>
            <div className={classes.desContent}>
              <h1 className={classes.headText}>Booking time</h1>
              <p className={classes.subText}>Find the perfect time for your dog.</p>
            </div>
          </div>
          <div className={classes.featureBox}>
            <AiTwotoneEnvironment className={classes.icons}/>
            <div className={classes.desContent}>
              <h1 className={classes.headText}>Track your dog</h1>
              <p className={classes.subText}>Keep track of your dog with caretaker anytime.</p>
            </div>
          </div>
          
        </div>
      </div>
    </>
  )
}

export default Features