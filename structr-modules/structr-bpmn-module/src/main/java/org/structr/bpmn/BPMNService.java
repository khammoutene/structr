/**
 * Copyright (C) 2010-2019 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.bpmn;

import java.util.LinkedList;
import java.util.List;
import org.neo4j.cypher.internal.compiler.v3_1.helpers.RuntimeJavaValueConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.service.Command;
import org.structr.api.service.RunnableService;
import org.structr.api.service.ServiceDependency;
import org.structr.api.service.StructrServices;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.App;
import org.structr.core.app.StructrApp;
import org.structr.core.graph.Tx;
import org.structr.schema.SchemaService;
import org.structr.bpmn.model.BPMNProcessNode;
import org.structr.bpmn.model.BPMNProcessStepNode;

/**
 */
@ServiceDependency(SchemaService.class)
public class BPMNService extends Thread implements RunnableService {

	private static final Logger logger = LoggerFactory.getLogger(BPMNService.class);

	private long lastRun    = 0L;
	private boolean running = false;

	public BPMNService() {

		super("BPMNService");

		this.setDaemon(true);
	}

	@Override
	public void startService() throws Exception {
		logger.info("Starting..");
		start();
	}

	@Override
	public void stopService() {
		logger.info("Shutting down..");
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void injectArguments(final Command command) {
	}

	@Override
	public boolean initialize(final StructrServices services) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return true;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void initialized() {
	}

	@Override
	public boolean isVital() {
		return false;
	}

	@Override
	public boolean waitAndRetry() {
		return false;
	}

	@Override
	public String getModuleName() {
		return "bpmn";
	}

	@Override
	public void run() {

		running = true;

		while (running) {

			for (final BPMNProcessNode process : fetchActiveProcesses()) {
				
				process.step();
			}

			/**
			 * The engine, the process or the step needs to know what to do and which
			 * of the possible next steps 
			 */

			try { Thread.sleep(1000); } catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	// ----- private methods -----
	private List<BPMNProcessNode> fetchActiveProcesses() {

		final App app                 = StructrApp.getInstance();
		final List<BPMNProcessNode> steps = new LinkedList<>();

		try (final Tx tx = app.tx()) {

			steps.addAll(app.nodeQuery(BPMNProcessNode.class).and(StructrApp.key(BPMNProcessNode.class, "active"), true).getAsList());

			tx.success();
			
		} catch (FrameworkException fex) {
			fex.printStackTrace();
		}

		return steps;
	}
}