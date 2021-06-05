package edu.uclm.esi.serverdevopsmetrics.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class BranchOp {

	private static final Log LOG = LogFactory.getLog(BranchOp.class);
	private String dir = "C:/Github/";
	private String resourcessalida = "C:/resources/salida-";
	

	public List<String> getBranches(String reponame, String owner) throws IOException, GitAPIException, InterruptedException {

		Git git;
		Repository repo;
		String githubuser;
		String githubkey;

		List<String> branches = new ArrayList<String>();
		
		githubuser = System.getProperty("github.user");
		githubkey = System.getProperty("github.key");
		
		LOG.info("El usuario es: "+githubuser);
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(githubuser, githubkey);
			
		File f = new File(dir + reponame);
		if(!f.exists() && !f.isDirectory()) { 
			Thread th = new Thread(() -> {
				try {
					Git.cloneRepository()
							  .setURI("https://github.com/"+owner+"/"+reponame+".git")
							  .setDirectory(f)
							  .call();
				} catch (GitAPIException e) {
					LOG.info("error clonating repository to local");
				}
		    });
			
			th.start();
			th.join();
			
			
		}
		
		repo = new FileRepositoryBuilder().setGitDir(new File(dir + reponame + "/.git")).build();

		git = new Git(repo);

		git.fetch().setCredentialsProvider(cp).setRemote("origin").call();

		String command = "cmd /c C:\\resources\\branches.bat ";
		
		byte[] bytes = reponame.getBytes(StandardCharsets.UTF_8);
		String reponameEnd =  new String(bytes, StandardCharsets.UTF_8);
		String completecommand = command + reponameEnd;
		LOG.info(completecommand);
		
		Process p = Runtime.getRuntime().exec(completecommand);
		
		LOG.info("Waiting for batch branches ...");
	    p.waitFor();

		LOG.info("Vamos a imprimir el archivo: C:/resources/salida-" + reponame + ".txt");
		try (BufferedReader in = new BufferedReader(new FileReader(this.resourcessalida + reponame + ".txt"))) {
			String line;

			while ((line = in.readLine()) != null) {
				if (!line.contains("/HEAD") && line.contains("/origin/") && !line.contains("dependabot/npm_and_yarn")) {

					branches.add(line.substring(line.lastIndexOf("/origin/") + 8));

				}
			}

			LOG.info("imprimos las branches");
			LOG.info("size branches: "+branches.size());
			for(int i=0; i<branches.size(); i++) {
				LOG.info("branch "+i+"·: "+branches.get(i));
			}

			return branches;
		} catch (Exception err) {
			LOG.info("No existe el archivo");
			return Collections.emptyList();
		}

	}

	public List<String> getFirstCommit(String reponame, List<String> branches)
			throws IOException, GitAPIException, InterruptedException {

		Git git;
		Repository repo;
		String githubuser;
		String githubkey;

		List<String> commits = new ArrayList<String>();

		repo = new FileRepositoryBuilder().setGitDir(new File(dir + reponame + "/.git")).build();

		git = new Git(repo);

		githubuser = System.getProperty("github.user");
		githubkey = System.getProperty("github.key");
				
		LOG.info("El usuario para firstcommits es: "+githubuser);

		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(githubuser, githubkey);
		git.fetch().setCredentialsProvider(cp).setRemote("origin").call();

		LOG.info("imprimos las branches");
		LOG.info("size branches en firstcommits: "+branches.size());
		
		LOG.info("vamos a ejecutar el cmd para la branch principal posible: main");
		
		String command = "cmd /c C:\\resources\\firstcommitmainPrincipal.bat ";
		byte[] bytes = reponame.getBytes(StandardCharsets.UTF_8);
		String reponEnd = new String(bytes, StandardCharsets.UTF_8);
		
		String reponEndParenthesis = reponEnd;
		reponEndParenthesis = reponEndParenthesis.replace("(", "^(");
		reponEndParenthesis = reponEndParenthesis.replace(")", "^)");
		
		String completecommand = command + reponEnd+ " " + " " +reponEndParenthesis;
		LOG.info(completecommand);
		
		Process p = Runtime.getRuntime().exec(completecommand);

		LOG.info("Waiting for batch file firstcommit ...");
	    
		p.waitFor();

		boolean branchEsMain=true;
		
		
		
		try (BufferedReader in = new BufferedReader(
				new FileReader(this.resourcessalida + reponame + "-branch-main.txt"));) {
			
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
			
			commits = execute(branches, reponame, "main");
		}
		
		else {
			
			commits = execute(branches, reponame, "");
		
		}
		
		return commits;

	}

	private List<String> execute(List<String> branches, String reponame, String option) throws IOException, InterruptedException {
		String command;
		byte[] bytes;
		String completecommand;
		String line;
		
		List<String> commits = new ArrayList<String>();
		for (int i = 0; i < branches.size(); i++) {

			LOG.info("vamos a ejecutar el cmd para la branch: "+branches.get(i));
			
			command = "cmd /c C:\\resources\\firstcommit"+option+".bat ";
			bytes = reponame.getBytes(StandardCharsets.UTF_8);
			String reponEnd = new String(bytes, StandardCharsets.UTF_8);
			String branchEnd = "";
			String branchOrigin = "";
			if(branches.get(i).equals("master")) {
				command = "cmd /c C:\\resources\\firstcommitPrincipal.bat ";
			}
			else if(branches.get(i).equals("main")) {
				command = "cmd /c C:\\resources\\firstcommitmainPrincipal.bat ";
			}
			else {
				bytes = branches.get(i).getBytes(StandardCharsets.UTF_8);
				branchEnd = new String(bytes, StandardCharsets.UTF_8);
				branchOrigin = "..origin/"+branchEnd;
			}
			
			String reponEndParenthesis = reponEnd;
			reponEndParenthesis = reponEndParenthesis.replace("(", "^(");
			reponEndParenthesis = reponEndParenthesis.replace(")", "^)");
			
			String branchEndParenthesis = branchEnd;
			branchEndParenthesis = reponEndParenthesis.replace("(", "^(");
			branchEndParenthesis = reponEndParenthesis.replace(")", "^)");
			
			completecommand = command + reponEnd+ " " + branchEnd+ " " + branchOrigin +" " + reponEndParenthesis +" " + branchEndParenthesis;
			LOG.info("ejecutamos commando: "+completecommand);
			
			Process p = Runtime.getRuntime().exec(completecommand);

			LOG.info("Waiting for batch file firstcommit"+option+" ...");
		    
			p.waitFor();

			try (BufferedReader in = new BufferedReader(
					new FileReader(this.resourcessalida + reponame + "-branch-" + branches.get(i) + ".txt"));) {
				

				if ((line = in.readLine()) == null) {
					commits.add("empty");
				} else {
					LOG.info("adding commit:"+line);
					String [] getOid = line.split(" ");
					commits.add(getOid[0]);
				}
				
			} catch (Exception err) {
				return Collections.emptyList();
			}
		}
		return commits;
	}

}
