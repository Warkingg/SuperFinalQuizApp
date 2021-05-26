package com.example.superquizapp.utility;

import java.io.File;

public class DeleteQuizFolder {
    public static void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }
    public  static void deleteQuizFolder(int quizId) {
        String filePath = "uploads/quiz_"+quizId;
        //Creating the File object
        File file = new File(filePath);
        if(file.exists()) {
            deleteFolder(file);
        }
        System.out.println("Files deleted........");
    }
}
