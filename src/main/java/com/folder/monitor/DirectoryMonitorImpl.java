package com.folder.monitor;

import com.folder.monitor.logger.FileLogger;
import com.folder.monitor.logger.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;


public class DirectoryMonitorImpl implements DirectoryMonitor {


  private final File LOG_FILE;
  private HashMap<String, String> oldAndNewFileNames;

  private static final String PATH_TO_MONITOR_DIRECTORY = System.getProperty("user.home") + File.separator + "folder_monitor";
  private static final String PATH_TO_MONITOR_LOG_DIRECTORY = PATH_TO_MONITOR_DIRECTORY + File.separator + "logs";
  private static final String PATH_TO_LOG_DIRECTORY_FILE = PATH_TO_MONITOR_DIRECTORY + File.separator + "logger_monitor.txt";
  private final String LOG_FILE_NAME = "folder_monitor.log";
  private final Logger logger;


  public DirectoryMonitorImpl() {
    LOG_FILE = new File(PATH_TO_MONITOR_LOG_DIRECTORY + File.separator + LOG_FILE_NAME);
    oldAndNewFileNames = new HashMap<>();
    logger = new FileLogger(LOG_FILE,new StringBuilder());
  }


  /**
   * Performs argument checking and calls method createFileWatcher to monitor folder
   *
   * @param monitoredFolder - a directory to monitor
   * @param targetFolder - a folder to move the renamed files
   * @throws NoSuchFileException in case the {@param monitoredFolder} does not exist.
   */

  @Override
  public void execute (String monitoredFolder, String targetFolder) throws NoSuchFileException {

    File folderToMonitor = new File(monitoredFolder);

    if(!folderToMonitor.isDirectory()){
      throw new NoSuchFileException("Please provide existing directory to monitor.");
    }

    Path monitoredDirectory = Paths.get(monitoredFolder);
    Path targetDirectory = Paths.get(targetFolder);

    try {
      createFileWatcher(monitoredDirectory,targetDirectory);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs letters distribution.
   *
   * @param monitoredDirectory - a directory to monitor
   * @param targetDirectory - a directory to move the renamed files
   * @throws IOException  in case the {Watch Service} fails.
   * @throws IOException in case creating folder monitor directory method fails.
   * @throws IOException in case {@param monitoredDirectory} fails to be registered.
   * @throws InterruptedException in case method take() cant return WatchKey object
   *
   */

  private void createFileWatcher(Path monitoredDirectory, Path targetDirectory) throws IOException, InterruptedException {
    FileSystem fileSystem = monitoredDirectory.getFileSystem();
    WatchService watchService = fileSystem.newWatchService();
    WatchKey key = monitoredDirectory.register(watchService,
      StandardWatchEventKinds.ENTRY_CREATE);

    createFolderMonitorDirectory();
    createFolderMonitorLogSubDirectory();
    createLogFile();

    while(key.isValid()) {
      WatchKey take = watchService.take();
      List<WatchEvent<?>> events = take.pollEvents();
      for (WatchEvent<?> event : events) {
        WatchEvent.Kind kind = event.kind();

        if (kind == StandardWatchEventKinds.OVERFLOW) { continue; }
        WatchEvent<Path> ev = cast(event);
        Path fileName = ev.context();
        Path fullFilename = monitoredDirectory.resolve(fileName);

        if(!Files.isRegularFile(fullFilename, LinkOption.NOFOLLOW_LINKS)) {
          logger.log("The newly submitted file "+ fileName + " is not a regular file. Skipping it.");
        } else {
          //Function to create random file name
          String generatedNewFileName = RandomStringUtils.randomAlphabetic(10);

          logger.log("Got file " + fileName + ". The file will be renamed to " + generatedNewFileName + ".");

          //Function to move the newly created file into the targetDirectory with the new file name.
          Files.move(fullFilename, Paths.get(targetDirectory+File.separator+generatedNewFileName+".txt"), StandardCopyOption.REPLACE_EXISTING);

          //Function to remove the .txt from the filename
          String fileNameWithOutExt = FilenameUtils.removeExtension(fileName.toString());

          //Putting old file name and the new one in a hashmap
          oldAndNewFileNames.put(fileNameWithOutExt,generatedNewFileName);
          //calling method to write the hash map in a file under directory
          writeHashMapToFile(fileNameWithOutExt,generatedNewFileName);
        }
      }
      logger.flush();
      take.reset();
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>)event;
  }


  /**
   * Creates folder monitor directory ../folder_monitor/
   *
   * @throws IOException  in case directory with the same name exists and cant be deleted
   */
  private void createFolderMonitorDirectory () throws IOException {

    File directory = new File(PATH_TO_MONITOR_DIRECTORY);
    //Check to see if there exists already a directory with the same name, if exists - delete it.
    if(directory.isDirectory()){
      FileUtils.deleteDirectory(directory);
    }

    //Creating new directory ../distribution
    directory.mkdirs();
  }


  /**
   * Creates folder monitor log sub directory   ../folder_monitor/logs/
   *
   * @throws IOException  in case directory cant be created
   */
  private void createFolderMonitorLogSubDirectory () throws IOException {
    Path subDirectory = Paths.get(PATH_TO_MONITOR_LOG_DIRECTORY);
    Files.createDirectories(subDirectory);
  }

  /**
   * Writes old file name and new file name as key:vakue pair in .txt file
   *
   * @throws IOException
   */

  private void writeHashMapToFile(String fileNameWithOutExt, String generatedNewFileName) throws IOException {

    File file = Paths.get(PATH_TO_LOG_DIRECTORY_FILE).toFile();
    try(PrintWriter out = new PrintWriter(
      new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(file,true), StandardCharsets.UTF_8)))) {
      out.println(fileNameWithOutExt + ":" + generatedNewFileName);
      }
    }

  /**
   * Creates log file
   *
   * @throws IOException  in case directory cant be created
   */

  private void createLogFile() {
    try {
      LOG_FILE.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
