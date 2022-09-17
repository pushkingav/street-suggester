import './App.css';
import {useState} from "react";

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
                <div className="search-group">
                    <div className="bp4-input-group .bp4-large">
                        <span className="bp4-icon bp4-icon-search"></span>
                        <input className="bp4-input" onChange={onChangeHandler}
                               onKeyDown={onKeyDownHandler} type="search" placeholder="Search input" dir="auto"/>
                    </div>
                    <button onClick={onSearchClickHandler} className="bp4-button .bp4-small">Search</button>
                    <button onClick={onSuggestClickHandler} className="bp4-button">Suggest</button>
                </div>
                <div>
                    {results.map((result, index) => <div key={result + index}>{result}</div>)}
                    {suggestedResults.map((result, index) => <div key={result + index}>{result}</div>)}
                </div>
            </header>
        </div>
    );
}

export default App;
