import './App.css';
import {useState} from "react";
import SearchField from "./SearchForm/SearchField/SearchField";
import SearchButton from "./SearchForm/SearchButton/SearchButton";
import {FormGroup, Switch} from "@blueprintjs/core";

const App = () => {
    const [results, setResults] = useState([]);
    const [suggestedResults, setSuggestedResults] = useState([]);

    const [pharmaName, setPharmaName] = useState("");
    const [address, setAddress] = useState("");
    const [zip, setZip] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");
    const [strict, setStrict] = useState(true);

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
            await onSearchClickHandler();
        }
    };

    const onSearchClickHandler = async () => {
        clearResults();
        const searchDetails = [];
        if (pharmaName !== "") searchDetails.push(`pharmaName=${encodeURIComponent(pharmaName)}`);
        if (address !== "") searchDetails.push(`address=${encodeURIComponent(address)}`);
        if (zip !== "") searchDetails.push(`zip=${encodeURIComponent(zip)}`);
        if (city !== "") searchDetails.push(`city=${encodeURIComponent(city)}`);
        if (state !== "") searchDetails.push(`state=${encodeURIComponent(state)}`);
        const request = searchDetails.join(`&`);
        console.log("Request = ", request);

        const response = await fetch(`http://${process.env.REACT_APP_PUBLIC_IP}:8080/search?${request}&strict=${strict}`);
        const body = await response.json();
        setResults(body);
        console.log(results);
    };

    const onStrictSwitchHandler = () => {
        setStrict(!strict);
    }

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
                        onKeyDownHandler={onKeyDownHandler}
                    />
                    <SearchField
                        label="Address"
                        id="address-selector"
                        type="search"
                        onChangeHandler={addressChangeHandler}
                        onKeyDownHandler={onKeyDownHandler}
                    />
                    <SearchField
                        label="Zip"
                        id="zip-selector"
                        type="search"
                        onChangeHandler={zipChangeHandler}
                        onKeyDownHandler={onKeyDownHandler}
                    />
                    <SearchField
                        label="City"
                        id="city-selector"
                        type="search"
                        onChangeHandler={cityChangeHandler}
                        onKeyDownHandler={onKeyDownHandler}
                    />
                    <SearchField
                        label="State"
                        id="state-selector"
                        type="search"
                        onChangeHandler={stateChangeHandler}
                        onKeyDownHandler={onKeyDownHandler}
                    />
                    <SearchButton icon="search" label="Search!" onClick={onSearchClickHandler}/>
                    <FormGroup label="Strict">
                        <Switch checked={strict} onClick={onStrictSwitchHandler}/>
                    </FormGroup>
                    <SearchButton icon="reset" label="Clear" onClick={clearResults}/>
                </div>
            </header>
            <div className="search-results-area">
                <div>
                    {results.map((result, index) => <div key={result + index}>{result}</div>)}
                    {suggestedResults.map((result, index) => <div key={result + index}>{result}</div>)}
                </div>
            </div>
        </div>
    );
}

export default App;
