import './App.css';
import {useState} from "react";
import SearchField from "./SearchForm/SearchField/SearchField";
import SearchButton from "./SearchForm/SearchButton/SearchButton";

const App = () => {
    const [searchString, setSearchString] = useState("");
    const [results, setResults] = useState([]);
    const [suggestedResults, setSuggestedResults] = useState([]);

    const [pharmaName, setPharmaName] = useState("");
    const [address, setAddress] = useState("");
    const [zip, setZip] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");

    const pharmaNameChangeHandler = (value) => {
        setPharmaName(value);
    }

    const addressChangeHandler = (value) => {
        setAddress(value);
    }

    const zipChangeHandler = (value) => {
        setZip(value);
    }

    const cityChangeHandler = (value) => {
        setCity(value);
    }

    const stateChangeHandler = (value) => {
        setState(value);
    }

    const onKeyDownHandler = async (e) => {
        if (e.keyCode === 13) {
            clearResults();
            const response = await fetch(`http://localhost:8080/search?searchString=${searchString}`);
            const body = await response.json();
            setResults(body);
            console.log(results);
        }
    };

    const onSearchClickHandler = async () => {
        clearResults();
        const searchDetails = [];
        if (pharmaName !== "") searchDetails.push(`pharmaName=${pharmaName}`);
        if (address !== "") searchDetails.push(`address=${address}`);
        if (zip !== "") searchDetails.push(`zip=${zip}`);
        if (city !== "") searchDetails.push(`city=${city}`);
        if (state !== "") searchDetails.push(`state=${state}`);
        const request = searchDetails.join(`&`);
        console.log("Request = ", request);

        const response = await fetch(`http://localhost:8080/search?${request}`);
        const body = await response.json();
        setResults(body);
        console.log(results);
    };

    const onSuggestClickHandler = async () => {
        clearResults();
        const response = await fetch(`http://localhost:8080/search/suggest?searchString=${searchString}`);
        const body = await response.json();
        setSuggestedResults(body);
        console.log(results);
    };

    const clearResults = () => {
        setResults([]);
        setSuggestedResults([]);
    }

    return (
        <div className="App">
            <header className="App-header">
                <div className="group-line">
                    <SearchField
                        label="Pharmacy Name"
                        id="pharmacy-name-selector"
                        type="search"
                        onChangeHandler={pharmaNameChangeHandler}
                    />
                    <SearchField
                        label="Address"
                        id="address-selector"
                        type="search"
                        onChangeHandler={addressChangeHandler}
                    />
                    <SearchField
                        label="Zip"
                        id="zip-selector"
                        type="search"
                        onChangeHandler={zipChangeHandler}
                    />
                    <SearchField
                        label="City"
                        id="city-selector"
                        type="search"
                        onChangeHandler={cityChangeHandler}
                    />
                    <SearchField
                        label="State"
                        id="state-selector"
                        type="search"
                        onChangeHandler={stateChangeHandler}
                    />
                    <SearchButton icon="search" label="Search!" onClick={onSearchClickHandler}/>
                </div>
            </header>
            <div className="search-results-area">
                <div>Search results go here...</div>
            </div>
        </div>
    );
}

export default App;
