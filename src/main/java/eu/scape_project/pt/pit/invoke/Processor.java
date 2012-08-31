/**
 * 
 */
package eu.scape_project.pt.pit.invoke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;

import eu.scape_project.pt.pit.tools.Action;
import eu.scape_project.pt.pit.tools.Input;
import eu.scape_project.pt.pit.tools.Template;
import eu.scape_project.pt.pit.tools.ToolSpec;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class Processor {

	protected ToolSpec ts;
	
	protected Action action;

	private In stdin;
    private Out stdout;

	/**
	 * Constructor from a toolspec and an action
	 * @param ts
	 * @param action
	 */
	public Processor( ToolSpec ts, Action action ) {
		this.ts = ts;
		this.action = action;
	}

	/**
	 * Factory method to create a processor from toolspec and action identifiers,
	 * @param toolspec_id 
	 * @param action_id 
	 * @return a new Processor object
	 * @throws ToolSpecNotFoundException 
	 * @throws CommandNotFoundException 
	 */
	
	//removed by rschmidt13
	/*
	public static Processor createProcessor( String toolspec_id, String action_id ) throws ToolSpecNotFoundException, CommandNotFoundException {
		try {
			ToolSpec ts = ToolSpec.fromInputStream( ToolSpec.class.getResourceAsStream("/toolspecs/"+toolspec_id+".ptspec.xml"));
			Action action = Processor.findTool(ts, action_id);
			// Create the right class:
			if( "identify".equals( action.getType() ) ) {
				return new Identify(ts,action);
			} else {
				return new Processor(ts,action);
			}
		} catch (FileNotFoundException e) {
			throw new ToolSpecNotFoundException("Toolspec "+toolspec_id+" not found!", e);
		} catch (JAXBException e) {
			throw new ToolSpecNotFoundException("Toolspec "+toolspec_id+" not parseable!", e);
		} catch (CommandNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	public static Action findTool( ToolSpec ts, String command_id ) throws CommandNotFoundException {
		Action cmd = null;
		for( Action c : ts.getActions() ) {
			if( c.getId() != null && c.getId().equals(command_id) ) cmd = c;
		}
		if( cmd == null ) throw new CommandNotFoundException("No command "+command_id+" could be found.");
		return cmd;
	}
	
	protected void runCommand( String[] cmd_template ) throws IOException {
		// Build the command:
		ProcessBuilder pb = new ProcessBuilder(cmd_template);
		System.out.println("Executing: "+pb.command());
		/*
		for( String command : pb.command() ) {
			System.out.println("Command : "+command);			
		}
		*/
		
		pb.redirectErrorStream(true);
		Process start = pb.start();
		try {
			//copy input stream to output stream
			if( this.stdin != null ) {
				IOUtils.copyLarge(this.stdin.getInputStream(),  start.getOutputStream() );
				start.getOutputStream().close();
				System.out.println("Data in...");
			}
			
            InputStream procStdout = start.getInputStream();
			// Copy the OutputStream:
            if( this.stdout != null ) {
                IOUtils.copy( procStdout, this.stdout.getOutputStream() );
            }
            else {
                //StringWriter sw = new StringWriter();
                IOUtils.copy( procStdout , System.out );
                //System.out.println(sw.toString());
            }

			
			// Wait for completion:
			// FIXME Needs time-out. 			
			start.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            start.destroy();
        }
	}
	
	/**
	 * @param cmd
	 * @return the commands from the Action as a string array
	 */
	public String[] substituteTemplates( Action cmd ) {
		String[] cmd_template = cmd.getCommand().split(" ");
		if( ts.getTemplate() == null ) return cmd_template;
		// Substitute the templates into the command string:
		HashMap<String,String> tpls = new HashMap<String,String>();
		for( Template v : ts.getTemplate() ) {
			tpls.put(v.getName(), v.getValue());
		}
		replaceAll(cmd_template,tpls);
		return cmd_template;
	}
	
	protected HashMap<String,String> getStandardVars( Action cmd ) throws IOException {
		// Now substiture the parameters into the templates.
		HashMap<String,String> vars = new HashMap<String,String>();
		if( ts.getInputs() != null ) {
			for( Input v : ts.getInputs().getInputs() ) {
				vars.put(v.getVar(), v.getDefault());
			}
		}
		// Create standard parameters.
		vars.put("logFile", File.createTempFile(ts.getTool().getName()+"-"+cmd.getId(), ".log").getAbsolutePath());
		return vars;
	}

    // moved to ToolInvoker
	protected void replaceAll(String[] cmd_template, HashMap<String,String> vars) {
		for( int i = 0; i < cmd_template.length; i++ ) {
			for( String key : vars.keySet() ) {
				// Something is inserting a null,null pair into the map - ignoring it here:
				if( key != null && vars.get(key) != null ) {
					String matchTo = Pattern.quote("${"+key+"}");
					System.out.println("GOT "+key+" "+vars.get(key));
					cmd_template[i] = cmd_template[i].replaceAll(matchTo, vars.get(key).replace("\\", "\\\\") );
				}
			}
		}
	}

	/**
	 * @return the hash map of inputs
	 */
	public HashMap<String,String> getInputs() {
		HashMap<String, String> vars = new HashMap<String, String>();
		return vars;
	}
	
	private void setStdin(In input) {
		this.stdin = input;
	}

    public void setStdout(Out output ) {
        this.stdout = output;
    }

	/**
	 * Generic invocation method:
	 * @param command_id
	 * @param parameters
	 * @throws IOException 
	 * @throws CommandNotFoundException 
	 */
	public void execute( HashMap<String,String> parameters) throws IOException, CommandNotFoundException {
		String[] cmd_template = substituteTemplates(action);
		HashMap<String, String> vars = getStandardVars(action);
        if( parameters != null )
            vars.putAll(parameters);
		
		for( String key : vars.keySet() ) {
			System.out.println("Key: "+key+" = "+vars.get(key));
		}
		
		// Now substitute the parameters:
		replaceAll(cmd_template,vars);

		// Now run the command:
		runCommand(cmd_template);
	}
	
	/**
	 * @param input
	 * @param parameters
	 * @throws IOException
	 */
	public void execute( In input, HashMap<String,String> parameters ) throws IOException {
		String[] cmd_template = substituteTemplates(action);
		HashMap<String, String> vars = getStandardVars(action);
        if( parameters != null )
            vars.putAll(parameters);
		
		// TODO Check input file exists!
		// For one input, we map like this, or support StdIn.
		if( getUseStdin() ) {
			this.setStdin(input);
		} else {
			vars.put("input", input.getFile().getAbsolutePath());
		}
		
		// Now substitute the parameters:
		replaceAll(cmd_template,vars);

		// Now run the command:
		runCommand(cmd_template);
	}
	
	/**
	 * @return true if using stdin
	 */
	public boolean getUseStdin() {
		if( ts.getInputs() != null ) return ts.getInputs().getUseStdin();
		if( action.getInputs() != null ) return action.getInputs().getUseStdin();
		return false;
	}
	
	/**
	 * @param input1
	 * @param input2
	 * @param parameters
	 * @throws IOException
	 */
	public void execute( In input1, In input2, HashMap<String,String> parameters ) throws IOException {
		String[] cmd_template = substituteTemplates(action);
		HashMap<String, String> vars = getStandardVars(action);
        if( parameters != null )
            vars.putAll(parameters);

		// Special parameters for this form:
		vars.put("input1", input1.getFile().getAbsolutePath());
		vars.put("input2", input2.getFile().getAbsolutePath());
		
		// Now substitute the parameters:
		replaceAll(cmd_template,vars);

		// Now run the command:
		runCommand(cmd_template);		
	}
	
	/**
	 * @param input
	 * @param output
	 * @param parameters
	 * @throws IOException
	 */
	public void execute( In input, Out output, HashMap<String,String> parameters ) throws IOException {
		String[] cmd_template = substituteTemplates(action);
		HashMap<String, String> vars = getStandardVars(action);
        if( parameters != null )
            vars.putAll(parameters);

		// Special parameters for this form:
		// For one input, we map like this, or support StdIn.
		if( ts.getInputs().getUseStdin() || action.getInputs().getUseStdin() ) {
			this.setStdin(input);
		} else {
			vars.put("input", input.getFile().getAbsolutePath());
		}
		vars.put("output", output.getFile().getAbsolutePath());
		
		// Now substitute the parameters:
		replaceAll(cmd_template,vars);

		// Now run the command:
		runCommand(cmd_template);		
		
	}
	

}
