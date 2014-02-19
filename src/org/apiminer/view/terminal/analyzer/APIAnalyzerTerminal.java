package org.apiminer.view.terminal.analyzer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apiminer.entities.api.Project;
import org.apiminer.entities.api.ProjectStatus;
import org.apiminer.entities.api.Repository;
import org.apiminer.entities.api.RepositoryType;
import org.apiminer.tasks.TaskResult;
import org.apiminer.tasks.TaskStatus;
import org.apiminer.tasks.TasksController;
import org.apiminer.tasks.implementations.APIAnalyzerTask;
import org.apiminer.util.FilesUtil;
import org.apiminer.view.terminal.AbstractTerminal;

public class APIAnalyzerTerminal extends AbstractTerminal {
	
	protected static final Logger LOGGER = Logger.getLogger(APIAnalyzerTerminal.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("=====================================================");
		System.out.println("             API Analyzer [Terminal]                ");
		System.out.println("=====================================================");
		System.out.println();
		
		Project project = new Project();
		
		System.out.printf("Project Name: ");
		project.setName(System.console().readLine());
		
		System.out.printf("Summary: ");
		project.setSummary(System.console().readLine());
		
		System.out.printf("Website: ");
		project.setUrlSite(System.console().readLine());
		
		Repository repository = new Repository();
		RepositoryType repositoryType = null;
		do{
			System.out.printf("Repository type %s: ", Arrays.toString(RepositoryType.values()));
			try {
				repositoryType = RepositoryType.parse(System.console().readLine());
				if (repositoryType == null) {
					System.out.println("- Invalid repository type!");
				}
			} catch (Exception e) {
				System.out.println("- Invalid repository type!");
			}
		}while( repositoryType == null);
		repository.setRepositoryType(repositoryType);
		
		System.out.printf("URL or path of sources: ");
		repository.setUrlAddress(System.console().readLine());
		if (repositoryType.equals(RepositoryType.LOCAL)) {
			repository.setSourceFilesDirectory(repository.getUrlAddress());
		}
		
		Collection<String> jars = null;
		do{
			System.out.printf("Directory of the jars of the project: ");
			jars = FilesUtil.collectFiles(System.console().readLine().trim(), ".jar", true);
//			if (jars == null || jars.isEmpty()) {
//				jars = null;
//				System.out.println("- The directory does not have jar files!");
//			}
		}while(jars == null);
		repository.setJars(new HashSet<String>(jars));
		
		project.setAddedAt(new Date(System.currentTimeMillis()));
		project.setClientOf(null);
		
		repository.setProject(project);
		project.setRepository(repository);
		
		System.out.printf("Include protected methods ('yes' to confirm): ");
		boolean includeProtected = System.console().readLine().trim().equalsIgnoreCase("yes");
		
		System.out.printf("Include private methods ('yes' to confirm): ");
		boolean includePrivate = System.console().readLine().trim().equalsIgnoreCase("yes");
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		LOGGER.info("Analyzing the API source code");
		
		APIAnalyzerTask apiAnalyzerTask = 
				new APIAnalyzerTask(project.getName(), 
						project.getSummary(), 
						project.getUrlSite(), 
						ProjectStatus.UNKNOWN, 
						project.getRepository().getRepositoryType(), 
						project.getRepository().getUrlAddress(), 
						true, 
						includePrivate, 
						includeProtected);
		
		TasksController.getInstance().addTask(apiAnalyzerTask);
		
		while (apiAnalyzerTask.getStatus() != TaskStatus.FINISHED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		
		LOGGER.info("Analyze of the API finished!");
		
		final TaskResult result = apiAnalyzerTask.getResult();
		if (result == TaskResult.FAILURE) {
			//TODO
			LOGGER.info("An error occurred when analyzing the API");
			LOGGER.error("Error: ", result.getProblem());
		} else {
			LOGGER.info("The process was successfully finished!");
		}
		
	}

}
