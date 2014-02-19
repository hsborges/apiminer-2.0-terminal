package org.apiminer.view.terminal.recommender;

import org.apache.log4j.Logger;
import org.apiminer.tasks.TaskResult;
import org.apiminer.tasks.TaskStatus;
import org.apiminer.tasks.TasksController;
import org.apiminer.tasks.implementations.ExampleRecommenderTask;
import org.apiminer.view.terminal.AbstractTerminal;

public class ExampleRecommenderTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleRecommenderTerminal.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("==============================================================");
		System.out.println("                 EXAMPLE RECOMMENDER TERMINAL                 ");
		System.out.println("==============================================================");
		System.out.println();
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		LOGGER.info("Making the example recommendations");
		
		ExampleRecommenderTask task = new ExampleRecommenderTask();
		
		TasksController.getInstance().addTask(task);
		
		while (task.getStatus() != TaskStatus.FINISHED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		LOGGER.info("Recommendation process finished!");
		
		final TaskResult result = task.getResult();
		if (result == TaskResult.FAILURE) {
			//TODO
			LOGGER.info("An error occurred when recommending the examples");
			LOGGER.error("Error: ", result.getProblem());
		} else {
			LOGGER.info("The process was successfully finished!");
		}
		
	}

}
