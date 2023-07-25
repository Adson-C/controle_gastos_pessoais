package com.example.ads_organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){

    // converter data e dataAtual
    long data = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String dataString = sdf.format(data);

    return dataString;
    }

    // retorno data mes e ano
    public static String mesAnoDataEscolihdo(String data){
        // regex 23/08/2023
           String retornoData[] = data.split("/");
           String dia = retornoData[0]; // dia 23
           String mes = retornoData[1]; // mes 08
           String ano = retornoData[2]; // ano 2023
            String mesAno = mes + ano;
            return mesAno;
    }

}
