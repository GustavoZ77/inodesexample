package org.uag;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private byte inodes [][] = new byte[3][11];
    private byte slots  [][] = new byte[11][11];
    private byte superBlock  [][] = new byte[2][11];
    public static String FILE_NAME = "p3.txt";

    private int changesCounter = 0;

    public static void main(String[] args) {
        Main main = new Main();
        Scanner myInput = new Scanner( System.in );
        Integer a = 0;
        try {
            main.readFromFile(Main.FILE_NAME);
            do{
                System.out.println("********Menu*****");
                System.out.println(" A) Enter 1 to modify slot");
                System.out.println(" B) Enter 2 to modify inodes");
                System.out.println(" C) Enter 3 to persist the data");
                System.out.println(" D) Enter -1 to leave");
                try{
                    a = myInput.nextInt();
                    if(a==1) {
                        System.out.println("Enter slot postiion: ");
                        a = myInput.nextInt();
                        main.updateBlocks(a);
                    }else if(a==2){
                        System.out.println("Enter inode number: ");
                        a = myInput.nextInt();
                        main.updateInode(a.byteValue());
                    }else if(a==3){
                        System.out.println("Saving...");
                        main.flushData(Main.FILE_NAME);
                    }
                }catch (Exception e){ }
            }while(a > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromFile(String fileName  ) throws IOException {
        RandomAccessFile file = new RandomAccessFile(fileName, "r");
        long SIZE =  file.length();
        int c=0;
        int offset=0;
        //read *
        file.seek(offset);
        byte[] stars = new byte[22];
        file.read(stars);
        //read #
        file.seek(23);
        byte[] pounds = new byte[32];
        file.read(pounds);
        //read 0
        file.seek(56);
        byte[] zeros = new byte[121];
        file.read(zeros);
        file.close();
        //loading data to memory structures
        this.loadStars(stars);
        this.loadPound(pounds);
        this.loadZero(zeros);

        this.printOnScreen();

    }

    private void loadStars(byte [] starts){
        int cont = 0;
        while(cont < ((starts.length)/2)){
            superBlock[0][cont] = starts[cont];
            superBlock[1][cont] = starts[(starts.length-1)-cont];
            cont++;
        }
    }

    private void loadPound(byte [] pounds){
        int cont = 0;
        while(cont <= (Math.round(pounds.length)/3)){
            inodes[0][cont] = pounds[cont];
            inodes[1][cont] = pounds[((pounds.length-1)/3)+cont];
            inodes[2][cont] = pounds[(pounds.length-1)-cont];
            cont++;
        }
    }

    private void loadZero(byte [] zeros){
        int cont = 0;
        for(int x=0;x<=10;x++){
            for(int y=0;y<=10;y++){
                slots[x][y] = zeros[cont];
            }
        cont++;
        }
    }

    public void printOnScreen(){
        System.out.println("*****BEGIN DATA BLOCK*****");
        System.out.print( "\n" );
        Arrays.asList(superBlock).forEach(Main::printer);
        Arrays.asList(inodes).forEach(Main::printer);
        Arrays.asList(slots).forEach(Main::printer);
        System.out.print( "\n" );
        System.out.println("******END DATA BLOCK*****");
    }

    private static void printer(byte[] bytes) {
      for(int x=0;x<bytes.length;x++){
          System.out.print((char) bytes[x]);
      }
      System.out.println();
    }

    public void updateBlocks(int pos ){
        if(pos < 11){
            for(int x=0;x<11;x++){
                this.slots[pos-1][x] = '@';
            }
            changesCounter++;
            updateSuperBlock();
        }else{
            System.err.println("out of memory");
        }
    }

    public void updateInode(byte update){
        if(update < 33){
            int pos = update/11;
            int offset = (update%11) > 0 ? (update%11) -1:0;
            inodes[pos][offset] = '@';
            changesCounter++;
            updateSuperBlock();
        }else{
            System.err.println("out of memory");
        }

    }

    public void updateSuperBlock(){
        this.superBlock[changesCounter > 10 ? 1 : 0][changesCounter > 10 ? (changesCounter/2)-1:changesCounter-1] = (byte) changesCounter;
        this.printOnScreen();
    }

    public void flushData(String fileName){
        RandomAccessFile file;
        try {
            file = new RandomAccessFile(fileName, "rw");
            for (byte[] x : Arrays.asList(superBlock)) {
                file.write(x);
            }
            for (byte[] x : Arrays.asList(inodes)) {
                file.write(x);
            }
            for (byte[] x : Arrays.asList(slots)) {
                file.write(x);
            }
            file.close();
            this.readFromFile(Main.FILE_NAME);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
