/*
 * Copyright (C) 2010-2020 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.entity;

import org.structr.common.SecurityContext;
import org.structr.common.error.ErrorBuffer;
import org.structr.common.error.FrameworkException;
import org.structr.core.graph.ModificationQueue;
import static org.structr.core.graph.NodeInterface.name;
import org.structr.core.graph.TransactionCommand;
import org.structr.schema.ReloadSchema;
import org.structr.schema.action.Actions;

/**
 *
 *
 */
public abstract class SchemaReloadingNode extends AbstractNode {

	public abstract boolean reloadSchemaOnCreate();
	public abstract boolean reloadSchemaOnModify(final ModificationQueue modificationQueue);
	public abstract boolean reloadSchemaOnDelete();

	@Override
	public void onCreation(SecurityContext securityContext, ErrorBuffer errorBuffer) throws FrameworkException {

		Actions.clearCache();

		super.onCreation(securityContext, errorBuffer);

		if (reloadSchemaOnCreate()) {

			// register transaction post processing that recreates the schema information
			TransactionCommand.postProcess("reloadSchema", new ReloadSchema());
		}
	}

	@Override
	public void onModification(SecurityContext securityContext, ErrorBuffer errorBuffer, final ModificationQueue modificationQueue) throws FrameworkException {

		Actions.clearCache();

		super.onModification(securityContext, errorBuffer, modificationQueue);

		if (reloadSchemaOnModify(modificationQueue)) {

			// register transaction post processing that recreates the schema information
			TransactionCommand.postProcess("reloadSchema", new ReloadSchema());
		}
	}

	@Override
	public void onNodeDeletion() {

		Actions.clearCache();

		super.onNodeDeletion();

		if (reloadSchemaOnDelete()) {

			// register transaction post processing that recreates the schema information
			TransactionCommand.postProcess("reloadSchema", new ReloadSchema());
		}
	}

	public String getResourceSignature() {
		return getProperty(name);
	}

	public String getClassName() {
		return getProperty(name);
	}

	public String getSuperclassName() {

		final String superclassName = getProperty(SchemaNode.extendsClass);
		if (superclassName == null) {

			return AbstractNode.class.getSimpleName();
		}

		return superclassName.substring(superclassName.lastIndexOf(".")+1);
	}
}
