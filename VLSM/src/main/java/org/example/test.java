package org.example;

import java.util.Scanner;

public class test {
    public static void gan(int n){
        Scanner sc = new Scanner(System.in);
        int []arr = new int[n];
        for (int i = 0; i < n; i++) {
//            arr[i] = sc.nextInt();
            arr[0] = 24;
            arr[1] = 26;
            arr[2] = 27;

        }
        for(int i = 0; i < n; i++){
            System.out.println("/"+ arr[i]);
        }
        System.out.println("clone");
        for(int i = 0; i < n; i++){
            System.out.println(arr[i]+i);
        }
    }

    public static void main(String[] args) {
     gan(3);
    }
}
