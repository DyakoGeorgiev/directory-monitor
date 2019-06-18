package com.folder.monitor;

import java.nio.file.NoSuchFileException;

public interface DirectoryMonitorFinder {


  String execute(String oldFileName, String targetFolder) throws NoSuchFileException;
}

