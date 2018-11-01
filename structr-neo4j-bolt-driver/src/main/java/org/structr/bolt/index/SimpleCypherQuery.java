/**
 * Copyright (C) 2010-2018 Structr GmbH
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
package org.structr.bolt.index;

import java.util.LinkedHashMap;
import java.util.Map;
import org.structr.api.search.QueryContext;

/**
 *
 */
public class SimpleCypherQuery implements PageableQuery {

	private final Map<String, Object> params = new LinkedHashMap<>();
	private QueryContext queryContext        = new QueryContext();
	private String base                      = null;
	private int pageSize                     = 0;
	private int page                         = 0;

	public SimpleCypherQuery(final String base) {

		this.pageSize = 100000;
		this.base     = base;
	}

	@Override
	public void nextPage() {
		page++;
	}

	@Override
	public int pageSize() {
		return this.pageSize;
	}

	@Override
	public String getSortKey() {
		return null;
	}

	@Override
	public String getStatement() {

		final StringBuilder buf = new StringBuilder(base);

		buf.append(" SKIP ");
		buf.append(page * pageSize);
		buf.append(" LIMIT ");
		buf.append(pageSize);

		return buf.toString();
	}

	@Override
	public Map<String, Object> getParameters() {
		return params;
	}

	@Override
	public QueryContext getQueryContext() {
		return queryContext;
	}

	@Override
	public boolean idsOnly() {
		return false;
	}
}
