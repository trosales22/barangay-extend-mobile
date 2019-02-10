package com.breadbuster.barangayextend.classes.Handlers;

import com.rengwuxian.materialedittext.MaterialEditText;

public class    Validation {
    public static boolean validateMaterialEditText(MaterialEditText[] fields){
        boolean isEmpty = false;

        for(int i=0; i<fields.length; i++){
            MaterialEditText currentField=fields[i];
            if(currentField.getText().toString().trim().length()<=0){
                currentField.setError("This item or field is required.");
                currentField.requestFocus();
                isEmpty = true;
            }
        }
        return isEmpty;
    }

    public static boolean validateSingleMaterialEditText(MaterialEditText field){
        boolean isEmpty = false;

        if(field.getText().toString().trim().length()<=0){
            field.setError("This item or field is required.");
            field.requestFocus();
            isEmpty = true;
        }

        return isEmpty;
    }
}
