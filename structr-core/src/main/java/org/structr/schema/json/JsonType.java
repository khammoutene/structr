/**
 * Copyright (C) 2010-2017 Structr GmbH
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
package org.structr.schema.json;

import java.net.URI;
import java.util.Set;

/**
 *
 *
 */
public interface JsonType extends Comparable<JsonType> {

	URI getId();
	JsonSchema getSchema();

	String getName();
	JsonType setName(final String name);

	boolean isAbstract();
	JsonType setAbstract();

	JsonMethod addMethod(final String name, final String source, final String comment);
	JsonMethod addMethod(final String name);

	JsonMethod overrideMethod(final String name, final boolean callSuper, final String implementation);

	JsonMethod addPropertyGetter(final String propertyName, final Class type);
	JsonMethod addPropertySetter(final String propertyName, final Class type);

	JsonType setExtends(final JsonType superType);
	JsonType setExtends(final URI externalReference);
	URI getExtends();

	JsonType setImplements(final URI externalReference);
	Set<URI> getImplements();

	Set<JsonProperty> getProperties();
	Set<String> getRequiredProperties();
	Set<String> getViewNames();
	Set<String> getViewPropertyNames(final String viewName);
	Set<JsonMethod> getMethods();

	JsonType addViewProperty(final String viewName, final String propertyName);

	JsonStringProperty addStringProperty(final String name, final String... views);
	JsonStringProperty addPasswordProperty(final String name, final String... views);
	JsonDateProperty addDateProperty(final String name, final String... views);
	JsonIntegerProperty addIntegerProperty(final String name, final String... views);
	JsonLongProperty addLongProperty(final String name, final String... views);
	JsonNumberProperty addNumberProperty(final String name, final String... views);
	JsonBooleanProperty addBooleanProperty(final String name, final String... views);
	JsonScriptProperty addScriptProperty(final String name, final String...views);
	JsonFunctionProperty addFunctionProperty(final String name, final String...views);
	JsonEnumProperty addEnumProperty(final String name, final String...views);
	JsonStringArrayProperty addStringArrayProperty(final String name, final String... views);
	JsonIntegerArrayProperty addIntegerArrayProperty(final String name, final String... views);
	JsonLongArrayProperty addLongArrayProperty(final String name, final String... views);
	JsonNumberArrayProperty addDoubleArrayProperty(final String name, final String... views);
	JsonBooleanArrayProperty addBooleanArrayProperty(final String name, final String... views);

	JsonReferenceProperty addReferenceProperty(final String name, final JsonReferenceProperty referencedProperty, final String... views);
}
