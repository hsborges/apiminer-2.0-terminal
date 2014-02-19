package org.apiminer.view.terminal.github;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apiminer.entities.api.Repository;
import org.apiminer.util.git.GitHubUtil;
import org.apiminer.util.git.RepositoryUtil;
import org.apiminer.view.terminal.AbstractTerminal;


public class GithubSearchTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(GithubSearchTerminal.class);
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.out.println("===========================================================");
		System.out.println("                   GITHUB PROJECT FINDER                   ");
		System.out.println("===========================================================");
		System.out.println();
		
		System.out.printf("Keyword to search: ");
		String keyword = System.console().readLine();
		
		System.out.printf("Username (Optional): ");
		String username = System.console().readLine();
		String password = null;
		
		if (username != null && !username.isEmpty()) {
			System.out.printf("Password: ");
			password = new String(System.console().readPassword());
		}
		
		Integer startPage = null;
		do {
			System.out.printf("Enter the start page (0 is the first):");
			startPage = Integer.parseInt(System.console().readLine());
		}while(startPage == null);
		
		Integer endPage = null;
		do{
			try{
				System.out.printf("Enter the end page (Unlimited):");
				endPage = Integer.parseInt(System.console().readLine());
			}catch (Exception e) {}
		}while(endPage == null);
		
		System.out.println("Select the output directory.");
		File outputDir = null;
		while(outputDir == null){
			System.out.printf("Path: ");
			outputDir = new File(System.console().readLine());
			if (!outputDir.exists() || !outputDir.isDirectory()) {
				outputDir = null;
				System.out.println("- Invalid directory. Try again.");
			}
		}
		
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		GitHubUtil gitHubUtil = null;
		if (username != null) {
			gitHubUtil = new GitHubUtil(username, password);
			
		}else{
			gitHubUtil = new GitHubUtil();
		}

		LOGGER.info("Searching for projects in Github ...");
		
		List<Repository> repositories = new LinkedList<Repository>(gitHubUtil.searchRepositories(keyword, startPage, endPage));
		
		LOGGER.info("Exporting the results ...");
		
		RepositoryUtil.export(new File(outputDir, "github-search-result" + System.currentTimeMillis()).getAbsolutePath(), repositories);
		
		LOGGER.info("Process finished!");
	}

}
