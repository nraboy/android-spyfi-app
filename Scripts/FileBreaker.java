package filebreaker;

import java.io.*;

public class FileBreaker {
    
    public static void splitFile(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            int size = 512000;
            byte buffer[] = new byte[size];
            int fileCount = 1;
            while(true) {
                int i = fis.read(buffer, 0, size);
                if(i <= -1) {
                    break;
                }
                String fn = filename + fileCount;
                FileOutputStream fos = new FileOutputStream(fn);
                fos.write(buffer, 0, i);
                fos.flush();
                fos.close();
                fileCount++;
            }
        } catch(FileNotFoundException e) {
            System.out.println("File Was Not Found");
        } catch(IOException e) {
            System.out.println("Could Not Read File");
        }
    }
    
    public static void joinFile(String splitFilename, String joinFilename, int fileTotal) {
        try {
            FileOutputStream fos = new FileOutputStream(joinFilename);
            int b = 0;
            for(int fileCount = 1; fileCount <= fileTotal; fileCount++) {
                FileInputStream fis = new FileInputStream(splitFilename + fileCount);
                while((b = fis.read()) != -1) {
                    fos.write(b);
                }
            }
            fos.flush();
            fos.close();
        } catch(FileNotFoundException e) {
            System.out.println("File Was Not Found");
        } catch(IOException e) {
            System.out.println("Could Not Read File");
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        //joinFile("test.db", "myDB.db", 5);
        System.out.println("Starting Process...");
        if(args[0].equals("split"))
            splitFile(args[1]);
        else if(args[0].equals("join"))
            joinFile(args[1], args[2], Integer.parseInt(args[3]));
        System.out.println("Complete!");
    }
}
