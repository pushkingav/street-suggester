import './App.css';
import {useState} from "react";
import SearchField from "./SearchForm/SearchField/SearchField";
import SearchButton from "./SearchForm/SearchButton/SearchButton";

const App = () => {
    const [searchString, setSearchString] = useState("");
    const [results, setResults] = useState([]);
    const [suggestedResults, setSuggestedResults] = useState([]);

    const onChangeHandler = (event) => {
        setSearchString(event.target.value);
        console.log(event.target.value);
    };

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
        const response = await fetch(`http://localhost:8080/search?searchString=${searchString}`);
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
                    />
                    <SearchField
                        label="Address"
                        id="address-selector"
                        type="search"
                    />
                    <SearchField
                        label="Zip"
                        id="zip-selector"
                        type="search"
                    />
                    <SearchField
                        label="City"
                        id="city-selector"
                        type="search"
                    />
                    <SearchField
                        label="State"
                        id="state-selector"
                        type="search"
                    />
                    <SearchButton icon="search" label="Search!"/>
                </div>
            </header>
            <div className="search-results-area">
                <div>Search results go here...</div>
            </div>
        </div>
    );
}

export default App;
