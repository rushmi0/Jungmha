import React, {useEffect, useState} from 'react'
import classes from './Homepage.module.css'


function Homepage() {
    const [index, setIndex] = useState(0);
    // const timeoutRef = React.useRef(null);
    // const [prevBg ,setPrevBg] = useState("");
    const bgImg = [
        "public/img/login1.jpg",
        "public/img/dogWalker.jpg",
        "public/img/login2.jpg",
        "public/img/Caretaker.jpg"
    ];


    // useEffect(() => {
    //
    //     timeoutRef.current =
    //         setTimeout(
    //             () => {
    //                 setIndex(
    //                     //(prevIndex) =>
    //                 //     prevIndex === bgImg.length - 1 ? 0 : prevIndex + 1
    //                 index === 2 ? 0 : index + 1
    //                 )
    //             }
    //                ,
    //             delay
    //             )
    //     return () => {};
    // }, [index]);


    useEffect(() => {
        const interval = setInterval(() => {
            if (index === 3) {
                setIndex(0);
                return;
            }
            setIndex((prevValue) => {
                if (prevValue === 3) {
                    return 0;
                }
                return prevValue + 1;
            });
        }, 5000);
        return () => clearInterval(interval);
    }, []);



  return (
    <>
        <div className={classes.box}>
            <div className="mx-auto">
                <div className={classes.gridBox}>
                    <div className={classes.textBg}>
                        <p className={classes.header1}>Find Someone to <a className={classes.header2}>walk your
                            dog</a> while you busy.</p>
                        <p className={classes.subHead}>At Jungmha, we provide reliable and affordable dog sitting
                            services in Bangkok. We understand that your dog is a part of your family, and we'll treat
                            them with the same love and care that you do.
                        </p>
                        <a className='btn bg-[#45BBBD] text-xl text-[#f7f7f7] border-0 rounded hover:bg-[#A6E2E3] hover:text-[#064E5C] mt-3 font-medium'
                           href="#caretaker">Find Caretaker</a>
                    </div>
                    <div className="flex w-full h-full xl:order-last lg:order-first md:order-first sm:order-first order-first"
                         style={{ transform: `translate3d(${-index * 100}%, 0, 0)`,
                             transitionDuration: '1.5s'}}
                    >
                        {bgImg.map((background, index) => (
                                <div
                                    key={index}
                                    className="w-full h-full  carousel-item"
                                    style={{
                                        backgroundImage: `url(${bgImg[index]})`,
                                        backgroundSize: "cover",
                                        backgroundPosition: "center",
                                    }}
                                >
                                </div>
                        ))}
                    </div>

                </div>
            </div>
        </div>
    </>
  )
}

export default Homepage