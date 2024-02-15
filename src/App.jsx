import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import UserSignUp from './components/UserSignUp';
import CaretakerSignUp from './components/CaretakerSignUp';
import Homepage from './components/Home/Homepage.jsx';
import RootLayout from './components/RootLayout';
import Home from './page/Home';
import UserLogin from './components/Login/UserLogin';
import CaretakerLogin from './components/Login/CaretakerLogin';
import UserProfile from "./components/profile/UserProfile.jsx";
import CaretakerProfile from "./components/profile/CaretakerProfile.jsx";
import Booking from "./components/Booking/Booking.jsx";


function App() {

  return (
    <>
       <BrowserRouter>
        <Routes>
          {/* RootLayout contain navigation bar as a component to use in everypage*/}
          <Route element={<RootLayout />}>
          {/* every elements inside RootLayout will show Navigation Bar on the top of the page */}
            <Route index element={<Home/>}></Route>
            <Route path="/profile/user" element={<UserProfile/>}></Route>
            <Route path="/profile/caretaker" element={<CaretakerProfile/>}></Route>
            <Route path="/booking" element={<Booking/>}></Route>

          </Route>
          {/* RootLayout-end */}
          {/* Login */}
          <Route path="/login/user" element={<UserLogin/>}></Route>
          <Route path="/login/caretaker" element={<CaretakerLogin />}> </Route>
          {/* Register */}
          <Route path="/register/user" element={<UserSignUp/>}></Route>
          <Route path="/register/caretaker" element={<CaretakerSignUp/>}></Route>

          
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
