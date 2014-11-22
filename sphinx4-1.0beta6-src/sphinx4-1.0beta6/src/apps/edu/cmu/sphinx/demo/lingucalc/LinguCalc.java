/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.lingucalc;

import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import java.io.IOException;
import java.net.URL;

import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

/**
 * A demonstration of how to use JSGF grammars in sphinx-4. This program
 * demonstrates how an application can cycle through a number of JSGF grammars
 * as well as how an application can build up grammar rules directly
 * 
 */
public class LinguCalc {
	private Recognizer recognizer;
	private JSGFGrammar jsgfGrammar;
	private Microphone microphone;
	private BaseRecognizer jsapiRecognizer;
	private Parser parser;

	/**
	 * Creates a new JSGFDemo. The demo will retrieve the configuration from the
	 * jsgf.config.xml file found in the classpath for the JSGFDemo. The
	 * jsgf.config.xml should at the minimum define a recognizer with the name
	 * 'recognizer', a JSGFGrammar named 'jsgfGrammar' and a Microphone named
	 * 'microphone'.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws PropertyException
	 *             if a property configuration occurs
	 * @throws InstantiationException
	 *             if a problem occurs while creating any of the recognizer
	 *             components.
	 * @throws EngineStateError
	 *             If engine is initialized in a wrong state
	 * @throws EngineException
	 *             If engine fails to initialize
	 */
	public LinguCalc() throws IOException, PropertyException,
			InstantiationException, EngineException, EngineStateError {

		URL url = LinguCalc.class.getResource("lingucalc.config.xml");
		ConfigurationManager cm = new ConfigurationManager(url);

		// retrive the recognizer, jsgfGrammar and the microphone from
		// the configuration file.

		recognizer = (Recognizer) cm.lookup("recognizer");
		jsgfGrammar = (JSGFGrammar) cm.lookup("jsgfGrammar");
		microphone = (Microphone) cm.lookup("microphone");

		jsapiRecognizer = new BaseRecognizer(jsgfGrammar.getGrammarManager());
		jsapiRecognizer.allocate();
		
		parser = new Parser();
	}

	/**
	 * Executes the demo. The demo will cycle through four separate JSGF
	 * grammars,a 'movies' grammar, a 'news' grammar a 'books' grammar and a
	 * 'music' grammar. The news, books and movies grammars are loaded from the
	 * corresponding JSGF grammar file, while the music grammar is loaded from a
	 * file, but then augmented via RuleGrammar.setRule.
	 */
	public void execute() throws IOException, GrammarException {
		System.out.println("JSGF Demo Version 1.0\n");

		System.out.print(" Loading recognizer ...");
		recognizer.allocate();
		System.out.println(" Ready");

		if (microphone.startRecording()) {
			loadAndRecognize("lingucalc");
		} else {
			System.out.println("Can't start the microphone");
		}

		System.out.print("\nDone. Cleaning up ...");
		recognizer.deallocate();

		System.out.println(" Goodbye.\n");
		System.exit(0);
	}

	/**
	 * Load the grammar with the given grammar name and start recognizing speech
	 * using the grammar. Spoken utterances will be echoed to the terminal. This
	 * method will return when the speaker utters the exit phrase for the
	 * grammar. The exit phrase is a phrase in the grammar with the word 'exit'
	 * as a tag.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws GrammarException
	 *             if a grammar format error is detected
	 */
	private void loadAndRecognize(String grammarName) throws IOException,
			GrammarException {
		try {
			jsgfGrammar.loadJSGF(grammarName);
		} catch (JSGFGrammarException e) {
			throw new GrammarException(e.getMessage());
		} catch (JSGFGrammarParseException e) {
			throw new GrammarException(e.getMessage());
		}
		dumpSampleSentences(grammarName);
		/*
		for (int i = 0; i < 20; i++) {
			System.out.println("===================================================");
			String s = jsgfGrammar.getRandomSentence();
			System.out.println("The Result of:");
			System.out.println(s);
			System.out.println("is:");
			System.out.println(parser.parse(s));
			System.out.println("===================================================");
		}
		*/
		System.out.println("===================================================");
		String s = "two plus three plus five";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "two times three divided by six";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "two squared";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "three cubed";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "store last result";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "retrieve last result";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "log base two of thirty five";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "two power three times three";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "one two five three times two three";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		s = "one two five three";
		System.out.println("The Result of:");
		System.out.println(s);
		System.out.println("is:");
		System.out.println(parser.parse(s));
		System.out.println("===================================================");
		
		recognizeAndReport();
	}

	/**
	 * Performs recognition with the currently loaded grammar. Recognition for
	 * potentially multiple utterances until an 'exit' tag is returned.
	 * 
	 * @throws GrammarException
	 *             if an error in the JSGF grammar is encountered
	 */
	private void recognizeAndReport() throws GrammarException {
		
		System.out.println("*************************************************");

		while (true) {
			Result result = recognizer.recognize();
			String bestResult = result.getBestFinalResultNoFiller();
			System.out.println("The Result of:");
			System.out.println(bestResult);
			System.out.println("is:");
			System.out.println(parser.parse(bestResult));
			System.out.println("===================================================");
		}
	}

	/**
	 * Dumps out a set of sample sentences for this grammar. TODO: Note the
	 * current implementation just generates a large set of random utterances
	 * and tosses away any duplicates. There's no guarantee that this will
	 * generate all of the possible utterances. (yep, this is a hack)
	 * 
	 */
	private void dumpSampleSentences(String title) {
		System.out.println(" ====== " + title + " ======");
		System.out.println("Speak one of: \n");
		jsgfGrammar.dumpRandomSentences(200);
		System.out.println(" ============================");
	}

	/**
	 * Main method for running LinguCalc.
	 */
	public static void main(String[] args) {
		try {
			LinguCalc lingucalc = new LinguCalc();
			lingucalc.execute();
		} catch (IOException ioe) {
			System.out.println("I/O Error " + ioe);
		} catch (PropertyException e) {
			System.out.println("Problem configuring recognizer" + e);
		} catch (InstantiationException e) {
			System.out.println("Problem creating components " + e);
		} catch (GrammarException e) {
			System.out.println("Problem with Grammar " + e);
		} catch (EngineException e) {
			System.out.println("Problem with parsing engine " + e);
		} catch (EngineStateError e) {
			System.out.println("Problem with engine state " + e);
		}
	}
}
