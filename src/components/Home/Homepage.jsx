import React, {useEffect, useState} from 'react'
import {motion} from "framer-motion";

import classes from './Homepage.module.css'

function Homepage() {
    const [index, setIndex] = useState(0);
    const timeoutRef = React.useRef(null);

    const bgImg = [
        "../src/assets/login1.jpg",
        "../src/assets/login2.jpg",
        "../src/assets/Caretaker.jpg"
    ];

    const delay = 5000;


    useEffect(() => {

        timeoutRef.current =
            setTimeout(
                () => {
                    setIndex(
                        index === 2 ? 0 : index + 1
                    )
                }
                   ,
                delay
                )
        console.log("index url: ", index)
        return () => {};
    }, [index]);






  return (
    <>
        <div className={classes.box}>
            <div className="mx-auto">
                <div className={classes.gridBox}>
                    <div className={classes.textBg}>
                        <p className={classes.header1}>Find Someone to <a className={classes.header2}>walk your dog</a> while you busy.</p>
                        <p className={classes.subHead}>At Jungmha, we provide reliable and affordable dog sitting services in Bangkok. We understand that your dog is a part of your family, and we'll treat them with the same love and care that you do.
                        </p>
                        <a className='btn bg-[#45BBBD] text-xl text-[#f7f7f7] border-0 rounded hover:bg-[#A6E2E3] hover:text-[#064E5C] mt-3 font-medium' href="#caretaker">Find Caretaker</a>
                    </div>


                        <motion.div
                            id="imgSlide"
                            key={index}
                            className="w-full h-full xl:order-last lg:order-first md:order-first sm:order-first order-first"
                            style={{
                                backgroundImage: `url(${bgImg[index]})`,
                                backgroundSize: "cover",
                                backgroundPosition: "center",
                            }}
                            initial={{x: 800, opacity: 0}}
                            animate={{x: 0, opacity: 1}}
                            exit={{x: -800, opacity: 0}}
                            transition={{
                                duration: 2
                            }}

                        >

                            {/*<img className={classes.imgBanner} src={logoBanner} alt="Jungmha" />*/}
                        </motion.div>


                </div>
            </div>
        </div>
    </>
  )
}

export default Homepage