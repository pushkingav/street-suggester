import './App.css';

const App = () => {
    const onClick = async () => {
        const response = await fetch('http://localhost:8080/search?searchString=avenue');
        const body = await response.json();
        console.log(body);
    };
    return (
        <div className="App">
            <header className="App-header">
                <input type="search" placeholder="Search..."/>
                <button onClick={onClick} type="submit">Search</button>

            </header>
        </div>
    );
}

export default App;
