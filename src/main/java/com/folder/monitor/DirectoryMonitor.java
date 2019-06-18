package com.folder.monitor;

import java.nio.file.NoSuchFileException;

public interface DirectoryMonitor {

  void execute(String monitoredFolder, String targetFolder) throws NoSuchFileException;
}
