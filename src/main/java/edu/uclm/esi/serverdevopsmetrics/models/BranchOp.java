package edu.uclm.esi.serverdevopsmetrics.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class BranchOp {

	private static final Log LOG = LogFactory.getLog(BranchOp.class);
	private String dir = "C:/Github/";
	private String resourcessalida = "C:/resources/salida-";
	private String branchStr =  "-branch-";
	private String slash = "/";
	private String guion = "-";
	
	private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
	
	private void logOutput(InputStream inputStream, String prefix) {
	    new Thread(() -> {
	        Scanner scanner = new Scanner(inputStream, "UTF-8");
	        while (scanner.hasNextLine()) {
	            synchronized (this) {
	                log(prefix + scanner.nextLine());
	            }
	        }
	        scanner.close();
	    }).start();
	}

	private synchronized void log(String message) {
	    System.out.println(format.format(new Date()) + ": " + message);
	}

	public List<String> getBranches(String reponame, String owner, String tokenGithub) throws IOException, InterruptedException {

			
		File f = new File(dir+ owner +slash+ reponame);
		if(!f.exists() && !f.isDirectory()) { 
			
			f = new File(dir + owner);
			if(!f.exists() && !f.isDirectory()) {
				f.mkdir();
			}
			
			String command = "cmd /c C:\\resources\\gitclone.bat ";

			String completecommand = command + owner + " " + reponame + " " + tokenGithub;
			LOG.info(completecommand);
			
			Process p = Runtime.getRuntime().exec(completecommand);
			logOutput(p.getInputStream(), "");
	        logOutput(p.getErrorStream(), "Error: ");
	        p.waitFor();
			LOG.info("Waiting for batch gitclone ...");
		   
		    
		}
		
		String command = "cmd /c C:\\resources\\gitfetch.bat ";

		String completecommand = command + owner + slash + reponame + " " + tokenGithub;
		LOG.info(completecommand);
		
		Process p = Runtime.getRuntime().exec(completecommand);
		logOutput(p.getInputStream(), "");
        logOutput(p.getErrorStream(), "Error: ");
        p.waitFor();
		LOG.info("Waiting for batch gitfetch ...");
	    

		command = "cmd /c C:\\resources\\branches.bat ";
		
		
		byte[] bytes = reponame.getBytes(StandardCharsets.UTF_8);
		String reponEnd = new String(bytes, StandardCharsets.UTF_8);
		
		String reponEndParenthesis = reponEnd;
		reponEndParenthesis = reponEndParenthesis.replace("(", "^(");
		reponEndParenthesis = reponEndParenthesis.replace(")", "^)");
		
		bytes = owner.getBytes(StandardCharsets.UTF_8);
		String ownerEnd = new String(bytes, StandardCharsets.UTF_8);
		
		String ownerEndParenthesis = ownerEnd;
		ownerEndParenthesis = ownerEndParenthesis.replace("(", "^(");
		ownerEndParenthesis = ownerEndParenthesis.replace(")", "^)");
		
		completecommand = command + owner + slash + reponEndParenthesis + " " + ownerEndParenthesis+guion+reponEndParenthesis;
		LOG.info(completecommand);
		
		p = Runtime.getRuntime().exec(completecommand);
		
		LOG.info("Waiting for batch branches ...");
	    p.waitFor();

	    return getBranches(ownerEndParenthesis, reponEndParenthesis);
	
	}

	private List<String> getBranches(String ownerEndParenthesis, String reponEndParenthesis) {
		
		List<String> branches = new ArrayList<String>();
		
		LOG.info("Vamos a imprimir el archivo: C:/resources/salida-"+ownerEndParenthesis+guion+reponEndParenthesis+ ".txt");
		try (BufferedReader in = new BufferedReader(new FileReader(this.resourcessalida +ownerEndParenthesis+guion+reponEndParenthesis+".txt"))) {
			String line;

			while ((line = in.readLine()) != null) {
				if (!line.contains("/HEAD") && line.contains("/origin/") && !line.contains("dependabot/npm_and_yarn")) {

					branches.add(line.substring(line.lastIndexOf("/origin/") + 8));

				}
			}

			LOG.info("imprimos las branches");
			LOG.info("size branches: "+branches.size());
			for(int i=0; i<branches.size(); i++) {
				LOG.info("branch "+i+": "+branches.get(i));
			}

			return branches;
		} catch (Exception err) {
			LOG.info("No existe el archivo");
			return Collections.emptyList();
		}
	}

	public List<String> getFirstCommit(String reponame, String owner, List<String> branches)
			throws IOException, InterruptedException {

		

		List<String> commits = new ArrayList<String>();

		LOG.info("imprimos las branches");
		LOG.info("size branches en firstcommits: "+branches.size());
		
		LOG.info("vamos a ejecutar el cmd para la branch principal posible: main");
		
		String command = "cmd /c C:\\resources\\firstcommitmainPrincipal.bat ";
		byte[] bytes = reponame.getBytes(StandardCharsets.UTF_8);
		String reponEnd = new String(bytes, StandardCharsets.UTF_8);
		
		String reponEndParenthesis = reponEnd;
		reponEndParenthesis = reponEndParenthesis.replace("(", "^(");
		reponEndParenthesis = reponEndParenthesis.replace(")", "^)");
		
		LOG.info("reponEndParenthesises es : "+reponEndParenthesis);
		
		bytes = owner.getBytes(StandardCharsets.UTF_8);
		
		String ownerEnd = new String(bytes, StandardCharsets.UTF_8);
		
		String ownerEndParenthesis = ownerEnd;
		ownerEndParenthesis = ownerEndParenthesis.replace("(", "^(");
		ownerEndParenthesis = ownerEndParenthesis.replace(")", "^)");

		String completecommand = command + ownerEndParenthesis+slash+reponEndParenthesis+ " " + ownerEndParenthesis+guion+reponEndParenthesis;
		LOG.info(completecommand);
		
		Process p = Runtime.getRuntime().exec(completecommand);

		LOG.info("Waiting for batch file firstcommit ...");
		logOutput(p.getInputStream(), "");
        logOutput(p.getErrorStream(), "Error: ");
        p.waitFor();

		
		boolean branchEsMain=true;
		LOG.info("Comprobando contenido de: "+this.resourcessalida + ownerEndParenthesis+guion+reponEndParenthesis + "-branch-main.txt");

		try (BufferedReader in = new BufferedReader(
				new FileReader(this.resourcessalida + ownerEndParenthesis+guion+reponEndParenthesis + "-branch-main.txt"));) {
			
			String line=in.readLine();
			LOG.info("la linea contiene: "+line);

			if (line==null) {
				LOG.info("La rama principal no es main");
				branchEsMain=false;
			}
			
		} catch (Exception err) {
			return Collections.emptyList();
		}
		
		if(branchEsMain) {
			
			commits = execute(branches, reponame, owner, "main");
		}
		
		else {
			
			commits = execute(branches, reponame, owner, "");
		
		}
		
		return commits;

	}

	private List<String> execute(List<String> branches, String reponame, String owner, String option) throws IOException, InterruptedException {
		String command;
		byte[] bytes;
		String completecommand;
		
		
		List<String> commits = new ArrayList<String>();
		boolean esPrincipal = false;
		String commit;
		for (int i = 0; i < branches.size(); i++) {

			LOG.info("vamos a ejecutar el cmd para la branch: "+branches.get(i));
			
			command = "cmd /c C:\\resources\\firstcommit"+option+".bat ";
			bytes = reponame.getBytes(StandardCharsets.UTF_8);
			String reponEnd = new String(bytes, StandardCharsets.UTF_8);
			String reponEndParenthesis = reponEnd;
			reponEndParenthesis = reponEndParenthesis.replace("(", "^(");
			reponEndParenthesis = reponEndParenthesis.replace(")", "^)");
			
			bytes = owner.getBytes(StandardCharsets.UTF_8);
			String ownerEnd = new String(bytes, StandardCharsets.UTF_8);
			String ownerEndParenthesis = ownerEnd;
			ownerEndParenthesis = ownerEndParenthesis.replace("(", "^(");
			ownerEndParenthesis = ownerEndParenthesis.replace(")", "^)");
			String branchEnd = "";
			String branchOrigin = "";
			if(branches.get(i).equals("master")) {
				command = "cmd /c C:\\resources\\firstcommitPrincipal.bat ";
				esPrincipal = true;
			}
			else if(branches.get(i).equals("main")) {
				command = "cmd /c C:\\resources\\firstcommitmainPrincipal.bat ";
				esPrincipal = true;
			}
			else {
				bytes = branches.get(i).getBytes(StandardCharsets.UTF_8);
				branchEnd = new String(bytes, StandardCharsets.UTF_8);
				branchOrigin = "..origin/"+branchEnd;
			}
			
			if(esPrincipal) {
				completecommand = command + ownerEndParenthesis+slash+reponEndParenthesis+ " " + ownerEndParenthesis+guion+reponEndParenthesis;
			}
			else {
				completecommand = command + ownerEndParenthesis+slash+reponEndParenthesis+ " " + branchOrigin+ " " + ownerEndParenthesis+guion+reponEndParenthesis + " " +branchEnd;	
			}
			LOG.info("ejecutamos commando: "+completecommand);
			
			Process p = Runtime.getRuntime().exec(completecommand);

			LOG.info("Waiting for batch file firstcommit"+option+" ...");
		    
			logOutput(p.getInputStream(), "");
	        logOutput(p.getErrorStream(), "Error: ");
	        p.waitFor();
			
			
			commit = obtenerResultado(reponEndParenthesis,  branches.get(i), ownerEndParenthesis);
			
			commits.add(commit);          
			
	        esPrincipal = false;
		}
		return commits;
	}

	private String obtenerResultado(String reponame, String branch, String owner) {
		
		String lastLine = "empty";
		String sCurrentLine="";
		
		try (BufferedReader in = new BufferedReader(
				new FileReader(this.resourcessalida + owner+guion+reponame + this.branchStr + branch + ".txt"));) {			  

			    while ((sCurrentLine = in.readLine()) != null) 
			    {
			    	
			        lastLine = sCurrentLine;
			    }
			    LOG.info("The last line in repo: "+reponame+" | branch: "+branch+" is: "+lastLine);
			if (lastLine.equals("empty")) {
				LOG.info("adding empty commit:"+lastLine);
				updatefile(reponame, owner, branch, lastLine);
				return lastLine;
			} else {
				updatefile(reponame, owner , branch, lastLine);
				LOG.info("adding commit:"+lastLine);
				String [] getOid = lastLine.split(" ");
				return getOid[0];
			}
		
			
		} catch (Exception err) {
			return "error";
		}
	}

	private void updatefile(String reponame, String owner, String branch, String lastLine) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(this.resourcessalida + owner+guion+reponame+ this.branchStr + branch + ".txt"))) {
	    	LOG.info("Contenido: "+lastLine);
	    	bw.write(lastLine);
			LOG.info("Reescribiendo "+this.resourcessalida + owner+guion+reponame + this.branchStr + branch + ".txt");

	        bw.flush();
	     }
	     catch (IOException e) {
	            e.printStackTrace();
	            LOG.info("Error writing in: "+this.resourcessalida + owner+guion+reponame + this.branchStr + branch + ".txt");
	     }
	     finally {
	    	 LOG.info("Finished");
	     }
	}
	
	  



}
