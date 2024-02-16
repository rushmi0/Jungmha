


const shiftTo = () => {

    const randomBytes = (length) => {
        let byteArray = [];
        for (let i = 0; i < length; i++) {
            byteArray.push(Math.floor(Math.random() * 256));
        }
        return byteArray;
    }

    return {
      randomBytes
    };

};

export default shiftTo;