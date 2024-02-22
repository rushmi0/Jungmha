import React, {useContext, useEffect, useState} from 'react';
import classes from './Booking.module.css'
import axios from "axios";
import {BASE_URL} from "../../../constants/BaseEndpoint.js";
import {AppContext} from "../Home/CaretakerCard.jsx";

function Booking() {
    const data = useContext(AppContext);
    console.log("Walker data: ",data);
    const [breed, setBreed] = useState([]);

    const base_url = BASE_URL["baseEndpoint"];
    const url = base_url + "/api/v1/dogs";
    useEffect(() => {
        axios.get(url)
            .then((res) => {
                setBreed(res.data);
            });
    }, [])
    return (
        <>

        </>
    );
}

export default Booking;