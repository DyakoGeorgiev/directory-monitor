package com.folder.monitor;

import java.nio.file.NoSuchFileException;
import java.util.Scanner;

public class Main {
  public static void main (String[] args) {
    executeMenu();
  }

  private static int getUserChoiceOperation() {
    String line = "------------------------------";
    Scanner in = new Scanner(System.in);
    int choice;
    System.out.println();
    System.out.println("What operation do you want to execute?");
    System.out.println("Type the operation number in the command line.");
    System.out.println(line);
    System.out.println("1 - Folder monitor");
    System.out.println("2 - Folder monitor finder");
    System.out.println(line);
    System.out.println("Type 0 to exit the program.");
    choice = in.nextInt();
    return choice;
  }

  public static void executeMenu(){
    Scanner in = new Scanner(System.in);
    while (true) {
      switch (getUserChoiceOperation()) {
        case 0:
          System.out.println("Program exit!");
          return;
        case 1:
          //Task 1 - Folder Monitor
          System.out.println("Type the monitored folder path: ");
          String monitoredFolder = in.nextLine();
          System.out.println("Type the target folder path:");
          String targetFolder = in.nextLine();

          DirectoryMonitor folderMonitor = new DirectoryMonitorImpl();
          try {
            folderMonitor.execute(monitoredFolder,targetFolder);
          } catch (NoSuchFileException e) {
            e.printStackTrace();
          }
          break;
        case 2:
          //Task 2 - Folder Monitor Finder
          String oldFileName = in.nextLine();
          String folderTarget = in.nextLine();
          DirectoryMonitorFinderImpl folderMonitorFinder = new DirectoryMonitorFinderImpl();
          try {
            System.out.println(folderMonitorFinder.execute(oldFileName,folderTarget));
          } catch (NoSuchFileException e) {
            e.printStackTrace();
          }
          break;
        default:
            System.out.println("Type the command number between 1-12 and 0 if you want to stop execution.");
            break;
      }
    }
  }
}
