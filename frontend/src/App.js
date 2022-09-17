import './App.css';
import {useState} from "react";
import PharmacyName from "./SearchForm/PharmacyName";

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
                <PharmacyName />
            </header>
        </div>
    );
}

export default App;
