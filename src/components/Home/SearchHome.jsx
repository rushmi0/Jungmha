import React from 'react';
import { useState, useEffect} from "react";
import axios from "axios";

import classes from './SearchHome.module.css'
function SearchHome() {
    const [province, setProvince] = useState([]);

    useEffect(() => {
        axios.get('https://raw.githubusercontent.com/kongvut/thai-province-data/master/api_province_with_amphure_tambon.json')
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
                        <input className="border-2 border-[#CBD5E0] rounded-lg text-lg  bg-[#fff] p-2 input input-bordered input-accent px-4" placeholder="Enter names"/>
                        <select className={classes.selectMenu}>
                            <option disabled selected>Price/Rate</option>
                            <option>Lowest Rate</option>
                            <option>Highest Rate</option>
                            <option>Discount(Lowest to Highest)</option>
                            <option>Discount(Highest to Lowest)</option>
                        </select>
                        <select defaultValue="" className={classes.selectMenu}>
                            <option disabled selected>Province</option>
                            {province.map((prov) => (
                                <option key={prov.id} value={prov.id} label={`${prov.name_th} - ${prov.name_en}`}/>
                            ))}

                        </select>
                        <button className={classes.searchBtn}>Search</button>
                    </div>

                </div>
            </div>
        </>
    );
}

export default SearchHome;