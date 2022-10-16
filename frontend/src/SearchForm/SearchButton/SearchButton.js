import React from 'react';
import {Button, FormGroup} from "@blueprintjs/core";

const SearchButton = ({label, icon, small, onClick}) => {
    return (
        <FormGroup label={label}>
            <Button icon={icon} small={small} onClick={onClick} />
        </FormGroup>
    );
};

export default SearchButton;