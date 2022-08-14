import './App.css';
import {useState} from "react";

const App = () => {
    const [searchString, setSearchString] = useState("");

    const onClickHandler = async () => {
        const response = await fetch(`http://localhost:8080/search?searchString=${searchString}`);
        const body = await response.json();
        console.log(body);
    };
    const onBlurHandler = (event) => {
        setSearchString(event.target.value);
        console.log(event.target.value);
    };
    return (
        <div className="App">
            <header className="App-header">
                <input type="search" onBlur={onBlurHandler} placeholder="Search..."/>
                <button onClick={onClickHandler} type="submit">Search</button>

            </header>
        </div>
    );
}

export default App;
