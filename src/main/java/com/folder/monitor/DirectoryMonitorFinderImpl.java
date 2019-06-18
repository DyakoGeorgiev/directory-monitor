package com.folder.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;

public class DirectoryMonitorFinderImpl implements DirectoryMonitorFinder{

  private static final String FOLDER_MONITOR_DIRECTORY_PATH = System.getProperty("user.home")+File.separator+"folder_monitor"+File.separator+"logger_monitor.txt";

  /**
   * Method finds absolute absolutePath from a given old file name of a file that was renamed after that.
   *
   * @param oldFileName - a name of a previously renamed file
   * @param targetFolder - absolutePath to directory where the new file is placed
   * @throws NoSuchFileException in case the {@param oldFileName} or {@param targetFolder} does not exist.
   */

  @Override
  public String execute (String oldFileName, String targetFolder) throws NoSuchFileException {

    if(oldFileName == null || targetFolder == null) {
      throw new NoSuchFileException("Please provide correct file name or target folder.");
    }

    if(Files.notExists(Paths.get(FOLDER_MONITOR_DIRECTORY_PATH))) {
      throw new NoSuchFileException("No file to search for filename.");
    }

    HashMap<String,String> hashMapFromFile = getHashMapFromFile();

    if(hashMapFromFile.get(oldFileName) == null) {
      throw new NoSuchFileException("No file with that name was found.");
    }

    File f = new File(targetFolder);


    if(!f.isDirectory()) {
      throw new NoSuchFileException("No directory was found with that name.");
    }

    File[] matchingFiles = f.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith(hashMapFromFile.get(oldFileName)) && name.endsWith("txt");
      }
    });

    if(!checkForFiles(matchingFiles)) {
      throw new NoSuchFileException("No file with that name was found.");
    }

    //Because the function returns only one string containing absolute absolutePath,
    // the function return the last file moved with that name.
    int lastFile = matchingFiles.length-1;

    return matchingFiles[lastFile].getAbsolutePath();
  }


  /**
   * Utility function that reads a file and stores the information as key:value pair.
   *
   */

  private HashMap<String,String> getHashMapFromFile (){
    String delimiter = ":";
    HashMap<String, String> map = new HashMap<>();
    try(BufferedReader in = new BufferedReader(new FileReader(FOLDER_MONITOR_DIRECTORY_PATH))){
      String line;
      while ((line = in.readLine()) != null) {
        String[] parts = line.split(delimiter);
        map.put(parts[0], parts[1]);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map;
  }

  /**
   * Utility function that checks if list of files has files.
   *
   */
  private boolean checkForFiles(File[] matchingFiles){
    return matchingFiles != null;
  }
}
