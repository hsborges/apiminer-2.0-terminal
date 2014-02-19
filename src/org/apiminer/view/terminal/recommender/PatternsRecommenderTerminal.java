package org.apiminer.view.terminal.recommender;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apiminer.daos.MiningDAO;
import org.apiminer.entities.mining.MiningResult;
import org.apiminer.tasks.TaskResult;
import org.apiminer.tasks.TaskStatus;
import org.apiminer.tasks.TasksController;
import org.apiminer.tasks.implementations.PatternsRecommenderTask;
import org.apiminer.view.terminal.AbstractTerminal;

public class PatternsRecommenderTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(PatternsRecommenderTerminal.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("==============================================================");
		System.out.println("             PATTERNS RECOMMENDER [TERMINAL]                 ");
		System.out.println("==============================================================");
		System.out.println();
		
		Long miningResultId = null;
		Map<Long, MiningResult> ids = new HashMap<Long, MiningResult>();
		
		System.out.println("Select the mining result (id) to make the recommendations:");
		for (MiningResult mr : new MiningDAO().findAll()) {
			ids.put(mr.getId(), mr);
			System.out.println(String.format("%d - Added at %s, %d rules from %d transactions", mr.getId(), SimpleDateFormat.getDateTimeInstance().format(mr.getAddedAt()), mr.getRules().size(), mr.getNumberOfTransactions()));	
		}
		do {
			System.out.printf("ID: ");
			try {
				long selectedId = Long.parseLong(System.console().readLine());
				if (ids.containsKey(selectedId)) {
					miningResultId = selectedId;
				}else{
					System.out.println("- Invalid identificator, try again.");
				}
			} catch (Exception e) {
				miningResultId = null;
			}
		}while(miningResultId == null);
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		LOGGER.info("Making the usage patterns recommendations");
		
		PatternsRecommenderTask task = new PatternsRecommenderTask(ids.get(miningResultId));
		
		TasksController.getInstance().addTask(task);
		
		while (task.getStatus() != TaskStatus.FINISHED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		
		LOGGER.info("Recommendation process finished!");
		
		final TaskResult result = task.getResult();
		if (result == TaskResult.FAILURE) {
			//TODO
			LOGGER.info("An error occurred when making the recommendations");
			LOGGER.error("Error: ", result.getProblem());
		} else {
			LOGGER.info("The process was successfully finished!");
		}
		

	}

}
