package org.apiminer.view.terminal.analyzer;

import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apiminer.entities.api.Project;
import org.apiminer.entities.api.ProjectStatus;
import org.apiminer.entities.api.Repository;
import org.apiminer.entities.api.RepositoryType;
import org.apiminer.tasks.TaskResult;
import org.apiminer.tasks.TaskStatus;
import org.apiminer.tasks.TasksController;
import org.apiminer.tasks.implementations.ClientAnalyzerTask;
import org.apiminer.view.terminal.AbstractTerminal;

public class ClientAnalyzerTerminal extends AbstractTerminal {
	
	protected static final Logger LOGGER = Logger.getLogger(ClientAnalyzerTerminal.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("=====================================================");
		System.out.println("             Client Analyzer [Terminal]                ");
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
		
		project.setAddedAt(new Date(System.currentTimeMillis()));
		
		repository.setProject(project);
		project.setRepository(repository);
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		LOGGER.info("Analyzing the API client");
		
		ClientAnalyzerTask clientAnalyzerTask = 
				new ClientAnalyzerTask(project.getName(), 
						project.getSummary(), 
						project.getUrlSite(), 
						ProjectStatus.UNKNOWN, 
						project.getRepository().getRepositoryType(), 
						project.getRepository().getUrlAddress());
		
		TasksController.getInstance().addTask(clientAnalyzerTask);
		
		while (clientAnalyzerTask.getStatus() != TaskStatus.FINISHED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		
		LOGGER.info("Analyze of the API client finished!");
		
		final TaskResult result = clientAnalyzerTask.getResult();
		if (result == TaskResult.FAILURE) {
			//TODO
			LOGGER.info("An error occurred when analyzing the API client");
			LOGGER.error("Error: ", result.getProblem());
		} else {
			LOGGER.info("The process was successfully finished!");
		}
		
		
	}

}
