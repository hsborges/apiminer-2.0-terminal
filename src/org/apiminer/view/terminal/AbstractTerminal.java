package org.apiminer.view.terminal;

import org.apiminer.tasks.TasksController;
import org.apiminer.util.LoggerUtil;


public abstract class AbstractTerminal {
	
	static {
		LoggerUtil.logEvents();
	}

	@Override
	protected void finalize() throws Throwable {
		TasksController.getInstance().stop();
		super.finalize();
	}

}