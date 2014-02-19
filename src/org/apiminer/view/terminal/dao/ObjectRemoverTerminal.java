package org.apiminer.view.terminal.dao;

import javax.persistence.PersistenceException;

import org.apiminer.daos.DatabaseType;
import org.apiminer.daos.MiningDAO;
import org.apiminer.daos.ProjectDAO;
import org.apiminer.entities.api.Project;
import org.apiminer.view.terminal.AbstractTerminal;

public final class ObjectRemoverTerminal extends AbstractTerminal {
	
	private ObjectRemoverTerminal(){}
	
	public static final void removeProject(long projectId, DatabaseType database) throws Exception{
		if (projectId < 0) {
			throw new IllegalArgumentException("Invalid project identificator");
		}
		
		try {
			new ProjectDAO().delete(projectId, database);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static final void removeMiningResult(long miningResultId){
		if (miningResultId < 0) {
			throw new IllegalArgumentException("Invalid project identificator");
		}
		
		try {
			new MiningDAO().delete(miningResultId, DatabaseType.PRE_PROCESSING);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final void removeProject(String projectName, DatabaseType databaseType) throws Exception{
		if (projectName == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		
		Project project = new ProjectDAO().find(projectName.trim(), databaseType);
		if (project == null) {
			throw new IllegalArgumentException("Project not registred!");
		}else{
			removeProject(project.getId(), databaseType);
		}
	}
	
	public static void main(String args[]) throws Exception{

		System.out.println("=============================================");
		System.out.println("           OBJECT REMOVER TERMINAL           ");
		System.out.println("=============================================");
		
		System.out.println();
		System.out.println("Select the object type to be removed: ");
		System.out.println("1 - Projects ");
		System.out.println("2 - Mining Results ");
		System.out.println("9 - EXIT ");
		System.out.println();
		
		while (true) {
			int option = Integer.parseInt(System.console().readLine());
			
			switch (option) {
			case 1:
				
				break;
				
			case 2:	
				
				break;
				
			case 9:	
				return;
				
			default:
				break;
			}
			
			break;
		}
		
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("Bye!");
		super.finalize();
	}
	
}
