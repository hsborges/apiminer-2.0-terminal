package org.apiminer.view.terminal.extractor;

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
import org.apiminer.tasks.implementations.ExampleExtractorTask;
import org.apiminer.view.terminal.AbstractTerminal;

public class ExampleExtractorTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleExtractorTerminal.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("=====================================================");
		System.out.println("             EXAMPLE EXTRACTOR [TERMINAL]              ");
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
		
		LOGGER.info("Extracting examples from the client " + project.getName());
		
		ExampleExtractorTask exampleExtractorTask = 
				new ExampleExtractorTask(project.getName(), 
						project.getSummary(), 
						project.getUrlSite(), 
						ProjectStatus.UNKNOWN, 
						project.getRepository().getRepositoryType(), 
						project.getRepository().getUrlAddress());
		
		TasksController.getInstance().addTask(exampleExtractorTask);
		
		while (exampleExtractorTask.getStatus() != TaskStatus.FINISHED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		
		LOGGER.info("Extraction process finished!");
		
		final TaskResult result = exampleExtractorTask.getResult();
		if (result == TaskResult.FAILURE) {
			//TODO
			LOGGER.info("An error occurred when extracting the examples!");
			LOGGER.error("Error: ", result.getProblem());
		} else {
			LOGGER.info("The process was successfully finished!");
		}
		
		
		
	}

}
