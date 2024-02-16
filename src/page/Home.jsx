import React from 'react'
import Homepage from '../components/Home/Homepage.jsx'
import Features from '../components/Home/Features.jsx'
import SearchHome from "../components/Home/SearchHome.jsx";
import CaretakerCard from "../components/Home/CaretakerCard.jsx";

function Home() {
  return (
    <>
        <Homepage/>
        <Features/>
        <SearchHome/>
        <CaretakerCard/>
    </>
  )
}

export default Home