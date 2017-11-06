/**
 * Copyright (C) 2010-2017 Structr GmbH
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
package org.structr.odf;

import java.util.Set;
import org.structr.api.service.LicenseManager;
import org.structr.module.StructrModule;
import org.structr.schema.SchemaInfo;
import org.structr.schema.action.Actions;

/**
 *
 */
public class ODFModule implements StructrModule{

	@Override
	public void onLoad(final LicenseManager licenseManager) {
	}

        @Override
        public String getName() {
                return "odf";
        }

        @Override
        public Set<String> getDependencies() {
                return null;
        }

        @Override
        public Set<String> getFeatures() {
                return null;
        }

        @Override
        public void insertImportStatements(SchemaInfo schemaInfo, StringBuilder buf) {
        }

        @Override
        public void insertSourceCode(SchemaInfo schemaInfo, StringBuilder buf) {
        }

        @Override
        public void insertSaveAction(SchemaInfo schemaNode, StringBuilder buf, Actions.Type type) {
        }

        @Override
        public Set<String> getInterfacesForType(SchemaInfo schemaNode) {
                return null;
        }

}
