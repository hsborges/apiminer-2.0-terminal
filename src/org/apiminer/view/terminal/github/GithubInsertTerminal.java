package org.apiminer.view.terminal.github;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.apiminer.entities.api.GitHubRepository;
import org.apiminer.entities.api.Repository;
import org.apiminer.tasks.AbstractTask;
import org.apiminer.tasks.TaskResult;
import org.apiminer.tasks.TasksController;
import org.apiminer.tasks.implementations.GitHubInsertTask;
import org.apiminer.util.git.GitHubUtil;
import org.apiminer.util.git.RepositoryUtil;
import org.apiminer.view.terminal.AbstractTerminal;


public class GithubInsertTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(GithubInsertTerminal.class);
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("===========================================================");
		System.out.println("                 Github Project Inserter                   ");
		System.out.println("===========================================================");
		System.out.println();
		
		System.out.println("Source type: ");
		System.out.println("1 - External XML ");
		System.out.println("2 - Search in Github ");
		System.out.println();
		
		Integer startPage = null;
		Integer endPage = null;
		
		List<Repository> repositories = null;
		GitHubUtil searchTool = null;
		String keyword = null;
		
		Integer option = null;
		while (option == null) {
			System.out.printf("Option: ");
			try {
				option = Integer.parseInt(System.console().readLine());
				if (option < 1 || option > 2) {
					System.out.println("- Invalid option, try again.");
					option = null;
				}
			} catch (Exception e) {
				System.out.println("- Invalid option, try again.");
			}
		}
		
		if (option == 1){
			
			String path = null;
			System.out.println();
			while (path == null){
				System.out.printf("Enter the file path: ");
				File f = new File(System.console().readLine());
				if (!(f.exists() && f.isFile())) {
					System.out.println("- Invalid file! try again.");
				}else{
					path = f.getAbsolutePath();
					repositories = RepositoryUtil.parse(path);
				}
			}
			
		}else if (option == 2){
			
			System.out.printf("Keyword to search: ");
			keyword = System.console().readLine();
			System.out.printf("Username (Optional): ");
			String username = System.console().readLine();
			String password = null;
			
			if (username != null && !username.isEmpty()) {
				System.out.printf("Password: ");
				password = new String(System.console().readPassword());
			}
			
			do {
				System.out.printf("Enter the start page (1 is the first):");
				startPage = Integer.parseInt(System.console().readLine());
			}while(startPage == null);
			
			do{
				try{
					System.out.printf("Enter the end page (Unlimited):");
					endPage = Integer.parseInt(System.console().readLine());
				}catch (Exception e) {}
			}while(endPage == null);
			
			if (username != null) {
				searchTool = new GitHubUtil(username, password);
			}else{
				searchTool = new GitHubUtil();
			}
			
		}else{
			System.exit(0);
		}
		
		Integer minCommits = null;
		while (minCommits == null) {
			System.out.printf("Minimum number of commits: ");
			try {
				minCommits = Integer.parseInt(System.console().readLine());
				if (minCommits < 0) {
					System.out.println("- Invalid value, try again.");
				}
			} catch (Exception e) {
				System.out.println("- Invalid value, try again.");
			}
		}
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		if (option == 2) {
			repositories = new LinkedList<Repository>(searchTool.searchRepositories(keyword, startPage, endPage));
		}
		
		Stack<Repository> stack = new Stack<Repository>();
		stack.addAll(repositories);
		
		List<AbstractTask> tasks = new LinkedList<AbstractTask>();
		final TasksController instance = TasksController.getInstance();
		
		while (!stack.isEmpty()) {
			Repository rep = stack.pop();
			
			if (rep instanceof GitHubRepository) {
				GitHubRepository grep = (GitHubRepository) rep;
				
				if (grep.getCommits() < minCommits) {
					continue;
				}
				
				GitHubInsertTask task;
				try {
					task = new GitHubInsertTask(grep);
				} catch (Exception e) {
					LOGGER.error("Error:", e);
					continue;
				}
				
				tasks.add(task);
				instance.addTask(task);
				
				char[] completeBar = new char[Math.round((repositories.size()-stack.size()) * 10 / repositories.size())];
				char[] todoBar = new char[Math.round(stack.size() * 10 / repositories.size())];
				Arrays.fill(completeBar, '-');
				Arrays.fill(todoBar, ' ');
				String progress = String.format("Progress: [%s%s] (%d remaining)", new String(completeBar), new String(todoBar), stack.size());
				LOGGER.info(progress);
				
				do{
					Thread.sleep(1000*2);
				}while(instance.numberOfQueuedTasks() > 0);
			}
		}
		
		while(instance.numberOfRunningTask() > 0 || instance.numberOfQueuedTasks() > 0){
			Thread.sleep(1000*2);
		}
		
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).getResult() == TaskResult.SUCCESS) {
				tasks.remove(i--);
			}
		}
		
		if (!tasks.isEmpty()) {
			LOGGER.info("The following projects were not inserted: \n");
			for(AbstractTask task : tasks) {
				LOGGER.info(String.format("Description: %s \n - Problem: %s \n",task.toString(), task.getResult().getProblem().getLocalizedMessage()));
			}
		}
		
		
	}

}
