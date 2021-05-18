package edu.uclm.esi.serverdevopsmetrics.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class BranchOp {

	private static final Log LOG = LogFactory.getLog(BranchOp.class);
	private String dir = "D:/Github/";
	
	@Autowired
	private Environment env;

	public List<String> getBranches(String reponame, String owner) throws IOException, GitAPIException, InterruptedException {

		Git git;
		Repository repo;
		String githubuser;
		String githubkey;

		List<String> branches = new ArrayList<String>();
		
		githubuser = env.getProperty("github.user");
		githubkey = env.getProperty("github.key");

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

		Runtime.getRuntime().exec(
				"cmd /c C:\\Users\\Crespo\\eclipse-workspace\\serverdevopsmetrics\\src\\main\\resources\\branches.bat "
						+ reponame);

		try (BufferedReader in = new BufferedReader(new FileReader("src/main/resources/salida-" + reponame + ".txt"))) {

			String line;

			while ((line = in.readLine()) != null) {
				if (!line.contains("/HEAD") && line.contains("/origin/") && !line.contains("dependabot/npm_and_yarn")) {

					branches.add(line.substring(line.lastIndexOf("/origin/") + 8));

				}
			}

			Thread.sleep(1000);

			return branches;
		} catch (Exception err) {
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

		githubuser = env.getProperty("github.user");
		githubkey = env.getProperty("github.key");

		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(githubuser, githubkey);
		git.fetch().setCredentialsProvider(cp).setRemote("origin").call();

		for (int i = 0; i < branches.size(); i++) {

			Runtime.getRuntime().exec(
					"cmd /c C:\\Users\\Crespo\\eclipse-workspace\\serverdevopsmetrics\\src\\main\\resources\\firstcommit.bat "
							+ reponame + " " + branches.get(i));

			

			try (BufferedReader in = new BufferedReader(
					new FileReader("src/main/resources/salida-" + reponame + "-branch-" + branches.get(i) + ".txt"));) {
				String line;

				if ((line = in.readLine()) == null) {
					commits.add("empty");
				} else {
					commits.add(line);
				}
			} catch (Exception err) {
				return Collections.emptyList();
			}
		}

		return commits;

	}

}
