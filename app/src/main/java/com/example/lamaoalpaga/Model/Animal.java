package com.example.lamaoalpaga.Model;

public enum Animal {
    LAMA, ALPAGA;

    public String getAnimalString(){
        if (this == LAMA){
            return "llama";
        }
        else{
            return "alpaca";
        }
    }
}
