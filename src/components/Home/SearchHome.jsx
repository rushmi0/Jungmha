import React from 'react';
import { useState, useEffect} from "react";
import axios from "axios";

import classes from './SearchHome.module.css'
import CaretakerCard from "./CaretakerCard.jsx";
function SearchHome() {
    const [province, setProvince] = useState([]);

    const [searchName, setSearchname] = useState("");
    const [selectPrice, setPrice] = useState("");
    const [selectVerify, setVerify] = useState("All");
    const [selectLocation, setLocation] = useState("Any");


    const priceHandler = (e) => {
        console.log(e.target.value);
        setPrice(e.target.value);
    }

    const verifyHandler = (e) => {
        console.log(e.target.value);
        setVerify(e.target.value);
    }

    const locationHandler = (e) => {
        console.log(e.target.value);
        setLocation(e.target.value);
    }

    const onClickFilter = () => {
        console.log(searchName);
        console.log(selectPrice);
        console.log(selectLocation);
        console.log(selectVerify);
    }


    useEffect(() => {
        axios.get('https://raw.githubusercontent.com/kongvut/thai-province-data/master/api_amphure.json')
            .then((res) => {
                setProvince(res.data);
            });
    }, [])




    return (
        <>
            <div id="caretaker" className="bg-[#45BBBD] p-14">
                <p className="text-center text-3xl font-bold text-[#f7f7f7] mb-4">Search for caretaker</p>
                <div className={classes.searchMenu}>
                    <div className={classes.searchGrid}>
                        <input className="border-2 border-[#CBD5E0] rounded-lg text-lg  bg-[#fff] p-2 input input-bordered input-accent px-4" placeholder="Enter names" onChange={(e) => setSearchname(e.target.value)}/>
                        {/*<select className={classes.selectMenu} onChange={priceHandler}>*/}
                        {/*    <option selected>All</option>*/}
                        {/*    <option >Lowest Rate</option>*/}
                        {/*    <option >Highest Rate</option>*/}
                        {/*</select>*/}
                        <select defaultValue="" className={classes.selectMenu} onChange={locationHandler}>
                            <option disabled selected>Amphure</option>
                            <option selected>Any</option>
                            {province.map((prov) => (
                                <option key={prov.id} >
                                    {prov.name_th}
                                </option>
                            ))}

                        </select>
                        <select className={classes.selectMenu} onChange={verifyHandler}>
                        <option selected>All</option>
                        <option>Verify</option>
                        <option>Not Verify</option>
                        </select>
                        <button className={classes.searchBtn} onClick={onClickFilter}>Search</button>
                    </div>

                </div>
            </div>
            
            <CaretakerCard name={searchName.toLowerCase()} price={selectPrice} location={selectLocation} verify={selectVerify}/>

        </>
    );
}

export default SearchHome;