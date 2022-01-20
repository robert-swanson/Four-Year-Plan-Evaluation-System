# Development Environment Setup

This document describes how to setup the development and build environment for PSL using IntelliJ.

This document assumes you already have IntelliJ installed and set up.

## Clone The Repo:

1. Navigate to where want to clone the repository
2. Clone the repo
3. Rename the repo

```
cd path/to/repo
git clone https://github.com/robert-swanson/Four-Year-Plan-Evaluation-System.git
mv Four-Year-Plan-Evaluation-System pslAudit
```

## Setup IntelliJ:

1. Open the project in IntelliJ
2. If asked, click “Trust Project”
3. If you get an error popup saying “Invalid VCS root mapping”, 
   1. Click “Configure...”, 
   2. Click the red text
   3. Delete it by clicking the minus button
   4. If you lose the popup, navigate to Preferences > Version Control > Directory Mappings, and complete steps 1-3
4. Setup an SDK
   1. Navigate to File > Project Structure > SDKs
   2. If there's nothing there (there’s probably something there)
      1. Click “+” > Download JDK... 
      2. Select the newest version (probably default) and click download
   3. Click “Project” on the left side of the Project Structure window
      1. Set the SDK
      2. Ensure “Compiler Output” is set to something (eg `.../pslAudit/out`)
   4. Click “Modules” on the left side
      1. If it says “Nothing to show”
         1. Click “+”
         2. Click ”New Module”
         3. Click “Next”
         4. Select the project directory for “Content Root”
         5. Click “Finish”
      2. Click on the “Sources” tab (just below the name of the project)
      3. Click “+ Add Content Root”
      4. Select the top-level project directory (eg `pslAudit`)
      5. Click the “Dependencies” tab
         1. Set the “Module SDK” to your installed SDK (not “Project SDK”)
         2. Click “+” (underneath “Module SDK”)
         3. Click “JARs or Directories...” to add each of the following libraries (hold shift to select multiple)
         4. `lib/antlr-runtime-4.9.1.jar`
         5. `lib/gson-2.8.6.jar`
         6. `lib/java-getopt-1.0.13.jar`
   5. Click “OK” to save the changes and close the “Project Structure” window
5. If you see a popup saying “Frameworks detected, Python framework is detected.” Click “configure” > “ok”
6. Set the Run Configurations
   1. Click the drop down to the left of the run button
   2. Click “Edit Configurations”
   3. For the configuration you want to configure, set the module (if it says “module not specified”) to the SDK you installed
   4. Click “Ok”
7. Setup ANTLR
   1. Install the plugin
      1. Open IntelliJ settings
      2. Go to Plugins
      3. Install the “ANTLR v4” plugin
      4. Restart IntelliJ
   2. Generate ANTLR recognizer
      1. Navigate to the `java-src/psl/PSLGrammar.g4`
      2. Right click the file and click “Generate ANTLR Recognizer”
      3. You should see a message saying “psl for PSLGrammar.g4 generated to ...”
      4. Right Click the `gen` directory that now exists in your project directory
         1. Click “Mark Directory as”
         2. Click “Generated Sources Root”
8. You should now be able to build and run
   1. If the run button is greyed out, try restarting IntelliJ

## Using PSL

PSL Takes in 5 input arguments corresponding to the 4 inputs and 1 output of the system:

- `--catalog path/to/catalog.json`
- `--offerings path/to/offerings.json`
- `--plan path/to/plan.json`
- `--psl path/to/psl.json`
- `--out path/to/result.json`

You can view and change what these are in the Run/Debug Configurations (click the dropdown to the left of run and edit configurations).

Running the program will parse the input files and print the result in the output file (creating it if it doesn’t exist)