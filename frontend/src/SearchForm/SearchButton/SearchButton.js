import React from 'react';
import {Button, FormGroup} from "@blueprintjs/core";

const SearchButton = ({label, icon, small}) => {
    return (
        <FormGroup label={label}>
            <Button icon={icon} small={small} />
        </FormGroup>
    );
};

export default SearchButton;