import React from 'react'
import logoBanner from '../assets/dog.svg'

function Homepage() {
  return (
    <>
        <div className="bg-[#DBF3FF] shadow">
            <div className='container mx-auto'>
                <div className="grid grid-cols-2 justify-items-center items-center">
                    <div className='p-[6rem]'>
                        <p className='text-4xl font-bold text-[#0B4550] mb-3'>Find Someone to <a className='text-4xl font-bold text-[#45BBBD]'>walk your dog</a> while you busy.</p>
                        <p className='text-lg mb-4 text-[#0B4550]'>At Jungmha, we provide reliable and affordable dog sitting services in Bangkok. We understand that your dog is a part of your family, and we'll treat them with the same love and care that you do.

</p>
                        <button className='btn bg-[#45BBBD] text-xl text-[#f7f7f7] border-0 rounded hover:bg-[#A6E2E3] hover:text-[#064E5C] mt-3 font-medium'>Find Caretaker</button>
                    </div>
                    <div>
                        <img className="w-[700px]" src={logoBanner} alt="Jungmha" />
                    </div>
                </div>
            </div>                       
        </div>
    </>
  )
}

export default Homepage