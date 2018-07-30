/**
 * Copyright (C) 2010-2018 Structr GmbH
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
package org.structr.text;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.config.Settings;

/**
 *
 *
 */
public class FulltextTokenizer extends Writer {

	private static final Logger logger = LoggerFactory.getLogger(FulltextTokenizer.class.getName());
	public static final Set<Character> SpecialChars = new LinkedHashSet<>();

	private final int wordMinLength          = Settings.IndexingMinLength.getValue();
	private final int wordMaxLength          = Settings.IndexingMaxLength.getValue();
	private final StringBuilder rawText      = new StringBuilder();
	private final StringBuilder wordBuffer   = new StringBuilder();
	private final List<String> words         = new LinkedList<>();
	private String language                  = "en";
	private char lastCharacter               = 0;
	private int consecutiveCharCount         = 0;
	private int wordCount                    = 0;

	static {

		SpecialChars.add('_');
		SpecialChars.add('ä');
		SpecialChars.add('ö');
		SpecialChars.add('ü');
		SpecialChars.add('Ä');
		SpecialChars.add('Ö');
		SpecialChars.add('Ü');
		SpecialChars.add('ß');
		SpecialChars.add('§');
		SpecialChars.add('-');
		SpecialChars.add('%');
		SpecialChars.add('/');
		SpecialChars.add('@');
		SpecialChars.add('$');
		SpecialChars.add('€');
		SpecialChars.add('æ');
		SpecialChars.add('¢');
		SpecialChars.add('.');
		SpecialChars.add(',');
	}

	public FulltextTokenizer() {
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException {

		final int limit  = off + len;
		final int length = Math.min(limit, cbuf.length);

		for (int i=off; i<length; i++) {

			final char c = cbuf[i];

			// remove occurrences of more than 10 identical chars in a row
			if (c == lastCharacter) {

				if (consecutiveCharCount++ >= 10) {
					continue;
				}

			} else {

				consecutiveCharCount = 0;
			}

			if (!Character.isAlphabetic(c) && !Character.isDigit(c) && !SpecialChars.contains(c)) {

				flush();

				if (Character.isWhitespace(c)) {

					rawText.append(c);

				} else {

					rawText.append(" ");
				}

			} else {

				wordBuffer.append(c);
				rawText.append(c);
			}

			lastCharacter = c;
		}
	}

	public String getLanguage() {
		return language;
	}

	public String getRawText() {
		return rawText.toString().trim();
	}

	public List<String> getWords() {
		return words;
	}

	@Override
	public void flush() throws IOException {

		String word = wordBuffer.toString().trim();
		if (StringUtils.isNotBlank(word)) {

			// try to separate numbers / dates etc.
			if (word.matches("-?[0-9\\.,/]+")) {

				while (word.endsWith(",")) {

					word = word.substring(0, word.length() - 1);
				}

				// remove minus sign from suspected numbers
				word = word.replace("-", "");

				addWord(word);

			} else {

				final String[] parts = word.split("[\\.,]+");
				final int len        = parts.length;

				for (int i=0; i<len; i++) {

					String part = parts[i].trim();
					part        = part.replaceAll("[\\-/]+", "");

					if (StringUtils.isNotBlank(part) && !StringUtils.isNumeric(part)) {

						addWord(part.toLowerCase());
					}
				}
			}
		}

		wordBuffer.setLength(0);
	}

	@Override
	public void close() throws IOException {

		flush();

		final LanguageDetector detector = new OptimaizeLangDetector();
		detector.loadModels();

		final LanguageResult result = detector.detect(getRawText());
		if (result != null) {

			language = result.getLanguage();
		}
	}

	public int getWordCount() {
		return wordCount;
	}

	// ----- private methods -----
	private void addWord(final String word) {

		final int length = word.length();
		if (length >= wordMinLength && length <= wordMaxLength) {

			words.add(word);

			wordCount++;
		}
	}
}
