package com.James.groovy;

import java.io.File;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;


/**
 * Created by James on 2016/10/20.
 */
public class SampleApp {

  public static void main(String[] args) throws Exception {


    String jsonText = "[{\"id\": \"959b17d4-5b72-4f81-89bd-118d10c77a59\", \"name\": \"name11\", \"description\": \"\", \"color\": \"#00B2EF\"}]";


    Binding binding = new Binding();
    binding.setProperty("jsonText", jsonText);

    GroovyShell groovyShell = new GroovyShell(binding);
    groovyShell.evaluate(new File("soa_groovy/src/main/java/com/James/groovy/GroovyTrans"));


  }
}
