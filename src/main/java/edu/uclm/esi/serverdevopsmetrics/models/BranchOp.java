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
		LOG.info("ejecutamos commando: "+completecommand);
		
		Runtime.getRuntime().exec(completecommand);
		
		for(int i=0; i<5; i++) {
			Thread.sleep(1000);
			LOG.info("espera: "+i);
		}

		LOG.info("Vamos a imprimir el archivo: C:/resources/salida-" + reponame + ".txt");
		try (BufferedReader in = new BufferedReader(new FileReader("C:/resources/salida-" + reponame + ".txt"))) {
			String line;

			while ((line = in.readLine()) != null) {
				if (!line.contains("/HEAD") && line.contains("/origin/") && !line.contains("dependabot/npm_and_yarn")) {

					branches.add(line.substring(line.lastIndexOf("/origin/") + 8));

				}
			}

			//Thread.sleep(1000);

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
		for (int i = 0; i < branches.size(); i++) {

			LOG.info("vamos a ejecutar el cmd para la branch: "+branches.get(i));
			
			String command = "cmd /c C:\\resources\\firstcommit.bat ";
			byte[] bytes = reponame.getBytes(StandardCharsets.UTF_8);
			String reponEnd = new String(bytes, StandardCharsets.UTF_8);
			bytes = branches.get(i).getBytes(StandardCharsets.UTF_8);
			String branchEnd = new String(bytes, StandardCharsets.UTF_8);
			
			
			String completecommand = command + reponEnd+ " " + branchEnd;
			LOG.info("ejecutamos commando: "+completecommand);
			
			Runtime.getRuntime().exec(completecommand);

			Thread.sleep(500);

			try (BufferedReader in = new BufferedReader(
					new FileReader("C:/resources/salida-" + reponame + "-branch-" + branches.get(i) + ".txt"));) {
				String line;

				if ((line = in.readLine()) == null) {
					commits.add("empty");
				} else {
					LOG.info("adding commit:"+line);
					commits.add(line);
				}
			} catch (Exception err) {
				return Collections.emptyList();
			}
		}

		return commits;

	}

}
