package org.apiminer.view.terminal.extractor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apiminer.daos.DatabaseType;
import org.apiminer.daos.ProjectDAO;
import org.apiminer.entities.api.Project;
import org.apiminer.util.inserter.ExamplesExtractorUtil;
import org.apiminer.view.terminal.AbstractTerminal;

public class ReExtractExampleTerminal extends AbstractTerminal {
	
	private static final Logger LOGGER = Logger.getLogger(ReExtractExampleTerminal.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("==============================================================");
		System.out.println("                RE-EXTRACT EXAMPLES TERMINAL                  ");
		System.out.println("==============================================================");
		System.out.println();
		
		Project project = null;
		
		Map<Long, Project> projectsMap = new HashMap<Long, Project>();
		System.out.println("Select the project (by id) to re-extract the examples");
		for (Project p : new ProjectDAO().findAllClients(DatabaseType.EXAMPLES)) {
			projectsMap.put(p.getId(), p);
			System.out.println(String.format("%d - %s, added at %s, client of %s", p.getId(), p.getName(), SimpleDateFormat.getDateTimeInstance().format(p.getAddedAt()), p.getClientOf().getName()));
		}
		
		do {
			System.out.printf("ID: ");
			Long projectId = Long.parseLong(System.console().readLine());
			if ( projectsMap.containsKey(projectId) ) {
				project = projectsMap.get(projectId);
			} else {
				System.out.println("- Invalid identificator, try again.");
			}
		}while( project == null );
		
		System.gc();
		
		System.out.println();
		System.out.println("Log:");
		System.out.println("-------------");
		
		try {
			ExamplesExtractorUtil.extractExamples(project, true);
			LOGGER.info("Examples were re-extracted with success! Bye.");
		} catch (Exception e) {
			LOGGER.error("Error on process the re-extraction. See the stack trace.",e);
		}

	}

}
