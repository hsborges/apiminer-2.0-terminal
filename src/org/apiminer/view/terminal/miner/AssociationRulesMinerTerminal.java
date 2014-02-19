package org.apiminer.view.terminal.miner;

import org.apache.log4j.Logger;
import org.apiminer.tasks.TaskResult;
import org.apiminer.tasks.TaskStatus;
import org.apiminer.tasks.TasksController;
import org.apiminer.tasks.implementations.AssociationRulesMineTask;
import org.apiminer.view.terminal.AbstractTerminal;

import weka.associations.AbstractAssociator;
import weka.associations.FPGrowth;

public class AssociationRulesMinerTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(AssociationRulesMinerTerminal.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("=============================================");
		System.out.println("      ASSOCIATION RULES MINER [TERMINAL]     ");
		System.out.println("=============================================");
		System.out.println();
		
		Integer minSupport = null;
		do {
			System.out.printf("Minimum support: ");
			try {
				minSupport = Integer.parseInt(System.console().readLine());
			} catch (NumberFormatException e) {}
		}while(minSupport == null);
		
		Float minConfidence = null;
		do {
			System.out.printf("Minimum confidence: ");
			try {
				minConfidence = Float.parseFloat(System.console().readLine());
			} catch (NumberFormatException e) {}
		}while(minConfidence == null);
		
		Integer maxNumberOfItens = null;
		do {
			System.out.printf("Max number of itens in itensets: ");
			try {
				maxNumberOfItens = Integer.parseInt(System.console().readLine());
			} catch (NumberFormatException e) {}
		}while(maxNumberOfItens == null);
		
		System.out.println();
		System.out.println("Select the algorithm (default is FPGrowth): ");
		System.out.printf("1 - %s\n", FPGrowth.class.getCanonicalName());
		Class<? extends AbstractAssociator> algorithm = null;
		do {
			System.out.printf("Algorithm: ");
			try {
				Integer option = Integer.parseInt(System.console().readLine());
				if (option == 1) {
					algorithm = FPGrowth.class;
				}
			} catch (NumberFormatException e) {}
		}while(algorithm == null);
		
		System.out.println();
		System.out.println("Log: ");
		System.out.println("--------------");
		
		LOGGER.info("Mining the association rules");
		
		AssociationRulesMineTask associationRulesMineTask = new AssociationRulesMineTask(algorithm, maxNumberOfItens, minConfidence , minSupport);
		TasksController.getInstance().addTask(associationRulesMineTask);
		
		while (associationRulesMineTask.getStatus() != TaskStatus.FINISHED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		
		LOGGER.info("Mining process finished!");
		
		final TaskResult result = associationRulesMineTask.getResult();
		if (result == TaskResult.FAILURE) {
			//TODO
			LOGGER.info("An error occurred when mining the rules");
			LOGGER.error("Error: ", result.getProblem());
		} else {
			LOGGER.info("The process was successfully finished!");
		}
		
	}

}
