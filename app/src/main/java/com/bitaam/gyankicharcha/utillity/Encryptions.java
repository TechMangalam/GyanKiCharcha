package com.bitaam.gyankicharcha.utillity;

import android.util.Base64;
import android.util.Log;

public  class Encryptions {

    String s;


    public Encryptions() {
    }



    public String getEncreption(String s, String p1, String p2){

        s = ""+ Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);

        int pnCounter=s.length()-1,sCounter=0,pSum=0;
        char[] sarr = s.toCharArray();

        while((pnCounter)>=0){
            pSum = (int) p1.charAt(sCounter) + (int)p2.charAt(sCounter);
            System.out.println(pSum);
            int cInt = (int)sarr[pnCounter] + pSum%10;
            System.out.println("CiNT"+cInt);
            sarr[pnCounter] = (char)cInt;

            if (sCounter>=9){
                sCounter=0;
            }else {
                sCounter ++;
            }
            pnCounter--;

        }
        Log.d("Test",new String(sarr));
        return new String(sarr);
    }

    public String getDecreption(String s,String p1,String p2){

        int pnCounter=s.length()-1,sCounter=0,pSum=0;
        char[] sarr = s.toCharArray();

        while((pnCounter)>=0){
            pSum = (int) p1.charAt(sCounter) + (int)p2.charAt(sCounter);
            System.out.println(pSum);
            int cInt = (int)sarr[pnCounter] - pSum%10;
            System.out.println("CiNT"+cInt);
            sarr[pnCounter] = (char)cInt;

            if (sCounter>=9){
                sCounter=0;
            }else {
                sCounter ++;
            }
            pnCounter--;

        }
        Log.d("Test",new String(Base64.decode(new String(sarr),Base64.NO_WRAP)));
        return new String(Base64.decode(new String(sarr),Base64.NO_WRAP));
    }

}
