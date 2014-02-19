package org.apiminer.view.terminal.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apiminer.SystemProperties;
import org.apiminer.daos.DatabaseType;
import org.apiminer.daos.ProjectDAO;
import org.apiminer.entities.api.Project;
import org.apiminer.util.FilesUtil;

public class DirectoryCleanerTerminal {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("==============================================================");
		System.out.println("                  DIRECTORY CLEANER TERMINAL                  ");
		System.out.println("==============================================================");
		System.out.println();
		
		String wd = SystemProperties.WORKING_DIRECTORY.getAbsolutePath();
		File wdFile = new File(wd);
		if (!wdFile.exists() || !wdFile.isDirectory()) {
			System.out.println("The working folder was not configured correctly");
			return;
		}
		
		
		List<String> folders = new ArrayList<String>();
		List<String> repositoriesProjects = new ArrayList<String>();
		
		for (File f : wdFile.listFiles()) {
			folders.add(f.getAbsolutePath());
		}
		
		ProjectDAO projectDAO = new ProjectDAO();
		for (Project p : projectDAO.findAllProjects(DatabaseType.EXAMPLES)) {
			repositoriesProjects.add(p.getRepository().getSourceFilesDirectory());
		}
		
		for (Project p : projectDAO.findAllProjects(DatabaseType.PRE_PROCESSING)) {
			repositoriesProjects.add(p.getRepository().getSourceFilesDirectory());
		}
		
		Stack<String> toRemove = new Stack<String>();
		toRemove.addAll(folders);
		toRemove.removeAll(repositoriesProjects);
		
		if (toRemove.isEmpty()) {
			System.out.println("Don't have files to remove!");
			return;
		}
		
		System.out.println("Divergent files: ");
		for (String str : toRemove) {
			System.out.println(String.format("- %s", str));
		}
		
		boolean removeAll = false;
		System.out.println("Are you sure you want to remove all the directories ('yes' to confirm)?");
		if (System.console().readLine().trim().equalsIgnoreCase("yes")) {
			removeAll = true;
		}
		
		
		while (!toRemove.isEmpty()) {
			File file = new File(toRemove.pop());
			if (!removeAll) {
				System.out.printf("Are you sure you want to remove the directory %s ('yes' to confirm)? \n", file.getAbsolutePath());
				System.out.printf("Answer: ");
				if (!System.console().readLine().trim().equalsIgnoreCase("yes")) {
					continue;
				}
			}else{
				try{
					FilesUtil.deleteFile(file);
					System.out.println(String.format("- File %s deleted. \n", file.getName()));
				}catch(IOException e){
					System.err.println(String.format("- Error on delete the file %s. \n", file.getName()));
					e.printStackTrace();
				}
				
			}
			
		}
		
		System.out.println("Process completed!");

	}
	
}
