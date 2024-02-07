import React from 'react'
import logoBanner from '../assets/dog.svg'
import bgDog from '../assets/bgDog.svg'
import classes from './Homepage.module.css'

function Homepage() {
  return (
    <>
        <div className={classes.box}>
            <div className={classes.containerAuto}>
                <div className={classes.gridBox}>
                    <div className='p-[6rem]'>
                        <p className={classes.header1}>Find Someone to <a className={classes.header2}>walk your dog</a> while you busy.</p>
                        <p className={classes.subHead}>At Jungmha, we provide reliable and affordable dog sitting services in Bangkok. We understand that your dog is a part of your family, and we'll treat them with the same love and care that you do.

</p>
                        <button className='btn bg-[#45BBBD] text-xl text-[#f7f7f7] border-0 rounded hover:bg-[#A6E2E3] hover:text-[#064E5C] mt-3 font-medium'>Find Caretaker</button>
                    </div>
                    <div>
                        <img className={classes.imgBanner} src={logoBanner} alt="Jungmha" />
                    </div>
                </div>
            </div>                       
        </div>
    </>
  )
}

export default Homepage